package com.iflytek.cloud.c;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.a.f.f;
import com.iflytek.cloud.c.a;

/* loaded from: classes.dex */
public class b {
    private Context d;
    private int h;
    private int j;
    private boolean k;
    private AudioTrack b = null;
    private com.iflytek.cloud.c.a c = null;
    private c e = null;
    private InterfaceC0147b f = null;
    private volatile a g = a.INIT;
    private boolean i = true;
    private boolean l = false;
    private Object m = new Object();
    AudioManager.OnAudioFocusChangeListener a = new com.iflytek.cloud.c.c(this);
    private int n = 0;
    private Handler o = new d(this, Looper.getMainLooper());

    public enum a {
        INIT,
        BUFFERING,
        PLAYING,
        PAUSED,
        STOPED
    }

    /* renamed from: com.iflytek.cloud.c.b$b, reason: collision with other inner class name */
    public interface InterfaceC0147b {
        void a();

        void a(int i, int i2, int i3);

        void a(SpeechError speechError);

        void b();

        void c();
    }

    private class c extends Thread {
        private c() {
        }

        /* synthetic */ c(b bVar, com.iflytek.cloud.c.c cVar) {
            this();
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            b bVar;
            try {
                try {
                    com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "start player");
                    com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "mAudioFocus= " + b.this.i);
                    if (b.this.i) {
                        f.a(b.this.d, Boolean.valueOf(b.this.k), b.this.a);
                    } else {
                        f.a(b.this.d, Boolean.valueOf(b.this.k), null);
                    }
                    f.a(b.this.d, Boolean.valueOf(b.this.k), b.this.a);
                    b.this.c.c();
                    if (b.this.g != a.STOPED && b.this.g != a.PAUSED) {
                        b.this.g = a.PLAYING;
                    }
                    while (true) {
                        if (b.this.g == a.STOPED) {
                            break;
                        }
                        b.this.g();
                        if (b.this.g == a.PLAYING || b.this.g == a.BUFFERING) {
                            if (b.this.c.g()) {
                                if (b.this.g == a.BUFFERING) {
                                    b.this.g = a.PLAYING;
                                    Message.obtain(b.this.o, 2).sendToTarget();
                                }
                                int iD = b.this.c.d();
                                a.C0146a c0146aE = b.this.c.e();
                                if (c0146aE != null) {
                                    b.this.n = c0146aE.d;
                                    Message.obtain(b.this.o, 3, iD, c0146aE.c).sendToTarget();
                                }
                                if (b.this.b.getPlayState() != 3) {
                                    b.this.b.play();
                                }
                                b.this.c.a(b.this.b, b.this.j);
                            } else {
                                if (b.this.c.f()) {
                                    com.iflytek.cloud.a.f.a.a.a("play stoped");
                                    b.this.g = a.STOPED;
                                    Message.obtain(b.this.o, 4).sendToTarget();
                                    break;
                                }
                                if (b.this.g == a.PLAYING) {
                                    com.iflytek.cloud.a.f.a.a.a("play onpaused!");
                                    b.this.g = a.BUFFERING;
                                    Message.obtain(b.this.o, 1).sendToTarget();
                                }
                                sleep(50L);
                            }
                        } else if (b.this.g == a.PAUSED) {
                            if (2 != b.this.b.getPlayState()) {
                                b.this.b.pause();
                            }
                            sleep(50L);
                        }
                    }
                    if (b.this.b != null) {
                        b.this.b.stop();
                    }
                    b.this.g = a.STOPED;
                    if (b.this.b != null) {
                        b.this.b.release();
                        b.this.b = null;
                    }
                    if (b.this.i) {
                        f.b(b.this.d, Boolean.valueOf(b.this.k), b.this.a);
                    } else {
                        f.b(b.this.d, Boolean.valueOf(b.this.k), null);
                    }
                    bVar = b.this;
                } catch (Exception e) {
                    e.printStackTrace();
                    Message.obtain(b.this.o, 0, new SpeechError(ErrorCode.ERROR_PLAY_MEDIA)).sendToTarget();
                    b.this.g = a.STOPED;
                    if (b.this.b != null) {
                        b.this.b.release();
                        b.this.b = null;
                    }
                    if (b.this.i) {
                        f.b(b.this.d, Boolean.valueOf(b.this.k), b.this.a);
                    } else {
                        f.b(b.this.d, Boolean.valueOf(b.this.k), null);
                    }
                    bVar = b.this;
                }
                bVar.e = null;
            } catch (Throwable th) {
                b.this.g = a.STOPED;
                if (b.this.b != null) {
                    b.this.b.release();
                    b.this.b = null;
                }
                if (b.this.i) {
                    f.b(b.this.d, Boolean.valueOf(b.this.k), b.this.a);
                } else {
                    f.b(b.this.d, Boolean.valueOf(b.this.k), null);
                }
                b.this.e = null;
                throw th;
            }
        }
    }

    public b(Context context, int i, boolean z) {
        this.d = null;
        this.h = 3;
        this.k = false;
        this.d = context;
        this.h = i;
        this.k = z;
    }

    private void f() throws Exception {
        com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "createAudio start");
        int iA = this.c.a();
        this.j = AudioTrack.getMinBufferSize(iA, 2, 2);
        if (this.b != null) {
            b();
        }
        com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "createAudio || mStreamType = " + this.h);
        this.b = new AudioTrack(this.h, iA, 2, 2, this.j * 2, 1);
        if (this.j == -2 || this.j == -1) {
            throw new Exception();
        }
        com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "createAudio end");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void g() throws Exception {
        if (this.b == null || this.b.getStreamType() != this.h) {
            com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "prepAudioPlayer || audiotrack stream type is change.");
            f();
        }
    }

    public a a() {
        return this.g;
    }

    public boolean a(com.iflytek.cloud.c.a aVar, InterfaceC0147b interfaceC0147b) {
        com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "play mPlaytate= " + this.g + ",mAudioFocus= " + this.i);
        if (this.g != a.STOPED && this.g != a.INIT && this.g != a.PAUSED && this.e != null) {
            return false;
        }
        this.c = aVar;
        this.f = interfaceC0147b;
        this.e = new c(this, null);
        this.e.start();
        return true;
    }

    public void b() {
        synchronized (this.m) {
            if (this.b != null) {
                if (this.b.getPlayState() == 3) {
                    this.b.stop();
                }
                this.b.release();
                this.b = null;
            }
            com.iflytek.cloud.a.f.a.a.a("PcmPlayer", "mAudioTrack released");
        }
    }

    public boolean c() {
        if (this.g == a.STOPED || this.g == a.PAUSED) {
            return false;
        }
        this.g = a.PAUSED;
        return true;
    }

    public boolean d() {
        if (this.g != a.PAUSED) {
            return false;
        }
        this.g = a.PLAYING;
        return true;
    }

    public void e() {
        this.g = a.STOPED;
    }
}
