package com.hisilicon.dlna.dmc.gui.activity;

import com.hisilicon.dlna.dmc.utility.PrefConfig;

/* loaded from: classes.dex */
public class AppPreference {
    public static boolean isAppFirstUse() {
        return PrefConfig.getIntPreferences("Pref_IS_APP_FIRST_USE") == 0;
    }

    public static void setAppAlreadyUse() {
        PrefConfig.setIntPreferences("Pref_IS_APP_FIRST_USE", 1);
    }

    public static boolean isMirrorFirstUse() {
        return PrefConfig.getIntPreferences("Pref_IS_MIRROR") == 0;
    }

    public static void setMirrrorUse() {
        PrefConfig.setIntPreferences("Pref_IS_MIRROR", 1);
    }

    public static int getMaxItemPerLoad() {
        return PrefConfig.getIntPreferences("Pref_MAX_RESULT", 60);
    }

    public static boolean getAutoNext() {
        return PrefConfig.getBooleanPreferences("Pref_AUTO_NEXT", true);
    }

    public static boolean getShuffle() {
        return PrefConfig.getBooleanPreferences("Pref_RANDOM_TRACK");
    }

    public static void setShuffle(boolean shuffle) {
        PrefConfig.setBooleanPreferences("Pref_RANDOM_TRACK", shuffle);
    }

    public static String getFriendlyName() {
        return PrefConfig.getStringPreferences("Pref_DLNA_FRIENDLY_NAME", "friendly_name");
    }

    public static String getMultiScreenUDN() {
        return PrefConfig.getStringPreferences("Pref_DLNA_MUTISCREEN_UDN");
    }

    public static void setMultiScreenUDN(String udn) {
        PrefConfig.setStringPreferences("Pref_DLNA_MUTISCREEN_UDN", udn);
    }

    public static String getManufacturer() {
        return PrefConfig.getStringPreferences("Pref_DLNA_MANUFACTURER", "manufacturer");
    }

    public static String getDLNACreater() {
        return PrefConfig.getStringPreferences("Pref_DLNA_CREATER", "dlna_creater");
    }

    public static String getCameraCachePath() {
        return PrefConfig.getStringPreferences("Pref_DLNA_CAMERA_CACHE_PATH");
    }

    public static void setCameraCachePath(String path) {
        PrefConfig.setStringPreferences("Pref_DLNA_CAMERA_CACHE_PATH", path);
    }

    public static int getPlayPosition() {
        return PrefConfig.getIntPreferences("Pref_PLAY_POSITION");
    }

    public static void setPlayPosition(int playPosition) {
        PrefConfig.setIntPreferences("Pref_PLAY_POSITION", playPosition);
    }
}
