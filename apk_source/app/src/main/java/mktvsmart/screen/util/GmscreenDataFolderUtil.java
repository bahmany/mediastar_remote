package mktvsmart.screen.util;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import mktvsmart.screen.GMScreenApp;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class GmscreenDataFolderUtil {
    private static Context appContext = GMScreenApp.getAppContext();

    public static String getGmscreenDataFolderPath() {
        File sdFile = Environment.getExternalStorageDirectory();
        String appFolderPath = String.valueOf(sdFile.getAbsolutePath()) + File.separator + appContext.getResources().getString(R.string.app_name);
        return appFolderPath;
    }

    public static void checkExsitAndCreatGmscreenDataDir() {
        File gmscreenDataFolder = new File(getGmscreenDataFolderPath());
        if (!gmscreenDataFolder.exists()) {
            gmscreenDataFolder.mkdir();
        }
    }

    public static void deleteFileByName(String fileName) {
        if (fileName != null && Environment.getExternalStorageState().equals("mounted")) {
            checkExsitAndCreatGmscreenDataDir();
            File debugDataFile = new File(getGmscreenDataFolderPath(), fileName);
            if (debugDataFile.exists()) {
                debugDataFile.delete();
            }
        }
    }
}
