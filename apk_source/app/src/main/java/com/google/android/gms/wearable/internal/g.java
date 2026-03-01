package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;

/* loaded from: classes.dex */
public class g implements DataEvent {
    private int FD;
    private DataItem avh;

    public g(DataEvent dataEvent) {
        this.FD = dataEvent.getType();
        this.avh = dataEvent.getDataItem().freeze();
    }

    @Override // com.google.android.gms.wearable.DataEvent
    public DataItem getDataItem() {
        return this.avh;
    }

    @Override // com.google.android.gms.wearable.DataEvent
    public int getType() {
        return this.FD;
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: pU, reason: merged with bridge method [inline-methods] */
    public DataEvent freeze() {
        return this;
    }
}
