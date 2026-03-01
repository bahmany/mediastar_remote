package com.google.android.gms.drive.realtime.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class p implements Parcelable.Creator<ParcelableCollaborator> {
    static void a(ParcelableCollaborator parcelableCollaborator, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, parcelableCollaborator.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, parcelableCollaborator.Rc);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, parcelableCollaborator.Rd);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, parcelableCollaborator.vL, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, parcelableCollaborator.Re, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, parcelableCollaborator.Nz, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, parcelableCollaborator.Rf, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, parcelableCollaborator.Rg, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aW */
    public ParcelableCollaborator createFromParcel(Parcel parcel) {
        boolean zC = false;
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        String strO5 = null;
        boolean zC2 = false;
        int iG = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 3:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 4:
                    strO5 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 5:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 6:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 8:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new ParcelableCollaborator(iG, zC2, zC, strO5, strO4, strO3, strO2, strO);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cj */
    public ParcelableCollaborator[] newArray(int i) {
        return new ParcelableCollaborator[i];
    }
}
