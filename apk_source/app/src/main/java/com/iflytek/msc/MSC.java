package com.iflytek.msc;

import com.iflytek.cloud.SpeechConstant;

/* loaded from: classes.dex */
public class MSC {
    private static boolean a;

    static {
        a = false;
        try {
            System.loadLibrary(SpeechConstant.MODE_MSC);
            a = true;
        } catch (UnsatisfiedLinkError e) {
            a = false;
        }
    }

    public static native int DebugLog(boolean z);

    public static native int QHCRDataWrite(char[] cArr, byte[] bArr, byte[] bArr2, int i, int i2);

    public static native int QHCRFini();

    public static native byte[] QHCRGetResult(char[] cArr, byte[] bArr, MSCSessionInfo mSCSessionInfo);

    public static native int QHCRInit(byte[] bArr);

    public static native int QHCRLogEvent(char[] cArr, byte[] bArr, byte[] bArr2);

    public static native char[] QHCRSessionBegin(byte[] bArr, MSCSessionInfo mSCSessionInfo);

    public static native int QHCRSessionEnd(char[] cArr, byte[] bArr);

    public static native int QISEAudioWrite(char[] cArr, byte[] bArr, int i, int i2, MSCSessionInfo mSCSessionInfo);

    public static native int QISEFini();

    public static native int QISEGetParam(char[] cArr, byte[] bArr, MSCSessionInfo mSCSessionInfo);

    public static final native byte[] QISEGetResult(char[] cArr, MSCSessionInfo mSCSessionInfo);

    public static native int QISEInit(byte[] bArr);

    public static final native char[] QISESessionBegin(byte[] bArr, byte[] bArr2, MSCSessionInfo mSCSessionInfo);

    public static native int QISESessionEnd(char[] cArr, byte[] bArr);

    public static native int QISETextPut(char[] cArr, byte[] bArr, byte[] bArr2);

    public static native int QISRAudioWrite(char[] cArr, byte[] bArr, int i, int i2, MSCSessionInfo mSCSessionInfo);

    public static native int QISRFini();

    public static native int QISRGetParam(char[] cArr, byte[] bArr, MSCSessionInfo mSCSessionInfo);

    public static final native byte[] QISRGetResult(char[] cArr, MSCSessionInfo mSCSessionInfo);

    public static native int QISRGrammarActivate(char[] cArr, byte[] bArr, byte[] bArr2);

    public static native int QISRInit(byte[] bArr);

    public static native int QISRLogEvent(char[] cArr, byte[] bArr, byte[] bArr2);

    public static native int QISRRegisterNotify(char[] cArr, String str, String str2, String str3, Object obj);

    public static final native char[] QISRSessionBegin(byte[] bArr, byte[] bArr2, MSCSessionInfo mSCSessionInfo);

    public static native int QISRSessionEnd(char[] cArr, byte[] bArr);

    public static native int QISRSetParam(char[] cArr, byte[] bArr, byte[] bArr2);

    public static native byte[] QISRUploadData(char[] cArr, byte[] bArr, byte[] bArr2, int i, byte[] bArr3, MSCSessionInfo mSCSessionInfo);

    public static native int QISVAudioWrite(char[] cArr, byte[] bArr, int i, int i2, MSCSessionInfo mSCSessionInfo);

    public static native int QISVFini();

    public static native int QISVGetParam(char[] cArr, byte[] bArr, MSCSessionInfo mSCSessionInfo);

    public static native byte[] QISVGetResult(char[] cArr, MSCSessionInfo mSCSessionInfo);

    public static native int QISVInit(byte[] bArr);

    public static native char[] QISVSessionBegin(byte[] bArr, byte[] bArr2, MSCSessionInfo mSCSessionInfo);

    public static native int QISVSessionEnd(char[] cArr, byte[] bArr);

    public static native int QMSPDownload(byte[] bArr, byte[] bArr2, Object obj);

    public static native byte[] QMSPDownloadData(byte[] bArr, MSCSessionInfo mSCSessionInfo);

    public static native int QMSPGetParam(byte[] bArr, MSCSessionInfo mSCSessionInfo);

    public static native int QMSPLogOut();

    public static native int QMSPLogin(byte[] bArr, byte[] bArr2, byte[] bArr3);

    public static native int QMSPRegisterNotify(String str, String str2);

    public static native byte[] QMSPSearch(byte[] bArr, byte[] bArr2, MSCSessionInfo mSCSessionInfo);

    public static native int QMSPSetParam(byte[] bArr, byte[] bArr2);

    public static native byte[] QMSPUploadData(byte[] bArr, byte[] bArr2, int i, byte[] bArr3, MSCSessionInfo mSCSessionInfo);

    public static native byte[] QTTSAudioGet(char[] cArr, MSCSessionInfo mSCSessionInfo);

    public static native char[] QTTSAudioInfo(char[] cArr);

    public static native int QTTSFini();

    public static native int QTTSGetParam(char[] cArr, byte[] bArr, MSCSessionInfo mSCSessionInfo);

    public static native int QTTSInit(byte[] bArr);

    public static final native char[] QTTSSessionBegin(byte[] bArr, MSCSessionInfo mSCSessionInfo);

    public static native int QTTSSessionEnd(char[] cArr, byte[] bArr);

    public static native int QTTSTextPut(char[] cArr, byte[] bArr);

    public static native int UMSPLogin(byte[] bArr, byte[] bArr2, byte[] bArr3, Object obj);

    public static boolean isLoaded() {
        return a;
    }
}
