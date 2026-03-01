package mktvsmart.screen.dataconvert.model;

import java.io.Serializable;

/* loaded from: classes.dex */
public class DataConvertTimeModel implements Serializable {
    public static int isConfirm = 0;
    private static final long serialVersionUID = 1;
    private String ProgramId;
    private int dayLen;
    private int hourLen;
    private boolean isShowItemDetail = false;
    private int minLen;
    private int timerDay;
    private int timerEndHour;
    private int timerEndMin;
    private int timerIndex;
    private int timerMonth;
    private String timerProgramName;
    private int timerRepeat;
    private int timerStartHour;
    private int timerStartMin;
    private int timerStatus;
    private int timerUniqID;
    public static int stbMonth = 1;
    public static int stbDay = 1;
    public static int stbHour = 0;
    public static int stbMin = 0;

    public int GetTimerDayLen() {
        return this.dayLen;
    }

    public void SetTimerDayLen(int len) {
        this.dayLen = len;
    }

    public int GetTimerHourLen() {
        return this.hourLen;
    }

    public void SetTimerHourLen(int len) {
        this.hourLen = len;
    }

    public int GetTimerMinLen() {
        return this.minLen;
    }

    public void SetTimerMinLen(int len) {
        this.minLen = len;
    }

    public int GetTimerIndex() {
        return this.timerIndex;
    }

    public void SetTimerIndex(int index) {
        this.timerIndex = index;
    }

    public boolean GetShowDetail() {
        return this.isShowItemDetail;
    }

    public void setShowDetail(boolean detail) {
        this.isShowItemDetail = detail;
    }

    public void SetTimeProgramName(String name) {
        this.timerProgramName = name;
    }

    public String GetTimeProgramName() {
        return this.timerProgramName;
    }

    public void SetTimeMonth(int month) {
        this.timerMonth = month;
    }

    public int GetTimeMonth() {
        return this.timerMonth;
    }

    public void SetTimeDay(int day) {
        this.timerDay = day;
    }

    public int GetTimeDay() {
        return this.timerDay;
    }

    public void SetStartHour(int shour) {
        this.timerStartHour = shour;
    }

    public int GetStartHour() {
        return this.timerStartHour;
    }

    public void SetStartMin(int smin) {
        this.timerStartMin = smin;
    }

    public int GetStartMin() {
        return this.timerStartMin;
    }

    public void SetEndHour(int ehour) {
        this.timerEndHour = ehour;
    }

    public int GetEndHour() {
        return this.timerEndHour;
    }

    public void SetEndMin(int emin) {
        this.timerEndMin = emin;
    }

    public int GetEndMin() {
        return this.timerEndMin;
    }

    public void SetTimerRepeat(int repeat) {
        this.timerRepeat = repeat;
    }

    public int GetTimerRepeat() {
        return this.timerRepeat;
    }

    public void SetTimerStatus(int status) {
        this.timerStatus = status;
    }

    public int GetTimerStatus() {
        return this.timerStatus;
    }

    public String getProgramId() {
        return this.ProgramId;
    }

    public void setProgramId(String programId) {
        this.ProgramId = programId;
    }

    public int getEventId() {
        return this.timerUniqID;
    }

    public void setEventId(int id) {
        this.timerUniqID = id;
    }
}
