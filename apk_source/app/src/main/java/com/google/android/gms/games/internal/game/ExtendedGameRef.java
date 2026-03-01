package com.google.android.gms.games.internal.game;

import android.os.Parcel;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.d;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.GameRef;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataRef;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ExtendedGameRef extends d implements ExtendedGame {
    private final GameRef aam;
    private final SnapshotMetadataRef aay;
    private final int aaz;

    ExtendedGameRef(DataHolder holder, int dataRow, int numChildren) {
        super(holder, dataRow);
        this.aam = new GameRef(holder, dataRow);
        this.aaz = numChildren;
        if (!aQ("external_snapshot_id") || aS("external_snapshot_id")) {
            this.aay = null;
        } else {
            this.aay = new SnapshotMetadataRef(holder, dataRow);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.gms.common.data.d
    public boolean equals(Object obj) {
        return ExtendedGameEntity.a(this, obj);
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public Game getGame() {
        return this.aam;
    }

    @Override // com.google.android.gms.common.data.d
    public int hashCode() {
        return ExtendedGameEntity.a(this);
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public ArrayList<GameBadge> kO() {
        if (this.IC.c("badge_title", this.JQ, this.IC.ar(this.JQ)) == null) {
            return new ArrayList<>(0);
        }
        ArrayList<GameBadge> arrayList = new ArrayList<>(this.aaz);
        for (int i = 0; i < this.aaz; i++) {
            arrayList.add(new GameBadgeRef(this.IC, this.JQ + i));
        }
        return arrayList;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public int kP() {
        return getInteger("availability");
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public boolean kQ() {
        return getBoolean("owned");
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public int kR() {
        return getInteger("achievement_unlocked_count");
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public long kS() {
        return getLong("last_played_server_time");
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public long kT() {
        return getLong("price_micros");
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public String kU() {
        return getString("formatted_price");
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public long kV() {
        return getLong("full_price_micros");
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public String kW() {
        return getString("formatted_full_price");
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public SnapshotMetadata kX() {
        return this.aay;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: kZ, reason: merged with bridge method [inline-methods] */
    public ExtendedGame freeze() {
        return new ExtendedGameEntity(this);
    }

    public String toString() {
        return ExtendedGameEntity.b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ((ExtendedGameEntity) freeze()).writeToParcel(dest, flags);
    }
}
