package com.google.android.gms.common.images;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class WebImage implements SafeParcelable {
    public static final Parcelable.Creator<WebImage> CREATOR = new b();
    private final int BR;
    private final Uri KJ;
    private final int lf;
    private final int lg;

    WebImage(int versionCode, Uri url, int width, int height) {
        this.BR = versionCode;
        this.KJ = url;
        this.lf = width;
        this.lg = height;
    }

    public WebImage(Uri url) throws IllegalArgumentException {
        this(url, 0, 0);
    }

    public WebImage(Uri url, int width, int height) throws IllegalArgumentException {
        this(1, url, width, height);
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width and height must not be negative");
        }
    }

    public WebImage(JSONObject json) throws IllegalArgumentException {
        this(d(json), json.optInt("width", 0), json.optInt("height", 0));
    }

    private static Uri d(JSONObject jSONObject) {
        if (!jSONObject.has("url")) {
            return null;
        }
        try {
            return Uri.parse(jSONObject.getString("url"));
        } catch (JSONException e) {
            return null;
        }
    }

    public JSONObject bL() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("url", this.KJ.toString());
            jSONObject.put("width", this.lf);
            jSONObject.put("height", this.lg);
        } catch (JSONException e) {
        }
        return jSONObject;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof WebImage)) {
            return false;
        }
        WebImage webImage = (WebImage) other;
        return m.equal(this.KJ, webImage.KJ) && this.lf == webImage.lf && this.lg == webImage.lg;
    }

    public int getHeight() {
        return this.lg;
    }

    public Uri getUrl() {
        return this.KJ;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int getWidth() {
        return this.lf;
    }

    public int hashCode() {
        return m.hashCode(this.KJ, Integer.valueOf(this.lf), Integer.valueOf(this.lg));
    }

    public String toString() {
        return String.format("Image %dx%d %s", Integer.valueOf(this.lf), Integer.valueOf(this.lg), this.KJ.toString());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        b.a(this, out, flags);
    }
}
