package com.iflytek.cloud.d.a;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VerifierResult;
import com.iflytek.cloud.d.a.a;

/* loaded from: classes.dex */
final class b extends Handler {
    final /* synthetic */ a.C0148a a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    b(a.C0148a c0148a, Looper looper) {
        super(looper);
        this.a = c0148a;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (this.a.b == null) {
            return;
        }
        switch (message.what) {
            case 0:
                this.a.b.onError((SpeechError) message.obj);
                break;
            case 1:
                this.a.b.onVolumeChanged(message.arg1, (byte[]) message.obj);
                break;
            case 2:
                this.a.b.onBeginOfSpeech();
                break;
            case 3:
                this.a.b.onEndOfSpeech();
                break;
            case 4:
                this.a.b.onResult((VerifierResult) message.obj);
                break;
        }
        super.handleMessage(message);
    }
}
