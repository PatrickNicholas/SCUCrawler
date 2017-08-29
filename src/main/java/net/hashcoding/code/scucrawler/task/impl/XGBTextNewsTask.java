package net.hashcoding.code.scucrawler.task.impl;

import net.hashcoding.code.scucrawler.task.BaseTask;
import net.hashcoding.code.scucrawler.task.pages.XGBPage;
import us.codecraft.webmagic.Site;

public class XGBTextNewsTask implements BaseTask {
    public final static String[] XGBTextNewsStarUrls = {
            "http://xsc.scu.edu.cn/P/PartialArticle?id=42&menu=42&rn=1",
            "http://xsc.scu.edu.cn/P/PartialArticle?id=42&menu=42&rn=2"
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
        return XGBTextNewsStarUrls;
    }

    @Override
    public Class getPageClass() {
        return XGBPage.class;
    }

    @Override
    public String toString() {
        return "Task [XGB Text News]";
    }
}
