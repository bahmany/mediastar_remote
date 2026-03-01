package com.hisilicon.multiscreen.protocol.utils;

/* loaded from: classes.dex */
public interface ISpeechMsgDealer {
    void pushAudioData(byte[] bArr, int i);

    void pushSpeechInfo(String str, int i);

    void speakError(String str);

    void startSpeaking();

    void stopSpeaking();
}
