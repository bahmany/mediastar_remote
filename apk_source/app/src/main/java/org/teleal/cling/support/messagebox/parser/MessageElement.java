package org.teleal.cling.support.messagebox.parser;

import javax.xml.xpath.XPath;
import org.teleal.common.xml.DOMElement;
import org.w3c.dom.Element;

/* loaded from: classes.dex */
public class MessageElement extends DOMElement<MessageElement, MessageElement> {
    public static final String XPATH_PREFIX = "m";

    public MessageElement(XPath xpath, Element element) {
        super(xpath, element);
    }

    protected String prefix(String localName) {
        return "m:" + localName;
    }

    protected DOMElement<MessageElement, MessageElement>.Builder<MessageElement> createParentBuilder(DOMElement el) {
        return new DOMElement<MessageElement, MessageElement>.Builder<MessageElement>(this, el) { // from class: org.teleal.cling.support.messagebox.parser.MessageElement.1
            /* renamed from: build, reason: merged with bridge method [inline-methods] */
            public MessageElement m15build(Element element) {
                return new MessageElement(MessageElement.this.getXpath(), element);
            }
        };
    }

    protected DOMElement<MessageElement, MessageElement>.ArrayBuilder<MessageElement> createChildBuilder(DOMElement el) {
        return new DOMElement<MessageElement, MessageElement>.ArrayBuilder<MessageElement>(this, el) { // from class: org.teleal.cling.support.messagebox.parser.MessageElement.2
            /* renamed from: newChildrenArray, reason: merged with bridge method [inline-methods] */
            public MessageElement[] m17newChildrenArray(int length) {
                return new MessageElement[length];
            }

            /* renamed from: build, reason: merged with bridge method [inline-methods] */
            public MessageElement m16build(Element element) {
                return new MessageElement(MessageElement.this.getXpath(), element);
            }
        };
    }
}
