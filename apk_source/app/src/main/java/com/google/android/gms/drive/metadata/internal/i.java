package com.google.android.gms.drive.metadata.internal;

import android.os.Bundle;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/* loaded from: classes.dex */
public class i<T extends Parcelable> extends com.google.android.gms.drive.metadata.b<T> {
    public i(String str, int i) {
        super(str, Collections.emptySet(), Collections.singleton(str), i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.drive.metadata.a
    public void a(Bundle bundle, Collection<T> collection) {
        bundle.putParcelableArrayList(getName(), new ArrayList<>(collection));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.drive.metadata.a
    /* renamed from: l, reason: merged with bridge method [inline-methods] */
    public Collection<T> g(Bundle bundle) {
        return bundle.getParcelableArrayList(getName());
    }
}
