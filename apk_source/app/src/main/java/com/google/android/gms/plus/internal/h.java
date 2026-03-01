package com.google.android.gms.plus.internal;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Arrays;

/* loaded from: classes.dex */
public class h implements SafeParcelable {
    public static final j CREATOR = new j();
    private final int BR;
    private final String Dd;
    private final String[] als;
    private final String[] alt;
    private final String[] alu;
    private final String alv;
    private final String alw;
    private final String alx;
    private final String aly;
    private final PlusCommonExtras alz;

    h(int i, String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String str5, PlusCommonExtras plusCommonExtras) {
        this.BR = i;
        this.Dd = str;
        this.als = strArr;
        this.alt = strArr2;
        this.alu = strArr3;
        this.alv = str2;
        this.alw = str3;
        this.alx = str4;
        this.aly = str5;
        this.alz = plusCommonExtras;
    }

    public h(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, PlusCommonExtras plusCommonExtras) {
        this.BR = 1;
        this.Dd = str;
        this.als = strArr;
        this.alt = strArr2;
        this.alu = strArr3;
        this.alv = str2;
        this.alw = str3;
        this.alx = str4;
        this.aly = null;
        this.alz = plusCommonExtras;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof h)) {
            return false;
        }
        h hVar = (h) obj;
        return this.BR == hVar.BR && m.equal(this.Dd, hVar.Dd) && Arrays.equals(this.als, hVar.als) && Arrays.equals(this.alt, hVar.alt) && Arrays.equals(this.alu, hVar.alu) && m.equal(this.alv, hVar.alv) && m.equal(this.alw, hVar.alw) && m.equal(this.alx, hVar.alx) && m.equal(this.aly, hVar.aly) && m.equal(this.alz, hVar.alz);
    }

    public String getAccountName() {
        return this.Dd;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.BR), this.Dd, this.als, this.alt, this.alu, this.alv, this.alw, this.alx, this.aly, this.alz);
    }

    public String[] ne() {
        return this.als;
    }

    public String[] nf() {
        return this.alt;
    }

    public String[] ng() {
        return this.alu;
    }

    public String nh() {
        return this.alv;
    }

    public String ni() {
        return this.alw;
    }

    public String nj() {
        return this.alx;
    }

    public String nk() {
        return this.aly;
    }

    public PlusCommonExtras nl() {
        return this.alz;
    }

    public Bundle nm() {
        Bundle bundle = new Bundle();
        bundle.setClassLoader(PlusCommonExtras.class.getClassLoader());
        this.alz.o(bundle);
        return bundle;
    }

    public String toString() {
        return m.h(this).a("versionCode", Integer.valueOf(this.BR)).a("accountName", this.Dd).a("requestedScopes", this.als).a("visibleActivities", this.alt).a("requiredFeatures", this.alu).a("packageNameForAuth", this.alv).a("callingPackageName", this.alw).a("applicationName", this.alx).a("extra", this.alz.toString()).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        j.a(this, out, flags);
    }
}
