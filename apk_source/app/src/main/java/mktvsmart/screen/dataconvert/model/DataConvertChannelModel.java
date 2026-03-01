package mktvsmart.screen.dataconvert.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DataConvertChannelModel implements Serializable {
    private static final long serialVersionUID = 1831321093741944842L;
    private int FavMark;
    private int LockMark;
    private String ProgramId;
    private int ProgramIndex;
    private String ProgramName;
    private String SatName;
    private String audioPid;
    private int channelType;
    private int fec;
    private int freq;
    private int haveEPG;
    private int isPlaying;
    private int isProgramHd;
    private int isProgramScramble;
    private short isTuner2;
    private int mCurrentChannelListDispIndex;
    private int[] mMatchChannelNameIndexArray;
    private int mSearchChannelSortPriority;
    private int mWillBePlayed;
    private int modulationSystem;
    private int modulationType;
    private String moveToPosition;
    private int pilotTones;
    private int pmtPid;
    private char pol;
    private int rollOff;
    private String subPid;
    private int symRate;
    private int ttxPid;
    private int videoPid;
    private boolean isSelected = false;
    public List<Integer> mfavGroupIDs = new ArrayList();

    public int getSearchChannelSortPriority() {
        return this.mSearchChannelSortPriority;
    }

    public void setSearchChannelSortPriority(int priority) {
        this.mSearchChannelSortPriority = priority;
    }

    public int getCurrentChannelListDispIndex() {
        return this.mCurrentChannelListDispIndex;
    }

    public void setCurrentChannelListDispIndex(int dispIndex) {
        this.mCurrentChannelListDispIndex = dispIndex;
    }

    public int[] getMatchChannelNameIndexArray() {
        return this.mMatchChannelNameIndexArray;
    }

    public void setMatchChannelNameIndexArray(int[] matchIndexArray) {
        this.mMatchChannelNameIndexArray = matchIndexArray;
    }

    public int getChannelTpye() {
        return this.channelType;
    }

    public void setChannelTpye(int type) {
        this.channelType = type;
    }

    public int getLockMark() {
        return this.LockMark;
    }

    public void setLockMark(int mark) {
        this.LockMark = mark;
    }

    public boolean getSelectedFlag() {
        return this.isSelected;
    }

    public void setSelectedFlag(boolean select) {
        this.isSelected = select;
    }

    public String GetProgramId() {
        return this.ProgramId;
    }

    public void SetProgramId(String id) {
        this.ProgramId = id;
    }

    public String getProgramName() {
        return this.ProgramName;
    }

    public void setProgramName(String name) {
        this.ProgramName = name;
    }

    public String GetSatName() {
        return this.SatName;
    }

    public void SetSatName(String name) {
        this.SatName = name;
    }

    public int GetProgramIndex() {
        return this.ProgramIndex;
    }

    public void SetProgramIndex(int index) {
        this.ProgramIndex = index;
    }

    public int GetIsProgramScramble() {
        return this.isProgramScramble;
    }

    public void SetIsProgramScramble(int type) {
        this.isProgramScramble = type;
    }

    public int GetFavMark() {
        return this.FavMark;
    }

    public void SetFavMark(int fav) {
        this.FavMark = fav;
    }

    public int GetHaveEPG() {
        return this.haveEPG;
    }

    public void SetHaveEPG(int haveEPG) {
        this.haveEPG = haveEPG;
    }

    public String getMoveToPosition() {
        return this.moveToPosition;
    }

    public void setMoveToPosition(String moveToPosition) {
        this.moveToPosition = moveToPosition;
    }

    public int getIsPlaying() {
        return this.isPlaying;
    }

    public void setIsPlaying(int flag) {
        this.isPlaying = flag;
    }

    public int getmWillBePlayed() {
        return this.mWillBePlayed;
    }

    public void setmWillBePlayed(int mWillBePlayed) {
        this.mWillBePlayed = mWillBePlayed;
    }

    public int getIsProgramHd() {
        return this.isProgramHd;
    }

    public void setIsProgramHd(int isProgramHd) {
        this.isProgramHd = isProgramHd;
    }

    public int getFreq() {
        return this.freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public char getPol() {
        return this.pol;
    }

    public void setPol(char pol) {
        this.pol = pol;
    }

    public int getModulationSystem() {
        return this.modulationSystem;
    }

    public void setModulationSystem(int modulationSystem) {
        this.modulationSystem = modulationSystem;
    }

    public int getModulationType() {
        return this.modulationType;
    }

    public void setModulationType(int modulationType) {
        this.modulationType = modulationType;
    }

    public int getRollOff() {
        return this.rollOff;
    }

    public void setRollOff(int rollOff) {
        this.rollOff = rollOff;
    }

    public int getPilotTones() {
        return this.pilotTones;
    }

    public void setPilotTones(int pilotTones) {
        this.pilotTones = pilotTones;
    }

    public int getSymRate() {
        return this.symRate;
    }

    public void setSymRate(int symRate) {
        this.symRate = symRate;
    }

    public int getFec() {
        return this.fec;
    }

    public void setFec(int fec) {
        this.fec = fec;
    }

    public int getVideoPid() {
        return this.videoPid;
    }

    public void setVideoPid(int videoPid) {
        this.videoPid = videoPid;
    }

    public String getAudioPid() {
        return this.audioPid;
    }

    public void setAudioPid(String audioPid) {
        this.audioPid = audioPid;
    }

    public int getTtxPid() {
        return this.ttxPid;
    }

    public void setTtxPid(int ttxPid) {
        this.ttxPid = ttxPid;
    }

    public String getSubPid() {
        return this.subPid;
    }

    public void setSubPid(String subPid) {
        this.subPid = subPid;
    }

    public int getPmtPid() {
        return this.pmtPid;
    }

    public void setPmtPid(int pmtPid) {
        this.pmtPid = pmtPid;
    }

    public short getIsTuner2() {
        return this.isTuner2;
    }

    public void setIsTuner2(short isTuner2) {
        this.isTuner2 = isTuner2;
    }
}
