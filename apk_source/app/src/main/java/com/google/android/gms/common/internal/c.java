package com.google.android.gms.common.internal;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public abstract class c implements SafeParcelable {
    private static final Object Ln = new Object();
    private static ClassLoader Lo = null;
    private static Integer Lp = null;
    private boolean Lq = false;

    private static boolean a(Class<?> cls) {
        try {
            return SafeParcelable.NULL.equals(cls.getField("NULL").get(null));
        } catch (IllegalAccessException e) {
            return false;
        } catch (NoSuchFieldException e2) {
            return false;
        }
    }

    protected static boolean aV(String str) {
        ClassLoader classLoaderGO = gO();
        if (classLoaderGO == null) {
            return true;
        }
        try {
            return a(classLoaderGO.loadClass(str));
        } catch (Exception e) {
            return false;
        }
    }

    protected static ClassLoader gO() {
        ClassLoader classLoader;
        synchronized (Ln) {
            classLoader = Lo;
        }
        return classLoader;
    }

    protected static Integer gP() {
        Integer num;
        synchronized (Ln) {
            num = Lp;
        }
        return num;
    }

    protected boolean gQ() {
        return this.Lq;
    }
}
