package com.google.android.gms.analytics;

import android.os.Build;
import java.io.File;

/* loaded from: classes.dex */
class o {
    static boolean ag(String str) {
        if (version() < 9) {
            return false;
        }
        File file = new File(str);
        file.setReadable(false, false);
        file.setWritable(false, false);
        file.setReadable(true, true);
        file.setWritable(true, true);
        return true;
    }

    public static int version() {
        try {
            return Integer.parseInt(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            z.T("Invalid version number: " + Build.VERSION.SDK);
            return 0;
        }
    }
}
