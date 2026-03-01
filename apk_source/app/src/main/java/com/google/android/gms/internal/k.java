package com.google.android.gms.internal;

import android.content.Context;
import android.net.Uri;
import android.view.MotionEvent;
import com.iflytek.speech.VoiceWakeuperAidl;

/* loaded from: classes.dex */
public class k {
    private String kR = "googleads.g.doubleclick.net";
    private String kS = "/pagead/ads";
    private String kT = "ad.doubleclick.net";
    private String[] kU = {".doubleclick.net", ".googleadservices.com", ".googlesyndication.com"};
    private g kV;

    public k(g gVar) {
        this.kV = gVar;
    }

    private Uri a(Uri uri, Context context, String str, boolean z) throws l {
        try {
            boolean zA = a(uri);
            if (zA) {
                if (uri.toString().contains("dc_ms=")) {
                    throw new l("Parameter already exists: dc_ms");
                }
            } else if (uri.getQueryParameter("ms") != null) {
                throw new l("Query parameter already exists: ms");
            }
            String strA = z ? this.kV.a(context, str) : this.kV.a(context);
            return zA ? b(uri, "dc_ms", strA) : a(uri, "ms", strA);
        } catch (UnsupportedOperationException e) {
            throw new l("Provided Uri is not in a valid state");
        }
    }

    private Uri a(Uri uri, String str, String str2) throws UnsupportedOperationException {
        String string = uri.toString();
        int iIndexOf = string.indexOf("&adurl");
        if (iIndexOf == -1) {
            iIndexOf = string.indexOf("?adurl");
        }
        return iIndexOf != -1 ? Uri.parse(string.substring(0, iIndexOf + 1) + str + "=" + str2 + "&" + string.substring(iIndexOf + 1)) : uri.buildUpon().appendQueryParameter(str, str2).build();
    }

    private Uri b(Uri uri, String str, String str2) {
        String string = uri.toString();
        int iIndexOf = string.indexOf(";adurl");
        if (iIndexOf != -1) {
            return Uri.parse(string.substring(0, iIndexOf + 1) + str + "=" + str2 + VoiceWakeuperAidl.PARAMS_SEPARATE + string.substring(iIndexOf + 1));
        }
        String encodedPath = uri.getEncodedPath();
        int iIndexOf2 = string.indexOf(encodedPath);
        return Uri.parse(string.substring(0, encodedPath.length() + iIndexOf2) + VoiceWakeuperAidl.PARAMS_SEPARATE + str + "=" + str2 + VoiceWakeuperAidl.PARAMS_SEPARATE + string.substring(encodedPath.length() + iIndexOf2));
    }

    public Uri a(Uri uri, Context context) throws l {
        try {
            return a(uri, context, uri.getQueryParameter("ai"), true);
        } catch (UnsupportedOperationException e) {
            throw new l("Provided Uri is not in a valid state");
        }
    }

    public void a(MotionEvent motionEvent) {
        this.kV.a(motionEvent);
    }

    public boolean a(Uri uri) {
        if (uri == null) {
            throw new NullPointerException();
        }
        try {
            return uri.getHost().equals(this.kT);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean b(Uri uri) {
        if (uri == null) {
            throw new NullPointerException();
        }
        try {
            String host = uri.getHost();
            for (String str : this.kU) {
                if (host.endsWith(str)) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public g z() {
        return this.kV;
    }
}
