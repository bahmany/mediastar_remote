package com.iflytek.cloud.a.e;

import android.content.Context;
import android.text.TextUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;
import com.iflytek.msc.MSC;
import com.iflytek.msc.MSCSessionInfo;
import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public class c extends com.iflytek.cloud.a.c.b {
    private MSCSessionInfo c = new MSCSessionInfo();

    public int a(Context context, String str, com.iflytek.cloud.a.c.a aVar) throws SpeechError, UnsupportedEncodingException {
        this.a = null;
        String strD = com.iflytek.cloud.b.c.d(context, aVar);
        com.iflytek.cloud.a.f.a.a.a("QTTSSessionBegin enter  params:" + strD);
        this.a = MSC.QTTSSessionBegin(strD.getBytes(aVar.n()), this.c);
        com.iflytek.cloud.a.f.a.a.a("QTTSSessionBegin leave:" + this.c.errorcode + " ErrorCode:" + this.c.errorcode);
        int i = this.c.errorcode;
        if (i == 0 || i == 10129 || i == 10113 || i == 10132) {
            return i;
        }
        throw new SpeechError(i);
    }

    public void a(String str) {
        if (this.a == null) {
            return;
        }
        if (TextUtils.isEmpty(str)) {
            str = "unknown";
        }
        com.iflytek.cloud.a.f.a.a.a("QTTSSessionEnd enter");
        com.iflytek.cloud.a.f.a.a.a("QTTSSessionEnd leavel:" + MSC.QTTSSessionEnd(this.a, str.getBytes()));
        this.a = null;
        this.b = null;
    }

    public synchronized void a(byte[] bArr) throws SpeechError {
        com.iflytek.cloud.a.f.a.a.a("QTTSTextPut enter");
        int iQTTSTextPut = MSC.QTTSTextPut(this.a, bArr);
        com.iflytek.cloud.a.f.a.a.a("QTTSTextPut leavel:" + iQTTSTextPut);
        if (iQTTSTextPut != 0) {
            throw new SpeechError(iQTTSTextPut);
        }
    }

    public synchronized byte[] a() throws SpeechError {
        byte[] bArrQTTSAudioGet;
        if (this.a == null) {
            throw new SpeechError(ErrorCode.ERROR_NET_EXPECTION);
        }
        com.iflytek.cloud.a.f.a.a.a("QTTSAudioGet enter");
        bArrQTTSAudioGet = MSC.QTTSAudioGet(this.a, this.c);
        com.iflytek.cloud.a.f.a.a.a("QTTSAudioGet leavel:" + this.c.errorcode + "value len = " + (bArrQTTSAudioGet == null ? 0 : bArrQTTSAudioGet.length));
        int i = this.c.errorcode;
        if (i != 0) {
            throw new SpeechError(i);
        }
        return bArrQTTSAudioGet;
    }

    public int b() {
        try {
            return new com.iflytek.cloud.b.a(new String(MSC.QTTSAudioInfo(this.a)), (String[][]) null).a("ced", 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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
                }
            }
        }
        return i;
    }

    public String c() {
        Exception exc;
        String str;
        try {
            String str2 = new String(MSC.QTTSAudioInfo(this.a));
            try {
                return new String(str2.getBytes("iso8859-1"), "gb2312");
            } catch (Exception e) {
                str = str2;
                exc = e;
                exc.printStackTrace();
                return str;
            }
        } catch (Exception e2) {
            exc = e2;
            str = "";
        }
    }

    public synchronized String c(String str) {
        String str2 = null;
        synchronized (this) {
            if (this.a != null) {
                try {
                    if (MSC.QTTSGetParam(this.a, str.getBytes(), this.c) == 0) {
                        str2 = new String(this.c.buffer);
                    }
                } catch (Exception e) {
                }
            }
        }
        return str2;
    }

    public synchronized boolean d() {
        return 2 == this.c.sesstatus;
    }

    protected String e() {
        if (this.b == null) {
            this.b = c("sid");
        }
        return this.b;
    }
}
