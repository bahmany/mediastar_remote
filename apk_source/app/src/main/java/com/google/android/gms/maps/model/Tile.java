package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class Tile implements SafeParcelable {
    public static final u CREATOR = new u();
    private final int BR;
    public final byte[] data;
    public final int height;
    public final int width;

    Tile(int versionCode, int width, int height, byte[] data) {
        this.BR = versionCode;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public Tile(int width, int height, byte[] data) {
        this(1, width, height, data);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (com.google.android.gms.maps.internal.v.mK()) {
            v.a(this, out, flags);
        } else {
            u.a(this, out, flags);
        }
    }
}
