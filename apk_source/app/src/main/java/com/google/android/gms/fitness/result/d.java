package com.google.android.gms.fitness.result;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.fitness.data.DataType;

/* loaded from: classes.dex */
public class d implements Parcelable.Creator<DataTypeResult> {
    static void a(DataTypeResult dataTypeResult, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) dataTypeResult.getStatus(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, dataTypeResult.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) dataTypeResult.getDataType(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bY */
    public DataTypeResult createFromParcel(Parcel parcel) {
        DataType dataType;
        Status status;
        int iG;
        DataType dataType2 = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int i = 0;
        Status status2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    Status status3 = (Status) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Status.CREATOR);
                    iG = i;
                    dataType = dataType2;
                    status = status3;
                    break;
                case 3:
                    dataType = (DataType) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataType.CREATOR);
                    status = status2;
                    iG = i;
                    break;
                case 1000:
                    DataType dataType3 = dataType2;
                    status = status2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    dataType = dataType3;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    dataType = dataType2;
                    status = status2;
                    iG = i;
                    break;
            }
            i = iG;
            status2 = status;
            dataType2 = dataType;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new DataTypeResult(i, status2, dataType2);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dq */
    public DataTypeResult[] newArray(int i) {
        return new DataTypeResult[i];
    }
}
