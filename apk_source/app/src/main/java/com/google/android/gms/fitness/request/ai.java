package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;

/* loaded from: classes.dex */
public class ai implements Parcelable.Creator<ah> {
    static void a(ah ahVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) ahVar.getDataType(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, ahVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) ahVar.getDataSource(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bU */
    public ah createFromParcel(Parcel parcel) {
        DataSource dataSource;
        DataType dataType;
        int iG;
        DataSource dataSource2 = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int i = 0;
        DataType dataType2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    DataType dataType3 = (DataType) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataType.CREATOR);
                    iG = i;
                    dataSource = dataSource2;
                    dataType = dataType3;
                    break;
                case 2:
                    dataSource = (DataSource) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataSource.CREATOR);
                    dataType = dataType2;
                    iG = i;
                    break;
                case 1000:
                    DataSource dataSource3 = dataSource2;
                    dataType = dataType2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    dataSource = dataSource3;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    dataSource = dataSource2;
                    dataType = dataType2;
                    iG = i;
                    break;
            }
            i = iG;
            dataType2 = dataType;
            dataSource2 = dataSource;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new ah(i, dataType2, dataSource2);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dm */
    public ah[] newArray(int i) {
        return new ah[i];
    }
}
