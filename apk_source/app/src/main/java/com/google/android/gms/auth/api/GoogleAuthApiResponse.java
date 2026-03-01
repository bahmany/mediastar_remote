package com.google.android.gms.auth.api;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class GoogleAuthApiResponse implements SafeParcelable {
    public static final GoogleAuthApiResponseCreator CREATOR = new GoogleAuthApiResponseCreator();
    final byte[] DA;
    final Bundle Dz;
    final int responseCode;
    final int versionCode;

    public GoogleAuthApiResponse(int versionCode, int responseCode, Bundle headers, byte[] body) {
        this.versionCode = versionCode;
        this.responseCode = responseCode;
        this.Dz = headers;
        this.DA = body;
    }

    public GoogleAuthApiResponse(int responseCode, Bundle headers, byte[] body) {
        this.versionCode = 1;
        this.responseCode = responseCode;
        this.Dz = headers;
        this.DA = body;
    }

    public GoogleAuthApiResponse(int responseCode, Map<String, String> headers, byte[] body) {
        this(responseCode, B(headers), body);
    }

    private static Bundle B(Map<String, String> map) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public byte[] getBody() {
        return this.DA;
    }

    public Bundle getHeaders() {
        return this.Dz;
    }

    public Map<String, String> getHeadersAsMap() {
        HashMap map = new HashMap();
        for (String str : this.Dz.keySet()) {
            map.put(str, this.Dz.getString(str));
        }
        return map;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        GoogleAuthApiResponseCreator.a(this, parcel, flags);
    }
}
