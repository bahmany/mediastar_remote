package com.iflytek.speech;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.speech.aidl.ISpeakerVerifier;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class SpeakerVerifierAidl extends SpeechModuleAidl<ISpeakerVerifier> {
    private final String TAG;
    public static String USER_NAME = "name";
    public static String IVP_SENTENCE_CNT = "count";
    public static String VID = "vid";
    public static String IVP_PARAM_CONSISTTHRESHOLD = "consistThreshold";
    public static String IVP_PARAM_DTW_CHECK_THRESHOLD = "checkThreshold";

    public interface DownloadListener {
        void onData(ArrayList<PassWord> arrayList);

        void onError(int i);
    }

    public class PassWord {
        public String pwdt = "";
        public String pwid = "";
        public String pwtext = "";

        public PassWord() {
        }
    }

    public SpeakerVerifierAidl(Context context, InitListener initListener) {
        super(context, initListener, UtilityConfig.ACTION_SPEAKER_VERIFIER);
        this.TAG = "SpeakerVerifier";
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public /* bridge */ /* synthetic */ boolean destory() {
        return super.destory();
    }

    public void endSpeak() {
        try {
            Log.d("SpeakerVerifier", "SpeakerVerifier | endSpeak");
            ((ISpeakerVerifier) this.mService).endSpeak();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public /* bridge */ /* synthetic */ Intent getIntent() {
        return super.getIntent();
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public String getParameter(String str) {
        return null;
    }

    public int getPasswordList(Context context, DownloadListener downloadListener, String str) {
        return 0;
    }

    public int identify(String str, String str2, VerifierListener verifierListener) {
        return 0;
    }

    @Override // com.iflytek.speech.SpeechModuleAidl
    public /* bridge */ /* synthetic */ boolean isActionInstalled(Context context, String str) {
        return super.isActionInstalled(context, str);
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public /* bridge */ /* synthetic */ boolean isAvailable() {
        return super.isAvailable();
    }

    public int register(String str, String str2, VerifierListener verifierListener) {
        Log.d("SpeakerVerifier", "SpeakerVerifier | register");
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (verifierListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            ((ISpeakerVerifier) this.mService).register(str, str2, verifierListener);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public int setParameter(String str, String str2) {
        return 0;
    }

    public void stopSpeak() {
        try {
            Log.d("SpeakerVerifier", "SpeakerVerifier | stopSpeak");
            ((ISpeakerVerifier) this.mService).stopSpeak();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int verify(String str, String str2, VerifierListener verifierListener) {
        Log.d("SpeakerVerifier", "SpeakerVerifier | verify");
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (verifierListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            ((ISpeakerVerifier) this.mService).verify(str, str2, verifierListener);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }
}
