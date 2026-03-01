package com.google.android.gms.drive.realtime.internal.event;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class h implements Parcelable.Creator<ValuesAddedDetails> {
    static void a(ValuesAddedDetails valuesAddedDetails, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, valuesAddedDetails.BR);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, valuesAddedDetails.mIndex);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, valuesAddedDetails.Rj);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, valuesAddedDetails.Rk);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, valuesAddedDetails.RF, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, valuesAddedDetails.RG);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bf, reason: merged with bridge method [inline-methods] */
    public ValuesAddedDetails createFromParcel(Parcel parcel) {
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        String strO = null;
        int iG2 = 0;
        int iG3 = 0;
        int iG4 = 0;
        int iG5 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG5 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iG4 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 3:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 6:
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
        return new ValuesAddedDetails(iG5, iG4, iG3, iG2, strO, iG);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cs, reason: merged with bridge method [inline-methods] */
    public ValuesAddedDetails[] newArray(int i) {
        return new ValuesAddedDetails[i];
    }
}
