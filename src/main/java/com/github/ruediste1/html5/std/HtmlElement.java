package com.github.ruediste1.html5.std;

import java.util.ArrayList;

public class HtmlElement {
	public String tag;
	public ArrayList<HtmlAttribute> attributes = new ArrayList<>();
	public boolean supportsGlobalAttributes;

	void print() {
		System.out.println("Element: " + tag);
		if (supportsGlobalAttributes)
			System.out.println("  global attributes");
		for (HtmlAttribute a : attributes) {
			System.out.println("  " + a);
		}
	}
}
