package com.google.android.gms.games;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.a;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.games.internal.GamesDowngradeableSafeParcel;
import com.google.android.gms.games.internal.player.MostRecentGameInfo;
import com.google.android.gms.games.internal.player.MostRecentGameInfoEntity;
import com.google.android.gms.internal.jv;

/* loaded from: classes.dex */
public final class PlayerEntity extends GamesDowngradeableSafeParcel implements Player {
    public static final Parcelable.Creator<PlayerEntity> CREATOR = new PlayerEntityCreatorCompat();
    private final int BR;
    private final String No;
    private final String Nz;
    private final Uri UW;
    private final Uri UX;
    private final long VA;
    private final int VB;
    private final long VC;
    private final MostRecentGameInfoEntity VD;
    private final PlayerLevelInfo VE;
    private final boolean VF;
    private final String Vh;
    private final String Vi;
    private final String Vz;

    static final class PlayerEntityCreatorCompat extends PlayerEntityCreator {
        PlayerEntityCreatorCompat() {
        }

        @Override // com.google.android.gms.games.PlayerEntityCreator, android.os.Parcelable.Creator
        /* renamed from: ce */
        public PlayerEntity createFromParcel(Parcel parcel) {
            if (PlayerEntity.c(PlayerEntity.gP()) || PlayerEntity.aV(PlayerEntity.class.getCanonicalName())) {
                return super.createFromParcel(parcel);
            }
            String string = parcel.readString();
            String string2 = parcel.readString();
            String string3 = parcel.readString();
            String string4 = parcel.readString();
            return new PlayerEntity(11, string, string2, string3 == null ? null : Uri.parse(string3), string4 == null ? null : Uri.parse(string4), parcel.readLong(), -1, -1L, null, null, null, null, null, true);
        }
    }

    PlayerEntity(int versionCode, String playerId, String displayName, Uri iconImageUri, Uri hiResImageUri, long retrievedTimestamp, int isInCircles, long lastPlayedWithTimestamp, String iconImageUrl, String hiResImageUrl, String title, MostRecentGameInfoEntity mostRecentGameInfo, PlayerLevelInfo playerLevelInfo, boolean isProfileVisible) {
        this.BR = versionCode;
        this.Vz = playerId;
        this.Nz = displayName;
        this.UW = iconImageUri;
        this.Vh = iconImageUrl;
        this.UX = hiResImageUri;
        this.Vi = hiResImageUrl;
        this.VA = retrievedTimestamp;
        this.VB = isInCircles;
        this.VC = lastPlayedWithTimestamp;
        this.No = title;
        this.VF = isProfileVisible;
        this.VD = mostRecentGameInfo;
        this.VE = playerLevelInfo;
    }

    public PlayerEntity(Player player) {
        this.BR = 11;
        this.Vz = player.getPlayerId();
        this.Nz = player.getDisplayName();
        this.UW = player.getIconImageUri();
        this.Vh = player.getIconImageUrl();
        this.UX = player.getHiResImageUri();
        this.Vi = player.getHiResImageUrl();
        this.VA = player.getRetrievedTimestamp();
        this.VB = player.jR();
        this.VC = player.getLastPlayedWithTimestamp();
        this.No = player.getTitle();
        this.VF = player.isProfileVisible();
        MostRecentGameInfo mostRecentGameInfoJS = player.jS();
        this.VD = mostRecentGameInfoJS == null ? null : new MostRecentGameInfoEntity(mostRecentGameInfoJS);
        this.VE = player.getLevelInfo();
        a.f(this.Vz);
        a.f(this.Nz);
        a.I(this.VA > 0);
    }

    static boolean a(Player player, Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        if (player == obj) {
            return true;
        }
        Player player2 = (Player) obj;
        return m.equal(player2.getPlayerId(), player.getPlayerId()) && m.equal(player2.getDisplayName(), player.getDisplayName()) && m.equal(player2.getIconImageUri(), player.getIconImageUri()) && m.equal(player2.getHiResImageUri(), player.getHiResImageUri()) && m.equal(Long.valueOf(player2.getRetrievedTimestamp()), Long.valueOf(player.getRetrievedTimestamp())) && m.equal(player2.getTitle(), player.getTitle()) && m.equal(player2.getLevelInfo(), player.getLevelInfo());
    }

    static int b(Player player) {
        return m.hashCode(player.getPlayerId(), player.getDisplayName(), player.getIconImageUri(), player.getHiResImageUri(), Long.valueOf(player.getRetrievedTimestamp()), player.getTitle(), player.getLevelInfo());
    }

    static String c(Player player) {
        return m.h(player).a("PlayerId", player.getPlayerId()).a("DisplayName", player.getDisplayName()).a("IconImageUri", player.getIconImageUri()).a("IconImageUrl", player.getIconImageUrl()).a("HiResImageUri", player.getHiResImageUri()).a("HiResImageUrl", player.getHiResImageUrl()).a("RetrievedTimestamp", Long.valueOf(player.getRetrievedTimestamp())).a("Title", player.getTitle()).a("LevelInfo", player.getLevelInfo()).toString();
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
    public Player freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.Player
    public String getDisplayName() {
        return this.Nz;
    }

    @Override // com.google.android.gms.games.Player
    public void getDisplayName(CharArrayBuffer dataOut) {
        jv.b(this.Nz, dataOut);
    }

    @Override // com.google.android.gms.games.Player
    public Uri getHiResImageUri() {
        return this.UX;
    }

    @Override // com.google.android.gms.games.Player
    public String getHiResImageUrl() {
        return this.Vi;
    }

    @Override // com.google.android.gms.games.Player
    public Uri getIconImageUri() {
        return this.UW;
    }

    @Override // com.google.android.gms.games.Player
    public String getIconImageUrl() {
        return this.Vh;
    }

    @Override // com.google.android.gms.games.Player
    public long getLastPlayedWithTimestamp() {
        return this.VC;
    }

    @Override // com.google.android.gms.games.Player
    public PlayerLevelInfo getLevelInfo() {
        return this.VE;
    }

    @Override // com.google.android.gms.games.Player
    public String getPlayerId() {
        return this.Vz;
    }

    @Override // com.google.android.gms.games.Player
    public long getRetrievedTimestamp() {
        return this.VA;
    }

    @Override // com.google.android.gms.games.Player
    public String getTitle() {
        return this.No;
    }

    @Override // com.google.android.gms.games.Player
    public void getTitle(CharArrayBuffer dataOut) {
        jv.b(this.No, dataOut);
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // com.google.android.gms.games.Player
    public boolean hasHiResImage() {
        return getHiResImageUri() != null;
    }

    @Override // com.google.android.gms.games.Player
    public boolean hasIconImage() {
        return getIconImageUri() != null;
    }

    public int hashCode() {
        return b(this);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.games.Player
    public boolean isProfileVisible() {
        return this.VF;
    }

    @Override // com.google.android.gms.games.Player
    public int jR() {
        return this.VB;
    }

    @Override // com.google.android.gms.games.Player
    public MostRecentGameInfo jS() {
        return this.VD;
    }

    public String toString() {
        return c(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (!gQ()) {
            PlayerEntityCreator.a(this, dest, flags);
            return;
        }
        dest.writeString(this.Vz);
        dest.writeString(this.Nz);
        dest.writeString(this.UW == null ? null : this.UW.toString());
        dest.writeString(this.UX != null ? this.UX.toString() : null);
        dest.writeLong(this.VA);
    }
}
