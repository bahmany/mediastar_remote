package com.iflytek.cloud;

import android.os.Message;
import android.os.RemoteException;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.speech.SynthesizeToUrlListener;

/* loaded from: classes.dex */
final class h extends SynthesizeToUrlListener.Stub {
    final /* synthetic */ SpeechSynthesizer a;
    final /* synthetic */ SpeechSynthesizer.b b;

    h(SpeechSynthesizer.b bVar, SpeechSynthesizer speechSynthesizer) {
        this.b = bVar;
        this.a = speechSynthesizer;
    }

    @Override // com.iflytek.speech.SynthesizeToUrlListener
    public void onSynthesizeCompleted(String str, int i) throws RemoteException {
        if (this.b.b != null) {
            Message.obtain(this.b.d, 2, i, 0, str).sendToTarget();
        }
    }
}
