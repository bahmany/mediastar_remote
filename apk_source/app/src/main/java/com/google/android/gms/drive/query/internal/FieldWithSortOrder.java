package com.google.android.gms.drive.query.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Locale;

/* loaded from: classes.dex */
public class FieldWithSortOrder implements SafeParcelable {
    public static final c CREATOR = new c();
    final int BR;
    final String Pt;
    final boolean QF;

    FieldWithSortOrder(int versionCode, String fieldName, boolean isSortAscending) {
        this.BR = versionCode;
        this.Pt = fieldName;
        this.QF = isSortAscending;
    }

    public FieldWithSortOrder(String fieldName, boolean isSortAscending) {
        this(1, fieldName, isSortAscending);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        Locale locale = Locale.US;
        Object[] objArr = new Object[2];
        objArr[0] = this.Pt;
        objArr[1] = this.QF ? "ASC" : "DESC";
        return String.format(locale, "FieldWithSortOrder[%s %s]", objArr);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        c.a(this, out, flags);
    }
}
