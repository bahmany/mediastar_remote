package com.google.android.gms.analytics;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: classes.dex */
interface f {
    void dI();

    void dO();

    LinkedBlockingQueue<Runnable> dP();

    void dispatch();

    Thread getThread();

    void u(Map<String, String> map);
}
