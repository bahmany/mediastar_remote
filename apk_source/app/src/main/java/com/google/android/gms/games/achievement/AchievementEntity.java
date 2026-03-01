package com.google.android.gms.games.achievement;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;
import com.google.android.gms.common.internal.a;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerEntity;
import com.google.android.gms.internal.jv;

/* loaded from: classes.dex */
public final class AchievementEntity implements SafeParcelable, Achievement {
    public static final AchievementEntityCreator CREATOR = new AchievementEntityCreator();
    private final int BR;
    private final int FD;
    private final String Tg;
    private final String VP;
    private final Uri VQ;
    private final String VR;
    private final Uri VS;
    private final String VT;
    private final int VU;
    private final String VV;
    private final PlayerEntity VW;
    private final int VX;
    private final String VY;
    private final long VZ;
    private final long Wa;
    private final String mName;
    private final int mState;

    AchievementEntity(int versionCode, String achievementId, int type, String name, String description, Uri unlockedImageUri, String unlockedImageUrl, Uri revealedImageUri, String revealedImageUrl, int totalSteps, String formattedTotalSteps, PlayerEntity player, int state, int currentSteps, String formattedCurrentSteps, long lastUpdatedTimestamp, long xpValue) {
        this.BR = versionCode;
        this.VP = achievementId;
        this.FD = type;
        this.mName = name;
        this.Tg = description;
        this.VQ = unlockedImageUri;
        this.VR = unlockedImageUrl;
        this.VS = revealedImageUri;
        this.VT = revealedImageUrl;
        this.VU = totalSteps;
        this.VV = formattedTotalSteps;
        this.VW = player;
        this.mState = state;
        this.VX = currentSteps;
        this.VY = formattedCurrentSteps;
        this.VZ = lastUpdatedTimestamp;
        this.Wa = xpValue;
    }

    public AchievementEntity(Achievement achievement) {
        this.BR = 1;
        this.VP = achievement.getAchievementId();
        this.FD = achievement.getType();
        this.mName = achievement.getName();
        this.Tg = achievement.getDescription();
        this.VQ = achievement.getUnlockedImageUri();
        this.VR = achievement.getUnlockedImageUrl();
        this.VS = achievement.getRevealedImageUri();
        this.VT = achievement.getRevealedImageUrl();
        this.VW = (PlayerEntity) achievement.getPlayer().freeze();
        this.mState = achievement.getState();
        this.VZ = achievement.getLastUpdatedTimestamp();
        this.Wa = achievement.getXpValue();
        if (achievement.getType() == 1) {
            this.VU = achievement.getTotalSteps();
            this.VV = achievement.getFormattedTotalSteps();
            this.VX = achievement.getCurrentSteps();
            this.VY = achievement.getFormattedCurrentSteps();
        } else {
            this.VU = 0;
            this.VV = null;
            this.VX = 0;
            this.VY = null;
        }
        a.f(this.VP);
        a.f(this.Tg);
    }

    static int a(Achievement achievement) {
        int totalSteps;
        int currentSteps;
        if (achievement.getType() == 1) {
            currentSteps = achievement.getCurrentSteps();
            totalSteps = achievement.getTotalSteps();
        } else {
            totalSteps = 0;
            currentSteps = 0;
        }
        return m.hashCode(achievement.getAchievementId(), achievement.getName(), Integer.valueOf(achievement.getType()), achievement.getDescription(), Long.valueOf(achievement.getXpValue()), Integer.valueOf(achievement.getState()), Long.valueOf(achievement.getLastUpdatedTimestamp()), achievement.getPlayer(), Integer.valueOf(currentSteps), Integer.valueOf(totalSteps));
    }

    static boolean a(Achievement achievement, Object obj) {
        boolean zEqual;
        boolean zEqual2;
        if (!(obj instanceof Achievement)) {
            return false;
        }
        if (achievement == obj) {
            return true;
        }
        Achievement achievement2 = (Achievement) obj;
        if (achievement.getType() == 1) {
            zEqual2 = m.equal(Integer.valueOf(achievement2.getCurrentSteps()), Integer.valueOf(achievement.getCurrentSteps()));
            zEqual = m.equal(Integer.valueOf(achievement2.getTotalSteps()), Integer.valueOf(achievement.getTotalSteps()));
        } else {
            zEqual = true;
            zEqual2 = true;
        }
        return m.equal(achievement2.getAchievementId(), achievement.getAchievementId()) && m.equal(achievement2.getName(), achievement.getName()) && m.equal(Integer.valueOf(achievement2.getType()), Integer.valueOf(achievement.getType())) && m.equal(achievement2.getDescription(), achievement.getDescription()) && m.equal(Long.valueOf(achievement2.getXpValue()), Long.valueOf(achievement.getXpValue())) && m.equal(Integer.valueOf(achievement2.getState()), Integer.valueOf(achievement.getState())) && m.equal(Long.valueOf(achievement2.getLastUpdatedTimestamp()), Long.valueOf(achievement.getLastUpdatedTimestamp())) && m.equal(achievement2.getPlayer(), achievement.getPlayer()) && zEqual2 && zEqual;
    }

    static String b(Achievement achievement) {
        m.a aVarA = m.h(achievement).a("Id", achievement.getAchievementId()).a("Type", Integer.valueOf(achievement.getType())).a("Name", achievement.getName()).a("Description", achievement.getDescription()).a("Player", achievement.getPlayer()).a("State", Integer.valueOf(achievement.getState()));
        if (achievement.getType() == 1) {
            aVarA.a("CurrentSteps", Integer.valueOf(achievement.getCurrentSteps()));
            aVarA.a("TotalSteps", Integer.valueOf(achievement.getTotalSteps()));
        }
        return aVarA.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public Achievement freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public String getAchievementId() {
        return this.VP;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public int getCurrentSteps() {
        return this.VX;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public String getDescription() {
        return this.Tg;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public void getDescription(CharArrayBuffer dataOut) {
        jv.b(this.Tg, dataOut);
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public String getFormattedCurrentSteps() {
        return this.VY;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public void getFormattedCurrentSteps(CharArrayBuffer dataOut) {
        jv.b(this.VY, dataOut);
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public String getFormattedTotalSteps() {
        return this.VV;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public void getFormattedTotalSteps(CharArrayBuffer dataOut) {
        jv.b(this.VV, dataOut);
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public long getLastUpdatedTimestamp() {
        return this.VZ;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public String getName() {
        return this.mName;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public void getName(CharArrayBuffer dataOut) {
        jv.b(this.mName, dataOut);
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public Player getPlayer() {
        return this.VW;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public Uri getRevealedImageUri() {
        return this.VS;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public String getRevealedImageUrl() {
        return this.VT;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public int getState() {
        return this.mState;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public int getTotalSteps() {
        return this.VU;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public int getType() {
        return this.FD;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public Uri getUnlockedImageUri() {
        return this.VQ;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public String getUnlockedImageUrl() {
        return this.VR;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // com.google.android.gms.games.achievement.Achievement
    public long getXpValue() {
        return this.Wa;
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
    public void writeToParcel(Parcel dest, int flags) {
        AchievementEntityCreator.a(this, dest, flags);
    }
}
