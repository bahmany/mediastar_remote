package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.List;

/* loaded from: classes.dex */
public class au implements SafeParcelable {
    public static final Parcelable.Creator<au> CREATOR = new av();
    public final long avC;
    public final List<am> avE;
    public final int statusCode;
    public final int versionCode;

    au(int i, int i2, long j, List<am> list) {
        this.versionCode = i;
        this.statusCode = i2;
        this.avC = j;
        this.avE = list;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        av.a(this, out, flags);
    }
}
