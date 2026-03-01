package org.videolan.vlc.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.hisilicon.multiscreen.protocol.message.MessageDef;
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
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;

/* loaded from: classes.dex */
public class VLCInstance {
    private static final String LIBS_LOCK = "vlc.lock";
    public static final int VLC_LIB_VERSION = 3;
    private static Application app;
    private static String vlcLibraryPath;
    private static String vlcPackage;
    private static final String[] LIBS_NEEDS = {"gdb.setup", "gdbserver", "libanw.10.so", "libanw.13.so", "libanw.14.so", "libanw.18.so", "libanw.21.so", "libiomx.10.so", "libiomx.13.so", "libiomx.14.so", "libvlcjni.so"};
    public static final String TAG = VLCInstance.class.getSimpleName();
    private static LibVLC instance = null;

    public static void setApp(Application app2) {
        app = app2;
    }

    public static Context getAppContext() {
        return app;
    }

    public static LibVLC getLibVlcInstance() throws NoSuchMethodException, NumberFormatException, ClassNotFoundException, SecurityException {
        if (instance == null) {
            instance = new LibVLC();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(app);
            updateLibVlcSettings(pref);
            try {
                instance.init(app);
            } catch (LibVlcException e) {
                e.printStackTrace();
            }
            LibVLC.setOnNativeCrashListener(new LibVLC.OnNativeCrashListener() { // from class: org.videolan.vlc.util.VLCInstance.1
                @Override // org.videolan.libvlc.LibVLC.OnNativeCrashListener
                public void onNativeCrash() {
                }
            });
        }
        return instance;
    }

    public static void updateLibVlcSettings(SharedPreferences pref) throws NoSuchMethodException, NumberFormatException, ClassNotFoundException, SecurityException {
        int aout;
        int vout;
        int deblocking;
        int hardwareAcceleration;
        int devHardwareDecoder;
        if (instance != null) {
            instance.setSubtitlesEncoding(pref.getString("subtitle_text_encoding", ""));
            instance.setTimeStretching(pref.getBoolean("enable_time_stretching_audio", false));
            instance.setFrameSkip(pref.getBoolean("enable_frame_skip", false));
            instance.setChroma(pref.getString("chroma_format", ""));
            instance.setVerboseMode(pref.getBoolean("enable_verbose_mode", true));
            if (pref.getBoolean("equalizer_enabled", false)) {
                instance.setEqualizer(Preferences.getFloatArray(pref, "equalizer_values"));
            }
            try {
                aout = Integer.parseInt(pref.getString("aout", MessageDef.DEVICE_NAME_PORT));
            } catch (NumberFormatException e) {
                aout = -1;
            }
            try {
                vout = Integer.parseInt(pref.getString("vout", MessageDef.DEVICE_NAME_PORT));
            } catch (NumberFormatException e2) {
                vout = -1;
            }
            try {
                deblocking = Integer.parseInt(pref.getString("deblocking", MessageDef.DEVICE_NAME_PORT));
            } catch (NumberFormatException e3) {
                deblocking = -1;
            }
            try {
                hardwareAcceleration = Integer.parseInt(pref.getString("hardware_acceleration", MessageDef.DEVICE_NAME_PORT));
            } catch (NumberFormatException e4) {
                hardwareAcceleration = -1;
            }
            try {
                devHardwareDecoder = Integer.parseInt(pref.getString("dev_hardware_decoder", MessageDef.DEVICE_NAME_PORT));
            } catch (NumberFormatException e5) {
                devHardwareDecoder = -1;
            }
            int networkCaching = pref.getInt("network_caching_value", 0);
            if (networkCaching > 60000) {
                networkCaching = 60000;
            } else if (networkCaching < 0) {
                networkCaching = 0;
            }
            instance.setAout(aout);
            instance.setVout(vout);
            instance.setDeblocking(deblocking);
            instance.setNetworkCaching(networkCaching);
            instance.setHardwareAcceleration(hardwareAcceleration);
            instance.setDevHardwareDecoder(devHardwareDecoder);
        }
    }

    public static final boolean checkVlcLibs(Activity ctx) {
        if (isInitialized(ctx) || ctx.getIntent().getBooleanExtra(InitActivity.FROM_ME, false)) {
            return true;
        }
        Intent i = new Intent();
        i.setClassName(getVitamioPackage(), InitActivity.class.getName());
        i.putExtras(ctx.getIntent());
        i.setData(ctx.getIntent().getData());
        i.putExtra("package", ctx.getPackageName());
        i.putExtra("className", ctx.getClass().getName());
        ctx.startActivity(i);
        ctx.finish();
        return false;
    }

    public static boolean initialize(Context ctx, int rawId) {
        return isInitialized(ctx) || extractLibs(ctx, rawId);
    }

    public static boolean isInitialized(Context ctx) throws Throwable {
        String[] libs;
        BufferedReader buffer;
        int libVersion;
        vlcPackage = ctx.getPackageName();
        vlcLibraryPath = String.valueOf(ContextUtils.getDataDir(ctx)) + "libs/";
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
                    try {
                        libVersion = Integer.valueOf(buffer.readLine()).intValue();
                        Log.i(TAG, String.format("isNativeLibsInited, new VLC Library VERSION: %d, Cur VLC Library version: %d", 3, Integer.valueOf(libVersion)));
                    } catch (IOException e) {
                        e = e;
                        buffer2 = buffer;
                        Log.e(TAG, "isNativeLibsInited", e);
                        IOUtils.closeSilently(buffer2);
                        Log.i(TAG, String.valueOf(vlcLibraryPath) + " is not exist");
                        return false;
                    } catch (NumberFormatException e2) {
                        e = e2;
                        buffer2 = buffer;
                        Log.e(TAG, "isNativeLibsInited", e);
                        IOUtils.closeSilently(buffer2);
                        Log.i(TAG, String.valueOf(vlcLibraryPath) + " is not exist");
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        buffer2 = buffer;
                        IOUtils.closeSilently(buffer2);
                        throw th;
                    }
                } catch (IOException e3) {
                    e = e3;
                } catch (NumberFormatException e4) {
                    e = e4;
                }
                if (libVersion == 3) {
                    IOUtils.closeSilently(buffer);
                    return true;
                }
                IOUtils.closeSilently(buffer);
            } catch (Throwable th2) {
                th = th2;
            }
        }
        Log.i(TAG, String.valueOf(vlcLibraryPath) + " is not exist");
        return false;
    }

    private static boolean extractLibs(Context ctx, int rawID) throws Throwable {
        long begin = System.currentTimeMillis();
        Log.d(TAG, "loadLibs start 3");
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
                FileWriter fw2 = new FileWriter(lock, false);
                try {
                    fw2.write(String.valueOf(3));
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

    public static String getVitamioPackage() {
        return vlcPackage;
    }

    public static final String getLibraryPath() {
        return vlcLibraryPath;
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
}
