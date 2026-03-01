package com.google.android.gms.analytics.ecommerce;

import com.google.android.gms.common.internal.n;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class Promotion {
    public static final String ACTION_CLICK = "click";
    public static final String ACTION_VIEW = "view";
    Map<String, String> BK = new HashMap();

    public Map<String, String> aq(String str) {
        HashMap map = new HashMap();
        for (Map.Entry<String, String> entry : this.BK.entrySet()) {
            map.put(str + entry.getKey(), entry.getValue());
        }
        return map;
    }

    void put(String name, String value) {
        n.b(name, (Object) "Name should be non-null");
        this.BK.put(name, value);
    }

    public Promotion setCreative(String value) {
        put("cr", value);
        return this;
    }

    public Promotion setId(String value) {
        put("id", value);
        return this;
    }

    public Promotion setName(String value) {
        put("nm", value);
        return this;
    }

    public Promotion setPosition(String value) {
        put("ps", value);
        return this;
    }
}
