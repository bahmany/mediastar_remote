package com.google.android.gms.internal;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import com.iflytek.speech.VoiceWakeuperAidl;
import java.util.HashMap;
import java.util.Map;

@ez
/* loaded from: classes.dex */
public final class bx {
    public static final by pA = new by() { // from class: com.google.android.gms.internal.bx.1
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
        }
    };
    public static final by pB = new by() { // from class: com.google.android.gms.internal.bx.2
        AnonymousClass2() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            String str = map.get("urls");
            if (TextUtils.isEmpty(str)) {
                gs.W("URLs missing in canOpenURLs GMSG.");
                return;
            }
            String[] strArrSplit = str.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
            HashMap map2 = new HashMap();
            PackageManager packageManager = gvVar.getContext().getPackageManager();
            for (String str2 : strArrSplit) {
                String[] strArrSplit2 = str2.split(VoiceWakeuperAidl.PARAMS_SEPARATE, 2);
                map2.put(str2, Boolean.valueOf(packageManager.resolveActivity(new Intent(strArrSplit2.length > 1 ? strArrSplit2[1].trim() : "android.intent.action.VIEW", Uri.parse(strArrSplit2[0].trim())), 65536) != null));
            }
            gvVar.a("openableURLs", map2);
        }
    };
    public static final by pC = new by() { // from class: com.google.android.gms.internal.bx.3
        AnonymousClass3() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            k kVarDx;
            String str = map.get("u");
            if (str == null) {
                gs.W("URL missing from click GMSG.");
                return;
            }
            Uri uri = Uri.parse(str);
            try {
                kVarDx = gvVar.dx();
            } catch (l e) {
                gs.W("Unable to append parameter to URL: " + str);
            }
            Uri uriA = (kVarDx == null || !kVarDx.b(uri)) ? uri : kVarDx.a(uri, gvVar.getContext());
            new gq(gvVar.getContext(), gvVar.dy().wD, uriA.toString()).start();
        }
    };
    public static final by pD = new by() { // from class: com.google.android.gms.internal.bx.4
        AnonymousClass4() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            dk dkVarDu = gvVar.du();
            if (dkVarDu == null) {
                gs.W("A GMSG tried to close something that wasn't an overlay.");
            } else {
                dkVarDu.close();
            }
        }
    };
    public static final by pE = new by() { // from class: com.google.android.gms.internal.bx.5
        AnonymousClass5() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            gvVar.o("1".equals(map.get("custom_close")));
        }
    };
    public static final by pF = new by() { // from class: com.google.android.gms.internal.bx.6
        AnonymousClass6() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            String str = map.get("u");
            if (str == null) {
                gs.W("URL missing from httpTrack GMSG.");
            } else {
                new gq(gvVar.getContext(), gvVar.dy().wD, str).start();
            }
        }
    };
    public static final by pG = new by() { // from class: com.google.android.gms.internal.bx.7
        AnonymousClass7() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            gs.U("Received log message: " + map.get("string"));
        }
    };
    public static final by pH = new by() { // from class: com.google.android.gms.internal.bx.8
        AnonymousClass8() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) throws NumberFormatException {
            String str = map.get("tx");
            String str2 = map.get("ty");
            String str3 = map.get("td");
            try {
                int i = Integer.parseInt(str);
                int i2 = Integer.parseInt(str2);
                int i3 = Integer.parseInt(str3);
                k kVarDx = gvVar.dx();
                if (kVarDx != null) {
                    kVarDx.z().a(i, i2, i3);
                }
            } catch (NumberFormatException e) {
                gs.W("Could not parse touch parameters from gmsg.");
            }
        }
    };
    public static final by pI = new ce();

    /* renamed from: com.google.android.gms.internal.bx$1 */
    static class AnonymousClass1 implements by {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
        }
    }

    /* renamed from: com.google.android.gms.internal.bx$2 */
    static class AnonymousClass2 implements by {
        AnonymousClass2() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            String str = map.get("urls");
            if (TextUtils.isEmpty(str)) {
                gs.W("URLs missing in canOpenURLs GMSG.");
                return;
            }
            String[] strArrSplit = str.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
            HashMap map2 = new HashMap();
            PackageManager packageManager = gvVar.getContext().getPackageManager();
            for (String str2 : strArrSplit) {
                String[] strArrSplit2 = str2.split(VoiceWakeuperAidl.PARAMS_SEPARATE, 2);
                map2.put(str2, Boolean.valueOf(packageManager.resolveActivity(new Intent(strArrSplit2.length > 1 ? strArrSplit2[1].trim() : "android.intent.action.VIEW", Uri.parse(strArrSplit2[0].trim())), 65536) != null));
            }
            gvVar.a("openableURLs", map2);
        }
    }

    /* renamed from: com.google.android.gms.internal.bx$3 */
    static class AnonymousClass3 implements by {
        AnonymousClass3() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            k kVarDx;
            String str = map.get("u");
            if (str == null) {
                gs.W("URL missing from click GMSG.");
                return;
            }
            Uri uri = Uri.parse(str);
            try {
                kVarDx = gvVar.dx();
            } catch (l e) {
                gs.W("Unable to append parameter to URL: " + str);
            }
            Uri uriA = (kVarDx == null || !kVarDx.b(uri)) ? uri : kVarDx.a(uri, gvVar.getContext());
            new gq(gvVar.getContext(), gvVar.dy().wD, uriA.toString()).start();
        }
    }

    /* renamed from: com.google.android.gms.internal.bx$4 */
    static class AnonymousClass4 implements by {
        AnonymousClass4() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            dk dkVarDu = gvVar.du();
            if (dkVarDu == null) {
                gs.W("A GMSG tried to close something that wasn't an overlay.");
            } else {
                dkVarDu.close();
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.bx$5 */
    static class AnonymousClass5 implements by {
        AnonymousClass5() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            gvVar.o("1".equals(map.get("custom_close")));
        }
    }

    /* renamed from: com.google.android.gms.internal.bx$6 */
    static class AnonymousClass6 implements by {
        AnonymousClass6() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            String str = map.get("u");
            if (str == null) {
                gs.W("URL missing from httpTrack GMSG.");
            } else {
                new gq(gvVar.getContext(), gvVar.dy().wD, str).start();
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.bx$7 */
    static class AnonymousClass7 implements by {
        AnonymousClass7() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            gs.U("Received log message: " + map.get("string"));
        }
    }

    /* renamed from: com.google.android.gms.internal.bx$8 */
    static class AnonymousClass8 implements by {
        AnonymousClass8() {
        }

        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) throws NumberFormatException {
            String str = map.get("tx");
            String str2 = map.get("ty");
            String str3 = map.get("td");
            try {
                int i = Integer.parseInt(str);
                int i2 = Integer.parseInt(str2);
                int i3 = Integer.parseInt(str3);
                k kVarDx = gvVar.dx();
                if (kVarDx != null) {
                    kVarDx.z().a(i, i2, i3);
                }
            } catch (NumberFormatException e) {
                gs.W("Could not parse touch parameters from gmsg.");
            }
        }
    }
}
