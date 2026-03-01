package com.iflytek.cloud;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Base64;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import com.iflytek.cloud.a.c.d;
import com.iflytek.msc.MSC;
import com.iflytek.speech.SpeechComponent;
import com.iflytek.speech.UtilityConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class SpeechUtility extends com.iflytek.cloud.a.c.d {
    private static SpeechUtility c = null;
    protected d.a a;
    private Context f;
    private ArrayList<SpeechComponent> d = new ArrayList<>();
    private int e = -1;
    private boolean g = false;

    private SpeechUtility(Context context, String str) throws PackageManager.NameNotFoundException {
        this.f = null;
        this.a = d.a.AUTO;
        this.f = context.getApplicationContext();
        setParameter(SpeechConstant.PARAMS, str);
        String parameter = getParameter(SpeechConstant.ENGINE_MODE);
        if (SpeechConstant.MODE_MSC.equals(parameter)) {
            this.a = d.a.MSC;
        } else if (SpeechConstant.MODE_PLUS.equals(parameter)) {
            this.a = d.a.PLUS;
        }
        b();
        d();
    }

    private void a(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        PackageManager packageManager = this.f.getPackageManager();
        Intent intent = new Intent(str);
        intent.setPackage(UtilityConfig.COMPONENT_PKG);
        List<ResolveInfo> listQueryIntentServices = packageManager.queryIntentServices(intent, KeyInfo.KEYCODE_O);
        if (listQueryIntentServices == null || listQueryIntentServices.size() <= 0) {
            return;
        }
        for (ResolveInfo resolveInfo : listQueryIntentServices) {
            SpeechComponent speechComponentB = b(resolveInfo.serviceInfo.packageName);
            if (speechComponentB != null) {
                try {
                    String[] strArrSplit = resolveInfo.serviceInfo.metaData.getString(UtilityConfig.METADATA_KEY_ENGINE_TYPE).split(ClientInfo.SEPARATOR_BETWEEN_VARS);
                    for (String str2 : strArrSplit) {
                        speechComponentB.addEngine(str2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int b() {
        if (!MSC.isLoaded()) {
            return ErrorCode.ERROR_ENGINE_NOT_SUPPORTED;
        }
        com.iflytek.cloud.a.f.a.a.a("SpeechUtility start login");
        SpeechError speechErrorA = new com.iflytek.cloud.a.d.b(this.f, this.b).a(getParameter("usr"), getParameter("pwd"));
        if (speechErrorA == null) {
            return 0;
        }
        return speechErrorA.getErrorCode();
    }

    private SpeechComponent b(String str) {
        boolean z;
        SpeechComponent speechComponent;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Iterator<SpeechComponent> it = this.d.iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            }
            if (str.equals(it.next().getPackageName())) {
                z = true;
                break;
            }
        }
        if (z) {
            speechComponent = null;
        } else {
            speechComponent = new SpeechComponent(str);
            this.d.add(speechComponent);
        }
        return speechComponent;
    }

    private boolean c() {
        if (!MSC.isLoaded()) {
            return false;
        }
        com.iflytek.cloud.a.f.a.b.a("QMSPLogOut", null);
        if (MSC.isLoaded()) {
            return com.iflytek.cloud.a.d.a.a();
        }
        return true;
    }

    private boolean c(String str) {
        PackageManager packageManager = this.f.getPackageManager();
        Intent intent = new Intent(str);
        intent.setPackage(UtilityConfig.COMPONENT_PKG);
        return packageManager.queryIntentActivities(intent, 1).size() > 0;
    }

    public static SpeechUtility createUtility(Context context, String str) {
        if (c == null) {
            c = new SpeechUtility(context, str);
        }
        return c;
    }

    private void d() throws PackageManager.NameNotFoundException {
        checkServiceInstalled();
        a(UtilityConfig.ACTION_SPEECH_RECOGNIZER);
        a(UtilityConfig.ACTION_SPEECH_SYNTHESIZER);
        a(UtilityConfig.ACTION_SPEECH_UNDERSTANDER);
        a(UtilityConfig.ACTION_TEXT_UNDERSTANDER);
        a(UtilityConfig.ACTION_SPEECH_WAKEUP);
    }

    public static SpeechUtility getUtility() {
        return c;
    }

    protected boolean a() {
        try {
            return this.f.getPackageManager().getPackageInfo(UtilityConfig.COMPONENT_PKG, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean checkServiceInstalled() throws PackageManager.NameNotFoundException {
        boolean z = false;
        int i = -1;
        try {
            PackageInfo packageInfo = this.f.getPackageManager().getPackageInfo(UtilityConfig.COMPONENT_PKG, 0);
            if (packageInfo != null) {
                z = true;
                i = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (z != this.g || this.e != i) {
            this.g = z;
            this.e = i;
            if (SpeechRecognizer.getRecognizer() != null) {
                SpeechRecognizer.getRecognizer().a(this.f);
            }
            if (SpeechSynthesizer.getSynthesizer() != null) {
                SpeechSynthesizer.getSynthesizer().a(this.f);
            }
        }
        return z;
    }

    public boolean destroy() {
        boolean zC = c != null ? c() : true;
        if (zC) {
            c = null;
            com.iflytek.cloud.a.f.a.a.a(" SpeechUtility destory success,mInstance=null");
        }
        return zC;
    }

    public String getComponentUrl() {
        StringBuffer stringBuffer = new StringBuffer(UtilityConfig.COMPONENT_URL);
        UtilityConfig.appendHttpParam(stringBuffer, "key", Base64.encodeToString(UtilityConfig.getComponentUrlParam(this.f).getBytes(), 0));
        UtilityConfig.appendHttpParam(stringBuffer, "version", "1.0");
        return stringBuffer.toString();
    }

    public d.a getEngineMode() {
        return this.a;
    }

    public int getServiceVersion() throws PackageManager.NameNotFoundException {
        if (this.e < 0) {
            try {
                PackageInfo packageInfo = this.f.getPackageManager().getPackageInfo(UtilityConfig.COMPONENT_PKG, 0);
                if (packageInfo != null) {
                    this.e = packageInfo.versionCode;
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return this.e;
    }

    public int openEngineSettings(String str) {
        try {
            Intent intent = new Intent();
            intent.setPackage(UtilityConfig.COMPONENT_PKG);
            String str2 = UtilityConfig.COMPONENT_PKG;
            if (SpeechConstant.ENG_TTS.equals(str) && c(UtilityConfig.SETTINGS_ACTION_TTS)) {
                str2 = UtilityConfig.SETTINGS_ACTION_TTS;
            } else if (SpeechConstant.ENG_ASR.equals(str) && c(UtilityConfig.SETTINGS_ACTION_ASR)) {
                str2 = UtilityConfig.SETTINGS_ACTION_ASR;
            } else if (c(UtilityConfig.SETTINGS_ACTION_MAIN)) {
                str2 = UtilityConfig.SETTINGS_ACTION_MAIN;
            }
            intent.setAction(str2);
            intent.addFlags(268435456);
            this.f.startActivity(intent);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.ERROR_ENGINE_NOT_SUPPORTED;
        }
    }

    public String[] queryAvailableEngines() throws PackageManager.NameNotFoundException {
        this.d.clear();
        d();
        ArrayList arrayList = new ArrayList();
        Iterator<SpeechComponent> it = this.d.iterator();
        while (it.hasNext()) {
            arrayList.addAll(it.next().getEngines());
        }
        String[] strArr = new String[arrayList.size()];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= arrayList.size()) {
                return strArr;
            }
            strArr[i2] = (String) arrayList.get(i2);
            i = i2 + 1;
        }
    }
}
