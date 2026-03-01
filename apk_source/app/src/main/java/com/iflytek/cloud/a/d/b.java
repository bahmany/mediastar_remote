package com.iflytek.cloud.a.d;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import java.io.IOException;

/* loaded from: classes.dex */
public class b extends com.iflytek.cloud.a.c.a {
    private SpeechListener a;
    private com.iflytek.cloud.a.d.a b;

    private class a {
        private byte[] b;
        private String c;

        public a(byte[] bArr, String str) {
            this.b = null;
            this.c = "";
            this.b = bArr;
            this.c = str;
        }

        public byte[] a() {
            return this.b;
        }

        public String b() {
            return this.c;
        }
    }

    public b(Context context, com.iflytek.cloud.b.a aVar) {
        super(context);
        this.a = null;
        this.b = new com.iflytek.cloud.a.d.a();
        a(aVar);
    }

    public b(Context context, com.iflytek.cloud.b.a aVar, HandlerThread handlerThread) {
        super(context, handlerThread);
        this.a = null;
        this.b = new com.iflytek.cloud.a.d.a();
        a(aVar);
    }

    public SpeechError a(String str, String str2) {
        SpeechError speechError = null;
        com.iflytek.cloud.a.f.a.b.a("QMSPLogin", null);
        try {
            try {
                com.iflytek.cloud.a.d.a.a(this.r, str, str2, this);
            } catch (SpeechError e) {
                e.printStackTrace();
                if (e != null) {
                    com.iflytek.cloud.a.f.a.a.a(v() + " occur Error = " + e.toString());
                    speechError = e;
                } else {
                    speechError = e;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                SpeechError speechError2 = new SpeechError(ErrorCode.ERROR_FILE_ACCESS);
                if (speechError2 != null) {
                    com.iflytek.cloud.a.f.a.a.a(v() + " occur Error = " + speechError2.toString());
                    speechError = speechError2;
                } else {
                    speechError = speechError2;
                }
            }
            return speechError;
        } finally {
            if (speechError != null) {
                com.iflytek.cloud.a.f.a.a.a(v() + " occur Error = " + speechError.toString());
            }
        }
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void a(Message message) throws Exception {
        byte[] bArrA;
        super.a(message);
        switch (message.what) {
            case 10:
                a aVar = (a) message.obj;
                if (aVar.a() != null && aVar.a().length > 0) {
                    com.iflytek.cloud.a.f.a.b.a("QMSPUploadData", null);
                    bArrA = this.b.a(this.r, aVar.b(), aVar.a(), this);
                    break;
                } else {
                    throw new SpeechError(ErrorCode.ERROR_EMPTY_UTTERANCE);
                }
                break;
            case 11:
                com.iflytek.cloud.a.f.a.b.a("QMSPDownloadData", null);
                bArrA = this.b.a(this.r, this);
                break;
            case 12:
                String str = (String) message.obj;
                if (!TextUtils.isEmpty(str)) {
                    com.iflytek.cloud.a.f.a.b.a("QMSPSearch", null);
                    bArrA = this.b.a(this.r, this, str);
                    break;
                } else {
                    throw new SpeechError(ErrorCode.ERROR_EMPTY_UTTERANCE);
                }
            default:
                bArrA = null;
                break;
        }
        if (bArrA == null) {
            throw new SpeechError(ErrorCode.ERROR_INVALID_RESULT);
        }
        if (this.a != null) {
            this.a.onData(bArrA);
        }
        c(null);
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void a(SpeechError speechError) {
        super.a(speechError);
        if (this.a == null || this.s) {
            return;
        }
        this.a.onCompleted(speechError);
    }

    public void a(SpeechListener speechListener) {
        this.a = speechListener;
        a(11);
    }

    public void a(SpeechListener speechListener, String str) {
        this.a = speechListener;
        d(obtainMessage(12, str));
    }

    public void a(SpeechListener speechListener, String str, byte[] bArr) {
        this.a = speechListener;
        d(obtainMessage(10, new a(bArr, str)));
    }
}
