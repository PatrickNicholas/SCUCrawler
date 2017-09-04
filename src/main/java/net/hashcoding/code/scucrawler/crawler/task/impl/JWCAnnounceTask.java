package net.hashcoding.code.scucrawler.crawler.task.impl;

import net.hashcoding.code.scucrawler.crawler.task.BaseTask;
import net.hashcoding.code.scucrawler.crawler.task.pages.JWCPage;
import us.codecraft.webmagic.Site;

public class JWCAnnounceTask implements BaseTask {
    public final static String[] JWCAnnounceStartUrls = {
            "http://jwc.scu.edu.cn/jwc/moreNotice.action",
            "http://jwc.scu.edu.cn/jwc/moreNotice.action?pager.pageNow=2",
    };

    @Override
    public String getType() {
        return "教务处";
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
        return JWCAnnounceStartUrls;
    }

    @Override
    public Class getPageClass() {
        return JWCPage.class;
    }

    @Override
    public String toString() {
        return "Task [JWC Announce]";
    }
}
