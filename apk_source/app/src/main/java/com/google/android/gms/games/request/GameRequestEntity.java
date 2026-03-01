package com.google.android.gms.games.request;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public final class GameRequestEntity implements SafeParcelable, GameRequest {
    public static final GameRequestEntityCreator CREATOR = new GameRequestEntityCreator();
    private final int BR;
    private final int FD;
    private final int Fa;
    private final String Xr;
    private final GameEntity aan;
    private final long abO;
    private final PlayerEntity acR;
    private final ArrayList<PlayerEntity> acS;
    private final long acT;
    private final Bundle acU;
    private final byte[] acw;

    GameRequestEntity(int versionCode, GameEntity game, PlayerEntity sender, byte[] data, String requestId, ArrayList<PlayerEntity> recipients, int type, long creationTimestamp, long expirationTimestamp, Bundle recipientStatuses, int status) {
        this.BR = versionCode;
        this.aan = game;
        this.acR = sender;
        this.acw = data;
        this.Xr = requestId;
        this.acS = recipients;
        this.FD = type;
        this.abO = creationTimestamp;
        this.acT = expirationTimestamp;
        this.acU = recipientStatuses;
        this.Fa = status;
    }

    public GameRequestEntity(GameRequest request) {
        this.BR = 2;
        this.aan = new GameEntity(request.getGame());
        this.acR = new PlayerEntity(request.getSender());
        this.Xr = request.getRequestId();
        this.FD = request.getType();
        this.abO = request.getCreationTimestamp();
        this.acT = request.getExpirationTimestamp();
        this.Fa = request.getStatus();
        byte[] data = request.getData();
        if (data == null) {
            this.acw = null;
        } else {
            this.acw = new byte[data.length];
            System.arraycopy(data, 0, this.acw, 0, data.length);
        }
        List<Player> recipients = request.getRecipients();
        int size = recipients.size();
        this.acS = new ArrayList<>(size);
        this.acU = new Bundle();
        for (int i = 0; i < size; i++) {
            Player playerFreeze = recipients.get(i).freeze();
            String playerId = playerFreeze.getPlayerId();
            this.acS.add((PlayerEntity) playerFreeze);
            this.acU.putInt(playerId, request.getRecipientStatus(playerId));
        }
    }

    static int a(GameRequest gameRequest) {
        return m.hashCode(gameRequest.getGame(), gameRequest.getRecipients(), gameRequest.getRequestId(), gameRequest.getSender(), b(gameRequest), Integer.valueOf(gameRequest.getType()), Long.valueOf(gameRequest.getCreationTimestamp()), Long.valueOf(gameRequest.getExpirationTimestamp()));
    }

    static boolean a(GameRequest gameRequest, Object obj) {
        if (!(obj instanceof GameRequest)) {
            return false;
        }
        if (gameRequest == obj) {
            return true;
        }
        GameRequest gameRequest2 = (GameRequest) obj;
        return m.equal(gameRequest2.getGame(), gameRequest.getGame()) && m.equal(gameRequest2.getRecipients(), gameRequest.getRecipients()) && m.equal(gameRequest2.getRequestId(), gameRequest.getRequestId()) && m.equal(gameRequest2.getSender(), gameRequest.getSender()) && Arrays.equals(b(gameRequest2), b(gameRequest)) && m.equal(Integer.valueOf(gameRequest2.getType()), Integer.valueOf(gameRequest.getType())) && m.equal(Long.valueOf(gameRequest2.getCreationTimestamp()), Long.valueOf(gameRequest.getCreationTimestamp())) && m.equal(Long.valueOf(gameRequest2.getExpirationTimestamp()), Long.valueOf(gameRequest.getExpirationTimestamp()));
    }

    private static int[] b(GameRequest gameRequest) {
        List<Player> recipients = gameRequest.getRecipients();
        int size = recipients.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = gameRequest.getRecipientStatus(recipients.get(i).getPlayerId());
        }
        return iArr;
    }

    static String c(GameRequest gameRequest) {
        return m.h(gameRequest).a("Game", gameRequest.getGame()).a("Sender", gameRequest.getSender()).a("Recipients", gameRequest.getRecipients()).a("Data", gameRequest.getData()).a("RequestId", gameRequest.getRequestId()).a("Type", Integer.valueOf(gameRequest.getType())).a("CreationTimestamp", Long.valueOf(gameRequest.getCreationTimestamp())).a("ExpirationTimestamp", Long.valueOf(gameRequest.getExpirationTimestamp())).toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public GameRequest freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public long getCreationTimestamp() {
        return this.abO;
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public byte[] getData() {
        return this.acw;
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public long getExpirationTimestamp() {
        return this.acT;
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public Game getGame() {
        return this.aan;
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public int getRecipientStatus(String playerId) {
        return this.acU.getInt(playerId, 0);
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public List<Player> getRecipients() {
        return new ArrayList(this.acS);
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public String getRequestId() {
        return this.Xr;
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public Player getSender() {
        return this.acR;
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public int getStatus() {
        return this.Fa;
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public int getType() {
        return this.FD;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return a(this);
    }

    @Override // com.google.android.gms.games.request.GameRequest
    public boolean isConsumed(String playerId) {
        return getRecipientStatus(playerId) == 1;
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    public Bundle lJ() {
        return this.acU;
    }

    public String toString() {
        return c(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        GameRequestEntityCreator.a(this, dest, flags);
    }
}
