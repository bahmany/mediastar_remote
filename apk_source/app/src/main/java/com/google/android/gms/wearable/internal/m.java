package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class m implements SafeParcelable, DataItem {
    public static final Parcelable.Creator<m> CREATOR = new n();
    final int BR;
    private byte[] acw;
    private final Map<String, DataItemAsset> avk;
    private final Uri mUri;

    m(int i, Uri uri, Bundle bundle, byte[] bArr) {
        this.BR = i;
        this.mUri = uri;
        HashMap map = new HashMap();
        bundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
        for (String str : bundle.keySet()) {
            map.put(str, (DataItemAssetParcelable) bundle.getParcelable(str));
        }
        this.avk = map;
        this.acw = bArr;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.gms.wearable.DataItem
    public Map<String, DataItemAsset> getAssets() {
        return this.avk;
    }

    @Override // com.google.android.gms.wearable.DataItem
    public byte[] getData() {
        return this.acw;
    }

    @Override // com.google.android.gms.wearable.DataItem
    public Uri getUri() {
        return this.mUri;
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.wearable.DataItem
    /* renamed from: m, reason: merged with bridge method [inline-methods] */
    public m setData(byte[] bArr) {
        this.acw = bArr;
        return this;
    }

    public Bundle pR() {
        Bundle bundle = new Bundle();
        bundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
        for (Map.Entry<String, DataItemAsset> entry : this.avk.entrySet()) {
            bundle.putParcelable(entry.getKey(), new DataItemAssetParcelable(entry.getValue()));
        }
        return bundle;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: pX, reason: merged with bridge method [inline-methods] */
    public m freeze() {
        return this;
    }

    public String toString() {
        return toString(Log.isLoggable("DataItem", 3));
    }

    public String toString(boolean verbose) {
        StringBuilder sb = new StringBuilder("DataItemParcelable[");
        sb.append("@");
        sb.append(Integer.toHexString(hashCode()));
        sb.append(",dataSz=" + (this.acw == null ? "null" : Integer.valueOf(this.acw.length)));
        sb.append(", numAssets=" + this.avk.size());
        sb.append(", uri=" + this.mUri);
        if (!verbose) {
            sb.append("]");
            return sb.toString();
        }
        sb.append("]\n  assets: ");
        for (String str : this.avk.keySet()) {
            sb.append("\n    " + str + ": " + this.avk.get(str));
        }
        sb.append("\n  ]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        n.a(this, dest, flags);
    }
}
