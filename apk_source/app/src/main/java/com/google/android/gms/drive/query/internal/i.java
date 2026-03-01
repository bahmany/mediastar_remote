package com.google.android.gms.drive.query.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class i implements Parcelable.Creator<LogicalFilter> {
    static void a(LogicalFilter logicalFilter, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, logicalFilter.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) logicalFilter.QC, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, logicalFilter.QP, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aQ */
    public LogicalFilter createFromParcel(Parcel parcel) {
        ArrayList arrayListC;
        Operator operator;
        int iG;
        ArrayList arrayList = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int i = 0;
        Operator operator2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    Operator operator3 = (Operator) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Operator.CREATOR);
                    iG = i;
                    arrayListC = arrayList;
                    operator = operator3;
                    break;
                case 2:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, FilterHolder.CREATOR);
                    operator = operator2;
                    iG = i;
                    break;
                case 1000:
                    ArrayList arrayList2 = arrayList;
                    operator = operator2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    arrayListC = arrayList2;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    arrayListC = arrayList;
                    operator = operator2;
                    iG = i;
                    break;
            }
            i = iG;
            operator2 = operator;
            arrayList = arrayListC;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new LogicalFilter(i, operator2, arrayList);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cc */
    public LogicalFilter[] newArray(int i) {
        return new LogicalFilter[i];
    }
}
