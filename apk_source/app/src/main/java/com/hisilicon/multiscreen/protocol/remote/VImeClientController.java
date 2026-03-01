package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.http.HiHttpClient;
import com.hisilicon.multiscreen.http.HiHttpException;
import com.hisilicon.multiscreen.http.HiHttpRecvMsgListener;
import com.hisilicon.multiscreen.http.HiHttpResponse;
import com.hisilicon.multiscreen.http.HiHttpServer;
import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.message.Action;
import com.hisilicon.multiscreen.protocol.message.Argument;
import com.hisilicon.multiscreen.protocol.message.ArgumentValue;
import com.hisilicon.multiscreen.protocol.message.MessageDef;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.SaxXmlUtil;
import com.hisilicon.multiscreen.protocol.utils.VImeStateMachineDriverMessage;
import com.hisilicon.multiscreen.protocol.utils.VImeStatusDefine;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.SAXException;

/* loaded from: classes.dex */
public class VImeClientController {
    private static final String CHECK_VIME_THREAD_NAME = "CheckVimeThread";
    private static final int HTTP_REQUEST_TIMEOUT = 2000;
    public static final long VIME_CHECK_PERIOD = 1000;
    private static VImeClientController mVImeClientController = null;
    private static VImeClientStateMachine mVImeClientStateMachine = null;
    private SaxXmlUtil mSaxXmlUtil;
    private HiHttpClient mHiHttpClient = null;
    private HiHttpServer mHttpServer = null;
    private IVImeActivityHandler mVImeActivityHandler = null;
    private Thread mCheckVImeThread = null;
    private boolean mIsRunning = false;
    private HiHttpRecvMsgListener mHiHttpMsglistener = new HiHttpRecvMsgListener() { // from class: com.hisilicon.multiscreen.protocol.remote.VImeClientController.1
        @Override // com.hisilicon.multiscreen.http.HiHttpRecvMsgListener
        public String onHttpMsgReceived(byte[] arg0, String remoteIP) {
            String response = null;
            try {
                response = VImeClientController.this.vimeMessageHandle(arg0);
            } catch (Exception e) {
                LogTool.e("Vime Message parse exception.");
            }
            if (response == null) {
                return "no_response";
            }
            return response;
        }
    };

    public static VImeClientController getInstance(HiDeviceInfo deviceInfo) {
        if (mVImeClientController == null) {
            mVImeClientController = new VImeClientController(deviceInfo);
        } else {
            mVImeClientController.setHostIp(deviceInfo.getDeviceIP());
            mVImeClientController.setHostPort(mVImeClientController.getVimeServerControlPort(deviceInfo));
            mVImeClientStateMachine = VImeClientStateMachine.getInstance();
        }
        return mVImeClientController;
    }

    VImeClientController(HiDeviceInfo deviceInfo) throws NumberFormatException {
        this.mSaxXmlUtil = null;
        initHttpClient(deviceInfo);
        this.mSaxXmlUtil = new SaxXmlUtil();
        mVImeClientStateMachine = VImeClientStateMachine.getInstance();
    }

    public void setVimeActivityHandler(IVImeActivityHandler handler) {
        this.mVImeActivityHandler = handler;
    }

    public boolean isVImeEnable() {
        return this.mIsRunning;
    }

    public void destroy() throws InterruptedException, IOException {
        stopHttpServer();
        stopCheckThread();
        if (mVImeClientStateMachine != null) {
            mVImeClientStateMachine.deInitVimeClient();
        }
        setInstance(null);
    }

    public boolean isInputStatusOnClient() {
        return mVImeClientStateMachine.getStatus() == VImeStatusDefine.VimeStatus.INPUT_STATUS_VIME_CLIENT;
    }

    public boolean enableVIme() throws InterruptedException, IOException, HiHttpException {
        Action actionMsg = getEnableVImeAction(new Action());
        Action responseAction = sendRequestAction(actionMsg);
        if (responseAction == null) {
            LogTool.v("enable VIme responseAction is null.");
            return false;
        }
        if (responseAction.getId() != 4354) {
            return false;
        }
        VImeStatusDefine.VimeStatus newState = handleActionAndUpdateStateMachine(responseAction);
        if (newState != VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT) {
            this.mVImeActivityHandler.openVimeSwitch();
            startCheckThread();
            startHttpServer();
            LogTool.d("Enable success on phone.");
            return true;
        }
        stopHttpServer();
        stopCheckThread();
        LogTool.d("Enable fail on phone.");
        return false;
    }

    public boolean disableVIme() throws InterruptedException, IOException {
        Action disableAction = getDisableVImeAction(new Action());
        stopHttpServer();
        stopCheckThread();
        mVImeClientStateMachine.updateStatus(VImeStateMachineDriverMessage.CLOSE_VIME);
        Action responseAction = sendRequestAction(disableAction);
        if (responseAction == null) {
            LogTool.e("disable VIme responseAction is null.");
            return false;
        }
        if (responseAction.getId() != 4370) {
            return false;
        }
        if (mVImeClientStateMachine.getStatus() == VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT) {
            LogTool.v("disable VIme success.");
            return true;
        }
        LogTool.e("status is not deinit after disable vime!");
        return false;
    }

    private static void setInstance(VImeClientController control) {
        mVImeClientController = control;
    }

    private Action getEnableVImeAction(Action action) {
        action.setId(Action.ACTION_ID_VIME_ENALBE);
        action.setName("enableVirtualIME");
        action.setResponseFlag("yes");
        action.setResponseId(Action.ACTION_ID_VIME_RET_ENABLE);
        return action;
    }

    private Action getDisableVImeAction(Action action) {
        action.setId(Action.ACTION_ID_VIME_DISABLE);
        action.setName("disableVirtualIME");
        action.setResponseFlag("yes");
        action.setResponseId(Action.ACTION_ID_VIME_RET_DISABLE);
        return action;
    }

    private void initHttpClient(HiDeviceInfo deviceInfo) throws NumberFormatException {
        int port = getVimeServerControlPort(deviceInfo);
        this.mHiHttpClient = new HiHttpClient(deviceInfo.getDeviceIP(), port, 2000);
        this.mHiHttpClient.debugOff();
    }

    private void setHostIp(String ip) {
        this.mHiHttpClient.setHostIp(ip);
    }

    private void setHostPort(int port) {
        this.mHiHttpClient.setPort(port);
    }

    private int getVimeServerControlPort(HiDeviceInfo deviceInfo) throws NumberFormatException {
        int port = Integer.parseInt(MessageDef.VIME_CLIENT_CONTROL_PORT);
        if (deviceInfo != null && deviceInfo.getService("HI_UPNP_VAR_VIMEControlServerURI") != null) {
            return deviceInfo.getService("HI_UPNP_VAR_VIMEControlServerURI").getServicePort();
        }
        LogTool.e("Vime server control service in deviceInfo is null, and init http client by default port " + port);
        return port;
    }

    private void startHttpServer() throws HiHttpException {
        if (this.mHttpServer == null) {
            LogTool.d("Start http server.");
            this.mHttpServer = new HiHttpServer();
            this.mHttpServer.setServerPort(2016);
            this.mHttpServer.setHttpRecvMsgListener(this.mHiHttpMsglistener);
            this.mHttpServer.startServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopHttpServer() throws IOException {
        if (this.mHttpServer != null) {
            this.mHttpServer.stopServer();
            this.mHttpServer = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String vimeMessageHandle(byte[] msg) throws ParserConfigurationException, InterruptedException, SAXException, IOException, IllegalArgumentException {
        ArgumentValue argumentValue;
        ArgumentValue argumentValue2;
        ArgumentValue argumentValue3;
        String responseString;
        Action receiveAction = this.mSaxXmlUtil.parse(msg);
        if (receiveAction == null) {
            LogTool.e("receiveAction is null.");
            return null;
        }
        Action responseAction = new Action();
        switch (receiveAction.getId()) {
            case Action.ACTION_ID_VIME_DISABLE /* 4369 */:
                Argument argument = new Argument();
                responseAction.setId(Action.ACTION_ID_VIME_RET_DISABLE);
                responseAction.setName("retDisableVirtualIME");
                if (handleActionAndUpdateStateMachine(receiveAction) == VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT) {
                    this.mVImeActivityHandler.closeVimeSwitch();
                    stopHttpServer();
                    stopCheckThread();
                    argumentValue = new ArgumentValue("result", 1);
                } else {
                    LogTool.e("VIme can't be disable on client.");
                    argumentValue = new ArgumentValue("result", 0);
                }
                argument.addArgumentValue(argumentValue);
                responseAction.addArgument(argument);
                responseAction.setResponseFlag("no");
                responseAction.setResponseId(0);
                break;
            case Action.ACTION_ID_VIME_CALL_INPUT /* 20481 */:
                Argument argument2 = new Argument();
                responseAction.setId(Action.ACTION_ID_VIME_RET_CALL_INPUT);
                responseAction.setName("retCallInputModule");
                VImeStatusDefine.VimeStatus StatusNow = handleActionAndUpdateStateMachine(receiveAction);
                if (StatusNow == VImeStatusDefine.VimeStatus.INPUT_STATUS_VIME_CLIENT) {
                    this.mVImeActivityHandler.callInput(receiveAction);
                    argumentValue3 = new ArgumentValue("result", 1);
                } else {
                    argumentValue3 = new ArgumentValue("result", 0);
                    LogTool.e("callInput failed! status of VIme statemachine is :" + StatusNow.getName());
                }
                argument2.addArgumentValue(argumentValue3);
                responseAction.addArgument(argument2);
                responseAction.setResponseFlag("no");
                responseAction.setResponseId(0);
                break;
            case Action.ACTION_ID_VIME_ENDINPUT_FROM_STB /* 20529 */:
                Argument argument3 = new Argument();
                responseAction.setId(Action.ACTION_ID_VIME_RET_ENDINPUT_FROM_STB);
                responseAction.setName("retEndInputFromSTB");
                if (handleActionAndUpdateStateMachine(receiveAction) == VImeStatusDefine.VimeStatus.READY_STATUS_VIME_CLIENT) {
                    this.mVImeActivityHandler.endInputByServer();
                    argumentValue2 = new ArgumentValue("result", 1);
                } else {
                    argumentValue2 = new ArgumentValue("result", 0);
                }
                argument3.addArgumentValue(argumentValue2);
                responseAction.addArgument(argument3);
                responseAction.setResponseFlag("no");
                responseAction.setResponseId(0);
                break;
            default:
                responseAction.setId(0);
                responseAction.setName("defaultResponse");
                responseAction.setResponseFlag("no");
                responseAction.setResponseId(0);
                break;
        }
        LogTool.v("receive action name is :" + receiveAction.getName());
        try {
            responseString = this.mSaxXmlUtil.serialize(responseAction);
        } catch (TransformerConfigurationException e) {
            responseString = null;
            LogTool.e("transformer configuration exception.");
        }
        return responseString;
    }

    private void startCheckThread() {
        this.mIsRunning = true;
        mVImeClientStateMachine.updateStatus(VImeStateMachineDriverMessage.CHECK_OK);
        if (this.mCheckVImeThread == null || !this.mCheckVImeThread.isAlive()) {
            this.mCheckVImeThread = new Thread(new CheckSTBVimeRunnable(this, null));
            this.mCheckVImeThread.setName(CHECK_VIME_THREAD_NAME);
            this.mCheckVImeThread.setDaemon(true);
            this.mCheckVImeThread.start();
        }
    }

    private void stopCheckThread() throws InterruptedException {
        this.mIsRunning = false;
        if (this.mCheckVImeThread != null && this.mCheckVImeThread.isAlive()) {
            try {
                this.mCheckVImeThread.join(1000L);
            } catch (InterruptedException e) {
                LogTool.e("InterruptedException when StopCheckVime");
            }
        }
        this.mCheckVImeThread = null;
    }

    private class CheckSTBVimeRunnable implements Runnable {
        private static final short MAX_CHECK_FAIL_TIMES = 6;
        private static final short MAX_CHECK_PHONE_NO_INPUT_TIMES = 3;
        private static final short MAX_CHECK_STB_NO_INPUT_TIMES = 6;
        private static final short MIN_CHECK_STB_FAIL_TIMES = 3;
        private VImeStatusDefine.VimeStatus VImeSTBStatus;
        private short mCheckPhoneNotInputTimes;
        private short mCheckSTBFailTimes;
        private short mCheckSTBNotInputTimes;

        private CheckSTBVimeRunnable() {
            this.mCheckSTBFailTimes = (short) 0;
            this.mCheckSTBNotInputTimes = (short) 0;
            this.mCheckPhoneNotInputTimes = (short) 0;
            this.VImeSTBStatus = null;
        }

        /* synthetic */ CheckSTBVimeRunnable(VImeClientController vImeClientController, CheckSTBVimeRunnable checkSTBVimeRunnable) {
            this();
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException, IOException {
            while (VImeClientController.this.mIsRunning) {
                this.VImeSTBStatus = VImeClientController.this.sendCheckMessage();
                if (this.VImeSTBStatus == VImeStatusDefine.VimeStatus.VIME_SERVER_STATUS_INIT || this.VImeSTBStatus == VImeStatusDefine.VimeStatus.VIME_SERVER_STATUS_READY) {
                    this.mCheckSTBFailTimes = (short) 0;
                    this.mCheckPhoneNotInputTimes = (short) 0;
                    VImeClientController.mVImeClientStateMachine.updateStatus(VImeStateMachineDriverMessage.CHECK_OK);
                    if (VImeClientController.mVImeClientStateMachine.getStatus() == VImeStatusDefine.VimeStatus.INPUT_STATUS_VIME_CLIENT || VImeClientController.this.mVImeActivityHandler.isInputActivityOnTop()) {
                        if (this.mCheckSTBNotInputTimes >= 6) {
                            VImeClientController.this.mVImeActivityHandler.endInputByServer();
                            VImeClientController.mVImeClientStateMachine.updateStatus(VImeStateMachineDriverMessage.END_INPUT);
                            this.mCheckSTBNotInputTimes = (short) 0;
                            LogTool.d("End input on client because we find STB is not in input status a few time.");
                        } else {
                            this.mCheckSTBNotInputTimes = (short) (this.mCheckSTBNotInputTimes + 1);
                        }
                    } else {
                        this.mCheckSTBNotInputTimes = (short) 0;
                    }
                } else if (this.VImeSTBStatus == VImeStatusDefine.VimeStatus.VIME_SERVER_STATUS_INPUT) {
                    this.mCheckSTBFailTimes = (short) 0;
                    this.mCheckSTBNotInputTimes = (short) 0;
                    VImeClientController.mVImeClientStateMachine.updateStatus(VImeStateMachineDriverMessage.CHECK_OK);
                    if (VImeClientController.mVImeClientStateMachine.getStatus() == VImeStatusDefine.VimeStatus.INPUT_STATUS_VIME_CLIENT && !VImeClientController.this.mVImeActivityHandler.isInputActivityOnTop()) {
                        if (this.mCheckPhoneNotInputTimes >= 3) {
                            VImeClientController.this.mVImeActivityHandler.endInputByServer();
                            VImeClientController.mVImeClientStateMachine.updateStatus(VImeStateMachineDriverMessage.END_INPUT);
                            this.mCheckPhoneNotInputTimes = (short) 0;
                        } else {
                            this.mCheckPhoneNotInputTimes = (short) (this.mCheckPhoneNotInputTimes + 1);
                        }
                    } else {
                        this.mCheckPhoneNotInputTimes = (short) 0;
                    }
                } else {
                    this.mCheckSTBFailTimes = (short) (this.mCheckSTBFailTimes + 1);
                    if (this.mCheckSTBFailTimes == 3) {
                        if (VImeClientController.mVImeClientStateMachine.getStatus() == VImeStatusDefine.VimeStatus.INPUT_STATUS_VIME_CLIENT) {
                            VImeClientController.this.mVImeActivityHandler.endInputBySelf();
                            LogTool.d("check fail 3 times, activity handler endInput by self.");
                        }
                        VImeClientController.mVImeClientStateMachine.updateStatus(VImeStateMachineDriverMessage.CHECK_FAIL);
                    } else if (this.mCheckSTBFailTimes > 6) {
                        VImeClientController.mVImeClientStateMachine.updateStatus(VImeStateMachineDriverMessage.CHECK_FAIL);
                        VImeClientController.this.mVImeActivityHandler.closeVimeSwitch();
                        VImeClientController.this.mIsRunning = false;
                        VImeClientController.this.stopHttpServer();
                    }
                }
                if (VImeClientController.this.mIsRunning) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        LogTool.e("Interrupted Exception");
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public VImeStatusDefine.VimeStatus sendCheckMessage() throws IOException {
        ArgumentValue argValueStatus = new ArgumentValue();
        Argument argument = new Argument();
        Action actionMsg = new Action();
        argValueStatus.setKey("status");
        argValueStatus.setValue(Integer.valueOf(mVImeClientStateMachine.getStatusIndex()));
        argument.addArgumentValue(argValueStatus);
        actionMsg.setId(Action.ACTION_ID_VIME_CHECK);
        actionMsg.setName("checkStatus");
        actionMsg.setResponseFlag("yes");
        actionMsg.setResponseId(Action.ACTION_ID_VIME_RET_CHECK);
        actionMsg.addArgument(argument);
        Action responseAction = sendRequestAction(actionMsg);
        if (responseAction != null && responseAction.getId() == 4386) {
            int serverState = ((Integer) responseAction.getArgument(0).getArgumentValue(1).getVaule()).intValue();
            return VImeStatusDefine.VimeStatus.getStatus(serverState);
        }
        LogTool.d("check response error.");
        return null;
    }

    private Action sendRequestAction(Action actionMsg) throws IOException {
        Action responseAction;
        try {
            String str = this.mSaxXmlUtil.serialize(actionMsg);
            HiHttpResponse httpResponse = this.mHiHttpClient.sendRequest(str);
            String responseContent = httpResponse.getResponseMessage();
            if (responseContent == null || responseContent.length() == 0) {
                LogTool.d("action response is null.");
                return null;
            }
            try {
                responseAction = this.mSaxXmlUtil.parse(responseContent.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                responseAction = null;
                LogTool.e("unsupported encoding exception.");
            } catch (IOException e2) {
                responseAction = null;
                LogTool.e("IO exception.");
            } catch (ParserConfigurationException e3) {
                responseAction = null;
                LogTool.e("parse configuration exception.");
            } catch (SAXException e4) {
                responseAction = null;
                LogTool.e("SAX exception.");
            }
            return responseAction;
        } catch (Exception e5) {
            LogTool.e("serialize exception");
            return null;
        }
    }

    private VImeStatusDefine.VimeStatus handleActionAndUpdateStateMachine(Action action) {
        if (action == null) {
            return null;
        }
        VImeStateMachineDriverMessage stateDriverMsg = null;
        switch (action.getId()) {
            case Action.ACTION_ID_VIME_RET_ENABLE /* 4354 */:
                stateDriverMsg = VImeStateMachineDriverMessage.OPEN_VIME;
                break;
            case Action.ACTION_ID_VIME_DISABLE /* 4369 */:
                stateDriverMsg = VImeStateMachineDriverMessage.CLOSE_VIME;
                break;
            case Action.ACTION_ID_VIME_RET_DISABLE /* 4370 */:
                stateDriverMsg = VImeStateMachineDriverMessage.CLOSE_VIME;
                LogTool.v("receive action vime ret disable");
                break;
            case Action.ACTION_ID_VIME_CALL_INPUT /* 20481 */:
                stateDriverMsg = VImeStateMachineDriverMessage.CALL_INPUT;
                break;
            case Action.ACTION_ID_VIME_ENDINPUT_FROM_STB /* 20529 */:
                stateDriverMsg = VImeStateMachineDriverMessage.END_INPUT;
                break;
            default:
                LogTool.e("Action id is null");
                break;
        }
        return mVImeClientStateMachine.updateStatus(stateDriverMsg);
    }
}
