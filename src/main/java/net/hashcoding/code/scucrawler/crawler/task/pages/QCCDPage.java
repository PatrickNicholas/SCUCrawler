package net.hashcoding.code.scucrawler.crawler.task.pages;

import net.hashcoding.code.scucrawler.crawler.task.BasePage;
import net.hashcoding.code.scucrawler.entity.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.Formatter;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.Date;
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
    private static final String TYPE = "青春川大通知";
    private static final String[] START_URLS = {
            "http://youth.scu.edu.cn/index.php/main/web/notice/"
    };

    @ExtractByUrl(".*")
    String url;

    @ExtractBy(value = "//div[@class='content-art']/h1/text()")
    String title;

    @ExtractBy(value = "//div[@class='content-art']/div[@class='content-text']/html()")
    String content;

    @Formatter("yyyy-MM-dd HH:mm:ss")
    @ExtractBy("//div[@class='content-art']/div[@class='attr']//regex('\\d+-\\d+-\\d+\\s+\\d+:\\d+:\\d+')")
    Date createdAt;

    // TODO:
    @ExtractBy(value = "//body/table[4]//a/text()")
    List<String> attachmentName;

    // TODO:
    @ExtractBy(value = "//body/table[4]//a/@href")
    List<String> attachmentUrl;

    @Override
    public Page getPage() {
        return Page.create(
                TYPE,
                url,
                "",
                title,
                content,
                createdAt,
                attachmentUrl,
                attachmentName);
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(3)
                .setRetrySleepTime(500)
                .setSleepTime(50)
                .setCharset("UTF-8");
    }

    @Override
    public String[] getStartUrls() {
        return START_URLS;
    }
}
