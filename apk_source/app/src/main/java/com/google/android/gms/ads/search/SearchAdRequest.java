package com.google.android.gms.ads.search;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.android.gms.internal.bg;

/* loaded from: classes.dex */
public final class SearchAdRequest {
    public static final int BORDER_TYPE_DASHED = 1;
    public static final int BORDER_TYPE_DOTTED = 2;
    public static final int BORDER_TYPE_NONE = 0;
    public static final int BORDER_TYPE_SOLID = 3;
    public static final int CALL_BUTTON_COLOR_DARK = 2;
    public static final int CALL_BUTTON_COLOR_LIGHT = 0;
    public static final int CALL_BUTTON_COLOR_MEDIUM = 1;
    public static final String DEVICE_ID_EMULATOR = bg.DEVICE_ID_EMULATOR;
    public static final int ERROR_CODE_INTERNAL_ERROR = 0;
    public static final int ERROR_CODE_INVALID_REQUEST = 1;
    public static final int ERROR_CODE_NETWORK_ERROR = 2;
    public static final int ERROR_CODE_NO_FILL = 3;
    private final bg ld;
    private final int xl;
    private final int xm;
    private final int xn;
    private final int xo;
    private final int xp;
    private final int xq;
    private final int xr;
    private final int xs;
    private final String xt;
    private final int xu;
    private final String xv;
    private final int xw;
    private final int xx;
    private final String xy;

    public static final class Builder {
        private int xl;
        private int xm;
        private int xn;
        private int xo;
        private int xp;
        private int xq;
        private int xs;
        private String xt;
        private int xu;
        private String xv;
        private int xw;
        private int xx;
        private String xy;
        private final bg.a le = new bg.a();
        private int xr = 0;

        public Builder addCustomEventExtrasBundle(Class<? extends CustomEvent> adapterClass, Bundle customEventExtras) {
            this.le.b(adapterClass, customEventExtras);
            return this;
        }

        public Builder addNetworkExtras(NetworkExtras networkExtras) {
            this.le.a(networkExtras);
            return this;
        }

        public Builder addNetworkExtrasBundle(Class<? extends MediationAdapter> adapterClass, Bundle networkExtras) {
            this.le.a(adapterClass, networkExtras);
            return this;
        }

        public Builder addTestDevice(String deviceId) {
            this.le.s(deviceId);
            return this;
        }

        public SearchAdRequest build() {
            return new SearchAdRequest(this);
        }

        public Builder setAnchorTextColor(int anchorTextColor) {
            this.xl = anchorTextColor;
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.xm = backgroundColor;
            this.xn = Color.argb(0, 0, 0, 0);
            this.xo = Color.argb(0, 0, 0, 0);
            return this;
        }

        public Builder setBackgroundGradient(int top, int bottom) {
            this.xm = Color.argb(0, 0, 0, 0);
            this.xn = bottom;
            this.xo = top;
            return this;
        }

        public Builder setBorderColor(int borderColor) {
            this.xp = borderColor;
            return this;
        }

        public Builder setBorderThickness(int borderThickness) {
            this.xq = borderThickness;
            return this;
        }

        public Builder setBorderType(int borderType) {
            this.xr = borderType;
            return this;
        }

        public Builder setCallButtonColor(int callButtonColor) {
            this.xs = callButtonColor;
            return this;
        }

        public Builder setCustomChannels(String channelIds) {
            this.xt = channelIds;
            return this;
        }

        public Builder setDescriptionTextColor(int descriptionTextColor) {
            this.xu = descriptionTextColor;
            return this;
        }

        public Builder setFontFace(String fontFace) {
            this.xv = fontFace;
            return this;
        }

        public Builder setHeaderTextColor(int headerTextColor) {
            this.xw = headerTextColor;
            return this;
        }

        public Builder setHeaderTextSize(int headerTextSize) {
            this.xx = headerTextSize;
            return this;
        }

        public Builder setLocation(Location location) {
            this.le.a(location);
            return this;
        }

        public Builder setQuery(String query) {
            this.xy = query;
            return this;
        }

        public Builder tagForChildDirectedTreatment(boolean tagForChildDirectedTreatment) {
            this.le.h(tagForChildDirectedTreatment);
            return this;
        }
    }

    private SearchAdRequest(Builder builder) {
        this.xl = builder.xl;
        this.xm = builder.xm;
        this.xn = builder.xn;
        this.xo = builder.xo;
        this.xp = builder.xp;
        this.xq = builder.xq;
        this.xr = builder.xr;
        this.xs = builder.xs;
        this.xt = builder.xt;
        this.xu = builder.xu;
        this.xv = builder.xv;
        this.xw = builder.xw;
        this.xx = builder.xx;
        this.xy = builder.xy;
        this.ld = new bg(builder.le, this);
    }

    /* synthetic */ SearchAdRequest(Builder x0, AnonymousClass1 x1) {
        this(x0);
    }

    bg V() {
        return this.ld;
    }

    public int getAnchorTextColor() {
        return this.xl;
    }

    public int getBackgroundColor() {
        return this.xm;
    }

    public int getBackgroundGradientBottom() {
        return this.xn;
    }

    public int getBackgroundGradientTop() {
        return this.xo;
    }

    public int getBorderColor() {
        return this.xp;
    }

    public int getBorderThickness() {
        return this.xq;
    }

    public int getBorderType() {
        return this.xr;
    }

    public int getCallButtonColor() {
        return this.xs;
    }

    public String getCustomChannels() {
        return this.xt;
    }

    public <T extends CustomEvent> Bundle getCustomEventExtrasBundle(Class<T> adapterClass) {
        return this.ld.getCustomEventExtrasBundle(adapterClass);
    }

    public int getDescriptionTextColor() {
        return this.xu;
    }

    public String getFontFace() {
        return this.xv;
    }

    public int getHeaderTextColor() {
        return this.xw;
    }

    public int getHeaderTextSize() {
        return this.xx;
    }

    public Location getLocation() {
        return this.ld.getLocation();
    }

    @Deprecated
    public <T extends NetworkExtras> T getNetworkExtras(Class<T> cls) {
        return (T) this.ld.getNetworkExtras(cls);
    }

    public <T extends MediationAdapter> Bundle getNetworkExtrasBundle(Class<T> adapterClass) {
        return this.ld.getNetworkExtrasBundle(adapterClass);
    }

    public String getQuery() {
        return this.xy;
    }

    public boolean isTestDevice(Context context) {
        return this.ld.isTestDevice(context);
    }
}
