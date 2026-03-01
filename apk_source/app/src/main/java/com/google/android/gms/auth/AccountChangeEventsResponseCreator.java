package com.google.android.gms.auth;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class AccountChangeEventsResponseCreator implements Parcelable.Creator<AccountChangeEventsResponse> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(AccountChangeEventsResponse accountChangeEventsResponse, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.c(parcel, 1, accountChangeEventsResponse.Di);
        b.c(parcel, 2, accountChangeEventsResponse.me, false);
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public AccountChangeEventsResponse createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        ArrayList arrayListC = null;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    iG = a.g(parcel, iB);
                    break;
                case 2:
                    arrayListC = a.c(parcel, iB, AccountChangeEvent.CREATOR);
                    break;
                default:
                    a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new AccountChangeEventsResponse(iG, arrayListC);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public AccountChangeEventsResponse[] newArray(int size) {
        return new AccountChangeEventsResponse[size];
    }
}
