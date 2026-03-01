package com.google.android.gms.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ez
/* loaded from: classes.dex */
public final class fu {
    private String pn;
    private List<String> uA;
    private List<String> ua;
    private String uv;
    private String uw;
    private List<String> ux;
    private String uy;
    private String uz;
    private long uB = -1;
    private boolean uC = false;
    private final long uD = -1;
    private long uE = -1;
    private int mOrientation = -1;
    private boolean uF = false;
    private boolean uG = false;
    private boolean uH = false;
    private boolean uI = false;

    static String a(Map<String, List<String>> map, String str) {
        List<String> list = map.get(str);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    static long b(Map<String, List<String>> map, String str) {
        List<String> list = map.get(str);
        if (list != null && !list.isEmpty()) {
            String str2 = list.get(0);
            try {
                return (long) (Float.parseFloat(str2) * 1000.0f);
            } catch (NumberFormatException e) {
                gs.W("Could not parse float from " + str + " header: " + str2);
            }
        }
        return -1L;
    }

    static List<String> c(Map<String, List<String>> map, String str) {
        String str2;
        List<String> list = map.get(str);
        if (list == null || list.isEmpty() || (str2 = list.get(0)) == null) {
            return null;
        }
        return Arrays.asList(str2.trim().split("\\s+"));
    }

    private boolean d(Map<String, List<String>> map, String str) {
        List<String> list = map.get(str);
        return (list == null || list.isEmpty() || !Boolean.valueOf(list.get(0)).booleanValue()) ? false : true;
    }

    private void f(Map<String, List<String>> map) {
        this.uv = a(map, "X-Afma-Ad-Size");
    }

    private void g(Map<String, List<String>> map) {
        List<String> listC = c(map, "X-Afma-Click-Tracking-Urls");
        if (listC != null) {
            this.ux = listC;
        }
    }

    private void h(Map<String, List<String>> map) {
        List<String> list = map.get("X-Afma-Debug-Dialog");
        if (list == null || list.isEmpty()) {
            return;
        }
        this.uy = list.get(0);
    }

    private void i(Map<String, List<String>> map) {
        List<String> listC = c(map, "X-Afma-Tracking-Urls");
        if (listC != null) {
            this.uA = listC;
        }
    }

    private void j(Map<String, List<String>> map) {
        long jB = b(map, "X-Afma-Interstitial-Timeout");
        if (jB != -1) {
            this.uB = jB;
        }
    }

    private void k(Map<String, List<String>> map) {
        this.uz = a(map, "X-Afma-ActiveView");
    }

    private void l(Map<String, List<String>> map) {
        this.uG |= d(map, "X-Afma-Native");
    }

    private void m(Map<String, List<String>> map) {
        this.uF |= d(map, "X-Afma-Custom-Rendering-Allowed");
    }

    private void n(Map<String, List<String>> map) {
        this.uC |= d(map, "X-Afma-Mediation");
    }

    private void o(Map<String, List<String>> map) {
        List<String> listC = c(map, "X-Afma-Manual-Tracking-Urls");
        if (listC != null) {
            this.ua = listC;
        }
    }

    private void p(Map<String, List<String>> map) {
        long jB = b(map, "X-Afma-Refresh-Rate");
        if (jB != -1) {
            this.uE = jB;
        }
    }

    private void q(Map<String, List<String>> map) {
        List<String> list = map.get("X-Afma-Orientation");
        if (list == null || list.isEmpty()) {
            return;
        }
        String str = list.get(0);
        if ("portrait".equalsIgnoreCase(str)) {
            this.mOrientation = gj.dn();
        } else if ("landscape".equalsIgnoreCase(str)) {
            this.mOrientation = gj.dm();
        }
    }

    private void r(Map<String, List<String>> map) {
        List<String> list = map.get("X-Afma-Use-HTTPS");
        if (list == null || list.isEmpty()) {
            return;
        }
        this.uH = Boolean.valueOf(list.get(0)).booleanValue();
    }

    private void s(Map<String, List<String>> map) {
        List<String> list = map.get("X-Afma-Content-Url-Opted-Out");
        if (list == null || list.isEmpty()) {
            return;
        }
        this.uI = Boolean.valueOf(list.get(0)).booleanValue();
    }

    public void a(String str, Map<String, List<String>> map, String str2) {
        this.uw = str;
        this.pn = str2;
        e(map);
    }

    public void e(Map<String, List<String>> map) {
        f(map);
        g(map);
        h(map);
        i(map);
        j(map);
        n(map);
        o(map);
        p(map);
        q(map);
        k(map);
        r(map);
        m(map);
        l(map);
        s(map);
    }

    public fk i(long j) {
        return new fk(this.uw, this.pn, this.ux, this.uA, this.uB, this.uC, -1L, this.ua, this.uE, this.mOrientation, this.uv, j, this.uy, this.uz, this.uF, this.uG, this.uH, this.uI);
    }
}
