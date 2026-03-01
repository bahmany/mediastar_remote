package mktvsmart.screen;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class GsEPGTableChannel {
    static final byte maxEpgDay = 7;
    private String ProgramId;
    private String ProgramName;
    private int currentEpgTime;
    private int originalNetworkID;
    private int progNo;
    private byte todayDate;
    private int transportStreamID;
    private int[] arrayEventFields = new int[7];
    public EpgDay[] epgChannelEvent = new EpgDay[7];

    public class EpgDay {
        private ArrayList<GsEPGEvent> arrayEventDay = new ArrayList<>();

        public EpgDay() {
        }

        public ArrayList<GsEPGEvent> getArrayList() {
            return this.arrayEventDay;
        }
    }

    public GsEPGTableChannel() {
        for (int i = 0; i < 7; i++) {
            this.epgChannelEvent[i] = new EpgDay();
        }
    }

    public EpgDay getEpgDayByIndex(int index) {
        return this.epgChannelEvent[index];
    }

    public int getProgNo() {
        return this.progNo;
    }

    public void setProgNo(int progNo) {
        this.progNo = progNo;
    }

    public int getOriginalNetworkID() {
        return this.originalNetworkID;
    }

    public void setOriginalNetworkID(int originalNetworkID) {
        this.originalNetworkID = originalNetworkID;
    }

    public int getTransportStreamID() {
        return this.transportStreamID;
    }

    public void setTransportStreamID(int transportStreamID) {
        this.transportStreamID = transportStreamID;
    }

    public byte getTodayDate() {
        return this.todayDate;
    }

    public void setTodayDate(byte todayDate) {
        this.todayDate = todayDate;
    }

    public int[] getArrayEventFields() {
        return this.arrayEventFields;
    }

    public void setArrayEventFieldByIndex(int index, int value) {
        this.arrayEventFields[index] = value;
    }

    public EpgDay[] getEpgChannelEvent() {
        return this.epgChannelEvent;
    }

    public void setEpgChannelEvent(EpgDay[] epgChannelEvent) {
        this.epgChannelEvent = epgChannelEvent;
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

    public int getCurrentEpgTime() {
        return this.currentEpgTime;
    }

    public void setCurrentEpgTime(int currentEpgTime) {
        this.currentEpgTime = currentEpgTime;
    }
}
