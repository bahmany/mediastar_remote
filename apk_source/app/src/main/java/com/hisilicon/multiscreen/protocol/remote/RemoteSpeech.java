package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.http.HiHttpClient;
import com.hisilicon.multiscreen.http.HiHttpResponse;
import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.message.Action;
import com.hisilicon.multiscreen.protocol.message.Argument;
import com.hisilicon.multiscreen.protocol.message.ArgumentValue;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.SaxXmlUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.SAXException;

/* loaded from: classes.dex */
public class RemoteSpeech {
    private static final String ENCODE_CHARSET = "UTF-8";
    private static final int HTTP_REQUEST_TIMEOUT = 3000;
    private static final int STB_PUSH_SERVER_PORT = 8867;
    private HiHttpClient mHiHttpClient;
    private String mHostIp;
    private SaxXmlUtil mSaxXmlUtil;

    public RemoteSpeech(HiDeviceInfo deviceInfo) {
        this.mHiHttpClient = null;
        this.mHostIp = "";
        this.mSaxXmlUtil = null;
        if (deviceInfo == null) {
            LogTool.e("device info is null in remote app list.");
            return;
        }
        this.mHostIp = deviceInfo.getDeviceIP();
        this.mHiHttpClient = new HiHttpClient(this.mHostIp, STB_PUSH_SERVER_PORT, 3000);
        this.mSaxXmlUtil = new SaxXmlUtil();
    }

    public void destroy() {
    }

    protected void resetDevice(HiDeviceInfo deviceInfo) {
        this.mHostIp = deviceInfo.getDeviceIP();
        this.mHiHttpClient.setHostIp(this.mHostIp);
    }

    public void sendSpeechInfo(String speechInfo, int type) throws IOException, IllegalArgumentException {
        Action sendSpeechAction = getSendSpeechInfoAction(new Action(), speechInfo, type);
        sendRequestAction(sendSpeechAction);
    }

    public void sendAudioDate(byte[] audiodate, int seq) throws IOException, IllegalArgumentException {
        Action sendAudioAction = getRecordAudioDataAction(new Action(), audiodate, seq);
        sendRequestAction(sendAudioAction);
    }

    public void sendErrorInfo(String error) throws IOException, IllegalArgumentException {
        Action sendErrorAction = getSendErrorInfoAction(new Action(), error);
        sendRequestAction(sendErrorAction);
    }

    public void startSpeak() throws IOException, IllegalArgumentException {
        Action startSpeak = getStartSpeakAction(new Action());
        sendRequestAction(startSpeak);
    }

    public void stopSpeak() throws IOException, IllegalArgumentException {
        Action stopSpeak = getStopSpeakAction(new Action());
        sendRequestAction(stopSpeak);
    }

    private Action getSendSpeechInfoAction(Action action, String speechInfo, int type) {
        Argument argument = new Argument();
        ArgumentValue argumentValue = new ArgumentValue("SpeechInfo", speechInfo);
        argument.addArgumentValue(argumentValue);
        ArgumentValue typeArgValue = new ArgumentValue("ResultType", Integer.valueOf(type));
        argument.addArgumentValue(typeArgValue);
        action.setId(Action.ACTION_ID_SPEECH_TEXT_SEND);
        action.setName("sendSpeechInfo");
        action.setResponseFlag("yes");
        action.setResponseId(Action.ACTION_ID_SPEECH_TEXT_RET_SEND);
        action.addArgument(argument);
        return action;
    }

    private Action getSendErrorInfoAction(Action action, String error) {
        Argument argument = new Argument();
        ArgumentValue argumentValue = new ArgumentValue("SpeechErrorInfo", error);
        argument.addArgumentValue(argumentValue);
        action.setId(Action.ACTION_ID_SPEECH_ERROR);
        action.setName("sendSpeechErrorInfo");
        action.setResponseFlag("yes");
        action.setResponseId(Action.ACTION_ID_SPEECH_ERROR_RET_SEND);
        action.addArgument(argument);
        return action;
    }

    private Action getRecordAudioDataAction(Action action, byte[] audiodate, int seq) {
        Argument argument = new Argument();
        ArgumentValue argumentValue = new ArgumentValue("Audio", audiodate);
        argument.addArgumentValue(argumentValue);
        ArgumentValue SeqArgValue = new ArgumentValue("Seq", Integer.valueOf(seq));
        argument.addArgumentValue(SeqArgValue);
        action.setId(Action.ACTION_ID_SPEECH_TEXT_SEND);
        action.setName("sendAudioData");
        action.setResponseFlag("yes");
        action.setResponseId(Action.ACTION_ID_SPEECH_TEXT_RET_SEND);
        action.addArgument(argument);
        return action;
    }

    private Action getStartSpeakAction(Action action) {
        action.setId(Action.ACTION_ID_START_SPEAKING);
        action.setName("StartSpeaking");
        action.setResponseFlag("yes");
        action.setResponseId(Action.ACTION_ID_RET_START_SPEAKING);
        return action;
    }

    private Action getStopSpeakAction(Action action) {
        action.setId(Action.ACTION_ID_STOP_SPEAKING);
        action.setName("StopSpeaking");
        action.setResponseFlag("yes");
        action.setResponseId(Action.ACTION_ID_RET_STOP_SPEAKING);
        return action;
    }

    private Action sendRequestAction(Action actionMsg) throws IOException, IllegalArgumentException {
        Action responseAction;
        String str = null;
        String responseContent = null;
        try {
            str = this.mSaxXmlUtil.serialize(actionMsg);
        } catch (TransformerConfigurationException e) {
            LogTool.e(e.getMessage());
            return null;
        } catch (SAXException e2) {
            LogTool.e(e2.getMessage());
        }
        HiHttpResponse httpResponse = this.mHiHttpClient.sendRequest(str);
        try {
            String responseContent2 = new String(httpResponse.getMessage(), "UTF-8");
            responseContent = responseContent2;
        } catch (UnsupportedEncodingException e3) {
            LogTool.e("Unsupported Encoding Exception.");
        }
        if (responseContent == null || responseContent.length() == 0) {
            LogTool.e("action response is null.");
            return null;
        }
        try {
            responseAction = this.mSaxXmlUtil.parse(responseContent.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e4) {
            responseAction = null;
            LogTool.e("unsupported encoding exception.");
        } catch (IOException e5) {
            responseAction = null;
            LogTool.e("IO exception.");
        } catch (ParserConfigurationException e6) {
            responseAction = null;
            LogTool.e("parse configuration exception.");
        } catch (SAXException e7) {
            responseAction = null;
            LogTool.e("SAX exception.");
        }
        return responseAction;
    }
}
