package com.google.android.gms.drive.metadata;

import android.os.Bundle;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class a<T> implements MetadataField<T> {
    private final String Pt;
    private final Set<String> Pu;
    private final Set<String> Pv;
    private final int Pw;

    protected a(String str, int i) {
        this.Pt = (String) n.b(str, (Object) "fieldName");
        this.Pu = Collections.singleton(str);
        this.Pv = Collections.emptySet();
        this.Pw = i;
    }

    protected a(String str, Collection<String> collection, Collection<String> collection2, int i) {
        this.Pt = (String) n.b(str, (Object) "fieldName");
        this.Pu = Collections.unmodifiableSet(new HashSet(collection));
        this.Pv = Collections.unmodifiableSet(new HashSet(collection2));
        this.Pw = i;
    }

    @Override // com.google.android.gms.drive.metadata.MetadataField
    public final T a(DataHolder dataHolder, int i, int i2) {
        if (b(dataHolder, i, i2)) {
            return c(dataHolder, i, i2);
        }
        return null;
    }

    protected abstract void a(Bundle bundle, T t);

    @Override // com.google.android.gms.drive.metadata.MetadataField
    public final void a(DataHolder dataHolder, MetadataBundle metadataBundle, int i, int i2) {
        n.b(dataHolder, "dataHolder");
        n.b(metadataBundle, "bundle");
        metadataBundle.b(this, a(dataHolder, i, i2));
    }

    @Override // com.google.android.gms.drive.metadata.MetadataField
    public final void a(T t, Bundle bundle) {
        n.b(bundle, "bundle");
        if (t == null) {
            bundle.putString(getName(), null);
        } else {
            a(bundle, (Bundle) t);
        }
    }

    protected boolean b(DataHolder dataHolder, int i, int i2) {
        Iterator<String> it = this.Pu.iterator();
        while (it.hasNext()) {
            if (dataHolder.h(it.next(), i, i2)) {
                return false;
            }
        }
        return true;
    }

    protected abstract T c(DataHolder dataHolder, int i, int i2);

    @Override // com.google.android.gms.drive.metadata.MetadataField
    public final T f(Bundle bundle) {
        n.b(bundle, "bundle");
        if (bundle.get(getName()) != null) {
            return g(bundle);
        }
        return null;
    }

    protected abstract T g(Bundle bundle);

    @Override // com.google.android.gms.drive.metadata.MetadataField
    public final String getName() {
        return this.Pt;
    }

    public String toString() {
        return this.Pt;
    }
}
