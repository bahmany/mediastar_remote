package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class l implements Parcelable.Creator<MaskedWalletRequest> {
    static void a(MaskedWalletRequest maskedWalletRequest, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, maskedWalletRequest.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, maskedWalletRequest.asr, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, maskedWalletRequest.ate);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, maskedWalletRequest.atf);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, maskedWalletRequest.atg);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, maskedWalletRequest.ath, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, maskedWalletRequest.asl, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, maskedWalletRequest.ati, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, (Parcelable) maskedWalletRequest.asA, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, maskedWalletRequest.atj);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, maskedWalletRequest.atk);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 12, (Parcelable[]) maskedWalletRequest.atl, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 13, maskedWalletRequest.atm);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 14, maskedWalletRequest.atn);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 15, maskedWalletRequest.ato, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dx, reason: merged with bridge method [inline-methods] */
    public MaskedWalletRequest createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO = null;
        boolean zC = false;
        boolean zC2 = false;
        boolean zC3 = false;
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        Cart cart = null;
        boolean zC4 = false;
        boolean zC5 = false;
        CountrySpecification[] countrySpecificationArr = null;
        boolean zC6 = true;
        boolean zC7 = true;
        ArrayList arrayListC = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 4:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 5:
                    zC3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 6:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 8:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 9:
                    cart = (Cart) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Cart.CREATOR);
                    break;
                case 10:
                    zC4 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 11:
                    zC5 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 12:
                    countrySpecificationArr = (CountrySpecification[]) com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB, CountrySpecification.CREATOR);
                    break;
                case 13:
                    zC6 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 14:
                    zC7 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 15:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, com.google.android.gms.identity.intents.model.CountrySpecification.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new MaskedWalletRequest(iG, strO, zC, zC2, zC3, strO2, strO3, strO4, cart, zC4, zC5, countrySpecificationArr, zC6, zC7, arrayListC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fx, reason: merged with bridge method [inline-methods] */
    public MaskedWalletRequest[] newArray(int i) {
        return new MaskedWalletRequest[i];
    }
}
