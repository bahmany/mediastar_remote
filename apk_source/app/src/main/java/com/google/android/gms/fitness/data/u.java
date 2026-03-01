package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class u implements Parcelable.Creator<Value> {
    static void a(Value value, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, value.getFormat());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, value.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, value.isSet());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, value.iS());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bx */
    public Value createFromParcel(Parcel parcel) {
        boolean zC = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        float fL = 0.0f;
        int iG = 0;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 3:
                    fL = com.google.android.gms.common.internal.safeparcel.a.l(parcel, iB);
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
        return new Value(iG2, iG, zC, fL);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cO */
    public Value[] newArray(int i) {
        return new Value[i];
    }
}
