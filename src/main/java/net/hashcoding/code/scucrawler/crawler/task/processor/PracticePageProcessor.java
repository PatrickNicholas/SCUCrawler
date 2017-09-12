package net.hashcoding.code.scucrawler.crawler.task.processor;

import net.hashcoding.code.scucrawler.crawler.task.BasePageProcessor;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PracticePageProcessor extends BasePageProcessor {

    private static final String helperUrl = "http://jy.scu.edu.cn/eweb/jygl/zpfw.so?modcode=jygl_zpfwjykx&subsyscode=zpfw&type=searchSxkxOrLyms&newstype=jykx";
    private static final String[] PRACTICE_TARGET_URLS = {
            "http://jy\\.scu\\.edu\\.cn/eweb/jygl/zpfw\\.so\\?modcode=jygl_zpfwjykx&subsyscode=zpfw&type=viewScfwNews&id=\\w+"
    };

    private List<Extractor> extractors = new ArrayList<>();

    public PracticePageProcessor() {
        extractors.add(new Extractor(this::processPractice, PRACTICE_TARGET_URLS));
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

    private void parsePracticeUrls(Page page) {
        List<String> ids = page.getHtml()
                .xpath("//div[@class='z_newsl']//a/@onclick/regex('viewNews\\('(\\w+)'\\)', 1)")
                .all();
        for (String id : ids) {
            page.addTargetRequest("/eweb/jygl/zpfw.so?modcode=jygl_zpfwjykx&subsyscode=zpfw&type=viewScfwNews&id=" + id);
        }
    }

    private void processHelperUrl(Page page) {
        page.setSkip(true);

        parsePracticeUrls(page);
    }

    private void processPractice(Page page) {
        String titlePattern = "//p[@class='d_title']/text()";
        String createdAtPattern = "//div[@class='d_time']/text()/regex('(\\d+-\\d+-\\d+\\s+\\d+:\\d+)', 1)";
        String contentPattern = "//div[@class='d_intro']/html()";

        String url = page.getUrl().toString();
        String title = page.getHtml().xpath(titlePattern).toString();
        String stringCreatedAt = page.getHtml().xpath(createdAtPattern).toString();
        String content = page.getHtml().xpath(contentPattern).toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(stringCreatedAt);
            page.getResultItems().put("createdAt", date);
        } catch (ParseException e) {
            /* ignore */
        }
        page.getResultItems().put("title", title);
        page.getResultItems().put("content", content);
        page.getResultItems().put("url", url);
        page.getResultItems().put("type", "川大实习");
        // System.out.println("川大实习" + " -> " + title);
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
