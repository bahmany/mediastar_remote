package com.google.android.gms.auth.api;

import android.app.PendingIntent;
import com.google.android.gms.common.api.Status;

/* loaded from: classes.dex */
public class GoogleAuthApiException extends Exception {
    private Status CM;
    private PendingIntent mPendingIntent;

    public GoogleAuthApiException(String message, Status status) {
        super(message);
        this.CM = status;
    }

    public GoogleAuthApiException(String message, Status status, PendingIntent pendingIntent) {
        super(message);
        this.CM = status;
        this.mPendingIntent = pendingIntent;
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public Status getStatus() {
        return this.CM;
    }

    public boolean isUserRecoverable() {
        return this.mPendingIntent != null;
    }
}
