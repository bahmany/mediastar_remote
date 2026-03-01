package com.iflytek.cloud.util.a.a;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
final class b implements Parcelable.Creator<a> {
    b() {
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public a createFromParcel(Parcel parcel) {
        a aVar = new a();
        aVar.a = parcel.readString();
        aVar.b = parcel.readString();
        aVar.c = parcel.readString();
        aVar.d = parcel.readString();
        aVar.e = parcel.readString();
        aVar.f = parcel.readString();
        aVar.g = parcel.readString();
        return aVar;
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public a[] newArray(int i) {
        return new a[i];
    }
}
