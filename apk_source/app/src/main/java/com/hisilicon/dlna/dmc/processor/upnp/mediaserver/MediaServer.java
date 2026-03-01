package com.hisilicon.dlna.dmc.processor.upnp.mediaserver;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.hisilicon.dlna.dmc.gui.activity.AppPreference;
import java.io.IOException;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
public class MediaServer {
    private static final String LOGTAG = MediaServer.class.getName();
    private static final String deviceType = "MediaServer";
    private static final String modelNumber = "v1";
    private static final int version = 1;
    private LocalService<LocalContentDirectoryService> contentDirectoryService;
    private HttpServer httpServer;
    private LocalDevice localDevice;
    private UDN udn;
    private String friendlyName = String.valueOf(AppPreference.getFriendlyName()) + "-" + deviceType;
    private String manufacturer = AppPreference.getManufacturer();

    public MediaServer(Context context) throws LocalServiceBindingException, ValidationException {
        String deviceName = String.valueOf(Build.MODEL.toUpperCase()) + " " + Build.DEVICE.toUpperCase() + " - DMS";
        this.udn = UDN.uniqueSystemIdentifier(String.valueOf(deviceName) + this.friendlyName);
        DeviceType type = new UDADeviceType(deviceType, 1);
        DeviceDetails details = new DeviceDetails(this.friendlyName, new ManufacturerDetails(this.manufacturer), new ModelDetails(deviceName, String.valueOf(deviceName) + " MediaServer for Android", modelNumber));
        this.contentDirectoryService = new AnnotationLocalServiceBinder().read(LocalContentDirectoryService.class);
        this.contentDirectoryService.setManager(new DefaultServiceManager(this.contentDirectoryService, LocalContentDirectoryService.class));
        LocalService<LocalConnectionManagerService> connectionManager = new AnnotationLocalServiceBinder().read(LocalConnectionManagerService.class);
        connectionManager.setManager(new DefaultServiceManager<>(connectionManager, LocalConnectionManagerService.class));
        this.localDevice = new LocalDevice(new DeviceIdentity(this.udn), type, details, new LocalService[]{this.contentDirectoryService, connectionManager});
        try {
            this.httpServer = new HttpServer(HttpServerUtil.PORT);
        } catch (Exception ioe) {
            ioe.printStackTrace();
            System.err.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }
        Log.v(LOGTAG, "Started Http Server on port " + HttpServerUtil.PORT);
    }

    public LocalService getLocalService() {
        return this.contentDirectoryService;
    }

    public LocalDevice getDevice() {
        return this.localDevice;
    }

    public void stopHttpServer() throws InterruptedException, IOException {
        this.httpServer.stop();
    }
}
