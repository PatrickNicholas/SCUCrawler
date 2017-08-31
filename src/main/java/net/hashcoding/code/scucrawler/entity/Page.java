package net.hashcoding.code.scucrawler.entity;

import java.util.ArrayList;
import java.util.List;

public class Page {
    public String type;
    public String url;
    public String thumbnail;
    public String title;
    public String content;
    public List<Attachment> attachments;

    public static Page create(String url,
                              String thumb,
                              String title,
                              String content,
                              List<String> attachmentUrls,
                              List<String> attachmentName) {
        Page page = new Page();
        page.type = "";
        page.url = url;
        page.thumbnail = thumb;
        page.title = title;
        page.content = content;
        page.attachments = new ArrayList<>();
        int size = attachmentName.size();
        for (int i = 0; i < size; ++i) {
            Attachment attachment = new Attachment();
            attachment.name = attachmentName.get(i);
            attachment.url = attachmentUrls.get(i);
            page.attachments.add(attachment);
        }
        return page;
    }

    public static class Attachment {
        public String name;
        public String url;
    }
}
