package com.google.android.gms.wearable;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class c implements SafeParcelable {
    public static final Parcelable.Creator<c> CREATOR = new d();
    final int BR;
    private final int FD;
    private final String Ss;
    private final int auH;
    private final boolean auI;
    private boolean auJ;
    private String auK;
    private final String mName;

    c(int i, String str, String str2, int i2, int i3, boolean z, boolean z2, String str3) {
        this.BR = i;
        this.mName = str;
        this.Ss = str2;
        this.FD = i2;
        this.auH = i3;
        this.auI = z;
        this.auJ = z2;
        this.auK = str3;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (!(o instanceof c)) {
            return false;
        }
        c cVar = (c) o;
        return m.equal(Integer.valueOf(this.BR), Integer.valueOf(cVar.BR)) && m.equal(this.mName, cVar.mName) && m.equal(this.Ss, cVar.Ss) && m.equal(Integer.valueOf(this.FD), Integer.valueOf(cVar.FD)) && m.equal(Integer.valueOf(this.auH), Integer.valueOf(cVar.auH)) && m.equal(Boolean.valueOf(this.auI), Boolean.valueOf(cVar.auI));
    }

    public String getAddress() {
        return this.Ss;
    }

    public String getName() {
        return this.mName;
    }

    public int getRole() {
        return this.auH;
    }

    public int getType() {
        return this.FD;
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.BR), this.mName, this.Ss, Integer.valueOf(this.FD), Integer.valueOf(this.auH), Boolean.valueOf(this.auI));
    }

    public boolean isConnected() {
        return this.auJ;
    }

    public boolean isEnabled() {
        return this.auI;
    }

    public String pQ() {
        return this.auK;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ConnectionConfiguration[ ");
        sb.append("mName=" + this.mName);
        sb.append(", mAddress=" + this.Ss);
        sb.append(", mType=" + this.FD);
        sb.append(", mRole=" + this.auH);
        sb.append(", mEnabled=" + this.auI);
        sb.append(", mIsConnected=" + this.auJ);
        sb.append(", mEnabled=" + this.auK);
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        d.a(this, dest, flags);
    }
}
