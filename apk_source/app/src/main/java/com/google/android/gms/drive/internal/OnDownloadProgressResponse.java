package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class OnDownloadProgressResponse implements SafeParcelable {
    public static final Parcelable.Creator<OnDownloadProgressResponse> CREATOR = new aj();
    final int BR;
    final long Ph;
    final long Pi;

    OnDownloadProgressResponse(int versionCode, long bytesLoaded, long bytesExpected) {
        this.BR = versionCode;
        this.Ph = bytesLoaded;
        this.Pi = bytesExpected;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* renamed from: if */
    public long m4if() {
        return this.Ph;
    }

    public long ig() {
        return this.Pi;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        aj.a(this, dest, flags);
    }
}
