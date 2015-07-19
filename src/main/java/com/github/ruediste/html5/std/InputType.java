package com.github.ruediste.html5.std;

/**
 * A value the "type" attribute of the "input" element can take
 */
public class InputType {

    public String name;

    /**
     * Description of the dataType the value can take
     */
    public String dataType;

    /**
     * Short description of the control
     */
    public String controlType;

    public void print() {
        System.out.println(name + "\t" + controlType + "\t" + dataType);
    }
}
