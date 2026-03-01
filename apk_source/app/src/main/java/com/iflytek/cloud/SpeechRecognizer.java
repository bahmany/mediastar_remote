package com.iflytek.cloud;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.cloud.a.c.d;
import com.iflytek.msc.MSC;
import com.iflytek.speech.SpeechRecognizerAidl;

/* loaded from: classes.dex */
public final class SpeechRecognizer extends com.iflytek.cloud.a.c.d {
    private static SpeechRecognizer c = null;
    InitListener a;
    private com.iflytek.cloud.d.a.c d;
    private SpeechRecognizerAidl e;
    private a f = null;

    final class a implements RecognizerListener {
        private RecognizerListener b;
        private com.iflytek.speech.RecognizerListener c;
        private Handler d = new e(this, Looper.getMainLooper());

        public a(RecognizerListener recognizerListener) {
            this.b = null;
            this.c = null;
            this.b = recognizerListener;
            this.c = new d(this, SpeechRecognizer.this);
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onBeginOfSpeech() {
            this.d.sendMessage(this.d.obtainMessage(2, 0, 0, null));
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onEndOfSpeech() {
            this.d.sendMessage(this.d.obtainMessage(3, 0, 0, null));
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onError(SpeechError speechError) {
            this.d.sendMessage(this.d.obtainMessage(0, speechError));
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onEvent(int i, int i2, int i3, String str) {
            Message message = new Message();
            message.what = i;
            message.arg1 = i2;
            message.arg2 = i3;
            message.obj = str;
            this.d.sendMessage(this.d.obtainMessage(6, 0, 0, message));
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onResult(RecognizerResult recognizerResult, boolean z) {
            this.d.sendMessage(this.d.obtainMessage(4, !z ? 0 : 1, 0, recognizerResult));
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onVolumeChanged(int i) {
            this.d.sendMessage(this.d.obtainMessage(1, i, 0, null));
        }
    }

    protected SpeechRecognizer(Context context, InitListener initListener) {
        this.d = null;
        this.e = null;
        this.a = null;
        this.a = initListener;
        if (MSC.isLoaded()) {
            this.d = new com.iflytek.cloud.d.a.c(context);
        }
        SpeechUtility utility = SpeechUtility.getUtility();
        if (utility != null && utility.a() && utility.getEngineMode() != d.a.MSC) {
            this.e = new SpeechRecognizerAidl(context.getApplicationContext(), initListener);
        } else if (initListener != null) {
            initListener.onInit(0);
        }
    }

    public static synchronized SpeechRecognizer createRecognizer(Context context, InitListener initListener) {
        if (c == null) {
            c = new SpeechRecognizer(context, initListener);
        }
        return c;
    }

    public static SpeechRecognizer getRecognizer() {
        return c;
    }

    protected void a(Context context) {
        SpeechUtility utility = SpeechUtility.getUtility();
        if (utility == null || !utility.a() || utility.getEngineMode() == d.a.MSC) {
            if (this.a == null || this.e == null) {
                return;
            }
            this.e.destory();
            this.e = null;
            return;
        }
        if (this.e != null && !this.e.isAvailable()) {
            this.e.destory();
            this.e = null;
        }
        this.e = new SpeechRecognizerAidl(context.getApplicationContext(), this.a);
    }

    public int buildGrammar(String str, String str2, GrammarListener grammarListener) {
        d.a aVarA = a(SpeechConstant.ENG_ASR, this.e);
        com.iflytek.cloud.a.f.a.a.a("start engine mode = " + aVarA.toString());
        if (aVarA != d.a.PLUS) {
            if (this.d == null) {
                return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
            }
            this.d.setParameter(this.b);
            return this.d.a(str, str2, grammarListener);
        }
        if (this.e == null) {
            return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
        }
        this.e.setParameter(SpeechConstant.PARAMS, null);
        this.e.setParameter(SpeechConstant.PARAMS, this.b.toString());
        return this.e.buildGrammar(str, str2, new b(this, grammarListener));
    }

    public void cancel() {
        if (this.d != null && this.d.f()) {
            this.d.cancel(false);
        } else if (this.e == null || !this.e.isListening()) {
            com.iflytek.cloud.a.f.a.a.b("SpeechRecognizer cancel failed, is not running");
        } else {
            this.e.cancel(this.f.c);
        }
    }

    public boolean destroy() {
        if (this.e != null) {
            this.e.destory();
            this.e = null;
        }
        boolean zDestroy = this.d != null ? this.d.destroy() : true;
        if (zDestroy) {
            c = null;
        }
        return zDestroy;
    }

    @Override // com.iflytek.cloud.a.c.d
    public String getParameter(String str) {
        return super.getParameter(str);
    }

    public boolean isListening() {
        if (this.d == null || !this.d.f()) {
            return this.e != null && this.e.isListening();
        }
        return true;
    }

    @Override // com.iflytek.cloud.a.c.d
    public boolean setParameter(String str, String str2) {
        return super.setParameter(str, str2);
    }

    public int startListening(RecognizerListener recognizerListener) {
        d.a aVarA = a(SpeechConstant.ENG_ASR, this.e);
        com.iflytek.cloud.a.f.a.a.a("start engine mode = " + aVarA.toString());
        if (aVarA != d.a.PLUS) {
            if (this.d == null) {
                return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
            }
            this.d.setParameter(this.b);
            return this.d.a(recognizerListener);
        }
        if (this.e == null) {
            return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
        }
        this.e.setParameter(SpeechConstant.PARAMS, null);
        this.e.setParameter(SpeechConstant.PARAMS, this.b.toString());
        this.f = new a(recognizerListener);
        return this.e.startListening(this.f.c);
    }

    public void stopListening() {
        if (this.d != null && this.d.f()) {
            this.d.e();
        } else if (this.e == null || !this.e.isListening()) {
            com.iflytek.cloud.a.f.a.a.b("SpeechRecognizer stopListening failed, is not running");
        } else {
            this.e.stopListening(this.f.c);
        }
    }

    public int updateLexicon(String str, String str2, LexiconListener lexiconListener) {
        d.a aVarA = a(SpeechConstant.ENG_ASR, this.e);
        com.iflytek.cloud.a.f.a.a.a("start engine mode = " + aVarA.toString());
        if (aVarA != d.a.PLUS) {
            if (this.d == null) {
                return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
            }
            this.d.setParameter(this.b);
            return this.d.a(str, str2, lexiconListener);
        }
        if (this.e == null) {
            return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
        }
        this.e.setParameter(SpeechConstant.PARAMS, null);
        this.e.setParameter(SpeechConstant.PARAMS, this.b.toString());
        return this.e.updateLexicon(str, str2, new c(this, lexiconListener));
    }

    public boolean writeAudio(byte[] bArr, int i, int i2) {
        if (this.d != null && this.d.f()) {
            return this.d.a(bArr, i, i2);
        }
        if (this.e != null && this.e.isListening()) {
            return this.e.writeAudio(bArr, i, i2) == 0;
        }
        com.iflytek.cloud.a.f.a.a.b("SpeechRecognizer writeAudio failed, is not running");
        return false;
    }
}
