package com.google.android.gms.internal;

import android.content.ContentResolver;
import android.content.Context;

/* loaded from: classes.dex */
public abstract class iv<T> {
    protected final String JH;
    protected final T JI;
    private T JJ = null;
    private static final Object mw = new Object();
    private static a JG = null;

    /* renamed from: com.google.android.gms.internal.iv$1 */
    static class AnonymousClass1 extends iv<Boolean> {
        AnonymousClass1(String str, Boolean bool) {
            super(str, bool);
        }
    }

    /* renamed from: com.google.android.gms.internal.iv$2 */
    static class AnonymousClass2 extends iv<Integer> {
        AnonymousClass2(String str, Integer num) {
            super(str, num);
        }
    }

    /* renamed from: com.google.android.gms.internal.iv$3 */
    static class AnonymousClass3 extends iv<String> {
        AnonymousClass3(String str, String str2) {
            super(str, str2);
        }
    }

    private interface a {
    }

    private static class b implements a {
        private final ContentResolver mContentResolver;

        public b(ContentResolver contentResolver) {
            this.mContentResolver = contentResolver;
        }
    }

    protected iv(String str, T t) {
        this.JH = str;
        this.JI = t;
    }

    public static void H(Context context) {
        synchronized (mw) {
            if (JG == null) {
                JG = new b(context.getContentResolver());
            }
        }
    }

    public static iv<Integer> a(String str, Integer num) {
        return new iv<Integer>(str, num) { // from class: com.google.android.gms.internal.iv.2
            AnonymousClass2(String str2, Integer num2) {
                super(str2, num2);
            }
        };
    }

    public static iv<Boolean> g(String str, boolean z) {
        return new iv<Boolean>(str, Boolean.valueOf(z)) { // from class: com.google.android.gms.internal.iv.1
            AnonymousClass1(String str2, Boolean bool) {
                super(str2, bool);
            }
        };
    }

    public static iv<String> m(String str, String str2) {
        return new iv<String>(str, str2) { // from class: com.google.android.gms.internal.iv.3
            AnonymousClass3(String str3, String str22) {
                super(str3, str22);
            }
        };
    }

    public String getKey() {
        return this.JH;
    }
}
