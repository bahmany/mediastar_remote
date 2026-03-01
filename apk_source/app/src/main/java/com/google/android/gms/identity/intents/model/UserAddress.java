package com.google.android.gms.identity.intents.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.identity.intents.AddressConstants;

/* loaded from: classes.dex */
public final class UserAddress implements SafeParcelable {
    public static final Parcelable.Creator<UserAddress> CREATOR = new b();
    private final int BR;
    String adC;
    String adD;
    String adE;
    String adF;
    String adG;
    String adH;
    String adI;
    String adJ;
    String adK;
    String adL;
    boolean adM;
    String adN;
    String adO;
    String name;
    String uW;

    UserAddress() {
        this.BR = 1;
    }

    UserAddress(int versionCode, String name, String address1, String address2, String address3, String address4, String address5, String administrativeArea, String locality, String countryCode, String postalCode, String sortingCode, String phoneNumber, boolean isPostBox, String companyName, String emailAddress) {
        this.BR = versionCode;
        this.name = name;
        this.adC = address1;
        this.adD = address2;
        this.adE = address3;
        this.adF = address4;
        this.adG = address5;
        this.adH = administrativeArea;
        this.adI = locality;
        this.uW = countryCode;
        this.adJ = postalCode;
        this.adK = sortingCode;
        this.adL = phoneNumber;
        this.adM = isPostBox;
        this.adN = companyName;
        this.adO = emailAddress;
    }

    public static UserAddress fromIntent(Intent data) {
        if (data == null || !data.hasExtra(AddressConstants.Extras.EXTRA_ADDRESS)) {
            return null;
        }
        return (UserAddress) data.getParcelableExtra(AddressConstants.Extras.EXTRA_ADDRESS);
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

    public String getAddress4() {
        return this.adF;
    }

    public String getAddress5() {
        return this.adG;
    }

    public String getAdministrativeArea() {
        return this.adH;
    }

    public String getCompanyName() {
        return this.adN;
    }

    public String getCountryCode() {
        return this.uW;
    }

    public String getEmailAddress() {
        return this.adO;
    }

    public String getLocality() {
        return this.adI;
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

    public String getSortingCode() {
        return this.adK;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public boolean isPostBox() {
        return this.adM;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        b.a(this, out, flags);
    }
}
