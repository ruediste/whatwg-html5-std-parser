package com.github.ruediste1.html5.std.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.github.ruediste1.html5.std.HtmlAttribute;
import com.github.ruediste1.html5.std.HtmlElement;
import com.github.ruediste1.html5.std.HtmlStandard;

public class Parser {

	public static void main(String[] args) {
		new Parser().parse().print();
	}

	/**
	 * Parse from a web location
	 */
	public HtmlStandard parse() {
		HtmlStandard result = null;

		try {
			result = parse(Jsoup
					.connect(
							"https://raw.githubusercontent.com/whatwg/html-mirror/master/complete.html")
					.get());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	/**
	 * Parse the specification provided as input stream
	 */
	public HtmlStandard parse(InputStream in) {
		try {
			return parse(Jsoup.parse(in, "UTF-8", ""));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parse from a zip file
	 */
	public HtmlStandard parse(ZipInputStream zin, String entryName)
			throws IOException {
		ZipEntry entry;
		while ((entry = zin.getNextEntry()) != null) {
			if (entryName.equals(entry.getName())) {
				return parse(Jsoup.parse(zin, "UTF-8", ""));
			}
		}
		return null;
	}

	/**
	 * Parse a {@link Jsoup} parsed document
	 */
	public HtmlStandard parse(Document dom) throws IOException {
		HtmlStandard result = new HtmlStandard();

		for (Element listElement : dom.getElementsByClass("element")) {
			// search backwards for header element do determine tag
			String tag = null;
			siblingLoop: for (int idx = listElement.elementSiblingIndex() - 1; idx >= 0; idx--) {
				Element header = listElement.parent().child(idx);
				if (!header.tagName().startsWith("h"))
					continue;
				for (Element dfn : header.getElementsByTag("dfn")) {
					for (Element code : dfn.getElementsByTag("code")) {
						tag = code.text();
						break siblingLoop;
					}
				}

			}

			// abort if tag has not been found
			if (tag == null)
				continue;

			// create HtmlElement and add to result
			HtmlElement element = new HtmlElement();
			element.tag = tag;
			result.elements.add(element);

			// search and add attributes
			for (Element attributesHeader : listElement
					.getElementsContainingText("Content attributes:")) {
				for (int idx = attributesHeader.elementSiblingIndex() + 1; idx < attributesHeader
						.parent().children().size(); idx++) {
					Element dd = attributesHeader.parent().child(idx);
					if (!"dd".equals(dd.tagName())) {
						break;
					}

					if (dd.text().equalsIgnoreCase("Global Attributes")) {
						element.supportsGlobalAttributes = true;
					} else {
						String[] parts = dd.text().split("—");
						if (parts.length == 2)
							element.attributes.add(new HtmlAttribute(parts[0],
									parts[1]));
						else {
							if (!dd.text().contains(" "))
								element.attributes.add(new HtmlAttribute(
										parts[0], ""));
						}
					}
				}
			}
		}

		// read global attributes
		{
			Element header = dom.getElementById("global-attributes");
			int count = 0;
			for (int idx = header.elementSiblingIndex() + 1; count < 2; idx++) {
				Element list = header.parent().child(idx);
				if (!list.classNames().contains("brief")) {
					continue;
				}
				count++;
				for (Element item : list.children()) {
					result.globalAttributes.add(new HtmlAttribute(item.text(),
							""));
				}
			}
		}

		return result;
	}

}
