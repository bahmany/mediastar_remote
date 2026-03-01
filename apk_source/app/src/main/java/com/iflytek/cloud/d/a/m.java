package com.iflytek.cloud.d.a;

import android.content.Context;
import android.text.TextUtils;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

/* loaded from: classes.dex */
public class m {
    protected static m a = null;
    private c b;

    private class a implements RecognizerListener {
        private final SpeechUnderstanderListener b;

        public a(SpeechUnderstanderListener speechUnderstanderListener) {
            this.b = speechUnderstanderListener;
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onBeginOfSpeech() {
            if (this.b != null) {
                this.b.onBeginOfSpeech();
            }
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onEndOfSpeech() {
            if (this.b != null) {
                this.b.onEndOfSpeech();
            }
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onError(SpeechError speechError) {
            if (this.b == null || speechError == null) {
                return;
            }
            this.b.onError(speechError);
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onEvent(int i, int i2, int i3, String str) {
            if (this.b != null) {
                this.b.onEvent(i, i2, i3, str);
            }
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onResult(RecognizerResult recognizerResult, boolean z) {
            if (this.b != null) {
                this.b.onResult(new UnderstanderResult(recognizerResult.getResultString()));
            }
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onVolumeChanged(int i) {
            if (this.b != null) {
                this.b.onVolumeChanged(i);
            }
        }
    }

    protected m(Context context) {
        this.b = null;
        this.b = new c(context);
    }

    public static synchronized m a(Context context) {
        if (a == null) {
            a = new m(context);
        }
        return a;
    }

    public void a(SpeechUnderstanderListener speechUnderstanderListener) {
        a aVar = new a(speechUnderstanderListener);
        if (TextUtils.isEmpty(this.b.getParameter("asr_sch"))) {
            this.b.setParameter("asr_sch", "1");
        }
        if (TextUtils.isEmpty(this.b.getParameter(SpeechConstant.NLP_VERSION))) {
            this.b.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        }
        if (TextUtils.isEmpty(this.b.getParameter(SpeechConstant.RESULT_TYPE))) {
            this.b.setParameter(SpeechConstant.RESULT_TYPE, "json");
        }
        this.b.a(aVar);
    }

    public void a(boolean z) {
        this.b.cancel(z);
    }

    public boolean a() {
        return this.b.f();
    }

    public boolean a(String str, String str2) {
        return this.b.setParameter(str, str2);
    }

    public boolean a(byte[] bArr, int i, int i2) {
        return this.b.a(bArr, i, i2);
    }

    public void b() {
        this.b.e();
    }

    public boolean c() {
        boolean zDestroy = this.b.destroy();
        if (zDestroy) {
            a = null;
        }
        return zDestroy;
    }
}
