package com.hisilicon.dlna.dmc.processor.impl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor;
import com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor;
import com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor;
import com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor;
import com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServerUtil;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.MediaInitException;
import com.hisilicon.multiscreen.mybox.HiMultiscreen;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.message.header.UDNHeader;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

/* loaded from: classes.dex */
public class UpnpProcessorImpl implements UpnpProcessor, RegistryListener, CoreUpnpService.CoreUpnpServiceListener {
    private static UpnpProcessor upnpProcessor;
    private PlaylistProcessor playlistProcessor;
    private ServiceConnection serviceConnection;
    private CoreUpnpService.CoreUpnpServiceBinder upnpService;
    private List<Device> devices = new ArrayList();
    private List<UpnpProcessor.SystemListener> systemListeners = new ArrayList();
    private List<UpnpProcessor.DevicesListener> devicesListeners = new ArrayList();
    private List<PlaylistProcessor.PlaylistListener> playlistListeners = new ArrayList();

    private UpnpProcessorImpl() {
    }

    public static UpnpProcessor getSington() {
        if (upnpProcessor == null) {
            upnpProcessor = new UpnpProcessorImpl();
        }
        return upnpProcessor;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void bindUpnpService() {
        this.serviceConnection = new ServiceConnection() { // from class: com.hisilicon.dlna.dmc.processor.impl.UpnpProcessorImpl.1
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
                UpnpProcessorImpl.this.upnpService = null;
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                UpnpProcessorImpl.this.upnpService = (CoreUpnpService.CoreUpnpServiceBinder) service;
                if (UpnpProcessorImpl.this.upnpService.isInitialized()) {
                    UpnpProcessorImpl.this.upnpService.addRegistryListener(UpnpProcessorImpl.this);
                    UpnpProcessorImpl.this.fireOnStartCompleteEvent();
                    UpnpProcessorImpl.this.upnpService.setProcessor(UpnpProcessorImpl.this);
                    UpnpProcessorImpl.this.upnpService.getControlPoint().search();
                    return;
                }
                UpnpProcessorImpl.this.fireOnStartFailedEvent();
            }
        };
        Intent intent = new Intent(HiMultiscreen.getApplication(), (Class<?>) CoreUpnpService.class);
        HiMultiscreen.getApplication().bindService(intent, this.serviceConnection, 1);
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void unbindUpnpService() {
        try {
            if (this.serviceConnection != null) {
                try {
                    HiMultiscreen.getApplication().unbindService(this.serviceConnection);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void removeAllRemoteDevices() {
        try {
            if (this.upnpService != null) {
                this.upnpService.getRegistry().removeAllRemoteDevices();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void updateOrCreateShareContent() throws MediaInitException {
        if (this.upnpService != null) {
            this.upnpService.updateOrCreateShareContent();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void updateOrCreateShareAMContent() throws MediaInitException {
        if (this.upnpService != null) {
            this.upnpService.updateOrCreateShareAMContent();
        }
    }

    public void searchAll() {
        if (this.upnpService != null) {
            this.upnpService.getRegistry().removeAllRemoteDevices();
            this.upnpService.getControlPoint().search();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void addDevicesListener(UpnpProcessor.DevicesListener listener) {
        synchronized (this.devicesListeners) {
            if (!this.devicesListeners.contains(listener)) {
                this.devicesListeners.add(listener);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void removeDevicesListener(UpnpProcessor.DevicesListener listener) {
        synchronized (this.devicesListeners) {
            if (this.devicesListeners.contains(listener)) {
                this.devicesListeners.remove(listener);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public Registry getRegistry() {
        return this.upnpService.getRegistry();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public ControlPoint getControlPoint() {
        if (this.upnpService != null) {
            return this.upnpService.getControlPoint();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireOnStartCompleteEvent() {
        if (this.systemListeners != null) {
            synchronized (this.systemListeners) {
                for (UpnpProcessor.SystemListener listener : this.systemListeners) {
                    if (listener != null) {
                        listener.onStartComplete();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireOnStartFailedEvent() {
        if (this.systemListeners != null) {
            synchronized (this.systemListeners) {
                for (UpnpProcessor.SystemListener listener : this.systemListeners) {
                    if (listener != null) {
                        listener.onStartFailed();
                    }
                }
            }
        }
    }

    private void fireOnRouterErrorEvent(String cause) {
        if (this.systemListeners != null) {
            synchronized (this.systemListeners) {
                for (UpnpProcessor.SystemListener listener : this.systemListeners) {
                    if (listener != null) {
                        listener.onRouterError(cause);
                    }
                }
            }
        }
    }

    private void fireOnNetworkChangedEvent() {
        if (this.systemListeners != null) {
            synchronized (this.systemListeners) {
                for (UpnpProcessor.SystemListener listener : this.systemListeners) {
                    if (listener != null) {
                        listener.onNetworkChanged();
                    }
                }
            }
        }
    }

    private void fireOnRouterDisabledEvent() {
        if (this.systemListeners != null) {
            synchronized (this.systemListeners) {
                for (UpnpProcessor.SystemListener listener : this.systemListeners) {
                    if (listener != null) {
                        listener.onRouterDisabledEvent();
                    }
                }
            }
        }
    }

    private void fireOnRouterEnabledEvent() {
        if (this.systemListeners != null) {
            synchronized (this.systemListeners) {
                for (UpnpProcessor.SystemListener listener : this.systemListeners) {
                    if (listener != null) {
                        listener.onRouterEnabledEvent();
                    }
                }
            }
        }
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        System.out.println("Come into remoteDeviceAdded!!" + device.getDetails().getFriendlyName());
        if (device.getType().getNamespace().equals("schemas-upnp-org") && device.getType().getType().equals("MediaRenderer")) {
            addSTBDMR(device);
        }
        fireDeviceAddedEvent(device);
    }

    private void addSTBDMR(Device device) {
        System.out.println("Come into addSTBDMR device");
        Device dmrDevice = getSington().getCurrentDMR();
        URL url = ((RemoteDevice) device).getIdentity().getDescriptorURL();
        System.out.println("The device port is:" + url.getPort());
        if (HttpServerUtil.isSTBDMR(dmrDevice)) {
            System.out.println("addSTBDMR It is not null device's name:" + dmrDevice.getDetails().getFriendlyName());
            return;
        }
        System.out.println("addSTBDMR Dmr list size is:" + getSington().getDMRList().size());
        if (HttpServerUtil.isSTBDMR(device)) {
            UDN udn = device.getIdentity().getUdn();
            getSington().stbOnline(udn);
        }
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
        System.out.println("Come into remoteDeviceUpdated!!" + device.getDetails().getFriendlyName());
        fireDeviceUpdateEvent(device);
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        System.out.println("Come into remoteDeviceRemoved!!" + device.getDetails().getFriendlyName());
        if (device.getType().getNamespace().equals("schemas-upnp-org") && device.getType().getType().equals("MediaRenderer")) {
            removeSTBDMR(device);
        }
        fireDeviceRemovedEvent(device);
    }

    private void removeSTBDMR(Device device) {
        if (HttpServerUtil.isSTBDMR(device)) {
            getSington().setCurrentDMR(null);
        }
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        System.out.println("Come into localDeviceAdded!!" + device.getDetails().getFriendlyName());
        fireDeviceAddedEvent(device);
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        System.out.println("Come into localDeviceRemoved!!" + device.getDetails().getFriendlyName());
        fireDeviceRemovedEvent(device);
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void beforeShutdown(Registry registry) {
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void afterShutdown() {
    }

    private void fireDeviceAddedEvent(Device device) {
        if (this.devicesListeners != null) {
            synchronized (this.devicesListeners) {
                for (UpnpProcessor.DevicesListener listener : this.devicesListeners) {
                    if (listener != null) {
                        listener.onDeviceAdded(device);
                    }
                }
            }
        }
    }

    private void fireDeviceUpdateEvent(Device device) {
        if (this.devicesListeners != null) {
            synchronized (this.devicesListeners) {
                for (UpnpProcessor.DevicesListener listener : this.devicesListeners) {
                    if (listener != null) {
                        listener.onDeviceUpdate(device);
                    }
                }
            }
        }
    }

    private void fireDeviceRemovedEvent(Device device) {
        if (this.devicesListeners != null) {
            synchronized (this.devicesListeners) {
                for (UpnpProcessor.DevicesListener listener : this.devicesListeners) {
                    if (listener != null) {
                        listener.onDeviceRemoved(device);
                    }
                }
            }
        }
    }

    private void fireOnDMSChangedEvent() {
        if (this.devicesListeners != null) {
            synchronized (this.devicesListeners) {
                for (UpnpProcessor.DevicesListener listener : this.devicesListeners) {
                    if (listener != null) {
                        listener.onDMSChanged();
                    }
                }
            }
        }
    }

    private void fireOnDMRChangedEvent() {
        if (this.devicesListeners != null) {
            synchronized (this.devicesListeners) {
                for (UpnpProcessor.DevicesListener listener : this.devicesListeners) {
                    if (listener != null) {
                        listener.onDMRChanged();
                    }
                }
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public Collection<Device> getDMSList() {
        return this.upnpService != null ? this.upnpService.getRegistry().getDevices(new DeviceType("schemas-upnp-org", "MediaServer")) : new ArrayList();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public Collection<Device> getDMRList() {
        return this.upnpService != null ? this.upnpService.getRegistry().getDevices(new DeviceType("schemas-upnp-org", "MediaRenderer")) : new ArrayList();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void setCurrentDMS(UDN uDN) {
        this.upnpService.setCurrentDMS(uDN);
        fireOnDMSChangedEvent();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void setLiveTV(UDN uDN) {
        this.upnpService.setLiveTV(uDN);
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void stbOnline(UDN uDN) {
        this.upnpService.stbOnline(uDN);
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void setCurrentDMR(UDN uDN) {
        if (this.upnpService != null) {
            this.upnpService.setCurrentDMR(uDN);
            fireOnDMRChangedEvent();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public Device getCurrentDMS() {
        if (this.upnpService != null) {
            return this.upnpService.getCurrentDMS();
        }
        return null;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public Device getCurrentDMR() {
        if (this.upnpService != null) {
            return this.upnpService.getCurrentDMR();
        }
        return null;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public PlaylistProcessor getPlaylistProcessor() {
        return this.playlistProcessor;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public DMSProcessor getDMSProcessor() {
        if (this.upnpService != null) {
            return this.upnpService.getDMSProcessor();
        }
        return null;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public DMRProcessor getDMRProcessor() {
        if (this.upnpService != null) {
            return this.upnpService.getDMRProcessor();
        }
        return null;
    }

    @Override // com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService.CoreUpnpServiceListener
    public void onNetworkChanged(NetworkInterface ni) {
        fireOnNetworkChangedEvent();
    }

    @Override // com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService.CoreUpnpServiceListener
    public void onRouterError(String message) {
        fireOnRouterErrorEvent(message);
    }

    @Override // com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService.CoreUpnpServiceListener
    public void onRouterDisabled() {
        fireOnRouterDisabledEvent();
    }

    @Override // com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService.CoreUpnpServiceListener
    public void onRouterEnabled() {
        fireOnRouterEnabledEvent();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void setPlaylistProcessor(PlaylistProcessor processor) {
        this.playlistProcessor = processor;
        if (processor == null) {
            this.playlistListeners.clear();
            return;
        }
        synchronized (this.playlistListeners) {
            for (PlaylistProcessor.PlaylistListener listener : this.playlistListeners) {
                this.playlistProcessor.addListener(listener);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void addSystemListener(UpnpProcessor.SystemListener listener) {
        synchronized (this.systemListeners) {
            if (!this.systemListeners.contains(listener)) {
                this.systemListeners.add(listener);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void removeSystemListener(UpnpProcessor.SystemListener listener) {
        synchronized (this.systemListeners) {
            if (this.systemListeners.contains(listener)) {
                this.systemListeners.remove(listener);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void refreshDevicesList() {
        if (this.upnpService != null) {
            this.upnpService.getRegistry().removeAllRemoteDevices();
            this.upnpService.getControlPoint().search();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void refreshDevice(UDN udn) {
        if (this.upnpService != null && udn != null) {
            this.upnpService.getRegistry().removeDevice(udn);
            this.upnpService.getControlPoint().search(new UDNHeader(udn));
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void setDMSExproted(boolean value) {
        if (this.upnpService != null) {
            this.upnpService.setDMSExported(value);
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void addDMRListener(DMRProcessor.DMRProcessorListener listener) {
        if (this.upnpService != null) {
            this.upnpService.addDMRListener(listener);
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void removeDMRListener(DMRProcessor.DMRProcessorListener listener) {
        if (this.upnpService != null) {
            this.upnpService.removeDMRListener(listener);
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void addPlaylistListener(PlaylistProcessor.PlaylistListener listener) {
        synchronized (this.playlistListeners) {
            if (!this.playlistListeners.contains(listener)) {
                this.playlistListeners.add(listener);
            }
            if (this.playlistProcessor != null) {
                this.playlistProcessor.addListener(listener);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void removePlaylistListener(PlaylistProcessor.PlaylistListener listener) {
        synchronized (this.playlistListeners) {
            this.playlistListeners.remove(listener);
            if (this.playlistProcessor != null) {
                this.playlistProcessor.removeListener(listener);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void stopLoading() {
        if (this.upnpService != null) {
            this.upnpService.stopLoading();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public void setDMSDevice(Device device) {
        for (Device item : this.devices) {
            if (item.getDetails().getFriendlyName().equals(device.getDetails().getFriendlyName())) {
                return;
            }
        }
        this.devices.add(device);
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.UpnpProcessor
    public List<Device> getDMSDevices() {
        return this.devices;
    }
}
