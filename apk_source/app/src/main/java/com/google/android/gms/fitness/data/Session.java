package com.google.android.gms.fitness.data;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessIntents;
import com.google.android.gms.plus.PlusShare;

/* loaded from: classes.dex */
public class Session implements SafeParcelable {
    public static final Parcelable.Creator<Session> CREATOR = new p();
    private final int BR;
    private final long KL;
    private final a SJ;
    private final long Si;
    private final int Sv;
    private final String Tf;
    private final String Tg;
    private final String mName;

    public static class Builder {
        private a SJ;
        private String Tf;
        private String Tg;
        private long KL = 0;
        private long Si = 0;
        private String mName = null;
        private int Sv = 4;

        public Session build() {
            com.google.android.gms.common.internal.n.a(this.KL > 0, "Start time should be specified.");
            com.google.android.gms.common.internal.n.a(this.Si == 0 || this.Si > this.KL, "End time should be later than start time.");
            if (this.Tf == null) {
                this.Tf = (this.mName == null ? "" : this.mName) + this.KL;
            }
            return new Session(this);
        }

        public Builder setActivity(int activity) {
            this.Sv = FitnessActivities.cw(activity);
            return this;
        }

        public Builder setDescription(String description) {
            com.google.android.gms.common.internal.n.b(description.length() <= 1000, "Session description cannot exceed %d characters", 1000);
            this.Tg = description;
            return this;
        }

        public Builder setEndTimeMillis(long endTimeMillis) {
            com.google.android.gms.common.internal.n.a(endTimeMillis >= 0, "End time should be positive.");
            this.Si = endTimeMillis;
            return this;
        }

        public Builder setIdentifier(String identifier) {
            this.Tf = identifier;
            return this;
        }

        public Builder setName(String name) {
            com.google.android.gms.common.internal.n.b(name.length() <= 100, "Session name cannot exceed %d characters", 100);
            this.mName = name;
            return this;
        }

        public Builder setStartTimeMillis(long startTimeMillis) {
            com.google.android.gms.common.internal.n.a(startTimeMillis > 0, "Start time should be positive.");
            this.KL = startTimeMillis;
            return this;
        }
    }

    Session(int versionCode, long startTimeMillis, long endTimeMillis, String name, String identifier, String description, int activity, a application) {
        this.BR = versionCode;
        this.KL = startTimeMillis;
        this.Si = endTimeMillis;
        this.mName = name;
        this.Tf = identifier;
        this.Tg = description;
        this.Sv = activity;
        this.SJ = application;
    }

    private Session(Builder builder) {
        this.BR = 2;
        this.KL = builder.KL;
        this.Si = builder.Si;
        this.mName = builder.mName;
        this.Tf = builder.Tf;
        this.Tg = builder.Tg;
        this.Sv = builder.Sv;
        this.SJ = builder.SJ;
    }

    /* synthetic */ Session(Builder x0, AnonymousClass1 x1) {
        this(x0);
    }

    private boolean a(Session session) {
        return this.KL == session.KL && this.Si == session.Si && com.google.android.gms.common.internal.m.equal(this.mName, session.mName) && com.google.android.gms.common.internal.m.equal(this.Tf, session.Tf) && com.google.android.gms.common.internal.m.equal(this.Tg, session.Tg) && com.google.android.gms.common.internal.m.equal(this.SJ, session.SJ) && this.Sv == session.Sv;
    }

    public static Session extract(Intent intent) {
        if (intent == null) {
            return null;
        }
        return (Session) com.google.android.gms.common.internal.safeparcel.c.a(intent, FitnessIntents.EXTRA_SESSION, CREATOR);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof Session) && a((Session) o));
    }

    public int getActivity() {
        return this.Sv;
    }

    public String getAppPackageName() {
        if (this.SJ == null) {
            return null;
        }
        return this.SJ.getPackageName();
    }

    public String getDescription() {
        return this.Tg;
    }

    public long getEndTimeMillis() {
        return this.Si;
    }

    public String getIdentifier() {
        return this.Tf;
    }

    public String getName() {
        return this.mName;
    }

    public long getStartTimeMillis() {
        return this.KL;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Long.valueOf(this.KL), Long.valueOf(this.Si), this.mName, this.Tf, Integer.valueOf(this.Sv), this.SJ, this.Tg);
    }

    public a iH() {
        return this.SJ;
    }

    public boolean isOngoing() {
        return this.Si == 0;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("startTime", Long.valueOf(this.KL)).a("endTime", Long.valueOf(this.Si)).a("name", this.mName).a("identifier", this.Tf).a(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION, this.Tg).a("activity", Integer.valueOf(this.Sv)).a("application", this.SJ).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        p.a(this, dest, flags);
    }
}
