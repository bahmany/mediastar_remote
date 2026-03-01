package org.videolan.vlc.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import org.videolan.libvlc.LibVlcUtil;

/* loaded from: classes.dex */
public class AndroidDevices {
    public static final String TAG = "VLC/Util/AndroidDevices";
    static final boolean hasNavBar;

    static {
        HashSet<String> devicesWithoutNavBar = new HashSet<>();
        devicesWithoutNavBar.add("HTC One V");
        devicesWithoutNavBar.add("HTC One S");
        devicesWithoutNavBar.add("HTC One X");
        devicesWithoutNavBar.add("HTC One XL");
        hasNavBar = LibVlcUtil.isICSOrLater() && !devicesWithoutNavBar.contains(Build.MODEL);
    }

    public static boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean hasNavBar() {
        return hasNavBar;
    }

    public static boolean hasCombBar(Context context) {
        return !isPhone(context) && Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT <= 16;
    }

    public static boolean isPhone(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService("phone");
        return manager.getPhoneType() != 0;
    }

    public static String[] getStorageDirectories() throws Throwable {
        String[] dirs = null;
        BufferedReader bufReader = null;
        ArrayList<String> list = new ArrayList<>();
        list.add(Environment.getExternalStorageDirectory().getPath());
        List<String> typeWL = Arrays.asList("vfat", "exfat", "sdcardfs", "fuse");
        List<String> typeBL = Arrays.asList("tmpfs");
        String[] mountWL = {"/mnt", "/Removable"};
        String[] mountBL = {"/mnt/secure", "/mnt/shell", "/mnt/asec", "/mnt/obb", "/mnt/media_rw/extSdCard", "/mnt/media_rw/sdcard", "/storage/emulated"};
        String[] deviceWL = {"/dev/block/vold", "/dev/fuse", "/mnt/media_rw/extSdCard"};
        try {
            BufferedReader bufReader2 = new BufferedReader(new FileReader("/proc/mounts"));
            while (true) {
                try {
                    String line = bufReader2.readLine();
                    if (line == null) {
                        break;
                    }
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String device = tokens.nextToken();
                    String mountpoint = tokens.nextToken();
                    String type = tokens.nextToken();
                    if (!list.contains(mountpoint) && !typeBL.contains(type) && !Strings.StartsWith(mountBL, mountpoint) && Strings.StartsWith(deviceWL, device) && (typeWL.contains(type) || Strings.StartsWith(mountWL, mountpoint))) {
                        list.add(mountpoint);
                    }
                } catch (FileNotFoundException e) {
                    bufReader = bufReader2;
                    if (bufReader != null) {
                        try {
                            bufReader.close();
                        } catch (IOException e2) {
                        }
                    }
                    return dirs;
                } catch (IOException e3) {
                    bufReader = bufReader2;
                    if (bufReader != null) {
                        try {
                            bufReader.close();
                        } catch (IOException e4) {
                        }
                    }
                    return dirs;
                } catch (Throwable th) {
                    th = th;
                    bufReader = bufReader2;
                    if (bufReader != null) {
                        try {
                            bufReader.close();
                        } catch (IOException e5) {
                        }
                    }
                    throw th;
                }
            }
            dirs = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                dirs[i] = list.get(i);
            }
            if (bufReader2 != null) {
                try {
                    bufReader2.close();
                } catch (IOException e6) {
                }
            }
        } catch (FileNotFoundException e7) {
        } catch (IOException e8) {
        } catch (Throwable th2) {
            th = th2;
        }
        return dirs;
    }
}
