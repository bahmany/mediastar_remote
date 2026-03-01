package com.iflytek.cloud;

import android.os.Bundle;

/* loaded from: classes.dex */
public interface SpeechListener {
    void onCompleted(SpeechError speechError);

    void onData(byte[] bArr);

    void onEvent(int i, Bundle bundle);
}
