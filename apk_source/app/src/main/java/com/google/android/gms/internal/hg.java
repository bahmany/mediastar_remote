package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class hg implements SafeParcelable {
    public static final hh CREATOR = new hh();
    final int BR;
    final String BZ;
    final String Ca;
    final String Cb;

    hg(int i, String str, String str2, String str3) {
        this.BR = i;
        this.BZ = str;
        this.Ca = str2;
        this.Cb = str3;
    }

    public hg(String str, String str2, String str3) {
        this(1, str, str2, str3);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        hh hhVar = CREATOR;
        return 0;
    }

    public String toString() {
        return String.format("DocumentId[packageName=%s, corpusName=%s, uri=%s]", this.BZ, this.Ca, this.Cb);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        hh hhVar = CREATOR;
        hh.a(this, dest, flags);
    }
}
