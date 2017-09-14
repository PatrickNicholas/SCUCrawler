package net.hashcoding.code.scucrawler.crawler;

import io.reactivex.schedulers.Schedulers;
import net.hashcoding.code.scucrawler.entity.Result;
import net.hashcoding.code.scucrawler.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArticleScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleScheduler.class);

    private int validatingCount = 0;
    private Queue<Request> queue = new LinkedList<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public ArticleScheduler() {
    }

    @Override
    public Request poll(Task task) {
        lock.lock();
        try {
            while (queue.isEmpty() && validatingCount != 0) {
                condition.await();
            }
            return queue.poll();
        } catch (InterruptedException e) {
            logger.error("Thread interrupted when poll", e);
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        lock.lock();
        validatingCount++;
        lock.unlock();

        String url = request.getUrl();
        Network.getArticleService()
                .isUrlExists(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(result -> success(result, request, task),
                        throwable -> failed(url, throwable.getMessage()));
    }

    private void success(Result<Boolean> result, Request request, Task task) {
        lock.lock();
        try {
            String url = request.getUrl();
            if (result.getCode() == 200) {
                if (!result.getData()) {
                    queue.offer(request);
                }
            } else {
                logger.error("Validate url: {} failed, with message: Status code {} -> {} ",
                        url, String.valueOf(result.getCode()), result.getDetailMessage());
            }

            validatingCount--;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    private void failed(String url, String message) {
        lock.lock();
        try {
            logger.error("Validate url: " + url + "failed, with message {}", message);

            validatingCount--;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
