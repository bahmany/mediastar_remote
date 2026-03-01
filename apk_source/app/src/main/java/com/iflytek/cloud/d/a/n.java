package com.iflytek.cloud.d.a;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.a.c.e;
import java.io.UnsupportedEncodingException;
import org.cybergarage.xml.XML;

/* loaded from: classes.dex */
public class n extends com.iflytek.cloud.a.c.e {

    private class a implements SpeechListener {
        private TextUnderstanderListener b;

        public a(TextUnderstanderListener textUnderstanderListener) {
            this.b = textUnderstanderListener;
        }

        @Override // com.iflytek.cloud.SpeechListener
        public void onCompleted(SpeechError speechError) {
            if (this.b == null || speechError == null) {
                return;
            }
            this.b.onError(speechError);
        }

        @Override // com.iflytek.cloud.SpeechListener
        public void onData(byte[] bArr) {
            if (bArr != null) {
                try {
                    this.b.onResult(new UnderstanderResult(new String(bArr, XML.CHARSET_UTF8)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NullPointerException e2) {
                    e2.printStackTrace();
                }
            }
        }

        @Override // com.iflytek.cloud.SpeechListener
        public void onEvent(int i, Bundle bundle) {
        }
    }

    public n(Context context) {
        super(context);
    }

    public void a(String str, TextUnderstanderListener textUnderstanderListener) {
        if (TextUtils.isEmpty(getParameter("asr_sch"))) {
            setParameter("asr_sch", "1");
        }
        if (TextUtils.isEmpty(getParameter(SpeechConstant.NLP_VERSION))) {
            setParameter(SpeechConstant.NLP_VERSION, "2.0");
        }
        if (TextUtils.isEmpty(getParameter(SpeechConstant.RESULT_TYPE))) {
            setParameter(SpeechConstant.RESULT_TYPE, "json");
        }
        this.d = new com.iflytek.cloud.a.d.b(this.a, this.b, a("textunderstand"));
        ((com.iflytek.cloud.a.d.b) this.d).a(new e.a(new a(textUnderstanderListener)), str);
    }

    @Override // com.iflytek.cloud.a.c.e
    public void cancel(boolean z) {
        super.cancel(z);
    }

    @Override // com.iflytek.cloud.a.c.e
    public boolean destroy() {
        return super.destroy();
    }

    public boolean e() {
        return d();
    }

    @Override // com.iflytek.cloud.a.c.d
    public String getParameter(String str) {
        return super.getParameter(str);
    }

    @Override // com.iflytek.cloud.a.c.d
    public boolean setParameter(String str, String str2) {
        return super.setParameter(str, str2);
    }
}
