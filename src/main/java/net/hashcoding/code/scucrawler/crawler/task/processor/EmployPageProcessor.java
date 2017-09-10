package net.hashcoding.code.scucrawler.crawler.task.processor;

import net.hashcoding.code.scucrawler.crawler.task.BasePageProcessor;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployPageProcessor extends BasePageProcessor {

    private static final String helperUrl = "http://jy.scu.edu.cn/eweb/jygl/index.so";
    private static final String[] CAREER_TALK_TARGET_URLS = {
            "http://jy\\.scu\\.edu\\.cn/eweb/jygl/zpfw\\.so\\?modcode=jygl_xjhxxck&subsyscode=zpfw&type=viewXjhxx&id=\\w+"
    };
    private static final String[] RECRUIT_TARGET_URLS = {
            "http://jy\\.scu\\.edu\\.cn/eweb/jygl/zpfw\\.so\\?modcode=jygl_zpxxck&subsyscode=zpfw&type=viewZpxx&id=\\w+"
    };
    private static final String[] PRACTICE_TARGET_URLS = {
            "http://jy\\.scu\\.edu\\.cn/eweb/jygl/zpfw\\.so\\?modcode=jygl_zpfwjykx&subsyscode=zpfw&type=viewScfwNews&id=\\w+"
    };

    private List<Extractor> extractors = new ArrayList<>();

    public EmployPageProcessor() {
        extractors.add(new Extractor(this::processCareerTalk, CAREER_TALK_TARGET_URLS));
        extractors.add(new Extractor(this::processRecruit, RECRUIT_TARGET_URLS));
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

    private void parseCareerTalkUrls(Page page) {
        List<String> xjhxxs = page.getHtml()
                .xpath("//div[@id='content']//ul[2]//a/@onclick/regex('viewXjhxx\\('(\\w+)'\\)', 1)")
                .all();
        for (String str : xjhxxs) {
            page.addTargetRequest("/eweb/jygl/zpfw.so?modcode=jygl_xjhxxck&subsyscode=zpfw&type=viewXjhxx&id=" + str);
        }
    }

    private void parseRecruitUrls(Page page) {
        List<String> functions = page.getHtml()
                .xpath("//div[@id='content']//ul[3]//a/@onclick/regex('viewZpxx\\('\\w+',\\s+'\\w+'\\)')")
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

    private void parsePracticeUrls(Page page) {
        List<String> ids = page.getHtml()
                .xpath("//div[@id='content']//ul[2]//a/@onclick/regex('viewJykx\\('(\\w+)'\\)', 1)")
                .all();
        for (String id : ids) {
            page.addTargetRequest("/eweb/jygl/zpfw.so?modcode=jygl_zpfwjykx&subsyscode=zpfw&type=viewScfwNews&id=" + id);
        }
    }

    private void processHelperUrl(Page page) {
        page.setSkip(true);

        parseCareerTalkUrls(page);
        parseRecruitUrls(page);
        parsePracticeUrls(page);
    }

    private void processCareerTalk(Page page) {
        String titlePattern = "//div[@class='content']/regex('<td.*(?=>)>主题</td>\\s+<td.*(?=>)>\\s+(\\S+)\\s+</td>', 1)";
        String contentPattern = "//div[@class='content']/html()";

        String url = page.getUrl().toString();
        String title = page.getHtml().xpath(titlePattern).toString();
        String content = page.getHtml().xpath(contentPattern).toString();
        page.getResultItems().put("title", title);
        page.getResultItems().put("content", content);
        page.getResultItems().put("url", url);
        page.getResultItems().put("type", "川大宣讲会");
//        System.out.println("川大宣讲会" + " -> " + title);
    }

    private void processRecruit(Page page) {
        String titlePattern = "//div[@class='content']/regex('<td.*(?=>)>主题</td>\\s+<td.*(?=>)>\\s+(\\S+)\\s+</td>', 1)";
        String contentPattern = "//div[@class='content']/html()";

        String url = page.getUrl().toString();
        String title = page.getHtml().xpath(titlePattern).toString();
        String content = page.getHtml().xpath(contentPattern).toString();
        page.getResultItems().put("title", title);
        page.getResultItems().put("content", content);
        page.getResultItems().put("url", url);
        page.getResultItems().put("type", "川大招聘");
//        System.out.println("川大招聘" + " -> " + title);
    }

    private void processPractice(Page page) {
        String titlePattern = "//div[@class='content']/regex('<td.*(?=>)>主题</td>\\s+<td.*(?=>)>\\s+(\\S+)\\s+</td>', 1)";
        String contentPattern = "//div[@class='content']/html()";

        String url = page.getUrl().toString();
        String title = page.getHtml().xpath(titlePattern).toString();
        String content = page.getHtml().xpath(contentPattern).toString();
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

    private interface ExtractorInterface {
        void extract(Page page);
    }

    private static class Extractor {
        private ExtractorInterface extractor;
        private List<Pattern> targetUrlPatterns = new ArrayList<>();

        Extractor(ExtractorInterface inter, String... targetUrls) {
            extractor = inter;
            for (String str : targetUrls) {
                targetUrlPatterns.add(Pattern.compile(str));
            }
        }

        private boolean isMatch(Page page) {
            for (Pattern targetPattern : targetUrlPatterns) {
                if (targetPattern.matcher(page.getUrl().toString()).matches()) {
                    return true;
                }
            }
            return false;
        }

        private void extract(Page page) {
            if (extractor != null) {
                extractor.extract(page);
            } else {
                page.setSkip(true);
            }
        }
    }
}
