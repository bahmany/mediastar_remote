package com.iflytek.cloud;

/* loaded from: classes.dex */
public interface RecognizerListener {
    void onBeginOfSpeech();

    void onEndOfSpeech();

    void onError(SpeechError speechError);

    void onEvent(int i, int i2, int i3, String str);

    void onResult(RecognizerResult recognizerResult, boolean z);

    void onVolumeChanged(int i);
}
