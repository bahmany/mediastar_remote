package com.google.android.gms.games.quest;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.g;

/* loaded from: classes.dex */
public final class QuestBuffer extends g<Quest> {
    public QuestBuffer(DataHolder dataHolder) {
        super(dataHolder);
    }

    @Override // com.google.android.gms.common.data.g
    protected String gE() {
        return "external_quest_id";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.data.g
    /* renamed from: m, reason: merged with bridge method [inline-methods] */
    public Quest f(int i, int i2) {
        return new QuestRef(this.IC, i, i2);
    }
}
