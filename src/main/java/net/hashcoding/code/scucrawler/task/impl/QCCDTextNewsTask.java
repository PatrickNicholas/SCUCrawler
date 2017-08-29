package net.hashcoding.code.scucrawler.task.impl;

import net.hashcoding.code.scucrawler.task.BaseTask;
import net.hashcoding.code.scucrawler.task.pages.QCCDPage;
import us.codecraft.webmagic.Site;

public class QCCDTextNewsTask implements BaseTask {
    public final static String [] QCCDTextNewsStarUrls = {
            "http://youth.scu.edu.cn/index.php/main/web/news-group/",
            "http://youth.scu.edu.cn/index.php/main/web/courtyard-style/",
    };

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
        return QCCDTextNewsStarUrls;
    }

    @Override
    public Class getPageClass() {
        return QCCDPage.class;
    }

    @Override
    public String toString() {
        return "Task [QCCD Text News]";
    }
}
