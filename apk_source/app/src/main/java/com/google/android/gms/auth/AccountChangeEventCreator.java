package com.google.android.gms.auth;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class AccountChangeEventCreator implements Parcelable.Creator<AccountChangeEvent> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(AccountChangeEvent accountChangeEvent, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.c(parcel, 1, accountChangeEvent.Di);
        b.a(parcel, 2, accountChangeEvent.Dj);
        b.a(parcel, 3, accountChangeEvent.Dd, false);
        b.c(parcel, 4, accountChangeEvent.Dk);
        b.c(parcel, 5, accountChangeEvent.Dl);
        b.a(parcel, 6, accountChangeEvent.Dm, false);
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    public AccountChangeEvent createFromParcel(Parcel parcel) {
        String strO = null;
        int iG = 0;
        int iC = a.C(parcel);
        long jI = 0;
        int iG2 = 0;
        String strO2 = null;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    iG3 = a.g(parcel, iB);
                    break;
                case 2:
                    jI = a.i(parcel, iB);
                    break;
                case 3:
                    strO2 = a.o(parcel, iB);
                    break;
                case 4:
                    iG2 = a.g(parcel, iB);
                    break;
                case 5:
                    iG = a.g(parcel, iB);
                    break;
                case 6:
                    strO = a.o(parcel, iB);
                    break;
                default:
                    a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new AccountChangeEvent(iG3, jI, strO2, iG2, iG, strO);
    }

    @Override // android.os.Parcelable.Creator
    public AccountChangeEvent[] newArray(int size) {
        return new AccountChangeEvent[size];
    }
}
