package com.voicetechnology.rtspclient.concepts;

import com.voicetechnology.rtspclient.MissingHeaderException;

/* loaded from: classes.dex */
public interface EntityMessage {
    byte[] getBytes() throws MissingHeaderException;

    Content getContent();

    Message getMessage();

    boolean isEntity();

    void setContent(Content content);
}
