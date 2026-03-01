package com.google.android.gms.tagmanager;

import android.content.Context;
import android.net.Uri;
import com.google.android.gms.internal.d;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
class i extends dg {
    private static final String ID = com.google.android.gms.internal.a.ARBITRARY_PIXEL.toString();
    private static final String URL = com.google.android.gms.internal.b.URL.toString();
    private static final String anK = com.google.android.gms.internal.b.ADDITIONAL_PARAMS.toString();
    private static final String anL = com.google.android.gms.internal.b.UNREPEATABLE.toString();
    static final String anM = "gtm_" + ID + "_unrepeatable";
    private static final Set<String> anN = new HashSet();
    private final a anO;
    private final Context mContext;

    public interface a {
        aq nM();
    }

    public i(final Context context) {
        this(context, new a() { // from class: com.google.android.gms.tagmanager.i.1
            @Override // com.google.android.gms.tagmanager.i.a
            public aq nM() {
                return y.W(context);
            }
        });
    }

    i(Context context, a aVar) {
        super(ID, URL);
        this.anO = aVar;
        this.mContext = context;
    }

    private synchronized boolean cg(String str) {
        boolean z = true;
        synchronized (this) {
            if (!ci(str)) {
                if (ch(str)) {
                    anN.add(str);
                } else {
                    z = false;
                }
            }
        }
        return z;
    }

    @Override // com.google.android.gms.tagmanager.dg
    public void E(Map<String, d.a> map) {
        String strJ = map.get(anL) != null ? di.j(map.get(anL)) : null;
        if (strJ == null || !cg(strJ)) {
            Uri.Builder builderBuildUpon = Uri.parse(di.j(map.get(URL))).buildUpon();
            d.a aVar = map.get(anK);
            if (aVar != null) {
                Object objO = di.o(aVar);
                if (!(objO instanceof List)) {
                    bh.T("ArbitraryPixel: additional params not a list: not sending partial hit: " + builderBuildUpon.build().toString());
                    return;
                }
                for (Object obj : (List) objO) {
                    if (!(obj instanceof Map)) {
                        bh.T("ArbitraryPixel: additional params contains non-map: not sending partial hit: " + builderBuildUpon.build().toString());
                        return;
                    }
                    for (Map.Entry entry : ((Map) obj).entrySet()) {
                        builderBuildUpon.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                    }
                }
            }
            String string = builderBuildUpon.build().toString();
            this.anO.nM().cw(string);
            bh.V("ArbitraryPixel: url = " + string);
            if (strJ != null) {
                synchronized (i.class) {
                    anN.add(strJ);
                    cz.a(this.mContext, anM, strJ, "true");
                }
            }
        }
    }

    boolean ch(String str) {
        return this.mContext.getSharedPreferences(anM, 0).contains(str);
    }

    boolean ci(String str) {
        return anN.contains(str);
    }
}
