package com.google.android.gms.cast;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.ik;
import java.util.Locale;

/* loaded from: classes.dex */
public class LaunchOptions implements SafeParcelable {
    public static final Parcelable.Creator<LaunchOptions> CREATOR = new c();
    private final int BR;
    private boolean Fb;
    private String Fc;

    public static final class Builder {
        private LaunchOptions Fd = new LaunchOptions();

        public LaunchOptions build() {
            return this.Fd;
        }

        public Builder setLocale(Locale locale) {
            this.Fd.setLanguage(ik.b(locale));
            return this;
        }

        public Builder setRelaunchIfRunning(boolean relaunchIfRunning) {
            this.Fd.setRelaunchIfRunning(relaunchIfRunning);
            return this;
        }
    }

    public LaunchOptions() {
        this(1, false, ik.b(Locale.getDefault()));
    }

    LaunchOptions(int versionCode, boolean relaunchIfRunning, String language) {
        this.BR = versionCode;
        this.Fb = relaunchIfRunning;
        this.Fc = language;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LaunchOptions)) {
            return false;
        }
        LaunchOptions launchOptions = (LaunchOptions) obj;
        return this.Fb == launchOptions.Fb && ik.a(this.Fc, launchOptions.Fc);
    }

    public String getLanguage() {
        return this.Fc;
    }

    public boolean getRelaunchIfRunning() {
        return this.Fb;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(Boolean.valueOf(this.Fb), this.Fc);
    }

    public void setLanguage(String language) {
        this.Fc = language;
    }

    public void setRelaunchIfRunning(boolean relaunchIfRunning) {
        this.Fb = relaunchIfRunning;
    }

    public String toString() {
        return String.format("LaunchOptions(relaunchIfRunning=%b, language=%s)", Boolean.valueOf(this.Fb), this.Fc);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        c.a(this, out, flags);
    }
}
