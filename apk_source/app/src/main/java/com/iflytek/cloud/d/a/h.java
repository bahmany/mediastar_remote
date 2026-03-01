package com.iflytek.cloud.d.a;

import android.os.Bundle;
import android.os.Message;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.d.a.g;
import java.io.IOException;
import java.util.ArrayList;

/* loaded from: classes.dex */
final class h implements com.iflytek.cloud.a.e.b {
    final /* synthetic */ g.a a;

    h(g.a aVar) {
        this.a = aVar;
    }

    @Override // com.iflytek.cloud.a.e.b
    public void a(SpeechError speechError) {
        if (this.a.i == null || speechError == null) {
            return;
        }
        Message.obtain(this.a.o, 6, speechError).sendToTarget();
        this.a.g.e();
    }

    @Override // com.iflytek.cloud.a.e.b
    public void a(ArrayList<byte[]> arrayList, int i, int i2, int i3, String str) {
        Bundle bundle = new Bundle();
        bundle.putInt("percent", i);
        bundle.putInt("begpos", i2);
        bundle.putInt("endpos", i3);
        bundle.putString("spellinfo", str);
        if (this.a.i != null) {
            Message.obtain(this.a.o, 2, bundle).sendToTarget();
        }
        try {
            this.a.h.a(arrayList, i, i2, i3);
            if (i >= 100) {
                this.a.h.b();
            }
            if (this.a.l || !this.a.h.a(this.a.k)) {
                return;
            }
            this.a.l = true;
            this.a.g.a(this.a.h, this.a.n);
            if (this.a.i != null) {
                Message.obtain(this.a.o, 1).sendToTarget();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Message.obtain(this.a.o, 6, new SpeechError(ErrorCode.ERROR_FILE_ACCESS)).sendToTarget();
            this.a.cancel(false);
        }
    }
}
