package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class nl implements SafeParcelable {
    public static final nm CREATOR = new nm();
    public final int akG;
    public final int akH;
    public final String akI;
    public final String akJ;
    public final boolean akK;
    public final String packageName;
    public final int versionCode;

    public nl(int i, String str, int i2, int i3, String str2, String str3, boolean z) {
        this.versionCode = i;
        this.packageName = str;
        this.akG = i2;
        this.akH = i3;
        this.akI = str2;
        this.akJ = str3;
        this.akK = z;
    }

    public nl(String str, int i, int i2, String str2, String str3, boolean z) {
        this.versionCode = 1;
        this.packageName = (String) com.google.android.gms.common.internal.n.i(str);
        this.akG = i;
        this.akH = i2;
        this.akI = str2;
        this.akJ = str3;
        this.akK = z;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof nl)) {
            return false;
        }
        nl nlVar = (nl) object;
        return this.packageName.equals(nlVar.packageName) && this.akG == nlVar.akG && this.akH == nlVar.akH && com.google.android.gms.common.internal.m.equal(this.akI, nlVar.akI) && com.google.android.gms.common.internal.m.equal(this.akJ, nlVar.akJ) && this.akK == nlVar.akK;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.packageName, Integer.valueOf(this.akG), Integer.valueOf(this.akH), this.akI, this.akJ, Boolean.valueOf(this.akK));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayLoggerContext[");
        sb.append("package=").append(this.packageName).append(',');
        sb.append("versionCode=").append(this.versionCode).append(',');
        sb.append("logSource=").append(this.akH).append(',');
        sb.append("uploadAccount=").append(this.akI).append(',');
        sb.append("loggingId=").append(this.akJ).append(',');
        sb.append("logAndroidId=").append(this.akK);
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        nm.a(this, out, flags);
    }
}
