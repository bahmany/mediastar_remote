package com.google.android.gms.location;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Comparator;

/* loaded from: classes.dex */
public class DetectedActivity implements SafeParcelable {
    public static final int IN_VEHICLE = 0;
    public static final int ON_BICYCLE = 1;
    public static final int ON_FOOT = 2;
    public static final int RUNNING = 8;
    public static final int STILL = 3;
    public static final int TILTING = 5;
    public static final int UNKNOWN = 4;
    public static final int WALKING = 7;
    private final int BR;
    int adU;
    int adV;
    public static final Comparator<DetectedActivity> adT = new Comparator<DetectedActivity>() { // from class: com.google.android.gms.location.DetectedActivity.1
        AnonymousClass1() {
        }

        @Override // java.util.Comparator
        /* renamed from: a */
        public int compare(DetectedActivity detectedActivity, DetectedActivity detectedActivity2) {
            int iCompareTo = Integer.valueOf(detectedActivity2.getConfidence()).compareTo(Integer.valueOf(detectedActivity.getConfidence()));
            return iCompareTo == 0 ? Integer.valueOf(detectedActivity.getType()).compareTo(Integer.valueOf(detectedActivity2.getType())) : iCompareTo;
        }
    };
    public static final DetectedActivityCreator CREATOR = new DetectedActivityCreator();

    /* renamed from: com.google.android.gms.location.DetectedActivity$1 */
    static class AnonymousClass1 implements Comparator<DetectedActivity> {
        AnonymousClass1() {
        }

        @Override // java.util.Comparator
        /* renamed from: a */
        public int compare(DetectedActivity detectedActivity, DetectedActivity detectedActivity2) {
            int iCompareTo = Integer.valueOf(detectedActivity2.getConfidence()).compareTo(Integer.valueOf(detectedActivity.getConfidence()));
            return iCompareTo == 0 ? Integer.valueOf(detectedActivity.getType()).compareTo(Integer.valueOf(detectedActivity2.getType())) : iCompareTo;
        }
    }

    public DetectedActivity(int activityType, int confidence) {
        this.BR = 1;
        this.adU = activityType;
        this.adV = confidence;
    }

    public DetectedActivity(int versionCode, int activityType, int confidence) {
        this.BR = versionCode;
        this.adU = activityType;
        this.adV = confidence;
    }

    private int cw(int i) {
        if (i > 9) {
            return 4;
        }
        return i;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getConfidence() {
        return this.adV;
    }

    public int getType() {
        return cw(this.adU);
    }

    public int getVersionCode() {
        return this.BR;
    }

    public String toString() {
        return "DetectedActivity [type=" + getType() + ", confidence=" + this.adV + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        DetectedActivityCreator.a(this, out, flags);
    }
}
