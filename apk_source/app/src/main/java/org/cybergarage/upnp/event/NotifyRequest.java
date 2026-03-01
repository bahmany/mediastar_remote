package org.cybergarage.upnp.event;

import org.cybergarage.http.HTTPRequest;
import org.cybergarage.soap.SOAPRequest;
import org.cybergarage.upnp.device.NT;
import org.cybergarage.upnp.device.NTS;
import org.cybergarage.util.Debug;
import org.cybergarage.xml.Node;

/* loaded from: classes.dex */
public class NotifyRequest extends SOAPRequest {
    private static final String PROPERTY = "property";
    private static final String PROPERTYSET = "propertyset";
    private static final String XMLNS = "e";

    public NotifyRequest() {
    }

    public NotifyRequest(HTTPRequest httpReq) {
        set(httpReq);
    }

    public void setNT(String value) {
        setHeader("NT", value);
    }

    public void setNTS(String value) {
        setHeader("NTS", value);
    }

    public void setSID(String id) {
        setHeader("SID", Subscription.toSIDHeaderString(id));
    }

    public String getSID() {
        return Subscription.getSID(getHeaderValue("SID"));
    }

    public void setSEQ(long value) {
        setHeader("SEQ", Long.toString(value));
    }

    public long getSEQ() {
        return getLongHeaderValue("SEQ");
    }

    public boolean setRequest(Subscriber sub, String varName, String value) {
        sub.getDeliveryURL();
        String sid = sub.getSID();
        long notifyCnt = sub.getNotifyCount();
        String host = sub.getDeliveryHost();
        String path = sub.getDeliveryPath();
        int port = sub.getDeliveryPort();
        setMethod("NOTIFY");
        setURI(path);
        setHost(host, port);
        setNT(NT.EVENT);
        setNTS(NTS.PROPCHANGE);
        setSID(sid);
        setSEQ(notifyCnt);
        setContentType("text/xml; charset=\"utf-8\"");
        Node propSetNode = createPropertySetNode(varName, value);
        setContent(propSetNode);
        return true;
    }

    private Node createPropertySetNode(String varName, String value) {
        Node propSetNode = new Node(PROPERTYSET);
        propSetNode.setNameSpace(XMLNS, "urn:schemas-upnp-org:event-1-0");
        Node propNode = new Node(PROPERTY);
        propSetNode.addNode(propNode);
        Node varNameNode = new Node(varName);
        varNameNode.setValue(value);
        propNode.addNode(varNameNode);
        return propSetNode;
    }

    private Node getVariableNode() {
        Node rootNode = getEnvelopeNode();
        if (rootNode == null || !rootNode.hasNodes()) {
            return null;
        }
        Node propNode = rootNode.getNode(0);
        if (propNode.hasNodes()) {
            return propNode.getNode(0);
        }
        return null;
    }

    private Property getProperty(Node varNode) {
        Property prop = new Property();
        if (varNode != null) {
            String variableName = varNode.getName();
            int index = variableName.lastIndexOf(58);
            if (index != -1) {
                variableName = variableName.substring(index + 1);
            }
            prop.setName(variableName);
            prop.setValue(varNode.getValue());
        }
        return prop;
    }

    public PropertyList getPropertyList() {
        PropertyList properties = new PropertyList();
        Node varSetNode = getEnvelopeNode();
        if (varSetNode == null) {
            Debug.message("varSetNode is null.");
        } else {
            for (int i = 0; i < varSetNode.getNNodes(); i++) {
                Node propNode = varSetNode.getNode(i);
                if (propNode != null) {
                    Property prop = getProperty(propNode.getNode(0));
                    properties.add(prop);
                }
            }
        }
        return properties;
    }
}
