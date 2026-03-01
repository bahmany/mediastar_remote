package com.google.android.gms.auth.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class GoogleAuthApiResponseCreator implements Parcelable.Creator<GoogleAuthApiResponse> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(GoogleAuthApiResponse googleAuthApiResponse, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.c(parcel, 1, googleAuthApiResponse.responseCode);
        b.c(parcel, 1000, googleAuthApiResponse.versionCode);
        b.a(parcel, 2, googleAuthApiResponse.Dz, false);
        b.a(parcel, 3, googleAuthApiResponse.DA, false);
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public GoogleAuthApiResponse createFromParcel(Parcel parcel) {
        byte[] bArrR = null;
        int iG = 0;
        int iC = a.C(parcel);
        Bundle bundleQ = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    iG = a.g(parcel, iB);
                    break;
                case 2:
                    bundleQ = a.q(parcel, iB);
                    break;
                case 3:
                    bArrR = a.r(parcel, iB);
                    break;
                case 1000:
                    iG2 = a.g(parcel, iB);
                    break;
                default:
                    a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new GoogleAuthApiResponse(iG2, iG, bundleQ, bArrR);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public GoogleAuthApiResponse[] newArray(int size) {
        return new GoogleAuthApiResponse[size];
    }
}
