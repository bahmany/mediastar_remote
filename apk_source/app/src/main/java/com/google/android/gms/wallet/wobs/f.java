package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class f implements SafeParcelable {
    public static final Parcelable.Creator<f> CREATOR = new i();
    private final int BR;
    l asR;
    g aur;
    String label;
    String type;

    f() {
        this.BR = 1;
    }

    f(int i, String str, g gVar, String str2, l lVar) {
        this.BR = i;
        this.label = str;
        this.aur = gVar;
        this.type = str2;
        this.asR = lVar;
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
        i.a(this, dest, flags);
    }
}
