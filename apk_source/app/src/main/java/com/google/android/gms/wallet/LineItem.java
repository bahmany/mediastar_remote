package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class LineItem implements SafeParcelable {
    public static final Parcelable.Creator<LineItem> CREATOR = new i();
    private final int BR;
    String asE;
    String asF;
    int asG;
    String ask;
    String asl;
    String description;

    public final class Builder {
        private Builder() {
        }

        /* synthetic */ Builder(LineItem x0, AnonymousClass1 x1) {
            this();
        }

        public LineItem build() {
            return LineItem.this;
        }

        public Builder setCurrencyCode(String currencyCode) {
            LineItem.this.asl = currencyCode;
            return this;
        }

        public Builder setDescription(String description) {
            LineItem.this.description = description;
            return this;
        }

        public Builder setQuantity(String quantity) {
            LineItem.this.asE = quantity;
            return this;
        }

        public Builder setRole(int role) {
            LineItem.this.asG = role;
            return this;
        }

        public Builder setTotalPrice(String totalPrice) {
            LineItem.this.ask = totalPrice;
            return this;
        }

        public Builder setUnitPrice(String unitPrice) {
            LineItem.this.asF = unitPrice;
            return this;
        }
    }

    public interface Role {
        public static final int REGULAR = 0;
        public static final int SHIPPING = 2;
        public static final int TAX = 1;
    }

    LineItem() {
        this.BR = 1;
        this.asG = 0;
    }

    LineItem(int versionCode, String description, String quantity, String unitPrice, String totalPrice, int role, String currencyCode) {
        this.BR = versionCode;
        this.description = description;
        this.asE = quantity;
        this.asF = unitPrice;
        this.ask = totalPrice;
        this.asG = role;
        this.asl = currencyCode;
    }

    public static Builder newBuilder() {
        LineItem lineItem = new LineItem();
        lineItem.getClass();
        return new Builder();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getCurrencyCode() {
        return this.asl;
    }

    public String getDescription() {
        return this.description;
    }

    public String getQuantity() {
        return this.asE;
    }

    public int getRole() {
        return this.asG;
    }

    public String getTotalPrice() {
        return this.ask;
    }

    public String getUnitPrice() {
        return this.asF;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        i.a(this, dest, flags);
    }
}
