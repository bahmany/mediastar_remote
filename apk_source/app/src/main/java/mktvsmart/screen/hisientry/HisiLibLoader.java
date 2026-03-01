package mktvsmart.screen.hisientry;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.hisilicon.multiscreen.gsensor.AirMouse;
import com.iflytek.cloud.SpeechConstant;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import mktvsmart.screen.R;
import org.videolan.vlc.util.ContextUtils;
import org.videolan.vlc.util.IOUtils;

/* loaded from: classes.dex */
public class HisiLibLoader {
    private static final String LIBS_LOCK = "mirrorlib.lock";
    private static final int MIRROR_LIB_VER = 2;
    private static String customeLibraryPath;
    public static final String TAG = HisiLibLoader.class.getSimpleName();
    private static final String[] LIBS_NEEDS = {"libairmouse_jni.so", "libmirror23_jni.so", "libmirror40_jni.so", "libmirror44_jni.so", "libmirror50_jni.so", "libmsc.so"};
    private static boolean sLoaded = false;
    private static boolean sInited = false;

    public static final String getLibraryPath() {
        return customeLibraryPath;
    }

    public static void initLibrary(Context context) {
        if (!isLoaded()) {
            if (isInited() || initialize(context, R.raw.libhisi)) {
                loadLibrary(context);
            }
        }
    }

    public static boolean loadLibrary(Context context) {
        int i = 0;
        setsInited(true);
        boolean bLoadSuccess = true;
        String[] libName = {"", "airmouse_jni", SpeechConstant.MODE_MSC};
        String osVersion = Build.VERSION.SDK;
        if (Integer.valueOf(osVersion).intValue() < 14) {
            libName[0] = "mirror23_jni";
        } else if (Integer.valueOf(osVersion).intValue() < 19) {
            libName[0] = "mirror40_jni";
        } else if (Integer.valueOf(osVersion).intValue() < 21) {
            libName[0] = "mirror44_jni";
        } else {
            libName[0] = "mirror50_jni";
        }
        String libPath1 = String.valueOf(ContextUtils.getDataDir(context)) + "lib/";
        String libPath2 = String.valueOf(ContextUtils.getDataDir(context)) + "libs/";
        if (checkLibExistInPath(libPath1, libName)) {
            try {
                int length = libName.length;
                while (i < length) {
                    String L = libName[i];
                    Log.w(TAG, "load the library: " + L);
                    System.loadLibrary(L);
                    i++;
                }
            } catch (Throwable t) {
                Log.w(TAG, "Unable to load the iomx library: " + t);
                bLoadSuccess = false;
            }
        } else if (checkLibExistInPath(libPath2, libName)) {
            int length2 = libName.length;
            while (i < length2) {
                String L2 = libName[i];
                Log.w(TAG, "load the library: " + libPath2 + "lib" + L2 + ".so");
                System.load(String.valueOf(libPath2) + "lib" + L2 + ".so");
                i++;
            }
        } else {
            bLoadSuccess = false;
        }
        setLoaded(true);
        AirMouse.sLoaded = true;
        return bLoadSuccess;
    }

    private static boolean checkLibExistInPath(String path, String[] libNames) {
        String[] libs;
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory() || (libs = dir.list()) == null) {
            return false;
        }
        Arrays.sort(libs);
        for (String L : libNames) {
            if (Arrays.binarySearch(libs, "lib" + L + ".so") < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean initialize(Context ctx, int rawId) {
        if (!isInitialized(ctx) && !extractLibs(ctx, rawId)) {
            return false;
        }
        setsInited(true);
        return true;
    }

    public static boolean isInitialized(Context ctx) throws Throwable {
        String[] libs;
        BufferedReader buffer;
        int libVersion;
        customeLibraryPath = String.valueOf(ContextUtils.getDataDir(ctx)) + "libs/";
        File dir = new File(getLibraryPath());
        if (dir.exists() && dir.isDirectory() && (libs = dir.list()) != null) {
            Arrays.sort(libs);
            for (String L : LIBS_NEEDS) {
                if (Arrays.binarySearch(libs, L) < 0) {
                    Log.e(TAG, String.format("Native libs %s not exists!", L));
                    return false;
                }
            }
            File lock = new File(String.valueOf(getLibraryPath()) + LIBS_LOCK);
            BufferedReader buffer2 = null;
            try {
                try {
                    buffer = new BufferedReader(new FileReader(lock));
                } catch (IOException e) {
                    e = e;
                } catch (NumberFormatException e2) {
                    e = e2;
                }
                try {
                    libVersion = Integer.valueOf(buffer.readLine()).intValue();
                    Log.i(TAG, String.format("isNativeLibsInited, new mirror Librar version: %d, cur mirror Library version: %d", 2, Integer.valueOf(libVersion)));
                } catch (IOException e3) {
                    e = e3;
                    buffer2 = buffer;
                    Log.e(TAG, "isNativeLibsInited", e);
                    IOUtils.closeSilently(buffer2);
                    return false;
                } catch (NumberFormatException e4) {
                    e = e4;
                    buffer2 = buffer;
                    Log.e(TAG, "isNativeLibsInited", e);
                    IOUtils.closeSilently(buffer2);
                    return false;
                } catch (Throwable th) {
                    th = th;
                    buffer2 = buffer;
                    IOUtils.closeSilently(buffer2);
                    throw th;
                }
                if (libVersion == 2) {
                    IOUtils.closeSilently(buffer);
                    return true;
                }
                IOUtils.closeSilently(buffer);
            } catch (Throwable th2) {
                th = th2;
            }
        }
        return false;
    }

    private static boolean extractLibs(Context ctx, int rawID) throws Throwable {
        long begin = System.currentTimeMillis();
        Log.d(TAG, "loadLibs start 2");
        File lock = new File(String.valueOf(getLibraryPath()) + LIBS_LOCK);
        if (lock.exists()) {
            lock.delete();
        }
        String libPath = copyCompressedLib(ctx, rawID, "libvlc.zip");
        Log.d(TAG, "copyCompressedLib time: " + ((System.currentTimeMillis() - begin) / 1000.0d));
        boolean inited = unCompressedLib(libPath, getLibraryPath());
        new File(libPath).delete();
        FileWriter fw = null;
        try {
            try {
                lock.createNewFile();
                FileWriter fw2 = new FileWriter(lock);
                try {
                    fw2.write(String.valueOf(2));
                    Log.d(TAG, "initializeNativeLibs: " + inited);
                    Log.d(TAG, "loadLibs time: " + ((System.currentTimeMillis() - begin) / 1000.0d));
                    IOUtils.closeSilently(fw2);
                    return true;
                } catch (IOException e) {
                    e = e;
                    fw = fw2;
                    Log.e(TAG, "Error creating lock file", e);
                    Log.d(TAG, "initializeNativeLibs: " + inited);
                    Log.d(TAG, "loadLibs time: " + ((System.currentTimeMillis() - begin) / 1000.0d));
                    IOUtils.closeSilently(fw);
                    return false;
                } catch (Throwable th) {
                    th = th;
                    fw = fw2;
                    Log.d(TAG, "initializeNativeLibs: " + inited);
                    Log.d(TAG, "loadLibs time: " + ((System.currentTimeMillis() - begin) / 1000.0d));
                    IOUtils.closeSilently(fw);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e2) {
            e = e2;
        }
    }

    private static String copyCompressedLib(Context ctx, int rawID, String destName) throws Throwable {
        Exception e;
        byte[] buffer = new byte[1024];
        InputStream is = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        String destPath = null;
        try {
            try {
                String destDir = getLibraryPath();
                destPath = String.valueOf(destDir) + destName;
                File f = new File(destDir);
                if (f.exists() && !f.isDirectory()) {
                    f.delete();
                }
                if (!f.exists()) {
                    f.mkdirs();
                }
                File f2 = new File(destPath);
                if (f2.exists() && !f2.isFile()) {
                    f2.delete();
                }
                if (!f2.exists()) {
                    f2.createNewFile();
                }
            } catch (Exception fe) {
                try {
                    Log.e(TAG, "loadLib", fe);
                } catch (Exception e2) {
                    e = e2;
                    Log.e(TAG, "loadLib", e);
                    IOUtils.closeSilently(fos);
                    IOUtils.closeSilently(bis);
                    IOUtils.closeSilently(is);
                    return null;
                }
            }
            is = ctx.getResources().openRawResource(rawID);
            BufferedInputStream bis2 = new BufferedInputStream(is);
            try {
                FileOutputStream fos2 = new FileOutputStream(destPath);
                while (bis2.read(buffer) != -1) {
                    try {
                        fos2.write(buffer);
                    } catch (Exception e3) {
                        e = e3;
                        fos = fos2;
                        bis = bis2;
                        Log.e(TAG, "loadLib", e);
                        IOUtils.closeSilently(fos);
                        IOUtils.closeSilently(bis);
                        IOUtils.closeSilently(is);
                        return null;
                    } catch (Throwable th) {
                        th = th;
                        fos = fos2;
                        bis = bis2;
                        IOUtils.closeSilently(fos);
                        IOUtils.closeSilently(bis);
                        IOUtils.closeSilently(is);
                        throw th;
                    }
                }
                IOUtils.closeSilently(fos2);
                IOUtils.closeSilently(bis2);
                IOUtils.closeSilently(is);
                return destPath;
            } catch (Exception e4) {
                e = e4;
                bis = bis2;
            } catch (Throwable th2) {
                th = th2;
                bis = bis2;
            }
        } catch (Throwable th3) {
            th = th3;
            IOUtils.closeSilently(fos);
            IOUtils.closeSilently(bis);
            IOUtils.closeSilently(is);
            throw th;
        }
    }

    private static boolean unCompressedLib(String libPath, String destDir) throws IOException {
        boolean bunCompressOk = false;
        try {
            try {
                byte[] buf = new byte[1024];
                ZipFile zip = new ZipFile(libPath);
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry e = entries.nextElement();
                    Log.d(TAG, "ZipEntry : " + e.getName());
                    if (Arrays.binarySearch(LIBS_NEEDS, e.getName()) >= 0 && !e.isDirectory()) {
                        String sFullPath = String.valueOf(destDir) + e.getName();
                        File destFile = new File(sFullPath);
                        Log.d(TAG, "init : " + sFullPath);
                        try {
                            InputStream in = zip.getInputStream(e);
                            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
                            while (true) {
                                int n = in.read(buf, 0, 1024);
                                if (n <= -1) {
                                    break;
                                }
                                out.write(buf, 0, n);
                            }
                            in.close();
                            out.flush();
                            out.close();
                            destFile.setReadable(true, false);
                            destFile.setWritable(true, false);
                            destFile.setExecutable(true, false);
                        } catch (IOException e1) {
                            Log.e(TAG, "loadLib", e1);
                            e1.printStackTrace();
                        }
                    }
                }
                zip.close();
                bunCompressOk = true;
                return true;
            } catch (FileNotFoundException e12) {
                Log.e(TAG, "loadLib", e12);
                e12.printStackTrace();
                return bunCompressOk;
            }
        } catch (IOException e2) {
            Log.e(TAG, "loadLib", e2);
            e2.printStackTrace();
            return bunCompressOk;
        }
    }

    public static boolean isInited() {
        return sInited;
    }

    private static void setsInited(boolean sInited2) {
        sInited = sInited2;
    }

    public static boolean isLoaded() {
        return sLoaded;
    }

    private static void setLoaded(boolean sLoaded2) {
        sLoaded = sLoaded2;
    }
}
