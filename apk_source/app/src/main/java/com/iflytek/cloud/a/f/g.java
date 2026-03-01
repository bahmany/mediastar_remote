package com.iflytek.cloud.a.f;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;

/* loaded from: classes.dex */
public class g {
    private static int a = 0;

    public static void a(View view) {
        view.setLayerType(1, null);
    }

    public static boolean a(Context context, AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener) {
        try {
            ((AudioManager) context.getSystemService(MultiSettingActivity.AUDIO_STATUS_KEY)).requestAudioFocus(onAudioFocusChangeListener, 3, 2);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean b(Context context, AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener) {
        try {
            ((AudioManager) context.getSystemService(MultiSettingActivity.AUDIO_STATUS_KEY)).abandonAudioFocus(onAudioFocusChangeListener);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
