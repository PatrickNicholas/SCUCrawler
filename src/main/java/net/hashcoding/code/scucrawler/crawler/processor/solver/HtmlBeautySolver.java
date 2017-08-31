package net.hashcoding.code.scucrawler.crawler.processor.solver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Maochuan Aug 30, 2017
 * <p>
 * Beautify the corresponding HTML code.
 * <p>
 * NOTICE: output's charset is "UTF-8".
 * <p>
 * There are a few steps:
 * 1. remove illegal elements, like: input,button,embed,script
 * 2. strip redundancy elements, like: span,font
 * 3. remove blank elements, like: `<div></div>`, ignore `<img>`
 * 4. merge the same adjacent elements, there are some case:
 * 1) trivial element: <b>xxx</b><b>ccc</b>
 * 2) <b><b>xxx</b></b>
 * 5. remove unnecessary attributes, like: style,id,class
 */
public class HtmlBeautySolver implements PageSolver {

    private HashSet<String> redundancyElements;
    private HashSet<String> trivialElements;
    private HashSet<String> voidElements;
    private HashMap<String, ProcessElement> processor;

    public HtmlBeautySolver() {
        redundancyElements = new HashSet<>(Arrays.asList("span", "font"));
        trivialElements = new HashSet<>(Arrays.asList(
                "u", "b", "i", "em", "strong", "big", "small", "strike"
        ));

        voidElements = new HashSet<>(Arrays.asList("br", "img", "hr"));

        processor = new HashMap<>();
        processor.put("table", this::parseTable);
    }

    @Override
    public String solve(String content) {
        Document doc = Jsoup.parse(content);

        doc.outputSettings()
                .charset("UTF-8")
                .prettyPrint(true);

        removeIllegalElements(doc);
        stripIllegalElements(doc);
        removeBlankElements(doc);
        mergeAdjacencyAlikeElement(doc);
        removeUnnecessaryAttributes(doc);

        if (doc.body() == null) {
            return "";
        }
        return doc.body().html();
    }

    /**
     * Delete a blank child node.
     *
     * @param element .
     * @return true if this element empty, otherwise false.
     */
    private boolean removeBlankChildren(Element element) {
        int idx = 0;
        while (idx < element.childNodeSize()) {
            Node node = element.childNode(idx);
            if (!isElement(node) && !isTextNode(node))
                node.remove();
            else if (isBlankTextNode(node))
                node.remove();
            else if (isBlankElement(node))
                node.remove();
            else
                idx++;
        }

        return element.childNodeSize() == 0 &&
                !voidElements.contains(element.tagName());
    }

    private boolean removeBlankElements(Element element) {
        String name = element.tagName().toLowerCase();
        ProcessElement processor = this.processor.get(name);
        if (processor == null) {
            return removeBlankChildren(element);
        } else {
            return processor.process(element);
        }
    }

    private boolean parseTable(Element element) {
        element.attr("width", "100%");
        return removeBlankChildren(element);
    }

    private void removeUnnecessaryAttributes(Document document) {
        document.getAllElements()
                .removeAttr("style")
                .removeAttr("class")
                .removeAttr("lang")
                .removeAttr("name")
                .removeAttr("id");
    }

    private void removeIllegalElements(Element element) {
        element.select("input").remove();
        element.select("button").remove();
        element.select("script").remove();
        element.select("embed").remove();
    }

    private void stripIllegalElements(Element element) {
        int idx = 0;
        while (idx < element.childNodeSize()) {
            Node node = element.childNode(idx);
            if (isElement(node)) {
                Element child = (Element) node;
                if (redundancyElements.contains(child.tagName())) {
                    element.insertChildren(idx, child.childNodes());
                    child.remove();
                    continue;
                } else {
                    stripIllegalElements(child);
                }
            }
            idx++;
        }
    }

    private void mergeAdjacencyAlikeElement(Element element) {
        int idx = 0;

        // first: remove adjacency element in current element.
        if (trivialElements.contains(element.tagName())) {
            while (idx < element.childNodeSize() - 1) {
                Node node = element.childNode(idx);
                Node next = element.childNode(idx + 1);
                if (tryMergeTextNodes(node, next) ||
                        tryMergeElements(node, next)) {
                    continue;
                }
                idx++;
            }
        }

        // second: recursively merge adjacency element.
        idx = 0;
        while (idx < element.childNodeSize()) {
            Node node = element.childNode(idx);
            if (isElement(node))
                mergeAdjacencyAlikeElement((Element) node);
            idx++;
        }

        // third:
        // <div><div>xxx</div></div>
        if (element.childNodeSize() == 1) {
            Node node = element.childNode(0);
            if (isElement(node) && ((Element) node).tag().equals(element.tag())) {
                element.insertChildren(0, node.childNodes());
                node.remove();
            }
        }
    }

    private boolean tryMergeTextNodes(Node node1, Node node2) {
        if (!isTextNode(node1) || !isTextNode(node2))
            return false;

        TextNode text1 = (TextNode) node1, text2 = (TextNode) node2;
        text1.text(text1.text() + text2.text());
        text2.remove();
        return true;
    }

    private boolean tryMergeElements(Node node1, Node node2) {
        if (!isElement(node1) || !isElement(node2))
            return false;

        Element e1 = (Element) node1, e2 = (Element) node2;
        if (e1.tag().equals(e2.tag())) {
            e1.insertChildren(e1.childNodeSize(), e2.childNodes());
            e2.remove();
            return true;
        }
        return false;
    }

    private boolean isTextNode(Node node) {
        return node instanceof TextNode;
    }

    private boolean isBlankTextNode(Node node) {
        return isTextNode(node) && ((TextNode) node).isBlank();
    }

    private boolean isElement(Node node) {
        return (node instanceof Element);
    }

    private boolean isBlankElement(Node node) {
        return isElement(node) && removeBlankElements((Element) node);
    }

    private interface ProcessElement {
        boolean process(Element element);
    }
}
