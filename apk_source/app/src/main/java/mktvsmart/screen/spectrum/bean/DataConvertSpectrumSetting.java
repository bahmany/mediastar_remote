package mktvsmart.screen.spectrum.bean;

/* loaded from: classes.dex */
public class DataConvertSpectrumSetting {
    public static final String DVBS_SPE_22K_ON = "dvbs_spe_22k_on";
    public static final String DVBS_SPE_CENT_FRE = "dvbs_spe_cent_fre";
    public static final String DVBS_SPE_DESECQ = "dvbs_spe_desecq";
    public static final String DVBS_SPE_REF = "dvbs_spe_ref";
    public static final String DVBS_SPE_SPEN = "dvbs_spe_spen";
    public static final String DVBS_SPE_V = "dvbs_spe_v";
    private int mDvbsSpe22kOn;
    private int mDvbsSpeCentFre;
    private int mDvbsSpeDesecq;
    private int mDvbsSpeRef;
    private int mDvbsSpeSpan;
    private int mDvbsSpeV;

    public void setDvbsSpeV(int dvbsSpeV) {
        this.mDvbsSpeV = dvbsSpeV;
    }

    public int getDvbsSpeV() {
        return this.mDvbsSpeV;
    }

    public void setDvbsSpe22kOn(int dvbsSpe22kOn) {
        this.mDvbsSpe22kOn = dvbsSpe22kOn;
    }

    public int getDvbsSpe22kOn() {
        return this.mDvbsSpe22kOn;
    }

    public void setDvbsSpeDesecq(int dvbsSpeDesecq) {
        this.mDvbsSpeDesecq = dvbsSpeDesecq;
    }

    public int getDvbsSpeDesecq() {
        return this.mDvbsSpeDesecq;
    }

    public void setDvbsSpeRef(int dvbsSpeRef) {
        this.mDvbsSpeRef = dvbsSpeRef;
    }

    public int getDvbsSpeRef() {
        return this.mDvbsSpeRef;
    }

    public void setDvbsSpeCentFre(int dvbsSpeCentFre) {
        this.mDvbsSpeCentFre = dvbsSpeCentFre;
    }

    public int getDvbsSpeCentFre() {
        return this.mDvbsSpeCentFre;
    }

    public void setDvbsSpeSpan(int dvbsSpeSpan) {
        this.mDvbsSpeSpan = dvbsSpeSpan;
    }

    public int getDvbsSpeSpan() {
        return this.mDvbsSpeSpan;
    }
}
