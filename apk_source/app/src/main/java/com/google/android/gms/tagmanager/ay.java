package com.google.android.gms.tagmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
class ay {
    private static String apn;
    static Map<String, String> apo = new HashMap();

    ay() {
    }

    static void cC(String str) {
        synchronized (ay.class) {
            apn = str;
        }
    }

    static void d(Context context, String str) {
        cz.a(context, "gtm_install_referrer", "referrer", str);
        f(context, str);
    }

    static String e(Context context, String str) {
        if (apn == null) {
            synchronized (ay.class) {
                if (apn == null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("gtm_install_referrer", 0);
                    if (sharedPreferences != null) {
                        apn = sharedPreferences.getString("referrer", "");
                    } else {
                        apn = "";
                    }
                }
            }
        }
        return x(apn, str);
    }

    static String f(Context context, String str, String str2) {
        String string = apo.get(str);
        if (string == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("gtm_click_referrers", 0);
            string = sharedPreferences != null ? sharedPreferences.getString(str, "") : "";
            apo.put(str, string);
        }
        return x(string, str2);
    }

    static void f(Context context, String str) {
        String strX = x(str, "conv");
        if (strX == null || strX.length() <= 0) {
            return;
        }
        apo.put(strX, str);
        cz.a(context, "gtm_click_referrers", strX, str);
    }

    static String x(String str, String str2) {
        if (str2 != null) {
            return Uri.parse("http://hostname/?" + str).getQueryParameter(str2);
        }
        if (str.length() > 0) {
            return str;
        }
        return null;
    }
}
