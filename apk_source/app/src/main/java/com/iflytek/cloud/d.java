package com.iflytek.cloud;

import android.os.RemoteException;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.speech.RecognizerListener;

/* loaded from: classes.dex */
final class d extends RecognizerListener.Stub {
    final /* synthetic */ SpeechRecognizer a;
    final /* synthetic */ SpeechRecognizer.a b;

    d(SpeechRecognizer.a aVar, SpeechRecognizer speechRecognizer) {
        this.b = aVar;
        this.a = speechRecognizer;
    }

    @Override // com.iflytek.speech.RecognizerListener
    public void onBeginOfSpeech() throws RemoteException {
        this.b.d.sendMessage(this.b.d.obtainMessage(2));
    }

    @Override // com.iflytek.speech.RecognizerListener
    public void onEndOfSpeech() throws RemoteException {
        this.b.d.sendMessage(this.b.d.obtainMessage(3));
    }

    @Override // com.iflytek.speech.RecognizerListener
    public void onError(int i) throws RemoteException {
        this.b.d.sendMessage(this.b.d.obtainMessage(0, new SpeechError(i)));
    }

    @Override // com.iflytek.speech.RecognizerListener
    public void onResult(com.iflytek.speech.RecognizerResult recognizerResult, boolean z) throws RemoteException {
        this.b.d.sendMessage(this.b.d.obtainMessage(4, !z ? 0 : 1, 0, new RecognizerResult(recognizerResult.getResultString())));
    }

    @Override // com.iflytek.speech.RecognizerListener
    public void onVolumeChanged(int i) throws RemoteException {
        this.b.d.sendMessage(this.b.d.obtainMessage(1, i, 0, null));
    }
}
