package com.google.android.gms.fitness.request;

import android.app.PendingIntent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.location.LocationRequest;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class o implements Parcelable.Creator<n> {
    static void a(n nVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) nVar.getDataSource(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, nVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) nVar.getDataType(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, nVar.jq(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, nVar.Uq);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, nVar.Ur);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, nVar.getSamplingRateMicros());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, nVar.jn());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, (Parcelable) nVar.jl(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, nVar.jm());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 10, nVar.iQ());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 11, nVar.jo(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 12, nVar.jp());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bI */
    public n createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        DataSource dataSource = null;
        DataType dataType = null;
        IBinder iBinderP = null;
        int iG2 = 0;
        int iG3 = 0;
        long jI = 0;
        long jI2 = 0;
        PendingIntent pendingIntent = null;
        long jI3 = 0;
        int iG4 = 0;
        ArrayList arrayListC = null;
        long jI4 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    dataSource = (DataSource) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataSource.CREATOR);
                    break;
                case 2:
                    dataType = (DataType) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataType.CREATOR);
                    break;
                case 3:
                    iBinderP = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 6:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 7:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 8:
                    pendingIntent = (PendingIntent) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, PendingIntent.CREATOR);
                    break;
                case 9:
                    jI3 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 10:
                    iG4 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 11:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, LocationRequest.CREATOR);
                    break;
                case 12:
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
        return new n(iG, dataSource, dataType, iBinderP, iG2, iG3, jI, jI2, pendingIntent, jI3, iG4, arrayListC, jI4);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cZ */
    public n[] newArray(int i) {
        return new n[i];
    }
}
