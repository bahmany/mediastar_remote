package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.wearable.internal.DataItemAssetParcelable;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class PutDataRequest implements SafeParcelable {
    public static final String WEAR_URI_SCHEME = "wear";
    final int BR;
    private byte[] acw;
    private final Bundle auP;
    private final Uri mUri;
    public static final Parcelable.Creator<PutDataRequest> CREATOR = new e();
    private static final Random auO = new SecureRandom();

    private PutDataRequest(int versionCode, Uri uri) {
        this(versionCode, uri, new Bundle(), null);
    }

    PutDataRequest(int versionCode, Uri uri, Bundle assets, byte[] data) {
        this.BR = versionCode;
        this.mUri = uri;
        this.auP = assets;
        this.auP.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
        this.acw = data;
    }

    public static PutDataRequest create(String path) {
        return k(dd(path));
    }

    public static PutDataRequest createFromDataItem(DataItem source) {
        PutDataRequest putDataRequestK = k(source.getUri());
        for (Map.Entry<String, DataItemAsset> entry : source.getAssets().entrySet()) {
            if (entry.getValue().getId() == null) {
                throw new IllegalStateException("Cannot create an asset for a put request without a digest: " + entry.getKey());
            }
            putDataRequestK.putAsset(entry.getKey(), Asset.createFromRef(entry.getValue().getId()));
        }
        putDataRequestK.setData(source.getData());
        return putDataRequestK;
    }

    public static PutDataRequest createWithAutoAppendedId(String pathPrefix) {
        StringBuilder sb = new StringBuilder(pathPrefix);
        if (!pathPrefix.endsWith(ServiceReference.DELIMITER)) {
            sb.append(ServiceReference.DELIMITER);
        }
        sb.append("PN").append(auO.nextLong());
        return new PutDataRequest(1, dd(sb.toString()));
    }

    private static Uri dd(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("An empty path was supplied.");
        }
        if (!str.startsWith(ServiceReference.DELIMITER)) {
            throw new IllegalArgumentException("A path must start with a single / .");
        }
        if (str.startsWith("//")) {
            throw new IllegalArgumentException("A path must start with a single / .");
        }
        return new Uri.Builder().scheme(WEAR_URI_SCHEME).path(str).build();
    }

    public static PutDataRequest k(Uri uri) {
        return new PutDataRequest(1, uri);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Asset getAsset(String key) {
        return (Asset) this.auP.getParcelable(key);
    }

    public Map<String, Asset> getAssets() {
        HashMap map = new HashMap();
        for (String str : this.auP.keySet()) {
            map.put(str, (Asset) this.auP.getParcelable(str));
        }
        return Collections.unmodifiableMap(map);
    }

    public byte[] getData() {
        return this.acw;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public boolean hasAsset(String key) {
        return this.auP.containsKey(key);
    }

    public Bundle pR() {
        return this.auP;
    }

    public PutDataRequest putAsset(String key, Asset value) {
        n.i(key);
        n.i(value);
        this.auP.putParcelable(key, value);
        return this;
    }

    public PutDataRequest removeAsset(String key) {
        this.auP.remove(key);
        return this;
    }

    public PutDataRequest setData(byte[] data) {
        this.acw = data;
        return this;
    }

    public String toString() {
        return toString(Log.isLoggable(DataMap.TAG, 3));
    }

    public String toString(boolean verbose) {
        StringBuilder sb = new StringBuilder("PutDataRequest[");
        sb.append("dataSz=" + (this.acw == null ? "null" : Integer.valueOf(this.acw.length)));
        sb.append(", numAssets=" + this.auP.size());
        sb.append(", uri=" + this.mUri);
        if (!verbose) {
            sb.append("]");
            return sb.toString();
        }
        sb.append("]\n  assets: ");
        for (String str : this.auP.keySet()) {
            sb.append("\n    " + str + ": " + this.auP.getParcelable(str));
        }
        sb.append("\n  ]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        e.a(this, dest, flags);
    }
}
