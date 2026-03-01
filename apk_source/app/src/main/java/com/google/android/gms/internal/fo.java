package com.google.android.gms.internal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import com.google.android.gms.internal.bq;
import com.google.android.gms.internal.fz;
import com.google.android.gms.internal.go;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class fo implements Callable<fz> {
    private final Context mContext;
    private final u pw;
    private final go tX;
    private final ai tY;
    private final fz.a tn;
    private final Object mw = new Object();
    private boolean tZ = false;
    private int tc = -2;
    private List<String> ua = null;

    public interface a<T extends bq.a> {
        T a(fo foVar, JSONObject jSONObject) throws ExecutionException, JSONException, InterruptedException;
    }

    public fo(Context context, u uVar, ai aiVar, go goVar, fz.a aVar) {
        this.mContext = context;
        this.pw = uVar;
        this.tX = goVar;
        this.tY = aiVar;
        this.tn = aVar;
    }

    private bq.a a(ah ahVar, a aVar, JSONObject jSONObject) throws ExecutionException, JSONException, InterruptedException {
        if (cI()) {
            return null;
        }
        String[] strArrB = b(jSONObject.getJSONObject("tracking_urls_and_actions"), "impression_tracking_urls");
        this.ua = strArrB == null ? null : Arrays.asList(strArrB);
        bq.a aVarA = aVar.a(this, jSONObject);
        if (aVarA == null) {
            gs.T("Failed to retrieve ad assets.");
            return null;
        }
        aVarA.a(new bq(this.pw, ahVar, jSONObject));
        return aVarA;
    }

    private fz a(bq.a aVar) {
        int i;
        synchronized (this.mw) {
            i = this.tc;
            if (aVar == null && this.tc == -2) {
                i = 0;
            }
        }
        return new fz(this.tn.vv.tx, null, this.tn.vw.qf, i, this.tn.vw.qg, this.ua, this.tn.vw.orientation, this.tn.vw.qj, this.tn.vv.tA, false, null, null, null, null, null, 0L, this.tn.lH, this.tn.vw.tH, this.tn.vs, this.tn.vt, this.tn.vw.tN, this.tn.vp, i != -2 ? null : aVar);
    }

    private String[] b(JSONObject jSONObject, String str) throws JSONException {
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray(str);
        if (jSONArrayOptJSONArray == null) {
            return null;
        }
        String[] strArr = new String[jSONArrayOptJSONArray.length()];
        for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
            strArr[i] = jSONArrayOptJSONArray.getString(i);
        }
        return strArr;
    }

    private JSONObject c(final ah ahVar) throws JSONException, TimeoutException {
        if (cI()) {
            return null;
        }
        final gk gkVar = new gk();
        ahVar.a("/nativeAdPreProcess", new by() { // from class: com.google.android.gms.internal.fo.1
            @Override // com.google.android.gms.internal.by
            public void a(gv gvVar, Map<String, String> map) {
                ahVar.g("/nativeAdPreProcess");
                try {
                    String str = map.get("success");
                    if (!TextUtils.isEmpty(str)) {
                        gkVar.a(new JSONObject(str).getJSONArray("ads").getJSONObject(0));
                        return;
                    }
                } catch (JSONException e) {
                    gs.b("Malformed native JSON response.", e);
                }
                fo.this.s(0);
                com.google.android.gms.common.internal.n.a(fo.this.cI(), "Unable to set the ad state error!");
                gkVar.a(null);
            }
        });
        ahVar.a("google.afma.nativeAds.preProcessJsonGmsg", new JSONObject(this.tn.vw.tG));
        return (JSONObject) gkVar.get();
    }

    private ah cH() throws ExecutionException, InterruptedException, CancellationException, TimeoutException {
        if (cI()) {
            return null;
        }
        ah ahVar = this.tY.a(this.mContext, this.tn.vv.lD, "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/native_ads.html").get();
        ahVar.a(this.pw, this.pw, this.pw, this.pw, false, this.pw);
        return ahVar;
    }

    public Future<Drawable> a(JSONObject jSONObject, String str, final boolean z) throws JSONException {
        JSONObject jSONObject2 = z ? jSONObject.getJSONObject(str) : jSONObject.optJSONObject(str);
        if (jSONObject2 == null) {
            jSONObject2 = new JSONObject();
        }
        String string = z ? jSONObject2.getString("url") : jSONObject2.optString("url");
        if (!TextUtils.isEmpty(string)) {
            return this.tX.a(string, new go.a<Drawable>() { // from class: com.google.android.gms.internal.fo.2
                @Override // com.google.android.gms.internal.go.a
                /* renamed from: a, reason: merged with bridge method [inline-methods] */
                public Drawable b(InputStream inputStream) {
                    byte[] bArrD;
                    try {
                        bArrD = jy.d(inputStream);
                    } catch (IOException e) {
                        bArrD = null;
                    }
                    if (bArrD == null) {
                        fo.this.a(2, z);
                        return null;
                    }
                    Bitmap bitmapDecodeByteArray = BitmapFactory.decodeByteArray(bArrD, 0, bArrD.length);
                    if (bitmapDecodeByteArray != null) {
                        return new BitmapDrawable(Resources.getSystem(), bitmapDecodeByteArray);
                    }
                    fo.this.a(2, z);
                    return null;
                }

                @Override // com.google.android.gms.internal.go.a
                /* renamed from: cJ, reason: merged with bridge method [inline-methods] */
                public Drawable cK() {
                    fo.this.a(2, z);
                    return null;
                }
            });
        }
        a(0, z);
        return new gl(null);
    }

    public void a(int i, boolean z) {
        if (z) {
            s(i);
        }
    }

    protected a b(JSONObject jSONObject) throws JSONException {
        if (cI()) {
            return null;
        }
        String string = jSONObject.getString("template_id");
        if (ContentTree.AUDIO_ID.equals(string)) {
            return new fp();
        }
        if ("1".equals(string)) {
            return new fq();
        }
        s(0);
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:8:0x001f  */
    @Override // java.util.concurrent.Callable
    /* renamed from: cG, reason: merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.android.gms.internal.fz call() {
        /*
            r3 = this;
            com.google.android.gms.internal.ah r0 = r3.cH()     // Catch: org.json.JSONException -> L15 java.util.concurrent.TimeoutException -> L29 java.lang.InterruptedException -> L30 java.util.concurrent.ExecutionException -> L32 java.util.concurrent.CancellationException -> L34
            org.json.JSONObject r1 = r3.c(r0)     // Catch: org.json.JSONException -> L15 java.util.concurrent.TimeoutException -> L29 java.lang.InterruptedException -> L30 java.util.concurrent.ExecutionException -> L32 java.util.concurrent.CancellationException -> L34
            com.google.android.gms.internal.fo$a r2 = r3.b(r1)     // Catch: org.json.JSONException -> L15 java.util.concurrent.TimeoutException -> L29 java.lang.InterruptedException -> L30 java.util.concurrent.ExecutionException -> L32 java.util.concurrent.CancellationException -> L34
            com.google.android.gms.internal.bq$a r0 = r3.a(r0, r2, r1)     // Catch: org.json.JSONException -> L15 java.util.concurrent.TimeoutException -> L29 java.lang.InterruptedException -> L30 java.util.concurrent.ExecutionException -> L32 java.util.concurrent.CancellationException -> L34
            com.google.android.gms.internal.fz r0 = r3.a(r0)     // Catch: org.json.JSONException -> L15 java.util.concurrent.TimeoutException -> L29 java.lang.InterruptedException -> L30 java.util.concurrent.ExecutionException -> L32 java.util.concurrent.CancellationException -> L34
        L14:
            return r0
        L15:
            r0 = move-exception
            java.lang.String r1 = "Malformed native JSON response."
            com.google.android.gms.internal.gs.d(r1, r0)
        L1b:
            boolean r0 = r3.tZ
            if (r0 != 0) goto L23
            r0 = 0
            r3.s(r0)
        L23:
            r0 = 0
            com.google.android.gms.internal.fz r0 = r3.a(r0)
            goto L14
        L29:
            r0 = move-exception
            java.lang.String r1 = "Timeout when loading native ad."
            com.google.android.gms.internal.gs.d(r1, r0)
            goto L1b
        L30:
            r0 = move-exception
            goto L1b
        L32:
            r0 = move-exception
            goto L1b
        L34:
            r0 = move-exception
            goto L1b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.fo.call():com.google.android.gms.internal.fz");
    }

    public boolean cI() {
        boolean z;
        synchronized (this.mw) {
            z = this.tZ;
        }
        return z;
    }

    public void s(int i) {
        synchronized (this.mw) {
            this.tZ = true;
            this.tc = i;
        }
    }
}
