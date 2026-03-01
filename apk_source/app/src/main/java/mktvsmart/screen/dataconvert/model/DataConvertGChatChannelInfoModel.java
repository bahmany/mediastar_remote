package mktvsmart.screen.dataconvert.model;

/* loaded from: classes.dex */
public class DataConvertGChatChannelInfoModel {
    private String mEpg;
    private String mSatAngle;
    private int mServiceId;
    private int mTp;

    public void setSatAngle(String satAngle) {
        this.mSatAngle = satAngle;
    }

    public String getSatAngle() {
        return this.mSatAngle;
    }

    public void setTp(int tp) {
        this.mTp = tp;
    }

    public int getTp() {
        return this.mTp;
    }

    public void setServiceId(int serviceId) {
        this.mServiceId = serviceId;
    }

    public int getServiceId() {
        return this.mServiceId;
    }

    public void setEpg(String epg) {
        this.mEpg = epg;
    }

    public String getEpg() {
        return this.mEpg;
    }
}
