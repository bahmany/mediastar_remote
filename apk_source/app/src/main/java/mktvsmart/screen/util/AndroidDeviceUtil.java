package mktvsmart.screen.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import java.util.Locale;
import java.util.UUID;
import mktvsmart.screen.GMScreenApp;

/* loaded from: classes.dex */
public class AndroidDeviceUtil {
    private static Context appContext = GMScreenApp.getAppContext();

    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) appContext.getSystemService("phone");
        if (tm.getDeviceId() == null) {
            return "None";
        }
        String imei = tm.getDeviceId();
        return imei;
    }

    public static String getWifiMac() {
        WifiManager wifiMgr = (WifiManager) appContext.getSystemService("wifi");
        WifiInfo info = wifiMgr == null ? null : wifiMgr.getConnectionInfo();
        if (info == null || info.getMacAddress() == null) {
            return "None";
        }
        String macAddress = info.getMacAddress();
        return macAddress;
    }

    public static boolean isTablet() {
        return (appContext.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static String getDeviceUUID() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(appContext);
        String uuid = pref.getString("device_uuid", "None");
        if (!uuid.equals("None")) {
            return uuid;
        }
        String imei = getIMEI();
        String mac = getWifiMac();
        if (imei.equals("None") && mac.equals("None")) {
            imei = UUID.randomUUID().toString();
            mac = UUID.randomUUID().toString();
        }
        String uuid2 = String.format(Locale.US, "%s-%s", imei, mac);
        pref.edit().putString("device_uuid", uuid2);
        return uuid2;
    }
}
