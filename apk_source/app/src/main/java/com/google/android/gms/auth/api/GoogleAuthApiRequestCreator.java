package com.google.android.gms.auth.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GoogleAuthApiRequestCreator implements Parcelable.Creator<GoogleAuthApiRequest> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(GoogleAuthApiRequest googleAuthApiRequest, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, googleAuthApiRequest.name, false);
        b.c(parcel, 1000, googleAuthApiRequest.versionCode);
        b.a(parcel, 2, googleAuthApiRequest.version, false);
        b.a(parcel, 3, googleAuthApiRequest.Dt, false);
        b.a(parcel, 4, googleAuthApiRequest.yR, false);
        b.a(parcel, 5, googleAuthApiRequest.Du, false);
        b.a(parcel, 6, googleAuthApiRequest.Dv, false);
        b.b(parcel, 7, googleAuthApiRequest.Dw, false);
        b.a(parcel, 8, googleAuthApiRequest.Dx, false);
        b.c(parcel, 9, googleAuthApiRequest.Dy);
        b.a(parcel, 10, googleAuthApiRequest.Dz, false);
        b.a(parcel, 11, googleAuthApiRequest.DA, false);
        b.a(parcel, 12, googleAuthApiRequest.DB);
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public GoogleAuthApiRequest createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        String strO = null;
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        Bundle bundleQ = null;
        String strO5 = null;
        ArrayList<String> arrayListC = null;
        String strO6 = null;
        int iG2 = 0;
        Bundle bundleQ2 = null;
        byte[] bArrR = null;
        long jI = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    strO = a.o(parcel, iB);
                    break;
                case 2:
                    strO2 = a.o(parcel, iB);
                    break;
                case 3:
                    strO3 = a.o(parcel, iB);
                    break;
                case 4:
                    strO4 = a.o(parcel, iB);
                    break;
                case 5:
                    bundleQ = a.q(parcel, iB);
                    break;
                case 6:
                    strO5 = a.o(parcel, iB);
                    break;
                case 7:
                    arrayListC = a.C(parcel, iB);
                    break;
                case 8:
                    strO6 = a.o(parcel, iB);
                    break;
                case 9:
                    iG2 = a.g(parcel, iB);
                    break;
                case 10:
                    bundleQ2 = a.q(parcel, iB);
                    break;
                case 11:
                    bArrR = a.r(parcel, iB);
                    break;
                case 12:
                    jI = a.i(parcel, iB);
                    break;
                case 1000:
                    iG = a.g(parcel, iB);
                    break;
                default:
                    a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new GoogleAuthApiRequest(iG, strO, strO2, strO3, strO4, bundleQ, strO5, arrayListC, strO6, iG2, bundleQ2, bArrR, jI);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public GoogleAuthApiRequest[] newArray(int size) {
        return new GoogleAuthApiRequest[size];
    }
}
