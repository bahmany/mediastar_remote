package com.iflytek.cloud.a.f.a;

import android.text.TextUtils;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import com.iflytek.speech.VoiceWakeuperAidl;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/* loaded from: classes.dex */
public class b {
    private static boolean b = false;
    public static HashMap<String, String> a = new HashMap<>();
    private static long c = 0;
    private static String d = "=";
    private static String e = ClientInfo.SEPARATOR_BETWEEN_VARS;
    private static String f = ":";
    private static String g = VoiceWakeuperAidl.PARAMS_SEPARATE;
    private static String h = "=========================================================\n";

    private static String a() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(System.currentTimeMillis()));
    }

    public static void a(String str, String str2) {
        if (b) {
            a.c("appendInfo:" + str + ClientInfo.SEPARATOR_BETWEEN_VARS + str2);
            if (c == 0) {
                a.put(str, a());
                c = System.currentTimeMillis();
                return;
            }
            long jCurrentTimeMillis = System.currentTimeMillis() - c;
            String str3 = !TextUtils.isEmpty(str2) ? str2 + f + jCurrentTimeMillis : "" + jCurrentTimeMillis;
            if (!a.containsKey(str) || TextUtils.isEmpty(a.get(str))) {
                a.put(str, str3);
            } else {
                a.put(str, a.get(str) + g + str3);
            }
        }
    }
}
