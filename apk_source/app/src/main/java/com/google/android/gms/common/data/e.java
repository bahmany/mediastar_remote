package com.google.android.gms.common.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class e<T extends SafeParcelable> extends DataBuffer<T> {
    private static final String[] JS = {"data"};
    private final Parcelable.Creator<T> JT;

    public e(DataHolder dataHolder, Parcelable.Creator<T> creator) {
        super(dataHolder);
        this.JT = creator;
    }

    @Override // com.google.android.gms.common.data.DataBuffer
    /* renamed from: aq, reason: merged with bridge method [inline-methods] */
    public T get(int i) {
        byte[] bArrF = this.IC.f("data", i, 0);
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.unmarshall(bArrF, 0, bArrF.length);
        parcelObtain.setDataPosition(0);
        T tCreateFromParcel = this.JT.createFromParcel(parcelObtain);
        parcelObtain.recycle();
        return tCreateFromParcel;
    }
}
