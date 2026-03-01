package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.wearable.Node;
import com.hisilicon.multiscreen.protocol.ClientInfo;

/* loaded from: classes.dex */
public class ak implements SafeParcelable, Node {
    public static final Parcelable.Creator<ak> CREATOR = new al();
    private final String BL;
    final int BR;
    private final String Nz;

    ak(int i, String str, String str2) {
        this.BR = i;
        this.BL = str;
        this.Nz = str2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ak)) {
            return false;
        }
        ak akVar = (ak) o;
        return akVar.BL.equals(this.BL) && akVar.Nz.equals(this.Nz);
    }

    @Override // com.google.android.gms.wearable.Node
    public String getDisplayName() {
        return this.Nz;
    }

    @Override // com.google.android.gms.wearable.Node
    public String getId() {
        return this.BL;
    }

    public int hashCode() {
        return ((this.BL.hashCode() + 629) * 37) + this.Nz.hashCode();
    }

    public String toString() {
        return "NodeParcelable{" + this.BL + ClientInfo.SEPARATOR_BETWEEN_VARS + this.Nz + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        al.a(this, dest, flags);
    }
}
