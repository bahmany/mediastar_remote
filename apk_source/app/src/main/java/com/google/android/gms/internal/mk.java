package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class mk implements SafeParcelable {
    public static final ml CREATOR = new ml();
    final int BR;
    private final String afo;
    private final String mTag;

    mk(int i, String str, String str2) {
        this.BR = i;
        this.afo = str;
        this.mTag = str2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        ml mlVar = CREATOR;
        return 0;
    }

    public boolean equals(Object that) {
        if (!(that instanceof mk)) {
            return false;
        }
        mk mkVar = (mk) that;
        return com.google.android.gms.common.internal.m.equal(this.afo, mkVar.afo) && com.google.android.gms.common.internal.m.equal(this.mTag, mkVar.mTag);
    }

    public String getTag() {
        return this.mTag;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.afo, this.mTag);
    }

    public String mi() {
        return this.afo;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("mPlaceId", this.afo).a("mTag", this.mTag).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        ml mlVar = CREATOR;
        ml.a(this, out, flags);
    }
}
