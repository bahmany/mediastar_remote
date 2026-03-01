package org.teleal.cling.model.message;

import java.io.UnsupportedEncodingException;
import org.cybergarage.http.HTTP;
import org.teleal.cling.model.message.UpnpOperation;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.UpnpHeader;

/* loaded from: classes.dex */
public abstract class UpnpMessage<O extends UpnpOperation> {
    private Object body;
    private BodyType bodyType;
    private UpnpHeaders headers;
    private O operation;
    private int udaMajorVersion;
    private int udaMinorVersion;

    public enum BodyType {
        STRING,
        BYTES;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static BodyType[] valuesCustom() {
            BodyType[] bodyTypeArrValuesCustom = values();
            int length = bodyTypeArrValuesCustom.length;
            BodyType[] bodyTypeArr = new BodyType[length];
            System.arraycopy(bodyTypeArrValuesCustom, 0, bodyTypeArr, 0, length);
            return bodyTypeArr;
        }
    }

    protected UpnpMessage(UpnpMessage<O> upnpMessage) {
        this.udaMajorVersion = 1;
        this.udaMinorVersion = 0;
        this.headers = new UpnpHeaders();
        this.bodyType = BodyType.STRING;
        this.operation = (O) upnpMessage.getOperation();
        this.headers = upnpMessage.getHeaders();
        this.body = upnpMessage.getBody();
        this.bodyType = upnpMessage.getBodyType();
        this.udaMajorVersion = upnpMessage.getUdaMajorVersion();
        this.udaMinorVersion = upnpMessage.getUdaMinorVersion();
    }

    protected UpnpMessage(O operation) {
        this.udaMajorVersion = 1;
        this.udaMinorVersion = 0;
        this.headers = new UpnpHeaders();
        this.bodyType = BodyType.STRING;
        this.operation = operation;
    }

    protected UpnpMessage(O operation, BodyType bodyType, Object body) {
        this.udaMajorVersion = 1;
        this.udaMinorVersion = 0;
        this.headers = new UpnpHeaders();
        this.bodyType = BodyType.STRING;
        this.operation = operation;
        this.bodyType = bodyType;
        this.body = body;
    }

    public int getUdaMajorVersion() {
        return this.udaMajorVersion;
    }

    public void setUdaMajorVersion(int udaMajorVersion) {
        this.udaMajorVersion = udaMajorVersion;
    }

    public int getUdaMinorVersion() {
        return this.udaMinorVersion;
    }

    public void setUdaMinorVersion(int udaMinorVersion) {
        this.udaMinorVersion = udaMinorVersion;
    }

    public UpnpHeaders getHeaders() {
        return this.headers;
    }

    public void setHeaders(UpnpHeaders headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return this.body;
    }

    public void setBody(BodyType bodyType, Object body) {
        this.bodyType = bodyType;
        this.body = body;
    }

    public void setBodyCharacters(byte[] characterData) throws UnsupportedEncodingException {
        String contentTypeCharset;
        BodyType bodyType = BodyType.STRING;
        if (getContentTypeCharset() != null) {
            contentTypeCharset = getContentTypeCharset();
        } else {
            contentTypeCharset = "UTF-8";
        }
        setBody(bodyType, new String(characterData, contentTypeCharset));
    }

    public boolean hasBody() {
        return getBody() != null;
    }

    public BodyType getBodyType() {
        return this.bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public String getBodyString() {
        try {
            if (!hasBody()) {
                return null;
            }
            if (getBodyType().equals(BodyType.STRING)) {
                return getBody().toString();
            }
            return new String((byte[]) getBody(), "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public byte[] getBodyBytes() {
        try {
            if (!hasBody()) {
                return null;
            }
            if (getBodyType().equals(BodyType.STRING)) {
                return ((String) getBody()).getBytes("UTF-8");
            }
            return (byte[]) getBody();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public O getOperation() {
        return this.operation;
    }

    public boolean isContentTypeMissingOrText() {
        ContentTypeHeader contentTypeHeader = getContentTypeHeader();
        return contentTypeHeader == null || contentTypeHeader.isText();
    }

    public ContentTypeHeader getContentTypeHeader() {
        return (ContentTypeHeader) getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class);
    }

    public boolean isContentTypeText() {
        ContentTypeHeader ct = getContentTypeHeader();
        return ct != null && ct.isText();
    }

    public boolean isContentTypeTextUDA() {
        ContentTypeHeader ct = getContentTypeHeader();
        return ct != null && ct.isUDACompliantXML();
    }

    public String getContentTypeCharset() {
        ContentTypeHeader ct = getContentTypeHeader();
        if (ct != null) {
            return (String) ct.getValue().getParameters().get(HTTP.CHARSET);
        }
        return null;
    }

    public boolean hasHostHeader() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.HOST) != null;
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ") " + getOperation().toString();
    }
}
