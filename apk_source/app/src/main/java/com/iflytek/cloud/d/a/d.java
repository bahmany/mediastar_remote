package com.iflytek.cloud.d.a;

import android.os.Bundle;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;

/* loaded from: classes.dex */
final class d implements SpeechListener {
    final /* synthetic */ GrammarListener a;
    final /* synthetic */ c b;

    d(c cVar, GrammarListener grammarListener) {
        this.b = cVar;
        this.a = grammarListener;
    }

    @Override // com.iflytek.cloud.SpeechListener
    public void onCompleted(SpeechError speechError) {
        if (speechError != null) {
            this.a.onBuildFinish(null, speechError);
        }
    }

    @Override // com.iflytek.cloud.SpeechListener
    public void onData(byte[] bArr) {
        this.a.onBuildFinish(new String(bArr), null);
    }

    @Override // com.iflytek.cloud.SpeechListener
    public void onEvent(int i, Bundle bundle) {
    }
}
