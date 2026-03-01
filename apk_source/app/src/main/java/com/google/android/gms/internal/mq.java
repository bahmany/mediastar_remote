package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class mq implements SafeParcelable {
    public static final Parcelable.Creator<mq> CREATOR = new mr();
    final int BR;
    private final String Ss;
    private final LatLng ahN;
    private final List<mo> ahO;
    private final String ahP;
    private final String ahQ;
    private final String mName;

    mq(int i, String str, LatLng latLng, String str2, List<mo> list, String str3, String str4) {
        this.BR = i;
        this.mName = str;
        this.ahN = latLng;
        this.Ss = str2;
        this.ahO = new ArrayList(list);
        this.ahP = str3;
        this.ahQ = str4;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAddress() {
        return this.Ss;
    }

    public String getName() {
        return this.mName;
    }

    public String getPhoneNumber() {
        return this.ahP;
    }

    public LatLng mj() {
        return this.ahN;
    }

    public List<mo> mk() {
        return this.ahO;
    }

    public String ml() {
        return this.ahQ;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        mr.a(this, parcel, flags);
    }
}
