package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public class hb implements Parcelable {

    @Deprecated
    public static final Parcelable.Creator<hb> CREATOR = new Parcelable.Creator<hb>() { // from class: com.google.android.gms.internal.hb.1
        AnonymousClass1() {
        }

        @Override // android.os.Parcelable.Creator
        @Deprecated
        /* renamed from: H */
        public hb[] newArray(int i) {
            return new hb[i];
        }

        @Override // android.os.Parcelable.Creator
        @Deprecated
        /* renamed from: k */
        public hb createFromParcel(Parcel parcel) {
            return new hb(parcel);
        }
    };
    private String BL;
    private String BM;
    private String mValue;

    /* renamed from: com.google.android.gms.internal.hb$1 */
    static class AnonymousClass1 implements Parcelable.Creator<hb> {
        AnonymousClass1() {
        }

        @Override // android.os.Parcelable.Creator
        @Deprecated
        /* renamed from: H */
        public hb[] newArray(int i) {
            return new hb[i];
        }

        @Override // android.os.Parcelable.Creator
        @Deprecated
        /* renamed from: k */
        public hb createFromParcel(Parcel parcel) {
            return new hb(parcel);
        }
    }

    @Deprecated
    public hb() {
    }

    @Deprecated
    hb(Parcel parcel) {
        readFromParcel(parcel);
    }

    public hb(String str, String str2, String str3) {
        this.BL = str;
        this.BM = str2;
        this.mValue = str3;
    }

    @Deprecated
    private void readFromParcel(Parcel in) {
        this.BL = in.readString();
        this.BM = in.readString();
        this.mValue = in.readString();
    }

    @Override // android.os.Parcelable
    @Deprecated
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return this.BL;
    }

    public String getValue() {
        return this.mValue;
    }

    @Override // android.os.Parcelable
    @Deprecated
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.BL);
        out.writeString(this.BM);
        out.writeString(this.mValue);
    }
}
