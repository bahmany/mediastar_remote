package com.google.android.gms.games.multiplayer;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerEntity;
import com.google.android.gms.games.internal.GamesDowngradeableSafeParcel;
import com.google.android.gms.internal.jv;

/* loaded from: classes.dex */
public final class ParticipantEntity extends GamesDowngradeableSafeParcel implements Participant {
    public static final Parcelable.Creator<ParticipantEntity> CREATOR = new ParticipantEntityCreatorCompat();
    private final int BR;
    private final int EZ;
    private final int Fa;
    private final String Nz;
    private final Uri UW;
    private final Uri UX;
    private final PlayerEntity VW;
    private final String Vh;
    private final String Vi;
    private final String Wf;
    private final String Xg;
    private final boolean abV;
    private final ParticipantResult abW;

    static final class ParticipantEntityCreatorCompat extends ParticipantEntityCreator {
        ParticipantEntityCreatorCompat() {
        }

        @Override // com.google.android.gms.games.multiplayer.ParticipantEntityCreator, android.os.Parcelable.Creator
        /* renamed from: cm */
        public ParticipantEntity createFromParcel(Parcel parcel) {
            if (ParticipantEntity.c(ParticipantEntity.gP()) || ParticipantEntity.aV(ParticipantEntity.class.getCanonicalName())) {
                return super.createFromParcel(parcel);
            }
            String string = parcel.readString();
            String string2 = parcel.readString();
            String string3 = parcel.readString();
            Uri uri = string3 == null ? null : Uri.parse(string3);
            String string4 = parcel.readString();
            return new ParticipantEntity(3, string, string2, uri, string4 == null ? null : Uri.parse(string4), parcel.readInt(), parcel.readString(), parcel.readInt() > 0, parcel.readInt() > 0 ? PlayerEntity.CREATOR.createFromParcel(parcel) : null, 7, null, null, null);
        }
    }

    ParticipantEntity(int versionCode, String participantId, String displayName, Uri iconImageUri, Uri hiResImageUri, int status, String clientAddress, boolean connectedToRoom, PlayerEntity player, int capabilities, ParticipantResult result, String iconImageUrl, String hiResImageUrl) {
        this.BR = versionCode;
        this.Xg = participantId;
        this.Nz = displayName;
        this.UW = iconImageUri;
        this.UX = hiResImageUri;
        this.Fa = status;
        this.Wf = clientAddress;
        this.abV = connectedToRoom;
        this.VW = player;
        this.EZ = capabilities;
        this.abW = result;
        this.Vh = iconImageUrl;
        this.Vi = hiResImageUrl;
    }

    public ParticipantEntity(Participant participant) {
        this.BR = 3;
        this.Xg = participant.getParticipantId();
        this.Nz = participant.getDisplayName();
        this.UW = participant.getIconImageUri();
        this.UX = participant.getHiResImageUri();
        this.Fa = participant.getStatus();
        this.Wf = participant.jU();
        this.abV = participant.isConnectedToRoom();
        Player player = participant.getPlayer();
        this.VW = player == null ? null : new PlayerEntity(player);
        this.EZ = participant.getCapabilities();
        this.abW = participant.getResult();
        this.Vh = participant.getIconImageUrl();
        this.Vi = participant.getHiResImageUrl();
    }

    static int a(Participant participant) {
        return m.hashCode(participant.getPlayer(), Integer.valueOf(participant.getStatus()), participant.jU(), Boolean.valueOf(participant.isConnectedToRoom()), participant.getDisplayName(), participant.getIconImageUri(), participant.getHiResImageUri(), Integer.valueOf(participant.getCapabilities()), participant.getResult(), participant.getParticipantId());
    }

    static boolean a(Participant participant, Object obj) {
        if (!(obj instanceof Participant)) {
            return false;
        }
        if (participant == obj) {
            return true;
        }
        Participant participant2 = (Participant) obj;
        return m.equal(participant2.getPlayer(), participant.getPlayer()) && m.equal(Integer.valueOf(participant2.getStatus()), Integer.valueOf(participant.getStatus())) && m.equal(participant2.jU(), participant.jU()) && m.equal(Boolean.valueOf(participant2.isConnectedToRoom()), Boolean.valueOf(participant.isConnectedToRoom())) && m.equal(participant2.getDisplayName(), participant.getDisplayName()) && m.equal(participant2.getIconImageUri(), participant.getIconImageUri()) && m.equal(participant2.getHiResImageUri(), participant.getHiResImageUri()) && m.equal(Integer.valueOf(participant2.getCapabilities()), Integer.valueOf(participant.getCapabilities())) && m.equal(participant2.getResult(), participant.getResult()) && m.equal(participant2.getParticipantId(), participant.getParticipantId());
    }

    static String b(Participant participant) {
        return m.h(participant).a("ParticipantId", participant.getParticipantId()).a("Player", participant.getPlayer()).a("Status", Integer.valueOf(participant.getStatus())).a("ClientAddress", participant.jU()).a("ConnectedToRoom", Boolean.valueOf(participant.isConnectedToRoom())).a("DisplayName", participant.getDisplayName()).a("IconImage", participant.getIconImageUri()).a("IconImageUrl", participant.getIconImageUrl()).a("HiResImage", participant.getHiResImageUri()).a("HiResImageUrl", participant.getHiResImageUrl()).a("Capabilities", Integer.valueOf(participant.getCapabilities())).a("Result", participant.getResult()).toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public Participant freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public int getCapabilities() {
        return this.EZ;
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public String getDisplayName() {
        return this.VW == null ? this.Nz : this.VW.getDisplayName();
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public void getDisplayName(CharArrayBuffer dataOut) {
        if (this.VW == null) {
            jv.b(this.Nz, dataOut);
        } else {
            this.VW.getDisplayName(dataOut);
        }
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public Uri getHiResImageUri() {
        return this.VW == null ? this.UX : this.VW.getHiResImageUri();
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public String getHiResImageUrl() {
        return this.VW == null ? this.Vi : this.VW.getHiResImageUrl();
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public Uri getIconImageUri() {
        return this.VW == null ? this.UW : this.VW.getIconImageUri();
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public String getIconImageUrl() {
        return this.VW == null ? this.Vh : this.VW.getIconImageUrl();
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public String getParticipantId() {
        return this.Xg;
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public Player getPlayer() {
        return this.VW;
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public ParticipantResult getResult() {
        return this.abW;
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public int getStatus() {
        return this.Fa;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return a(this);
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public boolean isConnectedToRoom() {
        return this.abV;
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.games.multiplayer.Participant
    public String jU() {
        return this.Wf;
    }

    public String toString() {
        return b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (!gQ()) {
            ParticipantEntityCreator.a(this, dest, flags);
            return;
        }
        dest.writeString(this.Xg);
        dest.writeString(this.Nz);
        dest.writeString(this.UW == null ? null : this.UW.toString());
        dest.writeString(this.UX != null ? this.UX.toString() : null);
        dest.writeInt(this.Fa);
        dest.writeString(this.Wf);
        dest.writeInt(this.abV ? 1 : 0);
        dest.writeInt(this.VW != null ? 1 : 0);
        if (this.VW != null) {
            this.VW.writeToParcel(dest, flags);
        }
    }
}
