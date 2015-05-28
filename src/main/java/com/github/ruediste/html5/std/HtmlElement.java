package com.github.ruediste.html5.std;

import java.util.ArrayList;

public class HtmlElement {
    public String tag;
    public ArrayList<HtmlAttribute> attributes = new ArrayList<>();
    public boolean supportsGlobalAttributes;
    public String description = "";
    public boolean endTagOmissed;

    void print() {
        System.out.println("Element: " + tag);
        if (supportsGlobalAttributes)
            System.out.println("  supports global attributes");
        for (HtmlAttribute a : attributes) {
            System.out.println("  " + a);
        }
    }
}
