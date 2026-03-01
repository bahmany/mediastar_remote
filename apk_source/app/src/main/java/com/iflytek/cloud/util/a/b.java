package com.iflytek.cloud.util.a;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import com.iflytek.cloud.util.ContactManager;
import java.io.IOException;

/* loaded from: classes.dex */
public class b extends ContactManager {
    private static C0150b f;
    private static a g;
    private HandlerThread h;
    private Handler j;
    private long k = 0;
    private long l = 0;
    private static b a = null;
    private static Context b = null;
    private static int c = 4;
    private static com.iflytek.cloud.util.a.c.a d = null;
    private static com.iflytek.cloud.util.a.a e = null;
    private static ContactManager.ContactListener i = null;

    private class a extends ContentObserver {
        public a(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            com.iflytek.cloud.a.f.a.a.a("iFly_ContactManager", "CallLogObserver | onChange");
            if (System.currentTimeMillis() - b.this.l < 5000) {
                com.iflytek.cloud.a.f.a.a.a("iFly_ContactManager", "onChange too much");
                return;
            }
            b.this.l = System.currentTimeMillis();
            b.this.c();
        }
    }

    /* renamed from: com.iflytek.cloud.util.a.b$b, reason: collision with other inner class name */
    private class C0150b extends ContentObserver {
        public C0150b(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) throws IOException {
            com.iflytek.cloud.a.f.a.a.a("iFly_ContactManager", "ContactObserver_Contact| onChange");
            if (System.currentTimeMillis() - b.this.k < 5000) {
                com.iflytek.cloud.a.f.a.a.a("iFly_ContactManager", "onChange too much");
                return;
            }
            b.this.k = System.currentTimeMillis();
            b.this.b();
            b.this.c();
        }
    }

    private b() {
        this.h = null;
        if (Build.VERSION.SDK_INT > c) {
            d = new com.iflytek.cloud.util.a.b.b(b);
        } else {
            d = new com.iflytek.cloud.util.a.b.a(b);
        }
        e = new com.iflytek.cloud.util.a.a(b, d);
        this.h = new HandlerThread("ContactManager_worker");
        this.h.start();
        this.j = new Handler(this.h.getLooper());
        this.h.setPriority(1);
        f = new C0150b(this.j);
        g = new a(this.j);
    }

    public static b a() {
        return a;
    }

    public static b a(Context context, ContactManager.ContactListener contactListener) {
        i = contactListener;
        b = context;
        if (a == null) {
            a = new b();
            b.getContentResolver().registerContentObserver(d.a(), true, f);
            b.getContentResolver().registerContentObserver(d.f(), true, g);
        }
        return a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b() throws IOException {
        if (i == null || e == null) {
            return;
        }
        String strA = e.a(e.a(), '\n');
        String str = b.getFilesDir().getParent() + "/name.txt";
        String strA2 = d.a(str);
        if (strA == null || strA2 == null || !strA.equals(strA2)) {
            d.a(str, strA, true);
            i.onContactQueryFinish(strA, true);
        } else {
            com.iflytek.cloud.a.f.a.a.a("iFly_ContactManager", "contact name is not change.");
            i.onContactQueryFinish(strA, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c() {
        if (e != null) {
            e.a(10);
        }
    }

    @Override // com.iflytek.cloud.util.ContactManager
    public void asyncQueryAllContactsName() {
        this.j.post(new c(this));
    }

    @Override // com.iflytek.cloud.util.ContactManager
    public String queryAllContactsName() throws Throwable {
        if (e == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : e.a()) {
            sb.append(str + '\n');
        }
        return sb.toString();
    }
}
