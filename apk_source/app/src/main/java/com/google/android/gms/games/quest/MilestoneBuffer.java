package com.google.android.gms.games.quest;

import com.google.android.gms.common.data.DataBuffer;

/* loaded from: classes.dex */
public final class MilestoneBuffer extends DataBuffer<Milestone> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.gms.common.data.DataBuffer
    public Milestone get(int position) {
        return new MilestoneRef(this.IC, position);
    }
}
