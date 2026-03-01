package com.google.android.gms.games.multiplayer.turnbased;

import android.os.Bundle;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.games.internal.constants.TurnBasedMatchTurnStatus;
import com.google.android.gms.games.multiplayer.InvitationBuffer;

/* loaded from: classes.dex */
public final class LoadMatchesResponse {
    private final InvitationBuffer aco;
    private final TurnBasedMatchBuffer acp;
    private final TurnBasedMatchBuffer acq;
    private final TurnBasedMatchBuffer acr;

    public LoadMatchesResponse(Bundle matchData) {
        DataHolder dataHolderA = a(matchData, 0);
        if (dataHolderA != null) {
            this.aco = new InvitationBuffer(dataHolderA);
        } else {
            this.aco = null;
        }
        DataHolder dataHolderA2 = a(matchData, 1);
        if (dataHolderA2 != null) {
            this.acp = new TurnBasedMatchBuffer(dataHolderA2);
        } else {
            this.acp = null;
        }
        DataHolder dataHolderA3 = a(matchData, 2);
        if (dataHolderA3 != null) {
            this.acq = new TurnBasedMatchBuffer(dataHolderA3);
        } else {
            this.acq = null;
        }
        DataHolder dataHolderA4 = a(matchData, 3);
        if (dataHolderA4 != null) {
            this.acr = new TurnBasedMatchBuffer(dataHolderA4);
        } else {
            this.acr = null;
        }
    }

    private static DataHolder a(Bundle bundle, int i) {
        String strDH = TurnBasedMatchTurnStatus.dH(i);
        if (bundle.containsKey(strDH)) {
            return (DataHolder) bundle.getParcelable(strDH);
        }
        return null;
    }

    public void close() {
        if (this.aco != null) {
            this.aco.close();
        }
        if (this.acp != null) {
            this.acp.close();
        }
        if (this.acq != null) {
            this.acq.close();
        }
        if (this.acr != null) {
            this.acr.close();
        }
    }

    public TurnBasedMatchBuffer getCompletedMatches() {
        return this.acr;
    }

    public InvitationBuffer getInvitations() {
        return this.aco;
    }

    public TurnBasedMatchBuffer getMyTurnMatches() {
        return this.acp;
    }

    public TurnBasedMatchBuffer getTheirTurnMatches() {
        return this.acq;
    }

    public boolean hasData() {
        if (this.aco != null && this.aco.getCount() > 0) {
            return true;
        }
        if (this.acp != null && this.acp.getCount() > 0) {
            return true;
        }
        if (this.acq == null || this.acq.getCount() <= 0) {
            return this.acr != null && this.acr.getCount() > 0;
        }
        return true;
    }
}
