package org.videolan.vlc.util;

import android.os.Build;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class CPU {
    public static final int FEATURE_ARM_NEON = 32;
    public static final int FEATURE_ARM_V5TE = 1;
    public static final int FEATURE_ARM_V6 = 2;
    public static final int FEATURE_ARM_V7A = 8;
    public static final int FEATURE_ARM_VFP = 4;
    public static final int FEATURE_ARM_VFPV3 = 16;
    public static final int FEATURE_MIPS = 128;
    public static final int FEATURE_X86 = 64;
    private static final String TAG = "VLC/CPU";
    private static final Map<String, String> cpuinfo = new HashMap();
    private static int cachedFeature = -1;
    private static String cachedFeatureString = null;

    public static String getFeatureString() throws Throwable {
        getFeature();
        return cachedFeatureString;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0039  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int getFeature() throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 486
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.videolan.vlc.util.CPU.getFeature():int");
    }

    private static int getCachedFeature() {
        if (cachedFeatureString == null) {
            StringBuffer sb = new StringBuffer();
            if ((cachedFeature & 1) > 0) {
                sb.append("V5TE ");
            }
            if ((cachedFeature & 2) > 0) {
                sb.append("V6 ");
            }
            if ((cachedFeature & 4) > 0) {
                sb.append("VFP ");
            }
            if ((cachedFeature & 8) > 0) {
                sb.append("V7A ");
            }
            if ((cachedFeature & 16) > 0) {
                sb.append("VFPV3 ");
            }
            if ((cachedFeature & 32) > 0) {
                sb.append("NEON ");
            }
            if ((cachedFeature & 64) > 0) {
                sb.append("X86 ");
            }
            if ((cachedFeature & 128) > 0) {
                sb.append("MIPS ");
            }
            cachedFeatureString = sb.toString();
        }
        Log.d("GET CPU FATURE: %s", cachedFeatureString);
        return cachedFeature;
    }

    public static boolean isDroidXDroid2() {
        return Build.MODEL.trim().equalsIgnoreCase("DROIDX") || Build.MODEL.trim().equalsIgnoreCase("DROID2") || Build.FINGERPRINT.toLowerCase().contains("shadow") || Build.FINGERPRINT.toLowerCase().contains("droid2");
    }
}
