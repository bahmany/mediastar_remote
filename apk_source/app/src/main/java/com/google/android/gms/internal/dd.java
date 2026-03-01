package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.google.android.gms.ads.AdSize;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class dd {
    static final Set<String> qT = new HashSet(Arrays.asList("top-left", "top-right", "top-center", "center", "bottom-left", "bottom-right", "bottom-center"));
    private final Context mContext;
    private final gv md;
    private final Map<String, String> qM;
    private int lf = -1;
    private int lg = -1;
    private int qU = 0;
    private int qV = 0;
    private boolean qW = true;
    private String qX = "top-right";

    public dd(gv gvVar, Map<String, String> map) {
        this.md = gvVar;
        this.qM = map;
        this.mContext = gvVar.dA();
    }

    private void bG() {
        int[] iArrT = gj.t(this.mContext);
        if (!TextUtils.isEmpty(this.qM.get("width"))) {
            int iM = gj.M(this.qM.get("width"));
            if (b(iM, iArrT[0])) {
                this.lf = iM;
            }
        }
        if (!TextUtils.isEmpty(this.qM.get("height"))) {
            int iM2 = gj.M(this.qM.get("height"));
            if (c(iM2, iArrT[1])) {
                this.lg = iM2;
            }
        }
        if (!TextUtils.isEmpty(this.qM.get("offsetX"))) {
            this.qU = gj.M(this.qM.get("offsetX"));
        }
        if (!TextUtils.isEmpty(this.qM.get("offsetY"))) {
            this.qV = gj.M(this.qM.get("offsetY"));
        }
        if (!TextUtils.isEmpty(this.qM.get("allowOffscreen"))) {
            this.qW = Boolean.parseBoolean(this.qM.get("allowOffscreen"));
        }
        String str = this.qM.get("customClosePosition");
        if (TextUtils.isEmpty(str) || !qT.contains(str)) {
            return;
        }
        this.qX = str;
    }

    boolean b(int i, int i2) {
        return i >= 50 && i < i2;
    }

    boolean bI() {
        return this.lf > -1 && this.lg > -1;
    }

    void bJ() throws JSONException {
        try {
            this.md.b("onSizeChanged", new JSONObject().put("x", this.qU).put("y", this.qV).put("width", this.lf).put("height", this.lg));
        } catch (JSONException e) {
            gs.b("Error occured while dispatching size change.", e);
        }
    }

    void bK() throws JSONException {
        try {
            this.md.b("onStateChanged", new JSONObject().put("state", "resized"));
        } catch (JSONException e) {
            gs.b("Error occured while dispatching state change.", e);
        }
    }

    boolean c(int i, int i2) {
        return i >= 50 && i < i2;
    }

    public void execute() throws JSONException {
        gs.U("PLEASE IMPLEMENT mraid.resize()");
        if (this.mContext == null) {
            gs.W("Not an activity context. Cannot resize.");
            return;
        }
        if (this.md.Y().og) {
            gs.W("Is interstitial. Cannot resize an interstitial.");
            return;
        }
        if (this.md.dz()) {
            gs.W("Is expanded. Cannot resize an expanded banner.");
            return;
        }
        bG();
        if (!bI()) {
            gs.W("Invalid width and height options. Cannot resize.");
            return;
        }
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int iA = gr.a(displayMetrics, this.lf) + 16;
        int iA2 = gr.a(displayMetrics, this.lg) + 16;
        ViewParent parent = this.md.getParent();
        if (parent != null && (parent instanceof ViewGroup)) {
            ((ViewGroup) parent).removeView(this.md);
        }
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        linearLayout.setBackgroundColor(0);
        PopupWindow popupWindow = new PopupWindow(this.mContext);
        popupWindow.setHeight(iA2);
        popupWindow.setWidth(iA);
        popupWindow.setClippingEnabled(!this.qW);
        popupWindow.setContentView(linearLayout);
        linearLayout.addView(this.md, -1, -1);
        popupWindow.showAtLocation(((Activity) this.mContext).getWindow().getDecorView(), 0, this.qU, this.qV);
        this.md.a(new ay(this.mContext, new AdSize(this.lf, this.lg)));
        bJ();
        bK();
    }
}
