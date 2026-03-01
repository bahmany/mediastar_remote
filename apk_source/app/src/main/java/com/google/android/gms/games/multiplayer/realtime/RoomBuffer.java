package com.google.android.gms.games.multiplayer.realtime;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.g;

/* loaded from: classes.dex */
public final class RoomBuffer extends g<Room> {
    public RoomBuffer(DataHolder dataHolder) {
        super(dataHolder);
    }

    @Override // com.google.android.gms.common.data.g
    protected String gE() {
        return "external_match_id";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.data.g
    /* renamed from: k, reason: merged with bridge method [inline-methods] */
    public Room f(int i, int i2) {
        return new RoomRef(this.IC, i, i2);
    }
}
