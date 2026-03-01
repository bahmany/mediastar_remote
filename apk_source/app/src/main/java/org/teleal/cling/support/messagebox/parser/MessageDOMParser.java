package org.teleal.cling.support.messagebox.parser;

import javax.xml.xpath.XPath;
import org.teleal.common.xml.DOMParser;
import org.teleal.common.xml.NamespaceContextMap;
import org.w3c.dom.Document;

/* loaded from: classes.dex */
public class MessageDOMParser extends DOMParser<MessageDOM> {
    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: createDOM, reason: merged with bridge method [inline-methods] */
    public MessageDOM m14createDOM(Document document) {
        return new MessageDOM(document);
    }

    public NamespaceContextMap createDefaultNamespaceContext(String... optionalPrefixes) {
        NamespaceContextMap ctx = new NamespaceContextMap() { // from class: org.teleal.cling.support.messagebox.parser.MessageDOMParser.1
            protected String getDefaultNamespaceURI() {
                return MessageDOM.NAMESPACE_URI;
            }
        };
        for (String optionalPrefix : optionalPrefixes) {
            ctx.put(optionalPrefix, MessageDOM.NAMESPACE_URI);
        }
        return ctx;
    }

    public XPath createXPath() {
        return super.createXPath(createDefaultNamespaceContext(MessageElement.XPATH_PREFIX));
    }
}
