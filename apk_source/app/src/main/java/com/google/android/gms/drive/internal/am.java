package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.CompletionEvent;

/* loaded from: classes.dex */
public class am implements Parcelable.Creator<OnEventResponse> {
    static void a(OnEventResponse onEventResponse, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, onEventResponse.BR);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, onEventResponse.NS);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) onEventResponse.Pk, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable) onEventResponse.Pl, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ao */
    public OnEventResponse createFromParcel(Parcel parcel) {
        CompletionEvent completionEvent;
        ChangeEvent changeEvent;
        int iG;
        int iG2;
        CompletionEvent completionEvent2 = null;
        int i = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        ChangeEvent changeEvent2 = null;
        int i2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    CompletionEvent completionEvent3 = completionEvent2;
                    changeEvent = changeEvent2;
                    iG = i;
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    completionEvent = completionEvent3;
                    break;
                case 2:
                    iG2 = i2;
                    ChangeEvent changeEvent3 = changeEvent2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    completionEvent = completionEvent2;
                    changeEvent = changeEvent3;
                    break;
                case 3:
                    iG = i;
                    iG2 = i2;
                    CompletionEvent completionEvent4 = completionEvent2;
                    changeEvent = (ChangeEvent) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ChangeEvent.CREATOR);
                    completionEvent = completionEvent4;
                    break;
                case 4:
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    completionEvent = completionEvent2;
                    changeEvent = changeEvent2;
                    iG = i;
                    iG2 = i2;
                    break;
                case 5:
                    completionEvent = (CompletionEvent) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, CompletionEvent.CREATOR);
                    changeEvent = changeEvent2;
                    iG = i;
                    iG2 = i2;
                    break;
            }
            i2 = iG2;
            i = iG;
            changeEvent2 = changeEvent;
            completionEvent2 = completionEvent;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new OnEventResponse(i2, i, changeEvent2, completionEvent2);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bA */
    public OnEventResponse[] newArray(int i) {
        return new OnEventResponse[i];
    }
}
