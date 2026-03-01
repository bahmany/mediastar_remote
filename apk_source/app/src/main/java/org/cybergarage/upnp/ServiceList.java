package org.cybergarage.upnp;

import java.util.Vector;

/* loaded from: classes.dex */
public class ServiceList extends Vector {
    public static final String ELEM_NAME = "serviceList";

    public Service getService(int n) {
        Object obj = null;
        try {
            obj = get(n);
        } catch (Exception e) {
        }
        return (Service) obj;
    }
}
