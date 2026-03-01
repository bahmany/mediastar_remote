package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.ji;

/* loaded from: classes.dex */
public class jd implements SafeParcelable {
    public static final je CREATOR = new je();
    private final int BR;
    private final jf Mk;

    jd(int i, jf jfVar) {
        this.BR = i;
        this.Mk = jfVar;
    }

    private jd(jf jfVar) {
        this.BR = 1;
        this.Mk = jfVar;
    }

    public static jd a(ji.b<?, ?> bVar) {
        if (bVar instanceof jf) {
            return new jd((jf) bVar);
        }
        throw new IllegalArgumentException("Unsupported safe parcelable field converter class.");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        je jeVar = CREATOR;
        return 0;
    }

    int getVersionCode() {
        return this.BR;
    }

    jf ha() {
        return this.Mk;
    }

    public ji.b<?, ?> hb() {
        if (this.Mk != null) {
            return this.Mk;
        }
        throw new IllegalStateException("There was no converter wrapped in this ConverterWrapper.");
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        je jeVar = CREATOR;
        je.a(this, out, flags);
    }
}
