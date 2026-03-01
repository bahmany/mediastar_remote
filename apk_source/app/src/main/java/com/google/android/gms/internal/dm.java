package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.dynamic.d;

@ez
/* loaded from: classes.dex */
public final class dm implements SafeParcelable {
    public static final dl CREATOR = new dl();
    public final gt lD;
    public final int orientation;
    public final dj rK;
    public final t rL;
    public final dn rM;
    public final gv rN;
    public final bw rO;
    public final String rP;
    public final boolean rQ;
    public final String rR;
    public final dq rS;
    public final int rT;
    public final bz rU;
    public final String rV;
    public final x rW;
    public final String rq;
    public final int versionCode;

    dm(int i, dj djVar, IBinder iBinder, IBinder iBinder2, IBinder iBinder3, IBinder iBinder4, String str, boolean z, String str2, IBinder iBinder5, int i2, int i3, String str3, gt gtVar, IBinder iBinder6, String str4, x xVar) {
        this.versionCode = i;
        this.rK = djVar;
        this.rL = (t) com.google.android.gms.dynamic.e.f(d.a.am(iBinder));
        this.rM = (dn) com.google.android.gms.dynamic.e.f(d.a.am(iBinder2));
        this.rN = (gv) com.google.android.gms.dynamic.e.f(d.a.am(iBinder3));
        this.rO = (bw) com.google.android.gms.dynamic.e.f(d.a.am(iBinder4));
        this.rP = str;
        this.rQ = z;
        this.rR = str2;
        this.rS = (dq) com.google.android.gms.dynamic.e.f(d.a.am(iBinder5));
        this.orientation = i2;
        this.rT = i3;
        this.rq = str3;
        this.lD = gtVar;
        this.rU = (bz) com.google.android.gms.dynamic.e.f(d.a.am(iBinder6));
        this.rV = str4;
        this.rW = xVar;
    }

    public dm(dj djVar, t tVar, dn dnVar, dq dqVar, gt gtVar) {
        this.versionCode = 4;
        this.rK = djVar;
        this.rL = tVar;
        this.rM = dnVar;
        this.rN = null;
        this.rO = null;
        this.rP = null;
        this.rQ = false;
        this.rR = null;
        this.rS = dqVar;
        this.orientation = -1;
        this.rT = 4;
        this.rq = null;
        this.lD = gtVar;
        this.rU = null;
        this.rV = null;
        this.rW = null;
    }

    public dm(t tVar, dn dnVar, bw bwVar, dq dqVar, gv gvVar, boolean z, int i, String str, gt gtVar, bz bzVar) {
        this.versionCode = 4;
        this.rK = null;
        this.rL = tVar;
        this.rM = dnVar;
        this.rN = gvVar;
        this.rO = bwVar;
        this.rP = null;
        this.rQ = z;
        this.rR = null;
        this.rS = dqVar;
        this.orientation = i;
        this.rT = 3;
        this.rq = str;
        this.lD = gtVar;
        this.rU = bzVar;
        this.rV = null;
        this.rW = null;
    }

    public dm(t tVar, dn dnVar, bw bwVar, dq dqVar, gv gvVar, boolean z, int i, String str, String str2, gt gtVar, bz bzVar) {
        this.versionCode = 4;
        this.rK = null;
        this.rL = tVar;
        this.rM = dnVar;
        this.rN = gvVar;
        this.rO = bwVar;
        this.rP = str2;
        this.rQ = z;
        this.rR = str;
        this.rS = dqVar;
        this.orientation = i;
        this.rT = 3;
        this.rq = null;
        this.lD = gtVar;
        this.rU = bzVar;
        this.rV = null;
        this.rW = null;
    }

    public dm(t tVar, dn dnVar, dq dqVar, gv gvVar, int i, gt gtVar, String str, x xVar) {
        this.versionCode = 4;
        this.rK = null;
        this.rL = tVar;
        this.rM = dnVar;
        this.rN = gvVar;
        this.rO = null;
        this.rP = null;
        this.rQ = false;
        this.rR = null;
        this.rS = dqVar;
        this.orientation = i;
        this.rT = 1;
        this.rq = null;
        this.lD = gtVar;
        this.rU = null;
        this.rV = str;
        this.rW = xVar;
    }

    public dm(t tVar, dn dnVar, dq dqVar, gv gvVar, boolean z, int i, gt gtVar) {
        this.versionCode = 4;
        this.rK = null;
        this.rL = tVar;
        this.rM = dnVar;
        this.rN = gvVar;
        this.rO = null;
        this.rP = null;
        this.rQ = z;
        this.rR = null;
        this.rS = dqVar;
        this.orientation = i;
        this.rT = 2;
        this.rq = null;
        this.lD = gtVar;
        this.rU = null;
        this.rV = null;
        this.rW = null;
    }

    public static void a(Intent intent, dm dmVar) {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo", dmVar);
        intent.putExtra("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo", bundle);
    }

    public static dm b(Intent intent) {
        try {
            Bundle bundleExtra = intent.getBundleExtra("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo");
            bundleExtra.setClassLoader(dm.class.getClassLoader());
            return (dm) bundleExtra.getParcelable("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo");
        } catch (Exception e) {
            return null;
        }
    }

    IBinder cc() {
        return com.google.android.gms.dynamic.e.k(this.rL).asBinder();
    }

    IBinder cd() {
        return com.google.android.gms.dynamic.e.k(this.rM).asBinder();
    }

    IBinder ce() {
        return com.google.android.gms.dynamic.e.k(this.rN).asBinder();
    }

    IBinder cf() {
        return com.google.android.gms.dynamic.e.k(this.rO).asBinder();
    }

    IBinder cg() {
        return com.google.android.gms.dynamic.e.k(this.rU).asBinder();
    }

    IBinder ch() {
        return com.google.android.gms.dynamic.e.k(this.rS).asBinder();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        dl.a(this, out, flags);
    }
}
