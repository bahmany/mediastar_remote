package com.iflytek.cloud.a.c;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;

/* loaded from: classes.dex */
public abstract class e extends d {
    protected Context a;
    protected Object c = new Object();
    protected volatile com.iflytek.cloud.a.c.a d = null;
    protected volatile HandlerThread e = null;

    protected class a implements SpeechListener {
        private SpeechListener b;
        private Handler c = new f(this, Looper.getMainLooper());

        public a(SpeechListener speechListener) {
            this.b = null;
            this.b = speechListener;
        }

        @Override // com.iflytek.cloud.SpeechListener
        public void onCompleted(SpeechError speechError) {
            this.c.sendMessage(this.c.obtainMessage(2, speechError));
        }

        @Override // com.iflytek.cloud.SpeechListener
        public void onData(byte[] bArr) {
            this.c.sendMessage(this.c.obtainMessage(1, bArr));
        }

        @Override // com.iflytek.cloud.SpeechListener
        public void onEvent(int i, Bundle bundle) {
            this.c.sendMessage(this.c.obtainMessage(0, i, 0, bundle));
        }
    }

    protected e(Context context) {
        this.a = null;
        if (context == null) {
            this.a = null;
            return;
        }
        com.iflytek.cloud.a.f.b.a(context.getApplicationContext());
        this.a = context.getApplicationContext();
        try {
            b();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected HandlerThread a(String str) {
        if (this.e == null) {
            this.e = new HandlerThread(str);
            this.e.start();
        }
        return this.e;
    }

    protected boolean a() {
        if (this.e == null || !this.e.isAlive()) {
            return true;
        }
        HandlerThread handlerThread = this.e;
        this.e = null;
        handlerThread.interrupt();
        return true;
    }

    protected void b() throws Exception {
    }

    protected String c() {
        return getClass().toString();
    }

    public void cancel(boolean z) {
        if (this.d != null) {
            this.d.b(z);
        }
    }

    protected boolean d() {
        return this.d != null && this.d.r();
    }

    public boolean destroy() {
        boolean zA = false;
        synchronized (this.c) {
            if (d()) {
                this.d.b(false);
            } else {
                zA = a();
                com.iflytek.cloud.a.f.a.a.c(c() + "destory =" + zA);
            }
        }
        return zA;
    }

    protected void finalize() throws Throwable {
        com.iflytek.cloud.a.f.a.a.a(c() + " finalize called");
        super.finalize();
    }

    public int getSampleRate() {
        return this.b.a(SpeechConstant.SAMPLE_RATE, ErrorCode.MSP_ERROR_LMOD_BASE);
    }
}
