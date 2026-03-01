package com.hisilicon.multiscreen.protocol.utils;

import android.util.Base64;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.hisilicon.multiscreen.protocol.message.Action;
import com.hisilicon.multiscreen.protocol.message.Argument;
import com.hisilicon.multiscreen.protocol.message.ArgumentValue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.cybergarage.upnp.ArgumentList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/* loaded from: classes.dex */
public class SaxXmlUtil {
    public static final String ENCODE_UTF_8 = "UTF-8";

    public Action parse(InputStream inStream) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        if (inStream == null) {
            return null;
        }
        XMLHandler xmlHandler = new XMLHandler();
        saxParser.parse(inStream, xmlHandler);
        return xmlHandler.getAction();
    }

    public Action parse(byte[] msgByte) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        if (msgByte == null) {
            LogTool.e("msgByte is null");
            return null;
        }
        ByteArrayInputStream vimeMsgInstream = new ByteArrayInputStream(msgByte);
        XMLHandler xmlHandler = new XMLHandler();
        saxParser.parse(vimeMsgInstream, xmlHandler);
        return xmlHandler.getAction();
    }

    public String serialize(Action action) throws TransformerConfigurationException, SAXException, IllegalArgumentException {
        String tmpValue;
        if (action == null) {
            LogTool.e("action is null");
            return null;
        }
        SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler handler = factory.newTransformerHandler();
        Transformer transformer = handler.getTransformer();
        transformer.setOutputProperty("encoding", "UTF-8");
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("omit-xml-declaration", "no");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        handler.setResult(result);
        handler.startDocument();
        AttributesImpl attrs = new AttributesImpl();
        attrs.clear();
        attrs.addAttribute("", "", "id", "String", "0x" + Integer.toHexString(action.getId()));
        handler.startElement("", "", org.cybergarage.upnp.Action.ELEM_NAME, attrs);
        handler.startElement("", "", "name", null);
        char[] ch = String.valueOf(action.getName()).toCharArray();
        handler.characters(ch, 0, ch.length);
        handler.endElement("", "", "name");
        attrs.clear();
        if (action.getArgumentList().size() > 0) {
            attrs.addAttribute("", "", "number", "String", String.valueOf(action.getArgumentList().size()));
            handler.startElement("", "", ArgumentList.ELEM_NAME, attrs);
            attrs.clear();
            for (int i = 0; i < action.getArgumentList().size(); i++) {
                Argument argument = action.getArgument(i);
                attrs.addAttribute("", "", "number", "String", String.valueOf(argument.getArgumentValueList().size()));
                handler.startElement("", "", org.cybergarage.upnp.Argument.ELEM_NAME, attrs);
                attrs.clear();
                for (int j = 0; j < argument.getArgumentValueList().size(); j++) {
                    attrs.addAttribute("", "", "name", "String", String.valueOf(argument.getArgumentValue(j).getKey()));
                    ArgumentValue value = argument.getArgumentValue(j);
                    boolean isByteArray = false;
                    if (value.getType().equals("byte[]")) {
                        isByteArray = true;
                    }
                    attrs.addAttribute("", "", PlaylistSQLiteHelper.COL_TYPE, "String", value.getType());
                    handler.startElement("", "", "value", attrs);
                    if (value.getVaule() != null) {
                        if (isByteArray) {
                            byte[] data = (byte[]) value.getVaule();
                            tmpValue = Base64.encodeToString(data, 0);
                        } else {
                            tmpValue = value.getVaule().toString();
                        }
                        char[] ch2 = tmpValue.toCharArray();
                        handler.characters(ch2, 0, ch2.length);
                    }
                    handler.endElement("", "", "value");
                }
                handler.endElement("", "", org.cybergarage.upnp.Argument.ELEM_NAME);
            }
            handler.endElement("", "", ArgumentList.ELEM_NAME);
        }
        attrs.clear();
        attrs.addAttribute("", "", "responseFlag", "String", String.valueOf(action.getResponseFlag()));
        attrs.addAttribute("", "", "responseId", "String", "0x" + Integer.toHexString(action.getResponseId()));
        handler.startElement("", "", "response", attrs);
        handler.endElement("", "", "");
        handler.endElement("", "", org.cybergarage.upnp.Action.ELEM_NAME);
        handler.endDocument();
        return writer.toString();
    }
}
