package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.wearable.MessageEvent;
import com.hisilicon.multiscreen.protocol.ClientInfo;

/* loaded from: classes.dex */
public class ah implements SafeParcelable, MessageEvent {
    public static final Parcelable.Creator<ah> CREATOR = new ai();
    final int BR;
    private final byte[] acw;
    private final String avw;
    private final String avx;
    private final int uQ;

    ah(int i, int i2, String str, byte[] bArr, String str2) {
        this.BR = i;
        this.uQ = i2;
        this.avw = str;
        this.acw = bArr;
        this.avx = str2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.gms.wearable.MessageEvent
    public byte[] getData() {
        return this.acw;
    }

    @Override // com.google.android.gms.wearable.MessageEvent
    public String getPath() {
        return this.avw;
    }

    @Override // com.google.android.gms.wearable.MessageEvent
    public int getRequestId() {
        return this.uQ;
    }

    @Override // com.google.android.gms.wearable.MessageEvent
    public String getSourceNodeId() {
        return this.avx;
    }

    public String toString() {
        return "MessageEventParcelable[" + this.uQ + ClientInfo.SEPARATOR_BETWEEN_VARS + this.avw + ", size=" + (this.acw == null ? "null" : Integer.valueOf(this.acw.length)) + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ai.a(this, dest, flags);
    }
}
