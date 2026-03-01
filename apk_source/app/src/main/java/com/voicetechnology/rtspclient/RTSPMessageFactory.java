package com.voicetechnology.rtspclient;

import com.voicetechnology.rtspclient.concepts.Content;
import com.voicetechnology.rtspclient.concepts.Header;
import com.voicetechnology.rtspclient.concepts.Message;
import com.voicetechnology.rtspclient.concepts.MessageBuffer;
import com.voicetechnology.rtspclient.concepts.MessageFactory;
import com.voicetechnology.rtspclient.concepts.Request;
import com.voicetechnology.rtspclient.concepts.Response;
import com.voicetechnology.rtspclient.headers.CSeqHeader;
import com.voicetechnology.rtspclient.headers.ContentEncodingHeader;
import com.voicetechnology.rtspclient.headers.ContentLengthHeader;
import com.voicetechnology.rtspclient.headers.ContentTypeHeader;
import com.voicetechnology.rtspclient.headers.SessionHeader;
import com.voicetechnology.rtspclient.messages.RTSPDescribeRequest;
import com.voicetechnology.rtspclient.messages.RTSPOptionsRequest;
import com.voicetechnology.rtspclient.messages.RTSPPlayRequest;
import com.voicetechnology.rtspclient.messages.RTSPSetupRequest;
import com.voicetechnology.rtspclient.messages.RTSPTeardownRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class RTSPMessageFactory implements MessageFactory {
    private static Map<String, Constructor<? extends Header>> headerMap = new HashMap();
    private static Map<Request.Method, Class<? extends Request>> requestMap = new HashMap();

    static {
        try {
            putHeader(CSeqHeader.class);
            putHeader(ContentEncodingHeader.class);
            putHeader(ContentLengthHeader.class);
            putHeader(ContentTypeHeader.class);
            putHeader(SessionHeader.class);
            requestMap.put(Request.Method.OPTIONS, RTSPOptionsRequest.class);
            requestMap.put(Request.Method.SETUP, RTSPSetupRequest.class);
            requestMap.put(Request.Method.TEARDOWN, RTSPTeardownRequest.class);
            requestMap.put(Request.Method.DESCRIBE, RTSPDescribeRequest.class);
            requestMap.put(Request.Method.PLAY, RTSPPlayRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void putHeader(Class<? extends Header> cls) throws Exception {
        headerMap.put(cls.getDeclaredField("NAME").get(null).toString().toLowerCase(), cls.getConstructor(String.class));
    }

    @Override // com.voicetechnology.rtspclient.concepts.MessageFactory
    public void incomingMessage(MessageBuffer buffer) throws IOException {
        Message message;
        int length;
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.getData(), buffer.getOffset(), buffer.getLength());
        int initial = in.available();
        try {
            try {
                String line = readLine(in);
                if (line.startsWith(Message.RTSP_TOKEN)) {
                    Message message2 = new RTSPResponse(line);
                    message = message2;
                } else {
                    Request.Method method = Request.Method.valueOf(line.substring(0, line.indexOf(32)));
                    Class<? extends Request> cls = requestMap.get(method);
                    if (cls != null) {
                        message = cls.getConstructor(String.class).newInstance(line);
                    } else {
                        Message message3 = new RTSPRequest(line);
                        message = message3;
                    }
                }
                while (true) {
                    String line2 = readLine(in);
                    if (in == null) {
                        throw new IncompleteMessageException();
                    }
                    if (line2.length() != 0) {
                        Constructor<? extends Header> c = headerMap.get(line2.substring(0, line2.indexOf(58)).toLowerCase());
                        if (c != null) {
                            message.addHeader(c.newInstance(line2));
                        } else {
                            message.addHeader(new Header(line2));
                        }
                    } else {
                        buffer.setMessage(message);
                        try {
                            length = ((ContentLengthHeader) message.getHeader("Content-Length")).getValue();
                        } catch (MissingHeaderException e) {
                        }
                        if (in.available() < length) {
                            throw new IncompleteMessageException();
                        }
                        Content content = new Content();
                        content.setDescription(message);
                        byte[] data = new byte[length];
                        in.read(data);
                        content.setBytes(data);
                        message.setEntityMessage(new RTSPEntityMessage(message, content));
                        try {
                            return;
                        } catch (IOException e2) {
                            return;
                        }
                    }
                }
            } catch (Exception e3) {
                throw new InvalidMessageException(e3);
            }
        } finally {
            buffer.setused(initial - in.available());
            try {
                in.close();
            } catch (IOException e4) {
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.MessageFactory
    public Request outgoingRequest(String uri, Request.Method method, int cseq, Header... extras) throws URISyntaxException {
        Class<? extends Request> cls = requestMap.get(method);
        try {
            Request message = cls != null ? cls.newInstance() : new RTSPRequest();
            message.setLine(uri, method);
            fillMessage(message, cseq, extras);
            return message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.MessageFactory
    public Request outgoingRequest(Content body, String uri, Request.Method method, int cseq, Header... extras) throws URISyntaxException {
        Message message = outgoingRequest(uri, method, cseq, extras);
        return (Request) message.setEntityMessage(new RTSPEntityMessage(message, body));
    }

    @Override // com.voicetechnology.rtspclient.concepts.MessageFactory
    public Response outgoingResponse(int code, String text, int cseq, Header... extras) {
        RTSPResponse message = new RTSPResponse();
        message.setLine(code, text);
        fillMessage(message, cseq, extras);
        return message;
    }

    @Override // com.voicetechnology.rtspclient.concepts.MessageFactory
    public Response outgoingResponse(Content body, int code, String text, int cseq, Header... extras) {
        Message message = outgoingResponse(code, text, cseq, extras);
        return (Response) message.setEntityMessage(new RTSPEntityMessage(message, body));
    }

    private void fillMessage(Message message, int cseq, Header[] extras) {
        message.addHeader(new CSeqHeader(cseq));
        for (Header h : extras) {
            message.addHeader(h);
        }
    }

    private String readLine(InputStream in) throws IOException {
        StringBuilder b = new StringBuilder();
        int ch = in.read();
        while (ch != -1 && ch != 13 && ch != 10) {
            b.append((char) ch);
            ch = in.read();
        }
        if (ch == -1) {
            return null;
        }
        in.read();
        return b.toString();
    }
}
