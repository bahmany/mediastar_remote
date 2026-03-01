package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.jr;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class d implements SafeParcelable {
    public static final Parcelable.Creator<d> CREATOR = new e();
    private final int BR;
    String auo;
    String aup;
    ArrayList<b> auq;

    d() {
        this.BR = 1;
        this.auq = jr.hz();
    }

    d(int i, String str, String str2, ArrayList<b> arrayList) {
        this.BR = i;
        this.auo = str;
        this.aup = str2;
        this.auq = arrayList;
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
        e.a(this, dest, flags);
    }
}
