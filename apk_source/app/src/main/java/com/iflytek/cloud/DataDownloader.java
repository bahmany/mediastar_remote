package com.iflytek.cloud;

import android.content.Context;
import com.iflytek.cloud.a.c.e;

/* loaded from: classes.dex */
public class DataDownloader extends com.iflytek.cloud.a.c.e {
    public DataDownloader(Context context) {
        super(context);
    }

    @Override // com.iflytek.cloud.a.c.e
    protected boolean a() {
        return true;
    }

    public void downloadData(SpeechListener speechListener) {
        this.d = new com.iflytek.cloud.a.d.b(this.a, this.b, a("download"));
        ((com.iflytek.cloud.a.d.b) this.d).a(new e.a(speechListener));
    }
}
