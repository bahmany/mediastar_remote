package org.cybergarage.upnp.ssdp;

import java.io.InputStream;
import org.cybergarage.http.HTTP;
import org.cybergarage.http.HTTPResponse;

/* loaded from: classes.dex */
public class SSDPResponse extends HTTPResponse {
    public SSDPResponse() {
        setVersion("1.1");
    }

    public SSDPResponse(InputStream in) {
        super(in);
    }

    public void setST(String value) {
        setHeader("ST", value);
    }

    public String getST() {
        return getHeaderValue("ST");
    }

    public void setLocation(String value) {
        setHeader(HTTP.LOCATION, value);
    }

    public String getLocation() {
        return getHeaderValue(HTTP.LOCATION);
    }

    public void setUSN(String value) {
        setHeader("USN", value);
    }

    public String getUSN() {
        return getHeaderValue("USN");
    }

    public void setMYNAME(String value) {
        setHeader(HTTP.MYNAME, value);
    }

    public String getMYNAME() {
        return getHeaderValue(HTTP.MYNAME);
    }

    public void setLeaseTime(int len) {
        setHeader(HTTP.CACHE_CONTROL, "max-age=" + Integer.toString(len));
    }

    public int getLeaseTime() {
        String cacheCtrl = getHeaderValue(HTTP.CACHE_CONTROL);
        return SSDP.getLeaseTime(cacheCtrl);
    }

    @Override // org.cybergarage.http.HTTPResponse
    public String getHeader() {
        StringBuffer str = new StringBuffer();
        str.append(getStatusLineString());
        str.append(getHeaderString());
        str.append("\r\n");
        return str.toString();
    }
}
