package net.hashcoding.code.scucrawler.crawler.task;

import us.codecraft.webmagic.processor.PageProcessor;

public abstract class BasePageProcessor implements PageProcessor {
    public abstract String[] getStartUrls();
}
