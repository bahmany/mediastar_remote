package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class c implements Parcelable.Creator<BleDevice> {
    static void a(BleDevice bleDevice, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, bleDevice.getAddress(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, bleDevice.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, bleDevice.getName(), false);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 3, bleDevice.getSupportedProfiles(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, bleDevice.getDataTypes(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bj */
    public BleDevice createFromParcel(Parcel parcel) {
        ArrayList arrayListC = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        ArrayList<String> arrayListC2 = null;
        String strO = null;
        String strO2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 2:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                case 4:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataType.CREATOR);
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
        return new BleDevice(iG, strO2, strO, arrayListC2, arrayListC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cy */
    public BleDevice[] newArray(int i) {
        return new BleDevice[i];
    }
}
