package net.hashcoding.code.scucrawler.crawler.task.impl;

import net.hashcoding.code.scucrawler.crawler.task.BaseTask;
import net.hashcoding.code.scucrawler.crawler.task.pages.CSPage;
import us.codecraft.webmagic.Site;

public class CSAnnounceTask implements BaseTask {

    public static final String[] StartUrls = {
            "http://cs.scu.edu.cn/cs/xytz/H9502index_1.htm",
    };

    @Override
    public String getType() {
        return "cstz";
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetrySleepTime(500)
                .setRetryTimes(3)
                .setSleepTime(50)
                .setCharset("GBK");
    }

    @Override
    public String[] getUrl() {
        return StartUrls;
    }

    @Override
    public Class getPageClass() {
        return CSPage.class;
    }

    @Override
    public String toString() {
        return "Task [CS Announce]";
    }
}
