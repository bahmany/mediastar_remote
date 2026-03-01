package com.google.android.gms.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class b implements Parcelable.Creator<LocationRequest> {
    static void a(LocationRequest locationRequest, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, locationRequest.mPriority);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, locationRequest.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, locationRequest.aeh);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, locationRequest.aei);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, locationRequest.Uz);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, locationRequest.adX);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, locationRequest.aej);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, locationRequest.aek);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, locationRequest.ael);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cs */
    public LocationRequest createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        int iG2 = 102;
        long jI = 3600000;
        long jI2 = 600000;
        boolean zC = false;
        long jI3 = Long.MAX_VALUE;
        int iG3 = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        float fL = 0.0f;
        long jI4 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 3:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 4:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 5:
                    jI3 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 6:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 7:
                    fL = com.google.android.gms.common.internal.safeparcel.a.l(parcel, iB);
                    break;
                case 8:
                    jI4 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
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
        return new LocationRequest(iG, iG2, jI, jI2, zC, jI3, iG3, fL, jI4);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ec */
    public LocationRequest[] newArray(int i) {
        return new LocationRequest[i];
    }
}
