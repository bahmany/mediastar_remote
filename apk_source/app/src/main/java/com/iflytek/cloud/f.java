package com.iflytek.cloud;

import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;

/* loaded from: classes.dex */
final class f extends SynthesizerListener.Stub {
    final /* synthetic */ SpeechSynthesizer a;
    final /* synthetic */ SpeechSynthesizer.a b;

    f(SpeechSynthesizer.a aVar, SpeechSynthesizer speechSynthesizer) {
        this.b = aVar;
        this.a = speechSynthesizer;
    }

    @Override // com.iflytek.speech.SynthesizerListener
    public void onBufferProgress(int i) throws RemoteException {
        if (this.b.b != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("percent", i);
            bundle.putInt("begpos", 0);
            bundle.putInt("endpos", 0);
            bundle.putString("spellinfo", "");
            if (this.b.b != null) {
                Message.obtain(this.b.d, 2, bundle).sendToTarget();
            }
        }
    }

    @Override // com.iflytek.speech.SynthesizerListener
    public void onCompleted(int i) throws RemoteException {
        if (this.b.b != null) {
            Message.obtain(this.b.d, 6, i == 0 ? null : new SpeechError(i)).sendToTarget();
        }
    }

    @Override // com.iflytek.speech.SynthesizerListener
    public void onSpeakBegin() throws RemoteException {
        if (this.b.b != null) {
            Message.obtain(this.b.d, 1).sendToTarget();
        }
    }

    @Override // com.iflytek.speech.SynthesizerListener
    public void onSpeakPaused() throws RemoteException {
        if (this.b.b != null) {
            Message.obtain(this.b.d, 3).sendToTarget();
        }
    }

    @Override // com.iflytek.speech.SynthesizerListener
    public void onSpeakProgress(int i) throws RemoteException {
        if (this.b.b != null) {
            Message.obtain(this.b.d, 5, i, 0, 0).sendToTarget();
        }
    }

    @Override // com.iflytek.speech.SynthesizerListener
    public void onSpeakResumed() throws RemoteException {
        if (this.b.b != null) {
            Message.obtain(this.b.d, 4, 0, 0, null).sendToTarget();
        }
    }
}
