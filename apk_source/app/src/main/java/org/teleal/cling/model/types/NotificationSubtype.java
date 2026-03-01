package org.teleal.cling.model.types;

import org.cybergarage.upnp.device.MAN;
import org.cybergarage.upnp.device.NTS;
import org.cybergarage.upnp.device.ST;

/* loaded from: classes.dex */
public enum NotificationSubtype {
    ALIVE(NTS.ALIVE),
    UPDATE("ssdp:update"),
    BYEBYE(NTS.BYEBYE),
    ALL(ST.ALL_DEVICE),
    DISCOVER(MAN.DISCOVER),
    PROPCHANGE(NTS.PROPCHANGE);

    private String headerString;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static NotificationSubtype[] valuesCustom() {
        NotificationSubtype[] notificationSubtypeArrValuesCustom = values();
        int length = notificationSubtypeArrValuesCustom.length;
        NotificationSubtype[] notificationSubtypeArr = new NotificationSubtype[length];
        System.arraycopy(notificationSubtypeArrValuesCustom, 0, notificationSubtypeArr, 0, length);
        return notificationSubtypeArr;
    }

    NotificationSubtype(String headerString) {
        this.headerString = headerString;
    }

    public String getHeaderString() {
        return this.headerString;
    }
}
