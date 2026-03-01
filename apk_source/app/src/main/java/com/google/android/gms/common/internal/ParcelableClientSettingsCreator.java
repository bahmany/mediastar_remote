package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ParcelableClientSettingsCreator implements Parcelable.Creator<ClientSettings.ParcelableClientSettings> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(ClientSettings.ParcelableClientSettings parcelableClientSettings, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, parcelableClientSettings.getAccountName(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, parcelableClientSettings.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 2, parcelableClientSettings.getScopes(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, parcelableClientSettings.getGravityForPopups());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, parcelableClientSettings.getRealClientPackageName(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public ClientSettings.ParcelableClientSettings createFromParcel(Parcel parcel) {
        int iG = 0;
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        ArrayList<String> arrayListC = null;
        String strO2 = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 2:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                case 3:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 4:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 1000:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new ClientSettings.ParcelableClientSettings(iG2, strO2, arrayListC, iG, strO);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public ClientSettings.ParcelableClientSettings[] newArray(int size) {
        return new ClientSettings.ParcelableClientSettings[size];
    }
}
