package com.google.android.gms.drive.metadata.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.drive.metadata.CustomPropertyKey;

/* loaded from: classes.dex */
public class c implements Parcelable.Creator<CustomProperty> {
    static void a(CustomProperty customProperty, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, customProperty.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) customProperty.PB, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, customProperty.mValue, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aG, reason: merged with bridge method [inline-methods] */
    public CustomProperty createFromParcel(Parcel parcel) {
        String strO;
        CustomPropertyKey customPropertyKey;
        int iG;
        String str = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int i = 0;
        CustomPropertyKey customPropertyKey2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    String str2 = str;
                    customPropertyKey = customPropertyKey2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    strO = str2;
                    break;
                case 2:
                    CustomPropertyKey customPropertyKey3 = (CustomPropertyKey) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, CustomPropertyKey.CREATOR);
                    iG = i;
                    strO = str;
                    customPropertyKey = customPropertyKey3;
                    break;
                case 3:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    customPropertyKey = customPropertyKey2;
                    iG = i;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    strO = str;
                    customPropertyKey = customPropertyKey2;
                    iG = i;
                    break;
            }
            i = iG;
            customPropertyKey2 = customPropertyKey;
            str = strO;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new CustomProperty(i, customPropertyKey2, str);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bS, reason: merged with bridge method [inline-methods] */
    public CustomProperty[] newArray(int i) {
        return new CustomProperty[i];
    }
}
