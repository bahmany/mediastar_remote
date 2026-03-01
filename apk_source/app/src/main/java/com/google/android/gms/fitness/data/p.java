package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class p implements Parcelable.Creator<Session> {
    static void a(Session session, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, session.getStartTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, session.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, session.getEndTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, session.getName(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, session.getIdentifier(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, session.getDescription(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 7, session.getActivity());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, (Parcelable) session.iH(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bu, reason: merged with bridge method [inline-methods] */
    public Session createFromParcel(Parcel parcel) {
        long jI = 0;
        int iG = 0;
        a aVar = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        String strO = null;
        String strO2 = null;
        String strO3 = null;
        long jI2 = 0;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 2:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 3:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 5:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 8:
                    aVar = (a) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, a.CREATOR);
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
        return new Session(iG2, jI2, jI, strO3, strO2, strO, iG, aVar);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cL, reason: merged with bridge method [inline-methods] */
    public Session[] newArray(int i) {
        return new Session[i];
    }
}
