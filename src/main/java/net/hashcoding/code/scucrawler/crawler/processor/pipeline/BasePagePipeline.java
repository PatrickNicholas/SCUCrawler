package net.hashcoding.code.scucrawler.crawler.processor.pipeline;

import net.hashcoding.code.scucrawler.crawler.processor.PageFactory;
import net.hashcoding.code.scucrawler.entity.Page;
import net.hashcoding.code.scucrawler.utils.HtmlPreprocessor;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.Date;

public class BasePagePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        String host = HtmlPreprocessor.getHostWithProtocol(
                task.getSite().getDomain());

        String url = ifAbsence(resultItems.get("url"));
        String type = resultItems.get("type");
        String title = ifAbsence(resultItems.get("title"));
        String content = ifAbsence(resultItems.get("content"));
        Page page = Page.create(type, url, "", title,
                content, new Date(), new ArrayList<>(), new ArrayList<>());

        PageFactory.solve(host, page);
    }

    private String ifAbsence(String str) {
        if (str == null)
            return "";
        return str;
    }
}

