package org.cybergarage.upnp.event;

import java.util.Vector;

/* loaded from: classes.dex */
public class PropertyList extends Vector {
    public static final String ELEM_NAME = "PropertyList";

    public Property getProperty(int n) {
        return (Property) get(n);
    }
}
