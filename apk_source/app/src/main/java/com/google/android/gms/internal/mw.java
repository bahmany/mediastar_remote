package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Locale;

/* loaded from: classes.dex */
public class mw implements SafeParcelable {
    public static final mx CREATOR = new mx();
    public final String Dv;
    public final String ahY;
    public final String ahZ;
    public final int versionCode;

    public mw(int i, String str, String str2, String str3) {
        this.versionCode = i;
        this.ahY = str;
        this.ahZ = str2;
        this.Dv = str3;
    }

    public mw(String str, Locale locale, String str2) {
        this.versionCode = 0;
        this.ahY = str;
        this.ahZ = locale.toString();
        this.Dv = str2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        mx mxVar = CREATOR;
        return 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !(object instanceof mw)) {
            return false;
        }
        mw mwVar = (mw) object;
        return this.ahZ.equals(mwVar.ahZ) && this.ahY.equals(mwVar.ahY) && com.google.android.gms.common.internal.m.equal(this.Dv, mwVar.Dv);
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.ahY, this.ahZ, this.Dv);
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("clientPackageName", this.ahY).a("locale", this.ahZ).a("accountName", this.Dv).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        mx mxVar = CREATOR;
        mx.a(this, out, flags);
    }
}
