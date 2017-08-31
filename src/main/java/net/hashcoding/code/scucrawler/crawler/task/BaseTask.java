package net.hashcoding.code.scucrawler.crawler.task;

import us.codecraft.webmagic.Site;

public interface BaseTask {
    String getType();

    Site getSite();

    String[] getUrl();

    Class getPageClass();
}
