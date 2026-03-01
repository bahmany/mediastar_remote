package com.google.android.gms.wearable;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.g;
import com.google.android.gms.wearable.internal.h;

/* loaded from: classes.dex */
public class DataEventBuffer extends g<DataEvent> implements Result {
    private final Status CM;

    public DataEventBuffer(DataHolder dataHolder) {
        super(dataHolder);
        this.CM = new Status(dataHolder.getStatusCode());
    }

    @Override // com.google.android.gms.common.data.g
    protected String gE() {
        return "path";
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.data.g
    /* renamed from: p, reason: merged with bridge method [inline-methods] */
    public DataEvent f(int i, int i2) {
        return new h(this.IC, i, i2);
    }
}
