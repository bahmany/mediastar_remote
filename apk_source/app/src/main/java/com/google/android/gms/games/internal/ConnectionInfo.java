package com.google.android.gms.games.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class ConnectionInfo implements SafeParcelable {
    public static final ConnectionInfoCreator CREATOR = new ConnectionInfoCreator();
    private final int BR;
    private final String Wf;
    private final int Wg;

    public ConnectionInfo(int versionCode, String clientAddress, int registrationLatency) {
        this.BR = versionCode;
        this.Wf = clientAddress;
        this.Wg = registrationLatency;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public String jU() {
        return this.Wf;
    }

    public int jV() {
        return this.Wg;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        ConnectionInfoCreator.a(this, out, flags);
    }
}
