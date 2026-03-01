package com.iflytek.cloud.d.a;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.iflytek.cloud.DataUploader;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import org.cybergarage.xml.XML;

/* loaded from: classes.dex */
public class c extends com.iflytek.cloud.a.c.e {
    private boolean f;

    final class a implements RecognizerListener {
        private RecognizerListener b;
        private boolean c = false;
        private Handler d = new f(this, Looper.getMainLooper());

        public a(RecognizerListener recognizerListener) {
            this.b = null;
            this.b = recognizerListener;
        }

        protected void a() throws Throwable {
            String strD = c.this.d.t().d("aap");
            if (!TextUtils.isEmpty(strD)) {
                com.iflytek.cloud.a.f.e.a(((com.iflytek.cloud.a.a.b) c.this.d).b(), strD);
            }
            com.iflytek.cloud.a.f.f.b(c.this.a, Boolean.valueOf(c.this.f), null);
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onBeginOfSpeech() {
            com.iflytek.cloud.a.f.a.a.a("onBeginOfSpeech");
            this.d.sendMessage(this.d.obtainMessage(2, 0, 0, null));
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onEndOfSpeech() {
            this.d.sendMessage(this.d.obtainMessage(3, 0, 0, null));
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onError(SpeechError speechError) throws Throwable {
            a();
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
        public void onResult(RecognizerResult recognizerResult, boolean z) throws Throwable {
            if (z) {
                a();
            }
            this.d.sendMessage(this.d.obtainMessage(4, !z ? 0 : 1, 0, recognizerResult));
        }

        @Override // com.iflytek.cloud.RecognizerListener
        public void onVolumeChanged(int i) {
            this.d.sendMessage(this.d.obtainMessage(1, i, 0, null));
        }
    }

    public c(Context context) {
        super(context);
        this.f = false;
    }

    public int a(RecognizerListener recognizerListener) {
        synchronized (this.c) {
            this.f = this.b.a(SpeechConstant.KEY_REQUEST_FOCUS, true);
            this.d = new com.iflytek.cloud.a.a.b(this.a, this.b, a("iat"));
            com.iflytek.cloud.a.f.f.a(this.a, Boolean.valueOf(this.f), null);
            ((com.iflytek.cloud.a.a.b) this.d).a(new a(recognizerListener));
        }
        return 0;
    }

    public int a(String str, String str2, GrammarListener grammarListener) {
        if (TextUtils.isEmpty(str2)) {
            return ErrorCode.ERROR_EMPTY_UTTERANCE;
        }
        if (TextUtils.isEmpty(str) || grammarListener == null) {
            return ErrorCode.ERROR_INVALID_PARAM;
        }
        DataUploader dataUploader = new DataUploader(this.a);
        d dVar = new d(this, grammarListener);
        dataUploader.setParameter(SpeechConstant.SUBJECT, SpeechConstant.ENG_ASR);
        dataUploader.setParameter(SpeechConstant.DATA_TYPE, str);
        String strB = this.b.b(SpeechConstant.TEXT_ENCODING, XML.CHARSET_UTF8);
        try {
            String parameter = getParameter(SpeechConstant.GRAMMAR_NAME);
            if (!TextUtils.isEmpty(parameter)) {
                str = parameter;
            }
            dataUploader.uploadData(dVar, str, str2.getBytes(strB));
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.ERROR_INVALID_PARAM;
        }
    }

    public int a(String str, String str2, LexiconListener lexiconListener) {
        if (TextUtils.isEmpty(str2)) {
            return ErrorCode.ERROR_EMPTY_UTTERANCE;
        }
        if (!TextUtils.isEmpty(str) && lexiconListener != null) {
            DataUploader dataUploader = new DataUploader(this.a);
            e eVar = new e(this, lexiconListener);
            dataUploader.setParameter(SpeechConstant.SUBJECT, "uup");
            String parameter = getParameter(SpeechConstant.LEXICON_TYPE);
            if (TextUtils.isEmpty(parameter)) {
                parameter = str;
            }
            dataUploader.setParameter(SpeechConstant.DATA_TYPE, parameter);
            try {
                dataUploader.uploadData(eVar, str, str2.getBytes(this.b.b(SpeechConstant.TEXT_ENCODING, XML.CHARSET_UTF8)));
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                return ErrorCode.ERROR_INVALID_PARAM;
            }
        }
        return ErrorCode.ERROR_INVALID_PARAM;
    }

    public boolean a(byte[] bArr, int i, int i2) {
        synchronized (this.c) {
            if (this.d == null) {
                com.iflytek.cloud.a.f.a.a.a("writeAudio error, no active session.");
                return false;
            }
            if (bArr == null || bArr.length <= 0) {
                com.iflytek.cloud.a.f.a.a.a("writeAudio error,buffer is null.");
                return false;
            }
            if (bArr.length < i2 + i) {
                com.iflytek.cloud.a.f.a.a.a("writeAudio error,buffer length < length.");
                return false;
            }
            if (((com.iflytek.cloud.a.a.b) this.d).a() != -1) {
                return false;
            }
            ((com.iflytek.cloud.a.a.b) this.d).a(bArr, i, i2);
            return true;
        }
    }

    public void b(String str) {
        synchronized (this.c) {
            if (this.d != null) {
                ((com.iflytek.cloud.a.a.b) this.d).j().a(str);
            }
        }
    }

    @Override // com.iflytek.cloud.a.c.e
    public void cancel(boolean z) {
        com.iflytek.cloud.a.f.f.b(this.a, Boolean.valueOf(this.f), null);
        super.cancel(z);
    }

    public void e() {
        synchronized (this.c) {
            if (this.d != null) {
                ((com.iflytek.cloud.a.a.b) this.d).a(true);
            }
        }
    }

    public boolean f() {
        return d();
    }
}
