package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.internal.ji;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.moments.ItemScope;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public final class nt extends jj implements ItemScope {
    public static final nu CREATOR = new nu();
    private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
    String BL;
    final int BR;
    String Tg;
    double adZ;
    double aea;
    final Set<Integer> alR;
    nt alS;
    List<String> alT;
    nt alU;
    String alV;
    String alW;
    String alX;
    List<nt> alY;
    int alZ;
    String amA;
    String amB;
    String amC;
    nt amD;
    String amE;
    String amF;
    String amG;
    nt amH;
    String amI;
    String amJ;
    String amK;
    String amL;
    List<nt> ama;
    nt amb;
    List<nt> amc;
    String amd;
    String ame;
    nt amf;
    String amg;
    String amh;
    List<nt> ami;
    String amj;
    String amk;
    String aml;
    String amm;
    String amn;
    String amo;
    String amp;
    String amq;
    nt amr;
    String ams;
    String amt;
    String amu;
    nt amv;
    nt amw;
    nt amx;
    List<nt> amy;
    String amz;
    String mName;
    String ol;
    String uO;
    String uR;

    static {
        alQ.put("about", ji.a.a("about", 2, nt.class));
        alQ.put("additionalName", ji.a.m("additionalName", 3));
        alQ.put("address", ji.a.a("address", 4, nt.class));
        alQ.put("addressCountry", ji.a.l("addressCountry", 5));
        alQ.put("addressLocality", ji.a.l("addressLocality", 6));
        alQ.put("addressRegion", ji.a.l("addressRegion", 7));
        alQ.put("associated_media", ji.a.b("associated_media", 8, nt.class));
        alQ.put("attendeeCount", ji.a.i("attendeeCount", 9));
        alQ.put("attendees", ji.a.b("attendees", 10, nt.class));
        alQ.put(MultiSettingActivity.AUDIO_STATUS_KEY, ji.a.a(MultiSettingActivity.AUDIO_STATUS_KEY, 11, nt.class));
        alQ.put("author", ji.a.b("author", 12, nt.class));
        alQ.put("bestRating", ji.a.l("bestRating", 13));
        alQ.put("birthDate", ji.a.l("birthDate", 14));
        alQ.put("byArtist", ji.a.a("byArtist", 15, nt.class));
        alQ.put("caption", ji.a.l("caption", 16));
        alQ.put("contentSize", ji.a.l("contentSize", 17));
        alQ.put("contentUrl", ji.a.l("contentUrl", 18));
        alQ.put("contributor", ji.a.b("contributor", 19, nt.class));
        alQ.put("dateCreated", ji.a.l("dateCreated", 20));
        alQ.put("dateModified", ji.a.l("dateModified", 21));
        alQ.put("datePublished", ji.a.l("datePublished", 22));
        alQ.put(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION, ji.a.l(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION, 23));
        alQ.put("duration", ji.a.l("duration", 24));
        alQ.put("embedUrl", ji.a.l("embedUrl", 25));
        alQ.put("endDate", ji.a.l("endDate", 26));
        alQ.put("familyName", ji.a.l("familyName", 27));
        alQ.put("gender", ji.a.l("gender", 28));
        alQ.put("geo", ji.a.a("geo", 29, nt.class));
        alQ.put("givenName", ji.a.l("givenName", 30));
        alQ.put("height", ji.a.l("height", 31));
        alQ.put("id", ji.a.l("id", 32));
        alQ.put("image", ji.a.l("image", 33));
        alQ.put("inAlbum", ji.a.a("inAlbum", 34, nt.class));
        alQ.put("latitude", ji.a.j("latitude", 36));
        alQ.put("location", ji.a.a("location", 37, nt.class));
        alQ.put("longitude", ji.a.j("longitude", 38));
        alQ.put("name", ji.a.l("name", 39));
        alQ.put("partOfTVSeries", ji.a.a("partOfTVSeries", 40, nt.class));
        alQ.put("performers", ji.a.b("performers", 41, nt.class));
        alQ.put("playerType", ji.a.l("playerType", 42));
        alQ.put("postOfficeBoxNumber", ji.a.l("postOfficeBoxNumber", 43));
        alQ.put("postalCode", ji.a.l("postalCode", 44));
        alQ.put("ratingValue", ji.a.l("ratingValue", 45));
        alQ.put("reviewRating", ji.a.a("reviewRating", 46, nt.class));
        alQ.put("startDate", ji.a.l("startDate", 47));
        alQ.put("streetAddress", ji.a.l("streetAddress", 48));
        alQ.put("text", ji.a.l("text", 49));
        alQ.put("thumbnail", ji.a.a("thumbnail", 50, nt.class));
        alQ.put(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_THUMBNAIL_URL, ji.a.l(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_THUMBNAIL_URL, 51));
        alQ.put("tickerSymbol", ji.a.l("tickerSymbol", 52));
        alQ.put(PlaylistSQLiteHelper.COL_TYPE, ji.a.l(PlaylistSQLiteHelper.COL_TYPE, 53));
        alQ.put("url", ji.a.l("url", 54));
        alQ.put("width", ji.a.l("width", 55));
        alQ.put("worstRating", ji.a.l("worstRating", 56));
    }

    public nt() {
        this.BR = 1;
        this.alR = new HashSet();
    }

    nt(Set<Integer> set, int i, nt ntVar, List<String> list, nt ntVar2, String str, String str2, String str3, List<nt> list2, int i2, List<nt> list3, nt ntVar3, List<nt> list4, String str4, String str5, nt ntVar4, String str6, String str7, String str8, List<nt> list5, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, nt ntVar5, String str18, String str19, String str20, String str21, nt ntVar6, double d, nt ntVar7, double d2, String str22, nt ntVar8, List<nt> list6, String str23, String str24, String str25, String str26, nt ntVar9, String str27, String str28, String str29, nt ntVar10, String str30, String str31, String str32, String str33, String str34, String str35) {
        this.alR = set;
        this.BR = i;
        this.alS = ntVar;
        this.alT = list;
        this.alU = ntVar2;
        this.alV = str;
        this.alW = str2;
        this.alX = str3;
        this.alY = list2;
        this.alZ = i2;
        this.ama = list3;
        this.amb = ntVar3;
        this.amc = list4;
        this.amd = str4;
        this.ame = str5;
        this.amf = ntVar4;
        this.amg = str6;
        this.amh = str7;
        this.ol = str8;
        this.ami = list5;
        this.amj = str9;
        this.amk = str10;
        this.aml = str11;
        this.Tg = str12;
        this.amm = str13;
        this.amn = str14;
        this.amo = str15;
        this.amp = str16;
        this.amq = str17;
        this.amr = ntVar5;
        this.ams = str18;
        this.amt = str19;
        this.BL = str20;
        this.amu = str21;
        this.amv = ntVar6;
        this.adZ = d;
        this.amw = ntVar7;
        this.aea = d2;
        this.mName = str22;
        this.amx = ntVar8;
        this.amy = list6;
        this.amz = str23;
        this.amA = str24;
        this.amB = str25;
        this.amC = str26;
        this.amD = ntVar9;
        this.amE = str27;
        this.amF = str28;
        this.amG = str29;
        this.amH = ntVar10;
        this.amI = str30;
        this.amJ = str31;
        this.uO = str32;
        this.uR = str33;
        this.amK = str34;
        this.amL = str35;
    }

    public nt(Set<Integer> set, nt ntVar, List<String> list, nt ntVar2, String str, String str2, String str3, List<nt> list2, int i, List<nt> list3, nt ntVar3, List<nt> list4, String str4, String str5, nt ntVar4, String str6, String str7, String str8, List<nt> list5, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, nt ntVar5, String str18, String str19, String str20, String str21, nt ntVar6, double d, nt ntVar7, double d2, String str22, nt ntVar8, List<nt> list6, String str23, String str24, String str25, String str26, nt ntVar9, String str27, String str28, String str29, nt ntVar10, String str30, String str31, String str32, String str33, String str34, String str35) {
        this.alR = set;
        this.BR = 1;
        this.alS = ntVar;
        this.alT = list;
        this.alU = ntVar2;
        this.alV = str;
        this.alW = str2;
        this.alX = str3;
        this.alY = list2;
        this.alZ = i;
        this.ama = list3;
        this.amb = ntVar3;
        this.amc = list4;
        this.amd = str4;
        this.ame = str5;
        this.amf = ntVar4;
        this.amg = str6;
        this.amh = str7;
        this.ol = str8;
        this.ami = list5;
        this.amj = str9;
        this.amk = str10;
        this.aml = str11;
        this.Tg = str12;
        this.amm = str13;
        this.amn = str14;
        this.amo = str15;
        this.amp = str16;
        this.amq = str17;
        this.amr = ntVar5;
        this.ams = str18;
        this.amt = str19;
        this.BL = str20;
        this.amu = str21;
        this.amv = ntVar6;
        this.adZ = d;
        this.amw = ntVar7;
        this.aea = d2;
        this.mName = str22;
        this.amx = ntVar8;
        this.amy = list6;
        this.amz = str23;
        this.amA = str24;
        this.amB = str25;
        this.amC = str26;
        this.amD = ntVar9;
        this.amE = str27;
        this.amF = str28;
        this.amG = str29;
        this.amH = ntVar10;
        this.amI = str30;
        this.amJ = str31;
        this.uO = str32;
        this.uR = str33;
        this.amK = str34;
        this.amL = str35;
    }

    @Override // com.google.android.gms.internal.ji
    protected boolean a(ji.a aVar) {
        return this.alR.contains(Integer.valueOf(aVar.hm()));
    }

    @Override // com.google.android.gms.internal.ji
    protected Object b(ji.a aVar) {
        switch (aVar.hm()) {
            case 2:
                return this.alS;
            case 3:
                return this.alT;
            case 4:
                return this.alU;
            case 5:
                return this.alV;
            case 6:
                return this.alW;
            case 7:
                return this.alX;
            case 8:
                return this.alY;
            case 9:
                return Integer.valueOf(this.alZ);
            case 10:
                return this.ama;
            case 11:
                return this.amb;
            case 12:
                return this.amc;
            case 13:
                return this.amd;
            case 14:
                return this.ame;
            case 15:
                return this.amf;
            case 16:
                return this.amg;
            case 17:
                return this.amh;
            case 18:
                return this.ol;
            case 19:
                return this.ami;
            case 20:
                return this.amj;
            case 21:
                return this.amk;
            case 22:
                return this.aml;
            case 23:
                return this.Tg;
            case 24:
                return this.amm;
            case 25:
                return this.amn;
            case 26:
                return this.amo;
            case 27:
                return this.amp;
            case 28:
                return this.amq;
            case 29:
                return this.amr;
            case 30:
                return this.ams;
            case 31:
                return this.amt;
            case 32:
                return this.BL;
            case 33:
                return this.amu;
            case 34:
                return this.amv;
            case 35:
            default:
                throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            case 36:
                return Double.valueOf(this.adZ);
            case 37:
                return this.amw;
            case 38:
                return Double.valueOf(this.aea);
            case 39:
                return this.mName;
            case 40:
                return this.amx;
            case 41:
                return this.amy;
            case 42:
                return this.amz;
            case 43:
                return this.amA;
            case 44:
                return this.amB;
            case 45:
                return this.amC;
            case 46:
                return this.amD;
            case 47:
                return this.amE;
            case 48:
                return this.amF;
            case 49:
                return this.amG;
            case 50:
                return this.amH;
            case 51:
                return this.amI;
            case 52:
                return this.amJ;
            case 53:
                return this.uO;
            case 54:
                return this.uR;
            case 55:
                return this.amK;
            case 56:
                return this.amL;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        nu nuVar = CREATOR;
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof nt)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        nt ntVar = (nt) obj;
        for (ji.a<?, ?> aVar : alQ.values()) {
            if (a(aVar)) {
                if (ntVar.a(aVar) && b(aVar).equals(ntVar.b(aVar))) {
                }
                return false;
            }
            if (ntVar.a(aVar)) {
                return false;
            }
        }
        return true;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getAbout() {
        return this.alS;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public List<String> getAdditionalName() {
        return this.alT;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getAddress() {
        return this.alU;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getAddressCountry() {
        return this.alV;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getAddressLocality() {
        return this.alW;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getAddressRegion() {
        return this.alX;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public List<ItemScope> getAssociated_media() {
        return (ArrayList) this.alY;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public int getAttendeeCount() {
        return this.alZ;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public List<ItemScope> getAttendees() {
        return (ArrayList) this.ama;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getAudio() {
        return this.amb;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public List<ItemScope> getAuthor() {
        return (ArrayList) this.amc;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getBestRating() {
        return this.amd;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getBirthDate() {
        return this.ame;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getByArtist() {
        return this.amf;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getCaption() {
        return this.amg;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getContentSize() {
        return this.amh;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getContentUrl() {
        return this.ol;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public List<ItemScope> getContributor() {
        return (ArrayList) this.ami;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getDateCreated() {
        return this.amj;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getDateModified() {
        return this.amk;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getDatePublished() {
        return this.aml;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getDescription() {
        return this.Tg;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getDuration() {
        return this.amm;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getEmbedUrl() {
        return this.amn;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getEndDate() {
        return this.amo;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getFamilyName() {
        return this.amp;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getGender() {
        return this.amq;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getGeo() {
        return this.amr;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getGivenName() {
        return this.ams;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getHeight() {
        return this.amt;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getId() {
        return this.BL;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getImage() {
        return this.amu;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getInAlbum() {
        return this.amv;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public double getLatitude() {
        return this.adZ;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getLocation() {
        return this.amw;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public double getLongitude() {
        return this.aea;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getName() {
        return this.mName;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getPartOfTVSeries() {
        return this.amx;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public List<ItemScope> getPerformers() {
        return (ArrayList) this.amy;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getPlayerType() {
        return this.amz;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getPostOfficeBoxNumber() {
        return this.amA;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getPostalCode() {
        return this.amB;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getRatingValue() {
        return this.amC;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getReviewRating() {
        return this.amD;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getStartDate() {
        return this.amE;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getStreetAddress() {
        return this.amF;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getText() {
        return this.amG;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public ItemScope getThumbnail() {
        return this.amH;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getThumbnailUrl() {
        return this.amI;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getTickerSymbol() {
        return this.amJ;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getType() {
        return this.uO;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getUrl() {
        return this.uR;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getWidth() {
        return this.amK;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public String getWorstRating() {
        return this.amL;
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAbout() {
        return this.alR.contains(2);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAdditionalName() {
        return this.alR.contains(3);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAddress() {
        return this.alR.contains(4);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAddressCountry() {
        return this.alR.contains(5);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAddressLocality() {
        return this.alR.contains(6);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAddressRegion() {
        return this.alR.contains(7);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAssociated_media() {
        return this.alR.contains(8);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAttendeeCount() {
        return this.alR.contains(9);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAttendees() {
        return this.alR.contains(10);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAudio() {
        return this.alR.contains(11);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasAuthor() {
        return this.alR.contains(12);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasBestRating() {
        return this.alR.contains(13);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasBirthDate() {
        return this.alR.contains(14);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasByArtist() {
        return this.alR.contains(15);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasCaption() {
        return this.alR.contains(16);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasContentSize() {
        return this.alR.contains(17);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasContentUrl() {
        return this.alR.contains(18);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasContributor() {
        return this.alR.contains(19);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasDateCreated() {
        return this.alR.contains(20);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasDateModified() {
        return this.alR.contains(21);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasDatePublished() {
        return this.alR.contains(22);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasDescription() {
        return this.alR.contains(23);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasDuration() {
        return this.alR.contains(24);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasEmbedUrl() {
        return this.alR.contains(25);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasEndDate() {
        return this.alR.contains(26);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasFamilyName() {
        return this.alR.contains(27);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasGender() {
        return this.alR.contains(28);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasGeo() {
        return this.alR.contains(29);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasGivenName() {
        return this.alR.contains(30);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasHeight() {
        return this.alR.contains(31);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasId() {
        return this.alR.contains(32);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasImage() {
        return this.alR.contains(33);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasInAlbum() {
        return this.alR.contains(34);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasLatitude() {
        return this.alR.contains(36);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasLocation() {
        return this.alR.contains(37);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasLongitude() {
        return this.alR.contains(38);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasName() {
        return this.alR.contains(39);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasPartOfTVSeries() {
        return this.alR.contains(40);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasPerformers() {
        return this.alR.contains(41);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasPlayerType() {
        return this.alR.contains(42);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasPostOfficeBoxNumber() {
        return this.alR.contains(43);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasPostalCode() {
        return this.alR.contains(44);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasRatingValue() {
        return this.alR.contains(45);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasReviewRating() {
        return this.alR.contains(46);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasStartDate() {
        return this.alR.contains(47);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasStreetAddress() {
        return this.alR.contains(48);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasText() {
        return this.alR.contains(49);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasThumbnail() {
        return this.alR.contains(50);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasThumbnailUrl() {
        return this.alR.contains(51);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasTickerSymbol() {
        return this.alR.contains(52);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasType() {
        return this.alR.contains(53);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasUrl() {
        return this.alR.contains(54);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasWidth() {
        return this.alR.contains(55);
    }

    @Override // com.google.android.gms.plus.model.moments.ItemScope
    public boolean hasWorstRating() {
        return this.alR.contains(56);
    }

    public int hashCode() {
        int iHashCode = 0;
        Iterator<ji.a<?, ?>> it = alQ.values().iterator();
        while (true) {
            int i = iHashCode;
            if (!it.hasNext()) {
                return i;
            }
            ji.a<?, ?> next = it.next();
            if (a(next)) {
                iHashCode = b(next).hashCode() + i + next.hm();
            } else {
                iHashCode = i;
            }
        }
    }

    @Override // com.google.android.gms.internal.ji
    public HashMap<String, ji.a<?, ?>> hf() {
        return alQ;
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: np, reason: merged with bridge method [inline-methods] */
    public nt freeze() {
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        nu nuVar = CREATOR;
        nu.a(this, out, flags);
    }
}
