package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.drive.Contents;

/* loaded from: classes.dex */
public class ai implements Parcelable.Creator<OnContentsResponse> {
    static void a(OnContentsResponse onContentsResponse, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, onContentsResponse.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) onContentsResponse.Op, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, onContentsResponse.Pg);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ak, reason: merged with bridge method [inline-methods] */
    public OnContentsResponse createFromParcel(Parcel parcel) {
        boolean zC;
        Contents contents;
        int iG;
        boolean z = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        Contents contents2 = null;
        int i = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    boolean z2 = z;
                    contents = contents2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    zC = z2;
                    break;
                case 2:
                    Contents contents3 = (Contents) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Contents.CREATOR);
                    iG = i;
                    zC = z;
                    contents = contents3;
                    break;
                case 3:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    contents = contents2;
                    iG = i;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    zC = z;
                    contents = contents2;
                    iG = i;
                    break;
            }
            i = iG;
            contents2 = contents;
            z = zC;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new OnContentsResponse(i, contents2, z);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bw, reason: merged with bridge method [inline-methods] */
    public OnContentsResponse[] newArray(int i) {
        return new OnContentsResponse[i];
    }
}
