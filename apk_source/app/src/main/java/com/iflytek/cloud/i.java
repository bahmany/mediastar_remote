package com.iflytek.cloud;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.cloud.SpeechSynthesizer;

/* loaded from: classes.dex */
final class i extends Handler {
    final /* synthetic */ SpeechSynthesizer.b a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    i(SpeechSynthesizer.b bVar, Looper looper) {
        super(looper);
        this.a = bVar;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (this.a.b == null) {
        }
        switch (message.what) {
            case 1:
                this.a.b.onBufferProgress(message.arg1);
                break;
            case 2:
                int i = message.arg1;
                this.a.b.onSynthesizeCompleted((String) message.obj, i == 0 ? null : new SpeechError(i));
                break;
        }
    }
}
