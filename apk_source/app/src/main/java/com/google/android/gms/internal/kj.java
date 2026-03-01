package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.internal.ks;

/* loaded from: classes.dex */
public interface kj extends Api.a {

    public static abstract class a<R extends Result> extends BaseImplementation.a<R, kj> {
        public a() {
            super(Fitness.CU);
        }
    }

    public static class b extends ks.a {
        private final BaseImplementation.b<Status> De;

        public b(BaseImplementation.b<Status> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.ks
        public void k(Status status) {
            this.De.b(status);
        }
    }

    public static abstract class c extends a<Status> {
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d, reason: merged with bridge method [inline-methods] */
        public Status c(Status status) {
            com.google.android.gms.common.internal.n.K(!status.isSuccess());
            return status;
        }
    }

    Context getContext();

    ko iT();
}
