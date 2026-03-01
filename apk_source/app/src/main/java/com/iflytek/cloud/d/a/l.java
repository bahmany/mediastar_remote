package com.iflytek.cloud.d.a;

import android.os.Handler;
import android.os.Message;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.d.a.g;
import java.io.IOException;
import java.util.ArrayList;

/* loaded from: classes.dex */
final class l implements com.iflytek.cloud.a.e.b {
    final /* synthetic */ Handler a;
    final /* synthetic */ g.a b;

    l(g.a aVar, Handler handler) {
        this.b = aVar;
        this.a = handler;
    }

    @Override // com.iflytek.cloud.a.e.b
    public void a(SpeechError speechError) {
        if (this.b.j != null) {
            Message.obtain(this.a, 6, speechError).sendToTarget();
        }
    }

    @Override // com.iflytek.cloud.a.e.b
    public void a(ArrayList<byte[]> arrayList, int i, int i2, int i3, String str) throws IOException {
        try {
            this.b.h.a(arrayList, i, i2, i3);
            if (i >= 100 && !this.b.h.b()) {
                throw new IOException();
            }
            if (this.b.j != null) {
                Message.obtain(this.a, 2, i, 0).sendToTarget();
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.b.cancel(false);
            this.b.h.h();
            if (this.b.j != null) {
                Message.obtain(this.a, 6, new SpeechError(ErrorCode.ERROR_FILE_ACCESS)).sendToTarget();
            }
        }
    }
}
