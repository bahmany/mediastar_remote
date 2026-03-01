package mktvsmart.screen.update;

import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: classes.dex */
public class ParseXmlService {
    public HashMap<String, String> parseXml(InputStream inStream) throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inStream);
        Element root = document.getDocumentElement();
        NodeList childNodes = root.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeType() == 1) {
                Element childElement = (Element) childNode;
                if ("version_name".equals(childElement.getNodeName())) {
                    hashMap.put("version_name", childElement.getFirstChild().getNodeValue());
                } else if ("version_code".equals(childElement.getNodeName())) {
                    hashMap.put("version_code", childElement.getFirstChild().getNodeValue());
                } else if ("url".equals(childElement.getNodeName())) {
                    hashMap.put("url", childElement.getFirstChild().getNodeValue());
                } else if ("name".equals(childElement.getNodeName())) {
                    hashMap.put("name", childElement.getFirstChild().getNodeValue());
                    System.out.println(childElement.getFirstChild().getNodeValue());
                }
            }
        }
        return hashMap;
    }
}
