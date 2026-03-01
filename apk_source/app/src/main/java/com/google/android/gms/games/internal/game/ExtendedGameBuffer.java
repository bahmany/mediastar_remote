package com.google.android.gms.games.internal.game;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.g;

/* loaded from: classes.dex */
public final class ExtendedGameBuffer extends g<ExtendedGame> {
    public ExtendedGameBuffer(DataHolder dataHolder) {
        super(dataHolder);
    }

    @Override // com.google.android.gms.common.data.g
    protected String gE() {
        return "external_game_id";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.data.g
    /* renamed from: h, reason: merged with bridge method [inline-methods] */
    public ExtendedGame f(int i, int i2) {
        return new ExtendedGameRef(this.IC, i, i2);
    }
}
