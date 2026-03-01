package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Collections;
import java.util.List;

@ez
/* loaded from: classes.dex */
public final class fk implements SafeParcelable {
    public static final fl CREATOR = new fl();
    public final int errorCode;
    public final int orientation;
    public final List<String> qf;
    public final List<String> qg;
    public final long qj;
    public final String rP;
    public final boolean tF;
    public final String tG;
    public final long tH;
    public final boolean tI;
    public final long tJ;
    public final List<String> tK;
    public final String tL;
    public final long tM;
    public final String tN;
    public final boolean tO;
    public final String tP;
    public final String tQ;
    public final boolean tR;
    public final boolean tS;
    public final boolean tT;
    public final int versionCode;

    public fk(int i) {
        this(10, null, null, null, i, null, -1L, false, -1L, null, -1L, -1, null, -1L, null, false, null, null, false, false, false, false);
    }

    public fk(int i, long j) {
        this(10, null, null, null, i, null, -1L, false, -1L, null, j, -1, null, -1L, null, false, null, null, false, false, false, false);
    }

    fk(int i, String str, String str2, List<String> list, int i2, List<String> list2, long j, boolean z, long j2, List<String> list3, long j3, int i3, String str3, long j4, String str4, boolean z2, String str5, String str6, boolean z3, boolean z4, boolean z5, boolean z6) {
        this.versionCode = i;
        this.rP = str;
        this.tG = str2;
        this.qf = list != null ? Collections.unmodifiableList(list) : null;
        this.errorCode = i2;
        this.qg = list2 != null ? Collections.unmodifiableList(list2) : null;
        this.tH = j;
        this.tI = z;
        this.tJ = j2;
        this.tK = list3 != null ? Collections.unmodifiableList(list3) : null;
        this.qj = j3;
        this.orientation = i3;
        this.tL = str3;
        this.tM = j4;
        this.tN = str4;
        this.tO = z2;
        this.tP = str5;
        this.tQ = str6;
        this.tR = z3;
        this.tS = z4;
        this.tF = z5;
        this.tT = z6;
    }

    public fk(String str, String str2, List<String> list, List<String> list2, long j, boolean z, long j2, List<String> list3, long j3, int i, String str3, long j4, String str4, String str5, boolean z2, boolean z3, boolean z4, boolean z5) {
        this(10, str, str2, list, -2, list2, j, z, j2, list3, j3, i, str3, j4, str4, false, null, str5, z2, z3, z4, z5);
    }

    public fk(String str, String str2, List<String> list, List<String> list2, long j, boolean z, long j2, List<String> list3, long j3, int i, String str3, long j4, String str4, boolean z2, String str5, String str6, boolean z3, boolean z4, boolean z5, boolean z6) {
        this(10, str, str2, list, -2, list2, j, z, j2, list3, j3, i, str3, j4, str4, z2, str5, str6, z3, z4, z5, z6);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        fl.a(this, out, flags);
    }
}
