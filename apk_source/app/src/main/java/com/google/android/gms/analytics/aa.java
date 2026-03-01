package com.google.android.gms.analytics;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
class aa {
    private final Map<String, Integer> AU = new HashMap();
    private final Map<String, String> AV = new HashMap();
    private final boolean AW;
    private final String AX;

    aa(String str, boolean z) {
        this.AW = z;
        this.AX = str;
    }

    void e(String str, int i) {
        if (this.AW) {
            Integer num = this.AU.get(str);
            if (num == null) {
                num = 0;
            }
            this.AU.put(str, Integer.valueOf(num.intValue() + i));
        }
    }

    String eM() {
        if (!this.AW) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.AX);
        for (String str : this.AU.keySet()) {
            sb.append("&").append(str).append("=").append(this.AU.get(str));
        }
        for (String str2 : this.AV.keySet()) {
            sb.append("&").append(str2).append("=").append(this.AV.get(str2));
        }
        return sb.toString();
    }
}
