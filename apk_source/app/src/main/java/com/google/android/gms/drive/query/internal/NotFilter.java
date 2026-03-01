package com.google.android.gms.drive.query.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.drive.query.Filter;

/* loaded from: classes.dex */
public class NotFilter extends AbstractFilter {
    public static final Parcelable.Creator<NotFilter> CREATOR = new k();
    final int BR;
    final FilterHolder QQ;

    NotFilter(int versionCode, FilterHolder toNegate) {
        this.BR = versionCode;
        this.QQ = toNegate;
    }

    public NotFilter(Filter toNegate) {
        this(1, new FilterHolder(toNegate));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.drive.query.Filter
    public <T> T a(f<T> fVar) {
        return (T) fVar.j(this.QQ.getFilter().a(fVar));
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        k.a(this, out, flags);
    }
}
