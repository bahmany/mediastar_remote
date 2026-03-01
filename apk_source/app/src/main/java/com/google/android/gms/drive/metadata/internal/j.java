package com.google.android.gms.drive.metadata.internal;

import android.os.Bundle;
import android.os.Parcelable;
import java.util.Collection;

/* loaded from: classes.dex */
public abstract class j<T extends Parcelable> extends com.google.android.gms.drive.metadata.a<T> {
    public j(String str, Collection<String> collection, Collection<String> collection2, int i) {
        super(str, collection, collection2, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.drive.metadata.a
    public void a(Bundle bundle, T t) {
        bundle.putParcelable(getName(), t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.drive.metadata.a
    /* renamed from: m, reason: merged with bridge method [inline-methods] */
    public T g(Bundle bundle) {
        return (T) bundle.getParcelable(getName());
    }
}
