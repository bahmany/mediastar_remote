package com.hisilicon.dlna.dmc.processor.interfaces;

import com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor;
import com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.MediaInitException;
import java.util.Collection;
import java.util.List;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.registry.Registry;

/* loaded from: classes.dex */
public interface UpnpProcessor {

    public interface DevicesListener {
        void onDMRChanged();

        void onDMSChanged();

        void onDeviceAdded(Device device);

        void onDeviceRemoved(Device device);

        void onDeviceUpdate(Device device);
    }

    public interface SystemListener {
        void onNetworkChanged();

        void onRouterDisabledEvent();

        void onRouterEnabledEvent();

        void onRouterError(String str);

        void onStartComplete();

        void onStartFailed();
    }

    void addDMRListener(DMRProcessor.DMRProcessorListener dMRProcessorListener);

    void addDevicesListener(DevicesListener devicesListener);

    void addPlaylistListener(PlaylistProcessor.PlaylistListener playlistListener);

    void addSystemListener(SystemListener systemListener);

    void bindUpnpService();

    ControlPoint getControlPoint();

    Device getCurrentDMR();

    Device getCurrentDMS();

    Collection<Device> getDMRList();

    DMRProcessor getDMRProcessor();

    List<Device> getDMSDevices();

    Collection<Device> getDMSList();

    DMSProcessor getDMSProcessor();

    PlaylistProcessor getPlaylistProcessor();

    Registry getRegistry();

    void refreshDevice(UDN udn);

    void refreshDevicesList();

    void removeAllRemoteDevices();

    void removeDMRListener(DMRProcessor.DMRProcessorListener dMRProcessorListener);

    void removeDevicesListener(DevicesListener devicesListener);

    void removePlaylistListener(PlaylistProcessor.PlaylistListener playlistListener);

    void removeSystemListener(SystemListener systemListener);

    void setCurrentDMR(UDN udn);

    void setCurrentDMS(UDN udn);

    void setDMSDevice(Device device);

    void setDMSExproted(boolean z);

    void setLiveTV(UDN udn);

    void setPlaylistProcessor(PlaylistProcessor playlistProcessor);

    void stbOnline(UDN udn);

    void stopLoading();

    void unbindUpnpService();

    void updateOrCreateShareAMContent() throws MediaInitException;

    void updateOrCreateShareContent() throws MediaInitException;
}
