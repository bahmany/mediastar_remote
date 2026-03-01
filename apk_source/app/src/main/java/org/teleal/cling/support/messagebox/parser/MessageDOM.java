package org.teleal.cling.support.messagebox.parser;

import javax.xml.xpath.XPath;
import org.teleal.common.xml.DOM;
import org.w3c.dom.Document;

/* loaded from: classes.dex */
public class MessageDOM extends DOM {
    public static final String NAMESPACE_URI = "urn:samsung-com:messagebox-1-0";

    public MessageDOM(Document dom) {
        super(dom);
    }

    public String getRootElementNamespace() {
        return NAMESPACE_URI;
    }

    /* renamed from: getRoot, reason: merged with bridge method [inline-methods] */
    public MessageElement m13getRoot(XPath xPath) {
        return new MessageElement(xPath, getW3CDocument().getDocumentElement());
    }

    /* renamed from: copy, reason: merged with bridge method [inline-methods] */
    public MessageDOM m12copy() {
        return new MessageDOM((Document) getW3CDocument().cloneNode(true));
    }

    public MessageElement createRoot(XPath xpath, String element) {
        super.createRoot(element);
        return m13getRoot(xpath);
    }
}
