package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;

@ez
/* loaded from: classes.dex */
public class dw {
    private final Context mContext;
    private Object sk;

    public dw(Context context) {
        this.mContext = context;
    }

    public Bundle a(String str, String str2, String str3) throws ClassNotFoundException {
        try {
            Class<?> clsLoadClass = this.mContext.getClassLoader().loadClass("com.android.vending.billing.IInAppBillingService");
            return (Bundle) clsLoadClass.getDeclaredMethod("getBuyIntent", Integer.TYPE, String.class, String.class, String.class, String.class).invoke(clsLoadClass.cast(this.sk), 3, str, str2, "inapp", str3);
        } catch (Exception e) {
            gs.d("IInAppBillingService is not available, please add com.android.vending.billing.IInAppBillingService to project.", e);
            return null;
        }
    }

    public int c(String str, String str2) throws ClassNotFoundException {
        try {
            Class<?> clsLoadClass = this.mContext.getClassLoader().loadClass("com.android.vending.billing.IInAppBillingService");
            return ((Integer) clsLoadClass.getDeclaredMethod("consumePurchase", Integer.TYPE, String.class, String.class).invoke(clsLoadClass.cast(this.sk), 3, str, str2)).intValue();
        } catch (Exception e) {
            gs.d("IInAppBillingService is not available, please add com.android.vending.billing.IInAppBillingService to project.", e);
            return 5;
        }
    }

    public Bundle d(String str, String str2) throws ClassNotFoundException {
        try {
            Class<?> clsLoadClass = this.mContext.getClassLoader().loadClass("com.android.vending.billing.IInAppBillingService");
            return (Bundle) clsLoadClass.getDeclaredMethod("getPurchases", Integer.TYPE, String.class, String.class, String.class).invoke(clsLoadClass.cast(this.sk), 3, str, "inapp", str2);
        } catch (Exception e) {
            gs.d("IInAppBillingService is not available, please add com.android.vending.billing.IInAppBillingService to project.", e);
            return null;
        }
    }

    public void destroy() {
        this.sk = null;
    }

    public void r(IBinder iBinder) {
        try {
            this.sk = this.mContext.getClassLoader().loadClass("com.android.vending.billing.IInAppBillingService$Stub").getDeclaredMethod("asInterface", IBinder.class).invoke(null, iBinder);
        } catch (Exception e) {
            gs.W("IInAppBillingService is not available, please add com.android.vending.billing.IInAppBillingService to project.");
        }
    }
}
