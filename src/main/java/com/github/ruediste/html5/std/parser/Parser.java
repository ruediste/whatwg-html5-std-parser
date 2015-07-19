package com.github.ruediste.html5.std.parser;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.ruediste.html5.std.HtmlAttribute;
import com.github.ruediste.html5.std.HtmlElement;
import com.github.ruediste.html5.std.HtmlStandard;
import com.github.ruediste.html5.std.InputType;

public class Parser {
    public static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0";
    public static String[] DEFAULT_HEADER = { "Accept-Encoding",
            "gzip, deflated" };
    public static String LOCATION = "https://html.spec.whatwg.org/";
    public static int TIMEOUT_DURATION = 60000;
    public static int UNBOUNDED_SIZE = 0;

    public static void main(String[] args) {
        Parser parser = new Parser();
        HtmlStandard std = parser.parse();
        std.print();
    }

    /**
     * Parse from a web location
     */
    public HtmlStandard parse() {
        try {
            Connection connection = connectTo(LOCATION);
            Document doc = connection.get();
            return parse(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection connectTo(String URL) {
        Connection connection = Jsoup.connect(URL);
        connection.maxBodySize(UNBOUNDED_SIZE);
        connection.timeout(TIMEOUT_DURATION);
        connection.header(DEFAULT_HEADER[0], DEFAULT_HEADER[1]);
        connection.userAgent(DEFAULT_USER_AGENT);
        return connection;
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
     * Parse a {@link Jsoup} parsed document
     */
    public HtmlStandard parse(Document dom) throws IOException {
        HtmlStandard result = new HtmlStandard();

        for (Element listElement : dom.body().select("dl.element")) {
            // search backwards for header element do determine tag
            String tag = null;
            siblingLoop: for (int idx = listElement.elementSiblingIndex() - 1; idx >= 0; idx--) {
                Element header = listElement.parent().child(idx);
                if (!header.tagName().startsWith("h"))
                    continue siblingLoop;
                for (Element code : header.select("dfn code")) {
                    tag = code.text();
                    break siblingLoop;
                }

            }

            // abort if tag has not been found
            if (tag == null) {
                System.out.println("Skipping element");
                continue;
            }

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
                        // add attribute
                        String[] parts = dd.text().split("—");
                        String name = parts[0];
                        if (name.contains(":"))
                            name = name.substring(name.indexOf(':') + 1);
                        name = name.trim();

                        if (parts.length == 2)
                            element.attributes.add(new HtmlAttribute(name,
                                    parts[1]));
                        else {
                            if (!dd.text().contains(" "))
                                element.attributes.add(new HtmlAttribute(name,
                                        ""));
                        }
                    }
                }
            }

            // add description
            {
                Element desc = listElement.parent().child(
                        listElement.elementSiblingIndex() + 1);
                element.description = desc.text();
            }

            // check if there is an end tag omission
            {
                Element omissionDt = dom.getElementById("the-" + element.tag
                        + "-element:concept-element-tag-omission");
                if (omissionDt != null
                        && omissionDt.parent().nextElementSibling() != null) {
                    String omissionText = omissionDt.parent()
                            .nextElementSibling().text();
                    if (omissionText.contains("No end tag")) {
                        element.endTagOmissed = true;
                    }
                }
            }
        }

        parseGlobalAttributes(dom, result);

        // read input types
        {
            Element table = dom.getElementById("attr-input-type-keywords");
            Elements rows = table.getElementsByTag("tbody").get(0)
                    .getElementsByTag("tr");
            for (Element row : rows) {
                InputType type = new InputType();
                type.name = row.child(0).text();
                type.dataType = row.child(2).text();
                type.controlType = row.child(3).text();
                result.inputTypes.add(type);
            }
        }

        return result;
    }

    private void parseGlobalAttributes(Document dom, HtmlStandard result) {
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
                    String name = item.text();
                    // suppress star for onXXX attributes
                    if (name.endsWith("*"))
                        name = name.substring(0, name.length() - 1);

                    // read the description
                    String desc = "";
                    {
                        Element descHeader = dom.getElementById("the-" + name
                                + "-attribute");
                        if (descHeader != null) {
                            int descIdx = descHeader.elementSiblingIndex() + 1;
                            Element next = descHeader.parent().child(descIdx);
                            if (next.classNames().contains("status"))
                                next = descHeader.parent().child(descIdx + 1);

                            desc = next.text();
                        }
                    }
                    result.globalAttributes.add(new HtmlAttribute(name.trim(),
                            desc));
                }
            }
        }

    }

}