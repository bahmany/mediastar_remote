package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.identity.intents.model.UserAddress;

/* loaded from: classes.dex */
public final class MaskedWallet implements SafeParcelable {
    public static final Parcelable.Creator<MaskedWallet> CREATOR = new k();
    private final int BR;
    String asq;
    String asr;
    String ast;
    Address asu;
    Address asv;
    String[] asw;
    UserAddress asx;
    UserAddress asy;
    InstrumentInfo[] asz;
    LoyaltyWalletObject[] atb;
    OfferWalletObject[] atc;

    public final class Builder {
        private Builder() {
        }

        /* synthetic */ Builder(MaskedWallet x0, AnonymousClass1 x1) {
            this();
        }

        public MaskedWallet build() {
            return MaskedWallet.this;
        }

        public Builder setBillingAddress(Address billingAddress) {
            MaskedWallet.this.asu = billingAddress;
            return this;
        }

        public Builder setBuyerBillingAddress(UserAddress buyerBillingAddress) {
            MaskedWallet.this.asx = buyerBillingAddress;
            return this;
        }

        public Builder setBuyerShippingAddress(UserAddress buyerShippingAddress) {
            MaskedWallet.this.asy = buyerShippingAddress;
            return this;
        }

        public Builder setEmail(String email) {
            MaskedWallet.this.ast = email;
            return this;
        }

        public Builder setGoogleTransactionId(String googleTransactionId) {
            MaskedWallet.this.asq = googleTransactionId;
            return this;
        }

        public Builder setInstrumentInfos(InstrumentInfo[] instrumentInfos) {
            MaskedWallet.this.asz = instrumentInfos;
            return this;
        }

        public Builder setLoyaltyWalletObjects(LoyaltyWalletObject[] loyaltyWalletObjects) {
            MaskedWallet.this.atb = loyaltyWalletObjects;
            return this;
        }

        public Builder setMerchantTransactionId(String merchantTransactionId) {
            MaskedWallet.this.asr = merchantTransactionId;
            return this;
        }

        public Builder setOfferWalletObjects(OfferWalletObject[] offerWalletObjects) {
            MaskedWallet.this.atc = offerWalletObjects;
            return this;
        }

        public Builder setPaymentDescriptions(String[] paymentDescriptions) {
            MaskedWallet.this.asw = paymentDescriptions;
            return this;
        }

        public Builder setShippingAddress(Address shippingAddress) {
            MaskedWallet.this.asv = shippingAddress;
            return this;
        }
    }

    private MaskedWallet() {
        this.BR = 2;
    }

    MaskedWallet(int versionCode, String googleTransactionId, String merchantTransactionId, String[] paymentDescriptions, String email, Address billingAddress, Address shippingAddress, LoyaltyWalletObject[] loyaltyWalletObjects, OfferWalletObject[] offerWalletObjects, UserAddress buyerBillingAddress, UserAddress buyerShippingAddress, InstrumentInfo[] instrumentInfos) {
        this.BR = versionCode;
        this.asq = googleTransactionId;
        this.asr = merchantTransactionId;
        this.asw = paymentDescriptions;
        this.ast = email;
        this.asu = billingAddress;
        this.asv = shippingAddress;
        this.atb = loyaltyWalletObjects;
        this.atc = offerWalletObjects;
        this.asx = buyerBillingAddress;
        this.asy = buyerShippingAddress;
        this.asz = instrumentInfos;
    }

    public static Builder newBuilderFrom(MaskedWallet maskedWallet) {
        com.google.android.gms.common.internal.n.i(maskedWallet);
        return pK().setGoogleTransactionId(maskedWallet.getGoogleTransactionId()).setMerchantTransactionId(maskedWallet.getMerchantTransactionId()).setPaymentDescriptions(maskedWallet.getPaymentDescriptions()).setInstrumentInfos(maskedWallet.getInstrumentInfos()).setEmail(maskedWallet.getEmail()).setLoyaltyWalletObjects(maskedWallet.getLoyaltyWalletObjects()).setOfferWalletObjects(maskedWallet.getOfferWalletObjects()).setBuyerBillingAddress(maskedWallet.getBuyerBillingAddress()).setBuyerShippingAddress(maskedWallet.getBuyerShippingAddress());
    }

    public static Builder pK() {
        MaskedWallet maskedWallet = new MaskedWallet();
        maskedWallet.getClass();
        return new Builder();
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

    public LoyaltyWalletObject[] getLoyaltyWalletObjects() {
        return this.atb;
    }

    public String getMerchantTransactionId() {
        return this.asr;
    }

    public OfferWalletObject[] getOfferWalletObjects() {
        return this.atc;
    }

    public String[] getPaymentDescriptions() {
        return this.asw;
    }

    @Deprecated
    public Address getShippingAddress() {
        return this.asv;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        k.a(this, dest, flags);
    }
}
