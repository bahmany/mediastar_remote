package mktvsmart.screen.vlc;

import android.content.SharedPreferences;
import com.google.android.gms.fitness.FitnessStatusCodes;
import mktvsmart.screen.GMScreenApp;
import mktvsmart.screen.GMScreenGlobalInfo;

/* loaded from: classes.dex */
public class TranscodeConstants {
    private static final String PREFENCE_NAME = "transcode";
    public static final int[] aiBitrate_7588 = {500, 800, 1000, 1500, FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS};
    public static final int[] aiBitrate_hisi = {256, 512, 1024, 2048};
    public static final String[] asResolution_7588 = {"720x576", "576x384", "360x240"};
    public static final String[] asResolution_hisi = {"720x576", "720x480", "356x288", "320x240"};
    public static int iCurBitrate;
    public static int iCurResolution;

    static {
        iCurResolution = 1;
        iCurBitrate = 0;
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 71:
            case 72:
            case 74:
                SharedPreferences pref = GMScreenApp.getAppContext().getSharedPreferences(PREFENCE_NAME, 0);
                iCurResolution = pref.getInt("3719_cur_resolution", 0);
                iCurBitrate = pref.getInt("3719_cur_bitrate", 1);
                System.out.println("iCurBitrate = " + iCurBitrate);
                break;
            case 73:
            default:
                SharedPreferences pref2 = GMScreenApp.getAppContext().getSharedPreferences(PREFENCE_NAME, 0);
                iCurResolution = pref2.getInt("7588_cur_resolution", 1);
                iCurBitrate = pref2.getInt("7588_cur_bitrate", 0);
                break;
        }
    }

    public static void saveTranscodeSetting(int resolution, int bitrate) {
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 71:
            case 72:
            case 74:
                SharedPreferences pref = GMScreenApp.getAppContext().getSharedPreferences(PREFENCE_NAME, 0);
                pref.edit().putInt("3719_cur_resolution", resolution).putInt("3719_cur_bitrate", bitrate).apply();
                break;
            case 73:
            default:
                SharedPreferences pref2 = GMScreenApp.getAppContext().getSharedPreferences(PREFENCE_NAME, 0);
                pref2.edit().putInt("7588_cur_resolution", resolution).putInt("7588_cur_bitrate", bitrate).apply();
                break;
        }
    }
}
