package net.hashcoding.code.scucrawler.processor.solver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.HashMap;
import java.util.HashSet;

public class HtmlBeautySolver implements PageSolver {

    private HashMap<String, ProcessElement> processor = new HashMap<>();
    private HashSet<String> redundancyElement = new HashSet<>();
    private HashSet<String> trivialElement = new HashSet<>();

    public HtmlBeautySolver() {
        processor.put("table", this::parseTable);

        redundancyElement.add("span");
        redundancyElement.add("font");

        trivialElement.add("u");
        trivialElement.add("b");
        trivialElement.add("i");
        trivialElement.add("em");
        trivialElement.add("strong");
        trivialElement.add("big");
        trivialElement.add("small");
        trivialElement.add("strike");
        trivialElement.add("strong");
    }

    @Override
    public String solve(String content) {
        Document doc = Jsoup.parse(content);

        doc.outputSettings()
                .charset("UTF-8")
                .prettyPrint(true);

        removeIllegalElement(doc);
        stripIllegalElement(doc);
        removeAttributes(doc);

        if (parseChildren(doc)) {
            return "";
        }

        mergeAdjacencyAlikeElement(doc);

        return doc.body().html();
    }

    private boolean parseChildren(Element element) {
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

        return element.childNodeSize() == 0;
    }

    private boolean parseElement(Element element) {
        String name = element.tagName().toLowerCase();
        ProcessElement processor = this.processor.get(name);
        if (processor == null) {
            return parseChildren(element);
        } else {
            return processor.process(element);
        }
    }

    private boolean parseTable(Element element) {
        element.attr("width", "100%");
        return parseChildren(element);
    }

    private void removeAttributes(Document document) {
        document.getAllElements()
                .removeAttr("style")
                .removeAttr("class")
                .removeAttr("lang")
                .removeAttr("name")
                .removeAttr("id");
    }

    private void removeIllegalElement(Element element) {
        element.select("input").remove();
        element.select("button").remove();
        element.select("script").remove();
        element.select("embed").remove();
    }

    private void stripIllegalElement(Element element) {
        int idx = 0;
        while (idx < element.childNodeSize()) {
            Node node = element.childNode(idx);
            if (isElement(node)) {
                Element child = (Element) node;
                if (redundancyElement.contains(child.tagName())) {
                    element.insertChildren(idx, child.childNodes());
                    child.remove();
                    continue;
                } else {
                    stripIllegalElement(child);
                }
            }
            idx++;
        }
    }

    private void mergeAdjacencyAlikeElement(Element element) {
        int idx = 0;

        // first: remove adjacency element in current element.
        if (trivialElement.contains(element.tagName())) {
            while (idx < element.childNodeSize() - 1) {
                Node node = element.childNode(idx);
                Node next = element.childNode(idx + 1);
                if (isTextNode(node) && isTextNode(next)) {
                    TextNode text1 = (TextNode) node, text2 = (TextNode) next;
                    text1.text(text1.text() + text2.text());
                    text2.remove();
                    continue;
                } else if (isElement(node) && isElement(next)) {
                    Element el1 = (Element) node, el2 = (Element) next;
                    if (el1.tag().equals(el2.tag())) {
                        el1.insertChildren(el1.childNodeSize(), el2.childNodes());
                        el2.remove();
                        continue;
                    }
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

    private boolean isTextNode(Node node) {
        return node instanceof TextNode;
    }

    private boolean isBlankTextNode(Node node) {
        return isTextNode(node) &&
                ((TextNode) node).isBlank();
    }

    private boolean isElement(Node node) {
        return (node instanceof Element);
    }

    private boolean isBlankElement(Node node) {
        return isElement(node) && parseElement((Element) node);
    }

    private interface ProcessElement {
        boolean process(Element element);
    }
}
