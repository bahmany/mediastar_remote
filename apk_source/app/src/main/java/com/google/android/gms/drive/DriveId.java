package com.google.android.gms.drive;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.internal.ah;
import com.google.android.gms.drive.internal.v;
import com.google.android.gms.internal.pl;
import com.google.android.gms.internal.pm;

/* loaded from: classes.dex */
public class DriveId implements SafeParcelable {
    public static final Parcelable.Creator<DriveId> CREATOR = new c();
    final int BR;
    final String Na;
    final long Nb;
    final long Nc;
    private volatile String Nd;

    DriveId(int versionCode, String resourceId, long sqlId, long databaseInstanceId) {
        this.Nd = null;
        this.BR = versionCode;
        this.Na = resourceId;
        n.K(!"".equals(resourceId));
        n.K((resourceId == null && sqlId == -1) ? false : true);
        this.Nb = sqlId;
        this.Nc = databaseInstanceId;
    }

    public DriveId(String resourceId, long sqlId, long databaseInstanceId) {
        this(1, resourceId, sqlId, databaseInstanceId);
    }

    public static DriveId bg(String str) {
        n.i(str);
        return new DriveId(str, -1L, -1L);
    }

    public static DriveId decodeFromString(String s) {
        n.b(s.startsWith("DriveId:"), "Invalid DriveId: " + s);
        return f(Base64.decode(s.substring("DriveId:".length()), 10));
    }

    static DriveId f(byte[] bArr) {
        try {
            ah ahVarG = ah.g(bArr);
            return new DriveId(ahVarG.versionCode, "".equals(ahVarG.Pd) ? null : ahVarG.Pd, ahVarG.Pe, ahVarG.Pf);
        } catch (pl e) {
            throw new IllegalArgumentException();
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public final String encodeToString() {
        if (this.Nd == null) {
            this.Nd = "DriveId:" + Base64.encodeToString(hN(), 10);
        }
        return this.Nd;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DriveId)) {
            return false;
        }
        DriveId driveId = (DriveId) obj;
        if (driveId.Nc == this.Nc) {
            return (driveId.Nb == -1 && this.Nb == -1) ? driveId.Na.equals(this.Na) : driveId.Nb == this.Nb;
        }
        v.p("DriveId", "Attempt to compare invalid DriveId detected. Has local storage been cleared?");
        return false;
    }

    public String getResourceId() {
        return this.Na;
    }

    final byte[] hN() {
        ah ahVar = new ah();
        ahVar.versionCode = this.BR;
        ahVar.Pd = this.Na == null ? "" : this.Na;
        ahVar.Pe = this.Nb;
        ahVar.Pf = this.Nc;
        return pm.f(ahVar);
    }

    public int hashCode() {
        return this.Nb == -1 ? this.Na.hashCode() : (String.valueOf(this.Nc) + String.valueOf(this.Nb)).hashCode();
    }

    public String toString() {
        return encodeToString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        c.a(this, out, flags);
    }
}
