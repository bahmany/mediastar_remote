package com.google.android.gms.internal;

import java.util.HashMap;
import java.util.Map;
import org.cybergarage.upnp.Icon;

/* loaded from: classes.dex */
public class hp {
    private static final String[] Cm = {"text1", "text2", Icon.ELEM_NAME, "intent_action", "intent_data", "intent_data_id", "intent_extra_data", "suggest_large_icon", "intent_activity"};
    private static final Map<String, Integer> Cn = new HashMap(Cm.length);

    static {
        for (int i = 0; i < Cm.length; i++) {
            Cn.put(Cm[i], Integer.valueOf(i));
        }
    }

    public static String O(int i) {
        if (i < 0 || i >= Cm.length) {
            return null;
        }
        return Cm[i];
    }

    public static int as(String str) {
        Integer num = Cn.get(str);
        if (num == null) {
            throw new IllegalArgumentException("[" + str + "] is not a valid global search section name");
        }
        return num.intValue();
    }

    public static int fm() {
        return Cm.length;
    }
}
