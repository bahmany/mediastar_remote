package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.maps.model.CameraPosition;

/* loaded from: classes.dex */
public class a implements Parcelable.Creator<GoogleMapOptions> {
    static void a(GoogleMapOptions googleMapOptions, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, googleMapOptions.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, googleMapOptions.mp());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, googleMapOptions.mq());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, googleMapOptions.getMapType());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable) googleMapOptions.getCamera(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, googleMapOptions.mr());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, googleMapOptions.ms());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, googleMapOptions.mt());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, googleMapOptions.mu());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, googleMapOptions.mv());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, googleMapOptions.mw());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cG */
    public GoogleMapOptions createFromParcel(Parcel parcel) {
        byte bE = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        CameraPosition cameraPosition = null;
        byte bE2 = 0;
        byte bE3 = 0;
        byte bE4 = 0;
        byte bE5 = 0;
        byte bE6 = 0;
        int iG = 0;
        byte bE7 = 0;
        byte bE8 = 0;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    bE8 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 3:
                    bE7 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 4:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    cameraPosition = (CameraPosition) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, CameraPosition.CREATOR);
                    break;
                case 6:
                    bE6 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 7:
                    bE5 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 8:
                    bE4 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 9:
                    bE3 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 10:
                    bE2 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 11:
                    bE = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new GoogleMapOptions(iG2, bE8, bE7, iG, cameraPosition, bE6, bE5, bE4, bE3, bE2, bE);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ev */
    public GoogleMapOptions[] newArray(int i) {
        return new GoogleMapOptions[i];
    }
}
