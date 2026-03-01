package com.google.android.gms.drive.query.internal;

import android.os.Parcel;
import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.drive.metadata.SearchableMetadataField;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;

/* loaded from: classes.dex */
public class ComparisonFilter<T> extends AbstractFilter {
    public static final a CREATOR = new a();
    final int BR;
    final Operator QC;
    final MetadataBundle QD;
    final MetadataField<T> QE;

    ComparisonFilter(int i, Operator operator, MetadataBundle metadataBundle) {
        this.BR = i;
        this.QC = operator;
        this.QD = metadataBundle;
        this.QE = (MetadataField<T>) e.b(metadataBundle);
    }

    public ComparisonFilter(Operator operator, SearchableMetadataField<T> field, T value) {
        this(1, operator, MetadataBundle.a(field, value));
    }

    @Override // com.google.android.gms.drive.query.Filter
    public <F> F a(f<F> fVar) {
        return fVar.b(this.QC, this.QE, getValue());
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
        a.a(this, out, flags);
    }
}
