package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;

/* loaded from: classes.dex */
public final class h extends com.google.android.gms.common.data.d implements DataEvent {
    private final int aaz;

    public h(DataHolder dataHolder, int i, int i2) {
        super(dataHolder, i);
        this.aaz = i2;
    }

    @Override // com.google.android.gms.wearable.DataEvent
    public DataItem getDataItem() {
        return new o(this.IC, this.JQ, this.aaz);
    }

    @Override // com.google.android.gms.wearable.DataEvent
    public int getType() {
        return getInteger("event_type");
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: pU, reason: merged with bridge method [inline-methods] */
    public DataEvent freeze() {
        return new g(this);
    }
}
