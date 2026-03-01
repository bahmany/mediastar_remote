package org.teleal.cling.support.model;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/* loaded from: classes.dex */
public class PersonWithRole extends Person {
    private String role;

    public PersonWithRole(String name) {
        super(name);
    }

    public PersonWithRole(String name, String role) {
        super(name);
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }

    public void setOnElement(Element element) throws DOMException {
        element.setTextContent(toString());
        String r = getRole() != null ? getRole() : "";
        element.setAttribute("role", r);
    }
}
