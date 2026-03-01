package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class d implements Parcelable.Creator<Bucket> {
    static void a(Bucket bucket, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, bucket.getStartTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, bucket.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, bucket.getEndTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) bucket.getSession(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, bucket.getActivity());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, bucket.getDataSets(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, bucket.getBucketType());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, bucket.iB());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bk, reason: merged with bridge method [inline-methods] */
    public Bucket createFromParcel(Parcel parcel) {
        long jI = 0;
        ArrayList arrayListC = null;
        boolean zC = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        int iG2 = 0;
        Session session = null;
        long jI2 = 0;
        int iG3 = 0;
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
                    session = (Session) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Session.CREATOR);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataSet.CREATOR);
                    break;
                case 6:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 7:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 1000:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new Bucket(iG3, jI2, jI, session, iG2, arrayListC, iG, zC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cA, reason: merged with bridge method [inline-methods] */
    public Bucket[] newArray(int i) {
        return new Bucket[i];
    }
}
