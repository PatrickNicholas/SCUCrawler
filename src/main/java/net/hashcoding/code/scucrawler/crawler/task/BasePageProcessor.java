package net.hashcoding.code.scucrawler.crawler.task;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class BasePageProcessor implements PageProcessor {
    public abstract String[] getStartUrls();

    public interface ExtractorInterface {
        void extract(Page page);
    }

    public static class Extractor {
        private ExtractorInterface extractor;
        private List<Pattern> targetUrlPatterns = new ArrayList<>();

        public Extractor(ExtractorInterface inter, String... targetUrls) {
            extractor = inter;
            for (String str : targetUrls) {
                targetUrlPatterns.add(Pattern.compile(str));
            }
        }

        public boolean isMatch(Page page) {
            for (Pattern targetPattern : targetUrlPatterns) {
                if (targetPattern.matcher(page.getUrl().toString()).matches()) {
                    return true;
                }
            }
            return false;
        }

        public void extract(Page page) {
            if (extractor != null) {
                extractor.extract(page);
            } else {
                page.setSkip(true);
            }
        }
    }
}
