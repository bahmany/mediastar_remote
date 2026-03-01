package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class az implements Parcelable.Creator<ay> {
    static void a(ay ayVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, ayVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, ayVar.of, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, ayVar.height);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, ayVar.heightPixels);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, ayVar.og);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, ayVar.width);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 7, ayVar.widthPixels);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, (Parcelable[]) ayVar.oh, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: c */
    public ay createFromParcel(Parcel parcel) {
        ay[] ayVarArr = null;
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG2 = 0;
        boolean zC = false;
        int iG3 = 0;
        int iG4 = 0;
        String strO = null;
        int iG5 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG5 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    iG4 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 4:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 6:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 7:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 8:
                    ayVarArr = (ay[]) com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB, ay.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new ay(iG5, strO, iG4, iG3, zC, iG2, iG, ayVarArr);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: f */
    public ay[] newArray(int i) {
        return new ay[i];
    }
}
