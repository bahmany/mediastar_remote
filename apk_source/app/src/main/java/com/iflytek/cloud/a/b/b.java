package com.iflytek.cloud.a.b;

import android.content.Context;
import android.os.SystemClock;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.a.c.b;
import com.iflytek.cloud.b.c;
import com.iflytek.msc.MSC;
import com.iflytek.msc.MSCSessionInfo;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/* loaded from: classes.dex */
public class b extends com.iflytek.cloud.a.c.b {
    private MSCSessionInfo c = new MSCSessionInfo();
    private MSCSessionInfo d = new MSCSessionInfo();
    private byte[] e = null;

    private synchronized void a(byte[] bArr, int i, int i2) throws SpeechError {
        int iQISVAudioWrite = MSC.QISVAudioWrite(this.a, bArr, i, i2, this.c);
        com.iflytek.cloud.a.f.a.a.a("QISVAudioWrite error:" + iQISVAudioWrite);
        if (iQISVAudioWrite != 0) {
            throw new SpeechError(iQISVAudioWrite);
        }
    }

    public int a(Context context, String str, com.iflytek.cloud.a.c.a aVar) throws SpeechError, UnsupportedEncodingException {
        String strB = c.b(context, aVar);
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        this.a = MSC.QISVSessionBegin(strB.getBytes(aVar.n()), str == null ? null : str.getBytes(aVar.n()), this.c);
        com.iflytek.cloud.a.f.a.a.a("sessionBegin ErrCode:" + this.c.errorcode + " time:" + (SystemClock.elapsedRealtime() - jElapsedRealtime));
        int i = this.c.errorcode;
        if (i == 0 || i == 10129 || i == 10113 || i == 10132) {
            return 0;
        }
        throw new SpeechError(i);
    }

    public synchronized void a() throws SpeechError {
        a(new byte[0], 0, 4);
    }

    public void a(String str) {
        if (this.a == null) {
            return;
        }
        com.iflytek.cloud.a.f.a.a.a("sessionEnd enter ");
        com.iflytek.cloud.a.f.a.a.a("sessionEnd leavel:" + (MSC.QISVSessionEnd(this.a, str.getBytes()) == 0) + " time:" + (System.currentTimeMillis() - System.currentTimeMillis()));
        this.a = null;
        this.b = null;
    }

    public synchronized void a(byte[] bArr, int i) throws SpeechError {
        a(bArr, i, 2);
    }

    public synchronized boolean b() {
        int i;
        i = this.c.epstatues;
        com.iflytek.cloud.a.f.a.a.a("Vad Epstatus=" + i);
        return i >= 3;
    }

    public synchronized int c() {
        int iQISRGetParam;
        int i = 0;
        synchronized (this) {
            try {
                iQISRGetParam = MSC.QISRGetParam(this.a, SpeechConstant.VOLUME.getBytes(), this.d);
            } catch (Exception e) {
                iQISRGetParam = 0;
            }
            try {
                if (iQISRGetParam == 0) {
                    i = Integer.parseInt(new String(new String(this.d.buffer)));
                } else {
                    com.iflytek.cloud.a.f.a.a.a("VAD CHECK FALSE");
                }
            } catch (Exception e2) {
                com.iflytek.cloud.a.f.a.a.a("getAudioVolume Exception vadret = " + iQISRGetParam);
                return i;
            }
        }
        return i;
    }

    public byte[] d() {
        return this.e;
    }

    public b.a e() throws SpeechError {
        Date date = new Date();
        this.e = MSC.QISVGetResult(this.a, this.c);
        com.iflytek.cloud.a.f.a.a.a("QISVGetResult leavel:" + (this.e != null) + " time:" + (new Date().getTime() - date.getTime()));
        int i = this.c.errorcode;
        if (i != 0) {
            com.iflytek.cloud.a.f.a.a.a("Result: error errorcode is " + i);
            throw new SpeechError(i);
        }
        int i2 = this.c.rsltstatus;
        switch (i2) {
            case 0:
            case 5:
                if (this.e != null) {
                    com.iflytek.cloud.a.f.a.a.a("ResultStatus: hasResult" + i2);
                    return b.a.hasResult;
                }
                break;
            case 1:
                com.iflytek.cloud.a.f.a.a.a("ResultStatus: noResult" + i2);
                throw new SpeechError(20005);
        }
        return b.a.noResult;
    }
}
