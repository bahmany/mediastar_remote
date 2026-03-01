package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.jr;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.wobs.p;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class LoyaltyWalletObject implements SafeParcelable {
    public static final Parcelable.Creator<LoyaltyWalletObject> CREATOR = new j();
    private final int BR;
    String Dv;
    String asI;
    String asJ;
    String asK;
    String asL;
    String asM;
    String asN;
    String asO;
    String asP;
    ArrayList<p> asQ;
    com.google.android.gms.wallet.wobs.l asR;
    ArrayList<LatLng> asS;
    String asT;
    String asU;
    ArrayList<com.google.android.gms.wallet.wobs.d> asV;
    boolean asW;
    ArrayList<com.google.android.gms.wallet.wobs.n> asX;
    ArrayList<com.google.android.gms.wallet.wobs.j> asY;
    ArrayList<com.google.android.gms.wallet.wobs.n> asZ;
    com.google.android.gms.wallet.wobs.f ata;
    String fl;
    int state;

    LoyaltyWalletObject() {
        this.BR = 4;
        this.asQ = jr.hz();
        this.asS = jr.hz();
        this.asV = jr.hz();
        this.asX = jr.hz();
        this.asY = jr.hz();
        this.asZ = jr.hz();
    }

    LoyaltyWalletObject(int versionCode, String id, String accountId, String issuerName, String programName, String accountName, String barcodeAlternateText, String barcodeType, String barcodeValue, String barcodeLabel, String classId, int state, ArrayList<p> messages, com.google.android.gms.wallet.wobs.l validTimeInterval, ArrayList<LatLng> locations, String infoModuleDataHexFontColor, String infoModuleDataHexBackgroundColor, ArrayList<com.google.android.gms.wallet.wobs.d> infoModuleDataLabelValueRows, boolean infoModuleDataShowLastUpdateTime, ArrayList<com.google.android.gms.wallet.wobs.n> imageModuleDataMainImageUris, ArrayList<com.google.android.gms.wallet.wobs.j> textModulesData, ArrayList<com.google.android.gms.wallet.wobs.n> linksModuleDataUris, com.google.android.gms.wallet.wobs.f loyaltyPoints) {
        this.BR = versionCode;
        this.fl = id;
        this.asI = accountId;
        this.asJ = issuerName;
        this.asK = programName;
        this.Dv = accountName;
        this.asL = barcodeAlternateText;
        this.asM = barcodeType;
        this.asN = barcodeValue;
        this.asO = barcodeLabel;
        this.asP = classId;
        this.state = state;
        this.asQ = messages;
        this.asR = validTimeInterval;
        this.asS = locations;
        this.asT = infoModuleDataHexFontColor;
        this.asU = infoModuleDataHexBackgroundColor;
        this.asV = infoModuleDataLabelValueRows;
        this.asW = infoModuleDataShowLastUpdateTime;
        this.asX = imageModuleDataMainImageUris;
        this.asY = textModulesData;
        this.asZ = linksModuleDataUris;
        this.ata = loyaltyPoints;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAccountId() {
        return this.asI;
    }

    public String getAccountName() {
        return this.Dv;
    }

    public String getBarcodeAlternateText() {
        return this.asL;
    }

    public String getBarcodeType() {
        return this.asM;
    }

    public String getBarcodeValue() {
        return this.asN;
    }

    public String getId() {
        return this.fl;
    }

    public String getIssuerName() {
        return this.asJ;
    }

    public String getProgramName() {
        return this.asK;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        j.a(this, dest, flags);
    }
}
