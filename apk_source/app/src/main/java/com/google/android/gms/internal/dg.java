package com.google.android.gms.internal;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.google.android.gms.internal.df;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class dg {
    private final Context mContext;
    private final WindowManager mG;
    private final gv md;
    private final bl rg;
    DisplayMetrics rh;
    private float ri;
    private int rl;
    int rj = -1;
    int rk = -1;
    private int rm = -1;
    private int rn = -1;
    private int[] ro = new int[2];

    public dg(gv gvVar, Context context, bl blVar) {
        this.md = gvVar;
        this.mContext = context;
        this.rg = blVar;
        this.mG = (WindowManager) context.getSystemService("window");
        bN();
        bO();
        bP();
    }

    private void bN() {
        this.rh = new DisplayMetrics();
        Display defaultDisplay = this.mG.getDefaultDisplay();
        defaultDisplay.getMetrics(this.rh);
        this.ri = this.rh.density;
        this.rl = defaultDisplay.getRotation();
    }

    private void bP() {
        this.md.getLocationOnScreen(this.ro);
        this.md.measure(0, 0);
        float f = 160.0f / this.rh.densityDpi;
        this.rm = Math.round(this.md.getMeasuredWidth() * f);
        this.rn = Math.round(f * this.md.getMeasuredHeight());
    }

    private df bV() {
        return new df.a().j(this.rg.bj()).i(this.rg.bk()).k(this.rg.bo()).l(this.rg.bl()).m(this.rg.bm()).bM();
    }

    void bO() {
        int iS = gj.s(this.mContext);
        float f = 160.0f / this.rh.densityDpi;
        this.rj = Math.round(this.rh.widthPixels * f);
        this.rk = Math.round((this.rh.heightPixels - iS) * f);
    }

    public void bQ() throws JSONException {
        bT();
        bU();
        bS();
        bR();
    }

    public void bR() {
        if (gs.u(2)) {
            gs.U("Dispatching Ready Event.");
        }
        this.md.b("onReadyEventReceived", new JSONObject());
    }

    public void bS() throws JSONException {
        try {
            this.md.b("onDefaultPositionReceived", new JSONObject().put("x", this.ro[0]).put("y", this.ro[1]).put("width", this.rm).put("height", this.rn));
        } catch (JSONException e) {
            gs.b("Error occured while dispatching default position.", e);
        }
    }

    public void bT() throws JSONException {
        try {
            this.md.b("onScreenInfoChanged", new JSONObject().put("width", this.rj).put("height", this.rk).put("density", this.ri).put("rotation", this.rl));
        } catch (JSONException e) {
            gs.b("Error occured while obtaining screen information.", e);
        }
    }

    public void bU() {
        this.md.b("onDeviceFeaturesReceived", bV().bL());
    }
}
