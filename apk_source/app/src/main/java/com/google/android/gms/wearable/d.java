package com.google.android.gms.wearable;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class d implements Parcelable.Creator<c> {
    static void a(c cVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, cVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, cVar.getName(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, cVar.getAddress(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, cVar.getType());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, cVar.getRole());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, cVar.isEnabled());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, cVar.isConnected());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, cVar.pQ(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dQ */
    public c createFromParcel(Parcel parcel) {
        String strO = null;
        boolean zC = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        boolean zC2 = false;
        int iG = 0;
        int iG2 = 0;
        String strO2 = null;
        String strO3 = null;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 6:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 7:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
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
        return new c(iG3, strO3, strO2, iG2, iG, zC2, zC, strO);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fS */
    public c[] newArray(int i) {
        return new c[i];
    }
}
