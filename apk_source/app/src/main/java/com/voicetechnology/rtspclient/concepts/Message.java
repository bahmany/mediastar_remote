package com.voicetechnology.rtspclient.concepts;

import com.voicetechnology.rtspclient.MissingHeaderException;
import com.voicetechnology.rtspclient.headers.CSeqHeader;

/* loaded from: classes.dex */
public interface Message {
    public static final String RTSP_TOKEN = "RTSP/";
    public static final String RTSP_VERSION = "1.0";
    public static final String RTSP_VERSION_TOKEN = "RTSP/1.0";

    void addHeader(Header header);

    byte[] getBytes() throws MissingHeaderException;

    CSeqHeader getCSeq();

    EntityMessage getEntityMessage();

    Header getHeader(String str) throws MissingHeaderException;

    Header[] getHeaders();

    String getLine();

    Message setEntityMessage(EntityMessage entityMessage);
}
