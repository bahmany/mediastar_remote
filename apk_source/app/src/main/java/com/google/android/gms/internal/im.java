package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class im implements Parcelable.Creator<il> {
    static void a(il ilVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, ilVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, ilVar.fF());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, ilVar.fN());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, ilVar.fO());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable) ilVar.getApplicationMetadata(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, ilVar.fP());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ah */
    public il[] newArray(int i) {
        return new il[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: x */
    public il createFromParcel(Parcel parcel) {
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        double dM = 0.0d;
        ApplicationMetadata applicationMetadata = null;
        int iG2 = 0;
        boolean zC = false;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    dM = com.google.android.gms.common.internal.safeparcel.a.m(parcel, iB);
                    break;
                case 3:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    applicationMetadata = (ApplicationMetadata) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ApplicationMetadata.CREATOR);
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
        return new il(iG3, dM, zC, iG2, applicationMetadata, iG);
    }
}
