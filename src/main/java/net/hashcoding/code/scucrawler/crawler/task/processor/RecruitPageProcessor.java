package net.hashcoding.code.scucrawler.crawler.task.processor;

import net.hashcoding.code.scucrawler.crawler.task.BasePageProcessor;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecruitPageProcessor extends BasePageProcessor {
    private static final String helperUrl = "http://jy.scu.edu.cn/eweb/jygl/zpfw.so?modcode=jygl_zpxxck&subsyscode=zpfw&type=searchZpxx&xxlb=5100";
    private static final String[] RECRUIT_TARGET_URLS = {
            "http://jy\\.scu\\.edu\\.cn/eweb/jygl/zpfw\\.so\\?modcode=jygl_zpxxck&subsyscode=zpfw&type=viewZpxx&id=\\w+"
    };

    private List<Extractor> extractors = new ArrayList<>();

    public RecruitPageProcessor() {
        extractors.add(new Extractor(this::processRecruit, RECRUIT_TARGET_URLS));
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

    private void parseRecruitUrls(Page page) {
        List<String> functions = page.getHtml()
                .xpath("//div[@class='z_newsl']//a/@onclick/regex('viewZpxx\\('\\w+',\\s+'\\w+'\\)')")
                .all();
        Pattern pattern = Pattern.compile("viewZpxx\\('(\\w+)',\\s+'(\\w+)'\\)");

        for (String str : functions) {
            Matcher matcher = pattern.matcher(str);
            if (!matcher.matches() || matcher.groupCount() != 2)
                continue;
            String id = matcher.group(1), type = matcher.group(2);
            String url = "/eweb/jygl/zpfw.so?modcode=" + type
                    + "&subsyscode=zpfw&type=viewZpxx&id=" + id;
            page.addTargetRequest(url);
        }
    }

    private void processHelperUrl(Page page) {
        page.setSkip(true);

        parseRecruitUrls(page);
    }

    private void processRecruit(Page page) {
        String titlePattern = "//div[@class='content']/regex('<td.*(?=>)>主题</td>\\s+<td.*(?=>)>\\s+(\\S+)\\s+</td>', 1)";
        String contentPattern = "//div[@class='content']//div[@class='bd_one']/html()";

        List<String> contents = page.getHtml().xpath(contentPattern).all();

        String url = page.getUrl().toString();
        String title = page.getHtml().xpath(titlePattern).toString();
        String content = StringUtils.join(contents, ' ');
        page.getResultItems().put("title", title);
        page.getResultItems().put("content", content);
        page.getResultItems().put("url", url);
        page.getResultItems().put("type", "川大招聘");
//        System.out.println("川大招聘" + " -> " + title);
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
