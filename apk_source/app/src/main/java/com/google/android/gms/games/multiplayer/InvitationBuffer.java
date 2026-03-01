package com.google.android.gms.games.multiplayer;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.g;

/* loaded from: classes.dex */
public final class InvitationBuffer extends g<Invitation> {
    public InvitationBuffer(DataHolder dataHolder) {
        super(dataHolder);
    }

    @Override // com.google.android.gms.common.data.g
    protected String gE() {
        return "external_invitation_id";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.data.g
    /* renamed from: j, reason: merged with bridge method [inline-methods] */
    public Invitation f(int i, int i2) {
        return new InvitationRef(this.IC, i, i2);
    }
}
