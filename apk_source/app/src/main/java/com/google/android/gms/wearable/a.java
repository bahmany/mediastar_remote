package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class a implements Parcelable.Creator<Asset> {
    static void a(Asset asset, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, asset.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, asset.getData(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, asset.getDigest(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) asset.auG, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable) asset.uri, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dP */
    public Asset createFromParcel(Parcel parcel) {
        Uri uri = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        ParcelFileDescriptor parcelFileDescriptor = null;
        String strO = null;
        byte[] bArrR = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    bArrR = com.google.android.gms.common.internal.safeparcel.a.r(parcel, iB);
                    break;
                case 3:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    parcelFileDescriptor = (ParcelFileDescriptor) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ParcelFileDescriptor.CREATOR);
                    break;
                case 5:
                    uri = (Uri) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Uri.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new Asset(iG, bArrR, strO, parcelFileDescriptor, uri);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fR */
    public Asset[] newArray(int i) {
        return new Asset[i];
    }
}
