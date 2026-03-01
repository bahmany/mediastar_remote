package com.google.android.gms.drive.query.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class Operator implements SafeParcelable {
    public static final Parcelable.Creator<Operator> CREATOR = new l();
    public static final Operator QR = new Operator("=");
    public static final Operator QS = new Operator("<");
    public static final Operator QT = new Operator("<=");
    public static final Operator QU = new Operator(">");
    public static final Operator QV = new Operator(">=");
    public static final Operator QW = new Operator("and");
    public static final Operator QX = new Operator("or");
    public static final Operator QY = new Operator("not");
    public static final Operator QZ = new Operator("contains");
    final int BR;
    final String mTag;

    Operator(int versionCode, String tag) {
        this.BR = versionCode;
        this.mTag = tag;
    }

    private Operator(String tag) {
        this(1, tag);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            Operator operator = (Operator) obj;
            return this.mTag == null ? operator.mTag == null : this.mTag.equals(operator.mTag);
        }
        return false;
    }

    public String getTag() {
        return this.mTag;
    }

    public int hashCode() {
        return (this.mTag == null ? 0 : this.mTag.hashCode()) + 31;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        l.a(this, out, flags);
    }
}
