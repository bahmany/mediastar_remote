package com.hisilicon.multiscreen.upnputils;

import com.hisilicon.multiscreen.protocol.utils.LogTool;
import java.util.Timer;
import java.util.TimerTask;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.event.EventListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.cybergarage.util.Debug;

/* loaded from: classes.dex */
public class MultiScreenUpnpControlPoint extends ControlPoint implements NotifyListener, EventListener, SearchResponseListener, DeviceChangeListener {
    private static final int HTTP_PORT_FOR_MULTISCREEN = 8060;
    public static final String REMOTE_ID_CAN_ACCESS = "0.0.0.0";
    public static final String REMOTE_ID_DEVICE_SUSPEND = "0.0.0.1";
    public static final String REMOTE_ID_STB_MANUAL_OFF = "REMOTE_ID_STB_MANUAL_OFF";
    private static final int SSDP_PORT_FOR_MULTISCREEN = 8009;
    public static boolean mIsStarted = false;
    private static MultiScreenUpnpControlPoint mMultiScreenControlPoint = null;
    private String mRemoteID = null;
    private Device mCurrentDevice = null;
    private DeviceList mOriginalList = null;
    private IUpnpControlPointListener mUpnpControlPointListener = null;
    private Timer mRenewSubscriberTimer = null;

    private class RenewSubscriberTask extends TimerTask {
        private RenewSubscriberTask() {
        }

        /* synthetic */ RenewSubscriberTask(MultiScreenUpnpControlPoint multiScreenUpnpControlPoint, RenewSubscriberTask renewSubscriberTask) {
            this();
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            MultiScreenUpnpControlPoint.this.renewSubscriber();
        }
    }

    public MultiScreenUpnpControlPoint() {
        UPnP.setEnable(9);
        UPnP.setDisable(2);
        UPnP.setDisable(1);
        Debug.off();
        setSSDPPort(SSDP_PORT_FOR_MULTISCREEN);
        setHTTPPort(HTTP_PORT_FOR_MULTISCREEN);
    }

    @Override // org.cybergarage.upnp.device.SearchResponseListener
    public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
    }

    @Override // org.cybergarage.upnp.event.EventListener
    public void eventNotifyReceived(String uuid, long seq, String name, String value) {
        if (name.equals(UpnpMultiScreenDeviceInfo.VAR_ACCESS_REMOTE_LIST) && !value.equals(this.mRemoteID)) {
            LogTool.e("RemoteID notify received");
            LogTool.d("My RemoteID is " + this.mRemoteID + ", STB RemoteID is " + value);
            if (this.mUpnpControlPointListener == null) {
                LogTool.e("UpnpControlPointListener has been set null.");
                return;
            }
            if (value.equals(REMOTE_ID_CAN_ACCESS)) {
                LogTool.d("STB can access");
                return;
            }
            if (value.equals(REMOTE_ID_STB_MANUAL_OFF)) {
                LogTool.d("STB manual off.");
                this.mUpnpControlPointListener.stbLeaveNotify();
            } else if (value.equals(REMOTE_ID_DEVICE_SUSPEND)) {
                LogTool.d("STB suspend.");
                this.mUpnpControlPointListener.stbSuspendNotify();
            } else {
                LogTool.e("STB is reaved");
                this.mUpnpControlPointListener.reavedNotify();
            }
        }
    }

    @Override // org.cybergarage.upnp.device.NotifyListener
    public void deviceNotifyReceived(SSDPPacket ssdpPacket) {
    }

    @Override // org.cybergarage.upnp.device.DeviceChangeListener
    public void deviceAdded(Device dev) {
        if (this.mUpnpControlPointListener != null && dev.getDeviceType().equals("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1")) {
            this.mUpnpControlPointListener.originalListAdd(dev);
        }
    }

    @Override // org.cybergarage.upnp.device.DeviceChangeListener
    public void deviceRemoved(Device dev) {
        if (this.mUpnpControlPointListener != null && dev.getDeviceType().equals("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1")) {
            this.mUpnpControlPointListener.originalListRemoved(dev);
        }
    }

    @Override // org.cybergarage.upnp.device.DeviceChangeListener
    public void deviceRefreshed(Device dev) {
        if (this.mUpnpControlPointListener != null && dev.getDeviceType().equals("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1")) {
            this.mUpnpControlPointListener.originalListAdd(dev);
        }
    }

    public void setControlPointListener(IUpnpControlPointListener listener) {
        this.mUpnpControlPointListener = listener;
    }

    public boolean startGsensor() {
        Action StartGsensorAct = getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_GSENSOR_TYPE, UpnpMultiScreenDeviceInfo.ACTION_GSENSOR_START);
        return postAction(StartGsensorAct);
    }

    public boolean stopGsensor() {
        Action StopGsensorAct = getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_GSENSOR_TYPE, UpnpMultiScreenDeviceInfo.ACTION_GSENSOR_STOP);
        return postAction(StopGsensorAct);
    }

    public Action getAction(String ServiceTypeName, String ActionName) {
        if (this.mCurrentDevice == null) {
            LogTool.e("Current device is null, fail to get action.");
            return null;
        }
        Service mService = this.mCurrentDevice.getService(ServiceTypeName);
        if (mService == null) {
            LogTool.e("Service not found");
            return null;
        }
        Action mAction = mService.getAction(ActionName);
        return mAction;
    }

    public boolean postAction(Action mAction) {
        if (mAction == null) {
            LogTool.e("Action not found");
            return false;
        }
        mAction.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_REMOTE_ID, this.mRemoteID);
        return mAction.postControlAction();
    }

    public StateVariable getStateVariable(String ServiceType, String StateVariableName) {
        if (this.mCurrentDevice == null) {
            LogTool.e("Current device is null, fail to get state variable.");
            return null;
        }
        StateVariable stateVar = this.mCurrentDevice.getStateVariable(ServiceType, StateVariableName);
        if (stateVar == null) {
            LogTool.e("Can not get state varibale: " + StateVariableName);
            return null;
        }
        if (stateVar.postQuerylAction()) {
            String Value = stateVar.getValue();
            StringBuffer stateValue = new StringBuffer("value of ");
            stateValue.append(StateVariableName);
            stateValue.append(" = ");
            stateValue.append(Value);
            LogTool.i(stateValue.toString());
            return stateVar;
        }
        LogTool.e(String.valueOf(StateVariableName) + ": postQuerylAction fail.");
        return null;
    }

    public boolean subscribeService(String serviceName) {
        LogTool.d("Subscribe");
        if (this.mCurrentDevice == null) {
            LogTool.e("Current device is null, fail to subscribe service.");
            return false;
        }
        Service service = this.mCurrentDevice.getService(serviceName);
        boolean isSuccess = subscribe(service);
        if (isSuccess) {
            LogTool.d("Success.");
            beginRenewSubscribeTask();
            return isSuccess;
        }
        LogTool.d("Fail.");
        return isSuccess;
    }

    public boolean unsubscribeService(String serviceName) {
        LogTool.d("Unsubscribe");
        if (this.mCurrentDevice == null) {
            LogTool.e("Current device is null, fail to unsubscribe service.");
            return false;
        }
        endRenewSubscribeTask();
        Service service = this.mCurrentDevice.getService(serviceName);
        return unsubscribe(service);
    }

    public static MultiScreenUpnpControlPoint getInstance() {
        if (mMultiScreenControlPoint == null) {
            LogTool.d("Create ctrpoint");
            mMultiScreenControlPoint = new MultiScreenUpnpControlPoint();
        }
        mMultiScreenControlPoint.registerListener();
        return mMultiScreenControlPoint;
    }

    public void destroy() {
        stopControl();
        unregisterListener();
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    public void startControl() {
        if (!isStarted()) {
            removeAlldevice();
            mMultiScreenControlPoint.start("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1");
        }
        mIsStarted = true;
    }

    public void stopControl() {
        if (isStarted()) {
            LogTool.d("removeAlldevice");
            removeAlldevice();
            mMultiScreenControlPoint.finalize();
        }
        mIsStarted = false;
    }

    public void removeAlldevice() {
        lock();
        this.mOriginalList = getDeviceList();
        for (int i = 0; i < this.mOriginalList.size(); i++) {
            removeDevice(this.mOriginalList.getDevice(i));
        }
        unlock();
    }

    public void removeCannotAccessDevice(Device device) {
        removeDevice(device);
    }

    public Device getCurrentDevice() {
        return this.mCurrentDevice;
    }

    public void setCurrentDevice(Device currentDevice) {
        this.mCurrentDevice = currentDevice;
    }

    public boolean setCurrentDeviceByHistroy(String uuid) {
        for (int i = 0; i < this.mOriginalList.size(); i++) {
            if (this.mOriginalList.getDevice(i).isDevice(uuid)) {
                this.mCurrentDevice = this.mOriginalList.getDevice(i);
                return true;
            }
        }
        return false;
    }

    public String getRemoteId() {
        return this.mRemoteID;
    }

    public void setRemoteId(String localId) {
        this.mRemoteID = localId;
    }

    private void registerListener() {
        addNotifyListener(this);
        addSearchResponseListener(this);
        addEventListener(this);
        addDeviceChangeListener(this);
    }

    private void unregisterListener() {
        removeNotifyListener(this);
        removeSearchResponseListener(this);
        removeEventListener(this);
        removeDeviceChangeListener(this);
    }

    private void beginRenewSubscribeTask() {
        LogTool.d("Begin");
        if (this.mRenewSubscriberTimer == null) {
            this.mRenewSubscriberTimer = new Timer();
            this.mRenewSubscriberTimer.schedule(new RenewSubscriberTask(this, null), 140000L, 140000L);
        }
    }

    private void endRenewSubscribeTask() {
        LogTool.d("End");
        if (this.mRenewSubscriberTimer != null) {
            this.mRenewSubscriberTimer.cancel();
            this.mRenewSubscriberTimer = null;
        }
    }

    public void renewSubscriber() {
        if (this.mCurrentDevice != null) {
            LogTool.d("renew subscriber service");
            renewSubscriberService(this.mCurrentDevice, -1L);
        }
    }
}
