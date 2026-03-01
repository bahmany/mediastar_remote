package com.google.android.gms.games.internal.player;

import android.net.Uri;
import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class MostRecentGameInfoEntity implements SafeParcelable, MostRecentGameInfo {
    public static final MostRecentGameInfoEntityCreator CREATOR = new MostRecentGameInfoEntityCreator();
    private final int BR;
    private final String aaB;
    private final String aaC;
    private final long aaD;
    private final Uri aaE;
    private final Uri aaF;
    private final Uri aaG;

    MostRecentGameInfoEntity(int versionCode, String gameId, String gameName, long activityTimestampMillis, Uri gameIconImageUri, Uri gameHiResIconImageUri, Uri gameFeaturedImageUri) {
        this.BR = versionCode;
        this.aaB = gameId;
        this.aaC = gameName;
        this.aaD = activityTimestampMillis;
        this.aaE = gameIconImageUri;
        this.aaF = gameHiResIconImageUri;
        this.aaG = gameFeaturedImageUri;
    }

    public MostRecentGameInfoEntity(MostRecentGameInfo info) {
        this.BR = 2;
        this.aaB = info.ln();
        this.aaC = info.lo();
        this.aaD = info.lp();
        this.aaE = info.lq();
        this.aaF = info.lr();
        this.aaG = info.ls();
    }

    static int a(MostRecentGameInfo mostRecentGameInfo) {
        return m.hashCode(mostRecentGameInfo.ln(), mostRecentGameInfo.lo(), Long.valueOf(mostRecentGameInfo.lp()), mostRecentGameInfo.lq(), mostRecentGameInfo.lr(), mostRecentGameInfo.ls());
    }

    static boolean a(MostRecentGameInfo mostRecentGameInfo, Object obj) {
        if (!(obj instanceof MostRecentGameInfo)) {
            return false;
        }
        if (mostRecentGameInfo == obj) {
            return true;
        }
        MostRecentGameInfo mostRecentGameInfo2 = (MostRecentGameInfo) obj;
        return m.equal(mostRecentGameInfo2.ln(), mostRecentGameInfo.ln()) && m.equal(mostRecentGameInfo2.lo(), mostRecentGameInfo.lo()) && m.equal(Long.valueOf(mostRecentGameInfo2.lp()), Long.valueOf(mostRecentGameInfo.lp())) && m.equal(mostRecentGameInfo2.lq(), mostRecentGameInfo.lq()) && m.equal(mostRecentGameInfo2.lr(), mostRecentGameInfo.lr()) && m.equal(mostRecentGameInfo2.ls(), mostRecentGameInfo.ls());
    }

    static String b(MostRecentGameInfo mostRecentGameInfo) {
        return m.h(mostRecentGameInfo).a("GameId", mostRecentGameInfo.ln()).a("GameName", mostRecentGameInfo.lo()).a("ActivityTimestampMillis", Long.valueOf(mostRecentGameInfo.lp())).a("GameIconUri", mostRecentGameInfo.lq()).a("GameHiResUri", mostRecentGameInfo.lr()).a("GameFeaturedUri", mostRecentGameInfo.ls()).toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return a(this, obj);
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

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public String ln() {
        return this.aaB;
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public String lo() {
        return this.aaC;
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public long lp() {
        return this.aaD;
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public Uri lq() {
        return this.aaE;
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public Uri lr() {
        return this.aaF;
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public Uri ls() {
        return this.aaG;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: lt, reason: merged with bridge method [inline-methods] */
    public MostRecentGameInfo freeze() {
        return this;
    }

    public String toString() {
        return b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        MostRecentGameInfoEntityCreator.a(this, out, flags);
    }
}
