package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.DataItemAsset;
import com.hisilicon.multiscreen.protocol.ClientInfo;

/* loaded from: classes.dex */
public class i implements DataItemAsset {
    private final String BL;
    private final String JH;

    public i(DataItemAsset dataItemAsset) {
        this.BL = dataItemAsset.getId();
        this.JH = dataItemAsset.getDataItemKey();
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
        sb.append("DataItemAssetEntity[");
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
}
