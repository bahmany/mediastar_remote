package com.google.android.gms.games.internal.game;

import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.games.internal.GamesDowngradeableSafeParcel;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataEntity;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class ExtendedGameEntity extends GamesDowngradeableSafeParcel implements ExtendedGame {
    public static final ExtendedGameEntityCreator CREATOR = new ExtendedGameEntityCreatorCompat();
    private final int BR;
    private final GameEntity aan;
    private final int aao;
    private final boolean aap;
    private final int aaq;
    private final long aar;
    private final long aas;
    private final String aat;
    private final long aau;
    private final String aav;
    private final ArrayList<GameBadgeEntity> aaw;
    private final SnapshotMetadataEntity aax;

    static final class ExtendedGameEntityCreatorCompat extends ExtendedGameEntityCreator {
        ExtendedGameEntityCreatorCompat() {
        }

        @Override // com.google.android.gms.games.internal.game.ExtendedGameEntityCreator, android.os.Parcelable.Creator
        /* renamed from: cg */
        public ExtendedGameEntity createFromParcel(Parcel parcel) {
            if (ExtendedGameEntity.c(ExtendedGameEntity.gP()) || ExtendedGameEntity.aV(ExtendedGameEntity.class.getCanonicalName())) {
                return super.createFromParcel(parcel);
            }
            GameEntity gameEntityCreateFromParcel = GameEntity.CREATOR.createFromParcel(parcel);
            int i = parcel.readInt();
            boolean z = parcel.readInt() == 1;
            int i2 = parcel.readInt();
            long j = parcel.readLong();
            long j2 = parcel.readLong();
            String string = parcel.readString();
            long j3 = parcel.readLong();
            String string2 = parcel.readString();
            int i3 = parcel.readInt();
            ArrayList arrayList = new ArrayList(i3);
            for (int i4 = 0; i4 < i3; i4++) {
                arrayList.add(GameBadgeEntity.CREATOR.createFromParcel(parcel));
            }
            return new ExtendedGameEntity(2, gameEntityCreateFromParcel, i, z, i2, j, j2, string, j3, string2, arrayList, null);
        }
    }

    ExtendedGameEntity(int versionCode, GameEntity game, int availability, boolean owned, int achievementUnlockedCount, long lastPlayedServerTimestamp, long priceMicros, String formattedPrice, long fullPriceMicros, String formattedFullPrice, ArrayList<GameBadgeEntity> badges, SnapshotMetadataEntity snapshot) {
        this.BR = versionCode;
        this.aan = game;
        this.aao = availability;
        this.aap = owned;
        this.aaq = achievementUnlockedCount;
        this.aar = lastPlayedServerTimestamp;
        this.aas = priceMicros;
        this.aat = formattedPrice;
        this.aau = fullPriceMicros;
        this.aav = formattedFullPrice;
        this.aaw = badges;
        this.aax = snapshot;
    }

    public ExtendedGameEntity(ExtendedGame extendedGame) {
        this.BR = 2;
        Game game = extendedGame.getGame();
        this.aan = game == null ? null : new GameEntity(game);
        this.aao = extendedGame.kP();
        this.aap = extendedGame.kQ();
        this.aaq = extendedGame.kR();
        this.aar = extendedGame.kS();
        this.aas = extendedGame.kT();
        this.aat = extendedGame.kU();
        this.aau = extendedGame.kV();
        this.aav = extendedGame.kW();
        SnapshotMetadata snapshotMetadataKX = extendedGame.kX();
        this.aax = snapshotMetadataKX != null ? new SnapshotMetadataEntity(snapshotMetadataKX) : null;
        ArrayList<GameBadge> arrayListKO = extendedGame.kO();
        int size = arrayListKO.size();
        this.aaw = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.aaw.add((GameBadgeEntity) arrayListKO.get(i).freeze());
        }
    }

    static int a(ExtendedGame extendedGame) {
        return m.hashCode(extendedGame.getGame(), Integer.valueOf(extendedGame.kP()), Boolean.valueOf(extendedGame.kQ()), Integer.valueOf(extendedGame.kR()), Long.valueOf(extendedGame.kS()), Long.valueOf(extendedGame.kT()), extendedGame.kU(), Long.valueOf(extendedGame.kV()), extendedGame.kW());
    }

    static boolean a(ExtendedGame extendedGame, Object obj) {
        if (!(obj instanceof ExtendedGame)) {
            return false;
        }
        if (extendedGame == obj) {
            return true;
        }
        ExtendedGame extendedGame2 = (ExtendedGame) obj;
        return m.equal(extendedGame2.getGame(), extendedGame.getGame()) && m.equal(Integer.valueOf(extendedGame2.kP()), Integer.valueOf(extendedGame.kP())) && m.equal(Boolean.valueOf(extendedGame2.kQ()), Boolean.valueOf(extendedGame.kQ())) && m.equal(Integer.valueOf(extendedGame2.kR()), Integer.valueOf(extendedGame.kR())) && m.equal(Long.valueOf(extendedGame2.kS()), Long.valueOf(extendedGame.kS())) && m.equal(Long.valueOf(extendedGame2.kT()), Long.valueOf(extendedGame.kT())) && m.equal(extendedGame2.kU(), extendedGame.kU()) && m.equal(Long.valueOf(extendedGame2.kV()), Long.valueOf(extendedGame.kV())) && m.equal(extendedGame2.kW(), extendedGame.kW());
    }

    static String b(ExtendedGame extendedGame) {
        return m.h(extendedGame).a("Game", extendedGame.getGame()).a("Availability", Integer.valueOf(extendedGame.kP())).a("Owned", Boolean.valueOf(extendedGame.kQ())).a("AchievementUnlockedCount", Integer.valueOf(extendedGame.kR())).a("LastPlayedServerTimestamp", Long.valueOf(extendedGame.kS())).a("PriceMicros", Long.valueOf(extendedGame.kT())).a("FormattedPrice", extendedGame.kU()).a("FullPriceMicros", Long.valueOf(extendedGame.kV())).a("FormattedFullPrice", extendedGame.kW()).a("Snapshot", extendedGame.kX()).toString();
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

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public ArrayList<GameBadge> kO() {
        return new ArrayList<>(this.aaw);
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public int kP() {
        return this.aao;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public boolean kQ() {
        return this.aap;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public int kR() {
        return this.aaq;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public long kS() {
        return this.aar;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public long kT() {
        return this.aas;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public String kU() {
        return this.aat;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public long kV() {
        return this.aau;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public String kW() {
        return this.aav;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    public SnapshotMetadata kX() {
        return this.aax;
    }

    @Override // com.google.android.gms.games.internal.game.ExtendedGame
    /* renamed from: kY */
    public GameEntity getGame() {
        return this.aan;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: kZ */
    public ExtendedGame freeze() {
        return this;
    }

    public String toString() {
        return b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (!gQ()) {
            ExtendedGameEntityCreator.a(this, dest, flags);
            return;
        }
        this.aan.writeToParcel(dest, flags);
        dest.writeInt(this.aao);
        dest.writeInt(this.aap ? 1 : 0);
        dest.writeInt(this.aaq);
        dest.writeLong(this.aar);
        dest.writeLong(this.aas);
        dest.writeString(this.aat);
        dest.writeLong(this.aau);
        dest.writeString(this.aav);
        int size = this.aaw.size();
        dest.writeInt(size);
        for (int i = 0; i < size; i++) {
            this.aaw.get(i).writeToParcel(dest, flags);
        }
    }
}
