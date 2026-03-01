package com.google.android.gms.location;

import android.os.Parcel;
import android.os.SystemClock;
import android.support.v7.internal.widget.ActivityChooserView;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class LocationRequest implements SafeParcelable {
    public static final b CREATOR = new b();
    public static final int PRIORITY_BALANCED_POWER_ACCURACY = 102;
    public static final int PRIORITY_HIGH_ACCURACY = 100;
    public static final int PRIORITY_LOW_POWER = 104;
    public static final int PRIORITY_NO_POWER = 105;
    private final int BR;
    boolean Uz;
    long adX;
    long aeh;
    long aei;
    int aej;
    float aek;
    long ael;
    int mPriority;

    public LocationRequest() {
        this.BR = 1;
        this.mPriority = 102;
        this.aeh = 3600000L;
        this.aei = 600000L;
        this.Uz = false;
        this.adX = Long.MAX_VALUE;
        this.aej = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.aek = 0.0f;
        this.ael = 0L;
    }

    LocationRequest(int versionCode, int priority, long interval, long fastestInterval, boolean explicitFastestInterval, long expireAt, int numUpdates, float smallestDisplacement, long maxWaitTime) {
        this.BR = versionCode;
        this.mPriority = priority;
        this.aeh = interval;
        this.aei = fastestInterval;
        this.Uz = explicitFastestInterval;
        this.adX = expireAt;
        this.aej = numUpdates;
        this.aek = smallestDisplacement;
        this.ael = maxWaitTime;
    }

    private static void a(float f) {
        if (f < 0.0f) {
            throw new IllegalArgumentException("invalid displacement: " + f);
        }
    }

    public static LocationRequest create() {
        return new LocationRequest();
    }

    private static void ea(int i) {
        switch (i) {
            case 100:
            case 102:
            case 104:
            case 105:
                return;
            case 101:
            case 103:
            default:
                throw new IllegalArgumentException("invalid quality: " + i);
        }
    }

    public static String eb(int i) {
        switch (i) {
            case 100:
                return "PRIORITY_HIGH_ACCURACY";
            case 101:
            case 103:
            default:
                return "???";
            case 102:
                return "PRIORITY_BALANCED_POWER_ACCURACY";
            case 104:
                return "PRIORITY_LOW_POWER";
            case 105:
                return "PRIORITY_NO_POWER";
        }
    }

    private static void v(long j) {
        if (j < 0) {
            throw new IllegalArgumentException("invalid interval: " + j);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LocationRequest)) {
            return false;
        }
        LocationRequest locationRequest = (LocationRequest) object;
        return this.mPriority == locationRequest.mPriority && this.aeh == locationRequest.aeh && this.aei == locationRequest.aei && this.Uz == locationRequest.Uz && this.adX == locationRequest.adX && this.aej == locationRequest.aej && this.aek == locationRequest.aek;
    }

    public long getExpirationTime() {
        return this.adX;
    }

    public long getFastestInterval() {
        return this.aei;
    }

    public long getInterval() {
        return this.aeh;
    }

    public int getNumUpdates() {
        return this.aej;
    }

    public int getPriority() {
        return this.mPriority;
    }

    public float getSmallestDisplacement() {
        return this.aek;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.mPriority), Long.valueOf(this.aeh), Long.valueOf(this.aei), Boolean.valueOf(this.Uz), Long.valueOf(this.adX), Integer.valueOf(this.aej), Float.valueOf(this.aek));
    }

    public LocationRequest setExpirationDuration(long millis) {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (millis > Long.MAX_VALUE - jElapsedRealtime) {
            this.adX = Long.MAX_VALUE;
        } else {
            this.adX = jElapsedRealtime + millis;
        }
        if (this.adX < 0) {
            this.adX = 0L;
        }
        return this;
    }

    public LocationRequest setExpirationTime(long millis) {
        this.adX = millis;
        if (this.adX < 0) {
            this.adX = 0L;
        }
        return this;
    }

    public LocationRequest setFastestInterval(long millis) {
        v(millis);
        this.Uz = true;
        this.aei = millis;
        return this;
    }

    public LocationRequest setInterval(long millis) {
        v(millis);
        this.aeh = millis;
        if (!this.Uz) {
            this.aei = (long) (this.aeh / 6.0d);
        }
        return this;
    }

    public LocationRequest setNumUpdates(int numUpdates) {
        if (numUpdates <= 0) {
            throw new IllegalArgumentException("invalid numUpdates: " + numUpdates);
        }
        this.aej = numUpdates;
        return this;
    }

    public LocationRequest setPriority(int priority) {
        ea(priority);
        this.mPriority = priority;
        return this;
    }

    public LocationRequest setSmallestDisplacement(float smallestDisplacementMeters) {
        a(smallestDisplacementMeters);
        this.aek = smallestDisplacementMeters;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Request[").append(eb(this.mPriority));
        if (this.mPriority != 105) {
            sb.append(" requested=");
            sb.append(this.aeh + "ms");
        }
        sb.append(" fastest=");
        sb.append(this.aei + "ms");
        if (this.adX != Long.MAX_VALUE) {
            long jElapsedRealtime = this.adX - SystemClock.elapsedRealtime();
            sb.append(" expireIn=");
            sb.append(jElapsedRealtime + "ms");
        }
        if (this.aej != Integer.MAX_VALUE) {
            sb.append(" num=").append(this.aej);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        b.a(this, parcel, flags);
    }
}
