package com.google.android.gms.maps;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.maps.internal.v;
import com.google.android.gms.maps.model.CameraPosition;

/* loaded from: classes.dex */
public final class GoogleMapOptions implements SafeParcelable {
    public static final a CREATOR = new a();
    private final int BR;
    private Boolean aiA;
    private Boolean aiB;
    private Boolean aiC;
    private Boolean aiD;
    private Boolean aiE;
    private Boolean aiv;
    private Boolean aiw;
    private int aix;
    private CameraPosition aiy;
    private Boolean aiz;

    public GoogleMapOptions() {
        this.aix = -1;
        this.BR = 1;
    }

    GoogleMapOptions(int versionCode, byte zOrderOnTop, byte useViewLifecycleInFragment, int mapType, CameraPosition camera, byte zoomControlsEnabled, byte compassEnabled, byte scrollGesturesEnabled, byte zoomGesturesEnabled, byte tiltGesturesEnabled, byte rotateGesturesEnabled) {
        this.aix = -1;
        this.BR = versionCode;
        this.aiv = com.google.android.gms.maps.internal.a.a(zOrderOnTop);
        this.aiw = com.google.android.gms.maps.internal.a.a(useViewLifecycleInFragment);
        this.aix = mapType;
        this.aiy = camera;
        this.aiz = com.google.android.gms.maps.internal.a.a(zoomControlsEnabled);
        this.aiA = com.google.android.gms.maps.internal.a.a(compassEnabled);
        this.aiB = com.google.android.gms.maps.internal.a.a(scrollGesturesEnabled);
        this.aiC = com.google.android.gms.maps.internal.a.a(zoomGesturesEnabled);
        this.aiD = com.google.android.gms.maps.internal.a.a(tiltGesturesEnabled);
        this.aiE = com.google.android.gms.maps.internal.a.a(rotateGesturesEnabled);
    }

    public static GoogleMapOptions createFromAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return null;
        }
        TypedArray typedArrayObtainAttributes = context.getResources().obtainAttributes(attrs, R.styleable.MapAttrs);
        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        if (typedArrayObtainAttributes.hasValue(R.styleable.MapAttrs_mapType)) {
            googleMapOptions.mapType(typedArrayObtainAttributes.getInt(R.styleable.MapAttrs_mapType, -1));
        }
        if (typedArrayObtainAttributes.hasValue(R.styleable.MapAttrs_zOrderOnTop)) {
            googleMapOptions.zOrderOnTop(typedArrayObtainAttributes.getBoolean(R.styleable.MapAttrs_zOrderOnTop, false));
        }
        if (typedArrayObtainAttributes.hasValue(R.styleable.MapAttrs_useViewLifecycle)) {
            googleMapOptions.useViewLifecycleInFragment(typedArrayObtainAttributes.getBoolean(R.styleable.MapAttrs_useViewLifecycle, false));
        }
        if (typedArrayObtainAttributes.hasValue(R.styleable.MapAttrs_uiCompass)) {
            googleMapOptions.compassEnabled(typedArrayObtainAttributes.getBoolean(R.styleable.MapAttrs_uiCompass, true));
        }
        if (typedArrayObtainAttributes.hasValue(R.styleable.MapAttrs_uiRotateGestures)) {
            googleMapOptions.rotateGesturesEnabled(typedArrayObtainAttributes.getBoolean(R.styleable.MapAttrs_uiRotateGestures, true));
        }
        if (typedArrayObtainAttributes.hasValue(R.styleable.MapAttrs_uiScrollGestures)) {
            googleMapOptions.scrollGesturesEnabled(typedArrayObtainAttributes.getBoolean(R.styleable.MapAttrs_uiScrollGestures, true));
        }
        if (typedArrayObtainAttributes.hasValue(R.styleable.MapAttrs_uiTiltGestures)) {
            googleMapOptions.tiltGesturesEnabled(typedArrayObtainAttributes.getBoolean(R.styleable.MapAttrs_uiTiltGestures, true));
        }
        if (typedArrayObtainAttributes.hasValue(R.styleable.MapAttrs_uiZoomGestures)) {
            googleMapOptions.zoomGesturesEnabled(typedArrayObtainAttributes.getBoolean(R.styleable.MapAttrs_uiZoomGestures, true));
        }
        if (typedArrayObtainAttributes.hasValue(R.styleable.MapAttrs_uiZoomControls)) {
            googleMapOptions.zoomControlsEnabled(typedArrayObtainAttributes.getBoolean(R.styleable.MapAttrs_uiZoomControls, true));
        }
        googleMapOptions.camera(CameraPosition.createFromAttributes(context, attrs));
        typedArrayObtainAttributes.recycle();
        return googleMapOptions;
    }

    public GoogleMapOptions camera(CameraPosition camera) {
        this.aiy = camera;
        return this;
    }

    public GoogleMapOptions compassEnabled(boolean enabled) {
        this.aiA = Boolean.valueOf(enabled);
        return this;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CameraPosition getCamera() {
        return this.aiy;
    }

    public Boolean getCompassEnabled() {
        return this.aiA;
    }

    public int getMapType() {
        return this.aix;
    }

    public Boolean getRotateGesturesEnabled() {
        return this.aiE;
    }

    public Boolean getScrollGesturesEnabled() {
        return this.aiB;
    }

    public Boolean getTiltGesturesEnabled() {
        return this.aiD;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.aiw;
    }

    int getVersionCode() {
        return this.BR;
    }

    public Boolean getZOrderOnTop() {
        return this.aiv;
    }

    public Boolean getZoomControlsEnabled() {
        return this.aiz;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.aiC;
    }

    public GoogleMapOptions mapType(int mapType) {
        this.aix = mapType;
        return this;
    }

    byte mp() {
        return com.google.android.gms.maps.internal.a.c(this.aiv);
    }

    byte mq() {
        return com.google.android.gms.maps.internal.a.c(this.aiw);
    }

    byte mr() {
        return com.google.android.gms.maps.internal.a.c(this.aiz);
    }

    byte ms() {
        return com.google.android.gms.maps.internal.a.c(this.aiA);
    }

    byte mt() {
        return com.google.android.gms.maps.internal.a.c(this.aiB);
    }

    byte mu() {
        return com.google.android.gms.maps.internal.a.c(this.aiC);
    }

    byte mv() {
        return com.google.android.gms.maps.internal.a.c(this.aiD);
    }

    byte mw() {
        return com.google.android.gms.maps.internal.a.c(this.aiE);
    }

    public GoogleMapOptions rotateGesturesEnabled(boolean enabled) {
        this.aiE = Boolean.valueOf(enabled);
        return this;
    }

    public GoogleMapOptions scrollGesturesEnabled(boolean enabled) {
        this.aiB = Boolean.valueOf(enabled);
        return this;
    }

    public GoogleMapOptions tiltGesturesEnabled(boolean enabled) {
        this.aiD = Boolean.valueOf(enabled);
        return this;
    }

    public GoogleMapOptions useViewLifecycleInFragment(boolean useViewLifecycleInFragment) {
        this.aiw = Boolean.valueOf(useViewLifecycleInFragment);
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (v.mK()) {
            b.a(this, out, flags);
        } else {
            a.a(this, out, flags);
        }
    }

    public GoogleMapOptions zOrderOnTop(boolean zOrderOnTop) {
        this.aiv = Boolean.valueOf(zOrderOnTop);
        return this;
    }

    public GoogleMapOptions zoomControlsEnabled(boolean enabled) {
        this.aiz = Boolean.valueOf(enabled);
        return this;
    }

    public GoogleMapOptions zoomGesturesEnabled(boolean enabled) {
        this.aiC = Boolean.valueOf(enabled);
        return this;
    }
}
