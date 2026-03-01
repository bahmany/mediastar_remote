package mktvsmart.screen.spectrum;

import com.google.android.gms.games.GamesStatusCodes;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsSendSocket;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.spectrum.bean.DataConvertSpectrumInfo;
import mktvsmart.screen.spectrum.bean.DataConvertSpectrumSetting;

/* loaded from: classes.dex */
public class SpeRequestManager {
    private static SpeRequestManager mSpectrumRequestManager;
    private CreateSocket mCSocket;
    private DataParser mParser = ParserFactory.getParser();
    private Socket mTcpSocket;

    private SpeRequestManager() throws SocketException {
        try {
            this.mCSocket = new CreateSocket(null, 0);
            this.mTcpSocket = this.mCSocket.GetSocket();
            this.mTcpSocket.setSoTimeout(GamesStatusCodes.STATUS_MILESTONE_CLAIMED_PREVIOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SpeRequestManager getInstance() {
        if (mSpectrumRequestManager == null) {
            mSpectrumRequestManager = new SpeRequestManager();
        }
        return mSpectrumRequestManager;
    }

    public void sendSpeInfoRequest(int osdLen) throws UnsupportedEncodingException {
        ArrayList<DataConvertSpectrumInfo> requsetSpeInfo = new ArrayList<>();
        DataConvertSpectrumInfo requsetSpeInfoItem = new DataConvertSpectrumInfo();
        requsetSpeInfoItem.setOsdLen(osdLen);
        requsetSpeInfo.add(requsetSpeInfoItem);
        byte[] dataBuff = null;
        try {
            dataBuff = this.mParser.serialize(requsetSpeInfo, GlobalConstantValue.GMS_MSG_SPE_REQUEST_SPECTRUM_INFO).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        GsSendSocket.sendSocketToStb(dataBuff, this.mTcpSocket, 0, dataBuff.length, GlobalConstantValue.GMS_MSG_SPE_REQUEST_SPECTRUM_INFO);
    }

    public void sendSpeSettingRequest() throws UnsupportedEncodingException {
        GsSendSocket.sendOnlyCommandSocketToStb(this.mTcpSocket, 302);
    }

    public void sendSetSpeVHRequest(int dvbsSpeVH) throws UnsupportedEncodingException {
        ArrayList<DataConvertSpectrumSetting> requsetSpeSetting = new ArrayList<>();
        DataConvertSpectrumSetting requsetSpeSettingItem = new DataConvertSpectrumSetting();
        requsetSpeSettingItem.setDvbsSpeV(dvbsSpeVH);
        requsetSpeSetting.add(requsetSpeSettingItem);
        byte[] dataBuff = null;
        try {
            dataBuff = this.mParser.serialize(requsetSpeSetting, GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_VH).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        GsSendSocket.sendSocketToStb(dataBuff, this.mTcpSocket, 0, dataBuff.length, GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_VH);
    }

    public void sendSetSpe22kRequset(int dvbsSpe22k) throws UnsupportedEncodingException {
        ArrayList<DataConvertSpectrumSetting> requsetSpeSetting = new ArrayList<>();
        DataConvertSpectrumSetting requsetSpeSettingItem = new DataConvertSpectrumSetting();
        requsetSpeSettingItem.setDvbsSpe22kOn(dvbsSpe22k);
        requsetSpeSetting.add(requsetSpeSettingItem);
        byte[] dataBuff = null;
        try {
            dataBuff = this.mParser.serialize(requsetSpeSetting, GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_22K).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        GsSendSocket.sendSocketToStb(dataBuff, this.mTcpSocket, 0, dataBuff.length, GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_22K);
    }

    public void sendSetSpeDiseqcRequest(int speDiseqc) throws UnsupportedEncodingException {
        ArrayList<DataConvertSpectrumSetting> requsetSpeSetting = new ArrayList<>();
        DataConvertSpectrumSetting requsetSpeSettingItem = new DataConvertSpectrumSetting();
        requsetSpeSettingItem.setDvbsSpeDesecq(speDiseqc);
        requsetSpeSetting.add(requsetSpeSettingItem);
        byte[] dataBuff = null;
        try {
            dataBuff = this.mParser.serialize(requsetSpeSetting, GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_DISEQC).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        GsSendSocket.sendSocketToStb(dataBuff, this.mTcpSocket, 0, dataBuff.length, GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_DISEQC);
    }

    public void sendSetSpeSpanAndCentFreRequest(int speSpan, int cenFre) throws UnsupportedEncodingException {
        ArrayList<DataConvertSpectrumSetting> requsetSpeSetting = new ArrayList<>();
        DataConvertSpectrumSetting requsetSpeSettingItem = new DataConvertSpectrumSetting();
        requsetSpeSettingItem.setDvbsSpeSpan(speSpan);
        requsetSpeSettingItem.setDvbsSpeCentFre(cenFre);
        requsetSpeSetting.add(requsetSpeSettingItem);
        byte[] dataBuff = null;
        try {
            dataBuff = this.mParser.serialize(requsetSpeSetting, GlobalConstantValue.GMS_MSG_SPE_DO_SET_SPAN_AND_CENT_FRE).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        GsSendSocket.sendSocketToStb(dataBuff, this.mTcpSocket, 0, dataBuff.length, GlobalConstantValue.GMS_MSG_SPE_DO_SET_SPAN_AND_CENT_FRE);
    }

    public void sendSetSpeRefRequest(int speRef) throws UnsupportedEncodingException {
        ArrayList<DataConvertSpectrumSetting> requsetSpeSetting = new ArrayList<>();
        DataConvertSpectrumSetting requsetSpeSettingItem = new DataConvertSpectrumSetting();
        requsetSpeSettingItem.setDvbsSpeRef(speRef);
        requsetSpeSetting.add(requsetSpeSettingItem);
        byte[] dataBuff = null;
        try {
            dataBuff = this.mParser.serialize(requsetSpeSetting, GlobalConstantValue.GMS_MSG_SPE_DO_SET_REF).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        GsSendSocket.sendSocketToStb(dataBuff, this.mTcpSocket, 0, dataBuff.length, GlobalConstantValue.GMS_MSG_SPE_DO_SET_REF);
    }
}
