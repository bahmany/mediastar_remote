package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class p implements SafeParcelable {
    public static final Parcelable.Creator<p> CREATOR = new q();
    private final int BR;
    l auC;
    n auD;
    n auE;
    String auy;
    String tG;

    p() {
        this.BR = 1;
    }

    p(int i, String str, String str2, l lVar, n nVar, n nVar2) {
        this.BR = i;
        this.auy = str;
        this.tG = str2;
        this.auC = lVar;
        this.auD = nVar;
        this.auE = nVar2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        q.a(this, dest, flags);
    }
}
