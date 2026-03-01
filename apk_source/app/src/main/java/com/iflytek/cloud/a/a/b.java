package com.iflytek.cloud.a.a;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.a.c.a;
import com.iflytek.cloud.a.c.c;
import com.iflytek.cloud.a.f.h;
import com.iflytek.cloud.c.e;
import com.iflytek.msc.MSC;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.cybergarage.xml.XML;

/* loaded from: classes.dex */
public class b extends com.iflytek.cloud.a.c.a implements e.a {
    public static int j = 0;
    public static int k = 0;
    protected volatile RecognizerListener a;
    protected boolean b;
    protected boolean c;
    protected boolean d;
    protected boolean e;
    protected int f;
    protected boolean g;
    protected a h;
    protected e i;
    protected String l;
    protected ConcurrentLinkedQueue<byte[]> m;
    protected ArrayList<String> n;
    protected c o;
    private int v;

    public b(Context context, com.iflytek.cloud.b.a aVar, HandlerThread handlerThread) {
        super(context, handlerThread);
        this.a = null;
        this.b = false;
        this.c = false;
        this.d = false;
        this.e = false;
        this.f = 1;
        this.g = true;
        this.h = new a();
        this.i = null;
        this.l = null;
        this.m = null;
        this.n = null;
        this.o = new c();
        this.v = 0;
        this.m = new ConcurrentLinkedQueue<>();
        this.n = new ArrayList<>();
        this.e = false;
        a(aVar);
    }

    private void a(boolean z, byte[] bArr) throws SpeechError, UnsupportedEncodingException {
        String str;
        com.iflytek.cloud.a.f.a.b.a("QISRGetResult", null);
        this.t = SystemClock.elapsedRealtime();
        if (bArr != null && bArr.length > 0) {
            str = new String(bArr, XML.CHARSET_UTF8);
        } else {
            if (this.n.size() <= 0) {
                throw new SpeechError(ErrorCode.MSP_ERROR_NO_DATA);
            }
            str = "";
        }
        l();
        this.n.add(str);
        if (this.a != null && r()) {
            this.a.onResult(new RecognizerResult(str), z);
        }
        com.iflytek.cloud.a.f.a.a.a("msc result time:" + System.currentTimeMillis());
        if (z) {
            c((SpeechError) null);
        }
    }

    private void w() throws InterruptedException, SpeechError, IOException {
        com.iflytek.cloud.a.f.a.a.a("recording stop");
        x();
        this.o.a("app_lau");
        this.h.a();
        m();
        a(8, a.EnumC0145a.min, false, this.u);
    }

    private void x() {
        if (this.i != null) {
            this.i.a();
            this.i = null;
            this.o.a("rec_close");
        }
    }

    public int a() {
        return this.f;
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void a(Message message) throws Exception {
        super.a(message);
        switch (message.what) {
            case 0:
                d();
                break;
            case 1:
                e();
                break;
            case 2:
                b(message);
                break;
            case 3:
                w();
                break;
            case 4:
                c(message);
                break;
            case 7:
                f();
                break;
            case 9:
                g();
                break;
        }
    }

    public synchronized void a(RecognizerListener recognizerListener) {
        this.a = recognizerListener;
        com.iflytek.cloud.a.f.a.a.a("startListening called");
        c();
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void a(SpeechError speechError) {
        com.iflytek.cloud.a.f.a.a.a("onSessionEnd");
        x();
        j = this.h.b("upflow");
        k = this.h.b("downflow");
        l();
        if (this.n.size() <= 0 && speechError == null && t().a("asr_nme", true)) {
            speechError = new SpeechError(ErrorCode.MSP_ERROR_NO_DATA);
        }
        if (speechError != null) {
            this.o.a("app_ret", speechError.getErrorCode(), false);
        } else {
            this.o.a("app_ret", 0L, false);
        }
        com.iflytek.cloud.a.f.a.b.a("QISRSessionEnd", null);
        this.o.a("rec_ustop", this.e ? "1" : "0", false);
        this.h.a("sessinfo", this.o.a());
        if (this.s) {
            this.h.a("user abort");
        } else if (speechError != null) {
            this.h.a("error" + speechError.getErrorCode());
        } else {
            this.h.a("success");
        }
        super.a(speechError);
        if (this.a != null) {
            if (this.s) {
                com.iflytek.cloud.a.f.a.a.a("RecognizerListener#onCancel");
                return;
            }
            com.iflytek.cloud.a.f.a.a.a("RecognizerListener#onEnd");
            if (speechError != null) {
                this.a.onError(speechError);
            }
        }
    }

    public void a(byte[] bArr, int i) {
        if (r()) {
            this.a.onVolumeChanged(i);
        }
    }

    @Override // com.iflytek.cloud.c.e.a
    public void a(byte[] bArr, int i, int i2) {
        if (bArr == null || i2 <= 0 || bArr.length < i2 || i2 <= 0 || !r()) {
            return;
        }
        if (!this.b) {
            this.b = true;
            this.o.a("rec_start");
        }
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        d(obtainMessage(2, bArr2));
    }

    protected void a(byte[] bArr, boolean z) throws SpeechError {
        if (!this.c) {
            this.c = true;
            this.o.a("app_fau");
        }
        com.iflytek.cloud.a.f.a.b.a("QISRAudioWrite", "" + bArr.length);
        this.h.a(bArr, bArr.length);
        if (z) {
            if (this.h.b() == 3) {
                g();
                return;
            }
            int iC = this.h.c();
            com.iflytek.cloud.a.f.a.a.a("QISRAudioWrite volume:" + iC);
            a(bArr, iC);
        }
    }

    public synchronized boolean a(boolean z) {
        com.iflytek.cloud.a.f.a.a.a("stopRecognize, current status is :" + s() + " usercancel : " + z);
        this.o.a("app_stop");
        x();
        this.e = z;
        a(3);
        return true;
    }

    public ConcurrentLinkedQueue<byte[]> b() {
        return this.m;
    }

    protected void b(Message message) throws Exception {
        byte[] bArr = (byte[]) message.obj;
        if (bArr == null || bArr.length == 0) {
            return;
        }
        this.m.add(bArr);
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
        x();
        if (s() == a.b.recording) {
            this.e = true;
        }
        super.b(z);
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void c() {
        this.o.a(t());
        super.c();
    }

    void c(Message message) throws InterruptedException, SpeechError, UnsupportedEncodingException {
        int i = message.arg1;
        byte[] bArr = (byte[]) message.obj;
        switch (i) {
            case 0:
                if (!this.d) {
                    this.d = true;
                    this.o.a("app_frs");
                }
                a(false, bArr);
                break;
            case 5:
                if (!this.d) {
                    this.d = true;
                    this.o.a("app_frs");
                }
                this.o.a("app_lrs");
                a(true, bArr);
                break;
        }
    }

    @Override // com.iflytek.cloud.c.e.a
    public void c(boolean z) {
        this.o.a("rec_ready");
    }

    protected void d() throws Exception {
        com.iflytek.cloud.a.f.a.a.a("start connecting");
        if (SpeechConstant.TYPE_CLOUD.equals(t().d(SpeechConstant.ENGINE_TYPE))) {
            h.a(this.r);
        }
        int iA = t().a("record_read_rate", 40);
        if (this.f != -1 && r()) {
            com.iflytek.cloud.a.f.a.a.a("start  record");
            this.i = new e(q(), iA, this.f);
            this.o.a("rec_open");
            this.i.a(this);
            a(9, a.EnumC0145a.min, false, this.p);
            if (this.a != null) {
                this.a.onBeginOfSpeech();
            }
        }
        com.iflytek.cloud.a.f.a.b.a("QISRSessionBegin", null);
        this.o.a("app_ssb");
        a(1, a.EnumC0145a.max, false, 0);
    }

    protected void e() throws Exception {
        int iA = this.h.a(this.r, this.l, this);
        if (iA != 0 || this.h.a == null) {
            this.v++;
            if (this.v > 40) {
                throw new SpeechError(iA);
            }
            if (r()) {
                Thread.sleep(15L);
                a(1, a.EnumC0145a.max, false, 0);
                return;
            }
            return;
        }
        if (r()) {
            MSC.QISRRegisterNotify(this.h.a, "rsltCb", "stusCb", "errCb", this);
            a(a.b.recording);
            if (t().a(SpeechConstant.ASR_NET_PERF, false)) {
                a(7, a.EnumC0145a.max, false, 0);
            }
        }
    }

    void errCb(char[] cArr, int i, byte[] bArr) {
        b(new SpeechError(i));
    }

    public void f() {
        if (r()) {
            int iB = this.h.b("netperf");
            if (this.a != null) {
                this.a.onEvent(10001, iB, 0, null);
            }
            a(7, a.EnumC0145a.normal, false, 100);
        }
    }

    public void g() {
        if (a.b.recording == s()) {
            a(false);
            if (this.a != null) {
                this.a.onEndOfSpeech();
            }
        }
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void h() {
        this.l = t().d(SpeechConstant.CLOUD_GRAMMAR);
        this.f = t().a(SpeechConstant.AUDIO_SOURCE, 1);
        this.g = com.iflytek.cloud.b.c.a(t().d(SpeechConstant.DOMAIN));
        this.p = t().a(SpeechConstant.KEY_SPEECH_TIMEOUT, this.p);
        com.iflytek.cloud.a.f.a.a.a("mSpeechTimeOut=" + this.p);
        super.h();
    }

    @Override // com.iflytek.cloud.a.c.a
    public boolean i() {
        return this.g;
    }

    public c j() {
        return this.o;
    }

    @Override // com.iflytek.cloud.c.e.a
    public void k() {
    }

    public String l() {
        return this.h.d();
    }

    void rsltCb(char[] cArr, byte[] bArr, int i, int i2) {
        if (bArr != null) {
            com.iflytek.cloud.a.f.a.a.a("MscRecognizer", "rsltCb:" + i2 + "result:" + new String(bArr));
        } else {
            com.iflytek.cloud.a.f.a.a.a("MscRecognizer", "rsltCb:" + i2 + "result:null");
        }
        Message messageObtainMessage = obtainMessage(4, i2, 0, bArr);
        if (hasMessages(4)) {
            a(messageObtainMessage, a.EnumC0145a.normal, false, 0);
        } else {
            a(messageObtainMessage, a.EnumC0145a.max, false, 0);
        }
    }

    void stusCb(char[] cArr, int i, int i2, int i3, byte[] bArr) {
    }
}
