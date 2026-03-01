package com.iflytek.cloud.a.a;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.b.c;
import com.iflytek.msc.MSC;
import com.iflytek.msc.MSCSessionInfo;
import java.io.UnsupportedEncodingException;
import org.cybergarage.xml.XML;

/* loaded from: classes.dex */
public class a extends com.iflytek.cloud.a.c.b {
    private MSCSessionInfo c = new MSCSessionInfo();
    private MSCSessionInfo d = new MSCSessionInfo();
    private byte[] e = null;

    private synchronized void a(byte[] bArr, int i, int i2) throws SpeechError {
        int iQISRAudioWrite = MSC.QISRAudioWrite(this.a, bArr, i, i2, this.d);
        this.c.sesstatus = this.d.sesstatus;
        com.iflytek.cloud.a.f.a.a.a("QISRAudioWrite length:" + i);
        if (iQISRAudioWrite != 0) {
            throw new SpeechError(this.d.errorcode);
        }
    }

    public int a(Context context, String str, com.iflytek.cloud.a.c.a aVar) throws SpeechError, UnsupportedEncodingException {
        String strA = c.a(context, str, aVar);
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (TextUtils.isEmpty(str)) {
            com.iflytek.cloud.a.f.a.a.a(strA);
            this.a = MSC.QISRSessionBegin(null, strA.getBytes(aVar.n()), this.c);
        } else {
            this.a = MSC.QISRSessionBegin(str.getBytes(aVar.n()), strA.getBytes(aVar.n()), this.c);
            com.iflytek.cloud.a.f.a.a.a("sessionBegin grammarId:" + str);
        }
        com.iflytek.cloud.a.f.a.a.a("sessionBegin ErrCode:" + this.c.errorcode + " time:" + (SystemClock.elapsedRealtime() - jElapsedRealtime));
        int i = this.c.errorcode;
        if (i == 0 || i == 10129 || i == 10113 || i == 10132) {
            return i;
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
        com.iflytek.cloud.a.f.a.a.a("sessionEnd leavel:" + (MSC.QISRSessionEnd(this.a, str.getBytes()) == 0) + " time:" + (System.currentTimeMillis() - System.currentTimeMillis()));
        this.a = null;
        this.b = null;
    }

    public synchronized void a(byte[] bArr, int i) throws SpeechError {
        a(bArr, i, 2);
    }

    public synchronized boolean a(String str, String str2) {
        int iQISRSetParam;
        boolean z = false;
        synchronized (this) {
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && this.a != null) {
                try {
                    iQISRSetParam = MSC.QISRSetParam(this.a, str.getBytes(XML.CHARSET_UTF8), str2.getBytes(XML.CHARSET_UTF8));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    iQISRSetParam = -1;
                }
                if (iQISRSetParam == 0) {
                    z = true;
                }
            }
        }
        return z;
    }

    public synchronized int b() {
        return this.d.epstatues;
    }

    public synchronized int b(String str) {
        int i = 0;
        synchronized (this) {
            if (this.a != null) {
                try {
                    String strC = c(str);
                    if (!TextUtils.isEmpty(strC)) {
                        i = Integer.parseInt(new String(strC));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return i;
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

    public synchronized String c(String str) {
        String str2 = null;
        synchronized (this) {
            if (this.a != null) {
                try {
                    if (MSC.QISRGetParam(this.a, str.getBytes(), this.c) == 0) {
                        str2 = new String(this.c.buffer);
                    }
                } catch (Exception e) {
                }
            }
        }
        return str2;
    }

    protected String d() {
        if (this.b == null) {
            this.b = c("sid");
        }
        return this.b;
    }
}
