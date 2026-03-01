package com.iflytek.cloud.a.e;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.a.c.a;
import com.iflytek.cloud.a.f.h;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class a extends com.iflytek.cloud.a.c.a {
    public static int a = 0;
    public static int b = 0;
    private String c;
    private c d;
    private b e;
    private ArrayList<byte[]> f;
    private int g;
    private int h;
    private StringBuilder i;
    private boolean j;
    private int k;

    public a(Context context, com.iflytek.cloud.b.a aVar, HandlerThread handlerThread) {
        super(context, handlerThread);
        this.c = "";
        this.d = null;
        this.e = null;
        this.f = null;
        this.g = 0;
        this.h = 0;
        this.i = null;
        this.j = false;
        this.k = 0;
        this.d = new c();
        this.f = new ArrayList<>();
        this.i = new StringBuilder();
        a(aVar);
    }

    protected void a() throws Exception {
        if (SpeechConstant.TYPE_CLOUD.equals(t().d(SpeechConstant.ENGINE_TYPE))) {
            h.a(this.r);
        }
        com.iflytek.cloud.a.f.a.b.a("QTTSInit", null);
        a(1);
        com.iflytek.cloud.a.f.a.b.a("QTTSSessionBegin", null);
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void a(Message message) throws Exception {
        switch (message.what) {
            case 0:
                a();
                break;
            case 1:
                b();
                break;
            case 5:
                d();
                break;
        }
    }

    @Override // com.iflytek.cloud.a.c.a
    protected void a(SpeechError speechError) {
        a = this.d.b("upflow");
        b = this.d.b("downflow");
        e();
        com.iflytek.cloud.a.f.a.b.a("QTTSSessionEnd", null);
        if (this.e == null) {
            this.d.a("user abort");
        } else if (speechError != null) {
            this.d.a("error" + speechError.getErrorCode());
            com.iflytek.cloud.a.f.a.a.a("QTts Error Code = " + speechError.getErrorCode());
        } else {
            this.d.a("success");
        }
        super.a(speechError);
        if (this.e != null) {
            if (this.s) {
                com.iflytek.cloud.a.f.a.a.a("SynthesizerPlayer#onCancel");
            } else {
                com.iflytek.cloud.a.f.a.a.a("SynthesizerPlayer#onEnd");
                this.e.a(speechError);
            }
        }
    }

    public void a(String str, b bVar) {
        this.c = str;
        this.e = bVar;
        if (str == null || TextUtils.isEmpty(str)) {
            this.e.a(new SpeechError(ErrorCode.ERROR_EMPTY_UTTERANCE));
        } else {
            this.j = t().a("tts_spell_info", false);
            c();
        }
    }

    protected void b() throws Exception {
        int iA = this.d.a(this.r, null, this);
        if (iA == 0) {
            com.iflytek.cloud.a.f.a.b.a("QTTSTextPut", null);
            this.d.a(this.c.getBytes(o()));
            a(a.b.waitresult);
            a(5);
            m();
            return;
        }
        this.k++;
        if (this.k > 40) {
            throw new SpeechError(iA);
        }
        if (r()) {
            Thread.sleep(15L);
            a(1, a.EnumC0145a.max, false, 0);
        }
    }

    @Override // com.iflytek.cloud.a.c.a
    public void b(boolean z) {
        if (z && r() && this.e != null) {
            this.e.a(new SpeechError(ErrorCode.ERROR_INTERRUPT));
        }
        super.b(z);
    }

    protected void d() throws Exception {
        if (this.d.d()) {
            if (this.e != null) {
                this.e.a(this.f, 100, this.h, this.c.length() - 1, this.i.toString());
                c(null);
                return;
            }
            return;
        }
        byte[] bArrA = this.d.a();
        if (bArrA != null && this.e != null) {
            com.iflytek.cloud.a.f.a.b.a("QTTSAudioGet", "" + bArrA.length);
            int iB = (this.d.b() / 2) - 1;
            if (this.j) {
                String strC = this.d.c();
                if (!TextUtils.isEmpty(strC)) {
                    this.i.append(strC);
                    this.i.append("#\n");
                }
            }
            if (this.g != 0 && iB != this.g && this.f.size() > 0) {
                this.e.a(this.f, (this.g * 100) / this.c.length(), this.h, this.g, this.i.toString());
                this.i.delete(0, this.i.length());
                this.f = new ArrayList<>();
                this.h = this.g;
            }
            m();
            this.g = iB;
            this.f.add(bArrA);
        }
        e();
        a(5, a.EnumC0145a.normal, false, 20);
    }

    public String e() {
        return this.d.e();
    }

    @Override // com.iflytek.cloud.a.c.a
    public String o() {
        return "unicode";
    }
}
