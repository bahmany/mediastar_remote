package com.google.android.gms.plus.model.moments;

import com.google.android.gms.common.data.Freezable;
import com.google.android.gms.internal.nt;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public interface ItemScope extends Freezable<ItemScope> {

    public static class Builder {
        private String BL;
        private String Tg;
        private double adZ;
        private double aea;
        private final Set<Integer> alR = new HashSet();
        private nt alS;
        private List<String> alT;
        private nt alU;
        private String alV;
        private String alW;
        private String alX;
        private List<nt> alY;
        private int alZ;
        private String amA;
        private String amB;
        private String amC;
        private nt amD;
        private String amE;
        private String amF;
        private String amG;
        private nt amH;
        private String amI;
        private String amJ;
        private String amK;
        private String amL;
        private List<nt> ama;
        private nt amb;
        private List<nt> amc;
        private String amd;
        private String ame;
        private nt amf;
        private String amg;
        private String amh;
        private List<nt> ami;
        private String amj;
        private String amk;
        private String aml;
        private String amm;
        private String amn;
        private String amo;
        private String amp;
        private String amq;
        private nt amr;
        private String ams;
        private String amt;
        private String amu;
        private nt amv;
        private nt amw;
        private nt amx;
        private List<nt> amy;
        private String amz;
        private String mName;
        private String ol;
        private String uO;
        private String uR;

        public ItemScope build() {
            return new nt(this.alR, this.alS, this.alT, this.alU, this.alV, this.alW, this.alX, this.alY, this.alZ, this.ama, this.amb, this.amc, this.amd, this.ame, this.amf, this.amg, this.amh, this.ol, this.ami, this.amj, this.amk, this.aml, this.Tg, this.amm, this.amn, this.amo, this.amp, this.amq, this.amr, this.ams, this.amt, this.BL, this.amu, this.amv, this.adZ, this.amw, this.aea, this.mName, this.amx, this.amy, this.amz, this.amA, this.amB, this.amC, this.amD, this.amE, this.amF, this.amG, this.amH, this.amI, this.amJ, this.uO, this.uR, this.amK, this.amL);
        }

        public Builder setAbout(ItemScope about) {
            this.alS = (nt) about;
            this.alR.add(2);
            return this;
        }

        public Builder setAdditionalName(List<String> additionalName) {
            this.alT = additionalName;
            this.alR.add(3);
            return this;
        }

        public Builder setAddress(ItemScope address) {
            this.alU = (nt) address;
            this.alR.add(4);
            return this;
        }

        public Builder setAddressCountry(String addressCountry) {
            this.alV = addressCountry;
            this.alR.add(5);
            return this;
        }

        public Builder setAddressLocality(String addressLocality) {
            this.alW = addressLocality;
            this.alR.add(6);
            return this;
        }

        public Builder setAddressRegion(String addressRegion) {
            this.alX = addressRegion;
            this.alR.add(7);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Builder setAssociated_media(List<ItemScope> list) {
            this.alY = list;
            this.alR.add(8);
            return this;
        }

        public Builder setAttendeeCount(int attendeeCount) {
            this.alZ = attendeeCount;
            this.alR.add(9);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Builder setAttendees(List<ItemScope> list) {
            this.ama = list;
            this.alR.add(10);
            return this;
        }

        public Builder setAudio(ItemScope audio) {
            this.amb = (nt) audio;
            this.alR.add(11);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Builder setAuthor(List<ItemScope> list) {
            this.amc = list;
            this.alR.add(12);
            return this;
        }

        public Builder setBestRating(String bestRating) {
            this.amd = bestRating;
            this.alR.add(13);
            return this;
        }

        public Builder setBirthDate(String birthDate) {
            this.ame = birthDate;
            this.alR.add(14);
            return this;
        }

        public Builder setByArtist(ItemScope byArtist) {
            this.amf = (nt) byArtist;
            this.alR.add(15);
            return this;
        }

        public Builder setCaption(String caption) {
            this.amg = caption;
            this.alR.add(16);
            return this;
        }

        public Builder setContentSize(String contentSize) {
            this.amh = contentSize;
            this.alR.add(17);
            return this;
        }

        public Builder setContentUrl(String contentUrl) {
            this.ol = contentUrl;
            this.alR.add(18);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Builder setContributor(List<ItemScope> list) {
            this.ami = list;
            this.alR.add(19);
            return this;
        }

        public Builder setDateCreated(String dateCreated) {
            this.amj = dateCreated;
            this.alR.add(20);
            return this;
        }

        public Builder setDateModified(String dateModified) {
            this.amk = dateModified;
            this.alR.add(21);
            return this;
        }

        public Builder setDatePublished(String datePublished) {
            this.aml = datePublished;
            this.alR.add(22);
            return this;
        }

        public Builder setDescription(String description) {
            this.Tg = description;
            this.alR.add(23);
            return this;
        }

        public Builder setDuration(String duration) {
            this.amm = duration;
            this.alR.add(24);
            return this;
        }

        public Builder setEmbedUrl(String embedUrl) {
            this.amn = embedUrl;
            this.alR.add(25);
            return this;
        }

        public Builder setEndDate(String endDate) {
            this.amo = endDate;
            this.alR.add(26);
            return this;
        }

        public Builder setFamilyName(String familyName) {
            this.amp = familyName;
            this.alR.add(27);
            return this;
        }

        public Builder setGender(String gender) {
            this.amq = gender;
            this.alR.add(28);
            return this;
        }

        public Builder setGeo(ItemScope geo) {
            this.amr = (nt) geo;
            this.alR.add(29);
            return this;
        }

        public Builder setGivenName(String givenName) {
            this.ams = givenName;
            this.alR.add(30);
            return this;
        }

        public Builder setHeight(String height) {
            this.amt = height;
            this.alR.add(31);
            return this;
        }

        public Builder setId(String id) {
            this.BL = id;
            this.alR.add(32);
            return this;
        }

        public Builder setImage(String image) {
            this.amu = image;
            this.alR.add(33);
            return this;
        }

        public Builder setInAlbum(ItemScope inAlbum) {
            this.amv = (nt) inAlbum;
            this.alR.add(34);
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.adZ = latitude;
            this.alR.add(36);
            return this;
        }

        public Builder setLocation(ItemScope location) {
            this.amw = (nt) location;
            this.alR.add(37);
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.aea = longitude;
            this.alR.add(38);
            return this;
        }

        public Builder setName(String name) {
            this.mName = name;
            this.alR.add(39);
            return this;
        }

        public Builder setPartOfTVSeries(ItemScope partOfTVSeries) {
            this.amx = (nt) partOfTVSeries;
            this.alR.add(40);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Builder setPerformers(List<ItemScope> list) {
            this.amy = list;
            this.alR.add(41);
            return this;
        }

        public Builder setPlayerType(String playerType) {
            this.amz = playerType;
            this.alR.add(42);
            return this;
        }

        public Builder setPostOfficeBoxNumber(String postOfficeBoxNumber) {
            this.amA = postOfficeBoxNumber;
            this.alR.add(43);
            return this;
        }

        public Builder setPostalCode(String postalCode) {
            this.amB = postalCode;
            this.alR.add(44);
            return this;
        }

        public Builder setRatingValue(String ratingValue) {
            this.amC = ratingValue;
            this.alR.add(45);
            return this;
        }

        public Builder setReviewRating(ItemScope reviewRating) {
            this.amD = (nt) reviewRating;
            this.alR.add(46);
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.amE = startDate;
            this.alR.add(47);
            return this;
        }

        public Builder setStreetAddress(String streetAddress) {
            this.amF = streetAddress;
            this.alR.add(48);
            return this;
        }

        public Builder setText(String text) {
            this.amG = text;
            this.alR.add(49);
            return this;
        }

        public Builder setThumbnail(ItemScope thumbnail) {
            this.amH = (nt) thumbnail;
            this.alR.add(50);
            return this;
        }

        public Builder setThumbnailUrl(String thumbnailUrl) {
            this.amI = thumbnailUrl;
            this.alR.add(51);
            return this;
        }

        public Builder setTickerSymbol(String tickerSymbol) {
            this.amJ = tickerSymbol;
            this.alR.add(52);
            return this;
        }

        public Builder setType(String type) {
            this.uO = type;
            this.alR.add(53);
            return this;
        }

        public Builder setUrl(String url) {
            this.uR = url;
            this.alR.add(54);
            return this;
        }

        public Builder setWidth(String width) {
            this.amK = width;
            this.alR.add(55);
            return this;
        }

        public Builder setWorstRating(String worstRating) {
            this.amL = worstRating;
            this.alR.add(56);
            return this;
        }
    }

    ItemScope getAbout();

    List<String> getAdditionalName();

    ItemScope getAddress();

    String getAddressCountry();

    String getAddressLocality();

    String getAddressRegion();

    List<ItemScope> getAssociated_media();

    int getAttendeeCount();

    List<ItemScope> getAttendees();

    ItemScope getAudio();

    List<ItemScope> getAuthor();

    String getBestRating();

    String getBirthDate();

    ItemScope getByArtist();

    String getCaption();

    String getContentSize();

    String getContentUrl();

    List<ItemScope> getContributor();

    String getDateCreated();

    String getDateModified();

    String getDatePublished();

    String getDescription();

    String getDuration();

    String getEmbedUrl();

    String getEndDate();

    String getFamilyName();

    String getGender();

    ItemScope getGeo();

    String getGivenName();

    String getHeight();

    String getId();

    String getImage();

    ItemScope getInAlbum();

    double getLatitude();

    ItemScope getLocation();

    double getLongitude();

    String getName();

    ItemScope getPartOfTVSeries();

    List<ItemScope> getPerformers();

    String getPlayerType();

    String getPostOfficeBoxNumber();

    String getPostalCode();

    String getRatingValue();

    ItemScope getReviewRating();

    String getStartDate();

    String getStreetAddress();

    String getText();

    ItemScope getThumbnail();

    String getThumbnailUrl();

    String getTickerSymbol();

    String getType();

    String getUrl();

    String getWidth();

    String getWorstRating();

    boolean hasAbout();

    boolean hasAdditionalName();

    boolean hasAddress();

    boolean hasAddressCountry();

    boolean hasAddressLocality();

    boolean hasAddressRegion();

    boolean hasAssociated_media();

    boolean hasAttendeeCount();

    boolean hasAttendees();

    boolean hasAudio();

    boolean hasAuthor();

    boolean hasBestRating();

    boolean hasBirthDate();

    boolean hasByArtist();

    boolean hasCaption();

    boolean hasContentSize();

    boolean hasContentUrl();

    boolean hasContributor();

    boolean hasDateCreated();

    boolean hasDateModified();

    boolean hasDatePublished();

    boolean hasDescription();

    boolean hasDuration();

    boolean hasEmbedUrl();

    boolean hasEndDate();

    boolean hasFamilyName();

    boolean hasGender();

    boolean hasGeo();

    boolean hasGivenName();

    boolean hasHeight();

    boolean hasId();

    boolean hasImage();

    boolean hasInAlbum();

    boolean hasLatitude();

    boolean hasLocation();

    boolean hasLongitude();

    boolean hasName();

    boolean hasPartOfTVSeries();

    boolean hasPerformers();

    boolean hasPlayerType();

    boolean hasPostOfficeBoxNumber();

    boolean hasPostalCode();

    boolean hasRatingValue();

    boolean hasReviewRating();

    boolean hasStartDate();

    boolean hasStreetAddress();

    boolean hasText();

    boolean hasThumbnail();

    boolean hasThumbnailUrl();

    boolean hasTickerSymbol();

    boolean hasType();

    boolean hasUrl();

    boolean hasWidth();

    boolean hasWorstRating();
}
