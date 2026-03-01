package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class o implements Parcelable.Creator<RawDataSet> {
    static void a(RawDataSet rawDataSet, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, rawDataSet.Tb);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, rawDataSet.BR);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, rawDataSet.Td);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, rawDataSet.Te, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, rawDataSet.Sy);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bt, reason: merged with bridge method [inline-methods] */
    public RawDataSet createFromParcel(Parcel parcel) {
        boolean zC = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        ArrayList arrayListC = null;
        int iG = 0;
        int iG2 = 0;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 3:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, RawDataPoint.CREATOR);
                    break;
                case 4:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 1000:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new RawDataSet(iG3, iG2, iG, arrayListC, zC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cK, reason: merged with bridge method [inline-methods] */
    public RawDataSet[] newArray(int i) {
        return new RawDataSet[i];
    }
}
