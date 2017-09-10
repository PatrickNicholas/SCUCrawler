package net.hashcoding.code.scucrawler.crawler.task;

import net.hashcoding.code.scucrawler.entity.Page;
import us.codecraft.webmagic.Site;

public interface BasePage {
    Page getPage();

    Site getSite();

    String[] getStartUrls();
}
