package com.google.android.gms.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.v7.internal.widget.ActivityChooserView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class gv extends WebView implements DownloadListener {
    private final WindowManager mG;
    private final Object mw;
    private ay qr;
    private final gt qs;
    private final k sX;
    private final gw wH;
    private final a wI;
    private dk wJ;
    private boolean wK;
    private boolean wL;
    private boolean wM;
    private boolean wN;

    @ez
    private static class a extends MutableContextWrapper {
        private Context mD;
        private Activity wO;

        public a(Context context) {
            super(context);
            setBaseContext(context);
        }

        public Context dA() {
            return this.wO;
        }

        @Override // android.content.MutableContextWrapper
        public void setBaseContext(Context base) {
            this.mD = base.getApplicationContext();
            this.wO = base instanceof Activity ? (Activity) base : null;
            super.setBaseContext(this.mD);
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public void startActivity(Intent intent) {
            if (this.wO != null) {
                this.wO.startActivity(intent);
            } else {
                intent.setFlags(268435456);
                this.mD.startActivity(intent);
            }
        }
    }

    private gv(a aVar, ay ayVar, boolean z, boolean z2, k kVar, gt gtVar) {
        super(aVar);
        this.mw = new Object();
        this.wI = aVar;
        this.qr = ayVar;
        this.wK = z;
        this.sX = kVar;
        this.qs = gtVar;
        this.mG = (WindowManager) getContext().getSystemService("window");
        setBackgroundColor(0);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSavePassword(false);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        gj.a(aVar, gtVar.wD, settings);
        if (Build.VERSION.SDK_INT >= 17) {
            gp.a(getContext(), settings);
        } else if (Build.VERSION.SDK_INT >= 11) {
            gn.a(getContext(), settings);
        }
        setDownloadListener(this);
        if (Build.VERSION.SDK_INT >= 11) {
            this.wH = new gy(this, z2);
        } else {
            this.wH = new gw(this, z2);
        }
        setWebViewClient(this.wH);
        if (Build.VERSION.SDK_INT >= 14) {
            setWebChromeClient(new gz(this));
        } else if (Build.VERSION.SDK_INT >= 11) {
            setWebChromeClient(new gx(this));
        }
        dB();
    }

    public static gv a(Context context, ay ayVar, boolean z, boolean z2, k kVar, gt gtVar) {
        return new gv(new a(context), ayVar, z, z2, kVar, gtVar);
    }

    private void dB() {
        synchronized (this.mw) {
            if (this.wK || this.qr.og) {
                if (Build.VERSION.SDK_INT < 14) {
                    gs.S("Disabling hardware acceleration on an overlay.");
                    dC();
                } else {
                    gs.S("Enabling hardware acceleration on an overlay.");
                    dD();
                }
            } else if (Build.VERSION.SDK_INT < 18) {
                gs.S("Disabling hardware acceleration on an AdView.");
                dC();
            } else {
                gs.S("Enabling hardware acceleration on an AdView.");
                dD();
            }
        }
    }

    private void dC() {
        synchronized (this.mw) {
            if (!this.wL && Build.VERSION.SDK_INT >= 11) {
                gn.i(this);
            }
            this.wL = true;
        }
    }

    private void dD() {
        synchronized (this.mw) {
            if (this.wL && Build.VERSION.SDK_INT >= 11) {
                gn.j(this);
            }
            this.wL = false;
        }
    }

    protected void X(String str) {
        synchronized (this.mw) {
            if (isDestroyed()) {
                gs.W("The webview is destroyed. Ignoring action.");
            } else {
                loadUrl(str);
            }
        }
    }

    public ay Y() {
        ay ayVar;
        synchronized (this.mw) {
            ayVar = this.qr;
        }
        return ayVar;
    }

    public void a(Context context, ay ayVar) {
        synchronized (this.mw) {
            this.wI.setBaseContext(context);
            this.wJ = null;
            this.qr = ayVar;
            this.wK = false;
            this.wN = false;
            gj.b(this);
            loadUrl("about:blank");
            this.wH.reset();
            setOnTouchListener(null);
            setOnClickListener(null);
        }
    }

    public void a(ay ayVar) {
        synchronized (this.mw) {
            this.qr = ayVar;
            requestLayout();
        }
    }

    public void a(dk dkVar) {
        synchronized (this.mw) {
            this.wJ = dkVar;
        }
    }

    public void a(String str, Map<String, ?> map) {
        try {
            b(str, gj.t(map));
        } catch (JSONException e) {
            gs.W("Could not convert parameters to JSON.");
        }
    }

    public void a(String str, JSONObject jSONObject) {
        if (jSONObject == null) {
            jSONObject = new JSONObject();
        }
        String string = jSONObject.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:" + str + "(");
        sb.append(string);
        sb.append(");");
        X(sb.toString());
    }

    public void b(String str, JSONObject jSONObject) {
        if (jSONObject == null) {
            jSONObject = new JSONObject();
        }
        String string = jSONObject.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:AFMA_ReceiveMessage('");
        sb.append(str);
        sb.append("'");
        sb.append(ClientInfo.SEPARATOR_BETWEEN_VARS);
        sb.append(string);
        sb.append(");");
        gs.V("Dispatching AFMA event: " + ((Object) sb));
        X(sb.toString());
    }

    public void bT() {
        if (dv().dF()) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            Display defaultDisplay = this.mG.getDefaultDisplay();
            defaultDisplay.getMetrics(displayMetrics);
            int iS = gj.s(getContext());
            float f = 160.0f / displayMetrics.densityDpi;
            try {
                b("onScreenInfoChanged", new JSONObject().put("width", Math.round(displayMetrics.widthPixels * f)).put("height", Math.round((displayMetrics.heightPixels - iS) * f)).put("density", displayMetrics.density).put("rotation", defaultDisplay.getRotation()));
            } catch (JSONException e) {
                gs.b("Error occured while obtaining screen information.", e);
            }
        }
    }

    public void ca() {
        HashMap map = new HashMap(1);
        map.put("version", this.qs.wD);
        a("onshow", map);
    }

    public void cb() {
        HashMap map = new HashMap(1);
        map.put("version", this.qs.wD);
        a("onhide", map);
    }

    public Context dA() {
        return this.wI.dA();
    }

    @Override // android.webkit.WebView
    public void destroy() {
        synchronized (this.mw) {
            super.destroy();
            this.wM = true;
        }
    }

    public dk du() {
        dk dkVar;
        synchronized (this.mw) {
            dkVar = this.wJ;
        }
        return dkVar;
    }

    public gw dv() {
        return this.wH;
    }

    public boolean dw() {
        return this.wN;
    }

    public k dx() {
        return this.sX;
    }

    public gt dy() {
        return this.qs;
    }

    public boolean dz() {
        boolean z;
        synchronized (this.mw) {
            z = this.wK;
        }
        return z;
    }

    @Override // android.webkit.WebView
    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        synchronized (this.mw) {
            if (!isDestroyed()) {
                super.evaluateJavascript(script, resultCallback);
                return;
            }
            gs.W("The webview is destroyed. Ignoring action.");
            if (resultCallback != null) {
                resultCallback.onReceiveValue(null);
            }
        }
    }

    public boolean isDestroyed() {
        boolean z;
        synchronized (this.mw) {
            z = this.wM;
        }
        return z;
    }

    public void o(boolean z) {
        synchronized (this.mw) {
            if (this.wJ != null) {
                this.wJ.o(z);
            } else {
                this.wN = z;
            }
        }
    }

    @Override // android.webkit.DownloadListener
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long size) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse(url), mimeType);
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            gs.S("Couldn't find an Activity to view url/mimetype: " + url + " / " + mimeType);
        }
    }

    @Override // android.webkit.WebView, android.widget.AbsoluteLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        synchronized (this.mw) {
            if (isInEditMode() || this.wK) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            int mode = View.MeasureSpec.getMode(widthMeasureSpec);
            int size = View.MeasureSpec.getSize(widthMeasureSpec);
            int mode2 = View.MeasureSpec.getMode(heightMeasureSpec);
            int size2 = View.MeasureSpec.getSize(heightMeasureSpec);
            int i2 = (mode == Integer.MIN_VALUE || mode == 1073741824) ? size : Integer.MAX_VALUE;
            if (mode2 == Integer.MIN_VALUE || mode2 == 1073741824) {
                i = size2;
            }
            if (this.qr.widthPixels > i2 || this.qr.heightPixels > i) {
                float f = this.wI.getResources().getDisplayMetrics().density;
                gs.W("Not enough space to show ad. Needs " + ((int) (this.qr.widthPixels / f)) + "x" + ((int) (this.qr.heightPixels / f)) + " dp, but only has " + ((int) (size / f)) + "x" + ((int) (size2 / f)) + " dp.");
                if (getVisibility() != 8) {
                    setVisibility(4);
                }
                setMeasuredDimension(0, 0);
            } else {
                if (getVisibility() != 8) {
                    setVisibility(0);
                }
                setMeasuredDimension(this.qr.widthPixels, this.qr.heightPixels);
            }
        }
    }

    @Override // android.webkit.WebView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.sX != null) {
            this.sX.a(event);
        }
        return super.onTouchEvent(event);
    }

    public void setContext(Context context) {
        this.wI.setBaseContext(context);
    }

    public void x(boolean z) {
        synchronized (this.mw) {
            this.wK = z;
            dB();
        }
    }
}
