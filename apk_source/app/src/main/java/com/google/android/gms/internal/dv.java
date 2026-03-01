package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.dynamic.d;

@ez
/* loaded from: classes.dex */
public final class dv implements SafeParcelable {
    public static final du CREATOR = new du();
    public final el lM;
    public final ee lT;
    public final eg si;
    public final Context sj;
    public final int versionCode;

    dv(int i, IBinder iBinder, IBinder iBinder2, IBinder iBinder3, IBinder iBinder4) {
        this.versionCode = i;
        this.lM = (el) com.google.android.gms.dynamic.e.f(d.a.am(iBinder));
        this.lT = (ee) com.google.android.gms.dynamic.e.f(d.a.am(iBinder2));
        this.si = (eg) com.google.android.gms.dynamic.e.f(d.a.am(iBinder3));
        this.sj = (Context) com.google.android.gms.dynamic.e.f(d.a.am(iBinder4));
    }

    public dv(eg egVar, el elVar, ee eeVar, Context context) {
        this.versionCode = 1;
        this.si = egVar;
        this.lM = elVar;
        this.lT = eeVar;
        this.sj = context;
    }

    public static void a(Intent intent, dv dvVar) {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("com.google.android.gms.ads.internal.purchase.InAppPurchaseManagerInfo", dvVar);
        intent.putExtra("com.google.android.gms.ads.internal.purchase.InAppPurchaseManagerInfo", bundle);
    }

    public static dv c(Intent intent) {
        try {
            Bundle bundleExtra = intent.getBundleExtra("com.google.android.gms.ads.internal.purchase.InAppPurchaseManagerInfo");
            bundleExtra.setClassLoader(dv.class.getClassLoader());
            return (dv) bundleExtra.getParcelable("com.google.android.gms.ads.internal.purchase.InAppPurchaseManagerInfo");
        } catch (Exception e) {
            return null;
        }
    }

    IBinder cl() {
        return com.google.android.gms.dynamic.e.k(this.lM).asBinder();
    }

    IBinder cm() {
        return com.google.android.gms.dynamic.e.k(this.lT).asBinder();
    }

    IBinder cn() {
        return com.google.android.gms.dynamic.e.k(this.si).asBinder();
    }

    IBinder co() {
        return com.google.android.gms.dynamic.e.k(this.sj).asBinder();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        du.a(this, out, flags);
    }
}
