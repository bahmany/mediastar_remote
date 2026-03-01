package com.google.android.gms.internal;

import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import java.util.Map;
import org.cybergarage.upnp.Action;

@ez
/* loaded from: classes.dex */
public final class ce implements by {
    private static int a(DisplayMetrics displayMetrics, Map<String, String> map, String str, int i) {
        String str2 = map.get(str);
        if (str2 == null) {
            return i;
        }
        try {
            return gr.a(displayMetrics, Integer.parseInt(str2));
        } catch (NumberFormatException e) {
            gs.W("Could not parse " + str + " in a video GMSG: " + str2);
            return i;
        }
    }

    @Override // com.google.android.gms.internal.by
    public void a(gv gvVar, Map<String, String> map) {
        String str = map.get(Action.ELEM_NAME);
        if (str == null) {
            gs.W("Action missing from video GMSG.");
            return;
        }
        dk dkVarDu = gvVar.du();
        if (dkVarDu == null) {
            gs.W("Could not get ad overlay for a video GMSG.");
            return;
        }
        boolean zEqualsIgnoreCase = "new".equalsIgnoreCase(str);
        boolean zEqualsIgnoreCase2 = "position".equalsIgnoreCase(str);
        if (zEqualsIgnoreCase || zEqualsIgnoreCase2) {
            DisplayMetrics displayMetrics = gvVar.getContext().getResources().getDisplayMetrics();
            int iA = a(displayMetrics, map, "x", 0);
            int iA2 = a(displayMetrics, map, "y", 0);
            int iA3 = a(displayMetrics, map, "w", -1);
            int iA4 = a(displayMetrics, map, "h", -1);
            if (zEqualsIgnoreCase && dkVarDu.bW() == null) {
                dkVarDu.c(iA, iA2, iA3, iA4);
                return;
            } else {
                dkVarDu.b(iA, iA2, iA3, iA4);
                return;
            }
        }
        Cdo cdoBW = dkVarDu.bW();
        if (cdoBW == null) {
            Cdo.a(gvVar, "no_video_view", (String) null);
            return;
        }
        if ("click".equalsIgnoreCase(str)) {
            DisplayMetrics displayMetrics2 = gvVar.getContext().getResources().getDisplayMetrics();
            int iA5 = a(displayMetrics2, map, "x", 0);
            int iA6 = a(displayMetrics2, map, "y", 0);
            long jUptimeMillis = SystemClock.uptimeMillis();
            MotionEvent motionEventObtain = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 0, iA5, iA6, 0);
            cdoBW.b(motionEventObtain);
            motionEventObtain.recycle();
            return;
        }
        if ("controls".equalsIgnoreCase(str)) {
            String str2 = map.get("enabled");
            if (str2 == null) {
                gs.W("Enabled parameter missing from controls video GMSG.");
                return;
            } else {
                cdoBW.q(Boolean.parseBoolean(str2));
                return;
            }
        }
        if ("currentTime".equalsIgnoreCase(str)) {
            String str3 = map.get("time");
            if (str3 == null) {
                gs.W("Time parameter missing from currentTime video GMSG.");
                return;
            }
            try {
                cdoBW.seekTo((int) (Float.parseFloat(str3) * 1000.0f));
                return;
            } catch (NumberFormatException e) {
                gs.W("Could not parse time parameter from currentTime video GMSG: " + str3);
                return;
            }
        }
        if ("hide".equalsIgnoreCase(str)) {
            cdoBW.setVisibility(4);
            return;
        }
        if ("load".equalsIgnoreCase(str)) {
            cdoBW.ci();
            return;
        }
        if ("pause".equalsIgnoreCase(str)) {
            cdoBW.pause();
            return;
        }
        if ("play".equalsIgnoreCase(str)) {
            cdoBW.play();
            return;
        }
        if ("show".equalsIgnoreCase(str)) {
            cdoBW.setVisibility(0);
        } else if ("src".equalsIgnoreCase(str)) {
            cdoBW.C(map.get("src"));
        } else {
            gs.W("Unknown video action: " + str);
        }
    }
}
