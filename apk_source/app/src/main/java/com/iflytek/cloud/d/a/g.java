package com.iflytek.cloud.d.a;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizeToUriListener;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.c.b;

/* loaded from: classes.dex */
public class g extends com.iflytek.cloud.a.c.e {
    private Context f;
    private a g;

    public class a extends com.iflytek.cloud.a.c.e {
        private com.iflytek.cloud.c.b g;
        private com.iflytek.cloud.c.a h;
        private SynthesizerListener i;
        private SynthesizeToUriListener j;
        private int k;
        private boolean l;
        private com.iflytek.cloud.a.e.b m;
        private b.InterfaceC0147b n;
        private Handler o;

        protected a(Context context) {
            super(context);
            this.g = null;
            this.h = null;
            this.i = null;
            this.j = null;
            this.k = 0;
            this.l = false;
            this.m = new h(this);
            this.n = new i(this);
            this.o = new j(this, Looper.getMainLooper());
        }

        public void a(String str, com.iflytek.cloud.b.a aVar, SynthesizerListener synthesizerListener) {
            this.i = synthesizerListener;
            this.g = new com.iflytek.cloud.c.b(this.a, aVar.a(SpeechConstant.STREAM_TYPE, 3), aVar.a(SpeechConstant.KEY_REQUEST_FOCUS, true));
            this.d = new com.iflytek.cloud.a.e.a(this.a, aVar, a(SpeechConstant.ENG_TTS));
            this.h = new com.iflytek.cloud.c.a(this.a, this.d.q(), aVar.d(SpeechConstant.TTS_AUDIO_PATH));
            this.h.a(str);
            this.k = aVar.a(SpeechConstant.TTS_BUFFER_TIME, 0);
            com.iflytek.cloud.a.f.a.a.a("minPlaySec:" + this.k);
            this.l = false;
            ((com.iflytek.cloud.a.e.a) this.d).a(str, this.m);
        }

        public void a(String str, String str2, com.iflytek.cloud.b.a aVar, SynthesizeToUriListener synthesizeToUriListener) {
            this.j = synthesizeToUriListener;
            this.d = new com.iflytek.cloud.a.e.a(this.a, aVar, a(SpeechConstant.ENG_TTS));
            this.h = new com.iflytek.cloud.c.a(this.a, this.d.q(), str2);
            this.h.a(str);
            ((com.iflytek.cloud.a.e.a) this.d).a(str, new l(this, new k(this, Looper.getMainLooper(), str2)));
        }

        @Override // com.iflytek.cloud.a.c.e
        public void cancel(boolean z) {
            if (z && g()) {
                if (this.i != null) {
                    this.i.onCompleted(new SpeechError(ErrorCode.ERROR_INTERRUPT));
                }
                if (this.j != null) {
                    this.i.onCompleted(new SpeechError(ErrorCode.ERROR_INTERRUPT));
                }
            }
            this.i = null;
            this.j = null;
            super.cancel(false);
            if (this.g != null) {
                this.g.e();
            }
            if (this.h != null) {
                this.h.h();
            }
        }

        @Override // com.iflytek.cloud.a.c.e
        public boolean destroy() {
            synchronized (this.c) {
                cancel(false);
            }
            return true;
        }

        public b.a e() {
            return (this.h == null || this.g == null) ? b.a.STOPED : this.g.a();
        }

        public void f() {
            if (this.h == null || this.g == null) {
                return;
            }
            this.g.c();
        }

        public boolean g() {
            if (d()) {
                return true;
            }
            return (e() == b.a.STOPED || e() == b.a.INIT) ? false : true;
        }

        public void h() {
            if (this.h == null || this.g == null) {
                return;
            }
            this.g.d();
        }
    }

    public g(Context context) {
        super(context);
        this.f = null;
        this.g = null;
        this.f = context.getApplicationContext();
    }

    public int a(String str, SynthesizerListener synthesizerListener) {
        if (this.g != null && this.g.g()) {
            this.g.cancel(this.b.a("tts_interrupt_error", false));
        }
        this.g = new a(this.f);
        this.g.a(str, this.b, synthesizerListener);
        return 0;
    }

    public void a(String str, String str2, SynthesizeToUriListener synthesizeToUriListener) {
        if (this.g != null && this.g.g()) {
            this.g.cancel(this.b.a("tts_interrupt_error", false));
        }
        this.g = new a(this.f);
        this.g.a(str, str2, this.b, synthesizeToUriListener);
    }

    public void a(boolean z) {
        if (this.g != null) {
            this.g.cancel(z);
        }
    }

    @Override // com.iflytek.cloud.a.c.e
    public boolean destroy() {
        a(false);
        if (this.g != null) {
            return this.g.destroy();
        }
        return true;
    }

    public void e() {
        if (this.g != null) {
            this.g.f();
        }
    }

    public void f() {
        if (this.g != null) {
            this.g.h();
        }
    }

    public boolean g() {
        if (this.g != null) {
            return this.g.g();
        }
        return false;
    }
}
