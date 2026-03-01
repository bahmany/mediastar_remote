package com.iflytek.cloud.d.a;

import android.os.Message;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.c.b;
import com.iflytek.cloud.d.a.g;

/* loaded from: classes.dex */
final class i implements b.InterfaceC0147b {
    final /* synthetic */ g.a a;

    i(g.a aVar) {
        this.a = aVar;
    }

    @Override // com.iflytek.cloud.c.b.InterfaceC0147b
    public void a() {
        if (this.a.i != null) {
            Message.obtain(this.a.o, 3).sendToTarget();
        }
    }

    @Override // com.iflytek.cloud.c.b.InterfaceC0147b
    public void a(int i, int i2, int i3) {
        Message.obtain(this.a.o, 5, i, i2, Integer.valueOf(i3)).sendToTarget();
    }

    @Override // com.iflytek.cloud.c.b.InterfaceC0147b
    public void a(SpeechError speechError) {
        Message.obtain(this.a.o, 6, speechError).sendToTarget();
        if (this.a.g != null) {
            this.a.g.e();
        }
        this.a.cancel(false);
    }

    @Override // com.iflytek.cloud.c.b.InterfaceC0147b
    public void b() {
        if (this.a.i != null) {
            Message.obtain(this.a.o, 4).sendToTarget();
        }
    }

    @Override // com.iflytek.cloud.c.b.InterfaceC0147b
    public void c() {
        Message.obtain(this.a.o, 6, null).sendToTarget();
    }
}
