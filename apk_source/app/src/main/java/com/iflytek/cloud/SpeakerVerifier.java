package com.iflytek.cloud;

import android.content.Context;

/* loaded from: classes.dex */
public class SpeakerVerifier extends com.iflytek.cloud.a.c.d {
    private static SpeakerVerifier a = null;
    private com.iflytek.cloud.d.a.a c;

    protected SpeakerVerifier(Context context, InitListener initListener) {
        this.c = null;
        this.c = new com.iflytek.cloud.d.a.a(context);
    }

    public static SpeakerVerifier createVerifier(Context context, InitListener initListener) {
        if (a == null) {
            a = new SpeakerVerifier(context, initListener);
        }
        return a;
    }

    public static SpeakerVerifier getVerifier() {
        return a;
    }

    public void cancel(boolean z) {
        this.c.cancel(z);
    }

    public boolean destroy() {
        boolean zDestroy = this.c != null ? this.c.destroy() : true;
        if (zDestroy) {
            a = null;
        }
        return zDestroy;
    }

    public String generatePassword(int i) {
        return this.c.a(i);
    }

    @Override // com.iflytek.cloud.a.c.d
    public String getParameter(String str) {
        return super.getParameter(str);
    }

    public void getPasswordList(SpeechListener speechListener, String str, String str2) {
        this.c.a(speechListener, str, str2);
    }

    public boolean isListening() {
        return this.c.f();
    }

    @Override // com.iflytek.cloud.a.c.d
    public boolean setParameter(String str, String str2) {
        return super.setParameter(str, str2);
    }

    public void startListening(VerifierListener verifierListener) {
        this.c.setParameter(SpeechConstant.PARAMS, null);
        this.c.setParameter(SpeechConstant.PARAMS, this.b.toString());
        this.c.a(verifierListener);
    }

    public void stopListening() {
        this.c.e();
    }

    public boolean writeAudio(byte[] bArr, int i, int i2) {
        return this.c.a(bArr, i, i2);
    }
}
