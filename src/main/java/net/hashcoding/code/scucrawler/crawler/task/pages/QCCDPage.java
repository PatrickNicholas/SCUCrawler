package net.hashcoding.code.scucrawler.crawler.task.pages;

import net.hashcoding.code.scucrawler.crawler.task.BasePage;
import net.hashcoding.code.scucrawler.entity.Page;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.List;

/**
 * Created by Maochuan on 2016/10/27.
 */
@TargetUrl(value = {
        "http://youth.scu.edu.cn/index.php/main/web/notice/detail/*",
        "http://youth.scu.edu.cn/index.php/main/web/news-group/detail/*",
        "http://youth.scu.edu.cn/index.php/main/web/courtyard-style/detail/*",
}, sourceRegion = "//ul[@class='list-art']//a")
public class QCCDPage implements BasePage {

    @ExtractByUrl(".*")
    String url;

    // @Formatter(formatter = HtmlEscapeFormatter.class)
    @ExtractBy(value = "//div[@class='content-art']/h1/text()")
    String title;

    // @Formatter(formatter = HtmlEscapeFormatter.class)
    @ExtractBy(value = "//div[@class='content-art']/div[@class='content-text']/html()")
    String content;

    // TODO:
    @ExtractBy(value = "//body/table[4]//a/text()")
    List<String> attachmentName;

    // TODO:
    @ExtractBy(value = "//body/table[4]//a/@href")
    List<String> attachmentUrl;

    @Override
    public Page getPage() {
        return Page.create(url, "", title, content, attachmentUrl, attachmentName);
    }
}
