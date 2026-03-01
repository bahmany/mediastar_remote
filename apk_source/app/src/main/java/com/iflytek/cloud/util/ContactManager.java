package com.iflytek.cloud.util;

import android.content.Context;
import com.iflytek.cloud.util.a.b;

/* loaded from: classes.dex */
public abstract class ContactManager {

    public interface ContactListener {
        void onContactQueryFinish(String str, boolean z);
    }

    protected ContactManager() {
    }

    public static ContactManager createManager(Context context, ContactListener contactListener) {
        return b.a(context, contactListener);
    }

    public static ContactManager getManager() {
        return b.a();
    }

    public abstract void asyncQueryAllContactsName();

    public abstract String queryAllContactsName();
}
