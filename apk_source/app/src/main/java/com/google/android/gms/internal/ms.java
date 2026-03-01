package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class ms implements SafeParcelable {
    final int BR;
    final int ahV;
    final String uO;
    public static final ms ahR = y("test_type", 1);
    public static final ms ahS = y("trellis_store", 2);
    public static final ms ahT = y("labeled_place", 6);
    public static final Set<ms> ahU = Collections.unmodifiableSet(new HashSet(Arrays.asList(ahR, ahS, ahT)));
    public static final mt CREATOR = new mt();

    ms(int i, String str, int i2) {
        com.google.android.gms.common.internal.n.aZ(str);
        this.BR = i;
        this.uO = str;
        this.ahV = i2;
    }

    private static ms y(String str, int i) {
        return new ms(0, str, i);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        mt mtVar = CREATOR;
        return 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ms)) {
            return false;
        }
        ms msVar = (ms) object;
        return this.uO.equals(msVar.uO) && this.ahV == msVar.ahV;
    }

    public int hashCode() {
        return this.uO.hashCode();
    }

    public String toString() {
        return this.uO;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        mt mtVar = CREATOR;
        mt.a(this, parcel, flags);
    }
}
