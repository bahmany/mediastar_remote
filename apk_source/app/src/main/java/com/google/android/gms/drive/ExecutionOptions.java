package com.google.android.gms.drive;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.internal.q;

/* loaded from: classes.dex */
public final class ExecutionOptions {
    public static final int CONFLICT_STRATEGY_KEEP_REMOTE = 1;
    public static final int CONFLICT_STRATEGY_OVERWRITE_REMOTE = 0;
    public static final int MAX_TRACKING_TAG_STRING_LENGTH = 65536;
    private final String Nf;
    private final boolean Ng;
    private final int Nh;

    public static final class Builder {
        private String Nf;
        private boolean Ng;
        private int Nh = 0;

        public ExecutionOptions build() {
            if (this.Nh != 1 || this.Ng) {
                return new ExecutionOptions(this.Nf, this.Ng, this.Nh);
            }
            throw new IllegalStateException("Cannot use CONFLICT_STRATEGY_KEEP_REMOTE without requesting completion notifications");
        }

        public Builder setConflictStrategy(int strategy) {
            if (!ExecutionOptions.aW(strategy)) {
                throw new IllegalArgumentException("Unrecognized value for conflict strategy: " + strategy);
            }
            this.Nh = strategy;
            return this;
        }

        public Builder setNotifyOnCompletion(boolean notify) {
            this.Ng = notify;
            return this;
        }

        public Builder setTrackingTag(String trackingTag) {
            if (!ExecutionOptions.bh(trackingTag)) {
                throw new IllegalArgumentException(String.format("trackingTag must not be null nor empty, and the length must be <= the maximum length (%s)", 65536));
            }
            this.Nf = trackingTag;
            return this;
        }
    }

    private ExecutionOptions(String trackingTag, boolean notifyOnCompletion, int conflictStrategy) {
        this.Nf = trackingTag;
        this.Ng = notifyOnCompletion;
        this.Nh = conflictStrategy;
    }

    /* synthetic */ ExecutionOptions(String x0, boolean x1, int x2, AnonymousClass1 x3) {
        this(x0, x1, x2);
    }

    public static void a(GoogleApiClient googleApiClient, ExecutionOptions executionOptions) {
        q qVar = (q) googleApiClient.a(Drive.CU);
        if (executionOptions.hP() && !qVar.ib()) {
            throw new IllegalStateException("Application must define an exported DriveEventService subclass in AndroidManifest.xml to be notified on completion");
        }
    }

    public static boolean aV(int i) {
        switch (i) {
            case 1:
                return true;
            default:
                return false;
        }
    }

    public static boolean aW(int i) {
        switch (i) {
            case 0:
            case 1:
                return true;
            default:
                return false;
        }
    }

    public static boolean bh(String str) {
        return (str == null || str.isEmpty() || str.length() > 65536) ? false : true;
    }

    public String hO() {
        return this.Nf;
    }

    public boolean hP() {
        return this.Ng;
    }

    public int hQ() {
        return this.Nh;
    }
}
