package com.google.android.gms.games.quest;

import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class MilestoneEntity implements SafeParcelable, Milestone {
    public static final MilestoneEntityCreator CREATOR = new MilestoneEntityCreator();
    private final int BR;
    private final String Wb;
    private final String Xj;
    private final long acD;
    private final long acE;
    private final byte[] acF;
    private final int mState;

    MilestoneEntity(int versionCode, String milestoneId, long currentProgress, long targetProgress, byte[] completionBlob, int state, String eventId) {
        this.BR = versionCode;
        this.Xj = milestoneId;
        this.acD = currentProgress;
        this.acE = targetProgress;
        this.acF = completionBlob;
        this.mState = state;
        this.Wb = eventId;
    }

    public MilestoneEntity(Milestone milestone) {
        this.BR = 4;
        this.Xj = milestone.getMilestoneId();
        this.acD = milestone.getCurrentProgress();
        this.acE = milestone.getTargetProgress();
        this.mState = milestone.getState();
        this.Wb = milestone.getEventId();
        byte[] completionRewardData = milestone.getCompletionRewardData();
        if (completionRewardData == null) {
            this.acF = null;
        } else {
            this.acF = new byte[completionRewardData.length];
            System.arraycopy(completionRewardData, 0, this.acF, 0, completionRewardData.length);
        }
    }

    static int a(Milestone milestone) {
        return m.hashCode(milestone.getMilestoneId(), Long.valueOf(milestone.getCurrentProgress()), Long.valueOf(milestone.getTargetProgress()), Integer.valueOf(milestone.getState()), milestone.getEventId());
    }

    static boolean a(Milestone milestone, Object obj) {
        if (!(obj instanceof Milestone)) {
            return false;
        }
        if (milestone == obj) {
            return true;
        }
        Milestone milestone2 = (Milestone) obj;
        return m.equal(milestone2.getMilestoneId(), milestone.getMilestoneId()) && m.equal(Long.valueOf(milestone2.getCurrentProgress()), Long.valueOf(milestone.getCurrentProgress())) && m.equal(Long.valueOf(milestone2.getTargetProgress()), Long.valueOf(milestone.getTargetProgress())) && m.equal(Integer.valueOf(milestone2.getState()), Integer.valueOf(milestone.getState())) && m.equal(milestone2.getEventId(), milestone.getEventId());
    }

    static String b(Milestone milestone) {
        return m.h(milestone).a("MilestoneId", milestone.getMilestoneId()).a("CurrentProgress", Long.valueOf(milestone.getCurrentProgress())).a("TargetProgress", Long.valueOf(milestone.getTargetProgress())).a("State", Integer.valueOf(milestone.getState())).a("CompletionRewardData", milestone.getCompletionRewardData()).a("EventId", milestone.getEventId()).toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.gms.common.data.Freezable
    public Milestone freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.quest.Milestone
    public byte[] getCompletionRewardData() {
        return this.acF;
    }

    @Override // com.google.android.gms.games.quest.Milestone
    public long getCurrentProgress() {
        return this.acD;
    }

    @Override // com.google.android.gms.games.quest.Milestone
    public String getEventId() {
        return this.Wb;
    }

    @Override // com.google.android.gms.games.quest.Milestone
    public String getMilestoneId() {
        return this.Xj;
    }

    @Override // com.google.android.gms.games.quest.Milestone
    public int getState() {
        return this.mState;
    }

    @Override // com.google.android.gms.games.quest.Milestone
    public long getTargetProgress() {
        return this.acE;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return a(this);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    public String toString() {
        return b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        MilestoneEntityCreator.a(this, out, flags);
    }
}
