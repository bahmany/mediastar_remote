package com.iflytek.cloud;

/* loaded from: classes.dex */
public interface SynthesizeToUriListener {
    void onBufferProgress(int i);

    void onSynthesizeCompleted(String str, SpeechError speechError);
}
