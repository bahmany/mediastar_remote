package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class n implements Parcelable.Creator<RawDataPoint> {
    static void a(RawDataPoint rawDataPoint, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, rawDataPoint.Sz);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, rawDataPoint.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, rawDataPoint.SA);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable[]) rawDataPoint.SB, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, rawDataPoint.Tb);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, rawDataPoint.Tc);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, rawDataPoint.SD);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, rawDataPoint.SE);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bs, reason: merged with bridge method [inline-methods] */
    public RawDataPoint createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        long jI = 0;
        long jI2 = 0;
        Value[] valueArr = null;
        int iG2 = 0;
        int iG3 = 0;
        long jI3 = 0;
        long jI4 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 2:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 3:
                    valueArr = (Value[]) com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB, Value.CREATOR);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 6:
                    jI3 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 7:
                    jI4 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 1000:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new RawDataPoint(iG, jI, jI2, valueArr, iG2, iG3, jI3, jI4);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cJ, reason: merged with bridge method [inline-methods] */
    public RawDataPoint[] newArray(int i) {
        return new RawDataPoint[i];
    }
}
