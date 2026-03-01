package com.iflytek.cloud.a.f;

import android.util.DisplayMetrics;
import java.text.SimpleDateFormat;

/* loaded from: classes.dex */
public class c {
    public static DisplayMetrics a = null;

    public static String a(long j) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS").format(Long.valueOf(j));
    }
}
