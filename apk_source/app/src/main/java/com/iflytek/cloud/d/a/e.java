package com.iflytek.cloud.d.a;

import android.os.Bundle;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;

/* loaded from: classes.dex */
final class e implements SpeechListener {
    final /* synthetic */ LexiconListener a;
    final /* synthetic */ c b;

    e(c cVar, LexiconListener lexiconListener) {
        this.b = cVar;
        this.a = lexiconListener;
    }

    @Override // com.iflytek.cloud.SpeechListener
    public void onCompleted(SpeechError speechError) {
        this.a.onLexiconUpdated(null, speechError);
    }

    @Override // com.iflytek.cloud.SpeechListener
    public void onData(byte[] bArr) {
    }

    @Override // com.iflytek.cloud.SpeechListener
    public void onEvent(int i, Bundle bundle) {
    }
}
