package com.google.android.gms.internal;

import android.content.Context;
import android.os.Parcel;
import android.util.DisplayMetrics;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@ez
/* loaded from: classes.dex */
public final class ay implements SafeParcelable {
    public static final az CREATOR = new az();
    public final int height;
    public final int heightPixels;
    public final String of;
    public final boolean og;
    public final ay[] oh;
    public final int versionCode;
    public final int width;
    public final int widthPixels;

    public ay() {
        this(2, "interstitial_mb", 0, 0, true, 0, 0, null);
    }

    ay(int i, String str, int i2, int i3, boolean z, int i4, int i5, ay[] ayVarArr) {
        this.versionCode = i;
        this.of = str;
        this.height = i2;
        this.heightPixels = i3;
        this.og = z;
        this.width = i4;
        this.widthPixels = i5;
        this.oh = ayVarArr;
    }

    public ay(Context context, AdSize adSize) {
        this(context, new AdSize[]{adSize});
    }

    public ay(Context context, AdSize[] adSizeArr) {
        int i;
        AdSize adSize = adSizeArr[0];
        this.versionCode = 2;
        this.og = false;
        this.width = adSize.getWidth();
        this.height = adSize.getHeight();
        boolean z = this.width == -1;
        boolean z2 = this.height == -2;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (z) {
            this.widthPixels = a(displayMetrics);
            i = (int) (this.widthPixels / displayMetrics.density);
        } else {
            int i2 = this.width;
            this.widthPixels = gr.a(displayMetrics, this.width);
            i = i2;
        }
        int iC = z2 ? c(displayMetrics) : this.height;
        this.heightPixels = gr.a(displayMetrics, iC);
        if (z || z2) {
            this.of = i + "x" + iC + "_as";
        } else {
            this.of = adSize.toString();
        }
        if (adSizeArr.length <= 1) {
            this.oh = null;
            return;
        }
        this.oh = new ay[adSizeArr.length];
        for (int i3 = 0; i3 < adSizeArr.length; i3++) {
            this.oh[i3] = new ay(context, adSizeArr[i3]);
        }
    }

    public ay(ay ayVar, ay[] ayVarArr) {
        this(2, ayVar.of, ayVar.height, ayVar.heightPixels, ayVar.og, ayVar.width, ayVar.widthPixels, ayVarArr);
    }

    public static int a(DisplayMetrics displayMetrics) {
        return displayMetrics.widthPixels;
    }

    public static int b(DisplayMetrics displayMetrics) {
        return (int) (c(displayMetrics) * displayMetrics.density);
    }

    private static int c(DisplayMetrics displayMetrics) {
        int i = (int) (displayMetrics.heightPixels / displayMetrics.density);
        if (i <= 400) {
            return 32;
        }
        return i <= 720 ? 50 : 90;
    }

    public AdSize bc() {
        return com.google.android.gms.ads.a.a(this.width, this.height, this.of);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        az.a(this, out, flags);
    }
}
