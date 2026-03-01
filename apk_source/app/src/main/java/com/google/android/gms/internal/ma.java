package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.location.LocationRequest;
import java.util.List;

/* loaded from: classes.dex */
public class ma implements Parcelable.Creator<lz> {
    static void a(lz lzVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) lzVar.Ux, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, lzVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, lzVar.aeX);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, lzVar.aeY);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, lzVar.aeZ);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, lzVar.afa, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, lzVar.mTag, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cv, reason: merged with bridge method [inline-methods] */
    public lz createFromParcel(Parcel parcel) {
        String strO = null;
        boolean zC = true;
        boolean zC2 = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        List<lr> listC = lz.aeW;
        boolean zC3 = true;
        LocationRequest locationRequest = null;
        int iG = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    locationRequest = (LocationRequest) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, LocationRequest.CREATOR);
                    break;
                case 2:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 3:
                    zC3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 4:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 5:
                    listC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, lr.CREATOR);
                    break;
                case 6:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
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
        return new lz(iG, locationRequest, zC2, zC3, zC, listC, strO);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ei, reason: merged with bridge method [inline-methods] */
    public lz[] newArray(int i) {
        return new lz[i];
    }
}
