package com.google.android.gms.games.request;

import com.google.android.gms.common.data.DataBuffer;

/* loaded from: classes.dex */
public final class GameRequestSummaryBuffer extends DataBuffer<GameRequestSummary> {
    @Override // com.google.android.gms.common.data.DataBuffer
    /* renamed from: dW, reason: merged with bridge method [inline-methods] */
    public GameRequestSummary get(int i) {
        return new GameRequestSummaryRef(this.IC, i);
    }
}
