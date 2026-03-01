package com.google.android.gms.drive.realtime.internal.event;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.List;

/* loaded from: classes.dex */
public class ParcelableEvent implements SafeParcelable {
    public static final Parcelable.Creator<ParcelableEvent> CREATOR = new b();
    final int BR;
    final String Re;
    final String Rh;
    final List<String> Rl;
    final boolean Rm;
    final String Rn;
    final TextInsertedDetails Ro;
    final TextDeletedDetails Rp;
    final ValuesAddedDetails Rq;
    final ValuesRemovedDetails Rr;
    final ValuesSetDetails Rs;
    final ValueChangedDetails Rt;
    final ReferenceShiftedDetails Ru;
    final ObjectChangedDetails Rv;
    final String vL;

    ParcelableEvent(int versionCode, String sessionId, String userId, List<String> compoundOperationNames, boolean isLocal, String objectId, String objectType, TextInsertedDetails textInsertedDetails, TextDeletedDetails textDeletedDetails, ValuesAddedDetails valuesAddedDetails, ValuesRemovedDetails valuesRemovedDetails, ValuesSetDetails valuesSetDetails, ValueChangedDetails valueChangedDetails, ReferenceShiftedDetails referenceShiftedDetails, ObjectChangedDetails objectChangedDetails) {
        this.BR = versionCode;
        this.vL = sessionId;
        this.Re = userId;
        this.Rl = compoundOperationNames;
        this.Rm = isLocal;
        this.Rh = objectId;
        this.Rn = objectType;
        this.Ro = textInsertedDetails;
        this.Rp = textDeletedDetails;
        this.Rq = valuesAddedDetails;
        this.Rr = valuesRemovedDetails;
        this.Rs = valuesSetDetails;
        this.Rt = valueChangedDetails;
        this.Ru = referenceShiftedDetails;
        this.Rv = objectChangedDetails;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        b.a(this, dest, flags);
    }
}
