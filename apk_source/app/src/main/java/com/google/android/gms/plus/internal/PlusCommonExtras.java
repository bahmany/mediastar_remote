package com.google.android.gms.plus.internal;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class PlusCommonExtras implements SafeParcelable {
    private final int BR;
    private String alp;
    private String alq;
    public static String TAG = "PlusCommonExtras";
    public static final f CREATOR = new f();

    public PlusCommonExtras() {
        this.BR = 1;
        this.alp = "";
        this.alq = "";
    }

    PlusCommonExtras(int versionCode, String gpsrc, String clientCallingPackage) {
        this.BR = versionCode;
        this.alp = gpsrc;
        this.alq = clientCallingPackage;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PlusCommonExtras)) {
            return false;
        }
        PlusCommonExtras plusCommonExtras = (PlusCommonExtras) obj;
        return this.BR == plusCommonExtras.BR && m.equal(this.alp, plusCommonExtras.alp) && m.equal(this.alq, plusCommonExtras.alq);
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.BR), this.alp, this.alq);
    }

    public String nc() {
        return this.alp;
    }

    public String nd() {
        return this.alq;
    }

    public void o(Bundle bundle) {
        bundle.putByteArray("android.gms.plus.internal.PlusCommonExtras.extraPlusCommon", com.google.android.gms.common.internal.safeparcel.c.a(this));
    }

    public String toString() {
        return m.h(this).a("versionCode", Integer.valueOf(this.BR)).a("Gpsrc", this.alp).a("ClientCallingPackage", this.alq).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        f.a(this, out, flags);
    }
}
