package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.jr;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class CommonWalletObject implements SafeParcelable {
    public static final Parcelable.Creator<CommonWalletObject> CREATOR = new com.google.android.gms.wallet.wobs.a();
    private final int BR;
    String asJ;
    String asL;
    String asM;
    String asN;
    String asO;
    String asP;
    ArrayList<p> asQ;
    l asR;
    ArrayList<LatLng> asS;
    String asT;
    String asU;
    ArrayList<d> asV;
    boolean asW;
    ArrayList<n> asX;
    ArrayList<j> asY;
    ArrayList<n> asZ;
    String fl;
    String name;
    int state;

    public final class a {
        private a() {
        }

        /* synthetic */ a(CommonWalletObject commonWalletObject, AnonymousClass1 anonymousClass1) {
            this();
        }

        public a dc(String str) {
            CommonWalletObject.this.fl = str;
            return this;
        }

        public CommonWalletObject pP() {
            return CommonWalletObject.this;
        }
    }

    CommonWalletObject() {
        this.BR = 1;
        this.asQ = jr.hz();
        this.asS = jr.hz();
        this.asV = jr.hz();
        this.asX = jr.hz();
        this.asY = jr.hz();
        this.asZ = jr.hz();
    }

    CommonWalletObject(int versionCode, String id, String classId, String name, String issuerName, String barcodeAlternateText, String barcodeType, String barcodeValue, String barcodeLabel, int state, ArrayList<p> messages, l validTimeInterval, ArrayList<LatLng> locations, String infoModuleDataHexFontColor, String infoModuleDataHexBackgroundColor, ArrayList<d> infoModuleDataLabelValueRows, boolean infoModuleDataShowLastUpdateTime, ArrayList<n> imageModuleDataMainImageUris, ArrayList<j> textModulesData, ArrayList<n> linksModuleDataUris) {
        this.BR = versionCode;
        this.fl = id;
        this.asP = classId;
        this.name = name;
        this.asJ = issuerName;
        this.asL = barcodeAlternateText;
        this.asM = barcodeType;
        this.asN = barcodeValue;
        this.asO = barcodeLabel;
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
    }

    public static a pO() {
        CommonWalletObject commonWalletObject = new CommonWalletObject();
        commonWalletObject.getClass();
        return new a();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return this.fl;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        com.google.android.gms.wallet.wobs.a.a(this, dest, flags);
    }
}
