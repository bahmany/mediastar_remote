package com.iflytek.speech;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.speech.aidl.IWakeuper;

/* loaded from: classes.dex */
public class VoiceWakeuperAidl extends SpeechModuleAidl<IWakeuper> {
    public static final String ARG_RES_PROVIDER_AUTHORITY = "content_provider_name";
    public static final String ARG_RES_TYPE = "engine_res_type";
    public static final String IVP_RES_ID = "ivp_res_id";
    public static final String IVP_USER_NAME = "ivp_user_name";
    public static final String IVW_AND_IVP = "ivw_and_ivp";
    public static final String IVW_FILES = "ivw_files";
    public static final String IVW_THRESHOLD = "ivw_threshold";
    public static final String IVW_WORD_ID = "ivw_word_id";
    public static final String PARAMS_SEPARATE = ";";
    public static final String PROVIDER_NAME = "com.iflytek.speech.SharedProvider";
    public static final int RES_FROM_ASSETS = 257;
    public static final int RES_FROM_CLIENT = 259;
    public static final int RES_SPECIFIED = 258;
    public static final String WAKEUP_RESULT_ID = "wakeup_result_id";
    public static final String WAKEUP_RESULT_SCORE = "wakeup_result_Score";

    public VoiceWakeuperAidl(Context context, InitListener initListener) {
        super(context, initListener, UtilityConfig.ACTION_SPEECH_WAKEUP);
    }

    public int cancel(WakeuperListener wakeuperListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (wakeuperListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            ((IWakeuper) this.mService).cancel(wakeuperListener);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public /* bridge */ /* synthetic */ boolean destory() {
        return super.destory();
    }

    public boolean destroy() {
        if (this.mService == 0) {
            return false;
        }
        try {
            ((IWakeuper) this.mService).destroy();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return super.destory();
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public /* bridge */ /* synthetic */ Intent getIntent() {
        return super.getIntent();
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public String getParameter(String str) {
        return super.getParameter(str);
    }

    @Override // com.iflytek.speech.SpeechModuleAidl
    public /* bridge */ /* synthetic */ boolean isActionInstalled(Context context, String str) {
        return super.isActionInstalled(context, str);
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public /* bridge */ /* synthetic */ boolean isAvailable() {
        return super.isAvailable();
    }

    public boolean isListening() {
        try {
            return ((IWakeuper) this.mService).isListening();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public int setParameter(String str, String str2) {
        return super.setParameter(str, str2);
    }

    public int startListening(WakeuperListener wakeuperListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (wakeuperListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            ((IWakeuper) this.mService).startListening(getIntent(), wakeuperListener);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }
}
