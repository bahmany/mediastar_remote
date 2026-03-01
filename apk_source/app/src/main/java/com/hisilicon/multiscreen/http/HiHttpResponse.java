package com.hisilicon.multiscreen.http;

/* loaded from: classes.dex */
public class HiHttpResponse {
    private byte[] mMessage;
    private String mResponseMessage;
    private int mStatusCode;

    public HiHttpResponse() {
        this.mStatusCode = 0;
        this.mResponseMessage = null;
        this.mMessage = new byte[0];
    }

    public HiHttpResponse(int statusCode, String body) {
        this.mStatusCode = 0;
        this.mResponseMessage = null;
        this.mMessage = new byte[0];
        setStatusCode(statusCode);
        setResponseMessage(body);
    }

    public HiHttpResponse(int statusCode, byte[] body) {
        this.mStatusCode = 0;
        this.mResponseMessage = null;
        this.mMessage = new byte[0];
        setStatusCode(statusCode);
        setMessage(body);
    }

    public void setStatusCode(int statusCode) {
        this.mStatusCode = statusCode;
    }

    public int getStatusCode() {
        return this.mStatusCode;
    }

    public void setResponseMessage(String responseMessage) {
        this.mResponseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return this.mResponseMessage;
    }

    public void setMessage(byte[] responseMessage) {
        this.mMessage = responseMessage;
    }

    public byte[] getMessage() {
        return this.mMessage;
    }
}
