package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.hq;
import com.hisilicon.multiscreen.protocol.message.MessageDef;

/* loaded from: classes.dex */
public class hi implements SafeParcelable {
    final int BR;
    public final String Ce;
    final hq Cf;
    public final int Cg;
    public final byte[] Ch;
    public static final int Cc = Integer.parseInt(MessageDef.DEVICE_NAME_PORT);
    public static final hj CREATOR = new hj();
    private static final hq Cd = new hq.a("SsbContext").E(true).at("blob").fn();

    hi(int i, String str, hq hqVar, int i2, byte[] bArr) {
        com.google.android.gms.common.internal.n.b(i2 == Cc || hp.O(i2) != null, "Invalid section type " + i2);
        this.BR = i;
        this.Ce = str;
        this.Cf = hqVar;
        this.Cg = i2;
        this.Ch = bArr;
        String strFl = fl();
        if (strFl != null) {
            throw new IllegalArgumentException(strFl);
        }
    }

    public hi(String str, hq hqVar) {
        this(1, str, hqVar, Cc, null);
    }

    public hi(String str, hq hqVar, String str2) {
        this(1, str, hqVar, hp.as(str2), null);
    }

    public hi(byte[] bArr, hq hqVar) {
        this(1, null, hqVar, Cc, bArr);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        hj hjVar = CREATOR;
        return 0;
    }

    public String fl() {
        if (this.Cg != Cc && hp.O(this.Cg) == null) {
            return "Invalid section type " + this.Cg;
        }
        if (this.Ce == null || this.Ch == null) {
            return null;
        }
        return "Both content and blobContent set";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        hj hjVar = CREATOR;
        hj.a(this, dest, flags);
    }
}
