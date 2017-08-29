package net.hashcoding.code.scucrawler.utils;

import net.hashcoding.code.scucrawler.utils.HtmlToMarkdown.MDLine.MDLineType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlToMarkdown {
	private static int indentation = -1;
	private static boolean orderedList = false;

	public static String convert(String theHTML, String baseURL) {
		Document doc = Jsoup.parse(theHTML, baseURL);

		return parseDocument(doc);
	}

	public static String convert(URL url, int timeoutMillis) throws IOException {
		Document doc = Jsoup.parse(url, timeoutMillis);

		return parseDocument(doc);
	}

	public static String convertHtml(String html, String charset) throws IOException {
		Document doc = Jsoup.parse(html, charset);

		return parseDocument(doc);
	}

	public static String convertFile(File file, String charset) throws IOException {
		Document doc = Jsoup.parse(file, charset);

		return parseDocument(doc);
	}

	private static String parseDocument(Document dirtyDoc) {
		indentation = -1;

		String title = dirtyDoc.title();

		Whitelist whitelist = Whitelist.relaxed();
		Cleaner cleaner = new Cleaner(whitelist);

		Document doc = cleaner.clean(dirtyDoc);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);

		if (!title.trim().equals("")) {
			return "# " + title + "\n\n" + getTextContent(doc);
		} else {
			return getTextContent(doc);
		}
	}

	private static String getTextContent(Element element) {
		ArrayList<MDLine> lines = new ArrayList<MDLine>();

		List<Node> children = element.childNodes();
		for (Node child : children) {
			if (child instanceof TextNode) {
				TextNode textNode = (TextNode) child;
				MDLine line = getLastLine(lines);
				if (line.getContent().equals("")) {
					if (!textNode.isBlank()) {
						line.append(textNode.text().replaceAll("#", "/#").replaceAll("\\*", "/\\*"));
					}
				} else {
					line.append(textNode.text().replaceAll("#", "/#").replaceAll("\\*", "/\\*"));
				}

			} else if (child instanceof Element) {
				Element childElement = (Element) child;
				processElement(childElement, lines);
			} else {
				System.out.println();
			}
		}

		int blankLines = 0;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).toString().trim();
			if (line.equals("")) {
				blankLines++;
			} else {
				blankLines = 0;
			}
			if (blankLines < 2) {
				result.append(line);
				if (i < lines.size() - 1) {
					result.append("\n");
				}
			}
		}

		return result.toString();
	}

	private static void processElement(Element element, ArrayList<MDLine> lines) {
		Tag tag = element.tag();

		String tagName = tag.getName();
		if (tagName.equals("div")) {
			div(element, lines);
		} else if (tagName.equals("p")) {
			p(element, lines);
		} else if (tagName.equals("br")) {
			br(lines);
		} else if (tagName.matches("^h[0-9]+$")) {
			h(element, lines);
		} else if (tagName.equals("strong") || tagName.equals("b")) {
			strong(element, lines);
		} else if (tagName.equals("em")) {
			em(element, lines);
		} else if (tagName.equals("hr")) {
			hr(lines);
		} else if (tagName.equals("a")) {
			a(element, lines);
		} else if (tagName.equals("img")) {
			img(element, lines);
		} else if (tagName.equals("code")) {
			code(element, lines);
		} else if (tagName.equals("ul")) {
			ul(element, lines);
		} else if (tagName.equals("ol")) {
			ol(element, lines);
		} else if (tagName.equals("li")) {
			li(element, lines);
		} else {
			MDLine line = getLastLine(lines);
			line.append(getTextContent(element));
		}
	}

	private static MDLine getLastLine(ArrayList<MDLine> lines) {
		MDLine line;
		if (lines.size() > 0) {
			line = lines.get(lines.size() - 1);
		} else {
			line = new MDLine(MDLineType.None, 0, "");
			lines.add(line);
		}

		return line;
	}

	private static void div(Element element, ArrayList<MDLine> lines) {
		MDLine line = getLastLine(lines);
		String content = getTextContent(element);
		if (!content.equals("")) {
			if (!line.getContent().trim().equals("")) {
				lines.add(new MDLine(MDLine.MDLineType.None, 0, ""));
				lines.add(new MDLine(MDLineType.None, 0, content));
				lines.add(new MDLine(MDLineType.None, 0, ""));
			} else {
				if (!content.trim().equals(""))
					line.append(content);
			}
		}
	}

	private static void p(Element element, ArrayList<MDLine> lines) {
		MDLine line = getLastLine(lines);
		if (!line.getContent().trim().equals(""))
			lines.add(new MDLine(MDLineType.None, 0, ""));
		lines.add(new MDLine(MDLineType.None, 0, ""));
		lines.add(new MDLine(MDLineType.None, 0, getTextContent(element)));
		lines.add(new MDLine(MDLineType.None, 0, ""));
		if (!line.getContent().trim().equals(""))
			lines.add(new MDLine(MDLineType.None, 0, ""));
	}

	private static void br(ArrayList<MDLine> lines) {
		MDLine line = getLastLine(lines);
		if (!line.getContent().trim().equals(""))
			lines.add(new MDLine(MDLineType.None, 0, ""));
	}

	private static void h(Element element, ArrayList<MDLine> lines) {
		MDLine line = getLastLine(lines);
		if (!line.getContent().trim().equals(""))
			lines.add(new MDLine(MDLineType.None, 0, ""));

		int level = Integer.valueOf(element.tagName().substring(1));
		switch (level) {
		case 1:
			lines.add(new MDLine(MDLineType.Head1, 0, getTextContent(element)));
			break;
		case 2:
			lines.add(new MDLine(MDLineType.Head2, 0, getTextContent(element)));
			break;
		default:
			lines.add(new MDLine(MDLineType.Head3, 0, getTextContent(element)));
			break;
		}

		lines.add(new MDLine(MDLineType.None, 0, ""));
		lines.add(new MDLine(MDLineType.None, 0, ""));
	}

	private static void strong(Element element, ArrayList<MDLine> lines) {
		MDLine line = getLastLine(lines);
		line.append("**");
		line.append(getTextContent(element));
		line.append("**");
	}

	private static void em(Element element, ArrayList<MDLine> lines) {
		MDLine line = getLastLine(lines);
		line.append("*");
		line.append(getTextContent(element));
		line.append("*");
	}

	private static void hr(ArrayList<MDLine> lines) {
		lines.add(new MDLine(MDLineType.None, 0, ""));
		lines.add(new MDLine(MDLineType.HR, 0, ""));
		lines.add(new MDLine(MDLineType.None, 0, ""));
	}

	private static void a(Element element, ArrayList<MDLine> lines) {
		MDLine line = getLastLine(lines);
		line.append("[");
		line.append(getTextContent(element));
		line.append("]");
		line.append("(");
		String url = element.attr("href");
		line.append(url);
		String title = element.attr("title");
		if (!title.equals("")) {
			line.append(" \"");
			line.append(title);
			line.append("\"");
		}
		line.append(")");
	}

	private static void img(Element element, ArrayList<MDLine> lines) {
		MDLine line = getLastLine(lines);

		line.append("![");
		String alt = element.attr("alt");
		line.append(alt);
		line.append("]");
		line.append("(");
		String url = element.attr("src");
		line.append(url);
		String title = element.attr("title");
		if (!title.equals("")) {
			line.append(" \"");
			line.append(title);
			line.append("\"");
		}
		line.append(")");
	}

	private static void code(Element element, ArrayList<MDLine> lines) {
		lines.add(new MDLine(MDLineType.None, 0, ""));
		MDLine line = new MDLine(MDLineType.None, 0, "    ");
		line.append(getTextContent(element).replace("\n", "    "));
		lines.add(line);
		lines.add(new MDLine(MDLineType.None, 0, ""));
	}

	private static void ul(Element element, ArrayList<MDLine> lines) {
		lines.add(new MDLine(MDLineType.None, 0, ""));
		indentation++;
		orderedList = false;
		MDLine line = new MDLine(MDLineType.None, 0, "");
		line.append(getTextContent(element));
		lines.add(line);
		indentation--;
		lines.add(new MDLine(MDLineType.None, 0, ""));
	}

	private static void ol(Element element, ArrayList<MDLine> lines) {
		lines.add(new MDLine(MDLineType.None, 0, ""));
		indentation++;
		orderedList = true;
		MDLine line = new MDLine(MDLine.MDLineType.None, 0, "");
		line.append(getTextContent(element));
		lines.add(line);
		indentation--;
		lines.add(new MDLine(MDLine.MDLineType.None, 0, ""));
	}

	private static void li(Element element, ArrayList<MDLine> lines) {
		MDLine line;
		if (orderedList) {
			line = new MDLine(MDLine.MDLineType.Ordered, indentation, getTextContent(element));
		} else {
			line = new MDLine(MDLine.MDLineType.Unordered, indentation, getTextContent(element));
		}
		lines.add(line);
	}

	public static class MDLine {
		private int level = 0;
		private MDLineType type;
		private StringBuilder content;

		public MDLine(MDLineType type, int level, String content) {
			this.type = type;
			this.level = level;
			this.content = new StringBuilder(content);
		}

		public MDLine create(String line) {
			int spaces = 0;
			while ((spaces < line.length()) && (line.charAt(spaces) == ' ')) {
				spaces++;
			}
			String content = line.substring(spaces);

			int newLevel = spaces / 4;

			if (content.length() > 0) {
				if (content.matches("^[0-9]+\\.\\s.*")) {
					int c = 0;
					while ((c < content.length()) && (Character.isDigit(content.charAt(c)))) {
						c++;
					}
					return new MDLine(MDLineType.Ordered, newLevel, content.substring(c + 2));
				} else if (content.matches("^(\\*|\\+|\\-)\\s.*")) {
					return new MDLine(MDLineType.Unordered, newLevel, content.substring(2));
				} else if (content.matches("^[#]+.*")) {
					int c = 0;
					while ((c < content.length()) && (content.charAt(c) == '#')) {
						c++;
					}
					MDLineType headerType;
					switch (c) {
					case 1:
						headerType = MDLineType.Head1;
						break;
					case 2:
						headerType = MDLineType.Head2;
						break;
					default:
						headerType = MDLineType.Head3;
						break;
					}

					while ((c < content.length()) && (content.charAt(c) == ' ')) {
						c++;
					}

					return new MDLine(headerType, newLevel, content.substring(c));
				}
			}

			content = line.substring(4 * newLevel);

			return new MDLine(MDLineType.None, newLevel, content);
		}

		public MDLineType getListTypeName() {
			return type;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int i) {
			level = Math.max(i, 0);
		}

		public String toString() {
			StringBuilder newLine = new StringBuilder();
			for (int j = 0; j < getLevel(); j++) {
				newLine.append("    ");
			}

			if (type.equals(MDLineType.Ordered)) {
				newLine.append(String.valueOf(1)).append(". ");
			} else if (type.equals(MDLineType.Unordered)) {
				newLine.append("* ");
			} else if (type.equals(MDLineType.Head1)) {
				newLine.append("# ");
			} else if (type.equals(MDLineType.Head2)) {
				newLine.append("## ");
			} else if (type.equals(MDLineType.Head3)) {
				newLine.append("### ");
			} else if (type.equals(MDLineType.HR)) {
				newLine.append("----");
			}

			newLine.append(getContent());

			return newLine.toString();
		}

		public String getContent() {
			return content.toString();
		}

		public void append(String appendContent) {
			if (content.length() == 0) {
				int i = 0;
				while (i < appendContent.length() && Character.isWhitespace(appendContent.charAt(i))) {
					i++;
				}
				content.append(appendContent.substring(i));
			} else {
				content.append(appendContent);
			}
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof MDLine && ((MDLine) o).type.equals(this.type);
		}

		public boolean isList() {
			return (type.equals(MDLineType.Ordered) || type.equals(MDLineType.Unordered));
		}

		public void setListType(MDLineType type2) {
			type = type2;
		}

		public enum MDLineType {
			Ordered, Unordered, None, Head1, Head2, Head3, HR
		}
	}
}
