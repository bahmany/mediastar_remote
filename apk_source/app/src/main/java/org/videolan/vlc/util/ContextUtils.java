package org.videolan.vlc.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class ContextUtils {
    public static int getVersionCode(Context ctx) {
        try {
            int version = ctx.getPackageManager().getPackageInfo(ctx.getApplicationInfo().packageName, 0).versionCode;
            return version;
        } catch (Exception e) {
            Log.e("ContextUtils", "getVersionInt", e);
            return 0;
        }
    }

    public static String getDataDir(Context ctx) {
        ApplicationInfo ai = ctx.getApplicationInfo();
        return ai.dataDir != null ? fixLastSlash(ai.dataDir) : "/data/data/" + ai.packageName + ServiceReference.DELIMITER;
    }

    public static String fixLastSlash(String str) {
        String res = str == null ? ServiceReference.DELIMITER : String.valueOf(str.trim()) + ServiceReference.DELIMITER;
        if (res.length() > 2 && res.charAt(res.length() - 2) == '/') {
            return res.substring(0, res.length() - 1);
        }
        return res;
    }

    public static String getLibDir(Context ctx) {
        ApplicationInfo ai = ctx.getApplicationInfo();
        return ai.nativeLibraryDir != null ? fixLastSlash(ai.nativeLibraryDir) : "/data/data/" + ai.packageName + ServiceReference.DELIMITER;
    }
}
