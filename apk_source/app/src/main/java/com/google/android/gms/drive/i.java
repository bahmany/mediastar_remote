package com.google.android.gms.drive;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.n;

/* loaded from: classes.dex */
public abstract class i implements Parcelable {
    private volatile transient boolean ND = false;

    protected abstract void I(Parcel parcel, int i);

    public final boolean hT() {
        return this.ND;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        n.I(!hT());
        this.ND = true;
        I(dest, flags);
    }
}
