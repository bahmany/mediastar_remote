package com.google.android.gms.games.quest;

import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.internal.jv;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class QuestEntity implements SafeParcelable, Quest {
    public static final QuestEntityCreator CREATOR = new QuestEntityCreator();
    private final int BR;
    private final int FD;
    private final String Tg;
    private final long VZ;
    private final GameEntity aan;
    private final String acG;
    private final long acH;
    private final Uri acI;
    private final String acJ;
    private final long acK;
    private final Uri acL;
    private final String acM;
    private final long acN;
    private final long acO;
    private final ArrayList<MilestoneEntity> acP;
    private final String mName;
    private final int mState;

    QuestEntity(int versionCode, GameEntity game, String questId, long acceptedTimestamp, Uri bannerImageUri, String bannerImageUrl, String description, long endTimestamp, long lastUpdatedTimestamp, Uri iconImageUri, String iconImageUrl, String name, long notifyTimestamp, long startTimestamp, int state, int type, ArrayList<MilestoneEntity> milestones) {
        this.BR = versionCode;
        this.aan = game;
        this.acG = questId;
        this.acH = acceptedTimestamp;
        this.acI = bannerImageUri;
        this.acJ = bannerImageUrl;
        this.Tg = description;
        this.acK = endTimestamp;
        this.VZ = lastUpdatedTimestamp;
        this.acL = iconImageUri;
        this.acM = iconImageUrl;
        this.mName = name;
        this.acN = notifyTimestamp;
        this.acO = startTimestamp;
        this.mState = state;
        this.FD = type;
        this.acP = milestones;
    }

    public QuestEntity(Quest quest) {
        this.BR = 2;
        this.aan = new GameEntity(quest.getGame());
        this.acG = quest.getQuestId();
        this.acH = quest.getAcceptedTimestamp();
        this.Tg = quest.getDescription();
        this.acI = quest.getBannerImageUri();
        this.acJ = quest.getBannerImageUrl();
        this.acK = quest.getEndTimestamp();
        this.acL = quest.getIconImageUri();
        this.acM = quest.getIconImageUrl();
        this.VZ = quest.getLastUpdatedTimestamp();
        this.mName = quest.getName();
        this.acN = quest.lI();
        this.acO = quest.getStartTimestamp();
        this.mState = quest.getState();
        this.FD = quest.getType();
        List<Milestone> listLH = quest.lH();
        int size = listLH.size();
        this.acP = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.acP.add((MilestoneEntity) listLH.get(i).freeze());
        }
    }

    static int a(Quest quest) {
        return m.hashCode(quest.getGame(), quest.getQuestId(), Long.valueOf(quest.getAcceptedTimestamp()), quest.getBannerImageUri(), quest.getDescription(), Long.valueOf(quest.getEndTimestamp()), quest.getIconImageUri(), Long.valueOf(quest.getLastUpdatedTimestamp()), quest.lH(), quest.getName(), Long.valueOf(quest.lI()), Long.valueOf(quest.getStartTimestamp()), Integer.valueOf(quest.getState()));
    }

    static boolean a(Quest quest, Object obj) {
        if (!(obj instanceof Quest)) {
            return false;
        }
        if (quest == obj) {
            return true;
        }
        Quest quest2 = (Quest) obj;
        return m.equal(quest2.getGame(), quest.getGame()) && m.equal(quest2.getQuestId(), quest.getQuestId()) && m.equal(Long.valueOf(quest2.getAcceptedTimestamp()), Long.valueOf(quest.getAcceptedTimestamp())) && m.equal(quest2.getBannerImageUri(), quest.getBannerImageUri()) && m.equal(quest2.getDescription(), quest.getDescription()) && m.equal(Long.valueOf(quest2.getEndTimestamp()), Long.valueOf(quest.getEndTimestamp())) && m.equal(quest2.getIconImageUri(), quest.getIconImageUri()) && m.equal(Long.valueOf(quest2.getLastUpdatedTimestamp()), Long.valueOf(quest.getLastUpdatedTimestamp())) && m.equal(quest2.lH(), quest.lH()) && m.equal(quest2.getName(), quest.getName()) && m.equal(Long.valueOf(quest2.lI()), Long.valueOf(quest.lI())) && m.equal(Long.valueOf(quest2.getStartTimestamp()), Long.valueOf(quest.getStartTimestamp())) && m.equal(Integer.valueOf(quest2.getState()), Integer.valueOf(quest.getState()));
    }

    static String b(Quest quest) {
        return m.h(quest).a("Game", quest.getGame()).a("QuestId", quest.getQuestId()).a("AcceptedTimestamp", Long.valueOf(quest.getAcceptedTimestamp())).a("BannerImageUri", quest.getBannerImageUri()).a("BannerImageUrl", quest.getBannerImageUrl()).a("Description", quest.getDescription()).a("EndTimestamp", Long.valueOf(quest.getEndTimestamp())).a("IconImageUri", quest.getIconImageUri()).a("IconImageUrl", quest.getIconImageUrl()).a("LastUpdatedTimestamp", Long.valueOf(quest.getLastUpdatedTimestamp())).a("Milestones", quest.lH()).a("Name", quest.getName()).a("NotifyTimestamp", Long.valueOf(quest.lI())).a("StartTimestamp", Long.valueOf(quest.getStartTimestamp())).a("State", Integer.valueOf(quest.getState())).toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public Quest freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long getAcceptedTimestamp() {
        return this.acH;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public Uri getBannerImageUri() {
        return this.acI;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getBannerImageUrl() {
        return this.acJ;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public Milestone getCurrentMilestone() {
        return lH().get(0);
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getDescription() {
        return this.Tg;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public void getDescription(CharArrayBuffer dataOut) {
        jv.b(this.Tg, dataOut);
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long getEndTimestamp() {
        return this.acK;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public Game getGame() {
        return this.aan;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public Uri getIconImageUri() {
        return this.acL;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getIconImageUrl() {
        return this.acM;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long getLastUpdatedTimestamp() {
        return this.VZ;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getName() {
        return this.mName;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public void getName(CharArrayBuffer dataOut) {
        jv.b(this.mName, dataOut);
    }

    @Override // com.google.android.gms.games.quest.Quest
    public String getQuestId() {
        return this.acG;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long getStartTimestamp() {
        return this.acO;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public int getState() {
        return this.mState;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public int getType() {
        return this.FD;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return a(this);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public boolean isEndingSoon() {
        return this.acN <= System.currentTimeMillis() + 1800000;
    }

    @Override // com.google.android.gms.games.quest.Quest
    public List<Milestone> lH() {
        return new ArrayList(this.acP);
    }

    @Override // com.google.android.gms.games.quest.Quest
    public long lI() {
        return this.acN;
    }

    public String toString() {
        return b(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        QuestEntityCreator.a(this, out, flags);
    }
}
