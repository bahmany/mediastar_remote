package com.iflytek.cloud.a.c;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import java.io.IOException;
import mktvsmart.screen.GlobalConstantValue;
import org.cybergarage.xml.XML;

/* loaded from: classes.dex */
public abstract class a extends Handler {
    private com.iflytek.cloud.b.a a;
    private volatile b b;
    protected int p;
    protected int q;
    protected Context r;
    protected volatile boolean s;
    protected long t;
    protected int u;

    /* renamed from: com.iflytek.cloud.a.c.a$a, reason: collision with other inner class name */
    protected enum EnumC0145a {
        max,
        normal,
        min
    }

    protected enum b {
        init,
        start,
        recording,
        waitresult,
        exiting,
        exited
    }

    public a(Context context) {
        this.p = 60000;
        this.q = ErrorCode.MSP_ERROR_LMOD_BASE;
        this.r = null;
        this.a = new com.iflytek.cloud.b.a();
        this.s = false;
        this.b = b.init;
        this.t = 0L;
        this.u = GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM;
        this.r = context;
        this.s = false;
    }

    public a(Context context, HandlerThread handlerThread) {
        super(handlerThread.getLooper());
        this.p = 60000;
        this.q = ErrorCode.MSP_ERROR_LMOD_BASE;
        this.r = null;
        this.a = new com.iflytek.cloud.b.a();
        this.s = false;
        this.b = b.init;
        this.t = 0L;
        this.u = GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM;
        this.r = context;
        this.s = false;
    }

    protected void a(int i) {
        a(obtainMessage(i), EnumC0145a.normal, false, 0);
    }

    protected void a(int i, EnumC0145a enumC0145a, boolean z, int i2) {
        a(obtainMessage(i), enumC0145a, z, i2);
    }

    protected void a(Message message) throws Exception {
    }

    protected void a(Message message, EnumC0145a enumC0145a, boolean z, int i) {
        if (s() == b.exited || s() == b.exiting) {
            return;
        }
        switch (message.what) {
            case 0:
                a(b.start);
                break;
            case 3:
                a(b.waitresult);
                break;
            case 21:
                a(b.exiting);
                break;
        }
        if (enumC0145a != EnumC0145a.max || i > 0) {
            sendMessageDelayed(message, i);
        } else {
            sendMessageAtFrontOfQueue(message);
        }
    }

    protected void a(SpeechError speechError) {
        a(b.exited);
        u();
    }

    protected synchronized void a(b bVar) {
        com.iflytek.cloud.a.f.a.a.a("curStatus=" + this.b + ",setStatus=" + bVar);
        if (this.b != b.exited && (this.b != b.exiting || bVar == b.exited)) {
            com.iflytek.cloud.a.f.a.a.a("setStatus success=" + bVar);
            this.b = bVar;
            this.t = SystemClock.elapsedRealtime();
        }
    }

    protected void a(com.iflytek.cloud.b.a aVar) {
        this.a = aVar.clone();
        this.a.a(com.iflytek.cloud.b.b.a);
        h();
    }

    public void b(boolean z) {
        this.s = true;
        u();
        c(null);
    }

    protected void c() {
        a(0, EnumC0145a.max, false, 0);
    }

    protected synchronized void c(SpeechError speechError) {
        if (speechError != null) {
            u();
            d(obtainMessage(21, speechError));
        } else {
            d(obtainMessage(21, speechError));
        }
    }

    protected void d(Message message) {
        a(message, EnumC0145a.normal, false, 0);
    }

    protected void h() {
        this.u = this.a.a(SpeechConstant.NET_TIMEOUT, this.u);
        this.q = this.a.a(SpeechConstant.SAMPLE_RATE, this.q);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 21:
                a((SpeechError) message.obj);
                return;
            default:
                SpeechError speechError = null;
                try {
                    try {
                        try {
                            switch (message.what) {
                                case 8:
                                    throw new SpeechError(ErrorCode.ERROR_NETWORK_TIMEOUT);
                                default:
                                    a(message);
                                    if (speechError != null) {
                                        return;
                                    } else {
                                        return;
                                    }
                            }
                        } catch (SpeechError e) {
                            e.printStackTrace();
                            if (e != null) {
                                com.iflytek.cloud.a.f.a.a.a(v() + " occur Error = " + e.toString());
                                c(e);
                                return;
                            }
                            return;
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            SpeechError speechError2 = new SpeechError(ErrorCode.ERROR_FILE_ACCESS);
                            if (speechError2 != null) {
                                com.iflytek.cloud.a.f.a.a.a(v() + " occur Error = " + speechError2.toString());
                                c(speechError2);
                                return;
                            }
                            return;
                        }
                    } catch (Exception e3) {
                        e3.printStackTrace();
                        SpeechError speechError3 = new SpeechError(e3);
                        if (speechError3 != null) {
                            com.iflytek.cloud.a.f.a.a.a(v() + " occur Error = " + speechError3.toString());
                            c(speechError3);
                            return;
                        }
                        return;
                    }
                } finally {
                    if (0 != 0) {
                        com.iflytek.cloud.a.f.a.a.a(v() + " occur Error = " + speechError.toString());
                        c(null);
                    }
                }
        }
    }

    public boolean i() {
        return false;
    }

    protected void m() {
        removeMessages(8);
        a(8, EnumC0145a.normal, false, this.u);
    }

    public String n() {
        return this.a.b("pte", XML.CHARSET_UTF8);
    }

    public String o() {
        return this.a.b("tte", XML.CHARSET_UTF8);
    }

    public String p() {
        return this.a.b("rse", XML.CHARSET_UTF8);
    }

    public int q() {
        return this.q;
    }

    public boolean r() {
        return (this.b == b.exited || this.b == b.exiting || this.b == b.init) ? false : true;
    }

    protected synchronized b s() {
        return this.b;
    }

    public com.iflytek.cloud.b.a t() {
        return this.a;
    }

    protected void u() {
        com.iflytek.cloud.a.f.a.a.a("clear all message");
        for (int i = 0; i < 20; i++) {
            removeMessages(i);
        }
    }

    protected String v() {
        return getClass().toString();
    }
}
