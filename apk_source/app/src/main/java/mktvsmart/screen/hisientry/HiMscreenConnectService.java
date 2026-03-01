package mktvsmart.screen.hisientry;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.text.format.Formatter;
import android.util.Log;
import com.hisilicon.multiscreen.controller.AccessUpnpController;
import com.hisilicon.multiscreen.controller.IAccessListener;
import com.hisilicon.multiscreen.mybox.IOriginalDeviceListListener;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.utils.HostNetInterface;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.ServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.GsSendSocket;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.message.process.MessageProcessor;
import org.cybergarage.upnp.Device;

/* loaded from: classes.dex */
public class HiMscreenConnectService extends IntentService {
    public static final String CONNECT_ACTION = "action.connect.himultiscreen";
    private AccessUpnpController mAccessController;
    private IAccessListener mAccessListener;
    private String mAddress;
    private IOriginalDeviceListListener mIOriginalDeviceListListener;
    private MultiScreenControlService mMultiScreenControlService;
    private Object mSyncDevList;
    private static final String TAG = HiMscreenConnectService.class.getSimpleName();
    public static String save_ip = null;
    private static String MY_IP_FROM_STB = "";

    public HiMscreenConnectService() {
        super("HiMscreenConnectService");
        this.mAddress = "";
        this.mAccessController = null;
        this.mAccessListener = null;
        this.mSyncDevList = new Object();
        this.mIOriginalDeviceListListener = new IOriginalDeviceListListener() { // from class: mktvsmart.screen.hisientry.HiMscreenConnectService.1
            AnonymousClass1() {
            }

            @Override // com.hisilicon.multiscreen.mybox.IOriginalDeviceListListener
            public void deviceAdd(Device device) {
                if (HiMscreenConnectService.this.mAddress.equals(HostNetInterface.uri2Ip(device.getLocation()))) {
                    ServiceUtil.saveUuid(device.getUDN());
                    HiMscreenConnectService.this.connectDelay(device.getUDN());
                }
            }

            @Override // com.hisilicon.multiscreen.mybox.IOriginalDeviceListListener
            public void deviceRemoved(Device device) {
            }
        };
    }

    /* renamed from: mktvsmart.screen.hisientry.HiMscreenConnectService$1 */
    class AnonymousClass1 implements IOriginalDeviceListListener {
        AnonymousClass1() {
        }

        @Override // com.hisilicon.multiscreen.mybox.IOriginalDeviceListListener
        public void deviceAdd(Device device) {
            if (HiMscreenConnectService.this.mAddress.equals(HostNetInterface.uri2Ip(device.getLocation()))) {
                ServiceUtil.saveUuid(device.getUDN());
                HiMscreenConnectService.this.connectDelay(device.getUDN());
            }
        }

        @Override // com.hisilicon.multiscreen.mybox.IOriginalDeviceListListener
        public void deviceRemoved(Device device) {
        }
    }

    private boolean isTarget(String uuid) {
        if (isFirst()) {
            return true;
        }
        boolean isTarget = isSaved(uuid);
        return isTarget;
    }

    private boolean isFirst() {
        if (ServiceUtil.getSavedUuid() != null && !"".equals(ServiceUtil.getSavedUuid())) {
            return false;
        }
        return true;
    }

    private boolean isSaved(String uuid) {
        if (!uuid.equals(ServiceUtil.getSavedUuid())) {
            return false;
        }
        return true;
    }

    public void connectDelay(String uuid) {
        if (isTarget(uuid)) {
            autoConnectDevice(uuid);
        }
    }

    private void autoConnectDevice(String uuid) {
        if (!this.mMultiScreenControlService.isReady()) {
            LogTool.d("It is not ready for auto connect, try manual connect.");
            return;
        }
        if (!isTarget(uuid)) {
            LogTool.d("isTarget(uuid) == false");
            return;
        }
        Device device = this.mMultiScreenControlService.getDeviceDiscover().getDeviceByUUID(uuid);
        String currUuid = "";
        if (device == null) {
            LogTool.e("Device is null!");
            return;
        }
        Device currentDevice = this.mMultiScreenControlService.getControlPoint().getCurrentDevice();
        if (currentDevice != null) {
            LogTool.d("currentDevice != null");
            currUuid = currentDevice.getUDN();
        }
        if (!uuid.equals(currUuid)) {
            connectDevcie(device);
        } else if (this.mMultiScreenControlService.isRunning()) {
            LogTool.d("mMultiScreenControlService.isRunning()");
        } else {
            LogTool.d("not connect");
        }
    }

    private boolean connectDevcie(Device device) {
        LogTool.d("connectDevcie " + device);
        if (device == null) {
            LogTool.e("device is null!");
            return false;
        }
        if (canAccess(device)) {
            initLocalState();
            connectSuccess(device);
            return true;
        }
        LogTool.d("Access failed: remove device.");
        clearCurrentDevice();
        this.mMultiScreenControlService.getDeviceDiscover().removeCannotAccessDevice(device);
        return false;
    }

    private void clearCurrentDevice() {
        this.mMultiScreenControlService.getControlPoint().setCurrentDevice(null);
    }

    private boolean canAccess(Device device) {
        LogTool.d("Try to access device.");
        String remoteId = getRemoteId();
        String localIP = getWifiIpAddress();
        this.mMultiScreenControlService.getControlPoint().setCurrentDevice(device);
        this.mMultiScreenControlService.getControlPoint().setRemoteId(remoteId);
        boolean retCanAccess = this.mAccessController.accessHello(remoteId, localIP, 3000, 3000);
        LogTool.i("Access STB is " + retCanAccess);
        return retCanAccess;
    }

    private String getRemoteId() {
        String mac = getMacAddress();
        return mac;
    }

    private String getMacAddress() {
        String macAddress = null;
        WifiManager wifiMgr = (WifiManager) getSystemService("wifi");
        WifiInfo info = wifiMgr == null ? null : wifiMgr.getConnectionInfo();
        if (info != null) {
            macAddress = info.getMacAddress();
        }
        return macAddress != null ? macAddress : "08:00:27:9a:82:c2";
    }

    private String getWifiIpAddress() {
        String ipAddress = null;
        WifiManager wifiMgr = (WifiManager) getSystemService("wifi");
        WifiInfo info = wifiMgr == null ? null : wifiMgr.getConnectionInfo();
        if (info != null) {
            ipAddress = HostNetInterface.int2Ip(info.getIpAddress());
        }
        return ipAddress != null ? ipAddress : "192.168.15.101";
    }

    public String getLocalIpAddress() throws SocketException {
        String ip = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (true) {
                    if (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                            ip = inetAddress.getHostAddress().toString();
                            Log.i(TAG, "ip=" + ip);
                            break;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("SocketException IpAddress", ex.toString());
        }
        return ip;
    }

    private void initLocalState() {
        this.mMultiScreenControlService.init();
        this.mMultiScreenControlService.setAccessControllerListener(this.mAccessListener);
        syncInfo();
    }

    private void syncInfo() {
        if (this.mMultiScreenControlService.isRunning()) {
            LogTool.d("Resume Activity from HOME.");
        } else if (this.mMultiScreenControlService.canSyncInfo()) {
            initNetworkChecker();
        } else {
            LogTool.e("sync STB info failed!");
            this.mMultiScreenControlService.setState(MultiScreenControlService.ClientState.NETWORK_LOST);
        }
    }

    private void initNetworkChecker() {
        LogTool.d("initNetworkChecker");
        this.mMultiScreenControlService.startPing();
    }

    private void connectSuccess(Device device) {
        ServiceUtil.saveUuid(device.getUDN());
        LogTool.i("getLocation " + device.getLocation());
        save_ip = HostNetInterface.uri2Ip(device.getLocation());
    }

    private void initData() {
        ServiceUtil.startMultiScreenControlService(this);
        this.mMultiScreenControlService = MultiScreenControlService.getInstance();
        this.mAccessController = new AccessUpnpController();
        this.mMultiScreenControlService.setOriginalDeviceListListener(this.mIOriginalDeviceListListener);
        this.mMultiScreenControlService.setAllAccessListener(this.mAccessListener);
        this.mMultiScreenControlService.setLocalNetworkInterface(new MultiScreenControlService.LocalNetworkInterface() { // from class: mktvsmart.screen.hisientry.HiMscreenConnectService.2
            AnonymousClass2() {
            }

            @Override // com.hisilicon.multiscreen.mybox.MultiScreenControlService.LocalNetworkInterface
            public String getActivityIpAddr() {
                return (HiMscreenConnectService.MY_IP_FROM_STB == null || HiMscreenConnectService.MY_IP_FROM_STB.length() <= 0) ? HiMscreenConnectService.this.getLocalIpAddress() : HiMscreenConnectService.MY_IP_FROM_STB;
            }
        });
    }

    /* renamed from: mktvsmart.screen.hisientry.HiMscreenConnectService$2 */
    class AnonymousClass2 implements MultiScreenControlService.LocalNetworkInterface {
        AnonymousClass2() {
        }

        @Override // com.hisilicon.multiscreen.mybox.MultiScreenControlService.LocalNetworkInterface
        public String getActivityIpAddr() {
            return (HiMscreenConnectService.MY_IP_FROM_STB == null || HiMscreenConnectService.MY_IP_FROM_STB.length() <= 0) ? HiMscreenConnectService.this.getLocalIpAddress() : HiMscreenConnectService.MY_IP_FROM_STB;
        }
    }

    @Override // android.app.IntentService, android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.IntentService, android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "HiMscreenConnectService onCreate");
        MessageProcessor.obtain().setOnMessageProcess(28, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.hisientry.HiMscreenConnectService.3
            AnonymousClass3() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    DataParser parser = ParserFactory.getParser();
                    List<?> list = null;
                    try {
                        InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        list = parser.parse(istream, 15);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    HiMscreenConnectService.MY_IP_FROM_STB = (String) list.get(0);
                }
            }
        });
    }

    /* renamed from: mktvsmart.screen.hisientry.HiMscreenConnectService$3 */
    class AnonymousClass3 implements MessageProcessor.PerformOnBackground {
        AnonymousClass3() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) {
            if (msg.arg1 > 0) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                DataParser parser = ParserFactory.getParser();
                List<?> list = null;
                try {
                    InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                    list = parser.parse(istream, 15);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HiMscreenConnectService.MY_IP_FROM_STB = (String) list.get(0);
            }
        }
    }

    @Override // android.app.IntentService, android.app.Service
    public void onDestroy() {
        super.onDestroy();
        MessageProcessor.obtain().removeProcessCallback(null, 28);
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) throws SocketException, InterruptedException, UnsupportedEncodingException {
        String action;
        Log.d(TAG, "onHandleIntent " + intent);
        if (intent != null && (action = intent.getAction()) != null) {
            if (!ServiceUtil.isServiceRunning(this, MultiScreenControlService.class.getName())) {
                DataParser parser = ParserFactory.getParser();
                CreateSocket cSocket = new CreateSocket("", 0);
                try {
                    Socket tcpSocket = cSocket.GetSocket();
                    byte[] req = parser.serialize(null, 28).getBytes("UTF-8");
                    tcpSocket.setSoTimeout(3000);
                    GsSendSocket.sendSocketToStb(req, tcpSocket, 0, req.length, 28);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
                initData();
            }
            if (action.equals(CONNECT_ACTION)) {
                String ipAddress = intent.getStringExtra("address");
                this.mAddress = ipAddress;
                String locationURL = "http://" + ipAddress + ":49152/description.xml";
                Log.d(TAG, "search " + locationURL);
                Log.d(TAG, "local address " + getWifiIpAddress());
                if (getWifiIpAddress() != null) {
                    this.mMultiScreenControlService.getControlPoint().searchByUrl(locationURL, getWifiIpAddress());
                }
            }
            HisiLibLoader.initLibrary(this);
        }
    }

    public String getRemoteBroadcastAddr(String remoteip) {
        int iRemoteIp = bytesToInt(ipToBytesByReg(remoteip));
        int iMaskIp = bytesToInt(ipToBytesByReg("255.255.255.255"));
        int broadcast = (iRemoteIp & iMaskIp) | (iMaskIp ^ (-1));
        return Formatter.formatIpAddress(broadcast);
    }

    public static byte[] ipToBytesByReg(String ipAddr) {
        byte[] ret = new byte[4];
        try {
            String[] ipArr = ipAddr.split("\\.");
            ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 255);
            ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 255);
            ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 255);
            ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 255);
            return ret;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.valueOf(ipAddr) + " is invalid IP");
        }
    }

    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[3] & 255;
        return addr | ((bytes[2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | ((bytes[1] << 16) & 16711680) | ((bytes[0] << 24) & ViewCompat.MEASURED_STATE_MASK);
    }
}
