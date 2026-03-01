package com.iflytek.cloud;

/* loaded from: classes.dex */
public interface VerifierListener {
    void onBeginOfSpeech();

    void onEndOfSpeech();

    void onError(SpeechError speechError);

    void onResult(VerifierResult verifierResult);

    void onVolumeChanged(int i, byte[] bArr);
}
