package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.kv;

/* loaded from: classes.dex */
public final class a implements SafeParcelable {
    private final int BR;
    private final String BZ;
    private final String Sq;
    private final String Sr;
    public static final a Sp = new a(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, String.valueOf(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE), null);
    public static final Parcelable.Creator<a> CREATOR = new b();

    a(int i, String str, String str2, String str3) {
        this.BR = i;
        this.BZ = (String) com.google.android.gms.common.internal.n.i(str);
        this.Sq = "";
        this.Sr = str3;
    }

    public a(String str, String str2, String str3) {
        this(1, str, "", str3);
    }

    private boolean a(a aVar) {
        return this.BZ.equals(aVar.BZ) && com.google.android.gms.common.internal.m.equal(this.Sq, aVar.Sq) && com.google.android.gms.common.internal.m.equal(this.Sr, aVar.Sr);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof a) && a((a) that));
    }

    public String getPackageName() {
        return this.BZ;
    }

    public String getVersion() {
        return this.Sq;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.BZ, this.Sq, this.Sr);
    }

    a iA() {
        return new a(kv.bq(this.BZ), kv.bq(this.Sq), kv.bq(this.Sr));
    }

    public String iz() {
        return this.Sr;
    }

    public String toString() {
        return String.format("Application{%s:%s:%s}", this.BZ, this.Sq, this.Sr);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        b.a(this, parcel, flags);
    }
}
