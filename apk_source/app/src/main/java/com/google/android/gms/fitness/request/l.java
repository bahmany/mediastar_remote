package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataType;

/* loaded from: classes.dex */
public class l implements SafeParcelable {
    public static final Parcelable.Creator<l> CREATOR = new m();
    private final int BR;
    private final DataType SF;

    public static class a {
        private DataType SF;

        public a c(DataType dataType) {
            this.SF = dataType;
            return this;
        }

        public l jk() {
            return new l(this);
        }
    }

    l(int i, DataType dataType) {
        this.BR = i;
        this.SF = dataType;
    }

    private l(a aVar) {
        this.BR = 1;
        this.SF = aVar.SF;
    }

    /* synthetic */ l(a aVar, AnonymousClass1 anonymousClass1) {
        this(aVar);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DataType getDataType() {
        return this.SF;
    }

    int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        m.a(this, parcel, flags);
    }
}
