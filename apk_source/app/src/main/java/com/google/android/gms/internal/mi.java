package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public final class mi implements SafeParcelable {
    public static final mj CREATOR = new mj();
    final int BR;
    final List<mo> afg;
    private final String afh;
    private final boolean afi;
    final List<ms> afj;
    final List<String> afk;
    private final Set<mo> afl;
    private final Set<ms> afm;
    private final Set<String> afn;

    mi(int i, List<mo> list, String str, boolean z, List<ms> list2, List<String> list3) {
        this.BR = i;
        this.afg = list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
        this.afh = str == null ? "" : str;
        this.afi = z;
        this.afj = list2 == null ? Collections.emptyList() : Collections.unmodifiableList(list2);
        this.afk = list3 == null ? Collections.emptyList() : Collections.unmodifiableList(list3);
        this.afl = f(this.afg);
        this.afm = f(this.afj);
        this.afn = f(this.afk);
    }

    private static <E> Set<E> f(List<E> list) {
        return list.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet(list));
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        mj mjVar = CREATOR;
        return 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof mi)) {
            return false;
        }
        mi miVar = (mi) object;
        return this.afl.equals(miVar.afl) && this.afi == miVar.afi && this.afm.equals(miVar.afm) && this.afn.equals(miVar.afn);
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.afl, Boolean.valueOf(this.afi), this.afm, this.afn);
    }

    @Deprecated
    public String mg() {
        return this.afh;
    }

    public boolean mh() {
        return this.afi;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("types", this.afl).a("placeIds", this.afn).a("requireOpenNow", Boolean.valueOf(this.afi)).a("requestedUserDataTypes", this.afm).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        mj mjVar = CREATOR;
        mj.a(this, parcel, flags);
    }
}
