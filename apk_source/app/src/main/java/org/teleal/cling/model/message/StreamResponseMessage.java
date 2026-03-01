package org.teleal.cling.model.message;

import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.common.util.MimeType;

/* loaded from: classes.dex */
public class StreamResponseMessage extends UpnpMessage<UpnpResponse> {
    public StreamResponseMessage(StreamResponseMessage source) {
        super(source);
    }

    public StreamResponseMessage(UpnpResponse.Status status) {
        super(new UpnpResponse(status));
    }

    public StreamResponseMessage(UpnpResponse operation) {
        super(operation);
    }

    public StreamResponseMessage(UpnpResponse operation, String body) {
        super(operation, UpnpMessage.BodyType.STRING, body);
    }

    public StreamResponseMessage(String body) {
        super(new UpnpResponse(UpnpResponse.Status.OK), UpnpMessage.BodyType.STRING, body);
    }

    public StreamResponseMessage(UpnpResponse operation, byte[] body) {
        super(operation, UpnpMessage.BodyType.BYTES, body);
    }

    public StreamResponseMessage(byte[] body) {
        super(new UpnpResponse(UpnpResponse.Status.OK), UpnpMessage.BodyType.BYTES, body);
    }

    public StreamResponseMessage(String body, ContentTypeHeader contentType) {
        this(body);
        getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, contentType);
    }

    public StreamResponseMessage(String body, MimeType mimeType) {
        this(body, new ContentTypeHeader(mimeType));
    }

    public StreamResponseMessage(byte[] body, ContentTypeHeader contentType) {
        this(body);
        getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, contentType);
    }

    public StreamResponseMessage(byte[] body, MimeType mimeType) {
        this(body, new ContentTypeHeader(mimeType));
    }
}
