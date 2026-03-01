package com.google.android.gms.internal;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class an extends Thread {
    private final int nf;
    private final int nh;
    private final am nu;
    private final al nv;
    private final ey nw;
    private final int nx;
    private final int ny;
    private final int nz;
    private boolean mStarted = false;
    private boolean ns = false;
    private boolean nt = false;
    private final Object mw = new Object();

    @ez
    class a {
        final int nG;
        final int nH;

        a(int i, int i2) {
            this.nG = i;
            this.nH = i2;
        }
    }

    public an(am amVar, al alVar, Bundle bundle, ey eyVar) {
        this.nu = amVar;
        this.nv = alVar;
        this.nw = eyVar;
        this.nf = bundle.getInt(bn.pe.getKey());
        this.ny = bundle.getInt(bn.pf.getKey());
        this.nh = bundle.getInt(bn.pg.getKey());
        this.nz = bundle.getInt(bn.ph.getKey());
        this.nx = bundle.getInt(bn.pi.getKey(), 10);
        setName("ContentFetchTask");
    }

    private void a(Activity activity) {
        if (activity == null) {
            return;
        }
        View viewFindViewById = null;
        if (activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            viewFindViewById = activity.getWindow().getDecorView().findViewById(R.id.content);
        }
        if (viewFindViewById != null) {
            g(viewFindViewById);
        }
    }

    private boolean a(final WebView webView, final ak akVar) {
        if (!kc.hI()) {
            return false;
        }
        akVar.aR();
        webView.post(new Runnable() { // from class: com.google.android.gms.internal.an.2
            ValueCallback<String> nC = new ValueCallback<String>() { // from class: com.google.android.gms.internal.an.2.1
                @Override // android.webkit.ValueCallback
                /* renamed from: k, reason: merged with bridge method [inline-methods] */
                public void onReceiveValue(String str) {
                    an.this.a(akVar, webView, str);
                }
            };

            @Override // java.lang.Runnable
            public void run() {
                if (webView.getSettings().getJavaScriptEnabled()) {
                    webView.evaluateJavascript("(function() { return  {text:document.body.innerText}})();", this.nC);
                }
            }
        });
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x004f, code lost:
    
        if (r0.importance != 100) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0055, code lost:
    
        if (r1.inKeyguardRestrictedInputMode() != false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x005b, code lost:
    
        if (r2.isScreenOn() == false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x005d, code lost:
    
        return true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean aW() {
        /*
            r7 = this;
            r3 = 0
            com.google.android.gms.internal.am r0 = r7.nu     // Catch: java.lang.Throwable -> L61
            android.content.Context r2 = r0.getContext()     // Catch: java.lang.Throwable -> L61
            if (r2 != 0) goto Lb
            r0 = r3
        La:
            return r0
        Lb:
            java.lang.String r0 = "activity"
            java.lang.Object r0 = r2.getSystemService(r0)     // Catch: java.lang.Throwable -> L61
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0     // Catch: java.lang.Throwable -> L61
            java.lang.String r1 = "keyguard"
            java.lang.Object r1 = r2.getSystemService(r1)     // Catch: java.lang.Throwable -> L61
            android.app.KeyguardManager r1 = (android.app.KeyguardManager) r1     // Catch: java.lang.Throwable -> L61
            java.lang.String r4 = "power"
            java.lang.Object r2 = r2.getSystemService(r4)     // Catch: java.lang.Throwable -> L61
            android.os.PowerManager r2 = (android.os.PowerManager) r2     // Catch: java.lang.Throwable -> L61
            if (r0 == 0) goto L29
            if (r1 == 0) goto L29
            if (r2 != 0) goto L2b
        L29:
            r0 = r3
            goto La
        L2b:
            java.util.List r0 = r0.getRunningAppProcesses()     // Catch: java.lang.Throwable -> L61
            if (r0 != 0) goto L33
            r0 = r3
            goto La
        L33:
            java.util.Iterator r4 = r0.iterator()     // Catch: java.lang.Throwable -> L61
        L37:
            boolean r0 = r4.hasNext()     // Catch: java.lang.Throwable -> L61
            if (r0 == 0) goto L5f
            java.lang.Object r0 = r4.next()     // Catch: java.lang.Throwable -> L61
            android.app.ActivityManager$RunningAppProcessInfo r0 = (android.app.ActivityManager.RunningAppProcessInfo) r0     // Catch: java.lang.Throwable -> L61
            int r5 = android.os.Process.myPid()     // Catch: java.lang.Throwable -> L61
            int r6 = r0.pid     // Catch: java.lang.Throwable -> L61
            if (r5 != r6) goto L37
            int r0 = r0.importance     // Catch: java.lang.Throwable -> L61
            r4 = 100
            if (r0 != r4) goto L5f
            boolean r0 = r1.inKeyguardRestrictedInputMode()     // Catch: java.lang.Throwable -> L61
            if (r0 != 0) goto L5f
            boolean r0 = r2.isScreenOn()     // Catch: java.lang.Throwable -> L61
            if (r0 == 0) goto L5f
            r0 = 1
            goto La
        L5f:
            r0 = r3
            goto La
        L61:
            r0 = move-exception
            r0 = r3
            goto La
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.an.aW():boolean");
    }

    a a(View view, ak akVar) {
        if (view == null) {
            return new a(0, 0);
        }
        if ((view instanceof TextView) && !(view instanceof EditText)) {
            akVar.i(((TextView) view).getText().toString());
            return new a(1, 0);
        }
        if ((view instanceof WebView) && !(view instanceof gv)) {
            akVar.aR();
            return a((WebView) view, akVar) ? new a(0, 1) : new a(0, 0);
        }
        if (!(view instanceof ViewGroup)) {
            return new a(0, 0);
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
            a aVarA = a(viewGroup.getChildAt(i3), akVar);
            i2 += aVarA.nG;
            i += aVarA.nH;
        }
        return new a(i2, i);
    }

    void a(ak akVar, WebView webView, String str) {
        akVar.aQ();
        try {
            if (!TextUtils.isEmpty(str)) {
                String strOptString = new JSONObject(str).optString("text");
                if (TextUtils.isEmpty(webView.getTitle())) {
                    akVar.h(strOptString);
                } else {
                    akVar.h(webView.getTitle() + "\n" + strOptString);
                }
            }
            if (akVar.aN()) {
                this.nv.b(akVar);
            }
        } catch (JSONException e) {
            gs.S("Json string may be malformed.");
        } catch (Throwable th) {
            gs.a("Failed to get webview content.", th);
            this.nw.b(th);
        }
    }

    public void aV() {
        synchronized (this.mw) {
            if (this.mStarted) {
                gs.S("Content hash thread already started, quiting...");
            } else {
                this.mStarted = true;
                start();
            }
        }
    }

    public ak aX() {
        return this.nv.aU();
    }

    public void aY() {
        synchronized (this.mw) {
            this.ns = true;
            gs.S("ContentFetchThread: paused, mPause = " + this.ns);
        }
    }

    public boolean aZ() {
        return this.ns;
    }

    boolean g(final View view) {
        if (view == null) {
            return false;
        }
        view.post(new Runnable() { // from class: com.google.android.gms.internal.an.1
            @Override // java.lang.Runnable
            public void run() {
                an.this.h(view);
            }
        });
        return true;
    }

    void h(View view) {
        try {
            ak akVar = new ak(this.nf, this.ny, this.nh, this.nz);
            a aVarA = a(view, akVar);
            akVar.aS();
            if (aVarA.nG == 0 && aVarA.nH == 0) {
                return;
            }
            if (aVarA.nH == 0 && akVar.aT() == 0) {
                return;
            }
            if (aVarA.nH == 0 && this.nv.a(akVar)) {
                return;
            }
            this.nv.c(akVar);
        } catch (Exception e) {
            gs.b("Exception in fetchContentOnUIThread", e);
            this.nw.b(e);
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (!this.nt) {
            try {
            } catch (Throwable th) {
                gs.b("Error in ContentFetchTask", th);
                this.nw.b(th);
            }
            if (aW()) {
                Activity activity = this.nu.getActivity();
                if (activity == null) {
                    gs.S("ContentFetchThread: no activity");
                } else {
                    a(activity);
                }
            } else {
                gs.S("ContentFetchTask: sleeping");
                aY();
            }
            Thread.sleep(this.nx * 1000);
            synchronized (this.mw) {
                while (this.ns) {
                    try {
                        gs.S("ContentFetchTask: waiting");
                        this.mw.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    public void wakeup() {
        synchronized (this.mw) {
            this.ns = false;
            this.mw.notifyAll();
            gs.S("ContentFetchThread: wakeup");
        }
    }
}
