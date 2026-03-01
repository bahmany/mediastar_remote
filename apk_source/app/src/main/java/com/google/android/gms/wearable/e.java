package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class e implements Parcelable.Creator<PutDataRequest> {
    static void a(PutDataRequest putDataRequest, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, putDataRequest.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) putDataRequest.getUri(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, putDataRequest.pR(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, putDataRequest.getData(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dR */
    public PutDataRequest createFromParcel(Parcel parcel) {
        byte[] bArrR;
        Bundle bundleQ;
        Uri uri;
        int iG;
        byte[] bArr = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int i = 0;
        Bundle bundle = null;
        Uri uri2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    byte[] bArr2 = bArr;
                    bundleQ = bundle;
                    uri = uri2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    bArrR = bArr2;
                    break;
                case 2:
                    iG = i;
                    Bundle bundle2 = bundle;
                    uri = (Uri) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Uri.CREATOR);
                    bArrR = bArr;
                    bundleQ = bundle2;
                    break;
                case 3:
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    bArrR = bArr;
                    bundleQ = bundle;
                    uri = uri2;
                    iG = i;
                    break;
                case 4:
                    uri = uri2;
                    iG = i;
                    byte[] bArr3 = bArr;
                    bundleQ = com.google.android.gms.common.internal.safeparcel.a.q(parcel, iB);
                    bArrR = bArr3;
                    break;
                case 5:
                    bArrR = com.google.android.gms.common.internal.safeparcel.a.r(parcel, iB);
                    bundleQ = bundle;
                    uri = uri2;
                    iG = i;
                    break;
            }
            i = iG;
            uri2 = uri;
            bundle = bundleQ;
            bArr = bArrR;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new PutDataRequest(i, uri2, bundle, bArr);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fT */
    public PutDataRequest[] newArray(int i) {
        return new PutDataRequest[i];
    }
}
