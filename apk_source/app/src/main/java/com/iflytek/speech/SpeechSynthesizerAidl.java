package com.iflytek.speech;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.speech.aidl.ISpeechSynthesizer;

/* loaded from: classes.dex */
public class SpeechSynthesizerAidl extends SpeechModuleAidl<ISpeechSynthesizer> {
    public SpeechSynthesizerAidl(Context context, InitListener initListener) {
        super(context, initListener, UtilityConfig.ACTION_SPEECH_SYNTHESIZER);
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public boolean destory() {
        this.mService = null;
        return super.destory();
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public /* bridge */ /* synthetic */ Intent getIntent() {
        return super.getIntent();
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public String getParameter(String str) {
        int serviceVersion;
        if (!str.equals(SpeechConstant.LOCAL_SPEAKERS)) {
            return super.getParameter(str);
        }
        try {
            if (SpeechUtility.getUtility() == null || (serviceVersion = SpeechUtility.getUtility().getServiceVersion()) < 44) {
                return null;
            }
            if (serviceVersion < 10000 || serviceVersion >= 10013) {
                return ((ISpeechSynthesizer) this.mService).getLocalSpeakerList();
            }
            return null;
        } catch (RemoteException e) {
            e.printStackTrace();
            return "20999";
        }
    }

    @Override // com.iflytek.speech.SpeechModuleAidl
    public /* bridge */ /* synthetic */ boolean isActionInstalled(Context context, String str) {
        return super.isActionInstalled(context, str);
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public /* bridge */ /* synthetic */ boolean isAvailable() {
        return super.isAvailable();
    }

    public boolean isSpeaking() {
        try {
            if (this.mService != 0) {
                return ((ISpeechSynthesizer) this.mService).isSpeaking();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int pauseSpeaking(SynthesizerListener synthesizerListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (synthesizerListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            return ((ISpeechSynthesizer) this.mService).pauseSpeaking(synthesizerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    public int resumeSpeaking(SynthesizerListener synthesizerListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (synthesizerListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            return ((ISpeechSynthesizer) this.mService).resumeSpeaking(synthesizerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public int setParameter(String str, String str2) {
        return super.setParameter(str, str2);
    }

    public int startSpeaking(String str, SynthesizerListener synthesizerListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (synthesizerListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            Intent intent = getIntent();
            intent.putExtra("text", str);
            return ((ISpeechSynthesizer) this.mService).startSpeaking(intent, synthesizerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    public int stopSpeaking(SynthesizerListener synthesizerListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (synthesizerListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            return ((ISpeechSynthesizer) this.mService).stopSpeaking(synthesizerListener);
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    public int synthesizeToUrl(String str, SynthesizeToUrlListener synthesizeToUrlListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (synthesizeToUrlListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            Intent intent = getIntent();
            intent.putExtra("text", str);
            return ((ISpeechSynthesizer) this.mService).synthesizeToUrl(intent, synthesizeToUrlListener);
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }
}
