package com.google.android.gms.games.internal.player;

import android.net.Uri;
import android.os.Parcel;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.d;

/* loaded from: classes.dex */
public final class MostRecentGameInfoRef extends d implements MostRecentGameInfo {
    private final PlayerColumnNames VN;

    public MostRecentGameInfoRef(DataHolder holder, int dataRow, PlayerColumnNames columnNames) {
        super(holder, dataRow);
        this.VN = columnNames;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.gms.common.data.d
    public boolean equals(Object obj) {
        return MostRecentGameInfoEntity.a(this, obj);
    }

    @Override // com.google.android.gms.common.data.d
    public int hashCode() {
        return MostRecentGameInfoEntity.a(this);
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public String ln() {
        return getString(this.VN.aba);
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public String lo() {
        return getString(this.VN.abb);
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public long lp() {
        return getLong(this.VN.abc);
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public Uri lq() {
        return aR(this.VN.abd);
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public Uri lr() {
        return aR(this.VN.abe);
    }

    @Override // com.google.android.gms.games.internal.player.MostRecentGameInfo
    public Uri ls() {
        return aR(this.VN.abf);
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: lt, reason: merged with bridge method [inline-methods] */
    public MostRecentGameInfo freeze() {
        return new MostRecentGameInfoEntity(this);
    }

    public String toString() {
        return MostRecentGameInfoEntity.b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ((MostRecentGameInfoEntity) freeze()).writeToParcel(dest, flags);
    }
}
