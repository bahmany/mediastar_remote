package com.iflytek.cloud;

import android.content.Context;
import com.iflytek.cloud.d.a.m;

/* loaded from: classes.dex */
public class SpeechUnderstander extends com.iflytek.cloud.a.c.d {
    public static final String SCENE = "scene";
    protected static SpeechUnderstander a = null;
    private m c;

    protected SpeechUnderstander(Context context, InitListener initListener) {
        this.c = null;
        setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        this.c = m.a(context);
        if (initListener != null) {
            initListener.onInit(0);
        }
    }

    public static synchronized SpeechUnderstander createUnderstander(Context context, InitListener initListener) {
        if (a == null) {
            a = new SpeechUnderstander(context, initListener);
        }
        return a;
    }

    public static SpeechUnderstander getUnderstander() {
        return a;
    }

    public void cancel() {
        this.c.a(false);
    }

    public boolean destroy() {
        boolean zC = this.c != null ? this.c.c() : true;
        if (zC) {
            a = null;
        }
        return zC;
    }

    @Override // com.iflytek.cloud.a.c.d
    public String getParameter(String str) {
        return super.getParameter(str);
    }

    public boolean isUnderstanding() {
        return this.c.a();
    }

    @Override // com.iflytek.cloud.a.c.d
    public boolean setParameter(String str, String str2) {
        return super.setParameter(str, str2);
    }

    public int startUnderstanding(SpeechUnderstanderListener speechUnderstanderListener) {
        this.c.a(SpeechConstant.PARAMS, null);
        this.c.a(SpeechConstant.PARAMS, this.b.toString());
        this.c.a(speechUnderstanderListener);
        return 0;
    }

    public void stopUnderstanding() {
        this.c.b();
    }

    public boolean writeAudio(byte[] bArr, int i, int i2) {
        return this.c.a(bArr, i, i2);
    }
}
