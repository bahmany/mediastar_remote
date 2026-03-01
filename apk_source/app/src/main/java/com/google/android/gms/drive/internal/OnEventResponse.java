package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEvent;

/* loaded from: classes.dex */
public class OnEventResponse implements SafeParcelable {
    public static final Parcelable.Creator<OnEventResponse> CREATOR = new am();
    final int BR;
    final int NS;
    final ChangeEvent Pk;
    final CompletionEvent Pl;

    OnEventResponse(int versionCode, int eventType, ChangeEvent changeEvent, CompletionEvent completionEvent) {
        this.BR = versionCode;
        this.NS = eventType;
        this.Pk = changeEvent;
        this.Pl = completionEvent;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DriveEvent ih() {
        switch (this.NS) {
            case 1:
                return this.Pk;
            case 2:
                return this.Pl;
            default:
                throw new IllegalStateException("Unexpected event type " + this.NS);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        am.a(this, dest, flags);
    }
}
