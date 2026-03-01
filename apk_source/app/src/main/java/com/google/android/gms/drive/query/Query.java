package com.google.android.gms.drive.query;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.query.internal.LogicalFilter;
import com.google.android.gms.drive.query.internal.MatchAllFilter;
import com.google.android.gms.drive.query.internal.Operator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class Query implements SafeParcelable {
    public static final Parcelable.Creator<Query> CREATOR = new a();
    final int BR;
    final LogicalFilter Qt;
    final String Qu;
    final SortOrder Qv;
    final List<String> Qw;

    public static class Builder {
        private String Qu;
        private SortOrder Qv;
        private List<String> Qw;
        private final List<Filter> Qx = new ArrayList();

        public Builder() {
        }

        public Builder(Query query) {
            this.Qx.add(query.getFilter());
            this.Qu = query.getPageToken();
            this.Qv = query.getSortOrder();
            this.Qw = query.iq();
        }

        public Builder addFilter(Filter filter) {
            if (!(filter instanceof MatchAllFilter)) {
                this.Qx.add(filter);
            }
            return this;
        }

        public Query build() {
            return new Query(new LogicalFilter(Operator.QW, this.Qx), this.Qu, this.Qv, this.Qw);
        }

        public Builder setPageToken(String token) {
            this.Qu = token;
            return this;
        }

        public Builder setSortOrder(SortOrder sortOrder) {
            this.Qv = sortOrder;
            return this;
        }
    }

    Query(int versionCode, LogicalFilter clause, String pageToken, SortOrder sortOrder, List<String> requestedMetadataFields) {
        this.BR = versionCode;
        this.Qt = clause;
        this.Qu = pageToken;
        this.Qv = sortOrder;
        this.Qw = requestedMetadataFields;
    }

    Query(LogicalFilter clause, String pageToken, SortOrder sortOrder, List<String> requestedMetadataFields) {
        this(1, clause, pageToken, sortOrder, requestedMetadataFields);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Filter getFilter() {
        return this.Qt;
    }

    public String getPageToken() {
        return this.Qu;
    }

    public SortOrder getSortOrder() {
        return this.Qv;
    }

    public List<String> iq() {
        return this.Qw;
    }

    public String toString() {
        return String.format(Locale.US, "Query[%s,%s,PageToken=%s]", this.Qt, this.Qv, this.Qu);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        a.a(this, out, flags);
    }
}
