package com.iflytek.speech;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.util.HashMap;

/* loaded from: classes.dex */
public class UtilityConfig {
    public static final String ACTION_SPEAKER_VERIFIER = "com.iflytek.component.speakerverifier";
    public static final String ACTION_SPEECH_RECOGNIZER = "com.iflytek.component.speechrecognizer";
    public static final String ACTION_SPEECH_SYNTHESIZER = "com.iflytek.component.speechsynthesizer";
    public static final String ACTION_SPEECH_UNDERSTANDER = "com.iflytek.component.speechunderstander";
    public static final String ACTION_SPEECH_WAKEUP = "com.iflytek.component.speechwakeuper";
    public static final String ACTION_TEXT_UNDERSTANDER = "com.iflytek.component.textunderstander";
    public static final String CHANNEL_ID = "16010000";
    public static final String CHANNEL_NAME = "dev.voicecloud";
    public static final String COMPONENT_PKG = "com.iflytek.speechcloud";
    public static final String COMPONENT_URL = "http://open.voicecloud.cn/s?";
    public static final String KEY_CALLER_APPID = "caller.appid";
    public static final String KEY_CALLER_NAME = "caller.name";
    public static final String KEY_CALLER_PKG_NAME = "caller.pkg";
    public static final String KEY_CALLER_VER_CODE = "caller.ver.code";
    public static final String KEY_CALLER_VER_NAME = "caller.ver.name";
    public static final String KEY_CHANNEL_ID = "channel.id";
    public static final String KEY_CHANNEL_NAME = "channel.name";
    public static final String KEY_REQUEST_PACKAGE = "request.package";
    public static final String METADATA_KEY_ENGINE_TYPE = "enginetype";
    public static final String SDK_VER_NAME = "sdk.ver.name";
    public static final String SETTINGS_ACTION_ASR = "com.iflytek.speechcloud.settings.asr";
    public static final String SETTINGS_ACTION_MAIN = "com.iflytek.speechcloud.settings.main";
    public static final String SETTINGS_ACTION_TTS = "com.iflytek.speechcloud.activity.speaker.SpeakerSetting";
    private static HashMap<String, String> callerHashMap = new HashMap<>();

    public static void appendHttpParam(StringBuffer stringBuffer, String str, String str2) {
        if (stringBuffer == null || str == null || str2 == null || str2.length() <= 0) {
            return;
        }
        stringBuffer.append('&');
        stringBuffer.append(str);
        stringBuffer.append('=');
        stringBuffer.append(str2);
    }

    public static String getCallerInfo(Context context, String str) throws PackageManager.NameNotFoundException {
        if (callerHashMap.containsKey(str)) {
            return callerHashMap.get(str);
        }
        try {
            String packageName = context.getPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            callerHashMap.put(KEY_CALLER_NAME, applicationInfo.loadLabel(context.getPackageManager()).toString());
            callerHashMap.put(KEY_CALLER_PKG_NAME, applicationInfo.packageName);
            callerHashMap.put(KEY_CALLER_VER_NAME, packageInfo.versionName);
            callerHashMap.put(KEY_CALLER_VER_CODE, String.valueOf(packageInfo.versionCode));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return callerHashMap.get(str);
    }

    public static String getComponentUrlParam(Context context) throws PackageManager.NameNotFoundException {
        String callerInfo = getCallerInfo(context, KEY_CALLER_NAME);
        String callerInfo2 = getCallerInfo(context, KEY_CALLER_PKG_NAME);
        String callerInfo3 = getCallerInfo(context, KEY_CALLER_VER_NAME);
        String callerInfo4 = getCallerInfo(context, KEY_CALLER_VER_CODE);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(KEY_CHANNEL_ID);
        stringBuffer.append('=');
        stringBuffer.append(CHANNEL_ID);
        appendHttpParam(stringBuffer, SDK_VER_NAME, Version.getVersionName());
        appendHttpParam(stringBuffer, KEY_CALLER_NAME, callerInfo);
        appendHttpParam(stringBuffer, KEY_CALLER_PKG_NAME, callerInfo2);
        appendHttpParam(stringBuffer, KEY_CALLER_VER_NAME, callerInfo3);
        appendHttpParam(stringBuffer, KEY_CALLER_VER_CODE, callerInfo4);
        return stringBuffer.toString();
    }
}
