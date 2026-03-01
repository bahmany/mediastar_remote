package com.google.android.gms.games.leaderboard;

import android.os.Bundle;

/* loaded from: classes.dex */
public final class LeaderboardScoreBufferHeader {
    private final Bundle MZ;

    public static final class Builder {
        private Builder() {
        }
    }

    public LeaderboardScoreBufferHeader(Bundle bundle) {
        this.MZ = bundle == null ? new Bundle() : bundle;
    }

    public Bundle lz() {
        return this.MZ;
    }
}
