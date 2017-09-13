package net.hashcoding.code.scucrawler.crawler.task.processor;

import net.hashcoding.code.scucrawler.crawler.task.BasePageProcessor;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CareerTalkPageProcessor extends BasePageProcessor {
    private static final String helperUrl = "http://jy.scu.edu.cn/eweb/jygl/zpfw.so?modcode=jygl_xjhxxck&subsyscode=zpfw&type=searchXjhxx&xjhType=all";
    private static final String[] CAREER_TALK_TARGET_URLS = {
            "http://jy\\.scu\\.edu\\.cn/eweb/jygl/zpfw\\.so\\?modcode=jygl_xjhxxck&subsyscode=zpfw&type=viewXjhxx&id=\\w+"
    };

    private List<Extractor> extractors = new ArrayList<>();

    public CareerTalkPageProcessor() {
        extractors.add(new Extractor(this::processCareerTalk, CAREER_TALK_TARGET_URLS));
    }

    @Override
    public void process(Page page) {
        if (page.getUrl().toString().equals(helperUrl)) {
            processHelperUrl(page);
        } else {
            for (Extractor extractor : extractors) {
                if (extractor.isMatch(page)) {
                    extractor.extract(page);
                    break;
                }
            }
            if (page.getResultItems().getAll().size() == 0) {
                page.getResultItems().setSkip(true);
            }
        }
    }

    private void parseCareerTalkUrls(Page page) {
        List<String> xjhxxs = page.getHtml()
                .xpath("//div[@class='z_newsl']//a/@onclick/regex('viewXphxx\\('(\\w+)'\\)', 1)")
                .all();
        for (String str : xjhxxs) {
            page.addTargetRequest("/eweb/jygl/zpfw.so?modcode=jygl_xjhxxck&subsyscode=zpfw&type=viewXjhxx&id=" + str);
        }
    }

    private void processHelperUrl(Page page) {
        page.setSkip(true);

        parseCareerTalkUrls(page);
    }

    private void processCareerTalk(Page page) {
        String titlePattern = "//div[@class='content']/regex('<td.*(?=>)>主题</td>\\s+<td.*(?=>)>\\s+(\\S+)\\s+</td>', 1)";
        String contentPattern = "//div[@class='content']//div[@class='bd_one']/html()";
        String timePattern = "//table[@id='zpxxtab']/regex('<td\\s+class=\"td_border_two\">(\\d+-\\d+-\\d+(&nbsp;)+\\d+：\\d+)', 1)";
        List<String> contents = page.getHtml().xpath(contentPattern).all();

        String url = page.getUrl().toString();
        String title = page.getHtml().xpath(titlePattern).toString();
        String content = StringUtils.join(contents, ' ');
        page.getResultItems().put("title", title);
        page.getResultItems().put("content", content);
        page.getResultItems().put("url", url);
        page.getResultItems().put("type", "川大宣讲会");

        String time = page.getHtml().xpath(timePattern).toString();
        time = time.replaceAll("(&nbsp;)+", " ");
        time = time.replaceAll("：", ":");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = dateFormat.parse(time);
            page.getResultItems().put("createdAt", date);
        } catch (ParseException e) {
            /* ignore */
        }
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
        return new String[]{helperUrl};
    }
}
