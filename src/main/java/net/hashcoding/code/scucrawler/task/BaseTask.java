package net.hashcoding.code.scucrawler.task;

import us.codecraft.webmagic.Site;

public interface BaseTask {
    Site getSite();

    String[] getUrl();

    Class getPageClass();
}
