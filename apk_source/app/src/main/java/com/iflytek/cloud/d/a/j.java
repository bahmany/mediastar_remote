package com.iflytek.cloud.d.a;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.d.a.g;

/* loaded from: classes.dex */
final class j extends Handler {
    final /* synthetic */ g.a a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    j(g.a aVar, Looper looper) {
        super(looper);
        this.a = aVar;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (this.a.i == null) {
        }
        switch (message.what) {
            case 1:
                this.a.i.onSpeakBegin();
                break;
            case 2:
                Bundle bundle = (Bundle) message.obj;
                int i = bundle.getInt("percent");
                int i2 = bundle.getInt("begpos");
                int i3 = bundle.getInt("endpos");
                String string = bundle.getString("spellinfo");
                if (this.a.i != null) {
                    this.a.i.onBufferProgress(i, i2, i3, string);
                    break;
                }
                break;
            case 3:
                this.a.i.onSpeakPaused();
                break;
            case 4:
                this.a.i.onSpeakResumed();
                break;
            case 5:
                int iIntValue = ((Integer) message.obj).intValue();
                if (this.a.i != null) {
                    this.a.i.onSpeakProgress(message.arg1, message.arg2, iIntValue);
                    break;
                }
                break;
            case 6:
                this.a.i.onCompleted((SpeechError) message.obj);
                break;
        }
    }
}
