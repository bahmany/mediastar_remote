package com.huawei.videoengineapp;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServerUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/* loaded from: classes.dex */
public class ViEAndroidJavaAPI {
    public static int DecStatisticParams_uiBitRate = 0;
    public static int DecStatisticParams_uiImageHeight = 0;
    public static int DecStatisticParams_uiImageWidth = 0;
    public static int DecStatisticParams_uiPktRate = 0;
    public static int EncStatisticParams_fKeyRedundanceRate = 0;
    public static int EncStatisticParams_fNoRefRedundanceRate = 0;
    public static int EncStatisticParams_fRefRedundanceRate = 0;
    public static int EncStatisticParams_uiBufferData = 0;
    public static int EncStatisticParams_uiEncBitRate = 0;
    public static int EncStatisticParams_uiEncFrameRate = 0;
    public static int EncStatisticParams_uiImageHeight = 0;
    public static int EncStatisticParams_uiImageWidth = 0;
    public static int EncStatisticParams_uiPktRate = 0;
    public static int EncStatisticParams_uiSendBitRate = 0;
    private static final String LOG_TAG = "hme-video";
    public static int stCaptureCapability_eVideoCodecType;
    public static int stCaptureCapability_eVideoRawType;
    public static int stCaptureCapability_uiImageHeight;
    public static int stCaptureCapability_uiImageWidth;
    public static int stCaptureCapability_uiMaxFps;
    public static boolean enableVideoSend = true;
    public static boolean enableVideoReceive = true;
    public static int logLevel = 0;
    public static int eRtpType = 1;
    public static int eProfile = 0;
    public static int eCodecType = 0;
    public static int uiPayloadType = 122;
    public static int uiEncQuality = 0;
    public static int uiKeyInterval = 2000;
    public static int uiWidth = 320;
    public static int uiHeight = 240;
    public static int fFrameRate = 15;
    public static int eRCType = 0;
    public static int uiBitRate = 256;
    public static int uiSendBitRate = 300;
    public static int uiMaxPktLen = 1500;
    public static int eAntiPktLoss = 3;
    public static boolean bCtrlResolution = true;
    public static boolean bCtrlFec = true;
    public static int uiMaxBitRate = 2500;
    public static int uiMaxFrameRate = 30;
    public static int uiMinFrameRate = 1;
    public static String szLocalIP = HttpServerUtil.LOOP;
    public static int uiLocalPort = 11111;
    public static String szRemoteIP = HttpServerUtil.LOOP;
    public static int uiRemotePort = 11111;
    public static int eRtcpType = 1;
    public static boolean eCipherType = false;
    public static int uiFecPktPT = 97;
    public static int uiRedPktPT = 96;
    public static int uiLossRate = 0;
    public static boolean bMultiFrm = false;
    public static boolean bDenoise = false;
    public static boolean bColorEnhance = false;
    public static int eDecCodecType = 0;
    public static int eDisplayMode = 1;
    public static int eRotateAngle = 0;
    public static boolean bMirrorYAxis = false;

    private native boolean NativeInit(Context context);

    public native int GetCameraOrientation(int i);

    public native int GetDecoderChannelStatistics();

    public native int GetEncChannelStatistics();

    public native int GetRenderSnapshot();

    public native int Init(int i);

    public native int[] QueryCPUConsume(int[] iArr);

    public native int ResetStream();

    public native int SetHook(int i);

    public native int SetLocalReceiver(int i, byte[] bArr);

    public native int SetReceiveCodec(int i, int i2, int i3, int i4, boolean z);

    public native int SetRotation(int i);

    public native int SetRotationAPI();

    public native int SetSendCodec(int i, boolean z);

    public native int SetSendDestination(int i, byte[] bArr);

    public native int SetupStream();

    public native int StartCamera(int i);

    public native int StartReceive(Object obj);

    public native int StartSend();

    public native int StopReceive();

    public native int StopSend();

    public native int SwitchCapture();

    public native int Terminate();

    public native int getCaptureShot();

    public native String getVersion();

    private static boolean getNeon() throws Throwable {
        boolean neon = false;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            try {
                FileReader fr2 = new FileReader("/proc/cpuinfo");
                try {
                    BufferedReader br2 = new BufferedReader(fr2, 1024);
                    while (true) {
                        try {
                            String line = br2.readLine();
                            if (line == null) {
                                break;
                            }
                            String line2 = line.toLowerCase();
                            if (line2.contains("neon") && line2.contains("features")) {
                                neon = true;
                            }
                        } catch (IOException e) {
                            ex = e;
                            br = br2;
                            fr = fr2;
                            ex.printStackTrace();
                            if (br != null) {
                                try {
                                    br.close();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                                br = null;
                            }
                            if (fr != null) {
                                try {
                                    fr.close();
                                } catch (IOException e3) {
                                }
                                fr = null;
                            }
                            Log.d("getNeon", "neon:" + neon);
                            return neon;
                        } catch (Throwable th) {
                            th = th;
                            br = br2;
                            fr = fr2;
                            if (br != null) {
                                try {
                                    br.close();
                                } catch (IOException e4) {
                                    e4.printStackTrace();
                                }
                            }
                            if (fr != null) {
                                try {
                                    fr.close();
                                } catch (IOException e5) {
                                }
                            }
                            throw th;
                        }
                    }
                    if (br2 != null) {
                        try {
                            br2.close();
                        } catch (IOException e6) {
                            e6.printStackTrace();
                        }
                        br = null;
                    } else {
                        br = br2;
                    }
                    if (fr2 != null) {
                        try {
                            fr2.close();
                        } catch (IOException e7) {
                        }
                        fr = null;
                    } else {
                        fr = fr2;
                    }
                } catch (IOException e8) {
                    ex = e8;
                    fr = fr2;
                } catch (Throwable th2) {
                    th = th2;
                    fr = fr2;
                }
            } catch (IOException e9) {
                ex = e9;
            }
            Log.d("getNeon", "neon:" + neon);
            return neon;
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public ViEAndroidJavaAPI(Context context) {
        if (Build.CPU_ABI.contains("armeabi-v7a")) {
            Log.i("HME_DEMO", "---------------armeabi-v7a :" + Build.CPU_ABI);
            Log.d(LOG_TAG, "android version:" + Build.VERSION.SDK_INT);
            if (getNeon()) {
                Log.d(LOG_TAG, "Loading libHME-Video.so...");
                System.loadLibrary("HME-Video");
                Log.d(LOG_TAG, "Loading libHME_VideoEngine_jni.so...");
                System.loadLibrary("HME_VideoEngine_jni");
            } else {
                Log.d(LOG_TAG, "Loading libHME-Video-v6.so...");
                System.loadLibrary("HME-Video-v6");
                Log.d(LOG_TAG, "Loading libHME_VideoEngine_jni-v6.so...");
                System.loadLibrary("HME_VideoEngine_jni-v6");
            }
        } else {
            Log.d(LOG_TAG, "Loading libHME-Video-v6.so...");
            System.loadLibrary("HME-Video-v6");
            Log.d(LOG_TAG, "Loading libHME_VideoEngine_jni-v6.so...");
            System.loadLibrary("HME_VideoEngine_jni-v6");
        }
        "".getBytes();
    }
}
