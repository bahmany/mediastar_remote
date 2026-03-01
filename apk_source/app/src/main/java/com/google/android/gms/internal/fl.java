package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class fl implements Parcelable.Creator<fk> {
    static void a(fk fkVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, fkVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, fkVar.rP, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, fkVar.tG, false);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 4, fkVar.qf, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, fkVar.errorCode);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 6, fkVar.qg, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, fkVar.tH);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, fkVar.tI);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, fkVar.tJ);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 10, fkVar.tK, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, fkVar.qj);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 12, fkVar.orientation);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 13, fkVar.tL, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 14, fkVar.tM);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 15, fkVar.tN, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 19, fkVar.tP, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 18, fkVar.tO);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 21, fkVar.tQ, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 23, fkVar.tS);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 22, fkVar.tR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 25, fkVar.tT);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 24, fkVar.tF);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: i, reason: merged with bridge method [inline-methods] */
    public fk createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO = null;
        String strO2 = null;
        ArrayList<String> arrayListC = null;
        int iG2 = 0;
        ArrayList<String> arrayListC2 = null;
        long jI = 0;
        boolean zC = false;
        long jI2 = 0;
        ArrayList<String> arrayListC3 = null;
        long jI3 = 0;
        int iG3 = 0;
        String strO3 = null;
        long jI4 = 0;
        String strO4 = null;
        boolean zC2 = false;
        String strO5 = null;
        String strO6 = null;
        boolean zC3 = false;
        boolean zC4 = false;
        boolean zC5 = false;
        boolean zC6 = false;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                case 5:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 6:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                case 7:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 8:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 9:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 10:
                    arrayListC3 = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                case 11:
                    jI3 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 12:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 13:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 14:
                    jI4 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 15:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 16:
                case 17:
                case 20:
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
                case 18:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 19:
                    strO5 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 21:
                    strO6 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 22:
                    zC3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 23:
                    zC4 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 24:
                    zC5 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 25:
                    zC6 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new fk(iG, strO, strO2, arrayListC, iG2, arrayListC2, jI, zC, jI2, arrayListC3, jI3, iG3, strO3, jI4, strO4, zC2, strO5, strO6, zC3, zC4, zC5, zC6);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: q, reason: merged with bridge method [inline-methods] */
    public fk[] newArray(int i) {
        return new fk[i];
    }
}
