package com.google.android.gms.drive.query.internal;

import android.os.Parcel;
import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;

/* loaded from: classes.dex */
public class HasFilter<T> extends AbstractFilter {
    public static final g CREATOR = new g();
    final int BR;
    final MetadataBundle QD;
    final MetadataField<T> QE;

    HasFilter(int i, MetadataBundle metadataBundle) {
        this.BR = i;
        this.QD = metadataBundle;
        this.QE = (MetadataField<T>) e.b(metadataBundle);
    }

    @Override // com.google.android.gms.drive.query.Filter
    public <F> F a(f<F> fVar) {
        return fVar.d(this.QE, getValue());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public T getValue() {
        return (T) this.QD.a(this.QE);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        g.a(this, out, flags);
    }
}
