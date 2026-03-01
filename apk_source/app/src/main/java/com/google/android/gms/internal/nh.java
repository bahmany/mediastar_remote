package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.hisilicon.multiscreen.protocol.ClientInfo;

/* loaded from: classes.dex */
public class nh implements SafeParcelable {
    public static final nj CREATOR = new nj();
    public final long akw;
    public final byte[] akx;
    public final Bundle aky;
    public final String tag;
    public final int versionCode;

    nh(int i, long j, String str, byte[] bArr, Bundle bundle) {
        this.versionCode = i;
        this.akw = j;
        this.tag = str;
        this.akx = bArr;
        this.aky = bundle;
    }

    public nh(long j, String str, byte[] bArr, String... strArr) {
        this.versionCode = 1;
        this.akw = j;
        this.tag = str;
        this.akx = bArr;
        this.aky = f(strArr);
    }

    private static Bundle f(String... strArr) {
        Bundle bundle = null;
        if (strArr != null) {
            if (strArr.length % 2 != 0) {
                throw new IllegalArgumentException("extras must have an even number of elements");
            }
            int length = strArr.length / 2;
            if (length != 0) {
                bundle = new Bundle(length);
                for (int i = 0; i < length; i++) {
                    bundle.putString(strArr[i * 2], strArr[(i * 2) + 1]);
                }
            }
        }
        return bundle;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("tag=").append(this.tag).append(ClientInfo.SEPARATOR_BETWEEN_VARS);
        sb.append("eventTime=").append(this.akw).append(ClientInfo.SEPARATOR_BETWEEN_VARS);
        if (this.aky != null && !this.aky.isEmpty()) {
            sb.append("keyValues=");
            for (String str : this.aky.keySet()) {
                sb.append("(").append(str).append(ClientInfo.SEPARATOR_BETWEEN_VARS);
                sb.append(this.aky.getString(str)).append(")");
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        nj.a(this, out, flags);
    }
}
