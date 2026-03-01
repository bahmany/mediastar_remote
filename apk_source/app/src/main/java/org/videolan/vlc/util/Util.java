package org.videolan.vlc.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* loaded from: classes.dex */
public class Util {
    public static void toaster(Context context, int stringId) {
        Toast.makeText(context, stringId, 0).show();
    }

    public static int convertPxToDp(int px) {
        DisplayMetrics metrics = VLCInstance.getAppContext().getResources().getDisplayMetrics();
        float logicalDensity = metrics.density;
        int dp = Math.round(px / logicalDensity);
        return dp;
    }

    public static int convertDpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(1, dp, VLCInstance.getAppContext().getResources().getDisplayMetrics()));
    }

    public static String readAsset(String assetName, String defaultS) throws IOException {
        try {
            InputStream is = VLCInstance.getAppContext().getResources().getAssets().open(assetName);
            BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF8"));
            StringBuilder sb = new StringBuilder();
            String line = r.readLine();
            if (line != null) {
                sb.append(line);
                for (String line2 = r.readLine(); line2 != null; line2 = r.readLine()) {
                    sb.append('\n');
                    sb.append(line2);
                }
            }
            defaultS = sb.toString();
            return defaultS;
        } catch (IOException e) {
            return defaultS;
        }
    }

    public static int getResourceFromAttribute(Context context, int attrId) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attrId});
        int resId = a.getResourceId(0, 0);
        a.recycle();
        return resId;
    }

    public static void setAlignModeByPref(int alignMode, TextView t) {
        if (alignMode == 1) {
            t.setEllipsize(TextUtils.TruncateAt.END);
            return;
        }
        if (alignMode == 2) {
            t.setEllipsize(TextUtils.TruncateAt.START);
        } else if (alignMode == 3) {
            t.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            t.setMarqueeRepeatLimit(-1);
            t.setSelected(true);
        }
    }
}
