package com.google.android.gms.games.snapshot;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerEntity;
import com.google.android.gms.internal.jv;

/* loaded from: classes.dex */
public final class SnapshotMetadataEntity implements SafeParcelable, SnapshotMetadata {
    public static final SnapshotMetadataEntityCreator CREATOR = new SnapshotMetadataEntityCreator();
    private final int BR;
    private final String No;
    private final String Tg;
    private final String Wx;
    private final GameEntity aan;
    private final Uri acZ;
    private final PlayerEntity add;
    private final String ade;
    private final long adf;
    private final long adg;
    private final float adh;
    private final String adi;

    SnapshotMetadataEntity(int versionCode, GameEntity game, PlayerEntity owner, String snapshotId, Uri coverImageUri, String coverImageUrl, String title, String description, long lastModifiedTimestamp, long playedTime, float coverImageAspectRatio, String uniqueName) {
        this.BR = versionCode;
        this.aan = game;
        this.add = owner;
        this.Wx = snapshotId;
        this.acZ = coverImageUri;
        this.ade = coverImageUrl;
        this.adh = coverImageAspectRatio;
        this.No = title;
        this.Tg = description;
        this.adf = lastModifiedTimestamp;
        this.adg = playedTime;
        this.adi = uniqueName;
    }

    public SnapshotMetadataEntity(SnapshotMetadata snapshotMetadata) {
        this.BR = 3;
        this.aan = new GameEntity(snapshotMetadata.getGame());
        this.add = new PlayerEntity(snapshotMetadata.getOwner());
        this.Wx = snapshotMetadata.getSnapshotId();
        this.acZ = snapshotMetadata.getCoverImageUri();
        this.ade = snapshotMetadata.getCoverImageUrl();
        this.adh = snapshotMetadata.getCoverImageAspectRatio();
        this.No = snapshotMetadata.getTitle();
        this.Tg = snapshotMetadata.getDescription();
        this.adf = snapshotMetadata.getLastModifiedTimestamp();
        this.adg = snapshotMetadata.getPlayedTime();
        this.adi = snapshotMetadata.getUniqueName();
    }

    static int a(SnapshotMetadata snapshotMetadata) {
        return m.hashCode(snapshotMetadata.getGame(), snapshotMetadata.getOwner(), snapshotMetadata.getSnapshotId(), snapshotMetadata.getCoverImageUri(), Float.valueOf(snapshotMetadata.getCoverImageAspectRatio()), snapshotMetadata.getTitle(), snapshotMetadata.getDescription(), Long.valueOf(snapshotMetadata.getLastModifiedTimestamp()), Long.valueOf(snapshotMetadata.getPlayedTime()), snapshotMetadata.getUniqueName());
    }

    static boolean a(SnapshotMetadata snapshotMetadata, Object obj) {
        if (!(obj instanceof SnapshotMetadata)) {
            return false;
        }
        if (snapshotMetadata == obj) {
            return true;
        }
        SnapshotMetadata snapshotMetadata2 = (SnapshotMetadata) obj;
        return m.equal(snapshotMetadata2.getGame(), snapshotMetadata.getGame()) && m.equal(snapshotMetadata2.getOwner(), snapshotMetadata.getOwner()) && m.equal(snapshotMetadata2.getSnapshotId(), snapshotMetadata.getSnapshotId()) && m.equal(snapshotMetadata2.getCoverImageUri(), snapshotMetadata.getCoverImageUri()) && m.equal(Float.valueOf(snapshotMetadata2.getCoverImageAspectRatio()), Float.valueOf(snapshotMetadata.getCoverImageAspectRatio())) && m.equal(snapshotMetadata2.getTitle(), snapshotMetadata.getTitle()) && m.equal(snapshotMetadata2.getDescription(), snapshotMetadata.getDescription()) && m.equal(Long.valueOf(snapshotMetadata2.getLastModifiedTimestamp()), Long.valueOf(snapshotMetadata.getLastModifiedTimestamp())) && m.equal(Long.valueOf(snapshotMetadata2.getPlayedTime()), Long.valueOf(snapshotMetadata.getPlayedTime())) && m.equal(snapshotMetadata2.getUniqueName(), snapshotMetadata.getUniqueName());
    }

    static String b(SnapshotMetadata snapshotMetadata) {
        return m.h(snapshotMetadata).a("Game", snapshotMetadata.getGame()).a("Owner", snapshotMetadata.getOwner()).a("SnapshotId", snapshotMetadata.getSnapshotId()).a("CoverImageUri", snapshotMetadata.getCoverImageUri()).a("CoverImageUrl", snapshotMetadata.getCoverImageUrl()).a("CoverImageAspectRatio", Float.valueOf(snapshotMetadata.getCoverImageAspectRatio())).a("Description", snapshotMetadata.getDescription()).a("LastModifiedTimestamp", Long.valueOf(snapshotMetadata.getLastModifiedTimestamp())).a("PlayedTime", Long.valueOf(snapshotMetadata.getPlayedTime())).a("UniqueName", snapshotMetadata.getUniqueName()).toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public SnapshotMetadata freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public float getCoverImageAspectRatio() {
        return this.adh;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public Uri getCoverImageUri() {
        return this.acZ;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public String getCoverImageUrl() {
        return this.ade;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public String getDescription() {
        return this.Tg;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public void getDescription(CharArrayBuffer dataOut) {
        jv.b(this.Tg, dataOut);
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public Game getGame() {
        return this.aan;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public long getLastModifiedTimestamp() {
        return this.adf;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public Player getOwner() {
        return this.add;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public long getPlayedTime() {
        return this.adg;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public String getSnapshotId() {
        return this.Wx;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public String getTitle() {
        return this.No;
    }

    @Override // com.google.android.gms.games.snapshot.SnapshotMetadata
    public String getUniqueName() {
        return this.adi;
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

    public String toString() {
        return b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        SnapshotMetadataEntityCreator.a(this, out, flags);
    }
}
