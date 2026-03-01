package com.iflytek.cloud.util.a;

import java.io.IOException;

/* loaded from: classes.dex */
final class c implements Runnable {
    final /* synthetic */ b a;

    c(b bVar) {
        this.a = bVar;
    }

    @Override // java.lang.Runnable
    public void run() throws IOException {
        this.a.b();
    }
}
