package com.hisilicon.multiscreen.protocol.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.internal.widget.ActivityChooserView;
import com.hisilicon.dlna.dmc.gui.activity.AppPreference;
import com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService;
import com.hisilicon.multiscreen.mybox.HiMultiscreen;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;

/* loaded from: classes.dex */
public class ServiceUtil {
    public static void checkMultiScreenControlService(Context context) {
        if (!isServiceRunning(context, MultiScreenControlService.class.getName())) {
            startMultiScreenControlService(context);
        }
    }

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService("activity");
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void startMultiScreenControlService(Context context) {
        LogTool.d("Start MultiScreenControlService");
        Intent intent = new Intent(context, (Class<?>) MultiScreenControlService.class);
        context.startService(intent);
    }

    public static void stopMultiScreenControlService(Context context) {
        LogTool.d("Stop MultiScreenControlService");
        Intent intent = new Intent(context, (Class<?>) MultiScreenControlService.class);
        context.stopService(intent);
    }

    public static String getSavedUuid() {
        return MultiScreenControlService.save_uuid;
    }

    public static void saveUuid(String uuid) {
        MultiScreenControlService.save_uuid = uuid;
        AppPreference.setMultiScreenUDN(uuid);
    }

    public static void startMediaSharingService(Context context) {
        Intent intent = new Intent(HiMultiscreen.getApplication(), (Class<?>) CoreUpnpService.class);
        context.startService(intent);
    }

    public static void stopMediaSharingService(Context context) {
        Intent intent = new Intent(HiMultiscreen.getApplication(), (Class<?>) CoreUpnpService.class);
        context.stopService(intent);
    }
}
