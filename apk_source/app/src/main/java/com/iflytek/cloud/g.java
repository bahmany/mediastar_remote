package com.iflytek.cloud;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.cloud.SpeechSynthesizer;

/* loaded from: classes.dex */
final class g extends Handler {
    final /* synthetic */ SpeechSynthesizer.a a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    g(SpeechSynthesizer.a aVar, Looper looper) {
        super(looper);
        this.a = aVar;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (this.a.b == null) {
        }
        switch (message.what) {
            case 1:
                this.a.b.onSpeakBegin();
                break;
            case 2:
                Bundle bundle = (Bundle) message.obj;
                this.a.b.onBufferProgress(bundle.getInt("percent"), bundle.getInt("begpos"), bundle.getInt("endpos"), bundle.getString("spellinfo"));
                break;
            case 3:
                this.a.b.onSpeakPaused();
                break;
            case 4:
                this.a.b.onSpeakResumed();
                break;
            case 5:
                this.a.b.onSpeakProgress(message.arg1, message.arg2, ((Integer) message.obj).intValue());
                break;
            case 6:
                this.a.b.onCompleted((SpeechError) message.obj);
                break;
        }
    }
}
