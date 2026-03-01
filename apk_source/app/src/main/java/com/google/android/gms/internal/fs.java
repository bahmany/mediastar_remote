package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public final class fs {
    private static final SimpleDateFormat up = new SimpleDateFormat("yyyyMMdd");

    public static fk a(Context context, fi fiVar, String str) {
        fk fkVar;
        List<String> list;
        List<String> list2;
        List<String> list3;
        try {
            JSONObject jSONObject = new JSONObject(str);
            String strOptString = jSONObject.optString("ad_base_url", null);
            String strOptString2 = jSONObject.optString("ad_url", null);
            String strOptString3 = jSONObject.optString("ad_size", null);
            String strOptString4 = jSONObject.optString("ad_html", null);
            long j = -1;
            String strOptString5 = jSONObject.optString("debug_dialog", null);
            long j2 = jSONObject.has("interstitial_timeout") ? (long) (jSONObject.getDouble("interstitial_timeout") * 1000.0d) : -1L;
            String strOptString6 = jSONObject.optString("orientation", null);
            int iDm = -1;
            if ("portrait".equals(strOptString6)) {
                iDm = gj.dn();
            } else if ("landscape".equals(strOptString6)) {
                iDm = gj.dm();
            }
            if (TextUtils.isEmpty(strOptString4)) {
                if (TextUtils.isEmpty(strOptString2)) {
                    gs.W("Could not parse the mediation config: Missing required ad_html or ad_url field.");
                    return new fk(0);
                }
                fk fkVarA = fr.a(context, fiVar.lD.wD, strOptString2, (String) null, (fv) null);
                strOptString = fkVarA.rP;
                strOptString4 = fkVarA.tG;
                j = fkVarA.tM;
                fkVar = fkVarA;
            } else {
                if (TextUtils.isEmpty(strOptString)) {
                    gs.W("Could not parse the mediation config: Missing required ad_base_url field");
                    return new fk(0);
                }
                fkVar = null;
            }
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("click_urls");
            List<String> linkedList = fkVar == null ? null : fkVar.qf;
            if (jSONArrayOptJSONArray != null) {
                if (linkedList == null) {
                    linkedList = new LinkedList<>();
                }
                for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                    linkedList.add(jSONArrayOptJSONArray.getString(i));
                }
                list = linkedList;
            } else {
                list = linkedList;
            }
            JSONArray jSONArrayOptJSONArray2 = jSONObject.optJSONArray("impression_urls");
            List<String> linkedList2 = fkVar == null ? null : fkVar.qg;
            if (jSONArrayOptJSONArray2 != null) {
                if (linkedList2 == null) {
                    linkedList2 = new LinkedList<>();
                }
                for (int i2 = 0; i2 < jSONArrayOptJSONArray2.length(); i2++) {
                    linkedList2.add(jSONArrayOptJSONArray2.getString(i2));
                }
                list2 = linkedList2;
            } else {
                list2 = linkedList2;
            }
            JSONArray jSONArrayOptJSONArray3 = jSONObject.optJSONArray("manual_impression_urls");
            List<String> linkedList3 = fkVar == null ? null : fkVar.tK;
            if (jSONArrayOptJSONArray3 != null) {
                if (linkedList3 == null) {
                    linkedList3 = new LinkedList<>();
                }
                for (int i3 = 0; i3 < jSONArrayOptJSONArray3.length(); i3++) {
                    linkedList3.add(jSONArrayOptJSONArray3.getString(i3));
                }
                list3 = linkedList3;
            } else {
                list3 = linkedList3;
            }
            if (fkVar != null) {
                if (fkVar.orientation != -1) {
                    iDm = fkVar.orientation;
                }
                if (fkVar.tH > 0) {
                    j2 = fkVar.tH;
                }
            }
            String strOptString7 = jSONObject.optString("active_view");
            boolean zOptBoolean = jSONObject.optBoolean("ad_is_javascript", false);
            return new fk(strOptString, strOptString4, list, list2, j2, false, -1L, list3, -1L, iDm, strOptString3, j, strOptString5, zOptBoolean, zOptBoolean ? jSONObject.optString("ad_passback_url", null) : null, strOptString7, false, false, fiVar.tF, false);
        } catch (JSONException e) {
            gs.W("Could not parse the mediation config: " + e.getMessage());
            return new fk(0);
        }
    }

    public static String a(fi fiVar, fw fwVar, Location location, String str, String str2) {
        try {
            HashMap map = new HashMap();
            ArrayList arrayList = new ArrayList();
            if (!TextUtils.isEmpty(str)) {
                arrayList.add(str);
            }
            if (!TextUtils.isEmpty(str2)) {
                arrayList.add(str2);
            }
            if (arrayList.size() > 0) {
                map.put("eid", TextUtils.join(ClientInfo.SEPARATOR_BETWEEN_VARS, arrayList));
            }
            if (fiVar.tw != null) {
                map.put("ad_pos", fiVar.tw);
            }
            a((HashMap<String, Object>) map, fiVar.tx);
            map.put("format", fiVar.lH.of);
            if (fiVar.lH.width == -1) {
                map.put("smart_w", "full");
            }
            if (fiVar.lH.height == -2) {
                map.put("smart_h", "auto");
            }
            if (fiVar.lH.oh != null) {
                StringBuilder sb = new StringBuilder();
                for (ay ayVar : fiVar.lH.oh) {
                    if (sb.length() != 0) {
                        sb.append("|");
                    }
                    sb.append(ayVar.width == -1 ? (int) (ayVar.widthPixels / fwVar.vi) : ayVar.width);
                    sb.append("x");
                    sb.append(ayVar.height == -2 ? (int) (ayVar.heightPixels / fwVar.vi) : ayVar.height);
                }
                map.put("sz", sb);
            }
            if (fiVar.tD != 0) {
                map.put("native_version", Integer.valueOf(fiVar.tD));
                map.put("native_templates", fiVar.lS);
            }
            map.put("slotname", fiVar.lA);
            map.put("pn", fiVar.applicationInfo.packageName);
            if (fiVar.ty != null) {
                map.put("vc", Integer.valueOf(fiVar.ty.versionCode));
            }
            map.put("ms", fiVar.tz);
            map.put("seq_num", fiVar.tA);
            map.put("session_id", fiVar.tB);
            map.put("js", fiVar.lD.wD);
            a((HashMap<String, Object>) map, fwVar);
            if (fiVar.tx.versionCode >= 2 && fiVar.tx.ob != null) {
                a((HashMap<String, Object>) map, fiVar.tx.ob);
            }
            if (fiVar.versionCode >= 2) {
                map.put("quality_signals", fiVar.tC);
            }
            if (fiVar.versionCode >= 4 && fiVar.tF) {
                map.put("forceHttps", Boolean.valueOf(fiVar.tF));
            }
            if (fiVar.versionCode >= 3 && fiVar.tE != null) {
                map.put("content_info", fiVar.tE);
            }
            if (gs.u(2)) {
                gs.V("Ad Request JSON: " + gj.t(map).toString(2));
            }
            return gj.t(map).toString();
        } catch (JSONException e) {
            gs.W("Problem serializing ad request to JSON: " + e.getMessage());
            return null;
        }
    }

    private static void a(HashMap<String, Object> map, Location location) {
        HashMap map2 = new HashMap();
        Float fValueOf = Float.valueOf(location.getAccuracy() * 1000.0f);
        Long lValueOf = Long.valueOf(location.getTime() * 1000);
        Long lValueOf2 = Long.valueOf((long) (location.getLatitude() * 1.0E7d));
        Long lValueOf3 = Long.valueOf((long) (location.getLongitude() * 1.0E7d));
        map2.put("radius", fValueOf);
        map2.put("lat", lValueOf2);
        map2.put("long", lValueOf3);
        map2.put("time", lValueOf);
        map.put("uule", map2);
    }

    private static void a(HashMap<String, Object> map, av avVar) {
        String strDj = gf.dj();
        if (strDj != null) {
            map.put("abf", strDj);
        }
        if (avVar.nT != -1) {
            map.put("cust_age", up.format(new Date(avVar.nT)));
        }
        if (avVar.extras != null) {
            map.put("extras", avVar.extras);
        }
        if (avVar.nU != -1) {
            map.put("cust_gender", Integer.valueOf(avVar.nU));
        }
        if (avVar.nV != null) {
            map.put("kw", avVar.nV);
        }
        if (avVar.nX != -1) {
            map.put("tag_for_child_directed_treatment", Integer.valueOf(avVar.nX));
        }
        if (avVar.nW) {
            map.put("adtest", "on");
        }
        if (avVar.versionCode >= 2) {
            if (avVar.nY) {
                map.put("d_imp_hdr", 1);
            }
            if (!TextUtils.isEmpty(avVar.nZ)) {
                map.put("ppid", avVar.nZ);
            }
            if (avVar.oa != null) {
                a(map, avVar.oa);
            }
        }
        if (avVar.versionCode < 3 || avVar.oc == null) {
            return;
        }
        map.put("url", avVar.oc);
    }

    private static void a(HashMap<String, Object> map, bj bjVar) {
        String str;
        String str2 = null;
        if (Color.alpha(bjVar.oH) != 0) {
            map.put("acolor", t(bjVar.oH));
        }
        if (Color.alpha(bjVar.backgroundColor) != 0) {
            map.put("bgcolor", t(bjVar.backgroundColor));
        }
        if (Color.alpha(bjVar.oI) != 0 && Color.alpha(bjVar.oJ) != 0) {
            map.put("gradientto", t(bjVar.oI));
            map.put("gradientfrom", t(bjVar.oJ));
        }
        if (Color.alpha(bjVar.oK) != 0) {
            map.put("bcolor", t(bjVar.oK));
        }
        map.put("bthick", Integer.toString(bjVar.oL));
        switch (bjVar.oM) {
            case 0:
                str = "none";
                break;
            case 1:
                str = "dashed";
                break;
            case 2:
                str = "dotted";
                break;
            case 3:
                str = "solid";
                break;
            default:
                str = null;
                break;
        }
        if (str != null) {
            map.put("btype", str);
        }
        switch (bjVar.oN) {
            case 0:
                str2 = "light";
                break;
            case 1:
                str2 = "medium";
                break;
            case 2:
                str2 = "dark";
                break;
        }
        if (str2 != null) {
            map.put("callbuttoncolor", str2);
        }
        if (bjVar.oO != null) {
            map.put("channel", bjVar.oO);
        }
        if (Color.alpha(bjVar.oP) != 0) {
            map.put("dcolor", t(bjVar.oP));
        }
        if (bjVar.oQ != null) {
            map.put("font", bjVar.oQ);
        }
        if (Color.alpha(bjVar.oR) != 0) {
            map.put("hcolor", t(bjVar.oR));
        }
        map.put("headersize", Integer.toString(bjVar.oS));
        if (bjVar.oT != null) {
            map.put("q", bjVar.oT);
        }
    }

    private static void a(HashMap<String, Object> map, fw fwVar) {
        map.put("am", Integer.valueOf(fwVar.uS));
        map.put("cog", s(fwVar.uT));
        map.put("coh", s(fwVar.uU));
        if (!TextUtils.isEmpty(fwVar.uV)) {
            map.put("carrier", fwVar.uV);
        }
        map.put("gl", fwVar.uW);
        if (fwVar.uX) {
            map.put("simulator", 1);
        }
        map.put("ma", s(fwVar.uY));
        map.put("sp", s(fwVar.uZ));
        map.put("hl", fwVar.va);
        if (!TextUtils.isEmpty(fwVar.vb)) {
            map.put("mv", fwVar.vb);
        }
        map.put("muv", Integer.valueOf(fwVar.vc));
        if (fwVar.vd != -2) {
            map.put("cnt", Integer.valueOf(fwVar.vd));
        }
        map.put("gnt", Integer.valueOf(fwVar.ve));
        map.put("pt", Integer.valueOf(fwVar.vf));
        map.put("rm", Integer.valueOf(fwVar.vg));
        map.put("riv", Integer.valueOf(fwVar.vh));
        map.put("u_sd", Float.valueOf(fwVar.vi));
        map.put("sh", Integer.valueOf(fwVar.vk));
        map.put("sw", Integer.valueOf(fwVar.vj));
        Bundle bundle = new Bundle();
        bundle.putInt("active_network_state", fwVar.vo);
        bundle.putBoolean("active_network_metered", fwVar.vn);
        map.put("connectivity", bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("is_charging", fwVar.vm);
        bundle2.putDouble("battery_level", fwVar.vl);
        map.put("battery", bundle2);
    }

    private static Integer s(boolean z) {
        return Integer.valueOf(z ? 1 : 0);
    }

    private static String t(int i) {
        return String.format(Locale.US, "#%06x", Integer.valueOf(16777215 & i));
    }
}
