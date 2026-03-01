package mktvsmart.screen.exception;

import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
public class AppDebug {
    private static final String LOG_FILE;

    static {
        String path;
        if (Environment.getExternalStorageState().equals("mounted")) {
            path = String.valueOf(Environment.getExternalStorageDirectory().toString()) + "/G-MScreen/log";
        } else {
            path = String.valueOf(Environment.getDownloadCacheDirectory().toString()) + "/G-MScreen/log";
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        LOG_FILE = String.valueOf(path) + "/log.txt";
        File file1 = new File(LOG_FILE);
        if (!file1.exists()) {
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void writeLog(String log) {
        try {
            FileWriter writer = new FileWriter(LOG_FILE, true);
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH:mm", Locale.US);
            String logmsg = String.valueOf(df.format(date)) + " :  " + log;
            writer.write(logmsg);
            writer.write("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
