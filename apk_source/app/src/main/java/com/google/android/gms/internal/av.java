package com.google.android.gms.internal;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.List;

@ez
/* loaded from: classes.dex */
public final class av implements SafeParcelable {
    public static final aw CREATOR = new aw();
    public final Bundle extras;
    public final long nT;
    public final int nU;
    public final List<String> nV;
    public final boolean nW;
    public final int nX;
    public final boolean nY;
    public final String nZ;
    public final bj oa;
    public final Location ob;
    public final String oc;
    public final Bundle od;
    public final int versionCode;

    public av(int i, long j, Bundle bundle, int i2, List<String> list, boolean z, int i3, boolean z2, String str, bj bjVar, Location location, String str2, Bundle bundle2) {
        this.versionCode = i;
        this.nT = j;
        this.extras = bundle;
        this.nU = i2;
        this.nV = list;
        this.nW = z;
        this.nX = i3;
        this.nY = z2;
        this.nZ = str;
        this.oa = bjVar;
        this.ob = location;
        this.oc = str2;
        this.od = bundle2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        aw.a(this, out, flags);
    }
}
