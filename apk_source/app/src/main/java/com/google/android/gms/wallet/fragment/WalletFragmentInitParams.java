package com.google.android.gms.wallet.fragment;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

/* loaded from: classes.dex */
public final class WalletFragmentInitParams implements SafeParcelable {
    public static final Parcelable.Creator<WalletFragmentInitParams> CREATOR = new a();
    final int BR;
    private String Dd;
    private MaskedWalletRequest atL;
    private MaskedWallet atM;
    private int atZ;

    public final class Builder {
        private Builder() {
        }

        /* synthetic */ Builder(WalletFragmentInitParams x0, AnonymousClass1 x1) {
            this();
        }

        public WalletFragmentInitParams build() {
            n.a((WalletFragmentInitParams.this.atM != null && WalletFragmentInitParams.this.atL == null) || (WalletFragmentInitParams.this.atM == null && WalletFragmentInitParams.this.atL != null), "Exactly one of MaskedWallet or MaskedWalletRequest is required");
            n.a(WalletFragmentInitParams.this.atZ >= 0, "masked wallet request code is required and must be non-negative");
            return WalletFragmentInitParams.this;
        }

        public Builder setAccountName(String accountName) {
            WalletFragmentInitParams.this.Dd = accountName;
            return this;
        }

        public Builder setMaskedWallet(MaskedWallet maskedWallet) {
            WalletFragmentInitParams.this.atM = maskedWallet;
            return this;
        }

        public Builder setMaskedWalletRequest(MaskedWalletRequest request) {
            WalletFragmentInitParams.this.atL = request;
            return this;
        }

        public Builder setMaskedWalletRequestCode(int requestCode) {
            WalletFragmentInitParams.this.atZ = requestCode;
            return this;
        }
    }

    private WalletFragmentInitParams() {
        this.BR = 1;
        this.atZ = -1;
    }

    WalletFragmentInitParams(int versionCode, String accountName, MaskedWalletRequest maskedWalletRequest, int maskedWalletRequestCode, MaskedWallet maskedWallet) {
        this.BR = versionCode;
        this.Dd = accountName;
        this.atL = maskedWalletRequest;
        this.atZ = maskedWalletRequestCode;
        this.atM = maskedWallet;
    }

    public static Builder newBuilder() {
        WalletFragmentInitParams walletFragmentInitParams = new WalletFragmentInitParams();
        walletFragmentInitParams.getClass();
        return new Builder();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAccountName() {
        return this.Dd;
    }

    public MaskedWallet getMaskedWallet() {
        return this.atM;
    }

    public MaskedWalletRequest getMaskedWalletRequest() {
        return this.atL;
    }

    public int getMaskedWalletRequestCode() {
        return this.atZ;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        a.a(this, dest, flags);
    }
}
