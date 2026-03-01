package com.google.android.gms.internal;

import android.os.ParcelFileDescriptor;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.hm;
import com.google.android.gms.internal.hw;

/* loaded from: classes.dex */
public abstract class hx<T> extends hw.a {
    protected BaseImplementation.b<T> CH;

    public hx(BaseImplementation.b<T> bVar) {
        this.CH = bVar;
    }

    @Override // com.google.android.gms.internal.hw
    public void a(Status status) {
    }

    @Override // com.google.android.gms.internal.hw
    public void a(Status status, ParcelFileDescriptor parcelFileDescriptor) {
    }

    @Override // com.google.android.gms.internal.hw
    public void a(Status status, boolean z) {
    }

    @Override // com.google.android.gms.internal.hw
    public void a(hm.b bVar) {
    }
}
