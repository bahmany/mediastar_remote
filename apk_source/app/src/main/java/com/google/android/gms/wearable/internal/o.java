package com.google.android.gms.wearable.internal;

import android.net.Uri;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class o extends com.google.android.gms.common.data.d implements DataItem {
    private final int aaz;

    public o(DataHolder dataHolder, int i, int i2) {
        super(dataHolder, i);
        this.aaz = i2;
    }

    @Override // com.google.android.gms.wearable.DataItem
    public Map<String, DataItemAsset> getAssets() {
        HashMap map = new HashMap(this.aaz);
        for (int i = 0; i < this.aaz; i++) {
            k kVar = new k(this.IC, this.JQ + i);
            if (kVar.getDataItemKey() != null) {
                map.put(kVar.getDataItemKey(), kVar);
            }
        }
        return map;
    }

    @Override // com.google.android.gms.wearable.DataItem
    public byte[] getData() {
        return getByteArray("data");
    }

    @Override // com.google.android.gms.wearable.DataItem
    public Uri getUri() {
        return Uri.parse(getString("path"));
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: pW, reason: merged with bridge method [inline-methods] */
    public DataItem freeze() {
        return new l(this);
    }

    @Override // com.google.android.gms.wearable.DataItem
    public DataItem setData(byte[] data) {
        throw new UnsupportedOperationException();
    }
}
