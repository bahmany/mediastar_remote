package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class s implements Parcelable.Creator<SessionReadRequest> {
    static void a(SessionReadRequest sessionReadRequest, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, sessionReadRequest.ju(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, sessionReadRequest.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, sessionReadRequest.getSessionId(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, sessionReadRequest.getStartTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, sessionReadRequest.getEndTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, sessionReadRequest.getDataTypes(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, sessionReadRequest.getDataSources(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, sessionReadRequest.jv());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, sessionReadRequest.jg());
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 9, sessionReadRequest.jw(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bL */
    public SessionReadRequest createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO = null;
        String strO2 = null;
        long jI = 0;
        long jI2 = 0;
        ArrayList arrayListC = null;
        ArrayList arrayListC2 = null;
        boolean zC = false;
        boolean zC2 = false;
        ArrayList<String> arrayListC3 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 2:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 4:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 5:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataType.CREATOR);
                    break;
                case 6:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataSource.CREATOR);
                    break;
                case 7:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 8:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 9:
                    arrayListC3 = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
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
        return new SessionReadRequest(iG, strO, strO2, jI, jI2, arrayListC, arrayListC2, zC, zC2, arrayListC3);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dd */
    public SessionReadRequest[] newArray(int i) {
        return new SessionReadRequest[i];
    }
}
