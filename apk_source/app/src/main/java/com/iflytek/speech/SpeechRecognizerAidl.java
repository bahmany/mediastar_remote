package com.iflytek.speech;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.speech.aidl.ISpeechRecognizer;

/* loaded from: classes.dex */
public class SpeechRecognizerAidl extends SpeechModuleAidl<ISpeechRecognizer> {
    public SpeechRecognizerAidl(Context context, InitListener initListener) {
        super(context, initListener, UtilityConfig.ACTION_SPEECH_RECOGNIZER);
    }

    public int buildGrammar(String str, String str2, GrammarListener grammarListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (grammarListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            Intent intent = getIntent();
            intent.putExtra(SpeechConstant.GRAMMAR_TYPE, str);
            intent.putExtra(SpeechConstant.GRAMMAR_CONTENT, str2);
            ((ISpeechRecognizer) this.mService).buildGrammar(intent, grammarListener);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    public int cancel(RecognizerListener recognizerListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (recognizerListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            ((ISpeechRecognizer) this.mService).cancel(recognizerListener);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
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
            if (this.mService != 0) {
                return ((ISpeechRecognizer) this.mService).isListening();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override // com.iflytek.speech.SpeechModuleAidl, com.iflytek.speech.ISpeechModule
    public int setParameter(String str, String str2) {
        return super.setParameter(str, str2);
    }

    public int startListening(RecognizerListener recognizerListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (recognizerListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            ((ISpeechRecognizer) this.mService).startListening(getIntent(), recognizerListener);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    public int stopListening(RecognizerListener recognizerListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (recognizerListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            ((ISpeechRecognizer) this.mService).stopListening(recognizerListener);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    public int updateLexicon(String str, String str2, LexiconListener lexiconListener) {
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        if (lexiconListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        try {
            Intent intent = getIntent();
            intent.putExtra(SpeechConstant.LEXICON_NAME, str);
            intent.putExtra(SpeechConstant.LEXICON_CONTENT, str2);
            ((ISpeechRecognizer) this.mService).updateLexicon(intent, lexiconListener);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_CALL_FAIL;
        }
    }

    public int writeAudio(byte[] bArr, int i, int i2) {
        if (SpeechUtility.getUtility().getServiceVersion() < 44) {
            return ErrorCode.ERROR_VERSION_LOWER;
        }
        if (this.mService == 0) {
            return ErrorCode.ERROR_ENGINE_INIT_FAIL;
        }
        try {
            ((ISpeechRecognizer) this.mService).writeAudio(getIntent(), bArr, i, i2);
            return 0;
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
