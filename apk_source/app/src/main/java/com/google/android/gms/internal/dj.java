package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@ez
/* loaded from: classes.dex */
public final class dj implements SafeParcelable {
    public static final di CREATOR = new di();
    public final String mimeType;
    public final String packageName;
    public final String rp;
    public final String rq;
    public final String rr;
    public final String rs;
    public final String rt;
    public final int versionCode;

    public dj(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        this.versionCode = i;
        this.rp = str;
        this.rq = str2;
        this.mimeType = str3;
        this.packageName = str4;
        this.rr = str5;
        this.rs = str6;
        this.rt = str7;
    }

    public dj(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        this(1, str, str2, str3, str4, str5, str6, str7);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        di.a(this, out, flags);
    }
}
