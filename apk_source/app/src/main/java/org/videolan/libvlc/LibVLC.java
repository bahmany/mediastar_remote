package org.videolan.libvlc;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.videolan.libvlc.HWDecoderUtil;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.vlc.util.ContextUtils;

/* loaded from: classes.dex */
public class LibVLC {
    public static final int AOUT_AUDIOTRACK = 0;
    public static final int AOUT_OPENSLES = 1;
    private static final String DEFAULT_CODEC_LIST = "mediacodec,iomx,all";
    public static final int DEV_HW_DECODER_AUTOMATIC = -1;
    public static final int DEV_HW_DECODER_MEDIACODEC = 2;
    public static final int DEV_HW_DECODER_MEDIACODEC_DR = 3;
    public static final int DEV_HW_DECODER_OMX = 0;
    public static final int DEV_HW_DECODER_OMX_DR = 1;
    private static final boolean HAS_WINDOW_VOUT = LibVlcUtil.isGingerbreadOrLater();
    public static final int HW_ACCELERATION_AUTOMATIC = -1;
    public static final int HW_ACCELERATION_DECODING = 1;
    public static final int HW_ACCELERATION_DISABLED = 0;
    public static final int HW_ACCELERATION_FULL = 2;
    public static final int INPUT_NAV_ACTIVATE = 0;
    public static final int INPUT_NAV_DOWN = 2;
    public static final int INPUT_NAV_LEFT = 3;
    public static final int INPUT_NAV_RIGHT = 4;
    public static final int INPUT_NAV_UP = 1;
    public static final int MEDIA_NO_HWACCEL = 2;
    public static final int MEDIA_NO_VIDEO = 1;
    public static final int MEDIA_PAUSED = 4;
    private static final String TAG = "VLC/LibVLC";
    public static final int VOUT_ANDROID_SURFACE = 0;
    public static final int VOUT_ANDROID_WINDOW = 2;
    public static final int VOUT_OPEGLES2 = 1;
    private static OnNativeCrashListener sOnNativeCrashListener;
    private long mLibVlcInstance = 0;
    private long mInternalMediaPlayerInstance = 0;
    private int hardwareAcceleration = -1;
    private int devHardwareDecoder = -1;
    private String codecList = DEFAULT_CODEC_LIST;
    private String devCodecList = null;
    private String subtitlesEncoding = "";
    private int aout = 0;
    private int vout = 0;
    private boolean timeStretching = false;
    private int deblocking = -1;
    private String chroma = "";
    private boolean verboseMode = true;
    private float[] equalizer = null;
    private boolean frameSkip = false;
    private int networkCaching = 0;
    private boolean httpReconnect = false;
    private boolean hdmiAudioEnabled = false;
    private String mCachePath = "";
    private boolean mIsInitialized = false;

    public interface OnNativeCrashListener {
        void onNativeCrash();
    }

    private native void detachEventHandler();

    private native void nativeDestroy();

    private native void nativeInit() throws LibVlcException;

    public static native boolean nativeIsPathDirectory(String str);

    public static native void nativeReadDirectory(String str, ArrayList<String> arrayList);

    public static native String nativeToURI(String str);

    public static native void sendMouseEvent(int i, int i2, int i3, int i4);

    private native void setEventHandler(EventHandler eventHandler);

    private native int setNativeEqualizer(long j, float[] fArr);

    public native int addSubtitleTrack(String str);

    public native void attachSubtitlesSurface(Surface surface);

    public native void attachSurface(Surface surface, IVideoPlayer iVideoPlayer);

    public native String changeset();

    public native String compiler();

    public native void detachSubtitlesSurface();

    public native void detachSurface();

    public native void eventVideoPlayerActivityCreated(boolean z);

    public native int expandMedia(ArrayList<String> arrayList);

    public native long getAudioDelay();

    public native int getAudioTrack();

    public native Map<Integer, String> getAudioTrackDescription();

    public native int getAudioTracksCount();

    public native float[] getBands();

    public native int getChapter();

    public native int getChapterCount();

    public native int getChapterCountForTitle(int i);

    public native String getChapterDescription(int i);

    public native long getLength();

    public native String getMeta(int i);

    public native int getPlayerState();

    public native float getPosition();

    public native float[] getPreset(int i);

    public native String[] getPresets();

    public native float getRate();

    public native long getSpuDelay();

    public native int getSpuTrack();

    public native Map<Integer, String> getSpuTrackDescription();

    public native int getSpuTracksCount();

    public native boolean getState();

    public native Map<String, Object> getStats();

    public native byte[] getThumbnail(String str, int i, int i2);

    public native long getTime();

    public native int getTitle();

    public native int getTitleCount();

    public native int getVideoTracksCount();

    public native int getVolume();

    public native boolean hasVideoTrack(String str) throws IOException;

    public native boolean isPlaying();

    public native boolean isSeekable();

    public native int nextChapter();

    public native void pause();

    public native void play();

    public native void playMRL(String str, String[] strArr);

    public native void playerNavigate(int i);

    public native int previousChapter();

    public native int setAudioDelay(long j);

    public native int setAudioTrack(int i);

    public native void setChapter(int i);

    public native void setPosition(float f);

    public native void setRate(float f);

    public native int setSpuDelay(long j);

    public native int setSpuTrack(int i);

    public native void setSurface(Surface surface);

    public native long setTime(long j);

    public native void setTitle(int i);

    public native int setVolume(int i);

    public native int setWindowSize(int i, int i2);

    public native void stop();

    public native boolean takeSnapShot(int i, String str, int i2, int i3);

    public native String version();

    public native boolean videoIsRecordable();

    public native boolean videoIsRecording();

    public native boolean videoRecordStart(String str);

    public native boolean videoRecordStop();

    public static boolean loadLibrary(Context context) {
        List<String> libNames = new ArrayList<>();
        if (Build.VERSION.SDK_INT <= 12) {
            libNames.add("anw.10");
        } else if (Build.VERSION.SDK_INT <= 13) {
            libNames.add("anw.13");
        } else if (Build.VERSION.SDK_INT <= 17) {
            libNames.add("anw.14");
        } else if (Build.VERSION.SDK_INT <= 20) {
            libNames.add("anw.18");
        } else {
            libNames.add("anw.21");
        }
        if (Build.VERSION.SDK_INT <= 10) {
            libNames.add("iomx.10");
        } else if (Build.VERSION.SDK_INT <= 13) {
            libNames.add("iomx.13");
        } else if (Build.VERSION.SDK_INT <= 17) {
            libNames.add("iomx.14");
        } else if (Build.VERSION.SDK_INT <= 18) {
            libNames.add("iomx.18");
        } else if (Build.VERSION.SDK_INT <= 19) {
            libNames.add("iomx.19");
        }
        libNames.add("vlcjni");
        String libPath1 = String.valueOf(ContextUtils.getDataDir(context)) + "lib/";
        String libPath2 = String.valueOf(ContextUtils.getDataDir(context)) + "libs/";
        if (checkLibExistInPath(libPath1, libNames)) {
            for (String L : libNames) {
                try {
                    System.loadLibrary(L);
                } catch (Throwable t) {
                    Log.w(TAG, "Unable to load the library: " + t);
                }
            }
            return true;
        }
        if (checkLibExistInPath(libPath2, libNames)) {
            for (String L2 : libNames) {
                try {
                    System.load(String.valueOf(libPath2) + "lib" + L2 + ".so");
                } catch (Throwable t2) {
                    Log.w(TAG, "Unable to load the library: " + t2);
                }
            }
            return true;
        }
        Log.w(TAG, "not found " + libNames + " in " + libPath2);
        return false;
    }

    private static boolean checkLibExistInPath(String path, List<String> libNames) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            int libCount = libNames.size();
            int misCount = 0;
            String[] libs = dir.list();
            if (libs == null) {
                return false;
            }
            Arrays.sort(libs);
            for (String L : libNames) {
                if (Arrays.binarySearch(libs, "lib" + L + ".so") < 0) {
                    Log.d(TAG, "not found lib" + L + ".so in " + path);
                    misCount++;
                }
            }
            if (misCount != libCount) {
                return true;
            }
            return false;
        }
        Log.w(TAG, "path not found ");
        return false;
    }

    protected void finalize() {
        if (this.mLibVlcInstance != 0) {
            Log.d(TAG, "LibVLC is was destroyed yet before finalize()");
            destroy();
        }
    }

    public int getHardwareAcceleration() {
        return this.hardwareAcceleration;
    }

    public void setHardwareAcceleration(int hardwareAcceleration) throws NoSuchMethodException, ClassNotFoundException, SecurityException {
        if (hardwareAcceleration == 0) {
            Log.d(TAG, "HWDec disabled: by user");
            this.hardwareAcceleration = 0;
            this.codecList = "all";
            return;
        }
        HWDecoderUtil.Decoder decoder = HWDecoderUtil.getDecoderFromDevice();
        if (decoder == HWDecoderUtil.Decoder.NONE) {
            this.hardwareAcceleration = 0;
            this.codecList = "all";
            Log.d(TAG, "HWDec disabled: device not working with mediacodec,iomx");
            return;
        }
        if (decoder == HWDecoderUtil.Decoder.UNKNOWN) {
            if (hardwareAcceleration < 0) {
                this.hardwareAcceleration = 0;
                this.codecList = "all";
                Log.d(TAG, "HWDec disabled: automatic and (unknown device or android version < 4.3)");
                return;
            } else {
                this.hardwareAcceleration = hardwareAcceleration;
                this.codecList = DEFAULT_CODEC_LIST;
                Log.d(TAG, "HWDec enabled: forced by user and unknown device");
                return;
            }
        }
        if (hardwareAcceleration < 0) {
            hardwareAcceleration = 2;
        }
        this.hardwareAcceleration = hardwareAcceleration;
        if (decoder == HWDecoderUtil.Decoder.ALL) {
            this.codecList = DEFAULT_CODEC_LIST;
        } else {
            StringBuilder sb = new StringBuilder();
            if (decoder == HWDecoderUtil.Decoder.MEDIACODEC) {
                sb.append("mediacodec,");
            } else if (decoder == HWDecoderUtil.Decoder.OMX) {
                sb.append("iomx,");
            }
            sb.append("all");
            this.codecList = sb.toString();
        }
        Log.d(TAG, "HWDec enabled: device working with: " + this.codecList);
    }

    public int getDevHardwareDecoder() {
        return this.devHardwareDecoder;
    }

    public void setDevHardwareDecoder(int devHardwareDecoder) {
        if (devHardwareDecoder != -1) {
            this.devHardwareDecoder = devHardwareDecoder;
            if (this.devHardwareDecoder == 0 || this.devHardwareDecoder == 1) {
                this.devCodecList = "iomx";
            } else {
                this.devCodecList = "mediacodec";
            }
            Log.d(TAG, "HWDec forced: " + this.devCodecList + (isDirectRendering() ? "-dr" : ""));
            this.devCodecList = String.valueOf(this.devCodecList) + ",none";
            return;
        }
        this.devHardwareDecoder = -1;
        this.devCodecList = null;
    }

    public boolean isDirectRendering() {
        if (HAS_WINDOW_VOUT) {
            return this.devHardwareDecoder != -1 ? this.devHardwareDecoder == 1 || this.devHardwareDecoder == 3 : this.hardwareAcceleration == 2;
        }
        return false;
    }

    public String[] getMediaOptions(boolean noHardwareAcceleration, boolean noVideo) {
        int flag = (noHardwareAcceleration ? 2 : 0) | (noVideo ? 1 : 0);
        return getMediaOptions(flag);
    }

    public String[] getMediaOptions(int flags) {
        boolean noHardwareAcceleration = (flags & 2) != 0;
        boolean noVideo = (flags & 1) != 0;
        boolean paused = (flags & 4) != 0;
        if (this.devHardwareDecoder != -1) {
            noVideo = false;
            noHardwareAcceleration = false;
        } else if (!noHardwareAcceleration) {
            noHardwareAcceleration = getHardwareAcceleration() == 0;
        }
        ArrayList<String> options = new ArrayList<>();
        if (!noHardwareAcceleration) {
            options.add(":file-caching=1500");
            options.add(":network-caching=1500");
            options.add(":codec=" + (this.devCodecList != null ? this.devCodecList : this.codecList));
        }
        if (noVideo) {
            options.add(":no-video");
        }
        if (paused) {
            options.add(":start-paused");
        }
        Log.d(TAG, "options = " + options);
        return (String[]) options.toArray(new String[options.size()]);
    }

    public String getSubtitlesEncoding() {
        return this.subtitlesEncoding;
    }

    public void setSubtitlesEncoding(String subtitlesEncoding) {
        this.subtitlesEncoding = subtitlesEncoding;
    }

    public int getAout() {
        return this.aout;
    }

    public void setAout(int aout) throws NoSuchMethodException, ClassNotFoundException, SecurityException {
        HWDecoderUtil.AudioOutput hwaout = HWDecoderUtil.getAudioOutputFromDevice();
        if (hwaout == HWDecoderUtil.AudioOutput.AUDIOTRACK || hwaout == HWDecoderUtil.AudioOutput.OPENSLES) {
            aout = hwaout == HWDecoderUtil.AudioOutput.OPENSLES ? 1 : 0;
        }
        this.aout = aout != 1 ? 0 : 1;
    }

    public int getVout() {
        return this.vout;
    }

    public void setVout(int vout) {
        if (vout < 0) {
            this.vout = 0;
        } else {
            this.vout = vout;
        }
        if (this.vout == 0 && HAS_WINDOW_VOUT) {
            this.vout = 2;
        }
    }

    public void setHdmiAudioEnabled(boolean enable) {
        this.hdmiAudioEnabled = enable;
    }

    public boolean isHdmiAudioEnabled() {
        return this.hdmiAudioEnabled;
    }

    public boolean useCompatSurface() {
        return this.vout != 2;
    }

    public boolean timeStretchingEnabled() {
        return this.timeStretching;
    }

    public void setTimeStretching(boolean timeStretching) {
        this.timeStretching = timeStretching;
    }

    public int getDeblocking() {
        int ret = this.deblocking;
        if (this.deblocking < 0) {
            LibVlcUtil.MachineSpecs m = LibVlcUtil.getMachineSpecs();
            if (m == null) {
                return ret;
            }
            if ((m.hasArmV6 && !m.hasArmV7) || m.hasMips) {
                ret = 4;
            } else if (m.frequency >= 1200.0f && m.processors > 2) {
                ret = 1;
            } else if (m.bogoMIPS >= 1200.0f && m.processors > 2) {
                ret = 1;
                Log.d(TAG, "Used bogoMIPS due to lack of frequency info");
            } else {
                ret = 3;
            }
        } else if (this.deblocking > 4) {
            ret = 3;
        }
        return ret;
    }

    public void setDeblocking(int deblocking) {
        this.deblocking = deblocking;
    }

    public String getChroma() {
        return this.chroma;
    }

    public void setChroma(String chroma) {
        if (chroma.equals("YV12") && !LibVlcUtil.isGingerbreadOrLater()) {
            chroma = "";
        }
        this.chroma = chroma;
    }

    public boolean isVerboseMode() {
        return this.verboseMode;
    }

    public void setVerboseMode(boolean verboseMode) {
        this.verboseMode = verboseMode;
    }

    public float[] getEqualizer() {
        return this.equalizer;
    }

    public void setEqualizer(float[] equalizer) {
        this.equalizer = equalizer;
        applyEqualizer();
    }

    private void applyEqualizer() {
        setNativeEqualizer(this.mInternalMediaPlayerInstance, this.equalizer);
    }

    public boolean frameSkipEnabled() {
        return this.frameSkip;
    }

    public void setFrameSkip(boolean frameskip) {
        this.frameSkip = frameskip;
    }

    public int getNetworkCaching() {
        return this.networkCaching;
    }

    public void setNetworkCaching(int networkcaching) {
        this.networkCaching = networkcaching;
    }

    public boolean getHttpReconnect() {
        return this.httpReconnect;
    }

    public void setHttpReconnect(boolean httpReconnect) {
        this.httpReconnect = httpReconnect;
    }

    public void init(Context context) throws LibVlcException {
        Log.v(TAG, "Initializing LibVLC");
        if (!this.mIsInitialized) {
            if (!loadLibrary(context)) {
                throw new LibVlcException("loadLibrary fail");
            }
            if (!LibVlcUtil.hasCompatibleCPU(context)) {
                Log.e(TAG, LibVlcUtil.getErrorMsg());
                throw new LibVlcException();
            }
            File cacheDir = context.getCacheDir();
            this.mCachePath = cacheDir != null ? cacheDir.getAbsolutePath() : null;
            nativeInit();
            setEventHandler(EventHandler.getInstance());
            this.mIsInitialized = true;
        }
    }

    public void destroy() {
        Log.v(TAG, "Destroying LibVLC instance");
        nativeDestroy();
        detachEventHandler();
        this.mIsInitialized = false;
    }

    public void playMRL(String mrl) {
        String[] options = getMediaOptions(false, false);
        playMRL(mrl, options);
    }

    public static String PathToURI(String path) {
        if (path == null) {
            throw new NullPointerException("Cannot convert null path!");
        }
        return nativeToURI(path);
    }

    public static void setOnNativeCrashListener(OnNativeCrashListener l) {
        sOnNativeCrashListener = l;
    }

    private static void onNativeCrash() {
        if (sOnNativeCrashListener != null) {
            sOnNativeCrashListener.onNativeCrash();
        }
    }

    public String getCachePath() {
        return this.mCachePath;
    }

    public boolean takeSnapShot(String file, int width, int height) {
        return takeSnapShot(0, file, width, height);
    }
}
