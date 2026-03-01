package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.dynamic.d;

/* loaded from: classes.dex */
public final class MarkerOptions implements SafeParcelable {
    public static final k CREATOR = new k();
    private final int BR;
    private String No;
    private boolean ajB;
    private float ajJ;
    private float ajK;
    private String ajS;
    private BitmapDescriptor ajT;
    private boolean ajU;
    private boolean ajV;
    private float ajW;
    private float ajX;
    private float ajY;
    private LatLng aja;
    private float mAlpha;

    public MarkerOptions() {
        this.ajJ = 0.5f;
        this.ajK = 1.0f;
        this.ajB = true;
        this.ajV = false;
        this.ajW = 0.0f;
        this.ajX = 0.5f;
        this.ajY = 0.0f;
        this.mAlpha = 1.0f;
        this.BR = 1;
    }

    MarkerOptions(int versionCode, LatLng position, String title, String snippet, IBinder wrappedIcon, float anchorU, float anchorV, boolean draggable, boolean visible, boolean flat, float rotation, float infoWindowAnchorU, float infoWindowAnchorV, float alpha) {
        this.ajJ = 0.5f;
        this.ajK = 1.0f;
        this.ajB = true;
        this.ajV = false;
        this.ajW = 0.0f;
        this.ajX = 0.5f;
        this.ajY = 0.0f;
        this.mAlpha = 1.0f;
        this.BR = versionCode;
        this.aja = position;
        this.No = title;
        this.ajS = snippet;
        this.ajT = wrappedIcon == null ? null : new BitmapDescriptor(d.a.am(wrappedIcon));
        this.ajJ = anchorU;
        this.ajK = anchorV;
        this.ajU = draggable;
        this.ajB = visible;
        this.ajV = flat;
        this.ajW = rotation;
        this.ajX = infoWindowAnchorU;
        this.ajY = infoWindowAnchorV;
        this.mAlpha = alpha;
    }

    public MarkerOptions alpha(float alpha) {
        this.mAlpha = alpha;
        return this;
    }

    public MarkerOptions anchor(float u, float v) {
        this.ajJ = u;
        this.ajK = v;
        return this;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MarkerOptions draggable(boolean draggable) {
        this.ajU = draggable;
        return this;
    }

    public MarkerOptions flat(boolean flat) {
        this.ajV = flat;
        return this;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public float getAnchorU() {
        return this.ajJ;
    }

    public float getAnchorV() {
        return this.ajK;
    }

    public BitmapDescriptor getIcon() {
        return this.ajT;
    }

    public float getInfoWindowAnchorU() {
        return this.ajX;
    }

    public float getInfoWindowAnchorV() {
        return this.ajY;
    }

    public LatLng getPosition() {
        return this.aja;
    }

    public float getRotation() {
        return this.ajW;
    }

    public String getSnippet() {
        return this.ajS;
    }

    public String getTitle() {
        return this.No;
    }

    int getVersionCode() {
        return this.BR;
    }

    public MarkerOptions icon(BitmapDescriptor icon) {
        this.ajT = icon;
        return this;
    }

    public MarkerOptions infoWindowAnchor(float u, float v) {
        this.ajX = u;
        this.ajY = v;
        return this;
    }

    public boolean isDraggable() {
        return this.ajU;
    }

    public boolean isFlat() {
        return this.ajV;
    }

    public boolean isVisible() {
        return this.ajB;
    }

    IBinder mN() {
        if (this.ajT == null) {
            return null;
        }
        return this.ajT.mm().asBinder();
    }

    public MarkerOptions position(LatLng position) {
        this.aja = position;
        return this;
    }

    public MarkerOptions rotation(float rotation) {
        this.ajW = rotation;
        return this;
    }

    public MarkerOptions snippet(String snippet) {
        this.ajS = snippet;
        return this;
    }

    public MarkerOptions title(String title) {
        this.No = title;
        return this;
    }

    public MarkerOptions visible(boolean visible) {
        this.ajB = visible;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (com.google.android.gms.maps.internal.v.mK()) {
            l.a(this, out, flags);
        } else {
            k.a(this, out, flags);
        }
    }
}
