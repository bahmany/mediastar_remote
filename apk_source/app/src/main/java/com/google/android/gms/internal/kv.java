package com.google.android.gms.internal;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.fitness.data.DataSource;

/* loaded from: classes.dex */
public class kv {
    private static final ThreadLocal<String> To = new ThreadLocal<>();

    public static String bq(String str) {
        return s(str, To.get());
    }

    public static DataSource c(DataSource dataSource) {
        if (dataSource.iJ()) {
            return (iU() || To.get().equals(dataSource.getAppPackageName())) ? dataSource : dataSource.iK();
        }
        return dataSource;
    }

    public static boolean iU() {
        String str = To.get();
        return str == null || str.startsWith(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
    }

    private static String s(String str, String str2) {
        if (str == null || str2 == null) {
            return str;
        }
        byte[] bArr = new byte[str.length() + str2.length()];
        System.arraycopy(str.getBytes(), 0, bArr, 0, str.length());
        System.arraycopy(str2.getBytes(), 0, bArr, str.length(), str2.length());
        return Integer.toHexString(kb.a(bArr, 0, bArr.length, 0));
    }
}
