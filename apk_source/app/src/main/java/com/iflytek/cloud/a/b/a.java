package com.iflytek.cloud.a.b;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import com.iflytek.cloud.a.c.a;
import com.iflytek.cloud.a.f.h;
import com.iflytek.cloud.c.e;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.cybergarage.xml.XML;

/* loaded from: classes.dex */
public class a extends com.iflytek.cloud.a.c.a implements e.a {
    protected volatile VerifierListener a;
    protected long b;
    protected boolean c;
    protected b d;
    protected e e;
    protected String f;
    protected String g;
    protected VerifierResult h;
    protected ConcurrentLinkedQueue<byte[]> i;
    protected int j;
    private long k;

    public a(Context context, com.iflytek.cloud.b.a aVar, HandlerThread handlerThread) {
        super(context, handlerThread);
        this.a = null;
        this.b = 0L;
        this.c = true;
        this.d = new b();
        this.e = null;
        this.f = "train";
        this.g = "";
        this.h = null;
        this.i = null;
        this.j = 1;
        this.k = 0L;
        this.i = new ConcurrentLinkedQueue<>();
        a(aVar);
    }

    private boolean g() {
        return "train".equalsIgnoreCase(t().d("sst"));
    }

    private void j() throws InterruptedException, SpeechError, IOException {
        com.iflytek.cloud.a.f.a.a.a("--->onStoped: in");
        if (!g()) {
            x();
        }
        this.d.a();
        a(4);
        com.iflytek.cloud.a.f.a.a.a("--->onStoped: out");
    }

    private void l() throws SpeechError, UnsupportedEncodingException {
        com.iflytek.cloud.a.f.a.a.a("--->requestResult: in");
        switch (this.d.e()) {
            case hasResult:
                w();
                break;
        }
        com.iflytek.cloud.a.f.a.a.a("--->requestResult: out");
    }

    private void w() throws SpeechError, UnsupportedEncodingException {
        this.t = SystemClock.elapsedRealtime();
        this.h = new VerifierResult(new String(this.d.d(), XML.CHARSET_UTF8));
        com.iflytek.cloud.a.f.a.a.b("result-->" + this.h.source);
        if (this.f.equals("train") && this.h.ret && this.h.suc < this.h.rgn) {
            if (this.a != null) {
                this.a.onResult(this.h);
            }
            a(0);
        } else {
            if (this.a != null) {
                this.a.onResult(this.h);
            }
            c((SpeechError) null);
        }
    }

    private void x() {
        if (this.e != null) {
            this.e.a();
            this.e = null;
        }
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void a(Message message) throws Exception {
        super.a(message);
        switch (message.what) {
            case 0:
                b();
                break;
            case 1:
                d();
                break;
            case 2:
                b(message);
                break;
            case 3:
                j();
                break;
            case 4:
                c(message);
                break;
            case 9:
                com.iflytek.cloud.a.f.a.a.a("--->on timeout vad");
                e();
                break;
        }
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void a(SpeechError speechError) {
        com.iflytek.cloud.a.f.a.a.a("--->onEnd: in");
        x();
        if (this.s) {
            this.d.a("user abort");
        } else if (speechError != null) {
            this.d.a("error" + speechError.getErrorCode());
        } else {
            this.d.a("success");
        }
        super.a(speechError);
        if (this.a != null && !this.s) {
            com.iflytek.cloud.a.f.a.a.a("VerifyListener#onEnd");
            if (speechError != null) {
                this.a.onError(speechError);
            }
        }
        com.iflytek.cloud.a.f.a.a.a("--->onEnd: out");
    }

    public synchronized void a(VerifierListener verifierListener) {
        com.iflytek.cloud.a.f.a.a.a("--->startVerify: in");
        this.a = verifierListener;
        c();
        com.iflytek.cloud.a.f.a.a.a("--->startVerify: out");
    }

    public void a(byte[] bArr, int i) {
        if (r()) {
            this.a.onVolumeChanged(i, bArr);
        }
    }

    @Override // com.iflytek.cloud.c.e.a
    public void a(byte[] bArr, int i, int i2) {
        if (a.b.recording == s() && i2 > 0) {
            byte[] bArr2 = new byte[i2];
            System.arraycopy(bArr, i, bArr2, 0, i2);
            d(obtainMessage(2, bArr2));
        }
    }

    protected void a(byte[] bArr, boolean z) throws SpeechError {
        this.d.a(bArr, bArr.length);
        if (z) {
            if (this.d.b()) {
                com.iflytek.cloud.a.f.a.a.a("---> VadCheck Time: Vad End Point");
                e();
            } else {
                int iC = this.d.c();
                com.iflytek.cloud.a.f.a.a.a("QISRAudioWrite volume:" + iC);
                a(bArr, iC);
            }
        }
    }

    public synchronized boolean a() {
        boolean z;
        com.iflytek.cloud.a.f.a.a.a("--->stopRecord: in");
        if (s() != a.b.recording) {
            com.iflytek.cloud.a.f.a.a.a("endVerify fail  status is :" + s());
            z = false;
        } else {
            if (this.e != null && !g()) {
                this.e.a();
            }
            a(3);
            com.iflytek.cloud.a.f.a.a.a("--->stopRecord: out");
            z = true;
        }
        return z;
    }

    protected void b() throws Exception {
        com.iflytek.cloud.a.f.a.a.a("--->onStart: in");
        if (SpeechConstant.TYPE_CLOUD.equals(t().d(SpeechConstant.ENGINE_TYPE))) {
            h.a(this.r);
        }
        int iA = t().a("record_read_rate", 40);
        if (this.j != -1 && r()) {
            com.iflytek.cloud.a.f.a.a.a("start  record");
            if (this.e == null) {
                this.e = new e(q(), iA, this.j);
                this.e.a(this);
            }
        }
        if (s() != a.b.exiting && this.a != null) {
            this.a.onBeginOfSpeech();
        }
        this.b = SystemClock.elapsedRealtime();
        removeMessages(9);
        a(9, a.EnumC0145a.min, false, this.p);
        a(1, a.EnumC0145a.max, false, 0);
        com.iflytek.cloud.a.f.a.a.a("--->onStart: out");
    }

    protected void b(Message message) throws Exception {
        byte[] bArr = (byte[]) message.obj;
        if (bArr == null || bArr.length == 0) {
            return;
        }
        this.i.add(bArr);
        a(bArr, true);
    }

    @Override // com.iflytek.cloud.c.e.a
    public void b(SpeechError speechError) {
        c(speechError);
    }

    @Override // com.iflytek.cloud.a.c.a
    public void b(boolean z) {
        if (z && r() && this.a != null) {
            this.a.onError(new SpeechError(ErrorCode.ERROR_INTERRUPT));
        }
        if (this.e != null) {
            this.e.a();
        }
        super.b(z);
    }

    void c(Message message) throws InterruptedException, SpeechError, UnsupportedEncodingException {
        if (!g()) {
            x();
        }
        l();
        if (s() == a.b.waitresult) {
            a(4, a.EnumC0145a.normal, false, 20);
        }
    }

    @Override // com.iflytek.cloud.c.e.a
    public void c(boolean z) {
        com.iflytek.cloud.a.f.a.a.a("time cost: onRecordStarted:" + (SystemClock.elapsedRealtime() - this.k));
    }

    protected void d() throws Exception {
        if (this.d.a == null) {
            this.d.a(this.r, this.g, this);
        }
        a(a.b.recording);
    }

    public void e() {
        if (a.b.recording == s()) {
            com.iflytek.cloud.a.f.a.a.a("--->vadEndCall: out");
            a();
            if (this.a != null) {
                this.a.onEndOfSpeech();
            }
        }
    }

    public ConcurrentLinkedQueue<byte[]> f() {
        return this.i;
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void h() {
        this.p = t().a(SpeechConstant.KEY_SPEECH_TIMEOUT, this.p);
        this.g = t().d("vid");
        this.j = t().a(SpeechConstant.AUDIO_SOURCE, 1);
        com.iflytek.cloud.a.f.a.a.a("mSpeechTimeOut=" + this.p);
        super.h();
    }

    @Override // com.iflytek.cloud.c.e.a
    public void k() {
    }
}
