package com.iflytek.cloud.d.a;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.d.a.g;

/* loaded from: classes.dex */
final class k extends Handler {
    final /* synthetic */ String a;
    final /* synthetic */ g.a b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    k(g.a aVar, Looper looper, String str) {
        super(looper);
        this.b = aVar;
        this.a = str;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (this.b.j == null) {
        }
        switch (message.what) {
            case 2:
                this.b.j.onBufferProgress(message.arg1);
                break;
            case 6:
                this.b.j.onSynthesizeCompleted(this.a, (SpeechError) message.obj);
                break;
        }
    }
}
