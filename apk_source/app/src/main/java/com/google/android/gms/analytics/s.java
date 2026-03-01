package com.google.android.gms.analytics;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.internal.hb;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: classes.dex */
class s extends Thread implements f {
    private static s yX;
    private volatile boolean mClosed;
    private final Context mContext;
    private final LinkedBlockingQueue<Runnable> yT;
    private volatile boolean yU;
    private volatile List<hb> yV;
    private volatile String yW;
    private volatile af yY;

    private s(Context context) {
        super("GAThread");
        this.yT = new LinkedBlockingQueue<>();
        this.yU = false;
        this.mClosed = false;
        if (context != null) {
            this.mContext = context.getApplicationContext();
        } else {
            this.mContext = context;
        }
        start();
    }

    static s B(Context context) {
        if (yX == null) {
            yX = new s(context);
        }
        return yX;
    }

    static String C(Context context) throws IOException {
        String str = null;
        try {
            FileInputStream fileInputStreamOpenFileInput = context.openFileInput("gaInstallData");
            byte[] bArr = new byte[8192];
            int i = fileInputStreamOpenFileInput.read(bArr, 0, 8192);
            if (fileInputStreamOpenFileInput.available() > 0) {
                z.T("Too much campaign data, ignoring it.");
                fileInputStreamOpenFileInput.close();
                context.deleteFile("gaInstallData");
            } else {
                fileInputStreamOpenFileInput.close();
                context.deleteFile("gaInstallData");
                if (i <= 0) {
                    z.W("Campaign file is empty.");
                } else {
                    String str2 = new String(bArr, 0, i);
                    z.U("Campaign found: " + str2);
                    str = str2;
                }
            }
        } catch (FileNotFoundException e) {
            z.U("No campaign data found.");
        } catch (IOException e2) {
            z.T("Error reading campaign data.");
            context.deleteFile("gaInstallData");
        }
        return str;
    }

    static int ah(String str) {
        int i = 1;
        if (!TextUtils.isEmpty(str)) {
            i = 0;
            for (int length = str.length() - 1; length >= 0; length--) {
                char cCharAt = str.charAt(length);
                i = ((i << 6) & 268435455) + cCharAt + (cCharAt << 14);
                int i2 = 266338304 & i;
                if (i2 != 0) {
                    i ^= i2 >> 21;
                }
            }
        }
        return i;
    }

    private String g(Throwable th) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        th.printStackTrace(printStream);
        printStream.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String v(Map<String, String> map) {
        return (!map.containsKey("useSecure") || aj.e(map.get("useSecure"), true)) ? "https:" : "http:";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean w(Map<String, String> map) {
        if (map.get("&sf") == null) {
            return false;
        }
        double dA = aj.a(map.get("&sf"), 100.0d);
        if (dA < 100.0d && ah(map.get("&cid")) % 10000 >= dA * 100.0d) {
            z.V(String.format("%s hit sampled out", map.get("&t") == null ? "unknown" : map.get("&t")));
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void x(Map<String, String> map) {
        l lVarW = a.w(this.mContext);
        aj.a(map, "&adid", lVarW);
        aj.a(map, "&ate", lVarW);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void y(Map<String, String> map) {
        g gVarDQ = g.dQ();
        aj.a(map, "&an", gVarDQ);
        aj.a(map, "&av", gVarDQ);
        aj.a(map, "&aid", gVarDQ);
        aj.a(map, "&aiid", gVarDQ);
        map.put("&v", "1");
    }

    void b(Runnable runnable) {
        this.yT.add(runnable);
    }

    @Override // com.google.android.gms.analytics.f
    public void dI() {
        b(new Runnable() { // from class: com.google.android.gms.analytics.s.3
            @Override // java.lang.Runnable
            public void run() {
                s.this.yY.dI();
            }
        });
    }

    @Override // com.google.android.gms.analytics.f
    public void dO() {
        b(new Runnable() { // from class: com.google.android.gms.analytics.s.4
            @Override // java.lang.Runnable
            public void run() {
                s.this.yY.dO();
            }
        });
    }

    @Override // com.google.android.gms.analytics.f
    public LinkedBlockingQueue<Runnable> dP() {
        return this.yT;
    }

    @Override // com.google.android.gms.analytics.f
    public void dispatch() {
        b(new Runnable() { // from class: com.google.android.gms.analytics.s.2
            @Override // java.lang.Runnable
            public void run() {
                s.this.yY.dispatch();
            }
        });
    }

    @Override // com.google.android.gms.analytics.f
    public Thread getThread() {
        return this;
    }

    protected void init() {
        this.yY.eh();
        this.yV = new ArrayList();
        this.yV.add(new hb("appendVersion", "&_v".substring(1), "ma4.0.3"));
        this.yV.add(new hb("appendQueueTime", "&qt".substring(1), null));
        this.yV.add(new hb("appendCacheBuster", "&z".substring(1), null));
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() throws InterruptedException, SecurityException, IllegalArgumentException {
        Process.setThreadPriority(10);
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            z.W("sleep interrupted in GAThread initialize");
        }
        try {
            if (this.yY == null) {
                this.yY = new r(this.mContext, this);
            }
            init();
            this.yW = C(this.mContext);
            z.V("Initialized GA Thread");
        } catch (Throwable th) {
            z.T("Error initializing the GAThread: " + g(th));
            z.T("Google Analytics will not start up.");
            this.yU = true;
        }
        while (!this.mClosed) {
            try {
                try {
                    Runnable runnableTake = this.yT.take();
                    if (!this.yU) {
                        runnableTake.run();
                    }
                } catch (InterruptedException e2) {
                    z.U(e2.toString());
                }
            } catch (Throwable th2) {
                z.T("Error on GAThread: " + g(th2));
                z.T("Google Analytics is shutting down.");
                this.yU = true;
            }
        }
    }

    @Override // com.google.android.gms.analytics.f
    public void u(Map<String, String> map) throws NumberFormatException {
        final HashMap map2 = new HashMap(map);
        String str = map.get("&ht");
        if (str != null) {
            try {
                Long.valueOf(str);
            } catch (NumberFormatException e) {
                str = null;
            }
        }
        if (str == null) {
            map2.put("&ht", Long.toString(System.currentTimeMillis()));
        }
        b(new Runnable() { // from class: com.google.android.gms.analytics.s.1
            @Override // java.lang.Runnable
            public void run() {
                s.this.x(map2);
                if (TextUtils.isEmpty((CharSequence) map2.get("&cid"))) {
                    map2.put("&cid", h.dR().getValue("&cid"));
                }
                if (GoogleAnalytics.getInstance(s.this.mContext).getAppOptOut() || s.this.w(map2)) {
                    return;
                }
                if (!TextUtils.isEmpty(s.this.yW)) {
                    t.eq().B(true);
                    map2.putAll(new HitBuilders.HitBuilder().setCampaignParamsFromUrl(s.this.yW).build());
                    t.eq().B(false);
                    s.this.yW = null;
                }
                s.this.y(map2);
                s.this.yY.b(x.z(map2), Long.valueOf((String) map2.get("&ht")).longValue(), s.this.v(map2), s.this.yV);
            }
        });
    }
}
