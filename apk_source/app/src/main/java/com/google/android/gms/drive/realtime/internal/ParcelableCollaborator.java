package com.google.android.gms.drive.realtime.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class ParcelableCollaborator implements SafeParcelable {
    public static final Parcelable.Creator<ParcelableCollaborator> CREATOR = new p();
    final int BR;
    final String Nz;
    final boolean Rc;
    final boolean Rd;
    final String Re;
    final String Rf;
    final String Rg;
    final String vL;

    ParcelableCollaborator(int versionCode, boolean isMe, boolean isAnonymous, String sessionId, String userId, String displayName, String color, String photoUrl) {
        this.BR = versionCode;
        this.Rc = isMe;
        this.Rd = isAnonymous;
        this.vL = sessionId;
        this.Re = userId;
        this.Nz = displayName;
        this.Rf = color;
        this.Rg = photoUrl;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ParcelableCollaborator) {
            return this.vL.equals(((ParcelableCollaborator) obj).vL);
        }
        return false;
    }

    public int hashCode() {
        return this.vL.hashCode();
    }

    public String toString() {
        return "Collaborator [isMe=" + this.Rc + ", isAnonymous=" + this.Rd + ", sessionId=" + this.vL + ", userId=" + this.Re + ", displayName=" + this.Nz + ", color=" + this.Rf + ", photoUrl=" + this.Rg + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        p.a(this, dest, flags);
    }
}
