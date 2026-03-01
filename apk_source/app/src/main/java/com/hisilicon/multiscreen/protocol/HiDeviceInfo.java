package com.hisilicon.multiscreen.protocol;

import android.os.Parcel;
import android.os.Parcelable;
import com.hisilicon.multiscreen.protocol.utils.HostNetInterface;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.upnputils.MultiScreenUpnpControlPoint;
import com.hisilicon.multiscreen.upnputils.UpnpMultiScreenDeviceInfo;
import java.util.Collection;
import java.util.HashMap;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.StateVariable;

/* loaded from: classes.dex */
public class HiDeviceInfo implements Parcelable {
    public static final Parcelable.Creator<HiDeviceInfo> CREATOR = new Parcelable.Creator<HiDeviceInfo>() { // from class: com.hisilicon.multiscreen.protocol.HiDeviceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HiDeviceInfo createFromParcel(Parcel in) {
            return new HiDeviceInfo(in, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HiDeviceInfo[] newArray(int size) {
            return new HiDeviceInfo[size];
        }
    };
    static final int DEFAULT_RETRY_TIMES_ADD_SERVICES = 1;
    private String mIpAddress;
    private String mName;
    private HashMap<String, ServiceInfo> mServices;

    public HiDeviceInfo() {
        this.mIpAddress = null;
        this.mName = null;
        this.mServices = null;
        this.mIpAddress = "";
        this.mName = "";
        this.mServices = new HashMap<>();
    }

    public HiDeviceInfo(String ip) {
        this.mIpAddress = null;
        this.mName = null;
        this.mServices = null;
        this.mIpAddress = ip;
        this.mName = "";
        this.mServices = new HashMap<>();
    }

    public HiDeviceInfo(MultiScreenUpnpControlPoint controlPoint) {
        this.mIpAddress = null;
        this.mName = null;
        this.mServices = null;
        Device device = controlPoint.getCurrentDevice();
        if (device == null) {
            LogTool.e("Current device is null, fail to construct HiDeviceInfo.");
        } else {
            this.mIpAddress = HostNetInterface.uri2Ip(device.getLocation());
            this.mName = device.getFriendlyName();
        }
    }

    public boolean resetIp(MultiScreenUpnpControlPoint controlPoint) {
        boolean isOK;
        Device device = controlPoint.getCurrentDevice();
        if (device == null) {
            LogTool.e("Current device is null, fail to reset ipAddress of HiDeviceInfo.");
            return false;
        }
        String ipAddress = HostNetInterface.uri2Ip(device.getLocation());
        String name = device.getFriendlyName();
        if (ipAddress == null || "".equals(ipAddress)) {
            isOK = false;
            LogTool.e("Reset ipAddress of HiDeviceInfo failed.");
        } else {
            isOK = true;
            setDeviceIP(ipAddress);
            setDeviceName(name);
        }
        return isOK;
    }

    public boolean resetServices(MultiScreenUpnpControlPoint controlPoint) {
        boolean isOK = resetIp(controlPoint);
        if (isOK) {
            canSyncInfo(controlPoint, 2);
        }
        return isOK;
    }

    public void setDeviceIP(String ip) {
        this.mIpAddress = ip;
    }

    public String getDeviceIP() {
        return this.mIpAddress;
    }

    public void setDeviceName(String name) {
        this.mName = name;
    }

    public String getDeviceName() {
        return this.mName;
    }

    public void addService(String name, ServiceInfo service) {
        if (this.mServices.get(name) != null) {
            this.mServices.get(name).setServiceName(service.getServiceName());
            this.mServices.get(name).setServicePort(service.getServicePort());
        } else {
            this.mServices.put(name, service);
        }
    }

    public void removeService(String name) {
        this.mServices.remove(name);
    }

    public ServiceInfo getService(String name) {
        if (this.mServices.containsKey(name)) {
            return this.mServices.get(name);
        }
        return null;
    }

    public Collection<ServiceInfo> getServiceList() {
        return this.mServices.values();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        if (this.mServices != null) {
            return this.mServices.size();
        }
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mIpAddress);
        dest.writeString(this.mName);
        dest.writeInt(this.mServices.size());
        for (String s : this.mServices.keySet()) {
            dest.writeString(s);
            dest.writeParcelable(this.mServices.get(s), 0);
        }
    }

    public void readFromParcel(Parcel in) {
        this.mIpAddress = in.readString();
        this.mName = in.readString();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            this.mServices.put(in.readString(), (ServiceInfo) in.readParcelable(ServiceInfo.class.getClassLoader()));
        }
    }

    private HiDeviceInfo(Parcel in) {
        this.mIpAddress = null;
        this.mName = null;
        this.mServices = null;
        this.mServices = new HashMap<>();
        readFromParcel(in);
    }

    /* synthetic */ HiDeviceInfo(Parcel parcel, HiDeviceInfo hiDeviceInfo) {
        this(parcel);
    }

    public ServiceInfo get(String key) {
        return this.mServices.get(key);
    }

    public void put(String key, ServiceInfo value) {
        this.mServices.put(key, value);
    }

    public boolean canSyncInfo(MultiScreenUpnpControlPoint controlPoint) {
        return addAllUpnpServices(controlPoint);
    }

    public boolean canSyncInfo(MultiScreenUpnpControlPoint controlPoint, int retryTimes) {
        return addAllUpnpServices(controlPoint, retryTimes);
    }

    public boolean addVinputService(MultiScreenUpnpControlPoint controlPoint) {
        boolean retValue = addOneUpnpService(controlPoint, UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_VINPUT_TYPE, "HI_UPNP_VAR_VinpuServerURI");
        return retValue;
    }

    public boolean addGsensorService(MultiScreenUpnpControlPoint controlPoint) {
        boolean retValue = addOneUpnpService(controlPoint, UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_GSENSOR_TYPE, "HI_UPNP_VAR_GsensorServerURI");
        return retValue;
    }

    public boolean addVIMEService(MultiScreenUpnpControlPoint controlPoint) {
        boolean controlReslut = addOneUpnpService(controlPoint, UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_VIME_TYPE, "HI_UPNP_VAR_VIMEControlServerURI");
        boolean transferReslut = addOneUpnpService(controlPoint, UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_VIME_TYPE, "HI_UPNP_VAR_VIMEDataServerURI");
        boolean retValue = controlReslut & transferReslut;
        return retValue;
    }

    private boolean addAllUpnpServices(MultiScreenUpnpControlPoint controlPoint) {
        return addAllUpnpServices(controlPoint, 1);
    }

    private boolean addAllUpnpServices(MultiScreenUpnpControlPoint controlPoint, int retryTimes) {
        boolean isOK;
        boolean vinputOK = false;
        boolean gsensorOK = false;
        boolean vimeOK = false;
        if (retryTimes < 1 || retryTimes > 4) {
            retryTimes = 1;
        }
        if (this.mServices == null) {
            this.mServices = new HashMap<>();
        }
        do {
            if (!vinputOK) {
                vinputOK = addVinputService(controlPoint);
            }
            if (!gsensorOK) {
                gsensorOK = addGsensorService(controlPoint);
            }
            if (!vimeOK) {
                vimeOK = addVIMEService(controlPoint);
            }
            isOK = vinputOK & gsensorOK & vimeOK;
            if (isOK) {
                break;
            }
            retryTimes--;
        } while (retryTimes > -1);
        return isOK;
    }

    private boolean addOneUpnpService(MultiScreenUpnpControlPoint controlPoint, String serviceName, String VarName) {
        StateVariable var = controlPoint.getStateVariable(serviceName, VarName);
        if (var == null) {
            LogTool.e("Add service fail: state variable not exist " + VarName);
            return false;
        }
        ServiceInfo sInfo = new ServiceInfo();
        LogTool.d(String.valueOf(var.getName()) + " = " + var.getValue());
        sInfo.setServiceName(var.getName());
        sInfo.setServicePort(HostNetInterface.uri2port(var.getValue()));
        addService(var.getName(), sInfo);
        return true;
    }
}
