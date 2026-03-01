package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.n;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class BatchResult implements Result {
    private final Status CM;
    private final PendingResult<?>[] Iy;

    BatchResult(Status status, PendingResult<?>[] pendingResults) {
        this.CM = status;
        this.Iy = pendingResults;
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    public <R extends Result> R take(BatchResultToken<R> batchResultToken) {
        n.b(batchResultToken.mId < this.Iy.length, "The result token does not belong to this batch");
        return (R) this.Iy[batchResultToken.mId].await(0L, TimeUnit.MILLISECONDS);
    }
}
