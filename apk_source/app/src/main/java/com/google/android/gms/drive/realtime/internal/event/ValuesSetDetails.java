package com.google.android.gms.drive.realtime.internal.event;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class ValuesSetDetails implements SafeParcelable {
    public static final Parcelable.Creator<ValuesSetDetails> CREATOR = new j();
    final int BR;
    final int Rj;
    final int Rk;
    final int mIndex;

    ValuesSetDetails(int versionCode, int index, int valueIndex, int valueCount) {
        this.BR = versionCode;
        this.mIndex = index;
        this.Rj = valueIndex;
        this.Rk = valueCount;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        j.a(this, dest, flags);
    }
}
