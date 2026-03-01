package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/* loaded from: classes.dex */
public class hq implements SafeParcelable {
    public static final hr CREATOR = new hr();
    final int BR;
    public final String Co;
    public final boolean Cp;
    public final boolean Cq;
    public final String Cr;
    public final hk[] Cs;
    final int[] Ct;
    public final String Cu;
    public final String name;
    public final int weight;

    public static final class a {
        private BitSet CB;
        private String CC;
        private String Cv;
        private boolean Cw;
        private boolean Cy;
        private String Cz;
        private final String mName;
        private int Cx = 1;
        private final List<hk> CA = new ArrayList();

        public a(String str) {
            this.mName = str;
        }

        public a E(boolean z) {
            this.Cw = z;
            return this;
        }

        public a F(boolean z) {
            this.Cy = z;
            return this;
        }

        public a P(int i) {
            if (this.CB == null) {
                this.CB = new BitSet();
            }
            this.CB.set(i);
            return this;
        }

        public a at(String str) {
            this.Cv = str;
            return this;
        }

        public a au(String str) {
            this.CC = str;
            return this;
        }

        public hq fn() {
            int i = 0;
            int[] iArr = null;
            if (this.CB != null) {
                iArr = new int[this.CB.cardinality()];
                int iNextSetBit = this.CB.nextSetBit(0);
                while (iNextSetBit >= 0) {
                    iArr[i] = iNextSetBit;
                    iNextSetBit = this.CB.nextSetBit(iNextSetBit + 1);
                    i++;
                }
            }
            return new hq(this.mName, this.Cv, this.Cw, this.Cx, this.Cy, this.Cz, (hk[]) this.CA.toArray(new hk[this.CA.size()]), iArr, this.CC);
        }
    }

    hq(int i, String str, String str2, boolean z, int i2, boolean z2, String str3, hk[] hkVarArr, int[] iArr, String str4) {
        this.BR = i;
        this.name = str;
        this.Co = str2;
        this.Cp = z;
        this.weight = i2;
        this.Cq = z2;
        this.Cr = str3;
        this.Cs = hkVarArr;
        this.Ct = iArr;
        this.Cu = str4;
    }

    hq(String str, String str2, boolean z, int i, boolean z2, String str3, hk[] hkVarArr, int[] iArr, String str4) {
        this(2, str, str2, z, i, z2, str3, hkVarArr, iArr, str4);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        hr hrVar = CREATOR;
        return 0;
    }

    public boolean equals(Object object) {
        if (!(object instanceof hq)) {
            return false;
        }
        hq hqVar = (hq) object;
        return this.name.equals(hqVar.name) && this.Co.equals(hqVar.Co) && this.Cp == hqVar.Cp;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        hr hrVar = CREATOR;
        hr.a(this, out, flags);
    }
}
