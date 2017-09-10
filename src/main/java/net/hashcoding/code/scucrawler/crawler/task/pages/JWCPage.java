package net.hashcoding.code.scucrawler.crawler.task.pages;

import net.hashcoding.code.scucrawler.crawler.task.BasePage;
import net.hashcoding.code.scucrawler.entity.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.*;

import java.util.Date;
import java.util.List;

@TargetUrl("http://jwc.scu.edu.cn/jwc/newsShow.action*")
@HelpUrl("http://jwc.scu.edu.cn/jwc/moreNotice.action")
public class JWCPage implements BasePage {
    private static final String TYPE = "教务处通知";
    private static final String[] START_URLS = {
            "http://jwc.scu.edu.cn/jwc/moreNotice.action",
            "http://jwc.scu.edu.cn/jwc/moreNotice.action?pager.pageNow=2",
    };

    @ExtractByUrl(".*")
    String url;

    @ExtractBy(value = "//body/table[3]/tbody/tr[2]/td/b/text()")
    String title;

    @ExtractBy(value = "//input[@id='news_content']/@value")
    String content;

    @Formatter("yyyy.MM.dd")
    @ExtractBy(value = "//font[@color='#999999']/regex('\\d+\\.\\d+\\.\\d+')")
    Date createdAt;

    @ExtractBy(value = "//body/table[4]//a/text()")
    List<String> attachmentName;

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
