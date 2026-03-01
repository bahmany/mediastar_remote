package com.iflytek.cloud;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
final class j implements Parcelable.Creator<UnderstanderResult> {
    j() {
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public UnderstanderResult createFromParcel(Parcel parcel) {
        return new UnderstanderResult(parcel);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public UnderstanderResult[] newArray(int i) {
        return new UnderstanderResult[i];
    }
}
