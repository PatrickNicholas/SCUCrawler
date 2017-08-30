package net.hashcoding.code.scucrawler.processor;

import net.hashcoding.code.scucrawler.entity.Page;
import net.hashcoding.code.scucrawler.utils.HtmlPreprocessor;
import net.hashcoding.code.scucrawler.utils.StringEscapeUtil;

public class PageProcessor implements Runnable {
    private String host;
    private Page page;
    private SolveCallback callback;

    public PageProcessor(String host, Page page, SolveCallback callback) {
        this.host = host;
        this.page = page;
        this.callback = callback;
    }

    @Override
    public void run() {
        pretreatment();
        solve();
        postTreatment();
        PagePersistence.submit(page);
    }

    private void pretreatment() {
        HtmlPreprocessor.imagesAddHostAndGetThumbnail(page, host);
        HtmlPreprocessor.hyperlinksAddHost(page, host);
        HtmlPreprocessor.processOmitsAttachments(page);
        HtmlPreprocessor.attachmentAddHost(page, host);

        page.title = StringEscapeUtil.unescapeHTML(page.title);
        page.content = StringEscapeUtil.unescapeHTML(page.content);
    }

    private void postTreatment() {
        page.title = StringEscapeUtil.unescapeFilename(page.title);
    }

    private void solve() {
        callback.call(page);
    }

    public interface SolveCallback {
        void call(Page page);
    }
}
