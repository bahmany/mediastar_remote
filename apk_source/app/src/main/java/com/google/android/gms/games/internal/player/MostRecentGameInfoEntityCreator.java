package com.google.android.gms.games.internal.player;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class MostRecentGameInfoEntityCreator implements Parcelable.Creator<MostRecentGameInfoEntity> {
    static void a(MostRecentGameInfoEntity mostRecentGameInfoEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, mostRecentGameInfoEntity.ln(), false);
        b.c(parcel, 1000, mostRecentGameInfoEntity.getVersionCode());
        b.a(parcel, 2, mostRecentGameInfoEntity.lo(), false);
        b.a(parcel, 3, mostRecentGameInfoEntity.lp());
        b.a(parcel, 4, (Parcelable) mostRecentGameInfoEntity.lq(), i, false);
        b.a(parcel, 5, (Parcelable) mostRecentGameInfoEntity.lr(), i, false);
        b.a(parcel, 6, (Parcelable) mostRecentGameInfoEntity.ls(), i, false);
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cj */
    public MostRecentGameInfoEntity createFromParcel(Parcel parcel) {
        Uri uri = null;
        int iC = a.C(parcel);
        int iG = 0;
        long jI = 0;
        Uri uri2 = null;
        Uri uri3 = null;
        String strO = null;
        String strO2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    strO2 = a.o(parcel, iB);
                    break;
                case 2:
                    strO = a.o(parcel, iB);
                    break;
                case 3:
                    jI = a.i(parcel, iB);
                    break;
                case 4:
                    uri3 = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 5:
                    uri2 = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 6:
                    uri = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 1000:
                    iG = a.g(parcel, iB);
                    break;
                default:
                    a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new MostRecentGameInfoEntity(iG, strO2, strO, jI, uri3, uri2, uri);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dP */
    public MostRecentGameInfoEntity[] newArray(int i) {
        return new MostRecentGameInfoEntity[i];
    }
}
