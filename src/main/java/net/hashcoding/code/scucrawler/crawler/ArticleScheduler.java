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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ArticleScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleScheduler.class);

    private AtomicInteger waitCount = new AtomicInteger(0);
    private TimeUnit timeUnit;
    private long pollTimeout;
    private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();

    public ArticleScheduler(long timeout, TimeUnit timeUnit) {
        this.pollTimeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public Request poll(Task task) {
        if (waitCount.get() == 0)
            return null;

        try {
            return queue.poll(pollTimeout, timeUnit);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted when poll", e);
            return null;
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
        waitCount.incrementAndGet();
        String url = request.getUrl();
        Network.getArticleService()
                .isUrlExists(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(result -> success(result, request, task),
                        throwable -> failed(url, throwable.getMessage()));
    }

    private void success(Result<Boolean> result, Request request, Task task) {
        waitCount.decrementAndGet();
        String url = request.getUrl();
        if (result.getCode() == 200) {
            if (!result.getData()) {
                pushWithBlocking(request, task);
            }
        } else {
            logger.error("Validate url: {} failed, with message: Status code {} -> {} ",
                    request.getUrl(), String.valueOf(result.getCode()), result.getDetailMessage());
        }
    }

    private void failed(String url, String message) {
        waitCount.decrementAndGet();
        logger.error("Validate url: " + url + "failed, with message {}", message);
    }

    private void pushWithBlocking(Request request, Task task) {
        queue.offer(request);
    }
}
