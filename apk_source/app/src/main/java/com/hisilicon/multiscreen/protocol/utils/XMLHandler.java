package com.hisilicon.multiscreen.protocol.utils;

import android.util.Base64;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.hisilicon.multiscreen.protocol.message.Action;
import com.hisilicon.multiscreen.protocol.message.Argument;
import com.hisilicon.multiscreen.protocol.message.ArgumentValue;
import java.util.Date;
import org.cybergarage.http.HTTP;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* compiled from: SaxXmlUtil.java */
/* loaded from: classes.dex */
class XMLHandler extends DefaultHandler {
    private Action action;
    Argument argument = null;
    ArgumentValue value = null;
    String myString = null;

    XMLHandler() {
    }

    public Action getAction() {
        return this.action;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        try {
            super.startDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        this.myString = null;
        if (org.cybergarage.upnp.Action.ELEM_NAME.equals(localName)) {
            this.action = new Action();
            this.action.setId(Integer.decode(attributes.getValue("id")).intValue());
            return;
        }
        if ("response".equals(localName)) {
            this.action.setResponseFlag(attributes.getValue("responseFlag"));
            this.action.setResponseId(Integer.decode(attributes.getValue("responseId")).intValue());
        } else if ("value".equals(localName)) {
            this.value = new ArgumentValue();
            this.value.setKey(attributes.getValue("name"));
            this.value.setType(attributes.getValue(PlaylistSQLiteHelper.COL_TYPE));
        } else if (org.cybergarage.upnp.Argument.ELEM_NAME.equals(localName)) {
            this.argument = new Argument();
            this.argument.setArgumentValueNum(Integer.decode(attributes.getValue("number")).intValue());
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (this.myString == null) {
            this.myString = String.valueOf(ch, start, length);
        } else {
            this.myString = String.valueOf(this.myString) + String.valueOf(ch, start, length);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String qName) throws SAXException, NumberFormatException {
        super.endElement(uri, localName, qName);
        if ("name".equals(localName)) {
            this.action.setName(this.myString);
        } else if (localName.equals("value")) {
            if (this.value.getType().equals("Integer")) {
                Integer mInteger = Integer.valueOf(this.myString);
                this.value.setValue(mInteger);
            } else if (this.value.getType().equals("Short")) {
                Short mShort = new Short(this.myString);
                this.value.setValue(mShort);
            } else if (this.value.getType().equals("Byte")) {
                Byte mByte = new Byte(this.myString);
                this.value.setValue(mByte);
            } else if (this.value.getType().equals("Long")) {
                Long mLong = Long.valueOf(this.myString);
                this.value.setValue(mLong);
            } else if (this.value.getType().equals("Float")) {
                Float mFloat = Float.valueOf(this.myString);
                this.value.setValue(mFloat);
            } else if (this.value.getType().equals("Double")) {
                Double mDouble = Double.valueOf(this.myString);
                this.value.setValue(mDouble);
            } else if (this.value.getType().equals("Boolean")) {
                Boolean mBoolean = Boolean.valueOf(this.myString);
                this.value.setValue(mBoolean);
            } else if (this.value.getType().equals(HTTP.DATE)) {
                Date mDate = new Date(this.myString);
                this.value.setValue(mDate);
            } else if (this.value.getType().equals("String")) {
                this.myString = SpecialCharUtil.getSpecialChar(this.myString);
                this.value.setValue(this.myString);
            } else if (this.value.getType().equals("byte[]")) {
                byte[] data = Base64.decode(this.myString, 0);
                this.value.setValue(data);
            } else {
                LogTool.e("vaule is other type " + this.value.getType());
            }
            this.argument.addArgumentValue(this.value);
        } else if (localName.equals(org.cybergarage.upnp.Argument.ELEM_NAME)) {
            this.action.addArgument(this.argument);
        }
        this.myString = null;
    }
}
