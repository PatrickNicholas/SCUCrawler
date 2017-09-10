package net.hashcoding.code.scucrawler.crawler.processor.pipeline;

import net.hashcoding.code.scucrawler.crawler.processor.PageFactory;
import net.hashcoding.code.scucrawler.crawler.task.BasePage;
import net.hashcoding.code.scucrawler.entity.Page;
import net.hashcoding.code.scucrawler.utils.HtmlPreprocessor;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

/**
 * Created by Maochuan on 2016/10/17.
 */
public class BasePageModelPipeline
        implements PageModelPipeline<BasePage> {

    public void process(BasePage page, Task task) {
        String host = HtmlPreprocessor.getHostWithProtocol(
                task.getSite().getDomain());
        Page target = page.getPage();
        PageFactory.solve(host, target);
    }
}
