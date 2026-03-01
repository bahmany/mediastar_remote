package com.google.android.gms.games.leaderboard;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.games.internal.constants.TimeSpan;
import java.util.HashMap;

/* loaded from: classes.dex */
public final class ScoreSubmissionData {
    private static final String[] abh = {"leaderboardId", "playerId", "timeSpan", "hasResult", "rawScore", "formattedScore", "newBest", "scoreTag"};
    private int HF;
    private String Vz;
    private HashMap<Integer, Result> abN = new HashMap<>();
    private String abj;

    public static final class Result {
        public final String formattedScore;
        public final boolean newBest;
        public final long rawScore;
        public final String scoreTag;

        public Result(long rawScore, String formattedScore, String scoreTag, boolean newBest) {
            this.rawScore = rawScore;
            this.formattedScore = formattedScore;
            this.scoreTag = scoreTag;
            this.newBest = newBest;
        }

        public String toString() {
            return m.h(this).a("RawScore", Long.valueOf(this.rawScore)).a("FormattedScore", this.formattedScore).a("ScoreTag", this.scoreTag).a("NewBest", Boolean.valueOf(this.newBest)).toString();
        }
    }

    public ScoreSubmissionData(DataHolder dataHolder) {
        this.HF = dataHolder.getStatusCode();
        int count = dataHolder.getCount();
        n.K(count == 3);
        for (int i = 0; i < count; i++) {
            int iAr = dataHolder.ar(i);
            if (i == 0) {
                this.abj = dataHolder.c("leaderboardId", i, iAr);
                this.Vz = dataHolder.c("playerId", i, iAr);
            }
            if (dataHolder.d("hasResult", i, iAr)) {
                a(new Result(dataHolder.a("rawScore", i, iAr), dataHolder.c("formattedScore", i, iAr), dataHolder.c("scoreTag", i, iAr), dataHolder.d("newBest", i, iAr)), dataHolder.b("timeSpan", i, iAr));
            }
        }
    }

    private void a(Result result, int i) {
        this.abN.put(Integer.valueOf(i), result);
    }

    public String getLeaderboardId() {
        return this.abj;
    }

    public String getPlayerId() {
        return this.Vz;
    }

    public Result getScoreResult(int timeSpan) {
        return this.abN.get(Integer.valueOf(timeSpan));
    }

    public String toString() {
        m.a aVarA = m.h(this).a("PlayerId", this.Vz).a("StatusCode", Integer.valueOf(this.HF));
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= 3) {
                return aVarA.toString();
            }
            Result result = this.abN.get(Integer.valueOf(i2));
            aVarA.a("TimesSpan", TimeSpan.dH(i2));
            aVarA.a("Result", result == null ? "null" : result.toString());
            i = i2 + 1;
        }
    }
}
