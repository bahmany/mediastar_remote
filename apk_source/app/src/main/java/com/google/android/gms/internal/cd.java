package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.teleal.cling.support.messagebox.parser.MessageElement;

@ez
/* loaded from: classes.dex */
public final class cd implements by {
    private final bz pL;
    private final v pM;

    public cd(bz bzVar, v vVar) {
        this.pL = bzVar;
        this.pM = vVar;
    }

    private static boolean b(Map<String, String> map) {
        return "1".equals(map.get("custom_close"));
    }

    private static int c(Map<String, String> map) {
        String str = map.get("o");
        if (str != null) {
            if ("p".equalsIgnoreCase(str)) {
                return gj.dn();
            }
            if ("l".equalsIgnoreCase(str)) {
                return gj.dm();
            }
        }
        return -1;
    }

    @Override // com.google.android.gms.internal.by
    public void a(gv gvVar, Map<String, String> map) {
        String str = map.get("a");
        if (str == null) {
            gs.W("Action missing from an open GMSG.");
            return;
        }
        if (this.pM != null && !this.pM.av()) {
            this.pM.d(map.get("u"));
            return;
        }
        gw gwVarDv = gvVar.dv();
        if ("expand".equalsIgnoreCase(str)) {
            if (gvVar.dz()) {
                gs.W("Cannot expand WebView that is already expanded.");
                return;
            } else {
                gwVarDv.a(b(map), c(map));
                return;
            }
        }
        if ("webapp".equalsIgnoreCase(str)) {
            String str2 = map.get("u");
            if (str2 != null) {
                gwVarDv.a(b(map), c(map), str2);
                return;
            } else {
                gwVarDv.a(b(map), c(map), map.get("html"), map.get("baseurl"));
                return;
            }
        }
        if (!"in_app_purchase".equalsIgnoreCase(str)) {
            gwVarDv.a(new dj(map.get("i"), map.get("u"), map.get(MessageElement.XPATH_PREFIX), map.get("p"), map.get("c"), map.get("f"), map.get("e")));
            return;
        }
        String str3 = map.get("product_id");
        String str4 = map.get("report_urls");
        if (this.pL != null) {
            if (str4 == null || str4.isEmpty()) {
                this.pL.a(str3, new ArrayList<>());
            } else {
                this.pL.a(str3, new ArrayList<>(Arrays.asList(str4.split(" "))));
            }
        }
    }
}
