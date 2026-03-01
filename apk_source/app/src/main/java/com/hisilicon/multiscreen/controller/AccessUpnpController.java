package com.hisilicon.multiscreen.controller;

import android.os.Build;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.hisilicon.multiscreen.controller.IAccessListener;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.upnputils.MultiScreenUpnpControlPoint;
import com.hisilicon.multiscreen.upnputils.UpnpMultiScreenDeviceInfo;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;

/* loaded from: classes.dex */
public class AccessUpnpController {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$multiscreen$controller$IAccessListener$Caller = null;
    private static final String ACCESS_PING_THREAD_NAME = "AccessPingThread";
    private static final int DEFAULT_PING_TIME = 3000;
    private static final int JOIN_THREAD_KEEP_ALIVE_TIMEOUT = 2000;
    private static final int JOIN_THREAD_PING_TIMEOUT = 1500;
    private static final int JOIN_THREAD_REACCESS_TIMEOUT = 2000;
    private static final int KEEP_ALIVE_PERIOD = 2000;
    private static final String KEEP_ALIVE_THREAD_NAME = "AccessKeepAliveThread";
    private static final int MAX_PING_FAIL_COUNT = 15;
    private static final int PING_FAIL_COUNT_OF_REACCESS = 5;
    private static final int PING_FAIL_COUNT_OF_WARNING = 5;
    private static final int PING_PERIOD = 1500;
    private static final int PING_REMOTE_ID_CAN_ACCESS = 0;
    private static final int PING_REMOTE_ID_MANUAL_OFF = -2;
    private static final int PING_REMOTE_ID_OTHERS = -1;
    private static final int PING_REMOTE_ID_SUSPEND = -3;
    private static final int PING_TIME_OUT = 30000;
    private static final int REACCESS_PERIOD = 2000;
    private static final String REACCESS_THREAD_NAME = "ReAccessThread";
    private static final int TIMEOUT_PING_CONNECT = 10000;
    private static final int TIMEOUT_PING_READ = 5000;
    private static final int TIMEOUT_REACCESS = 5000;
    private MultiScreenUpnpControlPoint mControlPoint;
    private MultiScreenControlService mMultiScreenControlService;
    private PingTime mPingTime;
    private IAccessListener mAccessListener = null;
    private int mPingFailCount = 0;
    private boolean mIsKeepAliveRunning = false;
    private Thread mKeepAliveThread = null;
    private boolean mIsPingRunning = false;
    private Thread mAccessPingThread = null;
    private boolean mIsReAccessRunning = false;
    private Thread mReAccessThread = null;

    static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$multiscreen$controller$IAccessListener$Caller() {
        int[] iArr = $SWITCH_TABLE$com$hisilicon$multiscreen$controller$IAccessListener$Caller;
        if (iArr == null) {
            iArr = new int[IAccessListener.Caller.valuesCustom().length];
            try {
                iArr[IAccessListener.Caller.AccessPing.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[IAccessListener.Caller.KeepAlive.ordinal()] = 3;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[IAccessListener.Caller.Others.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[IAccessListener.Caller.ReAccess.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            $SWITCH_TABLE$com$hisilicon$multiscreen$controller$IAccessListener$Caller = iArr;
        }
        return iArr;
    }

    private class PingTime {
        private boolean mIsValid;
        private long mPingStartTime;
        private int mPingTimeCost;

        public PingTime() {
            this.mPingStartTime = 0L;
            this.mPingTimeCost = 3000;
            this.mIsValid = true;
            this.mPingStartTime = 0L;
            this.mPingTimeCost = 3000;
            this.mIsValid = true;
        }

        protected void startup() {
            this.mPingStartTime = System.currentTimeMillis();
        }

        protected int getStartTime() {
            return (int) (System.currentTimeMillis() - this.mPingStartTime);
        }

        protected synchronized void setPingTimeCost(int timeCost) {
            this.mIsValid = true;
            this.mPingTimeCost = timeCost;
        }

        protected int getPingTimeCost() {
            if (!this.mIsValid) {
                return AccessUpnpController.PING_TIME_OUT;
            }
            this.mIsValid = false;
            return this.mPingTimeCost;
        }

        protected int getLatestPingTimeCost() {
            return this.mPingTimeCost;
        }
    }

    private class PingRunnable implements Runnable {
        private String mRemoteID;
        private int mTimeCost = 0;

        public PingRunnable() {
            this.mRemoteID = null;
            if (AccessUpnpController.this.mPingTime == null) {
                AccessUpnpController.this.mPingTime = AccessUpnpController.this.new PingTime();
            }
            AccessUpnpController.this.mPingTime.setPingTimeCost(3000);
            this.mRemoteID = AccessUpnpController.this.mControlPoint.getRemoteId();
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            while (AccessUpnpController.this.mIsPingRunning) {
                this.mTimeCost = AccessUpnpController.this.accessPing(this.mRemoteID);
                AccessUpnpController.this.mPingTime.setPingTimeCost(this.mTimeCost);
                if (this.mTimeCost < AccessUpnpController.PING_TIME_OUT && this.mTimeCost > 0) {
                    AccessUpnpController.this.mPingFailCount = 0;
                }
                if (AccessUpnpController.this.mIsPingRunning) {
                    AccessUpnpController.this.ThreadSleep(1500L);
                }
            }
        }
    }

    private class ReAccessRunnable implements Runnable {
        private String mRemoteID;

        public ReAccessRunnable() {
            this.mRemoteID = null;
            this.mRemoteID = AccessUpnpController.this.mControlPoint.getRemoteId();
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            while (AccessUpnpController.this.mIsReAccessRunning) {
                if (!AccessUpnpController.this.isKeepAliveStateOK()) {
                    if (AccessUpnpController.this.mIsReAccessRunning && AccessUpnpController.this.reAccess(this.mRemoteID, FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS, FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS)) {
                        AccessUpnpController.this.setKeepAliveStateOK();
                        AccessUpnpController.this.renewState();
                    }
                    if (AccessUpnpController.this.mIsReAccessRunning && !AccessUpnpController.this.isKeepAliveStateOK()) {
                        AccessUpnpController.this.mMultiScreenControlService.getDeviceDiscover().msearch();
                    }
                }
                if (AccessUpnpController.this.mIsReAccessRunning) {
                    AccessUpnpController.this.ThreadSleep(2000L);
                }
            }
        }
    }

    private class KeepAliveRunnable implements Runnable {
        private int mTempPingTimeCost = 3000;

        public KeepAliveRunnable() {
            AccessUpnpController.this.mPingFailCount = 0;
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            while (AccessUpnpController.this.mIsKeepAliveRunning) {
                this.mTempPingTimeCost = AccessUpnpController.this.mPingTime.getPingTimeCost();
                if (isPingException(this.mTempPingTimeCost)) {
                    LogTool.d("Ping time exception is being dealt.");
                    AccessUpnpController.this.mIsPingRunning = false;
                    AccessUpnpController.this.mIsReAccessRunning = false;
                    AccessUpnpController.this.mIsKeepAliveRunning = false;
                    return;
                }
                if (this.mTempPingTimeCost < AccessUpnpController.PING_TIME_OUT && this.mTempPingTimeCost > 0) {
                    AccessUpnpController.this.mPingFailCount = 0;
                } else {
                    AccessUpnpController.this.mPingFailCount++;
                }
                if (!isNetworkLost(AccessUpnpController.this.mPingFailCount)) {
                    if (AccessUpnpController.this.mIsKeepAliveRunning) {
                        AccessUpnpController.this.ThreadSleep(2000L);
                    }
                } else {
                    AccessUpnpController.this.mIsPingRunning = false;
                    AccessUpnpController.this.mIsReAccessRunning = false;
                    AccessUpnpController.this.mIsKeepAliveRunning = false;
                    return;
                }
            }
        }

        private boolean isPingException(int pingTime) {
            switch (pingTime) {
                case -3:
                    AccessUpnpController.this.mMultiScreenControlService.setState(MultiScreenControlService.ClientState.STB_SUSPEND);
                    AccessUpnpController.this.dealSTBSuspend(IAccessListener.Caller.KeepAlive);
                    break;
                case -2:
                    AccessUpnpController.this.mMultiScreenControlService.setState(MultiScreenControlService.ClientState.STB_LEAVE);
                    AccessUpnpController.this.dealSTBLeave(IAccessListener.Caller.KeepAlive);
                    break;
                case -1:
                    AccessUpnpController.this.mMultiScreenControlService.setState(MultiScreenControlService.ClientState.REAVED);
                    AccessUpnpController.this.dealReaved(IAccessListener.Caller.KeepAlive);
                    break;
            }
            return true;
        }

        private boolean isNetworkLost(int failedCount) {
            if (failedCount != 10) {
                if (failedCount >= 15) {
                    AccessUpnpController.this.mMultiScreenControlService.setState(MultiScreenControlService.ClientState.NETWORK_LOST);
                    AccessUpnpController.this.dealNetworkLost(IAccessListener.Caller.KeepAlive);
                    return true;
                }
                return false;
            }
            AccessUpnpController.this.dealNetworkNotWell();
            return false;
        }
    }

    public AccessUpnpController() {
        this.mControlPoint = null;
        this.mMultiScreenControlService = null;
        this.mPingTime = null;
        this.mMultiScreenControlService = MultiScreenControlService.getInstance();
        this.mControlPoint = this.mMultiScreenControlService.getControlPoint();
        this.mPingTime = new PingTime();
    }

    public void reset() {
        this.mMultiScreenControlService = MultiScreenControlService.getInstance();
        this.mControlPoint = this.mMultiScreenControlService.getControlPoint();
        if (this.mPingTime == null) {
            this.mPingTime = new PingTime();
        }
    }

    public void setListener(IAccessListener accessMsgListener) {
        this.mAccessListener = accessMsgListener;
    }

    public boolean accessHello(String remoteID, String localIP, int timeoutConnect, int timeoutRead) {
        boolean isSuccess = false;
        Action helloAct = initHello(remoteID, localIP);
        if (helloAct == null) {
            LogTool.e("Init Hello action failed!");
        } else {
            ArgumentList outArgList = sendHello(helloAct, timeoutConnect, timeoutRead);
            isSuccess = checkHello(remoteID, outArgList);
            if (isSuccess) {
                setVideoType(outArgList);
            }
        }
        return isSuccess;
    }

    public boolean accessByebye() {
        Action byebyeAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_ACCESS_TYPE, UpnpMultiScreenDeviceInfo.ACTION_ACCESS_BYEBYE);
        if (byebyeAct == null) {
            LogTool.e("Action not found");
            return false;
        }
        byebyeAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_REMOTE_ID, this.mControlPoint.getRemoteId());
        return byebyeAct.postControlAction();
    }

    public void startAccessPingTask() {
        if (this.mIsPingRunning || this.mIsReAccessRunning || this.mIsKeepAliveRunning) {
            stopAccessPingTask();
            LogTool.d("restart AccessPingTask");
        }
        startPing();
        startReAccessThread();
        startKeepAlive();
    }

    public void stopAccessPingTask(IAccessListener.Caller caller) {
        this.mIsPingRunning = false;
        this.mIsReAccessRunning = false;
        this.mIsKeepAliveRunning = false;
        if (caller == null) {
            caller = IAccessListener.Caller.Others;
            LogTool.d("Caller of IAccessListener is null, you should check it.");
        }
        switch ($SWITCH_TABLE$com$hisilicon$multiscreen$controller$IAccessListener$Caller()[caller.ordinal()]) {
            case 1:
                stopReAccessThread();
                stopKeepAlive();
                break;
            case 2:
                stopPing();
                stopKeepAlive();
                break;
            case 3:
                stopPing();
                stopReAccessThread();
                break;
            default:
                stopPing();
                stopReAccessThread();
                stopKeepAlive();
                break;
        }
    }

    private Action initHello(String remoteID, String localIP) {
        if (remoteID != null && localIP != null) {
            Action helloAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_ACCESS_TYPE, UpnpMultiScreenDeviceInfo.ACTION_ACCESS_HELLO);
            if (helloAct != null) {
                helloAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_REMOTE_ID, remoteID);
                helloAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_REMOTE_IP, localIP);
                helloAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_CLIENT_VERSION, ClientInfo.CLIENT_VERSION);
                helloAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_HANDSET_DEVICEINFO, Build.MODEL);
                helloAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_HANDSET_SDK_VERSION, Build.VERSION.SDK_INT);
                LogTool.d("handset info：" + Build.MODEL + ", sdk version:" + Build.VERSION.SDK_INT);
                LogTool.d("Access hello input remote id = " + remoteID);
                LogTool.d("Access hello input remote IP = " + localIP);
                return helloAct;
            }
            return helloAct;
        }
        LogTool.e("remoteID or localIP is null and hello action initialize failed.");
        return null;
    }

    private ArgumentList sendHello(Action helloAct, int timeoutConnect, int timeoutRead) {
        if (helloAct != null && helloAct.postControlAction(timeoutConnect, timeoutRead)) {
            ArgumentList outArgList = helloAct.getOutputArgumentList();
            return outArgList;
        }
        LogTool.e("Send Hello action fail.");
        return null;
    }

    private boolean checkHello(String remoteID, ArgumentList outArgList) {
        boolean isOK = false;
        if (outArgList == null) {
            LogTool.e("outArgList of Hello action is null.");
        } else {
            Argument argRemoteID = outArgList.getArgument(UpnpMultiScreenDeviceInfo.ARG_CURRENT_REMOTE_ID);
            if (argRemoteID != null) {
                String currentID = argRemoteID.getValue();
                if (remoteID.equals(currentID)) {
                    isOK = true;
                }
                LogTool.d("Access hello return = " + currentID);
            }
        }
        return isOK;
    }

    private void setVideoType(ArgumentList outArgList) {
        String videoType = MultiScreenControlService.VIDEO_JPEG_TYPE;
        if (outArgList != null) {
            Argument ArgVideoType = outArgList.getArgument(UpnpMultiScreenDeviceInfo.ARG_SUPPORT_VIDEO);
            if (ArgVideoType == null) {
                LogTool.i("Hello action does not return the type of video supported in server.");
            } else {
                videoType = ArgVideoType.getValue();
            }
        }
        LogTool.i("Set the type of video supported: " + videoType);
        this.mMultiScreenControlService.setSupportVideoType(videoType);
    }

    private void stopAccessPingTask() {
        stopAccessPingTask(IAccessListener.Caller.Others);
    }

    private void startPing() {
        this.mIsPingRunning = true;
        this.mPingTime.startup();
        this.mAccessPingThread = new Thread(new PingRunnable());
        this.mAccessPingThread.setName(ACCESS_PING_THREAD_NAME);
        this.mAccessPingThread.setDaemon(true);
        this.mAccessPingThread.start();
    }

    private void stopPing() throws InterruptedException {
        this.mIsPingRunning = false;
        if (this.mAccessPingThread != null && this.mAccessPingThread.isAlive()) {
            try {
                this.mAccessPingThread.join(1500L);
            } catch (InterruptedException e) {
                LogTool.e(e.getMessage());
            }
        }
        this.mAccessPingThread = null;
    }

    private void startKeepAlive() {
        this.mIsKeepAliveRunning = true;
        this.mKeepAliveThread = new Thread(new KeepAliveRunnable());
        this.mKeepAliveThread.setName(KEEP_ALIVE_THREAD_NAME);
        this.mKeepAliveThread.setDaemon(true);
        this.mKeepAliveThread.start();
    }

    private void stopKeepAlive() throws InterruptedException {
        this.mIsKeepAliveRunning = false;
        if (this.mKeepAliveThread != null && this.mKeepAliveThread.isAlive()) {
            try {
                this.mKeepAliveThread.join(2000L);
            } catch (InterruptedException e) {
                LogTool.e(e.getMessage());
            }
        }
        this.mKeepAliveThread = null;
    }

    private void startReAccessThread() {
        this.mIsReAccessRunning = true;
        this.mReAccessThread = new Thread(new ReAccessRunnable());
        this.mReAccessThread.setName(REACCESS_THREAD_NAME);
        this.mReAccessThread.setDaemon(true);
        this.mReAccessThread.start();
    }

    private void stopReAccessThread() throws InterruptedException {
        this.mIsReAccessRunning = false;
        if (this.mReAccessThread != null && this.mReAccessThread.isAlive()) {
            try {
                this.mReAccessThread.join(2000L);
            } catch (InterruptedException e) {
                LogTool.e(e.getMessage());
            }
        }
        this.mReAccessThread = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int accessPing(String RemoteID) {
        int timeCost = PING_TIME_OUT;
        Action pingAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_ACCESS_TYPE, UpnpMultiScreenDeviceInfo.ACTION_ACCESS_PING);
        if (pingAct == null) {
            LogTool.e("Action not found");
            return PING_TIME_OUT;
        }
        pingAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_REMOTE_ID, RemoteID);
        pingAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_PING_TIME, this.mPingTime.getStartTime());
        if (!pingAct.postControlAction(TIMEOUT_PING_CONNECT, FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS)) {
            LogTool.e("Post ping action fail");
        } else {
            ArgumentList outArgList = pingAct.getOutputArgumentList();
            String remoteIdSTB = outArgList.getArgument(0).getValue();
            if (RemoteID.equals(remoteIdSTB)) {
                timeCost = this.mPingTime.getStartTime() - Integer.parseInt(outArgList.getArgument(1).getValue());
            } else if (MultiScreenUpnpControlPoint.REMOTE_ID_CAN_ACCESS.equals(remoteIdSTB)) {
                LogTool.d("remote_id of STB is can_access.");
                timeCost = 0;
            } else if (MultiScreenUpnpControlPoint.REMOTE_ID_STB_MANUAL_OFF.equals(remoteIdSTB)) {
                LogTool.d("STB is manual off.");
                this.mMultiScreenControlService.setState(MultiScreenControlService.ClientState.STB_LEAVE);
                this.mIsPingRunning = false;
                dealSTBLeave(IAccessListener.Caller.AccessPing);
                timeCost = -2;
            } else if (MultiScreenUpnpControlPoint.REMOTE_ID_DEVICE_SUSPEND.equals(remoteIdSTB)) {
                this.mMultiScreenControlService.setState(MultiScreenControlService.ClientState.STB_SUSPEND);
                this.mIsPingRunning = false;
                dealSTBSuspend(IAccessListener.Caller.AccessPing);
                timeCost = -3;
            } else {
                LogTool.e("Be reaved by " + outArgList.getArgument(0).getValue());
                this.mMultiScreenControlService.setState(MultiScreenControlService.ClientState.REAVED);
                this.mIsPingRunning = false;
                dealReaved(IAccessListener.Caller.AccessPing);
                timeCost = -1;
            }
        }
        return timeCost;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean reAccess(String remoteID, int timeoutConnect, int timeoutRead) {
        String localIP = this.mMultiScreenControlService.getLastestIP();
        Device currentDevice = this.mControlPoint.getCurrentDevice();
        if (currentDevice == null) {
            LogTool.e("Current device is null, fail to reAccessSTB.");
            return false;
        }
        Device newDevice = this.mMultiScreenControlService.getDeviceDiscover().getDeviceByUUID(currentDevice.getUDN());
        if (newDevice == null) {
            LogTool.e("ReAccess device is not exist:" + currentDevice.getUDN());
            return false;
        }
        LogTool.d("ReAccess device name: " + newDevice.getFriendlyName());
        this.mMultiScreenControlService.getControlPoint().setCurrentDevice(newDevice);
        boolean isOK = accessHello(remoteID, localIP, timeoutConnect, timeoutRead);
        if (isOK) {
            LogTool.d("ReAccess successfully.");
        } else {
            LogTool.e("Fail to reAccess.");
        }
        return isOK;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean renewState() {
        return this.mMultiScreenControlService.renewState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isKeepAliveStateOK() {
        int tempPingTimeCost = this.mPingTime.getLatestPingTimeCost();
        if ((tempPingTimeCost > 0 && tempPingTimeCost < PING_TIME_OUT) || this.mPingFailCount < 5) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setKeepAliveStateOK() {
        this.mPingFailCount = 0;
        this.mPingTime.setPingTimeCost(3000);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dealReaved(IAccessListener.Caller caller) {
        if (this.mAccessListener == null) {
            LogTool.e("Access listener is null, cannot deal event: Reaved.");
        } else {
            this.mAccessListener.dealReaveEvent(caller);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dealSTBLeave(IAccessListener.Caller caller) {
        if (this.mAccessListener == null) {
            LogTool.e("Access listener is null, cannot deal event: STBLeave.");
        } else {
            this.mAccessListener.dealSTBLeaveEvent(caller);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dealSTBSuspend(IAccessListener.Caller caller) {
        if (this.mAccessListener == null) {
            LogTool.e("Access listener is null, cannot deal event: STBSuspend.");
        } else {
            this.mAccessListener.dealSTBSuspendEvent(caller);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dealNetworkNotWell() {
        if (this.mAccessListener == null) {
            LogTool.e("Access listener is null, cannot deal event: NetWorkNotWell.");
        } else {
            this.mAccessListener.dealNetWorkNotWellEvent();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dealNetworkLost(IAccessListener.Caller caller) {
        if (this.mAccessListener == null) {
            LogTool.e("Access listener is null, cannot deal event: NetWorkLost.");
        } else {
            this.mAccessListener.dealNetWorkLostEvent(caller);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ThreadSleep(long time) throws InterruptedException {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            LogTool.e("Interrupted Exception" + e.getMessage());
        }
    }
}
