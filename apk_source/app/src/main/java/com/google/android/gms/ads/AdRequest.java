package com.google.android.gms.ads;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.NetworkExtras;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.internal.bg;
import java.util.Date;
import java.util.Set;

/* loaded from: classes.dex */
public final class AdRequest {
    public static final String DEVICE_ID_EMULATOR = bg.DEVICE_ID_EMULATOR;
    public static final int ERROR_CODE_INTERNAL_ERROR = 0;
    public static final int ERROR_CODE_INVALID_REQUEST = 1;
    public static final int ERROR_CODE_NETWORK_ERROR = 2;
    public static final int ERROR_CODE_NO_FILL = 3;
    public static final int GENDER_FEMALE = 2;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_UNKNOWN = 0;
    public static final int MAX_CONTENT_URL_LENGTH = 512;
    private final bg ld;

    public static final class Builder {
        private final bg.a le = new bg.a();

        public Builder addCustomEventExtrasBundle(Class<? extends CustomEvent> adapterClass, Bundle customEventExtras) {
            this.le.b(adapterClass, customEventExtras);
            return this;
        }

        public Builder addKeyword(String keyword) {
            this.le.r(keyword);
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

        public AdRequest build() {
            return new AdRequest(this);
        }

        public Builder setBirthday(Date birthday) {
            this.le.a(birthday);
            return this;
        }

        public Builder setContentUrl(String contentUrl) {
            n.b(contentUrl, (Object) "Content URL must be non-null.");
            n.b(contentUrl, (Object) "Content URL must be non-empty.");
            n.b(contentUrl.length() <= 512, "Content URL must not exceed %d in length.  Provided length was %d.", 512, Integer.valueOf(contentUrl.length()));
            this.le.t(contentUrl);
            return this;
        }

        public Builder setGender(int gender) {
            this.le.g(gender);
            return this;
        }

        public Builder setLocation(Location location) {
            this.le.a(location);
            return this;
        }

        public Builder tagForChildDirectedTreatment(boolean tagForChildDirectedTreatment) {
            this.le.h(tagForChildDirectedTreatment);
            return this;
        }
    }

    private AdRequest(Builder builder) {
        this.ld = new bg(builder.le);
    }

    /* synthetic */ AdRequest(Builder x0, AnonymousClass1 x1) {
        this(x0);
    }

    bg V() {
        return this.ld;
    }

    public Date getBirthday() {
        return this.ld.getBirthday();
    }

    public String getContentUrl() {
        return this.ld.getContentUrl();
    }

    public <T extends CustomEvent> Bundle getCustomEventExtrasBundle(Class<T> adapterClass) {
        return this.ld.getCustomEventExtrasBundle(adapterClass);
    }

    public int getGender() {
        return this.ld.getGender();
    }

    public Set<String> getKeywords() {
        return this.ld.getKeywords();
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

    public boolean isTestDevice(Context context) {
        return this.ld.isTestDevice(context);
    }
}
