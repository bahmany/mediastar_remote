package com.iflytek.cloud.c;

import android.media.AudioRecord;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;

/* loaded from: classes.dex */
public class e extends Thread {
    private final short a = 16;
    private byte[] b = null;
    private AudioRecord c = null;
    private a d = null;
    private a e = null;
    private volatile boolean f = false;
    private int g;
    private int h;
    private int i;
    private int j;

    public interface a {
        void a(byte[] bArr, int i, int i2);

        void b(SpeechError speechError);

        void c(boolean z);

        void k();
    }

    public e(int i, int i2, int i3) {
        this.g = ErrorCode.MSP_ERROR_LMOD_BASE;
        this.h = 40;
        this.i = 40;
        this.j = i3;
        this.g = i;
        this.h = i2;
        if (this.h < 40 || this.h > 100) {
            this.h = 40;
        }
        this.i = 10;
    }

    private int b() throws SpeechError {
        if (this.c == null || this.d == null) {
            return 0;
        }
        int i = this.c.read(this.b, 0, this.b.length);
        if (i <= 0 || this.d == null) {
            return i;
        }
        this.d.a(this.b, 0, i);
        return i;
    }

    private void c() {
        if (this.c != null) {
            com.iflytek.cloud.a.f.a.a.a("release record begin");
            this.c.release();
            this.c = null;
            if (this.e != null) {
                this.e.k();
                this.e = null;
            }
            com.iflytek.cloud.a.f.a.a.a("release record over");
        }
    }

    public void a() {
        this.f = true;
        if (this.e == null) {
            this.e = this.d;
        }
        this.d = null;
    }

    public void a(a aVar) throws SpeechError {
        this.d = aVar;
        setPriority(10);
        start();
    }

    public void a(short s, int i, int i2) throws SpeechError {
        if (this.c != null) {
            c();
        }
        int i3 = (i * i2) / 1000;
        int i4 = (((i3 * 4) * 16) * s) / 8;
        int i5 = s == 1 ? 2 : 3;
        int minBufferSize = AudioRecord.getMinBufferSize(i, i5, 2);
        if (i4 < minBufferSize) {
            i4 = minBufferSize;
        }
        this.c = new AudioRecord(this.j, i, i5, 2, i4);
        this.b = new byte[((i3 * s) * 16) / 8];
        com.iflytek.cloud.a.f.a.a.a("\nSampleRate:" + i + "\nChannel:" + i5 + "\nFormat:2\nFramePeriod:" + i3 + "\nBufferSize:" + i4 + "\nMinBufferSize:" + minBufferSize + "\nActualBufferSize:" + this.b.length + "\n");
        if (this.c.getState() != 1) {
            com.iflytek.cloud.a.f.a.a.a("create AudioRecord error");
            throw new SpeechError(ErrorCode.ERROR_AUDIO_RECORD);
        }
    }

    protected void finalize() throws Throwable {
        c();
        super.finalize();
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() throws IllegalStateException, SpeechError {
        int i = 0;
        int i2 = 0;
        while (!this.f) {
            try {
                a((short) 1, this.g, this.h);
            } catch (Exception e) {
                i2++;
                if (i2 >= 4) {
                    throw new SpeechError(ErrorCode.ERROR_AUDIO_RECORD);
                }
                try {
                    sleep(40L);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    if (this.d != null) {
                        this.d.b(new SpeechError(ErrorCode.ERROR_AUDIO_RECORD));
                    }
                }
            }
        }
        if (this.c.getState() == 3) {
            throw new SpeechError(ErrorCode.ERROR_AUDIO_RECORD);
        }
        if (!this.f) {
            this.c.startRecording();
        }
        if (this.d != null) {
            this.d.c(true);
        }
        while (!this.f) {
            b();
            if (this.c.getRecordingState() != 3 && (i = i + 1) > 4) {
                throw new SpeechError(ErrorCode.ERROR_AUDIO_RECORD);
            }
            sleep(this.i);
        }
        c();
    }
}
