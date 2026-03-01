package com.voicetechnology.rtspclient;

/* loaded from: classes.dex */
public class HeaderMismatchException extends RuntimeException {
    private static final long serialVersionUID = 6316852391642646327L;

    public HeaderMismatchException(String expected, String current) {
        super("expected " + expected + " but got " + current);
    }
}
