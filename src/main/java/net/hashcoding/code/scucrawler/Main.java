package net.hashcoding.code.scucrawler;

import net.hashcoding.code.scucrawler.processor.PageFactory;
import net.hashcoding.code.scucrawler.processor.PagePersistence;
import net.hashcoding.code.scucrawler.processor.pipeline.BasePageModelPipeline;
import net.hashcoding.code.scucrawler.processor.solver.HtmlToMarkdownSolver;
import net.hashcoding.code.scucrawler.processor.solver.MarkdownToHtmlSolver;
import net.hashcoding.code.scucrawler.task.BaseTask;
import net.hashcoding.code.scucrawler.task.impl.*;
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

        PageFactory.push(new HtmlToMarkdownSolver());
        PageFactory.push(new MarkdownToHtmlSolver());

        logger.debug("delegate some tasks");
        delegate(new CSAnnounceTask(),
                new CSTextNewsTask(),
                new JWCAnnounceTask(),
                new JWCTextNewsTask(),
                new QCCDAnnounceTask(),
                new QCCDTextNewsTask(),
                new XGBAnnounceTask(),
                new XGBTextNewsTask());

        logger.debug("wait for PageFactory close");
        PageFactory.exit();

        logger.debug("wait for PagePersistence close");
        PagePersistence.exit();

        long endAt = System.currentTimeMillis();
        int count = PagePersistence.count();
        long duration = TimeUnit.MILLISECONDS.toSeconds(endAt - beginAt + 1);
        logger.info("{} QPS", count / duration);
    }

    private static void delegate(BaseTask ... tasks) {
        for (BaseTask task : tasks) {
            logger.debug("run task {}", task.toString());

            Spider spider = OOSpider.create(
                    task.getSite(),
                    new BasePageModelPipeline(),
                    task.getPageClass());

            spider.addUrl(task.getUrl());
            spider.setEmptySleepTime(2000);
            spider.thread(20);
            spider.run();
            assert(spider.getStatus() == Spider.Status.Stopped);
            spider.close();
        }
    }
}
