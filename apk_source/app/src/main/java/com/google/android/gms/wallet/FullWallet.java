package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.identity.intents.model.UserAddress;

/* loaded from: classes.dex */
public final class FullWallet implements SafeParcelable {
    public static final Parcelable.Creator<FullWallet> CREATOR = new f();
    private final int BR;
    String asq;
    String asr;
    ProxyCard ass;
    String ast;
    Address asu;
    Address asv;
    String[] asw;
    UserAddress asx;
    UserAddress asy;
    InstrumentInfo[] asz;

    private FullWallet() {
        this.BR = 1;
    }

    FullWallet(int versionCode, String googleTransactionId, String merchantTransactionId, ProxyCard proxyCard, String email, Address billingAddress, Address shippingAddress, String[] paymentDescriptions, UserAddress buyerBillingAddress, UserAddress buyerShippingAddress, InstrumentInfo[] instrumentInfos) {
        this.BR = versionCode;
        this.asq = googleTransactionId;
        this.asr = merchantTransactionId;
        this.ass = proxyCard;
        this.ast = email;
        this.asu = billingAddress;
        this.asv = shippingAddress;
        this.asw = paymentDescriptions;
        this.asx = buyerBillingAddress;
        this.asy = buyerShippingAddress;
        this.asz = instrumentInfos;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Deprecated
    public Address getBillingAddress() {
        return this.asu;
    }

    public UserAddress getBuyerBillingAddress() {
        return this.asx;
    }

    public UserAddress getBuyerShippingAddress() {
        return this.asy;
    }

    public String getEmail() {
        return this.ast;
    }

    public String getGoogleTransactionId() {
        return this.asq;
    }

    public InstrumentInfo[] getInstrumentInfos() {
        return this.asz;
    }

    public String getMerchantTransactionId() {
        return this.asr;
    }

    public String[] getPaymentDescriptions() {
        return this.asw;
    }

    public ProxyCard getProxyCard() {
        return this.ass;
    }

    @Deprecated
    public Address getShippingAddress() {
        return this.asv;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        f.a(this, out, flags);
    }
}
