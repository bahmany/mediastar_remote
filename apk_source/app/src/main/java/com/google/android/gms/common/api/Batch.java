package com.google.android.gms.common.api;

import android.os.Looper;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.PendingResult;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class Batch extends BaseImplementation.AbstractPendingResult<BatchResult> {
    private int Iv;
    private boolean Iw;
    private boolean Ix;
    private final PendingResult<?>[] Iy;
    private final Object mw;

    public static final class Builder {
        private List<PendingResult<?>> IA = new ArrayList();
        private Looper IB;

        public Builder(GoogleApiClient googleApiClient) {
            this.IB = googleApiClient.getLooper();
        }

        public <R extends Result> BatchResultToken<R> add(PendingResult<R> pendingResult) {
            BatchResultToken<R> batchResultToken = new BatchResultToken<>(this.IA.size());
            this.IA.add(pendingResult);
            return batchResultToken;
        }

        public Batch build() {
            return new Batch(this.IA, this.IB);
        }
    }

    private Batch(List<PendingResult<?>> pendingResultList, Looper looper) {
        super(new BaseImplementation.CallbackHandler(looper));
        this.mw = new Object();
        this.Iv = pendingResultList.size();
        this.Iy = new PendingResult[this.Iv];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= pendingResultList.size()) {
                return;
            }
            PendingResult<?> pendingResult = pendingResultList.get(i2);
            this.Iy[i2] = pendingResult;
            pendingResult.a(new PendingResult.a() { // from class: com.google.android.gms.common.api.Batch.1
                @Override // com.google.android.gms.common.api.PendingResult.a
                public void n(Status status) {
                    synchronized (Batch.this.mw) {
                        if (Batch.this.isCanceled()) {
                            return;
                        }
                        if (status.isCanceled()) {
                            Batch.this.Ix = true;
                        } else if (!status.isSuccess()) {
                            Batch.this.Iw = true;
                        }
                        Batch.b(Batch.this);
                        if (Batch.this.Iv == 0) {
                            if (Batch.this.Ix) {
                                Batch.super.cancel();
                            } else {
                                Batch.this.b((Batch) new BatchResult(Batch.this.Iw ? new Status(13) : Status.Jo, Batch.this.Iy));
                            }
                        }
                    }
                }
            });
            i = i2 + 1;
        }
    }

    static /* synthetic */ int b(Batch batch) {
        int i = batch.Iv;
        batch.Iv = i - 1;
        return i;
    }

    @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult, com.google.android.gms.common.api.PendingResult
    public void cancel() {
        super.cancel();
        for (PendingResult<?> pendingResult : this.Iy) {
            pendingResult.cancel();
        }
    }

    @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
    /* renamed from: createFailedResult, reason: merged with bridge method [inline-methods] */
    public BatchResult c(Status status) {
        return new BatchResult(status, this.Iy);
    }
}
