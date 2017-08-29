package net.hashcoding.code.scucrawler.processor.solver;

import net.hashcoding.code.scucrawler.utils.HtmlToMarkdown;

import java.io.IOException;

public class HtmlToMarkdownSolver implements PageSolver {

    public String solve(String content) {
        try {
            return HtmlToMarkdown.convertHtml(content, "GBK");
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
            return null;
        }
    }

}
