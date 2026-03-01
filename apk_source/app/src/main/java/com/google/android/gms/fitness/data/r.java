package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class r implements Parcelable.Creator<q> {
    static void a(q qVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) qVar.getSession(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, qVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) qVar.iP(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bv, reason: merged with bridge method [inline-methods] */
    public q createFromParcel(Parcel parcel) {
        DataSet dataSet;
        Session session;
        int iG;
        DataSet dataSet2 = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int i = 0;
        Session session2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    Session session3 = (Session) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Session.CREATOR);
                    iG = i;
                    dataSet = dataSet2;
                    session = session3;
                    break;
                case 2:
                    dataSet = (DataSet) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataSet.CREATOR);
                    session = session2;
                    iG = i;
                    break;
                case 1000:
                    DataSet dataSet3 = dataSet2;
                    session = session2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    dataSet = dataSet3;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    dataSet = dataSet2;
                    session = session2;
                    iG = i;
                    break;
            }
            i = iG;
            session2 = session;
            dataSet2 = dataSet;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new q(i, session2, dataSet2);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cM, reason: merged with bridge method [inline-methods] */
    public q[] newArray(int i) {
        return new q[i];
    }
}
