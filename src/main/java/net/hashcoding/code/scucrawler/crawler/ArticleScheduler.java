package net.hashcoding.code.scucrawler.crawler;

import io.reactivex.schedulers.Schedulers;
import net.hashcoding.code.scucrawler.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;

public class ArticleScheduler extends QueueScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleScheduler.class);

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        String url = request.getUrl();
        Network.getArticleService()
                .isUrlExists(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.getCode() == 200) {
                        if (!result.getData())
                            ArticleScheduler.super.pushWhenNoDuplicate(request, task);
                    } else {
                        logger.error("validate url " + url + "failed");
                    }
                }, throwable -> logger.error("validate url " + url + "failed", throwable));
    }
}
