package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class g implements Parcelable.Creator<DataSource> {
    static void a(DataSource dataSource, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) dataSource.getDataType(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, dataSource.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, dataSource.getName(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, dataSource.getType());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) dataSource.getDevice(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable) dataSource.iH(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, dataSource.getStreamName(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, dataSource.iJ());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bn, reason: merged with bridge method [inline-methods] */
    public DataSource createFromParcel(Parcel parcel) {
        boolean zC = false;
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        a aVar = null;
        Device device = null;
        int iG = 0;
        String strO2 = null;
        DataType dataType = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    dataType = (DataType) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataType.CREATOR);
                    break;
                case 2:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 4:
                    device = (Device) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Device.CREATOR);
                    break;
                case 5:
                    aVar = (a) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, a.CREATOR);
                    break;
                case 6:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
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
        return new DataSource(iG2, dataType, strO2, iG, device, aVar, strO, zC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cE, reason: merged with bridge method [inline-methods] */
    public DataSource[] newArray(int i) {
        return new DataSource[i];
    }
}
