package net.hashcoding.code.scucrawler;

import net.hashcoding.code.scucrawler.crawler.ArticleScheduler;
import net.hashcoding.code.scucrawler.crawler.processor.PageFactory;
import net.hashcoding.code.scucrawler.crawler.processor.PagePersistence;
import net.hashcoding.code.scucrawler.crawler.processor.pipeline.BasePageModelPipeline;
import net.hashcoding.code.scucrawler.crawler.processor.pipeline.BasePagePipeline;
import net.hashcoding.code.scucrawler.crawler.processor.solver.HtmlBeautySolver;
import net.hashcoding.code.scucrawler.crawler.task.BasePage;
import net.hashcoding.code.scucrawler.crawler.task.BasePageProcessor;
import net.hashcoding.code.scucrawler.crawler.task.pages.*;
import net.hashcoding.code.scucrawler.crawler.task.processor.CareerTalkPageProcessor;
import net.hashcoding.code.scucrawler.crawler.task.processor.PracticePageProcessor;
import net.hashcoding.code.scucrawler.crawler.task.processor.RecruitPageProcessor;
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
        delegate(
                new CSPage(),
                new JWCPage(),
                new QCCDPage(),
                new XGBPage(),
                new CSRecruitPage()
        );
        delegate(
                new PracticePageProcessor(),
                new CareerTalkPageProcessor(),
                new RecruitPageProcessor()
        );

        logger.debug("wait for PageFactory close");
        PageFactory.exit();

        logger.debug("wait for PagePersistence close");
        PagePersistence.exit();

        long endAt = System.currentTimeMillis();
        int count = PagePersistence.count();
        long duration = TimeUnit.MILLISECONDS.toSeconds(endAt - beginAt) + 1;
        logger.info("{} QPS", count / duration);
    }

    private static void delegate(BasePage... pages) {
        ArticleScheduler scheduler = new ArticleScheduler(5, TimeUnit.SECONDS);
        BasePageModelPipeline pipeline = new BasePageModelPipeline();
        for (BasePage page : pages) {
            logger.debug("run task {}", page.toString());

            Spider spider = OOSpider.create(page.getSite(), pipeline, page.getClass())
                    .setScheduler(scheduler)
                    .addUrl(page.getStartUrls());
            runAndWaitSpiderDone(spider);
        }
    }

    private static void delegate(BasePageProcessor... processors) {
        ArticleScheduler scheduler = new ArticleScheduler(5, TimeUnit.SECONDS);
        BasePagePipeline pipeline = new BasePagePipeline();
        for (BasePageProcessor processor : processors) {
            logger.debug("run task {}", processor.toString());
            Spider spider = Spider.create(processor)
                    .addPipeline(pipeline)
                    .addUrl(processor.getStartUrls())
                    .setScheduler(scheduler);
            runAndWaitSpiderDone(spider);
        }
    }

    private static void runAndWaitSpiderDone(Spider spider) {
        spider.setEmptySleepTime(3000);
        spider.thread(10);
        spider.run();
    }
}
