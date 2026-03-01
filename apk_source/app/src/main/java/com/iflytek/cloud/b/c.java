package com.iflytek.cloud.b;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.Version;
import com.iflytek.cloud.a.f.e;
import com.iflytek.cloud.a.f.h;

/* loaded from: classes.dex */
public class c {
    private static String a = "xiaoyan";

    public static String a(Context context) {
        if (context == null) {
            return "null";
        }
        a aVarA = com.iflytek.cloud.a.f.a.a(context);
        String strD = aVarA.d("os.imsi") + "|" + aVarA.d("os.imei");
        if (strD.length() < 10) {
            strD = aVarA.d("net.mac");
        }
        if (TextUtils.isEmpty(strD) || strD.length() <= 0) {
            return null;
        }
        return strD;
    }

    public static String a(Context context, com.iflytek.cloud.a.c.a aVar) throws SpeechError {
        if (context == null) {
            throw new SpeechError(ErrorCode.ERROR_INVALID_PARAM);
        }
        a aVarT = aVar.t();
        a(context, aVarT);
        aVarT.a(SpeechConstant.NET_TIMEOUT, "20000", false);
        aVarT.a("auth", "1", false);
        aVarT.a("msc.ver", Version.getVersion());
        a aVarA = com.iflytek.cloud.a.f.a.a(context);
        aVarT.a("mac", aVarA.d("net.mac"), false);
        aVarT.a("dvc", a(context), false);
        aVarT.a("msc.lat", "" + com.iflytek.cloud.a.f.b.a(context).a("msc.lat"), false);
        aVarT.a("msc.lng", "" + com.iflytek.cloud.a.f.b.a(context).a("msc.lng"), false);
        aVarT.a(aVarA, "app.name");
        aVarT.a(aVarA, "app.path");
        aVarT.a(aVarA, "app.pkg");
        aVarT.a(aVarA, "app.ver.name");
        aVarT.a(aVarA, "app.ver.code");
        aVarT.a(aVarA, "os.system");
        aVarT.a(aVarA, "os.resolution");
        aVarT.a(aVarA, "os.density");
        aVarT.a(aVarA, "net.mac");
        aVarT.a(aVarA, "os.imei");
        aVarT.a(aVarA, "os.imsi");
        aVarT.a(aVarA, "os.version");
        aVarT.a(aVarA, "os.release");
        aVarT.a(aVarA, "os.incremental");
        aVarT.a(aVarA, "os.android_id");
        aVarT.a(aVarA, com.iflytek.cloud.a.f.a.a[0][0]);
        aVarT.a(aVarA, com.iflytek.cloud.a.f.a.a[1][0]);
        aVarT.a(aVarA, com.iflytek.cloud.a.f.a.a[2][0]);
        aVarT.a(aVarA, com.iflytek.cloud.a.f.a.a[3][0]);
        a(aVarT);
        return aVarT.toString();
    }

    public static String a(Context context, String str, com.iflytek.cloud.a.c.a aVar) {
        a aVarT = aVar.t();
        a(context, aVarT);
        aVarT.a("rst", "json", false);
        aVarT.a("rse", aVar.p(), false);
        aVarT.a("tte", aVar.o(), false);
        aVarT.a("ssm", "1", false);
        if (TextUtils.isEmpty(str)) {
            aVarT.a("sub", "iat", false);
        } else {
            aVarT.a("sub", SpeechConstant.ENG_ASR, false);
        }
        int iQ = aVar.q();
        aVarT.a("auf=audio/L16;rate", Integer.toString(iQ), false);
        if (iQ == 16000) {
            aVarT.a("aue", "speex-wb", false);
        } else {
            aVarT.a("aue", "speex", false);
        }
        if (aVar.i()) {
            aVarT.a("vad_timeout", "5000", false);
            aVarT.a("vad_speech_tail", "1800", false);
            aVarT.a("eos", "1800", false);
        } else {
            aVarT.a("vad_timeout", "4000", false);
            aVarT.a("vad_speech_tail", "700", false);
            aVarT.a("eos", "700", false);
        }
        return aVarT.toString();
    }

    public static void a(Context context, a aVar) {
        if (context == null) {
            aVar.a(SpeechConstant.WAP_PROXY, "none", false);
            return;
        }
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            aVar.a(SpeechConstant.WAP_PROXY, "none", false);
        } else {
            aVar.a(SpeechConstant.WAP_PROXY, h.a(activeNetworkInfo), false);
            aVar.a("net_subtype", h.b(activeNetworkInfo), false);
        }
    }

    private static void a(a aVar) {
        if (aVar == null || Setting.d == Setting.LOG_LEVEL.none) {
            return;
        }
        String str = Setting.e;
        if (TextUtils.isEmpty(str)) {
            str = "/sdcard/msc/msc.log";
        }
        int i = -1;
        if (Setting.d == Setting.LOG_LEVEL.detail) {
            i = 31;
        } else if (Setting.d == Setting.LOG_LEVEL.normal) {
            i = 15;
        } else if (Setting.d == Setting.LOG_LEVEL.low) {
            i = 7;
        }
        e.b(str);
        aVar.a("log", str);
        aVar.a("lvl", "" + i);
        aVar.a("output", "1", false);
    }

    public static boolean a(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.contains("sms") || str.contains("iat");
    }

    public static String b(Context context, com.iflytek.cloud.a.c.a aVar) {
        a aVarT = aVar.t();
        a(context, aVarT);
        aVarT.a("rst", "json");
        aVarT.a("rse", aVar.p());
        aVarT.a("tte", aVar.o());
        aVarT.a("ssm", "1", false);
        aVarT.a("sub", SpeechConstant.ENG_IVP, false);
        int iQ = aVar.q();
        aVarT.a("auf=audio/L16;rate", Integer.toString(iQ), false);
        if (iQ == 16000) {
            aVarT.a("aue", "speex-wb", false);
        } else {
            aVarT.a("aue", "speex", false);
        }
        aVarT.a("vad_timeout", "3000", false);
        aVarT.a("vad_speech_tail", "700", false);
        aVarT.a("eos", "700", false);
        return aVarT.toString();
    }

    public static String c(Context context, com.iflytek.cloud.a.c.a aVar) {
        a aVarT = aVar.t();
        a(context, aVarT);
        aVarT.a("ssm", "1", false);
        aVarT.a("rst", "json", false);
        aVarT.a("rse", aVar.p(), false);
        aVarT.a("tte", aVar.o(), false);
        return aVarT.toString();
    }

    public static String d(Context context, com.iflytek.cloud.a.c.a aVar) {
        a aVarT = aVar.t();
        a(context, aVarT);
        aVarT.a(b.a);
        aVarT.a("ssm", "1", false);
        int iQ = aVar.q();
        aVarT.a("auf=audio/L16;rate", Integer.toString(iQ));
        if (iQ == 16000) {
            aVarT.a("aue", "speex-wb", false);
        } else {
            aVarT.a("aue", "speex", false);
        }
        aVarT.a("vcn", aVarT.b("vcn", a), true);
        aVarT.a("tte", aVar.o(), false);
        return aVarT.toString();
    }
}
