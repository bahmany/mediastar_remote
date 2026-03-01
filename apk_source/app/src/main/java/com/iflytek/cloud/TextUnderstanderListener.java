package com.iflytek.cloud;

/* loaded from: classes.dex */
public interface TextUnderstanderListener {
    void onError(SpeechError speechError);

    void onResult(UnderstanderResult understanderResult);
}
