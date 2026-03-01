package com.google.android.gms.games;

import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class PlayerLevel implements SafeParcelable {
    public static final PlayerLevelCreator CREATOR = new PlayerLevelCreator();
    private final int BR;
    private final int VG;
    private final long VH;
    private final long VI;

    PlayerLevel(int versionCode, int levelNumber, long minXp, long maxXp) {
        n.a(minXp >= 0, "Min XP must be positive!");
        n.a(maxXp > minXp, "Max XP must be more than min XP!");
        this.BR = versionCode;
        this.VG = levelNumber;
        this.VH = minXp;
        this.VI = maxXp;
    }

    public PlayerLevel(int value, long minXp, long maxXp) {
        this(1, value, minXp, maxXp);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerLevel)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        PlayerLevel playerLevel = (PlayerLevel) obj;
        return m.equal(Integer.valueOf(playerLevel.getLevelNumber()), Integer.valueOf(getLevelNumber())) && m.equal(Long.valueOf(playerLevel.getMinXp()), Long.valueOf(getMinXp())) && m.equal(Long.valueOf(playerLevel.getMaxXp()), Long.valueOf(getMaxXp()));
    }

    public int getLevelNumber() {
        return this.VG;
    }

    public long getMaxXp() {
        return this.VI;
    }

    public long getMinXp() {
        return this.VH;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.VG), Long.valueOf(this.VH), Long.valueOf(this.VI));
    }

    public String toString() {
        return m.h(this).a("LevelNumber", Integer.valueOf(getLevelNumber())).a("MinXp", Long.valueOf(getMinXp())).a("MaxXp", Long.valueOf(getMaxXp())).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        PlayerLevelCreator.a(this, out, flags);
    }
}
