package com.iflytek.cloud;

import android.content.Context;
import com.iflytek.cloud.d.a.n;

/* loaded from: classes.dex */
public class TextUnderstander extends com.iflytek.cloud.a.c.d {
    private n a;

    public TextUnderstander(Context context, InitListener initListener) {
        this.a = null;
        this.a = new n(context);
        if (initListener != null) {
            initListener.onInit(0);
        }
    }

    public void cancel() {
        this.a.cancel(false);
    }

    public boolean destroy() {
        return this.a.destroy();
    }

    @Override // com.iflytek.cloud.a.c.d
    public String getParameter(String str) {
        return super.getParameter(str);
    }

    public boolean isUnderstanding() {
        return this.a.e();
    }

    @Override // com.iflytek.cloud.a.c.d
    public boolean setParameter(String str, String str2) {
        return super.setParameter(str, str2);
    }

    public int understandText(String str, TextUnderstanderListener textUnderstanderListener) {
        this.a.setParameter(SpeechConstant.PARAMS, null);
        this.a.setParameter(SpeechConstant.PARAMS, this.b.toString());
        this.a.a(str, textUnderstanderListener);
        return 0;
    }
}
