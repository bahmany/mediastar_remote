package mktvsmart.screen;

/* loaded from: classes.dex */
public class GsEPGEvent {
    static final byte maxEpgLanguage = 5;
    private String ProgramId;
    private String ProgramName;
    private byte ageRating;
    private String endTime;
    private byte epgTimerType;
    private int eventDate;
    private int eventMonth;
    private String startTime;
    private byte totalEpgLanguage;
    private short[] titleLanCode = new short[5];
    private short[] subtitleLanCode = new short[5];
    private short[] descLanCode = new short[5];
    private short[] titleLen = new short[5];
    private short[] subtitleLen = new short[5];
    private short[] descLen = new short[5];
    private String[] eventTitle = new String[5];
    private String[] eventSubTitle = new String[5];
    private String[] eventDesc = new String[5];

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public short[] getTitleLanCode() {
        return this.titleLanCode;
    }

    public void setTitleLanCode(int index, int value) {
        this.titleLanCode[index] = (short) value;
    }

    public short[] getSubtitleLanCode() {
        return this.subtitleLanCode;
    }

    public void setSubtitleLanCode(int index, int value) {
        this.subtitleLanCode[index] = (short) value;
    }

    public short[] getDescLanCode() {
        return this.descLanCode;
    }

    public void setDescLanCode(int index, int value) {
        this.descLanCode[index] = (short) value;
    }

    public short[] getTitleLen() {
        return this.titleLen;
    }

    public void setTitleLen(int index, int value) {
        this.titleLen[index] = (short) value;
    }

    public short[] getSubtitleLen() {
        return this.subtitleLen;
    }

    public void setSubtitleLen(int index, int value) {
        this.subtitleLen[index] = (short) value;
    }

    public short[] getDescLen() {
        return this.descLen;
    }

    public void setDescLen(int index, int value) {
        this.descLen[index] = (short) value;
    }

    public String[] getEventTitle() {
        return this.eventTitle;
    }

    public void setEventTitle(int index, String string) {
        this.eventTitle[index] = string;
    }

    public String[] getEventSubTitle() {
        return this.eventSubTitle;
    }

    public void setEventSubTitle(int index, String string) {
        this.eventSubTitle[index] = string;
    }

    public String[] getEventDesc() {
        return this.eventDesc;
    }

    public void setEventDesc(int index, String string) {
        this.eventDesc[index] = string;
    }

    public byte getAgeRating() {
        return this.ageRating;
    }

    public void setAgeRating(int ageRating) {
        this.ageRating = (byte) ageRating;
    }

    public byte getTotalEpgLanguage() {
        return this.totalEpgLanguage;
    }

    public void setTotalEpgLanguage(int totalEpgLanguage) {
        this.totalEpgLanguage = (byte) totalEpgLanguage;
    }

    public String getProgramId() {
        return this.ProgramId;
    }

    public void setProgramId(String programId) {
        this.ProgramId = programId;
    }

    public String getProgramName() {
        return this.ProgramName;
    }

    public void setProgramName(String programName) {
        this.ProgramName = programName;
    }

    public int getEventDate() {
        return this.eventDate;
    }

    public void setEventDate(int eventDate) {
        this.eventDate = eventDate;
    }

    public byte getEpgTimerType() {
        return this.epgTimerType;
    }

    public void setEpgTimerType(int epgTimerType) {
        this.epgTimerType = (byte) epgTimerType;
    }

    public int getEventMonth() {
        return this.eventMonth;
    }

    public void setEventMonth(int eventMonth) {
        this.eventMonth = eventMonth;
    }
}
