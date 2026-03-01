package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class Cart implements SafeParcelable {
    public static final Parcelable.Creator<Cart> CREATOR = new b();
    private final int BR;
    String ask;
    String asl;
    ArrayList<LineItem> asm;

    public final class Builder {
        private Builder() {
        }

        /* synthetic */ Builder(Cart x0, AnonymousClass1 x1) {
            this();
        }

        public Builder addLineItem(LineItem lineItem) {
            Cart.this.asm.add(lineItem);
            return this;
        }

        public Cart build() {
            return Cart.this;
        }

        public Builder setCurrencyCode(String currencyCode) {
            Cart.this.asl = currencyCode;
            return this;
        }

        public Builder setLineItems(List<LineItem> lineItems) {
            Cart.this.asm.clear();
            Cart.this.asm.addAll(lineItems);
            return this;
        }

        public Builder setTotalPrice(String totalPrice) {
            Cart.this.ask = totalPrice;
            return this;
        }
    }

    Cart() {
        this.BR = 1;
        this.asm = new ArrayList<>();
    }

    Cart(int versionCode, String totalPrice, String currencyCode, ArrayList<LineItem> lineItems) {
        this.BR = versionCode;
        this.ask = totalPrice;
        this.asl = currencyCode;
        this.asm = lineItems;
    }

    public static Builder newBuilder() {
        Cart cart = new Cart();
        cart.getClass();
        return new Builder();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getCurrencyCode() {
        return this.asl;
    }

    public ArrayList<LineItem> getLineItems() {
        return this.asm;
    }

    public String getTotalPrice() {
        return this.ask;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        b.a(this, dest, flags);
    }
}
