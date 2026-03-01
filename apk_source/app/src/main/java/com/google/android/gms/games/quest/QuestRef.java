package com.google.android.gms.games.quest;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.d;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.GameRef;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class QuestRef extends d implements Quest {
    private final int aaz;
    private final Game abm;

    QuestRef(DataHolder holder, int dataRow, int numChildren) {
        super(holder, dataRow);
        this.abm = new GameRef(holder, dataRow);
        this.aaz = numChildren;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.gms.common.data.d
    public boolean equals(Object obj) {
        return QuestEntity.a(this, obj);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.gms.common.data.Freezable
    public Quest freeze() {
        return new QuestEntity(this);
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long getAcceptedTimestamp() {
        return getLong("accepted_ts");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public Uri getBannerImageUri() {
        return aR("quest_banner_image_uri");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getBannerImageUrl() {
        return getString("quest_banner_image_url");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public Milestone getCurrentMilestone() {
        return lH().get(0);
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getDescription() {
        return getString("quest_description");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public void getDescription(CharArrayBuffer dataOut) {
        a("quest_description", dataOut);
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long getEndTimestamp() {
        return getLong("quest_end_ts");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public Game getGame() {
        return this.abm;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public Uri getIconImageUri() {
        return aR("quest_icon_image_uri");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getIconImageUrl() {
        return getString("quest_icon_image_url");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long getLastUpdatedTimestamp() {
        return getLong("quest_last_updated_ts");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getName() {
        return getString("quest_name");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public void getName(CharArrayBuffer dataOut) {
        a("quest_name", dataOut);
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getQuestId() {
        return getString("external_quest_id");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long getStartTimestamp() {
        return getLong("quest_start_ts");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public int getState() {
        return getInteger("quest_state");
    }

    @Override // com.google.android.gms.games.quest.Quest
    public int getType() {
        return getInteger("quest_type");
    }

    @Override // com.google.android.gms.common.data.d
    public int hashCode() {
        return QuestEntity.a(this);
    }

    @Override // com.google.android.gms.games.quest.Quest
    public boolean isEndingSoon() {
        return lI() <= System.currentTimeMillis() + 1800000;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public List<Milestone> lH() {
        ArrayList arrayList = new ArrayList(this.aaz);
        for (int i = 0; i < this.aaz; i++) {
            arrayList.add(new MilestoneRef(this.IC, this.JQ + i));
        }
        return arrayList;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long lI() {
        return getLong("notification_ts");
    }

    public String toString() {
        return QuestEntity.b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ((QuestEntity) freeze()).writeToParcel(dest, flags);
    }
}
