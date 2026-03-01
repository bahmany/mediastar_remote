package com.hisilicon.multiscreen.controller;

import com.hisilicon.multiscreen.protocol.utils.HostNetInterface;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.upnputils.MultiScreenDeviceList;
import com.hisilicon.multiscreen.upnputils.MultiScreenUpnpControlPoint;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Comparator;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;

/* loaded from: classes.dex */
public class DeviceDiscover {
    private MultiScreenUpnpControlPoint mControlPoint;
    private MultiScreenDeviceList mDeviceList;
    private DeviceList mOriginalList = null;
    private Runnable mStartControlPointRunnable;

    public DeviceDiscover() {
        this.mControlPoint = null;
        this.mDeviceList = null;
        this.mStartControlPointRunnable = null;
        this.mControlPoint = MultiScreenUpnpControlPoint.getInstance();
        this.mDeviceList = new MultiScreenDeviceList();
        this.mStartControlPointRunnable = new Runnable() { // from class: com.hisilicon.multiscreen.controller.DeviceDiscover.1
            @Override // java.lang.Runnable
            public void run() {
                DeviceDiscover.this.mControlPoint.startControl();
            }
        };
    }

    public void initSearch() {
        if (this.mControlPoint != null) {
            Thread localThread = new Thread(this.mStartControlPointRunnable);
            localThread.setPriority(1);
            localThread.setName("initSearchThread");
            localThread.start();
            return;
        }
        LogTool.e("mControlPoint is null.");
    }

    public void finalizeSearch() {
        if (this.mControlPoint != null) {
            this.mControlPoint.stopControl();
        } else {
            LogTool.e("mControlPoint is null.");
        }
    }

    public void msearch() {
        this.mControlPoint.search("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1");
    }

    public void reSearch() {
        clearOriginalList();
        msearch();
    }

    public void removeDevice(Device device) {
        this.mDeviceList.lock();
        for (int i = 0; i < this.mDeviceList.size(); i++) {
            if (this.mDeviceList.getDevice(i).getUDN().equals(device.getUDN())) {
                this.mDeviceList.remove(i);
            }
        }
        this.mDeviceList.unlock();
    }

    public void clearOriginalList() {
        this.mControlPoint.removeAlldevice();
    }

    public boolean isOriginalListEmpty() {
        this.mOriginalList = this.mControlPoint.getDeviceList();
        return this.mOriginalList.isEmpty();
    }

    public void clearAllList() {
        this.mControlPoint.removeAlldevice();
        this.mDeviceList.clear();
    }

    public void syncOrderingList() {
        filterMultiScreenList();
        sortMultiScreenDeviceList(this.mDeviceList);
    }

    public void removeCannotAccessDevice(Device device) {
        this.mControlPoint.removeCannotAccessDevice(device);
        msearch();
    }

    public MultiScreenDeviceList getDeviceList() {
        return this.mDeviceList;
    }

    public Device getDeviceByUUID(String uuid) {
        this.mOriginalList = this.mControlPoint.getDeviceList();
        this.mOriginalList.lock();
        for (int i = 0; i < this.mOriginalList.size(); i++) {
            if (this.mOriginalList.getDevice(i).isDevice(uuid) && this.mOriginalList.getDevice(i).getDeviceType().startsWith("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1")) {
                this.mOriginalList.unlock();
                return this.mOriginalList.getDevice(i);
            }
        }
        this.mOriginalList.unlock();
        return null;
    }

    public Device getDeviceByIP(InetAddress inetAddress) {
        String ipAddress = inetAddress.getHostAddress();
        this.mOriginalList = this.mControlPoint.getDeviceList();
        this.mOriginalList.lock();
        for (int i = 0; i < this.mOriginalList.size(); i++) {
            if (ipAddress.equals(HostNetInterface.uri2Ip(this.mOriginalList.getDevice(i).getLocation())) && this.mOriginalList.getDevice(i).getDeviceType().startsWith("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1")) {
                this.mOriginalList.unlock();
                return this.mOriginalList.getDevice(i);
            }
        }
        this.mOriginalList.unlock();
        return null;
    }

    private void filterMultiScreenList() {
        this.mDeviceList.lock();
        this.mDeviceList.clear();
        this.mOriginalList = this.mControlPoint.getDeviceList();
        for (int i = 0; i < this.mOriginalList.size(); i++) {
            if (this.mOriginalList.getDevice(i).getDeviceType().startsWith("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1")) {
                this.mDeviceList.add(this.mOriginalList.getDevice(i));
            }
        }
        this.mDeviceList.unlock();
    }

    class DeviceListComparator implements Comparator<Device> {
        DeviceListComparator() {
        }

        @Override // java.util.Comparator
        public int compare(Device lDevice, Device rDevice) {
            if (lDevice.getLocation().compareTo(rDevice.getLocation()) > 0) {
                return 1;
            }
            if (lDevice.getLocation().compareTo(rDevice.getLocation()) < 0) {
                return -1;
            }
            return 0;
        }
    }

    private void sortMultiScreenDeviceList(MultiScreenDeviceList deviceList) {
        DeviceListComparator devComparator = new DeviceListComparator();
        deviceList.lock();
        Collections.sort(deviceList, devComparator);
        deviceList.unlock();
    }
}
