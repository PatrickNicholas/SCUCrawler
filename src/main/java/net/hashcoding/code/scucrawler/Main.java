package net.hashcoding.code.scucrawler;

import net.hashcoding.code.scucrawler.crawler.ArticleScheduler;
import net.hashcoding.code.scucrawler.crawler.processor.PageFactory;
import net.hashcoding.code.scucrawler.crawler.processor.PagePersistence;
import net.hashcoding.code.scucrawler.crawler.processor.pipeline.BasePageModelPipeline;
import net.hashcoding.code.scucrawler.crawler.processor.solver.HtmlBeautySolver;
import net.hashcoding.code.scucrawler.crawler.task.BaseTask;
import net.hashcoding.code.scucrawler.crawler.task.impl.CSAnnounceTask;
import net.hashcoding.code.scucrawler.crawler.task.impl.JWCAnnounceTask;
import net.hashcoding.code.scucrawler.crawler.task.impl.QCCDAnnounceTask;
import net.hashcoding.code.scucrawler.crawler.task.impl.XGBAnnounceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.OOSpider;

import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        long beginAt = System.currentTimeMillis();
        logger.debug("setup Page Factory with solver");

        PageFactory.push(new HtmlBeautySolver());

        logger.debug("delegate some tasks");
        delegate(new CSAnnounceTask(),
                new JWCAnnounceTask(),
                new QCCDAnnounceTask(),
                new XGBAnnounceTask());

        logger.debug("wait for PageFactory close");
        PageFactory.exit();

        logger.debug("wait for PagePersistence close");
        PagePersistence.exit();

        long endAt = System.currentTimeMillis();
        int count = PagePersistence.count();
        long duration = TimeUnit.MILLISECONDS.toSeconds(endAt - beginAt) + 1;
        logger.info("{} QPS", count / duration);
    }

    private static void delegate(BaseTask... tasks) {
        ArticleScheduler scheduler = new ArticleScheduler();
        for (BaseTask task : tasks) {
            logger.debug("run task {}", task.toString());

            Spider spider = OOSpider.create(
                    task.getSite(),
                    new BasePageModelPipeline(task.getType()),
                    task.getPageClass());
            spider.setScheduler(scheduler);
            spider.addUrl(task.getUrl());
            spider.setEmptySleepTime(1000);
            spider.thread(10);
            spider.run();
            assert (spider.getStatus() == Spider.Status.Stopped);
            spider.close();
        }
    }
}
