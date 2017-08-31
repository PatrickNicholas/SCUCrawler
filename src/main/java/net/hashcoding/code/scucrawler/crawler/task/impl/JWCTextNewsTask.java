package net.hashcoding.code.scucrawler.crawler.task.impl;

import net.hashcoding.code.scucrawler.crawler.task.BaseTask;
import net.hashcoding.code.scucrawler.crawler.task.pages.JWCPage;
import us.codecraft.webmagic.Site;

public class JWCTextNewsTask implements BaseTask {
    public final static String[] JWCTextNewsStarUrls = {
            "http://jwc.scu.edu.cn/jwc/moreNews.action",
            "http://jwc.scu.edu.cn/jwc/moreNews.action?pager.pageNow=2",
    };

    @Override
    public String getType() {
        return "jwcxw";
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(3)
                .setRetrySleepTime(500)
                .setSleepTime(50)
                .setCharset("UTF-8");
    }

    @Override
    public String[] getUrl() {
        return JWCTextNewsStarUrls;
    }

    @Override
    public Class getPageClass() {
        return JWCPage.class;
    }

    @Override
    public String toString() {
        return "Task [JWC Text News]";
    }
}
