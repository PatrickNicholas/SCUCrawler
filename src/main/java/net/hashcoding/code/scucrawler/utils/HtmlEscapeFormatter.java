package net.hashcoding.code.scucrawler.utils;

import us.codecraft.webmagic.model.formatter.ObjectFormatter;

public class HtmlEscapeFormatter implements ObjectFormatter<String> {

    public String format(String raw) throws Exception {
        if (raw == null)
            return null;
        raw = raw.trim();
        return StringEscapeUtil.unescapeHTML(raw, 0);
    }

    public Class<String> clazz() {
        return String.class;
    }

    public void initParam(String[] extra) {
    }

}
