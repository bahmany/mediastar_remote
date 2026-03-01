package com.google.android.gms.drive.query.internal;

import android.os.Parcel;
import com.google.android.gms.drive.metadata.SearchableCollectionMetadataField;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;
import java.util.Collection;
import java.util.Collections;

/* loaded from: classes.dex */
public class InFilter<T> extends AbstractFilter {
    public static final h CREATOR = new h();
    final int BR;
    final MetadataBundle QD;
    private final com.google.android.gms.drive.metadata.b<T> QO;

    InFilter(int versionCode, MetadataBundle value) {
        this.BR = versionCode;
        this.QD = value;
        this.QO = (com.google.android.gms.drive.metadata.b) e.b(value);
    }

    public InFilter(SearchableCollectionMetadataField<T> field, T value) {
        this(1, MetadataBundle.a(field, Collections.singleton(value)));
    }

    @Override // com.google.android.gms.drive.query.Filter
    public <F> F a(f<F> fVar) {
        return fVar.b((com.google.android.gms.drive.metadata.b<com.google.android.gms.drive.metadata.b<T>>) this.QO, (com.google.android.gms.drive.metadata.b<T>) getValue());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public T getValue() {
        return (T) ((Collection) this.QD.a(this.QO)).iterator().next();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        h.a(this, out, flags);
    }
}
