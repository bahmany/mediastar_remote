package com.iflytek.cloud.a.c;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.a.c.e;

/* loaded from: classes.dex */
final class f extends Handler {
    final /* synthetic */ e.a a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    f(e.a aVar, Looper looper) {
        super(looper);
        this.a = aVar;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (this.a.b == null) {
            return;
        }
        com.iflytek.cloud.a.f.a.a.a("SpeechListener onMsg = " + message.what);
        switch (message.what) {
            case 0:
                this.a.b.onEvent(message.arg1, (Bundle) message.obj);
                break;
            case 1:
                this.a.b.onData((byte[]) message.obj);
                break;
            case 2:
                this.a.b.onCompleted((SpeechError) message.obj);
                break;
        }
        super.handleMessage(message);
    }
}
