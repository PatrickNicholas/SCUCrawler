package net.hashcoding.code.scucrawler.crawler.task.pages;

import net.hashcoding.code.scucrawler.crawler.task.BasePage;
import net.hashcoding.code.scucrawler.entity.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.Formatter;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TargetUrl(value = {
        "http://cs.scu.edu.cn/cs/zsjy/jyxx/webinfo/*"
}, sourceRegion = "//table[@id='__01']//a")
public class CSRecruitPage implements BasePage {

    private static final String TYPE = "计算机学院招聘信息";
    private static final String[] START_URLS = {
            "http://cs.scu.edu.cn/cs/zsjy/jyxx/H951705index_1.htm"
    };

    @ExtractByUrl(".*")
    String url;

    @ExtractBy(value = "//table[@class='pcenter_t2']/allText()")
    String title;

    @ExtractBy(value = "//div[@id='BodyLabel']/html()")
    String content;

    @Formatter("yyyy-MM-dd HH:mm")
    @ExtractBy("//table[@id='__01']//span[@class='hangjc']/regex('\\d+-\\d+-\\d+\\s+\\d+:\\d+')")
    Date createdAt;

    //@ExtractBy(value="//body/table[4]//a/text()")
    List<String> names = new ArrayList<>();

    //@ExtractBy(value="//body/table[4]//a/@href")
    List<String> urls = new ArrayList<>();

    @Override
    public Page getPage() {
        return Page.create(
                TYPE,
                url,
                "",
                title,
                content,
                createdAt,
                urls,
                names);
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetrySleepTime(500)
                .setRetryTimes(3)
                .setSleepTime(50)
                .setCharset("GBK");
    }

    @Override
    public String[] getStartUrls() {
        return START_URLS;
    }
}
