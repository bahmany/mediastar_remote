package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.location.Geofence;
import java.util.Locale;

/* loaded from: classes.dex */
public class mb implements SafeParcelable, Geofence {
    public static final mc CREATOR = new mc();
    private final int BR;
    private final String Xr;
    private final int adW;
    private final short adY;
    private final double adZ;
    private final double aea;
    private final float aeb;
    private final int aec;
    private final int aed;
    private final long afb;

    public mb(int i, String str, int i2, short s, double d, double d2, float f, long j, int i3, int i4) {
        bV(str);
        b(f);
        a(d, d2);
        int iEj = ej(i2);
        this.BR = i;
        this.adY = s;
        this.Xr = str;
        this.adZ = d;
        this.aea = d2;
        this.aeb = f;
        this.afb = j;
        this.adW = iEj;
        this.aec = i3;
        this.aed = i4;
    }

    public mb(String str, int i, short s, double d, double d2, float f, long j, int i2, int i3) {
        this(1, str, i, s, d, d2, f, j, i2, i3);
    }

    private static void a(double d, double d2) {
        if (d > 90.0d || d < -90.0d) {
            throw new IllegalArgumentException("invalid latitude: " + d);
        }
        if (d2 > 180.0d || d2 < -180.0d) {
            throw new IllegalArgumentException("invalid longitude: " + d2);
        }
    }

    private static void b(float f) {
        if (f <= 0.0f) {
            throw new IllegalArgumentException("invalid radius: " + f);
        }
    }

    private static void bV(String str) {
        if (str == null || str.length() > 100) {
            throw new IllegalArgumentException("requestId is null or too long: " + str);
        }
    }

    private static int ej(int i) {
        int i2 = i & 7;
        if (i2 == 0) {
            throw new IllegalArgumentException("No supported transition specified: " + i);
        }
        return i2;
    }

    private static String ek(int i) {
        switch (i) {
            case 1:
                return "CIRCLE";
            default:
                return null;
        }
    }

    public static mb h(byte[] bArr) {
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.unmarshall(bArr, 0, bArr.length);
        parcelObtain.setDataPosition(0);
        mb mbVarCreateFromParcel = CREATOR.createFromParcel(parcelObtain);
        parcelObtain.recycle();
        return mbVarCreateFromParcel;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        mc mcVar = CREATOR;
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof mb)) {
            mb mbVar = (mb) obj;
            return this.aeb == mbVar.aeb && this.adZ == mbVar.adZ && this.aea == mbVar.aea && this.adY == mbVar.adY;
        }
        return false;
    }

    public long getExpirationTime() {
        return this.afb;
    }

    public double getLatitude() {
        return this.adZ;
    }

    public double getLongitude() {
        return this.aea;
    }

    public int getNotificationResponsiveness() {
        return this.aec;
    }

    @Override // com.google.android.gms.location.Geofence
    public String getRequestId() {
        return this.Xr;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        long jDoubleToLongBits = Double.doubleToLongBits(this.adZ);
        int i = ((int) (jDoubleToLongBits ^ (jDoubleToLongBits >>> 32))) + 31;
        long jDoubleToLongBits2 = Double.doubleToLongBits(this.aea);
        return (((((((i * 31) + ((int) (jDoubleToLongBits2 ^ (jDoubleToLongBits2 >>> 32)))) * 31) + Float.floatToIntBits(this.aeb)) * 31) + this.adY) * 31) + this.adW;
    }

    public short lY() {
        return this.adY;
    }

    public float lZ() {
        return this.aeb;
    }

    public int ma() {
        return this.adW;
    }

    public int mb() {
        return this.aed;
    }

    public String toString() {
        return String.format(Locale.US, "Geofence[%s id:%s transitions:%d %.6f, %.6f %.0fm, resp=%ds, dwell=%dms, @%d]", ek(this.adY), this.Xr, Integer.valueOf(this.adW), Double.valueOf(this.adZ), Double.valueOf(this.aea), Float.valueOf(this.aeb), Integer.valueOf(this.aec / 1000), Integer.valueOf(this.aed), Long.valueOf(this.afb));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        mc mcVar = CREATOR;
        mc.a(this, parcel, flags);
    }
}
