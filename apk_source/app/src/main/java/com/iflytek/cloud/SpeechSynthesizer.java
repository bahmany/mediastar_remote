package com.iflytek.cloud;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.cloud.a.c.d;
import com.iflytek.msc.MSC;
import com.iflytek.speech.SpeechSynthesizerAidl;
import com.iflytek.speech.SynthesizeToUrlListener;

/* loaded from: classes.dex */
public class SpeechSynthesizer extends com.iflytek.cloud.a.c.d {
    private static SpeechSynthesizer c = null;
    InitListener a;
    private com.iflytek.cloud.d.a.g d;
    private SpeechSynthesizerAidl e;
    private a f = null;

    /* JADX INFO: Access modifiers changed from: private */
    final class a implements SynthesizerListener {
        private SynthesizerListener b;
        private com.iflytek.speech.SynthesizerListener c;
        private Handler d = new g(this, Looper.getMainLooper());

        public a(SynthesizerListener synthesizerListener) {
            this.b = null;
            this.c = null;
            this.b = synthesizerListener;
            this.c = new f(this, SpeechSynthesizer.this);
        }

        @Override // com.iflytek.cloud.SynthesizerListener
        public void onBufferProgress(int i, int i2, int i3, String str) {
            if (this.b != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("percent", i);
                bundle.putInt("begpos", i2);
                bundle.putInt("endpos", i3);
                bundle.putString("spellinfo", str);
                if (this.b != null) {
                    Message.obtain(this.d, 2, bundle).sendToTarget();
                }
            }
        }

        @Override // com.iflytek.cloud.SynthesizerListener
        public void onCompleted(SpeechError speechError) {
            if (this.b != null) {
                Message.obtain(this.d, 6, speechError).sendToTarget();
            }
        }

        @Override // com.iflytek.cloud.SynthesizerListener
        public void onSpeakBegin() {
            if (this.b != null) {
                Message.obtain(this.d, 1).sendToTarget();
            }
        }

        @Override // com.iflytek.cloud.SynthesizerListener
        public void onSpeakPaused() {
            if (this.b != null) {
                Message.obtain(this.d, 3).sendToTarget();
            }
        }

        @Override // com.iflytek.cloud.SynthesizerListener
        public void onSpeakProgress(int i, int i2, int i3) {
            if (this.b != null) {
                Message.obtain(this.d, 5, i, i2, Integer.valueOf(i3)).sendToTarget();
            }
        }

        @Override // com.iflytek.cloud.SynthesizerListener
        public void onSpeakResumed() {
            if (this.b != null) {
                Message.obtain(this.d, 4).sendToTarget();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class b implements SynthesizeToUriListener {
        private SynthesizeToUriListener b;
        private SynthesizeToUrlListener c;
        private Handler d = new i(this, Looper.getMainLooper());

        public b(SynthesizeToUriListener synthesizeToUriListener) {
            this.b = null;
            this.c = null;
            this.b = synthesizeToUriListener;
            this.c = new h(this, SpeechSynthesizer.this);
        }

        @Override // com.iflytek.cloud.SynthesizeToUriListener
        public void onBufferProgress(int i) {
            if (this.b != null) {
                Message.obtain(this.d, 1, i, 0, null).sendToTarget();
            }
        }

        @Override // com.iflytek.cloud.SynthesizeToUriListener
        public void onSynthesizeCompleted(String str, SpeechError speechError) {
            if (this.b != null) {
                Message.obtain(this.d, 2, speechError == null ? 0 : speechError.getErrorCode(), 0, str).sendToTarget();
            }
        }
    }

    protected SpeechSynthesizer(Context context, InitListener initListener) {
        this.d = null;
        this.e = null;
        this.a = null;
        this.a = initListener;
        if (MSC.isLoaded()) {
            this.d = new com.iflytek.cloud.d.a.g(context);
        }
        SpeechUtility utility = SpeechUtility.getUtility();
        if (utility != null && utility.a() && utility.getEngineMode() != d.a.MSC) {
            this.e = new SpeechSynthesizerAidl(context.getApplicationContext(), initListener);
        } else if (initListener != null) {
            initListener.onInit(0);
        }
    }

    public static SpeechSynthesizer createSynthesizer(Context context, InitListener initListener) {
        if (c == null) {
            c = new SpeechSynthesizer(context, initListener);
        }
        return c;
    }

    public static SpeechSynthesizer getSynthesizer() {
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
        this.e = new SpeechSynthesizerAidl(context.getApplicationContext(), this.a);
    }

    public boolean destroy() {
        if (this.e != null) {
            this.e.destory();
        }
        boolean zDestroy = this.d != null ? this.d.destroy() : true;
        if (zDestroy) {
            c = null;
        }
        return zDestroy;
    }

    @Override // com.iflytek.cloud.a.c.d
    public String getParameter(String str) {
        return (!SpeechConstant.LOCAL_SPEAKERS.equals(str) || this.e == null) ? super.getParameter(str) : this.e.getParameter(str);
    }

    public boolean isSpeaking() {
        if (this.d == null || !this.d.g()) {
            return this.e != null && this.e.isSpeaking();
        }
        return true;
    }

    public void pauseSpeaking() {
        if (this.d != null && this.d.g()) {
            this.d.e();
        } else {
            if (this.e == null || !this.e.isSpeaking()) {
                return;
            }
            this.e.pauseSpeaking(this.f.c);
        }
    }

    public void resumeSpeaking() {
        if (this.d != null && this.d.g()) {
            this.d.f();
        } else {
            if (this.e == null || !this.e.isSpeaking()) {
                return;
            }
            this.e.resumeSpeaking(this.f.c);
        }
    }

    @Override // com.iflytek.cloud.a.c.d
    public boolean setParameter(String str, String str2) {
        return super.setParameter(str, str2);
    }

    public int startSpeaking(String str, SynthesizerListener synthesizerListener) {
        if (a(SpeechConstant.ENG_TTS, this.e) != d.a.PLUS) {
            if (this.d == null) {
                return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
            }
            this.d.setParameter(this.b);
            return this.d.a(str, synthesizerListener);
        }
        if (this.e == null) {
            return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
        }
        this.e.setParameter(SpeechConstant.PARAMS, null);
        this.e.setParameter(SpeechConstant.PARAMS, this.b.toString());
        this.f = new a(synthesizerListener);
        return this.e.startSpeaking(str, this.f.c);
    }

    public void stopSpeaking() {
        if (this.d != null && this.d.g()) {
            this.d.a(false);
        } else {
            if (this.e == null || !this.e.isSpeaking()) {
                return;
            }
            this.e.stopSpeaking(this.f.c);
        }
    }

    public int synthesizeToUri(String str, String str2, SynthesizeToUriListener synthesizeToUriListener) {
        if (a(SpeechConstant.ENG_TTS, this.e) != d.a.PLUS) {
            if (this.d == null) {
                return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
            }
            this.d.setParameter(this.b);
            this.d.a(str, str2, synthesizeToUriListener);
            return 0;
        }
        if (this.e == null) {
            return ErrorCode.ERROR_COMPONENT_NOT_INSTALLED;
        }
        this.e.setParameter(SpeechConstant.PARAMS, null);
        this.e.setParameter(SpeechConstant.PARAMS, this.b.toString());
        return this.e.synthesizeToUrl(str, new b(synthesizeToUriListener).c);
    }
}
