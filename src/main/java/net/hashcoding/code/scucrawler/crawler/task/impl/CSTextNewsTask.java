package net.hashcoding.code.scucrawler.crawler.task.impl;

import net.hashcoding.code.scucrawler.crawler.task.BaseTask;
import net.hashcoding.code.scucrawler.crawler.task.pages.CSPage;
import us.codecraft.webmagic.Site;

public class CSTextNewsTask implements BaseTask {
    public final static String[] CSTextNewsStarUrls = {
            "http://cs.scu.edu.cn/cs/xyxw/H9501index_1.htm",
    };

    @Override
    public String getType() {
        return "csxw";
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
        return CSTextNewsStarUrls;
    }

    @Override
    public Class getPageClass() {
        return CSPage.class;
    }

    @Override
    public String toString() {
        return "Task [CS Text News]";
    }
}
