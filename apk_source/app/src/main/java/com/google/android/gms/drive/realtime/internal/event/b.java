package com.google.android.gms.drive.realtime.internal.event;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class b implements Parcelable.Creator<ParcelableEvent> {
    static void a(ParcelableEvent parcelableEvent, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, parcelableEvent.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, parcelableEvent.vL, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, parcelableEvent.Re, false);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 4, parcelableEvent.Rl, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, parcelableEvent.Rm);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, parcelableEvent.Rh, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, parcelableEvent.Rn, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, (Parcelable) parcelableEvent.Ro, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, (Parcelable) parcelableEvent.Rp, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, (Parcelable) parcelableEvent.Rq, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, (Parcelable) parcelableEvent.Rr, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 12, (Parcelable) parcelableEvent.Rs, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 13, (Parcelable) parcelableEvent.Rt, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 14, (Parcelable) parcelableEvent.Ru, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 15, (Parcelable) parcelableEvent.Rv, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aZ, reason: merged with bridge method [inline-methods] */
    public ParcelableEvent createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO = null;
        String strO2 = null;
        ArrayList<String> arrayListC = null;
        boolean zC = false;
        String strO3 = null;
        String strO4 = null;
        TextInsertedDetails textInsertedDetails = null;
        TextDeletedDetails textDeletedDetails = null;
        ValuesAddedDetails valuesAddedDetails = null;
        ValuesRemovedDetails valuesRemovedDetails = null;
        ValuesSetDetails valuesSetDetails = null;
        ValueChangedDetails valueChangedDetails = null;
        ReferenceShiftedDetails referenceShiftedDetails = null;
        ObjectChangedDetails objectChangedDetails = null;
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
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                case 5:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 6:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 8:
                    textInsertedDetails = (TextInsertedDetails) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, TextInsertedDetails.CREATOR);
                    break;
                case 9:
                    textDeletedDetails = (TextDeletedDetails) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, TextDeletedDetails.CREATOR);
                    break;
                case 10:
                    valuesAddedDetails = (ValuesAddedDetails) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ValuesAddedDetails.CREATOR);
                    break;
                case 11:
                    valuesRemovedDetails = (ValuesRemovedDetails) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ValuesRemovedDetails.CREATOR);
                    break;
                case 12:
                    valuesSetDetails = (ValuesSetDetails) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ValuesSetDetails.CREATOR);
                    break;
                case 13:
                    valueChangedDetails = (ValueChangedDetails) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ValueChangedDetails.CREATOR);
                    break;
                case 14:
                    referenceShiftedDetails = (ReferenceShiftedDetails) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ReferenceShiftedDetails.CREATOR);
                    break;
                case 15:
                    objectChangedDetails = (ObjectChangedDetails) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ObjectChangedDetails.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new ParcelableEvent(iG, strO, strO2, arrayListC, zC, strO3, strO4, textInsertedDetails, textDeletedDetails, valuesAddedDetails, valuesRemovedDetails, valuesSetDetails, valueChangedDetails, referenceShiftedDetails, objectChangedDetails);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cm, reason: merged with bridge method [inline-methods] */
    public ParcelableEvent[] newArray(int i) {
        return new ParcelableEvent[i];
    }
}
