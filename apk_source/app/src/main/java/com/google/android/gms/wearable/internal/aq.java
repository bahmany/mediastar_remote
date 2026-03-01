package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.wearable.internal.ae;

/* loaded from: classes.dex */
public class aq implements SafeParcelable {
    public static final Parcelable.Creator<aq> CREATOR = new ar();
    final int BR;
    public final ae auZ;

    aq(int i, IBinder iBinder) {
        this.BR = i;
        if (iBinder != null) {
            this.auZ = ae.a.bS(iBinder);
        } else {
            this.auZ = null;
        }
    }

    public aq(ae aeVar) {
        this.BR = 1;
        this.auZ = aeVar;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    IBinder pT() {
        if (this.auZ == null) {
            return null;
        }
        return this.auZ.asBinder();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ar.a(this, dest, flags);
    }
}
