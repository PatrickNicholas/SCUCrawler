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
 * Created by Maochuan on 2016/10/26.
 */
@TargetUrl(value = "http://xsc.scu.edu.cn/P/Info/*")
public class XGBPage implements BasePage {

    public final static String[] START_URLS = {
            "http://xsc.scu.edu.cn/P/PartialArticle?id=43&menu=43&rn=1",
            "http://xsc.scu.edu.cn/P/PartialArticle?id=43&menu=43&rn=2"
    };
    private final static String TYPE = "学工部通知";

    @ExtractByUrl(".*")
    String url;

    @ExtractBy(value = "//h1[@class='v-info-tle']/text()")
    String title;

    @ExtractBy(value = "//div[@class='v-info-content']/html()")
    String content;
    @Formatter("yyyy-MM-dd")
    @ExtractBy("//div[@class='v-info-info']/regex('\\d+-\\d+-\\d+')")
    Date createdAt;

    @ExtractBy(value = "//p[@class='u-down-attach']//a/text()")
    List<String> attachmentName;

    @ExtractBy(value = "//p[@class='u-down-attach']//a/@href")
    List<String> attachmentUrl;

    @Override
    public Page getPage() {
        return Page.create(
                TYPE,
                url, "",
                title, content, createdAt, attachmentUrl, attachmentName);
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
