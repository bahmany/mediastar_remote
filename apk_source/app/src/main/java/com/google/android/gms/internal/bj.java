package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.ads.search.SearchAdRequest;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@ez
/* loaded from: classes.dex */
public final class bj implements SafeParcelable {
    public static final bk CREATOR = new bk();
    public final int backgroundColor;
    public final int oH;
    public final int oI;
    public final int oJ;
    public final int oK;
    public final int oL;
    public final int oM;
    public final int oN;
    public final String oO;
    public final int oP;
    public final String oQ;
    public final int oR;
    public final int oS;
    public final String oT;
    public final int versionCode;

    bj(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, String str, int i10, String str2, int i11, int i12, String str3) {
        this.versionCode = i;
        this.oH = i2;
        this.backgroundColor = i3;
        this.oI = i4;
        this.oJ = i5;
        this.oK = i6;
        this.oL = i7;
        this.oM = i8;
        this.oN = i9;
        this.oO = str;
        this.oP = i10;
        this.oQ = str2;
        this.oR = i11;
        this.oS = i12;
        this.oT = str3;
    }

    public bj(SearchAdRequest searchAdRequest) {
        this.versionCode = 1;
        this.oH = searchAdRequest.getAnchorTextColor();
        this.backgroundColor = searchAdRequest.getBackgroundColor();
        this.oI = searchAdRequest.getBackgroundGradientBottom();
        this.oJ = searchAdRequest.getBackgroundGradientTop();
        this.oK = searchAdRequest.getBorderColor();
        this.oL = searchAdRequest.getBorderThickness();
        this.oM = searchAdRequest.getBorderType();
        this.oN = searchAdRequest.getCallButtonColor();
        this.oO = searchAdRequest.getCustomChannels();
        this.oP = searchAdRequest.getDescriptionTextColor();
        this.oQ = searchAdRequest.getFontFace();
        this.oR = searchAdRequest.getHeaderTextColor();
        this.oS = searchAdRequest.getHeaderTextSize();
        this.oT = searchAdRequest.getQuery();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        bk.a(this, out, flags);
    }
}
