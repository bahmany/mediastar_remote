package com.iflytek.cloud.a.f;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechError;
import com.iflytek.speech.VoiceWakeuperAidl;

/* loaded from: classes.dex */
public class h {
    public static String a(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            return "none";
        }
        try {
            if (networkInfo.getType() == 1) {
                return "wifi";
            }
            String lowerCase = networkInfo.getExtraInfo().toLowerCase();
            return TextUtils.isEmpty(lowerCase) ? "none" : (lowerCase.startsWith("3gwap") || lowerCase.startsWith("uniwap")) ? "uniwap" : lowerCase.startsWith("cmwap") ? "cmwap" : lowerCase.startsWith("ctwap") ? "ctwap" : lowerCase.startsWith("ctnet") ? "ctnet" : lowerCase;
        } catch (Exception e) {
            com.iflytek.cloud.a.f.a.a.a(e.toString());
            return "none";
        }
    }

    public static void a(Context context) throws SpeechError {
        if (!Setting.b || context == null) {
            return;
        }
        NetworkInfo[] allNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getAllNetworkInfo();
        if (allNetworkInfo != null && allNetworkInfo.length > 0) {
            for (NetworkInfo networkInfo : allNetworkInfo) {
                if (networkInfo == null || networkInfo.isConnectedOrConnecting()) {
                    return;
                }
            }
        }
        throw new SpeechError(20001);
    }

    public static String b(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            return "none";
        }
        try {
            if (networkInfo.getType() == 1) {
                return "none";
            }
            return ("" + networkInfo.getSubtypeName()) + VoiceWakeuperAidl.PARAMS_SEPARATE + networkInfo.getSubtype();
        } catch (Exception e) {
            com.iflytek.cloud.a.f.a.a.a(e.toString());
            return "none";
        }
    }
}
