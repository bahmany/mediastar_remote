package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.wearable.DataItemAsset;
import com.hisilicon.multiscreen.protocol.ClientInfo;

/* loaded from: classes.dex */
public class DataItemAssetParcelable implements SafeParcelable, DataItemAsset {
    public static final Parcelable.Creator<DataItemAssetParcelable> CREATOR = new j();
    private final String BL;
    final int BR;
    private final String JH;

    DataItemAssetParcelable(int versionCode, String id, String key) {
        this.BR = versionCode;
        this.BL = id;
        this.JH = key;
    }

    public DataItemAssetParcelable(DataItemAsset value) {
        this.BR = 1;
        this.BL = (String) com.google.android.gms.common.internal.n.i(value.getId());
        this.JH = (String) com.google.android.gms.common.internal.n.i(value.getDataItemKey());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.gms.wearable.DataItemAsset
    public String getDataItemKey() {
        return this.JH;
    }

    @Override // com.google.android.gms.wearable.DataItemAsset
    public String getId() {
        return this.BL;
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: pV, reason: merged with bridge method [inline-methods] */
    public DataItemAsset freeze() {
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataItemAssetParcelable[");
        sb.append("@");
        sb.append(Integer.toHexString(hashCode()));
        if (this.BL == null) {
            sb.append(",noid");
        } else {
            sb.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
            sb.append(this.BL);
        }
        sb.append(", key=");
        sb.append(this.JH);
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        j.a(this, dest, flags);
    }
}
