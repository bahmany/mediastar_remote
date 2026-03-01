package com.google.android.gms.drive.query;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.drive.query.internal.LogicalFilter;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class a implements Parcelable.Creator<Query> {
    static void a(Query query, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, query.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) query.Qt, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, query.Qu, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) query.Qv, i, false);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 5, query.Qw, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aI, reason: merged with bridge method [inline-methods] */
    public Query createFromParcel(Parcel parcel) {
        ArrayList<String> arrayListC = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        SortOrder sortOrder = null;
        String strO = null;
        LogicalFilter logicalFilter = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    logicalFilter = (LogicalFilter) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, LogicalFilter.CREATOR);
                    break;
                case 3:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    sortOrder = (SortOrder) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, SortOrder.CREATOR);
                    break;
                case 5:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
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
        return new Query(iG, logicalFilter, strO, sortOrder, arrayListC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bU, reason: merged with bridge method [inline-methods] */
    public Query[] newArray(int i) {
        return new Query[i];
    }
}
