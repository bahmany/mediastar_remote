package com.google.android.gms.games.internal.experience;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.d;
import com.google.android.gms.games.GameRef;

/* loaded from: classes.dex */
public final class ExperienceEventRef extends d implements ExperienceEvent {
    private final GameRef aam;

    public ExperienceEventRef(DataHolder holder, int dataRow) {
        super(holder, dataRow);
        if (aS("external_game_id")) {
            this.aam = null;
        } else {
            this.aam = new GameRef(this.IC, this.JQ);
        }
    }

    @Override // com.google.android.gms.games.internal.experience.ExperienceEvent
    public String getIconImageUrl() {
        return getString("icon_url");
    }
}
