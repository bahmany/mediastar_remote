package mktvsmart.screen.spectrum.bean;

/* loaded from: classes.dex */
public class DataConvertSpectrumInfo {
    public static final String END_FRE = "EndFre";
    public static final String OSDLEN = "OsdLen";
    public static final String SPE_INFO_ARRAY = "SpeInfoArray";
    public static final String START_FRE = "StartFre";
    private int mEndFre;
    private int mOsdLen;
    private int[] mRetDbuv;
    private int mStartFre;

    public void setStartFre(int startFre) {
        this.mStartFre = startFre;
    }

    public int getStartFre() {
        return this.mStartFre;
    }

    public void setEndFre(int endFre) {
        this.mEndFre = endFre;
    }

    public int getEndFre() {
        return this.mEndFre;
    }

    public void setOsdLen(int osdLen) {
        this.mOsdLen = osdLen;
    }

    public int getOsdLen() {
        return this.mOsdLen;
    }

    public void setRetDbuv(int[] retDbuv) {
        this.mRetDbuv = retDbuv;
    }

    public int[] getRetDbuv() {
        return this.mRetDbuv;
    }
}
