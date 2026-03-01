package com.google.android.gms.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
class kt<T extends Result> implements PendingResult<T> {
    private final T Tn;

    kt(T t) {
        this.Tn = t;
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public void a(PendingResult.a aVar) {
        aVar.n(this.Tn.getStatus());
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public T await() {
        return this.Tn;
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public T await(long time, TimeUnit units) {
        return this.Tn;
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public void cancel() {
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public boolean isCanceled() {
        return false;
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public void setResultCallback(ResultCallback<T> callback) {
        callback.onResult(this.Tn);
    }

    @Override // com.google.android.gms.common.api.PendingResult
    public void setResultCallback(ResultCallback<T> callback, long time, TimeUnit units) {
        callback.onResult(this.Tn);
    }
}
