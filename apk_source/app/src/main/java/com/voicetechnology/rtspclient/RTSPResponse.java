package com.voicetechnology.rtspclient;

import com.voicetechnology.rtspclient.concepts.Response;

/* loaded from: classes.dex */
public class RTSPResponse extends RTSPMessage implements Response {
    private int status;
    private String text;

    public RTSPResponse() {
    }

    public RTSPResponse(String line) {
        setLine(line);
        String line2 = line.substring(line.indexOf(32) + 1);
        this.status = Integer.parseInt(line2.substring(0, line2.indexOf(32)));
        this.text = line2.substring(line2.indexOf(32) + 1);
    }

    @Override // com.voicetechnology.rtspclient.concepts.Response
    public int getStatusCode() {
        return this.status;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Response
    public String getStatusText() {
        return this.text;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Response
    public void setLine(int statusCode, String statusText) {
        this.status = statusCode;
        this.text = statusText;
        super.setLine("RTSP/1.0 " + this.status + ' ' + this.text);
    }
}
