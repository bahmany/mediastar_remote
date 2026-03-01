package com.google.android.gms.games;

import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class PlayerLevelInfo implements SafeParcelable {
    public static final PlayerLevelInfoCreator CREATOR = new PlayerLevelInfoCreator();
    private final int BR;
    private final long VJ;
    private final long VK;
    private final PlayerLevel VL;
    private final PlayerLevel VM;

    PlayerLevelInfo(int versionCode, long currentXpTotal, long lastLevelUpTimestamp, PlayerLevel currentLevel, PlayerLevel nextLevel) {
        n.I(currentXpTotal != -1);
        n.i(currentLevel);
        n.i(nextLevel);
        this.BR = versionCode;
        this.VJ = currentXpTotal;
        this.VK = lastLevelUpTimestamp;
        this.VL = currentLevel;
        this.VM = nextLevel;
    }

    public PlayerLevelInfo(long currentXpTotal, long lastLevelUpTimestamp, PlayerLevel currentLevel, PlayerLevel nextLevel) {
        this(1, currentXpTotal, lastLevelUpTimestamp, currentLevel, nextLevel);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerLevelInfo)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        PlayerLevelInfo playerLevelInfo = (PlayerLevelInfo) obj;
        return m.equal(Long.valueOf(this.VJ), Long.valueOf(playerLevelInfo.VJ)) && m.equal(Long.valueOf(this.VK), Long.valueOf(playerLevelInfo.VK)) && m.equal(this.VL, playerLevelInfo.VL) && m.equal(this.VM, playerLevelInfo.VM);
    }

    public PlayerLevel getCurrentLevel() {
        return this.VL;
    }

    public long getCurrentXpTotal() {
        return this.VJ;
    }

    public long getLastLevelUpTimestamp() {
        return this.VK;
    }

    public PlayerLevel getNextLevel() {
        return this.VM;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(Long.valueOf(this.VJ), Long.valueOf(this.VK), this.VL, this.VM);
    }

    public boolean isMaxLevel() {
        return this.VL.equals(this.VM);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        PlayerLevelInfoCreator.a(this, out, flags);
    }
}
