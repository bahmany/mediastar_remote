package com.google.android.gms.analytics;

import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
class x {
    static String a(w wVar, long j) {
        StringBuilder sb = new StringBuilder();
        sb.append(wVar.eG());
        if (wVar.eI() > 0) {
            long jEI = j - wVar.eI();
            if (jEI >= 0) {
                sb.append("&qt").append("=").append(jEI);
            }
        }
        sb.append("&z").append("=").append(wVar.eH());
        return sb.toString();
    }

    static String encode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("URL encoding failed for: " + input);
        }
    }

    static Map<String, String> z(Map<String, String> map) {
        HashMap map2 = new HashMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().startsWith("&") && entry.getValue() != null) {
                String strSubstring = entry.getKey().substring(1);
                if (!TextUtils.isEmpty(strSubstring)) {
                    map2.put(strSubstring, entry.getValue());
                }
            }
        }
        return map2;
    }
}
