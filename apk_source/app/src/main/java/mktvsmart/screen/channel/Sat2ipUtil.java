package mktvsmart.screen.channel;

import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.Iterator;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertTpModel;

/* loaded from: classes.dex */
public class Sat2ipUtil {
    public static String getRtspUriBase(String ipAddr) {
        return "rtsp:/" + ipAddr + ":554/";
    }

    public static String getRtspUriQuery(DataConvertChannelModel channelModel) throws NumberFormatException {
        int tp_index = Integer.parseInt(ChannelData.GetTpSubStringByPrgoramId(channelModel.GetProgramId()));
        int sat_index = Integer.parseInt(ChannelData.GetSatSubStringByPrgoramId(channelModel.GetProgramId()));
        DataConvertTpModel tpModel = new DataConvertTpModel();
        Iterator<DataConvertTpModel> it = ChannelData.getInstance().getmAllTpList().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            DataConvertTpModel model = it.next();
            if (model.getTpIndex() == tp_index && model.getSatIndex() == sat_index) {
                tpModel.setTpIndex(tp_index);
                tpModel.setSatIndex(sat_index);
                tpModel.setFec(model.getFec());
                tpModel.setFreq(model.getFreq());
                tpModel.setPol(model.getPol());
                tpModel.setSymRate(model.getSymRate());
                break;
            }
        }
        return "?alisatid=" + tpModel.getSatIndex() + "&freq=" + tpModel.getFreq() + "&pol=" + tpModel.getPol() + "&msys=" + (channelModel.getModulationSystem() == 0 ? "dvbs" : "dvbs2") + "&mtype=" + (channelModel.getModulationType() == 0 ? "qpsk" : "8psk") + "&ro=" + (channelModel.getRollOff() / 100.0f) + "&plts=" + (channelModel.getPilotTones() == 0 ? "off" : "on") + "&sr=" + tpModel.getSymRate() + "&fec=" + tpModel.getFec() + "&camode=" + channelModel.GetIsProgramScramble() + "&vpid=" + channelModel.getVideoPid() + "&apid=" + channelModel.getAudioPid() + "&ttxpid=" + channelModel.getTtxPid() + "&subtpid=" + (channelModel.getSubPid() == null ? 0 : channelModel.getSubPid()) + "&pmt=" + channelModel.getPmtPid() + "&prognumber=" + ChannelData.GetProgSubStringByPrgoramId(channelModel.GetProgramId()) + "&pids=" + channelModel.getVideoPid() + ClientInfo.SEPARATOR_BETWEEN_VARS + channelModel.getAudioPid() + ClientInfo.SEPARATOR_BETWEEN_VARS + channelModel.getTtxPid() + ClientInfo.SEPARATOR_BETWEEN_VARS + (channelModel.getSubPid() == null ? 0 : channelModel.getSubPid()) + ClientInfo.SEPARATOR_BETWEEN_VARS + channelModel.getPmtPid();
    }
}
