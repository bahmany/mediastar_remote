package com.google.android.gms.common.data;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class b implements Parcelable.Creator<a> {
    static void a(a aVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, aVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) aVar.JK, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, aVar.FD);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ao, reason: merged with bridge method [inline-methods] */
    public a[] newArray(int i) {
        return new a[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: y, reason: merged with bridge method [inline-methods] */
    public a createFromParcel(Parcel parcel) {
        int iG;
        ParcelFileDescriptor parcelFileDescriptor;
        int iG2;
        int i = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        ParcelFileDescriptor parcelFileDescriptor2 = null;
        int i2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    int i3 = i;
                    parcelFileDescriptor = parcelFileDescriptor2;
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    iG = i3;
                    break;
                case 2:
                    ParcelFileDescriptor parcelFileDescriptor3 = (ParcelFileDescriptor) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ParcelFileDescriptor.CREATOR);
                    iG2 = i2;
                    iG = i;
                    parcelFileDescriptor = parcelFileDescriptor3;
                    break;
                case 3:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    parcelFileDescriptor = parcelFileDescriptor2;
                    iG2 = i2;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    iG = i;
                    parcelFileDescriptor = parcelFileDescriptor2;
                    iG2 = i2;
                    break;
            }
            i2 = iG2;
            parcelFileDescriptor2 = parcelFileDescriptor;
            i = iG;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new a(i2, parcelFileDescriptor2, i);
    }
}
