package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.wearable.internal.ae;

/* loaded from: classes.dex */
public class b implements SafeParcelable {
    public static final Parcelable.Creator<b> CREATOR = new c();
    final int BR;
    public final ae auZ;
    public final IntentFilter[] ava;

    b(int i, IBinder iBinder, IntentFilter[] intentFilterArr) {
        this.BR = i;
        if (iBinder != null) {
            this.auZ = ae.a.bS(iBinder);
        } else {
            this.auZ = null;
        }
        this.ava = intentFilterArr;
    }

    public b(ax axVar) {
        this.BR = 1;
        this.auZ = axVar;
        this.ava = axVar.pZ();
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
        c.a(this, dest, flags);
    }
}
