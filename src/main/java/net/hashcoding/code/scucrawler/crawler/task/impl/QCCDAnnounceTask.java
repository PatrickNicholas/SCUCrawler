package net.hashcoding.code.scucrawler.crawler.task.impl;

import net.hashcoding.code.scucrawler.crawler.task.BaseTask;
import net.hashcoding.code.scucrawler.crawler.task.pages.QCCDPage;
import us.codecraft.webmagic.Site;

public class QCCDAnnounceTask implements BaseTask {
    public final static String[] QCCDAnnounceStartUrls = {
            "http://youth.scu.edu.cn/index.php/main/web/notice/",
    };

    @Override
    public String getType() {
        return "qccdtz";
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
        return QCCDAnnounceStartUrls;
    }

    @Override
    public Class getPageClass() {
        return QCCDPage.class;
    }

    @Override
    public String toString() {
        return "Task [QCCD Announce]";
    }
}
