package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.dynamic.d;

/* loaded from: classes.dex */
public final class GroundOverlayOptions implements SafeParcelable {
    public static final e CREATOR = new e();
    public static final float NO_DIMENSION = -1.0f;
    private final int BR;
    private float ajA;
    private boolean ajB;
    private BitmapDescriptor ajD;
    private LatLng ajE;
    private float ajF;
    private float ajG;
    private LatLngBounds ajH;
    private float ajI;
    private float ajJ;
    private float ajK;
    private float ajt;

    public GroundOverlayOptions() {
        this.ajB = true;
        this.ajI = 0.0f;
        this.ajJ = 0.5f;
        this.ajK = 0.5f;
        this.BR = 1;
    }

    GroundOverlayOptions(int versionCode, IBinder wrappedImage, LatLng location, float width, float height, LatLngBounds bounds, float bearing, float zIndex, boolean visible, float transparency, float anchorU, float anchorV) {
        this.ajB = true;
        this.ajI = 0.0f;
        this.ajJ = 0.5f;
        this.ajK = 0.5f;
        this.BR = versionCode;
        this.ajD = new BitmapDescriptor(d.a.am(wrappedImage));
        this.ajE = location;
        this.ajF = width;
        this.ajG = height;
        this.ajH = bounds;
        this.ajt = bearing;
        this.ajA = zIndex;
        this.ajB = visible;
        this.ajI = transparency;
        this.ajJ = anchorU;
        this.ajK = anchorV;
    }

    private GroundOverlayOptions a(LatLng latLng, float f, float f2) {
        this.ajE = latLng;
        this.ajF = f;
        this.ajG = f2;
        return this;
    }

    public GroundOverlayOptions anchor(float u, float v) {
        this.ajJ = u;
        this.ajK = v;
        return this;
    }

    public GroundOverlayOptions bearing(float bearing) {
        this.ajt = ((bearing % 360.0f) + 360.0f) % 360.0f;
        return this;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public float getAnchorU() {
        return this.ajJ;
    }

    public float getAnchorV() {
        return this.ajK;
    }

    public float getBearing() {
        return this.ajt;
    }

    public LatLngBounds getBounds() {
        return this.ajH;
    }

    public float getHeight() {
        return this.ajG;
    }

    public BitmapDescriptor getImage() {
        return this.ajD;
    }

    public LatLng getLocation() {
        return this.ajE;
    }

    public float getTransparency() {
        return this.ajI;
    }

    int getVersionCode() {
        return this.BR;
    }

    public float getWidth() {
        return this.ajF;
    }

    public float getZIndex() {
        return this.ajA;
    }

    public GroundOverlayOptions image(BitmapDescriptor image) {
        this.ajD = image;
        return this;
    }

    public boolean isVisible() {
        return this.ajB;
    }

    IBinder mM() {
        return this.ajD.mm().asBinder();
    }

    public GroundOverlayOptions position(LatLng location, float width) {
        com.google.android.gms.common.internal.n.a(this.ajH == null, "Position has already been set using positionFromBounds");
        com.google.android.gms.common.internal.n.b(location != null, "Location must be specified");
        com.google.android.gms.common.internal.n.b(width >= 0.0f, "Width must be non-negative");
        return a(location, width, -1.0f);
    }

    public GroundOverlayOptions position(LatLng location, float width, float height) {
        com.google.android.gms.common.internal.n.a(this.ajH == null, "Position has already been set using positionFromBounds");
        com.google.android.gms.common.internal.n.b(location != null, "Location must be specified");
        com.google.android.gms.common.internal.n.b(width >= 0.0f, "Width must be non-negative");
        com.google.android.gms.common.internal.n.b(height >= 0.0f, "Height must be non-negative");
        return a(location, width, height);
    }

    public GroundOverlayOptions positionFromBounds(LatLngBounds bounds) {
        com.google.android.gms.common.internal.n.a(this.ajE == null, "Position has already been set using position: " + this.ajE);
        this.ajH = bounds;
        return this;
    }

    public GroundOverlayOptions transparency(float transparency) {
        com.google.android.gms.common.internal.n.b(transparency >= 0.0f && transparency <= 1.0f, "Transparency must be in the range [0..1]");
        this.ajI = transparency;
        return this;
    }

    public GroundOverlayOptions visible(boolean visible) {
        this.ajB = visible;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (com.google.android.gms.maps.internal.v.mK()) {
            f.a(this, out, flags);
        } else {
            e.a(this, out, flags);
        }
    }

    public GroundOverlayOptions zIndex(float zIndex) {
        this.ajA = zIndex;
        return this;
    }
}
