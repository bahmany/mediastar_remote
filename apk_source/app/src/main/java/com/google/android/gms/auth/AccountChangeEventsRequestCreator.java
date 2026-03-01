package com.google.android.gms.auth;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class AccountChangeEventsRequestCreator implements Parcelable.Creator<AccountChangeEventsRequest> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(AccountChangeEventsRequest accountChangeEventsRequest, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.c(parcel, 1, accountChangeEventsRequest.Di);
        b.c(parcel, 2, accountChangeEventsRequest.Dl);
        b.a(parcel, 3, accountChangeEventsRequest.Dd, false);
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public AccountChangeEventsRequest createFromParcel(Parcel parcel) {
        int iG = 0;
        int iC = a.C(parcel);
        String strO = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    iG2 = a.g(parcel, iB);
                    break;
                case 2:
                    iG = a.g(parcel, iB);
                    break;
                case 3:
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
        return new AccountChangeEventsRequest(iG2, iG, strO);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public AccountChangeEventsRequest[] newArray(int size) {
        return new AccountChangeEventsRequest[size];
    }
}
