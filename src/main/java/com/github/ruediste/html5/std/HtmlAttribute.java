package com.github.ruediste.html5.std;

import com.google.common.base.Strings;

public class HtmlAttribute {

    public String name;
    public String description = "";
    public AttributeType type;

    public HtmlAttribute(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (type != null) {
            sb.append(" :");
            sb.append(type.toString());
        }
        if (!Strings.isNullOrEmpty(description)) {
            sb.append(" -> ");
            sb.append(description);
        }

        return sb.toString();

    }
}
