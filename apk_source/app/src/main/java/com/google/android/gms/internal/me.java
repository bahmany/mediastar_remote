package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.List;

/* loaded from: classes.dex */
public class me implements SafeParcelable {
    public static final mf CREATOR = new mf();
    final int BR;
    private final boolean afc;
    private final List<mo> afd;

    me(int i, boolean z, List<mo> list) {
        this.BR = i;
        this.afc = z;
        this.afd = list;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        mf mfVar = CREATOR;
        return 0;
    }

    public boolean mc() {
        return this.afc;
    }

    public List<mo> md() {
        return this.afd;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        mf mfVar = CREATOR;
        mf.a(this, parcel, flags);
    }
}
