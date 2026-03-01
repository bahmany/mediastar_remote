package com.google.android.gms.common.data;

import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class f implements Parcelable.Creator<DataHolder> {
    static void a(DataHolder dataHolder, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, dataHolder.gC(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, dataHolder.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable[]) dataHolder.gD(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, dataHolder.getStatusCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, dataHolder.gz(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: at */
    public DataHolder[] newArray(int i) {
        return new DataHolder[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: z */
    public DataHolder createFromParcel(Parcel parcel) {
        int iG = 0;
        Bundle bundleQ = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        CursorWindow[] cursorWindowArr = null;
        String[] strArrA = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strArrA = com.google.android.gms.common.internal.safeparcel.a.A(parcel, iB);
                    break;
                case 2:
                    cursorWindowArr = (CursorWindow[]) com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB, CursorWindow.CREATOR);
                    break;
                case 3:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 4:
                    bundleQ = com.google.android.gms.common.internal.safeparcel.a.q(parcel, iB);
                    break;
                case 1000:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        DataHolder dataHolder = new DataHolder(iG2, strArrA, cursorWindowArr, iG, bundleQ);
        dataHolder.gB();
        return dataHolder;
    }
}
