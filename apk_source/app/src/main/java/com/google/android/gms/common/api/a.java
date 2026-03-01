package com.google.android.gms.common.api;

import com.google.android.gms.common.data.DataHolder;

/* loaded from: classes.dex */
public abstract class a implements Releasable, Result {
    protected final Status CM;
    protected final DataHolder IC;

    protected a(DataHolder dataHolder) {
        this.CM = new Status(dataHolder.getStatusCode());
        this.IC = dataHolder;
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    @Override // com.google.android.gms.common.api.Releasable
    public void release() {
        if (this.IC != null) {
            this.IC.close();
        }
    }
}
