package com.google.android.gms.maps.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.internal.c;
import com.google.android.gms.maps.model.RuntimeRemoteException;

/* loaded from: classes.dex */
public class u {
    private static Context ajm;
    private static c ajn;

    public static c R(Context context) throws GooglePlayServicesNotAvailableException, PackageManager.NameNotFoundException {
        com.google.android.gms.common.internal.n.i(context);
        if (ajn != null) {
            return ajn;
        }
        S(context);
        ajn = T(context);
        try {
            ajn.a(com.google.android.gms.dynamic.e.k(getRemoteContext(context).getResources()), GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE);
            return ajn;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    private static void S(Context context) throws GooglePlayServicesNotAvailableException, PackageManager.NameNotFoundException {
        int iIsGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        switch (iIsGooglePlayServicesAvailable) {
            case 0:
                return;
            default:
                throw new GooglePlayServicesNotAvailableException(iIsGooglePlayServicesAvailable);
        }
    }

    private static c T(Context context) {
        if (mI()) {
            Log.i(u.class.getSimpleName(), "Making Creator statically");
            return (c) c(mJ());
        }
        Log.i(u.class.getSimpleName(), "Making Creator dynamically");
        return c.a.aP((IBinder) a(getRemoteContext(context).getClassLoader(), "com.google.android.gms.maps.internal.CreatorImpl"));
    }

    private static <T> T a(ClassLoader classLoader, String str) {
        try {
            return (T) c(((ClassLoader) com.google.android.gms.common.internal.n.i(classLoader)).loadClass(str));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to find dynamic class " + str);
        }
    }

    private static <T> T c(Class<?> cls) {
        try {
            return (T) cls.newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to call the default constructor of " + cls.getName());
        } catch (InstantiationException e2) {
            throw new IllegalStateException("Unable to instantiate the dynamic class " + cls.getName());
        }
    }

    private static Context getRemoteContext(Context context) {
        if (ajm == null) {
            if (mI()) {
                ajm = context.getApplicationContext();
            } else {
                ajm = GooglePlayServicesUtil.getRemoteContext(context);
            }
        }
        return ajm;
    }

    private static boolean mI() {
        return false;
    }

    private static Class<?> mJ() {
        try {
            return Build.VERSION.SDK_INT < 15 ? Class.forName("com.google.android.gms.maps.internal.CreatorImplGmm6") : Class.forName("com.google.android.gms.maps.internal.CreatorImpl");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
