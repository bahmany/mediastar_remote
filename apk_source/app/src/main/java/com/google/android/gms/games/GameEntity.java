package com.google.android.gms.games;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.games.internal.GamesDowngradeableSafeParcel;
import com.google.android.gms.internal.jv;

/* loaded from: classes.dex */
public final class GameEntity extends GamesDowngradeableSafeParcel implements Game {
    public static final Parcelable.Creator<GameEntity> CREATOR = new GameEntityCreatorCompat();
    private final int BR;
    private final String Ez;
    private final String Nz;
    private final String Tg;
    private final String UT;
    private final String UU;
    private final String UV;
    private final Uri UW;
    private final Uri UX;
    private final Uri UY;
    private final boolean UZ;
    private final boolean Va;
    private final String Vb;
    private final int Vc;
    private final int Vd;
    private final int Ve;
    private final boolean Vf;
    private final boolean Vg;
    private final String Vh;
    private final String Vi;
    private final String Vj;
    private final boolean Vk;
    private final boolean Vl;
    private final boolean Vm;
    private final String Vn;

    static final class GameEntityCreatorCompat extends GameEntityCreator {
        GameEntityCreatorCompat() {
        }

        @Override // com.google.android.gms.games.GameEntityCreator, android.os.Parcelable.Creator
        /* renamed from: cd */
        public GameEntity createFromParcel(Parcel parcel) {
            if (GameEntity.c(GameEntity.gP()) || GameEntity.aV(GameEntity.class.getCanonicalName())) {
                return super.createFromParcel(parcel);
            }
            String string = parcel.readString();
            String string2 = parcel.readString();
            String string3 = parcel.readString();
            String string4 = parcel.readString();
            String string5 = parcel.readString();
            String string6 = parcel.readString();
            String string7 = parcel.readString();
            Uri uri = string7 == null ? null : Uri.parse(string7);
            String string8 = parcel.readString();
            Uri uri2 = string8 == null ? null : Uri.parse(string8);
            String string9 = parcel.readString();
            return new GameEntity(5, string, string2, string3, string4, string5, string6, uri, uri2, string9 == null ? null : Uri.parse(string9), parcel.readInt() > 0, parcel.readInt() > 0, parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), false, false, null, null, null, false, false, false, null);
        }
    }

    GameEntity(int versionCode, String applicationId, String displayName, String primaryCategory, String secondaryCategory, String description, String developerName, Uri iconImageUri, Uri hiResImageUri, Uri featuredImageUri, boolean playEnabledGame, boolean instanceInstalled, String instancePackageName, int gameplayAclStatus, int achievementTotalCount, int leaderboardCount, boolean realTimeEnabled, boolean turnBasedEnabled, String iconImageUrl, String hiResImageUrl, String featuredImageUrl, boolean muted, boolean identitySharingConfirmed, boolean snapshotsEnabled, String themeColor) {
        this.BR = versionCode;
        this.Ez = applicationId;
        this.Nz = displayName;
        this.UT = primaryCategory;
        this.UU = secondaryCategory;
        this.Tg = description;
        this.UV = developerName;
        this.UW = iconImageUri;
        this.Vh = iconImageUrl;
        this.UX = hiResImageUri;
        this.Vi = hiResImageUrl;
        this.UY = featuredImageUri;
        this.Vj = featuredImageUrl;
        this.UZ = playEnabledGame;
        this.Va = instanceInstalled;
        this.Vb = instancePackageName;
        this.Vc = gameplayAclStatus;
        this.Vd = achievementTotalCount;
        this.Ve = leaderboardCount;
        this.Vf = realTimeEnabled;
        this.Vg = turnBasedEnabled;
        this.Vk = muted;
        this.Vl = identitySharingConfirmed;
        this.Vm = snapshotsEnabled;
        this.Vn = themeColor;
    }

    public GameEntity(Game game) {
        this.BR = 5;
        this.Ez = game.getApplicationId();
        this.UT = game.getPrimaryCategory();
        this.UU = game.getSecondaryCategory();
        this.Tg = game.getDescription();
        this.UV = game.getDeveloperName();
        this.Nz = game.getDisplayName();
        this.UW = game.getIconImageUri();
        this.Vh = game.getIconImageUrl();
        this.UX = game.getHiResImageUri();
        this.Vi = game.getHiResImageUrl();
        this.UY = game.getFeaturedImageUri();
        this.Vj = game.getFeaturedImageUrl();
        this.UZ = game.jL();
        this.Va = game.jN();
        this.Vb = game.jO();
        this.Vc = game.jP();
        this.Vd = game.getAchievementTotalCount();
        this.Ve = game.getLeaderboardCount();
        this.Vf = game.isRealTimeMultiplayerEnabled();
        this.Vg = game.isTurnBasedMultiplayerEnabled();
        this.Vk = game.isMuted();
        this.Vl = game.jM();
        this.Vm = game.areSnapshotsEnabled();
        this.Vn = game.getThemeColor();
    }

    static int a(Game game) {
        return m.hashCode(game.getApplicationId(), game.getDisplayName(), game.getPrimaryCategory(), game.getSecondaryCategory(), game.getDescription(), game.getDeveloperName(), game.getIconImageUri(), game.getHiResImageUri(), game.getFeaturedImageUri(), Boolean.valueOf(game.jL()), Boolean.valueOf(game.jN()), game.jO(), Integer.valueOf(game.jP()), Integer.valueOf(game.getAchievementTotalCount()), Integer.valueOf(game.getLeaderboardCount()), Boolean.valueOf(game.isRealTimeMultiplayerEnabled()), Boolean.valueOf(game.isTurnBasedMultiplayerEnabled()), Boolean.valueOf(game.isMuted()), Boolean.valueOf(game.jM()), Boolean.valueOf(game.areSnapshotsEnabled()), game.getThemeColor());
    }

    static boolean a(Game game, Object obj) {
        if (!(obj instanceof Game)) {
            return false;
        }
        if (game == obj) {
            return true;
        }
        Game game2 = (Game) obj;
        if (m.equal(game2.getApplicationId(), game.getApplicationId()) && m.equal(game2.getDisplayName(), game.getDisplayName()) && m.equal(game2.getPrimaryCategory(), game.getPrimaryCategory()) && m.equal(game2.getSecondaryCategory(), game.getSecondaryCategory()) && m.equal(game2.getDescription(), game.getDescription()) && m.equal(game2.getDeveloperName(), game.getDeveloperName()) && m.equal(game2.getIconImageUri(), game.getIconImageUri()) && m.equal(game2.getHiResImageUri(), game.getHiResImageUri()) && m.equal(game2.getFeaturedImageUri(), game.getFeaturedImageUri()) && m.equal(Boolean.valueOf(game2.jL()), Boolean.valueOf(game.jL())) && m.equal(Boolean.valueOf(game2.jN()), Boolean.valueOf(game.jN())) && m.equal(game2.jO(), game.jO()) && m.equal(Integer.valueOf(game2.jP()), Integer.valueOf(game.jP())) && m.equal(Integer.valueOf(game2.getAchievementTotalCount()), Integer.valueOf(game.getAchievementTotalCount())) && m.equal(Integer.valueOf(game2.getLeaderboardCount()), Integer.valueOf(game.getLeaderboardCount())) && m.equal(Boolean.valueOf(game2.isRealTimeMultiplayerEnabled()), Boolean.valueOf(game.isRealTimeMultiplayerEnabled()))) {
            if (m.equal(Boolean.valueOf(game2.isTurnBasedMultiplayerEnabled()), Boolean.valueOf(game.isTurnBasedMultiplayerEnabled() && m.equal(Boolean.valueOf(game2.isMuted()), Boolean.valueOf(game.isMuted())) && m.equal(Boolean.valueOf(game2.jM()), Boolean.valueOf(game.jM())))) && m.equal(Boolean.valueOf(game2.areSnapshotsEnabled()), Boolean.valueOf(game.areSnapshotsEnabled())) && m.equal(game2.getThemeColor(), game.getThemeColor())) {
                return true;
            }
        }
        return false;
    }

    static String b(Game game) {
        return m.h(game).a("ApplicationId", game.getApplicationId()).a("DisplayName", game.getDisplayName()).a("PrimaryCategory", game.getPrimaryCategory()).a("SecondaryCategory", game.getSecondaryCategory()).a("Description", game.getDescription()).a("DeveloperName", game.getDeveloperName()).a("IconImageUri", game.getIconImageUri()).a("IconImageUrl", game.getIconImageUrl()).a("HiResImageUri", game.getHiResImageUri()).a("HiResImageUrl", game.getHiResImageUrl()).a("FeaturedImageUri", game.getFeaturedImageUri()).a("FeaturedImageUrl", game.getFeaturedImageUrl()).a("PlayEnabledGame", Boolean.valueOf(game.jL())).a("InstanceInstalled", Boolean.valueOf(game.jN())).a("InstancePackageName", game.jO()).a("AchievementTotalCount", Integer.valueOf(game.getAchievementTotalCount())).a("LeaderboardCount", Integer.valueOf(game.getLeaderboardCount())).a("RealTimeMultiplayerEnabled", Boolean.valueOf(game.isRealTimeMultiplayerEnabled())).a("TurnBasedMultiplayerEnabled", Boolean.valueOf(game.isTurnBasedMultiplayerEnabled())).a("AreSnapshotsEnabled", Boolean.valueOf(game.areSnapshotsEnabled())).a("ThemeColor", game.getThemeColor()).toString();
    }

    @Override // com.google.android.gms.games.Game
    public boolean areSnapshotsEnabled() {
        return this.Vm;
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
    public Game freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.Game
    public int getAchievementTotalCount() {
        return this.Vd;
    }

    @Override // com.google.android.gms.games.Game
    public String getApplicationId() {
        return this.Ez;
    }

    @Override // com.google.android.gms.games.Game
    public String getDescription() {
        return this.Tg;
    }

    @Override // com.google.android.gms.games.Game
    public void getDescription(CharArrayBuffer dataOut) {
        jv.b(this.Tg, dataOut);
    }

    @Override // com.google.android.gms.games.Game
    public String getDeveloperName() {
        return this.UV;
    }

    @Override // com.google.android.gms.games.Game
    public void getDeveloperName(CharArrayBuffer dataOut) {
        jv.b(this.UV, dataOut);
    }

    @Override // com.google.android.gms.games.Game
    public String getDisplayName() {
        return this.Nz;
    }

    @Override // com.google.android.gms.games.Game
    public void getDisplayName(CharArrayBuffer dataOut) {
        jv.b(this.Nz, dataOut);
    }

    @Override // com.google.android.gms.games.Game
    public Uri getFeaturedImageUri() {
        return this.UY;
    }

    @Override // com.google.android.gms.games.Game
    public String getFeaturedImageUrl() {
        return this.Vj;
    }

    @Override // com.google.android.gms.games.Game
    public Uri getHiResImageUri() {
        return this.UX;
    }

    @Override // com.google.android.gms.games.Game
    public String getHiResImageUrl() {
        return this.Vi;
    }

    @Override // com.google.android.gms.games.Game
    public Uri getIconImageUri() {
        return this.UW;
    }

    @Override // com.google.android.gms.games.Game
    public String getIconImageUrl() {
        return this.Vh;
    }

    @Override // com.google.android.gms.games.Game
    public int getLeaderboardCount() {
        return this.Ve;
    }

    @Override // com.google.android.gms.games.Game
    public String getPrimaryCategory() {
        return this.UT;
    }

    @Override // com.google.android.gms.games.Game
    public String getSecondaryCategory() {
        return this.UU;
    }

    @Override // com.google.android.gms.games.Game
    public String getThemeColor() {
        return this.Vn;
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

    @Override // com.google.android.gms.games.Game
    public boolean isMuted() {
        return this.Vk;
    }

    @Override // com.google.android.gms.games.Game
    public boolean isRealTimeMultiplayerEnabled() {
        return this.Vf;
    }

    @Override // com.google.android.gms.games.Game
    public boolean isTurnBasedMultiplayerEnabled() {
        return this.Vg;
    }

    @Override // com.google.android.gms.games.Game
    public boolean jL() {
        return this.UZ;
    }

    @Override // com.google.android.gms.games.Game
    public boolean jM() {
        return this.Vl;
    }

    @Override // com.google.android.gms.games.Game
    public boolean jN() {
        return this.Va;
    }

    @Override // com.google.android.gms.games.Game
    public String jO() {
        return this.Vb;
    }

    @Override // com.google.android.gms.games.Game
    public int jP() {
        return this.Vc;
    }

    public String toString() {
        return b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (!gQ()) {
            GameEntityCreator.a(this, dest, flags);
            return;
        }
        dest.writeString(this.Ez);
        dest.writeString(this.Nz);
        dest.writeString(this.UT);
        dest.writeString(this.UU);
        dest.writeString(this.Tg);
        dest.writeString(this.UV);
        dest.writeString(this.UW == null ? null : this.UW.toString());
        dest.writeString(this.UX == null ? null : this.UX.toString());
        dest.writeString(this.UY != null ? this.UY.toString() : null);
        dest.writeInt(this.UZ ? 1 : 0);
        dest.writeInt(this.Va ? 1 : 0);
        dest.writeString(this.Vb);
        dest.writeInt(this.Vc);
        dest.writeInt(this.Vd);
        dest.writeInt(this.Ve);
    }
}
