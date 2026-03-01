package com.google.android.gms.games.leaderboard;

import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.common.data.DataHolder;

/* loaded from: classes.dex */
public final class LeaderboardScoreBuffer extends DataBuffer<LeaderboardScore> {
    private final LeaderboardScoreBufferHeader abn;

    public LeaderboardScoreBuffer(DataHolder dataHolder) {
        super(dataHolder);
        this.abn = new LeaderboardScoreBufferHeader(dataHolder.gz());
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.gms.common.data.DataBuffer
    public LeaderboardScore get(int position) {
        return new LeaderboardScoreRef(this.IC, position);
    }

    public LeaderboardScoreBufferHeader ly() {
        return this.abn;
    }
}
