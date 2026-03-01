package com.google.android.gms.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class hf implements Parcelable.Creator<he> {
    static void a(he heVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable[]) heVar.BS, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, heVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, heVar.BT, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, heVar.BU);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) heVar.account, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: I, reason: merged with bridge method [inline-methods] */
    public he[] newArray(int i) {
        return new he[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: l, reason: merged with bridge method [inline-methods] */
    public he createFromParcel(Parcel parcel) {
        boolean zC = false;
        Account account = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        String strO = null;
        hi[] hiVarArr = null;
        int iG = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    hiVarArr = (hi[]) com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB, hi.CREATOR);
                    break;
                case 2:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 4:
                    account = (Account) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Account.CREATOR);
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
        return new he(iG, hiVarArr, strO, zC, account);
    }
}
