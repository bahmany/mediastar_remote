package com.google.android.gms.location;

import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class c implements SafeParcelable {
    public static final d CREATOR = new d();
    private final int BR;
    int aem;
    int aen;
    long aeo;

    c(int i, int i2, int i3, long j) {
        this.BR = i;
        this.aem = i2;
        this.aen = i3;
        this.aeo = j;
    }

    private String ed(int i) {
        switch (i) {
            case 0:
                return "STATUS_SUCCESSFUL";
            case 1:
            default:
                return "STATUS_UNKNOWN";
            case 2:
                return "STATUS_TIMED_OUT_ON_SCAN";
            case 3:
                return "STATUS_NO_INFO_IN_DATABASE";
            case 4:
                return "STATUS_INVALID_SCAN";
            case 5:
                return "STATUS_UNABLE_TO_QUERY_DATABASE";
            case 6:
                return "STATUS_SCANS_DISABLED_IN_SETTINGS";
            case 7:
                return "STATUS_LOCATION_DISABLED_IN_SETTINGS";
            case 8:
                return "STATUS_IN_PROGRESS";
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object other) {
        if (!(other instanceof c)) {
            return false;
        }
        c cVar = (c) other;
        return this.aem == cVar.aem && this.aen == cVar.aen && this.aeo == cVar.aeo;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.aem), Integer.valueOf(this.aen), Long.valueOf(this.aeo));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LocationStatus[cell status: ").append(ed(this.aem));
        sb.append(", wifi status: ").append(ed(this.aen));
        sb.append(", elapsed realtime ns: ").append(this.aeo);
        sb.append(']');
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        d.a(this, parcel, flags);
    }
}
