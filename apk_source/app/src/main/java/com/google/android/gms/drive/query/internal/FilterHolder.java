package com.google.android.gms.drive.query.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.query.Filter;

/* loaded from: classes.dex */
public class FilterHolder implements SafeParcelable {
    public static final Parcelable.Creator<FilterHolder> CREATOR = new d();
    final int BR;
    final ComparisonFilter<?> QG;
    final FieldOnlyFilter QH;
    final LogicalFilter QI;
    final NotFilter QJ;
    final InFilter<?> QK;
    final MatchAllFilter QL;
    final HasFilter QM;
    private final Filter QN;

    FilterHolder(int versionCode, ComparisonFilter<?> comparisonField, FieldOnlyFilter fieldOnlyFilter, LogicalFilter logicalFilter, NotFilter notFilter, InFilter<?> containsFilter, MatchAllFilter matchAllFilter, HasFilter<?> hasFilter) {
        this.BR = versionCode;
        this.QG = comparisonField;
        this.QH = fieldOnlyFilter;
        this.QI = logicalFilter;
        this.QJ = notFilter;
        this.QK = containsFilter;
        this.QL = matchAllFilter;
        this.QM = hasFilter;
        if (this.QG != null) {
            this.QN = this.QG;
            return;
        }
        if (this.QH != null) {
            this.QN = this.QH;
            return;
        }
        if (this.QI != null) {
            this.QN = this.QI;
            return;
        }
        if (this.QJ != null) {
            this.QN = this.QJ;
            return;
        }
        if (this.QK != null) {
            this.QN = this.QK;
        } else if (this.QL != null) {
            this.QN = this.QL;
        } else {
            if (this.QM == null) {
                throw new IllegalArgumentException("At least one filter must be set.");
            }
            this.QN = this.QM;
        }
    }

    public FilterHolder(Filter filter) {
        this.BR = 2;
        this.QG = filter instanceof ComparisonFilter ? (ComparisonFilter) filter : null;
        this.QH = filter instanceof FieldOnlyFilter ? (FieldOnlyFilter) filter : null;
        this.QI = filter instanceof LogicalFilter ? (LogicalFilter) filter : null;
        this.QJ = filter instanceof NotFilter ? (NotFilter) filter : null;
        this.QK = filter instanceof InFilter ? (InFilter) filter : null;
        this.QL = filter instanceof MatchAllFilter ? (MatchAllFilter) filter : null;
        this.QM = filter instanceof HasFilter ? (HasFilter) filter : null;
        if (this.QG == null && this.QH == null && this.QI == null && this.QJ == null && this.QK == null && this.QL == null && this.QM == null) {
            throw new IllegalArgumentException("Invalid filter type or null filter.");
        }
        this.QN = filter;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Filter getFilter() {
        return this.QN;
    }

    public String toString() {
        return String.format("FilterHolder[%s]", this.QN);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        d.a(this, out, flags);
    }
}
