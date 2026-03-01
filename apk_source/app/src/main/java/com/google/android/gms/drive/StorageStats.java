package com.google.android.gms.drive;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class StorageStats implements SafeParcelable {
    public static final Parcelable.Creator<StorageStats> CREATOR = new g();
    final int BR;
    final long Nt;
    final long Nu;
    final long Nv;
    final long Nw;
    final int Nx;

    StorageStats(int versionCode, long metadataSizeBytes, long cachedContentsSizeBytes, long pinnedItemsSizeBytes, long totalSizeBytes, int numPinnedItems) {
        this.BR = versionCode;
        this.Nt = metadataSizeBytes;
        this.Nu = cachedContentsSizeBytes;
        this.Nv = pinnedItemsSizeBytes;
        this.Nw = totalSizeBytes;
        this.Nx = numPinnedItems;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        g.a(this, out, flags);
    }
}
