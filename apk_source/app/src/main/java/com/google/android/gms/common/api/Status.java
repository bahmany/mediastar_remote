package com.google.android.gms.common.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender;
import android.os.Parcel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class Status implements Result, SafeParcelable {
    private final int BR;
    private final int HF;
    private final String Jt;
    private final PendingIntent mPendingIntent;
    public static final Status Jo = new Status(0);
    public static final Status Jp = new Status(14);
    public static final Status Jq = new Status(8);
    public static final Status Jr = new Status(15);
    public static final Status Js = new Status(16);
    public static final StatusCreator CREATOR = new StatusCreator();

    public Status(int statusCode) {
        this(1, statusCode, null, null);
    }

    Status(int versionCode, int statusCode, String statusMessage, PendingIntent pendingIntent) {
        this.BR = versionCode;
        this.HF = statusCode;
        this.Jt = statusMessage;
        this.mPendingIntent = pendingIntent;
    }

    public Status(int statusCode, String statusMessage, PendingIntent pendingIntent) {
        this(1, statusCode, statusMessage, pendingIntent);
    }

    private String fY() {
        return this.Jt != null ? this.Jt : CommonStatusCodes.getStatusCodeString(this.HF);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Status)) {
            return false;
        }
        Status status = (Status) obj;
        return this.BR == status.BR && this.HF == status.HF && m.equal(this.Jt, status.Jt) && m.equal(this.mPendingIntent, status.mPendingIntent);
    }

    PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public PendingIntent getResolution() {
        return this.mPendingIntent;
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this;
    }

    public int getStatusCode() {
        return this.HF;
    }

    public String getStatusMessage() {
        return this.Jt;
    }

    int getVersionCode() {
        return this.BR;
    }

    @Deprecated
    public ConnectionResult gu() {
        return new ConnectionResult(this.HF, this.mPendingIntent);
    }

    public boolean hasResolution() {
        return this.mPendingIntent != null;
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.BR), Integer.valueOf(this.HF), this.Jt, this.mPendingIntent);
    }

    public boolean isCanceled() {
        return this.HF == 16;
    }

    public boolean isInterrupted() {
        return this.HF == 14;
    }

    public boolean isSuccess() {
        return this.HF <= 0;
    }

    public void startResolutionForResult(Activity activity, int requestCode) throws IntentSender.SendIntentException {
        if (hasResolution()) {
            activity.startIntentSenderForResult(this.mPendingIntent.getIntentSender(), requestCode, null, 0, 0, 0);
        }
    }

    public String toString() {
        return m.h(this).a("statusCode", fY()).a("resolution", this.mPendingIntent).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        StatusCreator.a(this, out, flags);
    }
}
