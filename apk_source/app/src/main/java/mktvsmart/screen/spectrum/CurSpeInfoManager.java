package mktvsmart.screen.spectrum;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.spectrum.bean.DataConvertSpectrumInfo;
import mktvsmart.screen.spectrum.bean.DataConvertSpectrumSetting;

/* loaded from: classes.dex */
public class CurSpeInfoManager {
    private static CurSpeInfoManager mCurSpeInfoManager;
    private DataConvertSpectrumInfo mCurSpectrumInfo;
    private DataConvertSpectrumSetting mCurSpectrumSetting;

    private CurSpeInfoManager() {
    }

    public static CurSpeInfoManager getInstance() {
        if (mCurSpeInfoManager == null) {
            mCurSpeInfoManager = new CurSpeInfoManager();
        }
        return mCurSpeInfoManager;
    }

    public void setCurrentSpectrumInfo(byte[] recvData) {
        DataParser parser = ParserFactory.getParser();
        try {
            InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
            this.mCurSpectrumInfo = (DataConvertSpectrumInfo) parser.parse(istream, 28).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentSpectrumSetting(byte[] recvData) {
        DataParser parser = ParserFactory.getParser();
        try {
            InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
            this.mCurSpectrumSetting = (DataConvertSpectrumSetting) parser.parse(istream, 29).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCurrentStartFre() {
        if (this.mCurSpectrumInfo != null) {
            return this.mCurSpectrumInfo.getStartFre();
        }
        return -1;
    }

    public void setCurrentStartFre(int startFre) {
        if (this.mCurSpectrumInfo != null) {
            this.mCurSpectrumInfo.setStartFre(startFre);
        }
    }

    public int getCurrentEndFre() {
        if (this.mCurSpectrumInfo != null) {
            return this.mCurSpectrumInfo.getEndFre();
        }
        return -1;
    }

    public void setCurrentEndFre(int endFre) {
        if (this.mCurSpectrumInfo != null) {
            this.mCurSpectrumInfo.setEndFre(endFre);
        }
    }

    public int[] getCurrentRetDbuv() {
        if (this.mCurSpectrumInfo != null) {
            return this.mCurSpectrumInfo.getRetDbuv();
        }
        return null;
    }

    public int getCurrentSpeVH() {
        if (this.mCurSpectrumSetting != null) {
            return this.mCurSpectrumSetting.getDvbsSpeV();
        }
        return -1;
    }

    public void setCurrentSpeVH(int speVH) {
        if (this.mCurSpectrumSetting != null) {
            this.mCurSpectrumSetting.setDvbsSpeV(speVH);
        }
    }

    public int getCurrentSpe22kOn() {
        if (this.mCurSpectrumSetting != null) {
            return this.mCurSpectrumSetting.getDvbsSpe22kOn();
        }
        return -1;
    }

    public void setCurrentSpe22kOn(int spe22kOn) {
        if (this.mCurSpectrumSetting != null) {
            this.mCurSpectrumSetting.setDvbsSpe22kOn(spe22kOn);
        }
    }

    public int getCurrentSpeSpan() {
        if (this.mCurSpectrumSetting != null) {
            return this.mCurSpectrumSetting.getDvbsSpeSpan();
        }
        return -1;
    }

    public void setCurrentSpeSpan(int speSpan) {
        if (this.mCurSpectrumSetting != null) {
            this.mCurSpectrumSetting.setDvbsSpeSpan(speSpan);
        }
    }

    public int getCurrentSpeCentFre() {
        if (this.mCurSpectrumSetting != null) {
            return this.mCurSpectrumSetting.getDvbsSpeCentFre();
        }
        return -1;
    }

    public void setCurrentSpeCentFre(int speCentFre) {
        if (this.mCurSpectrumSetting != null) {
            this.mCurSpectrumSetting.setDvbsSpeCentFre(speCentFre);
        }
    }

    public int getCurrentSpeDiseqc() {
        if (this.mCurSpectrumSetting != null) {
            return this.mCurSpectrumSetting.getDvbsSpeDesecq();
        }
        return -1;
    }

    public void setCurrentSpeDisecq(int speDisecq) {
        if (this.mCurSpectrumSetting != null) {
            this.mCurSpectrumSetting.setDvbsSpeDesecq(speDisecq);
        }
    }

    public int getCurrentSpeRef() {
        if (this.mCurSpectrumSetting != null) {
            return this.mCurSpectrumSetting.getDvbsSpeRef();
        }
        return -1;
    }

    public void setCurrentSpeRef(int speRef) {
        if (this.mCurSpectrumSetting != null) {
            this.mCurSpectrumSetting.setDvbsSpeRef(speRef);
        }
    }
}
