package net.hashcoding.code.scucrawler.task.pages;

import net.hashcoding.code.scucrawler.entity.Page;
import net.hashcoding.code.scucrawler.task.BasePage;
import net.hashcoding.code.scucrawler.utils.HtmlEscapeFormatter;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.Formatter;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.List;

/**
 * Created by Maochuan on 2016/10/26.
 */
@TargetUrl(value = "http://xsc.scu.edu.cn/P/Info/*")
public class XGBPage implements BasePage {

    @ExtractByUrl(".*")
    String url;

    @Formatter(formatter = HtmlEscapeFormatter.class)
    @ExtractBy(value = "//h1[@class='v-info-tle']/text()")
    String title;

    @Formatter(formatter = HtmlEscapeFormatter.class)
    @ExtractBy(value = "//div[@class='v-info-content']/html()")
    String content;

    @ExtractBy(value = "//p[@class='u-down-attach']//a/text()")
    List<String> attachmentName;

    @ExtractBy(value = "//p[@class='u-down-attach']//a/@href")
    List<String> attachmentUrl;

    @Override
    public Page getPage() {
        return Page.create(url, "", title, content, attachmentUrl, attachmentName);
    }
}
