package com.google.android.gms.games.leaderboard;

import com.google.android.gms.common.internal.m;
import com.google.android.gms.games.internal.constants.LeaderboardCollection;
import com.google.android.gms.games.internal.constants.TimeSpan;

/* loaded from: classes.dex */
public final class LeaderboardVariantEntity implements LeaderboardVariant {
    private final int abB;
    private final int abC;
    private final boolean abD;
    private final long abE;
    private final String abF;
    private final long abG;
    private final String abH;
    private final String abI;
    private final long abJ;
    private final String abK;
    private final String abL;
    private final String abM;

    public LeaderboardVariantEntity(LeaderboardVariant variant) {
        this.abB = variant.getTimeSpan();
        this.abC = variant.getCollection();
        this.abD = variant.hasPlayerInfo();
        this.abE = variant.getRawPlayerScore();
        this.abF = variant.getDisplayPlayerScore();
        this.abG = variant.getPlayerRank();
        this.abH = variant.getDisplayPlayerRank();
        this.abI = variant.getPlayerScoreTag();
        this.abJ = variant.getNumScores();
        this.abK = variant.lB();
        this.abL = variant.lC();
        this.abM = variant.lD();
    }

    static int a(LeaderboardVariant leaderboardVariant) {
        return m.hashCode(Integer.valueOf(leaderboardVariant.getTimeSpan()), Integer.valueOf(leaderboardVariant.getCollection()), Boolean.valueOf(leaderboardVariant.hasPlayerInfo()), Long.valueOf(leaderboardVariant.getRawPlayerScore()), leaderboardVariant.getDisplayPlayerScore(), Long.valueOf(leaderboardVariant.getPlayerRank()), leaderboardVariant.getDisplayPlayerRank(), Long.valueOf(leaderboardVariant.getNumScores()), leaderboardVariant.lB(), leaderboardVariant.lD(), leaderboardVariant.lC());
    }

    static boolean a(LeaderboardVariant leaderboardVariant, Object obj) {
        if (!(obj instanceof LeaderboardVariant)) {
            return false;
        }
        if (leaderboardVariant == obj) {
            return true;
        }
        LeaderboardVariant leaderboardVariant2 = (LeaderboardVariant) obj;
        return m.equal(Integer.valueOf(leaderboardVariant2.getTimeSpan()), Integer.valueOf(leaderboardVariant.getTimeSpan())) && m.equal(Integer.valueOf(leaderboardVariant2.getCollection()), Integer.valueOf(leaderboardVariant.getCollection())) && m.equal(Boolean.valueOf(leaderboardVariant2.hasPlayerInfo()), Boolean.valueOf(leaderboardVariant.hasPlayerInfo())) && m.equal(Long.valueOf(leaderboardVariant2.getRawPlayerScore()), Long.valueOf(leaderboardVariant.getRawPlayerScore())) && m.equal(leaderboardVariant2.getDisplayPlayerScore(), leaderboardVariant.getDisplayPlayerScore()) && m.equal(Long.valueOf(leaderboardVariant2.getPlayerRank()), Long.valueOf(leaderboardVariant.getPlayerRank())) && m.equal(leaderboardVariant2.getDisplayPlayerRank(), leaderboardVariant.getDisplayPlayerRank()) && m.equal(Long.valueOf(leaderboardVariant2.getNumScores()), Long.valueOf(leaderboardVariant.getNumScores())) && m.equal(leaderboardVariant2.lB(), leaderboardVariant.lB()) && m.equal(leaderboardVariant2.lD(), leaderboardVariant.lD()) && m.equal(leaderboardVariant2.lC(), leaderboardVariant.lC());
    }

    static String b(LeaderboardVariant leaderboardVariant) {
        return m.h(leaderboardVariant).a("TimeSpan", TimeSpan.dH(leaderboardVariant.getTimeSpan())).a("Collection", LeaderboardCollection.dH(leaderboardVariant.getCollection())).a("RawPlayerScore", leaderboardVariant.hasPlayerInfo() ? Long.valueOf(leaderboardVariant.getRawPlayerScore()) : "none").a("DisplayPlayerScore", leaderboardVariant.hasPlayerInfo() ? leaderboardVariant.getDisplayPlayerScore() : "none").a("PlayerRank", leaderboardVariant.hasPlayerInfo() ? Long.valueOf(leaderboardVariant.getPlayerRank()) : "none").a("DisplayPlayerRank", leaderboardVariant.hasPlayerInfo() ? leaderboardVariant.getDisplayPlayerRank() : "none").a("NumScores", Long.valueOf(leaderboardVariant.getNumScores())).a("TopPageNextToken", leaderboardVariant.lB()).a("WindowPageNextToken", leaderboardVariant.lD()).a("WindowPagePrevToken", leaderboardVariant.lC()).toString();
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public int getCollection() {
        return this.abC;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public String getDisplayPlayerRank() {
        return this.abH;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public String getDisplayPlayerScore() {
        return this.abF;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public long getNumScores() {
        return this.abJ;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public long getPlayerRank() {
        return this.abG;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public String getPlayerScoreTag() {
        return this.abI;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public long getRawPlayerScore() {
        return this.abE;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public int getTimeSpan() {
        return this.abB;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public boolean hasPlayerInfo() {
        return this.abD;
    }

    public int hashCode() {
        return a(this);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public String lB() {
        return this.abK;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public String lC() {
        return this.abL;
    }

    @Override // com.google.android.gms.games.leaderboard.LeaderboardVariant
    public String lD() {
        return this.abM;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: lE, reason: merged with bridge method [inline-methods] */
    public LeaderboardVariant freeze() {
        return this;
    }

    public String toString() {
        return b(this);
    }
}
