package com.google.android.gms.location;

import android.content.Intent;
import android.os.Parcel;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class ActivityRecognitionResult implements SafeParcelable {
    public static final ActivityRecognitionResultCreator CREATOR = new ActivityRecognitionResultCreator();
    public static final String EXTRA_ACTIVITY_RESULT = "com.google.android.location.internal.EXTRA_ACTIVITY_RESULT";
    private final int BR;
    List<DetectedActivity> adQ;
    long adR;
    long adS;

    public ActivityRecognitionResult(int versionCode, List<DetectedActivity> probableActivities, long timeMillis, long elapsedRealtimeMillis) {
        this.BR = 1;
        this.adQ = probableActivities;
        this.adR = timeMillis;
        this.adS = elapsedRealtimeMillis;
    }

    public ActivityRecognitionResult(DetectedActivity mostProbableActivity, long time, long elapsedRealtimeMillis) {
        this((List<DetectedActivity>) Collections.singletonList(mostProbableActivity), time, elapsedRealtimeMillis);
    }

    public ActivityRecognitionResult(List<DetectedActivity> probableActivities, long time, long elapsedRealtimeMillis) {
        boolean z = false;
        n.b(probableActivities != null && probableActivities.size() > 0, "Must have at least 1 detected activity");
        if (time > 0 && elapsedRealtimeMillis > 0) {
            z = true;
        }
        n.b(z, "Must set times");
        this.BR = 1;
        this.adQ = probableActivities;
        this.adR = time;
        this.adS = elapsedRealtimeMillis;
    }

    public static ActivityRecognitionResult extractResult(Intent intent) {
        if (!hasResult(intent)) {
            return null;
        }
        Object obj = intent.getExtras().get(EXTRA_ACTIVITY_RESULT);
        if (!(obj instanceof byte[])) {
            if (obj instanceof ActivityRecognitionResult) {
                return (ActivityRecognitionResult) obj;
            }
            return null;
        }
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.unmarshall((byte[]) obj, 0, ((byte[]) obj).length);
        parcelObtain.setDataPosition(0);
        return CREATOR.createFromParcel(parcelObtain);
    }

    public static boolean hasResult(Intent intent) {
        if (intent == null) {
            return false;
        }
        return intent.hasExtra(EXTRA_ACTIVITY_RESULT);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getActivityConfidence(int activityType) {
        for (DetectedActivity detectedActivity : this.adQ) {
            if (detectedActivity.getType() == activityType) {
                return detectedActivity.getConfidence();
            }
        }
        return 0;
    }

    public long getElapsedRealtimeMillis() {
        return this.adS;
    }

    public DetectedActivity getMostProbableActivity() {
        return this.adQ.get(0);
    }

    public List<DetectedActivity> getProbableActivities() {
        return this.adQ;
    }

    public long getTime() {
        return this.adR;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public String toString() {
        return "ActivityRecognitionResult [probableActivities=" + this.adQ + ", timeMillis=" + this.adR + ", elapsedRealtimeMillis=" + this.adS + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        ActivityRecognitionResultCreator.a(this, out, flags);
    }
}
