package com.google.android.gms.drive.query.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.drive.query.Filter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class LogicalFilter extends AbstractFilter {
    public static final Parcelable.Creator<LogicalFilter> CREATOR = new i();
    final int BR;
    final Operator QC;
    final List<FilterHolder> QP;
    private List<Filter> Qx;

    LogicalFilter(int versionCode, Operator operator, List<FilterHolder> filterHolders) {
        this.BR = versionCode;
        this.QC = operator;
        this.QP = filterHolders;
    }

    public LogicalFilter(Operator operator, Filter filter, Filter... additionalFilters) {
        this.BR = 1;
        this.QC = operator;
        this.QP = new ArrayList(additionalFilters.length + 1);
        this.QP.add(new FilterHolder(filter));
        this.Qx = new ArrayList(additionalFilters.length + 1);
        this.Qx.add(filter);
        for (Filter filter2 : additionalFilters) {
            this.QP.add(new FilterHolder(filter2));
            this.Qx.add(filter2);
        }
    }

    public LogicalFilter(Operator operator, Iterable<Filter> filters) {
        this.BR = 1;
        this.QC = operator;
        this.Qx = new ArrayList();
        this.QP = new ArrayList();
        for (Filter filter : filters) {
            this.Qx.add(filter);
            this.QP.add(new FilterHolder(filter));
        }
    }

    @Override // com.google.android.gms.drive.query.Filter
    public <T> T a(f<T> fVar) {
        ArrayList arrayList = new ArrayList();
        Iterator<FilterHolder> it = this.QP.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().getFilter().a(fVar));
        }
        return fVar.b(this.QC, arrayList);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        i.a(this, out, flags);
    }
}
