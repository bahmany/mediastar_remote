package com.iflytek.cloud;

import android.os.RemoteException;
import com.iflytek.speech.GrammarListener;

/* loaded from: classes.dex */
final class b extends GrammarListener.Stub {
    final /* synthetic */ GrammarListener a;
    final /* synthetic */ SpeechRecognizer b;

    b(SpeechRecognizer speechRecognizer, GrammarListener grammarListener) {
        this.b = speechRecognizer;
        this.a = grammarListener;
    }

    @Override // com.iflytek.speech.GrammarListener
    public void onBuildFinish(String str, int i) throws RemoteException {
        if (this.a != null) {
            this.a.onBuildFinish(str, i == 0 ? null : new SpeechError(i));
        }
    }
}
