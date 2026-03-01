package com.hisilicon.multiscreen.mybox;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.text.format.Formatter;
import com.hisilicon.dlna.dmc.gui.activity.AppPreference;
import com.hisilicon.multiscreen.controller.AccessUpnpController;
import com.hisilicon.multiscreen.controller.DeviceDiscover;
import com.hisilicon.multiscreen.controller.IAccessListener;
import com.hisilicon.multiscreen.controller.MirrorUpnpController;
import com.hisilicon.multiscreen.controller.RemoteAPPUpnpController;
import com.hisilicon.multiscreen.controller.VIMEUpnpController;
import com.hisilicon.multiscreen.controller.VinputUpnpController;
import com.hisilicon.multiscreen.gsensor.AirMouse;
import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.remote.RemoteControlCenter;
import com.hisilicon.multiscreen.protocol.utils.HostNetInterface;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener;
import com.hisilicon.multiscreen.upnputils.MultiScreenUpnpControlPoint;
import com.hisilicon.multiscreen.upnputils.UpnpMultiScreenDeviceInfo;
import com.hisilicon.multiscreen.vime.VImeClientControlService;
import org.cybergarage.net.AndoridNetInfoInterface;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.ssdp.SSDP;

/* loaded from: classes.dex */
public class MultiScreenControlService extends Service {
    public static final int AUDIO_PAUSE = 0;
    public static final int AUDIO_PLAY = 1;
    private static final long DELAY_MILLIS = 300;
    private static final int MSG_SHOW_NO_CONNECT = 300;
    private static final int MSG_START_SEARCH = 100;
    private static final int MSG_STOP_SEARCH = 200;
    public static final String VIDEO_H264_TYPE = "h264";
    public static final int VIDEO_PAUSE = 0;
    public static final int VIDEO_PLAY = 1;
    private LocalNetworkInterface ipInterface;
    public static String save_uuid = null;
    public static boolean mIsVideoPlay = false;
    private static boolean mIsAudioPlay = false;
    public static final String VIDEO_JPEG_TYPE = "jpeg";
    public static String mSupportVideoType = VIDEO_JPEG_TYPE;
    private static MultiScreenControlService mMultiScreenControlService = null;
    private static MultiScreenUpnpControlPoint mControlPoint = null;
    private static String prevConIP = null;
    private static boolean isUpnpStackOpen = false;
    private static Context mContext = null;
    private static boolean mIsNetworkAvailable = false;
    private ClientState mServiceState = ClientState.DEINIT;
    private TopActivity mActivityState = TopActivity.discovery;
    private HiDeviceInfo mHiDevice = null;
    private RemoteControlCenter mRemoteControlCenter = null;
    private AirMouse mAirMouse = null;
    private AccessUpnpController mAccessUpnpController = null;
    private VIMEUpnpController mVIMEUpnpController = null;
    private MirrorUpnpController mMirrorUpnpController = null;
    private RemoteAPPUpnpController mRemoteAppUpnpController = null;
    private VinputUpnpController mVinputUpnpController = null;
    private DeviceDiscover mDeviceDiscover = null;
    private IAccessListener mAccessListener = null;
    private IOriginalDeviceListListener mIOriginalDeviceListListener = null;
    private IUpnpControlPointListener mUpnpControlPointListener = null;
    private WifiManager.MulticastLock mMulticastLock = null;
    private BroadcastReceiver mNetworkChangeReceiver = null;
    private Thread mSubscribeAccessServiceThread = null;
    private Thread mNoConnectNoticeThread = null;
    private MyHandler myHandler = null;
    private AndoridNetInfoInterface mNetInfoInterface = new AndoridNetInfoInterface() { // from class: com.hisilicon.multiscreen.mybox.MultiScreenControlService.1
        AnonymousClass1() {
        }

        @Override // org.cybergarage.net.AndoridNetInfoInterface
        public String getBroadCastAddress() {
            return MultiScreenControlService.this.getBroadcastIpAddress();
        }
    };
    private Runnable mNoConnectNoticeRunnable = new Runnable() { // from class: com.hisilicon.multiscreen.mybox.MultiScreenControlService.2
        private int mTimes;

        AnonymousClass2() {
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            this.mTimes = 3;
            while (!MultiScreenControlService.isNetworkAvailable()) {
                this.mTimes--;
                MultiScreenControlService.this.showNoConnect();
                threadSleep(8000L);
                if (this.mTimes <= 0) {
                    return;
                }
            }
        }

        private void threadSleep(long time) throws InterruptedException {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
            }
        }
    };

    public enum ClientState {
        INIT,
        DEINIT,
        RUNNING,
        NETWORK_LOST,
        REAVED,
        STB_LEAVE,
        STB_SUSPEND;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ClientState[] valuesCustom() {
            ClientState[] clientStateArrValuesCustom = values();
            int length = clientStateArrValuesCustom.length;
            ClientState[] clientStateArr = new ClientState[length];
            System.arraycopy(clientStateArrValuesCustom, 0, clientStateArr, 0, length);
            return clientStateArr;
        }
    }

    public interface LocalNetworkInterface {
        String getActivityIpAddr();
    }

    public enum TopActivity {
        discovery,
        remote,
        mirror;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static TopActivity[] valuesCustom() {
            TopActivity[] topActivityArrValuesCustom = values();
            int length = topActivityArrValuesCustom.length;
            TopActivity[] topActivityArr = new TopActivity[length];
            System.arraycopy(topActivityArrValuesCustom, 0, topActivityArr, 0, length);
            return topActivityArr;
        }
    }

    /* renamed from: com.hisilicon.multiscreen.mybox.MultiScreenControlService$1 */
    class AnonymousClass1 implements AndoridNetInfoInterface {
        AnonymousClass1() {
        }

        @Override // org.cybergarage.net.AndoridNetInfoInterface
        public String getBroadCastAddress() {
            return MultiScreenControlService.this.getBroadcastIpAddress();
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().build());
        LogTool.d("onCreate");
        super.onCreate();
        initData();
        allowMulticast();
        initHandler();
        initControlPoint();
        registerConnectReceiver();
        mMultiScreenControlService = this;
        synchronized (MultiScreenControlService.class) {
            MultiScreenControlService.class.notifyAll();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogTool.d("onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return 2;
    }

    @Override // android.app.Service
    public void onDestroy() throws InterruptedException {
        LogTool.d("onDestroy");
        super.onDestroy();
        if (isRunning()) {
            stopPing(IAccessListener.Caller.Others, ClientState.DEINIT);
        }
        deinitControllers();
        unregisterConnectReceiver();
        deInitControlPoint();
        deInitHandler();
        unlockMulticast();
        mMultiScreenControlService = null;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        LogTool.d("onBind");
        return null;
    }

    public static MultiScreenControlService getInstance() {
        if (mMultiScreenControlService == null) {
            synchronized (MultiScreenControlService.class) {
                try {
                    System.out.println("mMultiScreenControlService is not running, wait it creat");
                    MultiScreenControlService.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return mMultiScreenControlService;
    }

    public MultiScreenUpnpControlPoint getControlPoint() {
        if (mControlPoint == null) {
            mControlPoint = MultiScreenUpnpControlPoint.getInstance();
        }
        return mControlPoint;
    }

    public void init() {
        if (this.mHiDevice == null) {
            this.mHiDevice = new HiDeviceInfo(mControlPoint);
            initAllControllers();
        } else {
            this.mHiDevice.resetIp(mControlPoint);
            resetAllControllers();
        }
        setState(ClientState.INIT);
    }

    public boolean renewState() {
        boolean isOK = this.mHiDevice.resetServices(mControlPoint);
        if (isOK) {
            resetAllControllers();
            renewMirror();
            renewVime();
            subscribeAccessService();
        } else {
            LogTool.w("Fail to renewState.");
        }
        return isOK;
    }

    private void renewMirror() {
        boolean isMirrorSet = setDefaultMirrorParameter(2);
        TopActivity topActivity = getInstance().getTopActivity();
        if (topActivity == TopActivity.mirror) {
            if (!isMirrorSet) {
                isMirrorSet = setDefaultMirrorParameter(1);
            }
            if (isMirrorSet) {
                if (!startMirror(2)) {
                    LogTool.e("Fail to start mirror.");
                    return;
                }
                return;
            }
            LogTool.e("Fail to renew mirror parameter.");
        }
    }

    private void renewVime() {
        VImeClientControlService vimeService;
        TopActivity topActivity = getInstance().getTopActivity();
        if (topActivity != TopActivity.discovery && (vimeService = VImeClientControlService.getInstance()) != null) {
            vimeService.reset();
        }
    }

    public HiDeviceInfo getHiDevice() {
        return this.mHiDevice;
    }

    public boolean isRunning() {
        return getState() == ClientState.RUNNING;
    }

    public boolean isReaved() {
        return getState() == ClientState.REAVED;
    }

    public boolean isSuspend() {
        return getState() == ClientState.STB_SUSPEND;
    }

    public boolean isReady() {
        return (isReaved() || isSuspend()) ? false : true;
    }

    public ClientState getState() {
        return this.mServiceState;
    }

    public void setState(ClientState state) {
        LogTool.d("now state:" + state.name());
        this.mServiceState = state;
    }

    public TopActivity getTopActivity() {
        return this.mActivityState;
    }

    public void setTopActivity(TopActivity top) {
        this.mActivityState = top;
    }

    public RemoteControlCenter getRemoteControlCenter() {
        if (this.mRemoteControlCenter == null) {
            LogTool.d("Get new remote control center.");
            this.mRemoteControlCenter = new RemoteControlCenter(this.mHiDevice);
        }
        return this.mRemoteControlCenter;
    }

    public AirMouse getAirMouse() {
        if (this.mAirMouse == null) {
            this.mAirMouse = new AirMouse(mContext);
        }
        return this.mAirMouse;
    }

    public AccessUpnpController getAccessController() {
        if (this.mAccessUpnpController == null) {
            LogTool.e("AccessUpnpController is null");
        }
        return this.mAccessUpnpController;
    }

    public VIMEUpnpController getVIMEUpnpController() {
        if (this.mVIMEUpnpController == null) {
            LogTool.e("VIMEUpnpController is null");
        }
        return this.mVIMEUpnpController;
    }

    public DeviceDiscover getDeviceDiscover() {
        if (this.mDeviceDiscover == null) {
            this.mDeviceDiscover = new DeviceDiscover();
        }
        return this.mDeviceDiscover;
    }

    public void startPing() {
        this.mAccessUpnpController.startAccessPingTask();
        subscribeAccessService();
        setState(ClientState.RUNNING);
    }

    public void stopPing(IAccessListener.Caller caller, ClientState state) throws InterruptedException {
        setState(state);
        unsubscribeAccessService();
        if (this.mAccessUpnpController != null) {
            this.mAccessUpnpController.stopAccessPingTask(caller);
        }
    }

    public boolean accessByebye() {
        if (this.mAccessUpnpController != null) {
            return this.mAccessUpnpController.accessByebye();
        }
        return false;
    }

    public void setAllAccessListener(IAccessListener listener) {
        setAccessControllerListener(listener);
        setControlPointListener(listener);
    }

    public void setAccessControllerListener(IAccessListener listener) {
        if (this.mAccessUpnpController == null) {
            LogTool.d("AccessUpnpController is null, and it will init after device connected.");
        } else {
            this.mAccessUpnpController.setListener(listener);
        }
    }

    public void setOriginalDeviceListListener(IOriginalDeviceListListener listener) {
        this.mIOriginalDeviceListListener = listener;
    }

    public boolean subscribeAccessService() {
        boolean isOK = subscribeService();
        if (!isOK) {
            LogTool.d("Fail to subscribe AccessService, and start subscribe thread.");
            startSubscribeThread();
        }
        return isOK;
    }

    public boolean unsubscribeAccessService() throws InterruptedException {
        boolean retValue = unsubscribeService();
        stopSubscribeThread();
        return retValue;
    }

    public boolean isVideoPlay() {
        return mIsVideoPlay;
    }

    public void setVideoPlay(boolean isPlay) {
        mIsVideoPlay = isPlay;
    }

    public boolean isAudioPlay() {
        return mIsAudioPlay;
    }

    public void setAudioPlay(boolean isPlay) {
        mIsAudioPlay = isPlay;
    }

    public String getSupportVideoType() {
        return mSupportVideoType;
    }

    public void setSupportVideoType(String type) {
        mSupportVideoType = type;
    }

    public boolean setMirrorParameter(String arg, int times) {
        return this.mMirrorUpnpController.setMirrorParameter(arg, times);
    }

    public boolean setDefaultMirrorParameter(int times) {
        StringBuffer paramBuff = new StringBuffer();
        paramBuff.append("fps=15,resend=0,cip=");
        paramBuff.append(getLastestIP());
        paramBuff.append(",cport=8888");
        paramBuff.append(",video_play=");
        if (isVideoPlay()) {
            paramBuff.append(1);
        } else {
            paramBuff.append(0);
        }
        LogTool.d("param " + paramBuff.toString());
        paramBuff.append(",source=");
        paramBuff.append(mSupportVideoType);
        return setMirrorParameter(paramBuff.toString(), times);
    }

    public boolean startMirror(int times) {
        if (this.mMirrorUpnpController != null) {
            return this.mMirrorUpnpController.startMirror(times);
        }
        return false;
    }

    public boolean stopMirror(int times) {
        if (this.mMirrorUpnpController != null) {
            return this.mMirrorUpnpController.stopMirror(times);
        }
        return false;
    }

    public boolean canSyncInfo() {
        if (this.mHiDevice == null) {
            LogTool.e("HiDevice is null.");
            return false;
        }
        boolean result = this.mHiDevice.canSyncInfo(mControlPoint);
        return result;
    }

    public String getLastestIP() {
        LogTool.d("Get local prevConIP:" + prevConIP);
        return prevConIP;
    }

    public boolean startVIMEUpnpControl() {
        return this.mVIMEUpnpController.startVIMEControlServer();
    }

    public boolean stopVIMEUpnpControl() {
        return this.mVIMEUpnpController.stopVIMEControlServer();
    }

    public boolean startVInputUpnpControl() {
        return this.mVinputUpnpController.startVinput();
    }

    public boolean stopVInputUpnpControl() {
        return this.mVinputUpnpController.stopVinput();
    }

    public boolean startRemoteAppUpnpControl() {
        return this.mRemoteAppUpnpController.startRemoteApp();
    }

    public boolean stopRemoteAppControl() {
        return this.mRemoteAppUpnpController.stopRemoteApp();
    }

    public static boolean isNetworkAvailable() {
        return mIsNetworkAvailable;
    }

    public static void readSaveUuid() {
        save_uuid = AppPreference.getMultiScreenUDN();
    }

    private void initData() {
        mContext = this;
        readSaveUuid();
        initMirrorSetting();
    }

    private void allowMulticast() {
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        this.mMulticastLock = wifiManager.createMulticastLock("multicast.upnp");
        this.mMulticastLock.setReferenceCounted(true);
        this.mMulticastLock.acquire();
    }

    private void unlockMulticast() {
        this.mMulticastLock.release();
    }

    private void initControlPoint() {
        SSDP.setNetInterfaceUtil(this.mNetInfoInterface);
        mControlPoint = MultiScreenUpnpControlPoint.getInstance();
    }

    private void initAllControllers() {
        this.mRemoteControlCenter = new RemoteControlCenter(this.mHiDevice);
        this.mAirMouse = new AirMouse(mContext);
        this.mAccessUpnpController = new AccessUpnpController();
        this.mMirrorUpnpController = new MirrorUpnpController();
        this.mVIMEUpnpController = new VIMEUpnpController();
        this.mRemoteAppUpnpController = new RemoteAPPUpnpController();
        this.mVinputUpnpController = new VinputUpnpController();
    }

    private void resetAllControllers() {
        this.mRemoteControlCenter.reset(this.mHiDevice);
        this.mAirMouse.reset(mContext);
        this.mAccessUpnpController.reset();
        this.mMirrorUpnpController.reset();
        this.mVIMEUpnpController.reset();
        this.mRemoteAppUpnpController.reset();
        this.mVinputUpnpController.reset();
    }

    private void deinitControllers() {
        if (this.mAirMouse != null) {
            this.mAirMouse.deinit();
        }
    }

    private void initMirrorSetting() {
        setVideoPlay(readVideoPreference());
        setAudioPlay(readAudioPreference());
    }

    private void deInitControlPoint() {
        if (mControlPoint != null) {
            mControlPoint.destroy();
            mControlPoint.setCurrentDevice(null);
            mControlPoint = null;
        }
        setState(ClientState.DEINIT);
        this.mUpnpControlPointListener = null;
    }

    private void setControlPointListener(IAccessListener listener) {
        this.mAccessListener = listener;
        if (this.mUpnpControlPointListener == null) {
            this.mUpnpControlPointListener = new IUpnpControlPointListener() { // from class: com.hisilicon.multiscreen.mybox.MultiScreenControlService.3
                AnonymousClass3() {
                }

                @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
                public void reavedNotify() {
                    MultiScreenControlService.this.setState(ClientState.REAVED);
                    if (MultiScreenControlService.this.mAccessListener != null) {
                        MultiScreenControlService.this.mAccessListener.dealReaveEvent(IAccessListener.Caller.Others);
                        LogTool.d("Reaved Notify callback.");
                    } else {
                        LogTool.d("Access listener is null.");
                    }
                }

                @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
                public void stbLeaveNotify() {
                    MultiScreenControlService.this.setState(ClientState.STB_LEAVE);
                    if (MultiScreenControlService.this.mAccessListener != null) {
                        MultiScreenControlService.this.mAccessListener.dealSTBLeaveEvent(IAccessListener.Caller.Others);
                        LogTool.d("STB Leave Notify callback.");
                    } else {
                        LogTool.d("Access listener is null.");
                    }
                }

                @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
                public void originalListAdd(Device device) {
                    if (MultiScreenControlService.this.mIOriginalDeviceListListener != null) {
                        MultiScreenControlService.this.mIOriginalDeviceListListener.deviceAdd(device);
                    } else {
                        LogTool.d("mIOriginalDeviceListListener is null.");
                    }
                }

                @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
                public void originalListRemoved(Device device) {
                    if (MultiScreenControlService.this.mIOriginalDeviceListListener != null) {
                        MultiScreenControlService.this.mIOriginalDeviceListListener.deviceRemoved(device);
                    } else {
                        LogTool.d("mIOriginalDeviceListListener is null.");
                    }
                }

                @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
                public void stbSuspendNotify() {
                    MultiScreenControlService.this.setState(ClientState.STB_SUSPEND);
                    if (MultiScreenControlService.this.mAccessListener != null) {
                        MultiScreenControlService.this.mAccessListener.dealSTBSuspendEvent(IAccessListener.Caller.Others);
                        LogTool.d("Suspend Notify callback.");
                    } else {
                        LogTool.d("Access listener is null.");
                    }
                }
            };
        }
        if (mControlPoint == null) {
            initControlPoint();
        }
        mControlPoint.setControlPointListener(this.mUpnpControlPointListener);
    }

    /* renamed from: com.hisilicon.multiscreen.mybox.MultiScreenControlService$3 */
    class AnonymousClass3 implements IUpnpControlPointListener {
        AnonymousClass3() {
        }

        @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
        public void reavedNotify() {
            MultiScreenControlService.this.setState(ClientState.REAVED);
            if (MultiScreenControlService.this.mAccessListener != null) {
                MultiScreenControlService.this.mAccessListener.dealReaveEvent(IAccessListener.Caller.Others);
                LogTool.d("Reaved Notify callback.");
            } else {
                LogTool.d("Access listener is null.");
            }
        }

        @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
        public void stbLeaveNotify() {
            MultiScreenControlService.this.setState(ClientState.STB_LEAVE);
            if (MultiScreenControlService.this.mAccessListener != null) {
                MultiScreenControlService.this.mAccessListener.dealSTBLeaveEvent(IAccessListener.Caller.Others);
                LogTool.d("STB Leave Notify callback.");
            } else {
                LogTool.d("Access listener is null.");
            }
        }

        @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
        public void originalListAdd(Device device) {
            if (MultiScreenControlService.this.mIOriginalDeviceListListener != null) {
                MultiScreenControlService.this.mIOriginalDeviceListListener.deviceAdd(device);
            } else {
                LogTool.d("mIOriginalDeviceListListener is null.");
            }
        }

        @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
        public void originalListRemoved(Device device) {
            if (MultiScreenControlService.this.mIOriginalDeviceListListener != null) {
                MultiScreenControlService.this.mIOriginalDeviceListListener.deviceRemoved(device);
            } else {
                LogTool.d("mIOriginalDeviceListListener is null.");
            }
        }

        @Override // com.hisilicon.multiscreen.upnputils.IUpnpControlPointListener
        public void stbSuspendNotify() {
            MultiScreenControlService.this.setState(ClientState.STB_SUSPEND);
            if (MultiScreenControlService.this.mAccessListener != null) {
                MultiScreenControlService.this.mAccessListener.dealSTBSuspendEvent(IAccessListener.Caller.Others);
                LogTool.d("Suspend Notify callback.");
            } else {
                LogTool.d("Access listener is null.");
            }
        }
    }

    private void registerConnectReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mNetworkChangeReceiver = new BroadcastReceiver() { // from class: com.hisilicon.multiscreen.mybox.MultiScreenControlService.4
            boolean isNoConnection = false;

            AnonymousClass4() {
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    this.isNoConnection = intent.getBooleanExtra("noConnectivity", false);
                    if (this.isNoConnection) {
                        LogTool.e("Network error.");
                        MultiScreenControlService.this.setNetworkAvailable(false);
                        MultiScreenControlService.this.startNoConnectNoticeThread();
                        MultiScreenControlService.this.stopDiscovery();
                        return;
                    }
                    ConnectivityManager connec = (ConnectivityManager) MultiScreenControlService.this.getSystemService("connectivity");
                    NetworkInfo info = connec.getActiveNetworkInfo();
                    if (info == null || !info.isAvailable()) {
                        MultiScreenControlService.this.setNetworkAvailable(false);
                    } else {
                        MultiScreenControlService.this.setNetworkAvailable(true);
                        MultiScreenControlService.this.restartDiscovery();
                    }
                }
            }
        };
        registerReceiver(this.mNetworkChangeReceiver, filter);
    }

    /* renamed from: com.hisilicon.multiscreen.mybox.MultiScreenControlService$4 */
    class AnonymousClass4 extends BroadcastReceiver {
        boolean isNoConnection = false;

        AnonymousClass4() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                this.isNoConnection = intent.getBooleanExtra("noConnectivity", false);
                if (this.isNoConnection) {
                    LogTool.e("Network error.");
                    MultiScreenControlService.this.setNetworkAvailable(false);
                    MultiScreenControlService.this.startNoConnectNoticeThread();
                    MultiScreenControlService.this.stopDiscovery();
                    return;
                }
                ConnectivityManager connec = (ConnectivityManager) MultiScreenControlService.this.getSystemService("connectivity");
                NetworkInfo info = connec.getActiveNetworkInfo();
                if (info == null || !info.isAvailable()) {
                    MultiScreenControlService.this.setNetworkAvailable(false);
                } else {
                    MultiScreenControlService.this.setNetworkAvailable(true);
                    MultiScreenControlService.this.restartDiscovery();
                }
            }
        }
    }

    private void unregisterConnectReceiver() {
        if (this.mNetworkChangeReceiver != null) {
            unregisterReceiver(this.mNetworkChangeReceiver);
            this.mNetworkChangeReceiver = null;
        }
    }

    private void initHandler() {
        this.myHandler = new MyHandler(this, null);
    }

    private void deInitHandler() {
        if (this.myHandler != null) {
            clearMessages();
            this.myHandler = null;
        }
    }

    public void stopDiscovery() {
        clearMessages();
        Message msg = this.myHandler.obtainMessage(200);
        this.myHandler.sendMessageDelayed(msg, DELAY_MILLIS);
    }

    public void restartDiscovery() {
        clearMessages();
        clearNoticeMessage();
        Message msg = this.myHandler.obtainMessage(200);
        this.myHandler.sendMessageDelayed(msg, DELAY_MILLIS);
        Message msg2 = this.myHandler.obtainMessage(100);
        this.myHandler.sendMessageDelayed(msg2, DELAY_MILLIS);
    }

    private void clearMessages() {
        if (this.myHandler.hasMessages(100)) {
            this.myHandler.removeMessages(100);
        }
        if (this.myHandler.hasMessages(200)) {
            this.myHandler.removeMessages(200);
        }
    }

    public void showNoConnect() {
        clearNoticeMessage();
        Message msg = this.myHandler.obtainMessage(300);
        this.myHandler.sendMessageDelayed(msg, DELAY_MILLIS);
    }

    private void clearNoticeMessage() {
        if (this.myHandler.hasMessages(300)) {
            this.myHandler.removeMessages(300);
        }
    }

    private class MyHandler extends Handler {
        private MyHandler() {
        }

        /* synthetic */ MyHandler(MultiScreenControlService multiScreenControlService, MyHandler myHandler) {
            this();
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    startUpnpStack();
                    checkState();
                    break;
                case 200:
                    stopUpnpStack();
                    break;
                case 300:
                    showNoConnect();
                    break;
            }
        }

        private void startUpnpStack() {
            String curConIP = MultiScreenControlService.this.getWifiIpAddress();
            if (!MultiScreenControlService.isUpnpStackOpen && curConIP != null) {
                LogTool.d("Network available: NowIP:" + curConIP + " PreIP:" + MultiScreenControlService.prevConIP);
                MultiScreenControlService.this.getDeviceDiscover().initSearch();
                MultiScreenControlService.isUpnpStackOpen = true;
            }
            MultiScreenControlService.prevConIP = curConIP;
        }

        private void stopUpnpStack() {
            MultiScreenControlService.this.getDeviceDiscover().finalizeSearch();
            MultiScreenControlService.isUpnpStackOpen = false;
        }

        private void showNoConnect() {
            DialogUtils.showToastLong("Network Error", MultiScreenControlService.mContext);
        }

        private void checkState() {
            if (MultiScreenControlService.getInstance().isRunning()) {
                MultiScreenControlService.getInstance().renewState();
            }
        }
    }

    /* renamed from: com.hisilicon.multiscreen.mybox.MultiScreenControlService$2 */
    class AnonymousClass2 implements Runnable {
        private int mTimes;

        AnonymousClass2() {
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            this.mTimes = 3;
            while (!MultiScreenControlService.isNetworkAvailable()) {
                this.mTimes--;
                MultiScreenControlService.this.showNoConnect();
                threadSleep(8000L);
                if (this.mTimes <= 0) {
                    return;
                }
            }
        }

        private void threadSleep(long time) throws InterruptedException {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
            }
        }
    }

    public void startNoConnectNoticeThread() {
        this.mNoConnectNoticeThread = new Thread(this.mNoConnectNoticeRunnable);
        this.mNoConnectNoticeThread.setName("NoConnectNoticeThread");
        this.mNoConnectNoticeThread.start();
    }

    private void startSubscribeThread() {
        if (this.mSubscribeAccessServiceThread == null) {
            this.mSubscribeAccessServiceThread = new Thread(new SubscribeServiceRunnable());
            this.mSubscribeAccessServiceThread.setName("SubscribeAccessServiceThread");
            this.mSubscribeAccessServiceThread.start();
        }
    }

    private void stopSubscribeThread() throws InterruptedException {
        if (this.mSubscribeAccessServiceThread != null) {
            try {
                this.mSubscribeAccessServiceThread.join(500L);
            } catch (InterruptedException e) {
                LogTool.e(e.getMessage());
            }
            this.mSubscribeAccessServiceThread = null;
        }
    }

    private class SubscribeServiceRunnable implements Runnable {
        private boolean isOK;
        private int subscribeTime;

        public SubscribeServiceRunnable() {
            this.isOK = false;
            this.subscribeTime = 10;
            this.isOK = false;
            this.subscribeTime = 10;
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            do {
                this.isOK = MultiScreenControlService.this.subscribeService();
                this.subscribeTime--;
                if (this.isOK || this.subscribeTime <= 0) {
                    break;
                } else {
                    threadSleep();
                }
            } while (!this.isOK);
            LogTool.d("End of SubscribeServiceRunnable, result:" + this.isOK);
        }

        private void threadSleep() throws InterruptedException {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean subscribeService() {
        if (mControlPoint == null) {
            return false;
        }
        boolean retValue = mControlPoint.subscribeService(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_ACCESS_TYPE);
        return retValue;
    }

    private boolean unsubscribeService() {
        if (mControlPoint == null) {
            return false;
        }
        boolean retValue = mControlPoint.unsubscribeService(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_ACCESS_TYPE);
        return retValue;
    }

    public String getWifiIpAddress() {
        if (this.ipInterface != null) {
            return this.ipInterface.getActivityIpAddr();
        }
        WifiManager wifiMgr = (WifiManager) getSystemService("wifi");
        WifiInfo info = wifiMgr == null ? null : wifiMgr.getConnectionInfo();
        if (info == null) {
            return null;
        }
        String ipAddress = HostNetInterface.int2Ip(info.getIpAddress());
        return ipAddress;
    }

    public void setLocalNetworkInterface(LocalNetworkInterface ipInterface) {
        this.ipInterface = ipInterface;
    }

    public String getBroadcastIpAddress() {
        WifiManager wifiMgr = (WifiManager) getSystemService("wifi");
        if (wifiMgr == null) {
            LogTool.e("wifi manager is null");
            return null;
        }
        DhcpInfo dhcpInfo = wifiMgr.getDhcpInfo();
        if (dhcpInfo == null) {
            LogTool.e("dhcpInfo is null");
            return null;
        }
        int broadcast = (dhcpInfo.ipAddress & dhcpInfo.netmask) | (dhcpInfo.netmask ^ (-1));
        return Formatter.formatIpAddress(broadcast);
    }

    public void setNetworkAvailable(boolean isAvailable) {
        mIsNetworkAvailable = isAvailable;
    }

    private boolean readVideoPreference() {
        return readStatusPreference(MultiSettingActivity.VIDEO_STATUS_KEY, true);
    }

    private boolean readAudioPreference() {
        return readStatusPreference(MultiSettingActivity.AUDIO_STATUS_KEY, true);
    }

    private boolean readStatusPreference(String statusKey, boolean defValue) {
        SharedPreferences prefrence = getSharedPreferences(MultiSettingActivity.SETTING_STATUS_FILE_NAME, 0);
        return prefrence.getBoolean(statusKey, defValue);
    }
}
