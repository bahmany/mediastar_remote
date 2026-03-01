package com.voicetechnology.rtspclient.concepts;

/* loaded from: classes.dex */
public interface Response extends Message {
    int getStatusCode();

    String getStatusText();

    void setLine(int i, String str);
}
