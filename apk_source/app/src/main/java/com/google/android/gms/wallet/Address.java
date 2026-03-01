package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@Deprecated
/* loaded from: classes.dex */
public final class Address implements SafeParcelable {
    public static final Parcelable.Creator<Address> CREATOR = new a();
    private final int BR;
    String adC;
    String adD;
    String adE;
    String adJ;
    String adL;
    boolean adM;
    String adN;
    String asi;
    String asj;
    String name;
    String uW;

    Address() {
        this.BR = 1;
    }

    Address(int versionCode, String name, String address1, String address2, String address3, String countryCode, String city, String state, String postalCode, String phoneNumber, boolean isPostBox, String companyName) {
        this.BR = versionCode;
        this.name = name;
        this.adC = address1;
        this.adD = address2;
        this.adE = address3;
        this.uW = countryCode;
        this.asi = city;
        this.asj = state;
        this.adJ = postalCode;
        this.adL = phoneNumber;
        this.adM = isPostBox;
        this.adN = companyName;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAddress1() {
        return this.adC;
    }

    public String getAddress2() {
        return this.adD;
    }

    public String getAddress3() {
        return this.adE;
    }

    public String getCity() {
        return this.asi;
    }

    public String getCompanyName() {
        return this.adN;
    }

    public String getCountryCode() {
        return this.uW;
    }

    public String getName() {
        return this.name;
    }

    public String getPhoneNumber() {
        return this.adL;
    }

    public String getPostalCode() {
        return this.adJ;
    }

    public String getState() {
        return this.asj;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public boolean isPostBox() {
        return this.adM;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        a.a(this, out, flags);
    }
}
