package com.google.android.gms.wearable;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.internal.pb;
import com.google.android.gms.internal.pm;

/* loaded from: classes.dex */
public class PutDataMapRequest {
    private final DataMap auM = new DataMap();
    private final PutDataRequest auN;

    private PutDataMapRequest(PutDataRequest putDataRequest, DataMap dataMap) {
        this.auN = putDataRequest;
        if (dataMap != null) {
            this.auM.putAll(dataMap);
        }
    }

    public static PutDataMapRequest create(String path) {
        return new PutDataMapRequest(PutDataRequest.create(path), null);
    }

    public static PutDataMapRequest createFromDataMapItem(DataMapItem source) {
        return new PutDataMapRequest(PutDataRequest.k(source.getUri()), source.getDataMap());
    }

    public static PutDataMapRequest createWithAutoAppendedId(String pathPrefix) {
        return new PutDataMapRequest(PutDataRequest.createWithAutoAppendedId(pathPrefix), null);
    }

    public PutDataRequest asPutDataRequest() {
        pb.a aVarA = pb.a(this.auM);
        this.auN.setData(pm.f(aVarA.avQ));
        int size = aVarA.avR.size();
        for (int i = 0; i < size; i++) {
            String string = Integer.toString(i);
            Asset asset = aVarA.avR.get(i);
            if (string == null) {
                throw new IllegalStateException("asset key cannot be null: " + asset);
            }
            if (asset == null) {
                throw new IllegalStateException("asset cannot be null: key=" + string);
            }
            if (Log.isLoggable(DataMap.TAG, 3)) {
                Log.d(DataMap.TAG, "asPutDataRequest: adding asset: " + string + " " + asset);
            }
            this.auN.putAsset(string, asset);
        }
        return this.auN;
    }

    public DataMap getDataMap() {
        return this.auM;
    }

    public Uri getUri() {
        return this.auN.getUri();
    }
}
