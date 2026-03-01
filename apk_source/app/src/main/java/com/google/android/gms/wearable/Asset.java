package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class Asset implements SafeParcelable {
    public static final Parcelable.Creator<Asset> CREATOR = new a();
    final int BR;
    private byte[] acw;
    private String auF;
    public ParcelFileDescriptor auG;
    public Uri uri;

    Asset(int versionCode, byte[] data, String digest, ParcelFileDescriptor fd, Uri uri) {
        this.BR = versionCode;
        this.acw = data;
        this.auF = digest;
        this.auG = fd;
        this.uri = uri;
    }

    public static Asset createFromBytes(byte[] assetData) {
        if (assetData == null) {
            throw new IllegalArgumentException("Asset data cannot be null");
        }
        return new Asset(1, assetData, null, null, null);
    }

    public static Asset createFromFd(ParcelFileDescriptor fd) {
        if (fd == null) {
            throw new IllegalArgumentException("Asset fd cannot be null");
        }
        return new Asset(1, null, null, fd, null);
    }

    public static Asset createFromRef(String digest) {
        if (digest == null) {
            throw new IllegalArgumentException("Asset digest cannot be null");
        }
        return new Asset(1, null, digest, null, null);
    }

    public static Asset createFromUri(Uri uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Asset uri cannot be null");
        }
        return new Asset(1, null, null, null, uri);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Asset)) {
            return false;
        }
        Asset asset = (Asset) o;
        return m.equal(this.acw, asset.acw) && m.equal(this.auF, asset.auF) && m.equal(this.auG, asset.auG) && m.equal(this.uri, asset.uri);
    }

    public byte[] getData() {
        return this.acw;
    }

    public String getDigest() {
        return this.auF;
    }

    public ParcelFileDescriptor getFd() {
        return this.auG;
    }

    public Uri getUri() {
        return this.uri;
    }

    public int hashCode() {
        return m.hashCode(this.acw, this.auF, this.auG, this.uri);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Asset[@");
        sb.append(Integer.toHexString(hashCode()));
        if (this.auF == null) {
            sb.append(", nodigest");
        } else {
            sb.append(", ");
            sb.append(this.auF);
        }
        if (this.acw != null) {
            sb.append(", size=");
            sb.append(this.acw.length);
        }
        if (this.auG != null) {
            sb.append(", fd=");
            sb.append(this.auG);
        }
        if (this.uri != null) {
            sb.append(", uri=");
            sb.append(this.uri);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        a.a(this, dest, flags | 1);
    }
}
