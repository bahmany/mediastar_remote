package com.google.android.gms.drive.realtime.internal.event;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class d implements Parcelable.Creator<ReferenceShiftedDetails> {
    static void a(ReferenceShiftedDetails referenceShiftedDetails, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, referenceShiftedDetails.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, referenceShiftedDetails.Rz, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, referenceShiftedDetails.RA, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, referenceShiftedDetails.RB);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, referenceShiftedDetails.RC);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bb, reason: merged with bridge method [inline-methods] */
    public ReferenceShiftedDetails createFromParcel(Parcel parcel) {
        String strO = null;
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG2 = 0;
        String strO2 = null;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
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
        return new ReferenceShiftedDetails(iG3, strO2, strO, iG2, iG);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: co, reason: merged with bridge method [inline-methods] */
    public ReferenceShiftedDetails[] newArray(int i) {
        return new ReferenceShiftedDetails[i];
    }
}
