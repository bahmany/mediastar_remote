package com.iflytek.cloud;

import android.content.Context;
import com.iflytek.cloud.a.c.e;

/* loaded from: classes.dex */
public class DataUploader extends com.iflytek.cloud.a.c.e {
    public DataUploader(Context context) {
        super(context);
    }

    @Override // com.iflytek.cloud.a.c.e
    protected boolean a() {
        return true;
    }

    public void uploadData(SpeechListener speechListener, String str, byte[] bArr) {
        this.d = new com.iflytek.cloud.a.d.b(this.a, this.b, a("upload"));
        ((com.iflytek.cloud.a.d.b) this.d).a(new e.a(speechListener), str, bArr);
    }
}
