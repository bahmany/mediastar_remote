package com.iflytek.cloud.ui;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

/* loaded from: classes.dex */
final class b implements RecognizerListener {
    final /* synthetic */ a a;

    b(a aVar) {
        this.a = aVar;
    }

    @Override // com.iflytek.cloud.RecognizerListener
    public void onBeginOfSpeech() {
    }

    @Override // com.iflytek.cloud.RecognizerListener
    public void onEndOfSpeech() {
        this.a.j();
    }

    @Override // com.iflytek.cloud.RecognizerListener
    public void onError(SpeechError speechError) {
        if (speechError == null || !this.a.b) {
            this.a.f();
        } else {
            this.a.a(speechError);
        }
        if (this.a.h != null) {
            this.a.h.onError(speechError);
        }
    }

    @Override // com.iflytek.cloud.RecognizerListener
    public void onEvent(int i, int i2, int i3, String str) {
    }

    @Override // com.iflytek.cloud.RecognizerListener
    public void onResult(RecognizerResult recognizerResult, boolean z) {
        if (z) {
            this.a.f();
        }
        if (this.a.h != null) {
            this.a.h.onResult(recognizerResult, z);
        }
    }

    @Override // com.iflytek.cloud.RecognizerListener
    public void onVolumeChanged(int i) {
        if (this.a.k == 1) {
            this.a.e.a((i + 2) / 5);
            this.a.e.invalidate();
        }
    }
}
