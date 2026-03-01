package com.google.android.gms.maps;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

/* loaded from: classes.dex */
public final class StreetViewPanoramaOptions implements SafeParcelable {
    public static final c CREATOR = new c();
    private final int BR;
    private Boolean aiC;
    private StreetViewPanoramaCamera aiY;
    private String aiZ;
    private Boolean aiw;
    private LatLng aja;
    private Integer ajb;
    private Boolean ajc;
    private Boolean ajd;
    private Boolean aje;

    public StreetViewPanoramaOptions() {
        this.ajc = true;
        this.aiC = true;
        this.ajd = true;
        this.aje = true;
        this.BR = 1;
    }

    StreetViewPanoramaOptions(int versionCode, StreetViewPanoramaCamera camera, String panoId, LatLng position, Integer radius, byte userNavigationEnabled, byte zoomGesturesEnabled, byte panningGesturesEnabled, byte streetNamesEnabled, byte useViewLifecycleInFragment) {
        this.ajc = true;
        this.aiC = true;
        this.ajd = true;
        this.aje = true;
        this.BR = versionCode;
        this.aiY = camera;
        this.aja = position;
        this.ajb = radius;
        this.aiZ = panoId;
        this.ajc = com.google.android.gms.maps.internal.a.a(userNavigationEnabled);
        this.aiC = com.google.android.gms.maps.internal.a.a(zoomGesturesEnabled);
        this.ajd = com.google.android.gms.maps.internal.a.a(panningGesturesEnabled);
        this.aje = com.google.android.gms.maps.internal.a.a(streetNamesEnabled);
        this.aiw = com.google.android.gms.maps.internal.a.a(useViewLifecycleInFragment);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Boolean getPanningGesturesEnabled() {
        return this.ajd;
    }

    public String getPanoramaId() {
        return this.aiZ;
    }

    public LatLng getPosition() {
        return this.aja;
    }

    public Integer getRadius() {
        return this.ajb;
    }

    public Boolean getStreetNamesEnabled() {
        return this.aje;
    }

    public StreetViewPanoramaCamera getStreetViewPanoramaCamera() {
        return this.aiY;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.aiw;
    }

    public Boolean getUserNavigationEnabled() {
        return this.ajc;
    }

    int getVersionCode() {
        return this.BR;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.aiC;
    }

    byte mC() {
        return com.google.android.gms.maps.internal.a.c(this.ajc);
    }

    byte mD() {
        return com.google.android.gms.maps.internal.a.c(this.ajd);
    }

    byte mE() {
        return com.google.android.gms.maps.internal.a.c(this.aje);
    }

    byte mq() {
        return com.google.android.gms.maps.internal.a.c(this.aiw);
    }

    byte mu() {
        return com.google.android.gms.maps.internal.a.c(this.aiC);
    }

    public StreetViewPanoramaOptions panningGesturesEnabled(boolean enabled) {
        this.ajd = Boolean.valueOf(enabled);
        return this;
    }

    public StreetViewPanoramaOptions panoramaCamera(StreetViewPanoramaCamera camera) {
        this.aiY = camera;
        return this;
    }

    public StreetViewPanoramaOptions panoramaId(String panoId) {
        this.aiZ = panoId;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng position) {
        this.aja = position;
        return this;
    }

    public StreetViewPanoramaOptions position(LatLng position, Integer radius) {
        this.aja = position;
        this.ajb = radius;
        return this;
    }

    public StreetViewPanoramaOptions streetNamesEnabled(boolean enabled) {
        this.aje = Boolean.valueOf(enabled);
        return this;
    }

    public StreetViewPanoramaOptions useViewLifecycleInFragment(boolean useViewLifecycleInFragment) {
        this.aiw = Boolean.valueOf(useViewLifecycleInFragment);
        return this;
    }

    public StreetViewPanoramaOptions userNavigationEnabled(boolean enabled) {
        this.ajc = Boolean.valueOf(enabled);
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        c.a(this, out, flags);
    }

    public StreetViewPanoramaOptions zoomGesturesEnabled(boolean enabled) {
        this.aiC = Boolean.valueOf(enabled);
        return this;
    }
}
