package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class mm implements SafeParcelable {
    public static final mn CREATOR = new mn();
    static final long afp = TimeUnit.HOURS.toMillis(1);
    final int BR;
    private final long aeh;
    private final mi afq;
    private final int mPriority;

    public mm(int i, mi miVar, long j, int i2) {
        this.BR = i;
        this.afq = miVar;
        this.aeh = j;
        this.mPriority = i2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        mn mnVar = CREATOR;
        return 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof mm)) {
            return false;
        }
        mm mmVar = (mm) object;
        return com.google.android.gms.common.internal.m.equal(this.afq, mmVar.afq) && this.aeh == mmVar.aeh && this.mPriority == mmVar.mPriority;
    }

    public long getInterval() {
        return this.aeh;
    }

    public int getPriority() {
        return this.mPriority;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.afq, Long.valueOf(this.aeh), Integer.valueOf(this.mPriority));
    }

    public mi mf() {
        return this.afq;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("filter", this.afq).a("interval", Long.valueOf(this.aeh)).a("priority", Integer.valueOf(this.mPriority)).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        mn mnVar = CREATOR;
        mn.a(this, parcel, flags);
    }
}
