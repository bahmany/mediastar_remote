package com.google.android.gms.internal;

import android.content.Context;
import android.text.TextUtils;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.math.BigInteger;
import java.util.Locale;

@ez
/* loaded from: classes.dex */
public final class gf {
    private static final Object uf = new Object();
    private static String we;

    public static String a(Context context, String str, String str2) {
        String str3;
        synchronized (uf) {
            if (we == null && !TextUtils.isEmpty(str)) {
                b(context, str, str2);
            }
            str3 = we;
        }
        return str3;
    }

    private static void b(Context context, String str, String str2) {
        try {
            ClassLoader classLoader = context.createPackageContext(str2, 3).getClassLoader();
            Class<?> cls = Class.forName("com.google.ads.mediation.MediationAdapter", false, classLoader);
            BigInteger bigInteger = new BigInteger(new byte[1]);
            String[] strArrSplit = str.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
            BigInteger bit = bigInteger;
            for (int i = 0; i < strArrSplit.length; i++) {
                if (gj.a(classLoader, cls, strArrSplit[i])) {
                    bit = bit.setBit(i);
                }
            }
            we = String.format(Locale.US, "%X", bit);
        } catch (Throwable th) {
            we = "err";
        }
    }

    public static String dj() {
        String str;
        synchronized (uf) {
            str = we;
        }
        return str;
    }
}
