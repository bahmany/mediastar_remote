package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.http.HiHttpClient;
import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.message.Action;
import com.hisilicon.multiscreen.protocol.message.Argument;
import com.hisilicon.multiscreen.protocol.message.ArgumentValue;
import com.hisilicon.multiscreen.protocol.message.VImeTextInfo;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.SaxXmlUtil;
import com.hisilicon.multiscreen.protocol.utils.VImeStateMachineDriverMessage;
import java.io.IOException;

/* loaded from: classes.dex */
public class VImeClientTransfer {
    private static final int HTTP_REQUEST_TIMEOUT = 2000;
    private static VImeClientStateMachine mVImeClientStateMachine = null;
    private static VImeClientTransfer mVImeClientTransfer = null;
    private HiHttpClient mHiHttpClient;
    private SaxXmlUtil mSaxVirtualIMEClient;

    public static VImeClientTransfer getInstance(HiDeviceInfo deviceInfo) {
        if (mVImeClientTransfer == null) {
            mVImeClientTransfer = new VImeClientTransfer(deviceInfo);
        } else {
            mVImeClientTransfer.resetHttpClient(deviceInfo.getDeviceIP(), deviceInfo.getService("HI_UPNP_VAR_VIMEDataServerURI").getServicePort());
            mVImeClientStateMachine = VImeClientStateMachine.getInstance();
        }
        return mVImeClientTransfer;
    }

    private VImeClientTransfer(HiDeviceInfo deviceInfo) {
        this.mHiHttpClient = null;
        this.mSaxVirtualIMEClient = null;
        this.mHiHttpClient = new HiHttpClient(deviceInfo.getDeviceIP(), deviceInfo.getService("HI_UPNP_VAR_VIMEDataServerURI").getServicePort(), 2000);
        this.mSaxVirtualIMEClient = new SaxXmlUtil();
        mVImeClientStateMachine = VImeClientStateMachine.getInstance();
    }

    private void resetHttpClient(String ip, int port) {
        this.mHiHttpClient.setHostIp(ip);
        this.mHiHttpClient.setPort(port);
    }

    public Action sendText(int seq, String curText, int startSelection, int endSelection) throws IOException {
        Argument argument = new Argument();
        Action textMessage = new Action();
        ArgumentValue argSeq = new ArgumentValue();
        argSeq.setKey(VImeTextInfo.TEXT_SEQUENCE);
        argSeq.setValue(Integer.valueOf(seq));
        argument.addArgumentValue(argSeq);
        ArgumentValue argText = new ArgumentValue();
        argText.setKey(VImeTextInfo.SRC_TEXT);
        argText.setValue(curText);
        argument.addArgumentValue(argText);
        ArgumentValue argStartSelection = new ArgumentValue();
        argStartSelection.setKey(VImeTextInfo.SRC_START_SELECTION);
        argStartSelection.setValue(Integer.valueOf(startSelection));
        argument.addArgumentValue(argStartSelection);
        ArgumentValue argEndSelection = new ArgumentValue();
        argEndSelection.setKey(VImeTextInfo.SRC_END_SELECTION);
        argEndSelection.setValue(Integer.valueOf(endSelection));
        argument.addArgumentValue(argEndSelection);
        textMessage.setId(Action.ACTION_ID_VIME_SEND_TEXT);
        textMessage.setName("sendText");
        textMessage.setResponseFlag("yes");
        textMessage.setResponseId(Action.ACTION_ID_VIME_RET_SEND_TEXT);
        textMessage.addArgument(argument);
        try {
            String str = this.mSaxVirtualIMEClient.serialize(textMessage);
            this.mHiHttpClient.sendRequest(str);
        } catch (Exception e) {
            LogTool.e("parse string error in sending text.");
        }
        return null;
    }

    public Action endInput(int imeOptionIndex) throws IOException {
        Action endInput_message = getEndInputAction(imeOptionIndex);
        mVImeClientStateMachine.updateStatus(VImeStateMachineDriverMessage.END_INPUT);
        try {
            String str = this.mSaxVirtualIMEClient.serialize(endInput_message);
            this.mHiHttpClient.sendRequest(str);
        } catch (Exception e) {
            LogTool.e("parse input stream error");
        }
        return null;
    }

    private Action getEndInputAction(int imeOption) {
        Action endInput_message = new Action();
        Argument argument = new Argument();
        endInput_message.setId(Action.ACTION_ID_VIME_ENDINPUT_FROM_PHONE);
        endInput_message.setName("endInputFromPhone");
        ArgumentValue argumentValue = new ArgumentValue();
        argumentValue.setKey(VImeTextInfo.INPUT_OPTION);
        argumentValue.setValue(Integer.valueOf(imeOption));
        argument.addArgumentValue(argumentValue);
        endInput_message.addArgument(argument);
        endInput_message.setResponseFlag("yes");
        endInput_message.setResponseId(Action.ACTION_ID_VIME_RET_ENDINPUT_FROM_PHONE);
        return endInput_message;
    }
}
