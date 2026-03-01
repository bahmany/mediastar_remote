package org.cybergarage.upnp.device;

/* loaded from: classes.dex */
public class USN {
    public static final String ROOTDEVICE = "upnp:rootdevice";

    public static final boolean isRootDevice(String usnValue) {
        if (usnValue == null) {
            return false;
        }
        return usnValue.endsWith("upnp:rootdevice");
    }

    public static final boolean isHiMultiScreenDevice(String ntValue) {
        if (ntValue == null) {
            return false;
        }
        return ntValue.endsWith("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1");
    }

    public static final String getUDN(String usnValue) {
        if (usnValue == null) {
            return "";
        }
        int idx = usnValue.indexOf("::");
        if (idx < 0) {
            return usnValue.trim();
        }
        String udnValue = new String(usnValue.getBytes(), 0, idx);
        return udnValue.trim();
    }
}
