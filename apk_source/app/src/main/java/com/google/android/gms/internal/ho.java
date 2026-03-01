package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.internal.hm;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ho implements Parcelable.Creator<hm.b> {
    static void a(hm.b bVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, bVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) bVar.Ck, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, bVar.Cl, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: N */
    public hm.b[] newArray(int i) {
        return new hm.b[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: q */
    public hm.b createFromParcel(Parcel parcel) {
        ArrayList arrayListC;
        Status status;
        int iG;
        ArrayList arrayList = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int i = 0;
        Status status2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    Status status3 = (Status) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Status.CREATOR);
                    iG = i;
                    arrayListC = arrayList;
                    status = status3;
                    break;
                case 2:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, hs.CREATOR);
                    status = status2;
                    iG = i;
                    break;
                case 1000:
                    ArrayList arrayList2 = arrayList;
                    status = status2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    arrayListC = arrayList2;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    arrayListC = arrayList;
                    status = status2;
                    iG = i;
                    break;
            }
            i = iG;
            status2 = status;
            arrayList = arrayListC;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new hm.b(i, status2, arrayList);
    }
}
