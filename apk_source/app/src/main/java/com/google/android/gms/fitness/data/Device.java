package com.google.android.gms.fitness.data;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.kv;

/* loaded from: classes.dex */
public final class Device implements SafeParcelable {
    public static final Parcelable.Creator<Device> CREATOR = new i();
    public static final int TYPE_CHEST_STRAP = 4;
    public static final int TYPE_PHONE = 1;
    public static final int TYPE_SCALE = 5;
    public static final int TYPE_TABLET = 2;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_WATCH = 3;
    private final int BR;
    private final int FD;
    private final String SQ;
    private final String SR;
    private final String SS;
    private final String Sq;

    Device(int versionCode, String manufacturer, String model, String version, String uid, int type) {
        this.BR = versionCode;
        this.SQ = (String) com.google.android.gms.common.internal.n.i(manufacturer);
        this.SR = (String) com.google.android.gms.common.internal.n.i(model);
        this.Sq = "";
        this.SS = (String) com.google.android.gms.common.internal.n.i(uid);
        this.FD = type;
    }

    public Device(String manufacturer, String model, String uid, int type) {
        this(1, manufacturer, model, "", uid, type);
    }

    public Device(String manufacturer, String model, String version, String uid, int type) {
        this(manufacturer, model, uid, type);
    }

    private static int M(Context context) {
        switch (O(context)) {
            case 8:
            case 9:
                break;
            case 10:
                if (N(context)) {
                }
                break;
            default:
                if (Q(context)) {
                }
                break;
        }
        return 0;
    }

    public static boolean N(Context context) {
        return (context.getResources().getConfiguration().uiMode & 15) == 6;
    }

    private static int O(Context context) {
        return ((P(context) % 1000) / 100) + 5;
    }

    private static int P(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("Fitness", "Could not find package info for Google Play Services");
            return -1;
        }
    }

    private static boolean Q(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getPhoneType() != 0;
    }

    private boolean a(Device device) {
        return com.google.android.gms.common.internal.m.equal(this.SQ, device.SQ) && com.google.android.gms.common.internal.m.equal(this.SR, device.SR) && com.google.android.gms.common.internal.m.equal(this.Sq, device.Sq) && com.google.android.gms.common.internal.m.equal(this.SS, device.SS) && this.FD == device.FD;
    }

    public static Device getLocalDevice(Context context) {
        return new Device(Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE, Build.SERIAL, M(context));
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof Device) && a((Device) that));
    }

    public String getManufacturer() {
        return this.SQ;
    }

    public String getModel() {
        return this.SR;
    }

    String getStreamIdentifier() {
        return String.format("%s:%s:%s", this.SQ, this.SR, this.SS);
    }

    public int getType() {
        return this.FD;
    }

    public String getUid() {
        return this.SS;
    }

    public String getVersion() {
        return this.Sq;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.SQ, this.SR, this.Sq, this.SS, Integer.valueOf(this.FD));
    }

    Device iM() {
        return new Device(kv.bq(this.SQ), kv.bq(this.SR), kv.bq(this.Sq), this.SS, this.FD);
    }

    public String iN() {
        return kv.iU() ? this.SS : kv.bq(this.SS);
    }

    public String toString() {
        return String.format("Device{%s:%s:%s}", getStreamIdentifier(), this.Sq, Integer.valueOf(this.FD));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        i.a(this, parcel, flags);
    }
}
