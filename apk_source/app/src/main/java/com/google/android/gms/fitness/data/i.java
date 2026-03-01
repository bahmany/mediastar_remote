package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class i implements Parcelable.Creator<Device> {
    static void a(Device device, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, device.getManufacturer(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, device.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, device.getModel(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, device.getVersion(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, device.iN(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, device.getType());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bp */
    public Device createFromParcel(Parcel parcel) {
        int iG = 0;
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 2:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 5:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 1000:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new Device(iG2, strO4, strO3, strO2, strO, iG);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cG */
    public Device[] newArray(int i) {
        return new Device[i];
    }
}
