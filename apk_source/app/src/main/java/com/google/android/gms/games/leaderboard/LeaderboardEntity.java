package com.google.android.gms.games.leaderboard;

import android.database.CharArrayBuffer;
import android.net.Uri;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.internal.jv;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class LeaderboardEntity implements Leaderboard {
    private final String Nz;
    private final Uri UW;
    private final String Vh;
    private final String abj;
    private final int abk;
    private final ArrayList<LeaderboardVariantEntity> abl;
    private final Game abm;

    public LeaderboardEntity(Leaderboard leaderboard) {
        this.abj = leaderboard.getLeaderboardId();
        this.Nz = leaderboard.getDisplayName();
        this.UW = leaderboard.getIconImageUri();
        this.Vh = leaderboard.getIconImageUrl();
        this.abk = leaderboard.getScoreOrder();
        Game game = leaderboard.getGame();
        this.abm = game == null ? null : new GameEntity(game);
        ArrayList<LeaderboardVariant> variants = leaderboard.getVariants();
        int size = variants.size();
        this.abl = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.abl.add((LeaderboardVariantEntity) variants.get(i).freeze());
        }
    }

    static int a(Leaderboard leaderboard) {
        return m.hashCode(leaderboard.getLeaderboardId(), leaderboard.getDisplayName(), leaderboard.getIconImageUri(), Integer.valueOf(leaderboard.getScoreOrder()), leaderboard.getVariants());
    }

    static boolean a(Leaderboard leaderboard, Object obj) {
        if (!(obj instanceof Leaderboard)) {
            return false;
        }
        if (leaderboard == obj) {
            return true;
        }
        Leaderboard leaderboard2 = (Leaderboard) obj;
        return m.equal(leaderboard2.getLeaderboardId(), leaderboard.getLeaderboardId()) && m.equal(leaderboard2.getDisplayName(), leaderboard.getDisplayName()) && m.equal(leaderboard2.getIconImageUri(), leaderboard.getIconImageUri()) && m.equal(Integer.valueOf(leaderboard2.getScoreOrder()), Integer.valueOf(leaderboard.getScoreOrder())) && m.equal(leaderboard2.getVariants(), leaderboard.getVariants());
    }

    static String b(Leaderboard leaderboard) {
        return m.h(leaderboard).a("LeaderboardId", leaderboard.getLeaderboardId()).a("DisplayName", leaderboard.getDisplayName()).a("IconImageUri", leaderboard.getIconImageUri()).a("IconImageUrl", leaderboard.getIconImageUrl()).a("ScoreOrder", Integer.valueOf(leaderboard.getScoreOrder())).a("Variants", leaderboard.getVariants()).toString();
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.games.leaderboard.Leaderboard
    public String getDisplayName() {
        return this.Nz;
    }

    @Override // com.google.android.gms.games.leaderboard.Leaderboard
    public void getDisplayName(CharArrayBuffer dataOut) {
        jv.b(this.Nz, dataOut);
    }

    @Override // com.google.android.gms.games.leaderboard.Leaderboard
    public Game getGame() {
        return this.abm;
    }

    @Override // com.google.android.gms.games.leaderboard.Leaderboard
    public Uri getIconImageUri() {
        return this.UW;
    }

    @Override // com.google.android.gms.games.leaderboard.Leaderboard
    public String getIconImageUrl() {
        return this.Vh;
    }

    @Override // com.google.android.gms.games.leaderboard.Leaderboard
    public String getLeaderboardId() {
        return this.abj;
    }

    @Override // com.google.android.gms.games.leaderboard.Leaderboard
    public int getScoreOrder() {
        return this.abk;
    }

    @Override // com.google.android.gms.games.leaderboard.Leaderboard
    public ArrayList<LeaderboardVariant> getVariants() {
        return new ArrayList<>(this.abl);
    }

    public int hashCode() {
        return a(this);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: lx, reason: merged with bridge method [inline-methods] */
    public Leaderboard freeze() {
        return this;
    }

    public String toString() {
        return b(this);
    }
}
