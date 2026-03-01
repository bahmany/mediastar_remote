package com.iflytek.cloud.d.a;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.iflytek.cloud.DataDownloader;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import java.util.Random;

/* loaded from: classes.dex */
public class a extends com.iflytek.cloud.a.c.e {
    private boolean f;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.iflytek.cloud.d.a.a$a, reason: collision with other inner class name */
    final class C0148a implements VerifierListener {
        private VerifierListener b;
        private Handler c = new b(this, Looper.getMainLooper());

        public C0148a(VerifierListener verifierListener) {
            this.b = null;
            this.b = verifierListener;
        }

        protected void a() throws Throwable {
            String strD = a.this.d.t().d("aap");
            if (!TextUtils.isEmpty(strD)) {
                com.iflytek.cloud.a.f.e.a(((com.iflytek.cloud.a.b.a) a.this.d).f(), strD);
            }
            com.iflytek.cloud.a.f.f.b(a.this.a, Boolean.valueOf(a.this.f), null);
        }

        @Override // com.iflytek.cloud.VerifierListener
        public void onBeginOfSpeech() {
            this.c.sendMessage(this.c.obtainMessage(2, 0, 0, null));
        }

        @Override // com.iflytek.cloud.VerifierListener
        public void onEndOfSpeech() {
            this.c.sendMessage(this.c.obtainMessage(3, 0, 0, null));
        }

        @Override // com.iflytek.cloud.VerifierListener
        public void onError(SpeechError speechError) throws Throwable {
            a();
            this.c.sendMessage(this.c.obtainMessage(0, speechError));
        }

        @Override // com.iflytek.cloud.VerifierListener
        public void onResult(VerifierResult verifierResult) throws Throwable {
            a();
            this.c.sendMessage(this.c.obtainMessage(4, verifierResult));
        }

        @Override // com.iflytek.cloud.VerifierListener
        public void onVolumeChanged(int i, byte[] bArr) {
            this.c.sendMessage(this.c.obtainMessage(1, i, 0, bArr));
        }
    }

    public a(Context context) {
        super(context);
        this.f = false;
    }

    public String a(int i) {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();
        String str = "023456789".charAt(random.nextInt("023456789".length())) + "";
        stringBuffer.append(str);
        for (int i2 = 0; i2 < i - 1; i2++) {
            Boolean bool = false;
            while (!bool.booleanValue()) {
                str = "023456789".charAt(random.nextInt("023456789".length())) + "";
                bool = stringBuffer.indexOf(str) >= 0 ? false : Integer.parseInt(new StringBuilder().append(stringBuffer.charAt(stringBuffer.length() + (-1))).append("").toString()) * Integer.parseInt(str) != 10;
            }
            stringBuffer.append(str);
        }
        return stringBuffer.toString();
    }

    public void a(SpeechListener speechListener, String str, String str2) {
        com.iflytek.cloud.b.a aVar = new com.iflytek.cloud.b.a(str2, (String[][]) null);
        aVar.a("rse", "gb2312", false);
        aVar.a("sub", SpeechConstant.ENG_IVP, false);
        aVar.a("ent", str, false);
        DataDownloader dataDownloader = new DataDownloader(this.a);
        dataDownloader.setParameter(SpeechConstant.PARAMS, aVar.toString());
        dataDownloader.downloadData(speechListener);
    }

    public void a(VerifierListener verifierListener) {
        synchronized (this.c) {
            this.f = this.b.a(SpeechConstant.KEY_REQUEST_FOCUS, true);
            this.d = new com.iflytek.cloud.a.b.a(this.a, this.b, a("verify"));
            com.iflytek.cloud.a.f.f.a(this.a, Boolean.valueOf(this.f), null);
            ((com.iflytek.cloud.a.b.a) this.d).a(new C0148a(verifierListener));
        }
    }

    public boolean a(byte[] bArr, int i, int i2) {
        boolean z = false;
        synchronized (this.c) {
            if (this.d == null) {
                com.iflytek.cloud.a.f.a.a.a("writeAudio error, no active session.");
            } else if (bArr == null || bArr.length <= 0) {
                com.iflytek.cloud.a.f.a.a.a("writeAudio error,buffer is null.");
            } else if (bArr.length < i2 + i) {
                com.iflytek.cloud.a.f.a.a.a("writeAudio error,buffer length < length.");
            } else {
                ((com.iflytek.cloud.a.b.a) this.d).a(bArr, i, i2);
                z = true;
            }
        }
        return z;
    }

    public void e() {
        synchronized (this.c) {
            if (this.d != null) {
                ((com.iflytek.cloud.a.b.a) this.d).a();
            }
        }
    }

    public boolean f() {
        return d();
    }
}
