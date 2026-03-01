package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class m implements Parcelable.Creator<RawBucket> {
    static void a(RawBucket rawBucket, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, rawBucket.KL);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, rawBucket.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, rawBucket.Si);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) rawBucket.Sk, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, rawBucket.Sv);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, rawBucket.Sw, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, rawBucket.Sx);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, rawBucket.Sy);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: br, reason: merged with bridge method [inline-methods] */
    public RawBucket createFromParcel(Parcel parcel) {
        long jI = 0;
        ArrayList arrayListC = null;
        boolean zC = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        int iG2 = 0;
        Session session = null;
        long jI2 = 0;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 2:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 3:
                    session = (Session) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Session.CREATOR);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, RawDataSet.CREATOR);
                    break;
                case 6:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 7:
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
        return new RawBucket(iG3, jI2, jI, session, iG2, arrayListC, iG, zC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cI, reason: merged with bridge method [inline-methods] */
    public RawBucket[] newArray(int i) {
        return new RawBucket[i];
    }
}
