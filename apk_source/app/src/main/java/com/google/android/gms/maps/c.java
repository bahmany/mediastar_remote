package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

/* loaded from: classes.dex */
public class c implements Parcelable.Creator<StreetViewPanoramaOptions> {
    static void a(StreetViewPanoramaOptions streetViewPanoramaOptions, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, streetViewPanoramaOptions.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) streetViewPanoramaOptions.getStreetViewPanoramaCamera(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, streetViewPanoramaOptions.getPanoramaId(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) streetViewPanoramaOptions.getPosition(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, streetViewPanoramaOptions.getRadius(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, streetViewPanoramaOptions.mC());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, streetViewPanoramaOptions.mu());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, streetViewPanoramaOptions.mD());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, streetViewPanoramaOptions.mE());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, streetViewPanoramaOptions.mq());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cH, reason: merged with bridge method [inline-methods] */
    public StreetViewPanoramaOptions createFromParcel(Parcel parcel) {
        Integer numH = null;
        byte bE = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        byte bE2 = 0;
        byte bE3 = 0;
        byte bE4 = 0;
        byte bE5 = 0;
        LatLng latLng = null;
        String strO = null;
        StreetViewPanoramaCamera streetViewPanoramaCamera = null;
        int iG = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    streetViewPanoramaCamera = (StreetViewPanoramaCamera) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, StreetViewPanoramaCamera.CREATOR);
                    break;
                case 3:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    latLng = (LatLng) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, LatLng.CREATOR);
                    break;
                case 5:
                    numH = com.google.android.gms.common.internal.safeparcel.a.h(parcel, iB);
                    break;
                case 6:
                    bE5 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 7:
                    bE4 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 8:
                    bE3 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 9:
                    bE2 = com.google.android.gms.common.internal.safeparcel.a.e(parcel, iB);
                    break;
                case 10:
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
        return new StreetViewPanoramaOptions(iG, streetViewPanoramaCamera, strO, latLng, numH, bE5, bE4, bE3, bE2, bE);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ew, reason: merged with bridge method [inline-methods] */
    public StreetViewPanoramaOptions[] newArray(int i) {
        return new StreetViewPanoramaOptions[i];
    }
}
