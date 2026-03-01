package com.google.android.gms.wearable;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.g;
import com.google.android.gms.wearable.internal.o;

/* loaded from: classes.dex */
public class DataItemBuffer extends g<DataItem> implements Result {
    private final Status CM;

    public DataItemBuffer(DataHolder dataHolder) {
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
    /* renamed from: q, reason: merged with bridge method [inline-methods] */
    public DataItem f(int i, int i2) {
        return new o(this.IC, i, i2);
    }
}
