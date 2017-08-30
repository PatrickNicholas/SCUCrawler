package net.hashcoding.code.scucrawler.task.pages;

import net.hashcoding.code.scucrawler.entity.Page;
import net.hashcoding.code.scucrawler.task.BasePage;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maochuan on 2016/11/4.
 */
@TargetUrl(value = {
        "http://cs.scu.edu.cn/cs/xytz/webinfo/*",
        "http://cs.scu.edu.cn/cs/xyxw/webinfo/*",
}, sourceRegion = "//table[@id='__01']//a")
public class CSPage implements BasePage {

    @ExtractByUrl(".*")
    String url;

    // @Formatter(formatter = HtmlEscapeFormatter.class)
    @ExtractBy(value = "//table[@class='pcenter_t2']/allText()")
    String title;

    // @Formatter(formatter = HtmlEscapeFormatter.class)
    @ExtractBy(value = "//div[@id='BodyLabel']/html()")
    String content;

    //@ExtractBy(value="//body/table[4]//a/text()")
    List<String> names = new ArrayList<>();

    //@ExtractBy(value="//body/table[4]//a/@href")
    List<String> urls = new ArrayList<>();


    @Override
    public Page getPage() {
        return Page.create(url, "", title, content, urls, names);
    }
}
