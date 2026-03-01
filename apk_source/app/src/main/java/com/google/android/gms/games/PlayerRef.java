package com.google.android.gms.games;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.d;
import com.google.android.gms.games.internal.player.MostRecentGameInfo;
import com.google.android.gms.games.internal.player.MostRecentGameInfoRef;
import com.google.android.gms.games.internal.player.PlayerColumnNames;

/* loaded from: classes.dex */
public final class PlayerRef extends d implements Player {
    private final PlayerLevelInfo VE;
    private final PlayerColumnNames VN;
    private final MostRecentGameInfoRef VO;

    public PlayerRef(DataHolder holder, int dataRow) {
        this(holder, dataRow, null);
    }

    public PlayerRef(DataHolder holder, int dataRow, String prefix) {
        super(holder, dataRow);
        this.VN = new PlayerColumnNames(prefix);
        this.VO = new MostRecentGameInfoRef(holder, dataRow, this.VN);
        if (!jT()) {
            this.VE = null;
            return;
        }
        int integer = getInteger(this.VN.aaR);
        int integer2 = getInteger(this.VN.aaU);
        PlayerLevel playerLevel = new PlayerLevel(integer, getLong(this.VN.aaS), getLong(this.VN.aaT));
        this.VE = new PlayerLevelInfo(getLong(this.VN.aaQ), getLong(this.VN.aaW), playerLevel, integer != integer2 ? new PlayerLevel(integer2, getLong(this.VN.aaT), getLong(this.VN.aaV)) : playerLevel);
    }

    private boolean jT() {
        return (aS(this.VN.aaQ) || getLong(this.VN.aaQ) == -1) ? false : true;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.gms.common.data.d
    public boolean equals(Object obj) {
        return PlayerEntity.a(this, obj);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public Player freeze() {
        return new PlayerEntity(this);
    }

    @Override // com.google.android.gms.games.Player
    public String getDisplayName() {
        return getString(this.VN.aaI);
    }

    @Override // com.google.android.gms.games.Player
    public void getDisplayName(CharArrayBuffer dataOut) {
        a(this.VN.aaI, dataOut);
    }

    @Override // com.google.android.gms.games.Player
    public Uri getHiResImageUri() {
        return aR(this.VN.aaL);
    }

    @Override // com.google.android.gms.games.Player
    public String getHiResImageUrl() {
        return getString(this.VN.aaM);
    }

    @Override // com.google.android.gms.games.Player
    public Uri getIconImageUri() {
        return aR(this.VN.aaJ);
    }

    @Override // com.google.android.gms.games.Player
    public String getIconImageUrl() {
        return getString(this.VN.aaK);
    }

    @Override // com.google.android.gms.games.Player
    public long getLastPlayedWithTimestamp() {
        if (!aQ(this.VN.aaP) || aS(this.VN.aaP)) {
            return -1L;
        }
        return getLong(this.VN.aaP);
    }

    @Override // com.google.android.gms.games.Player
    public PlayerLevelInfo getLevelInfo() {
        return this.VE;
    }

    @Override // com.google.android.gms.games.Player
    public String getPlayerId() {
        return getString(this.VN.aaH);
    }

    @Override // com.google.android.gms.games.Player
    public long getRetrievedTimestamp() {
        return getLong(this.VN.aaN);
    }

    @Override // com.google.android.gms.games.Player
    public String getTitle() {
        return getString(this.VN.aaX);
    }

    @Override // com.google.android.gms.games.Player
    public void getTitle(CharArrayBuffer dataOut) {
        a(this.VN.aaX, dataOut);
    }

    @Override // com.google.android.gms.games.Player
    public boolean hasHiResImage() {
        return getHiResImageUri() != null;
    }

    @Override // com.google.android.gms.games.Player
    public boolean hasIconImage() {
        return getIconImageUri() != null;
    }

    @Override // com.google.android.gms.common.data.d
    public int hashCode() {
        return PlayerEntity.b(this);
    }

    @Override // com.google.android.gms.games.Player
    public boolean isProfileVisible() {
        return getBoolean(this.VN.aaZ);
    }

    @Override // com.google.android.gms.games.Player
    public int jR() {
        return getInteger(this.VN.aaO);
    }

    @Override // com.google.android.gms.games.Player
    public MostRecentGameInfo jS() {
        if (aS(this.VN.aba)) {
            return null;
        }
        return this.VO;
    }

    public String toString() {
        return PlayerEntity.c(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ((PlayerEntity) freeze()).writeToParcel(dest, flags);
    }
}
