package net.hashcoding.code.scucrawler.utils;

import net.hashcoding.code.scucrawler.entity.Page;
import org.apache.http.util.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Maochuan on 2016/10/29.
 */
public class HtmlPreprocessor {
    public static String getHostWithProtocol(String originHost) {
        // absUrl need protocol of host
        if (!originHost.startsWith("http://")
                && !originHost.startsWith("HTTP://")) {
            originHost = "http://" + originHost;
        }
        return originHost;
    }

    public static void imagesAddHostAndGetThumbnail(
            Page page, String host) {
        final String[] thumbnail = {""};

        Document root = Jsoup.parse(page.content);
        root.setBaseUri(host);

        Elements images = root.select("img");
        images.forEach((Element element) -> {
            String url = element.absUrl("src");
            element.attr("src", url);
            if (TextUtils.isEmpty(thumbnail[0]))
                thumbnail[0] = url;
        });

        page.content = root.toString();
        page.thumbnail = thumbnail[0];
    }

    public static void hyperlinksAddHost(Page page, String host) {
        Document root = Jsoup.parse(page.content);
        root.setBaseUri(host);

        Elements images = root.select("a");
        images.forEach((Element element) ->
            element.attr("href", element.absUrl("href"))
        );

        page.content = root.toString();
    }

    public static void attachmentAddHost(Page page, String host) {
        List<Page.Attachment> attachments = page.attachments;
        Iterator<Page.Attachment> it = attachments.iterator();
        while (it.hasNext()) {
            Page.Attachment attachment = it.next();
            attachment.url = StringUtil.resolve(host, attachment.url);
        }
    }

    public static void processOmitsAttachments(Page page) {
        // TODO: 等待大神
    }
}
