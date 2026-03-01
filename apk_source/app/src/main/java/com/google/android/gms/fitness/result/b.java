package com.google.android.gms.fitness.result;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class b implements Parcelable.Creator<DataReadResult> {
    static void a(DataReadResult dataReadResult, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.d(parcel, 1, dataReadResult.jH(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, dataReadResult.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) dataReadResult.getStatus(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.d(parcel, 3, dataReadResult.jG(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, dataReadResult.jF());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, dataReadResult.iG(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 7, dataReadResult.jI(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bW */
    public DataReadResult createFromParcel(Parcel parcel) {
        int iG = 0;
        ArrayList arrayListC = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayListC2 = null;
        Status status = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, arrayList, getClass().getClassLoader());
                    break;
                case 2:
                    status = (Status) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Status.CREATOR);
                    break;
                case 3:
                    com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, arrayList2, getClass().getClassLoader());
                    break;
                case 5:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 6:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataSource.CREATOR);
                    break;
                case 7:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataType.CREATOR);
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
        return new DataReadResult(iG2, arrayList, status, arrayList2, iG, arrayListC2, arrayListC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: do */
    public DataReadResult[] newArray(int i) {
        return new DataReadResult[i];
    }
}
