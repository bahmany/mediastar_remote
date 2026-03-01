package com.iflytek.cloud.a.f;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.view.View;

/* loaded from: classes.dex */
public class f {
    public static int a = 9;
    public static int b = 14;

    public static void a(View view) {
        if (Build.VERSION.SDK_INT >= b) {
            g.a(view);
        }
    }

    public static boolean a(Context context, Boolean bool, AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener) {
        if (!bool.booleanValue() || Build.VERSION.SDK_INT < a) {
            return false;
        }
        g.a(context, onAudioFocusChangeListener);
        return false;
    }

    public static boolean b(Context context, Boolean bool, AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener) {
        if (!bool.booleanValue() || Build.VERSION.SDK_INT < a) {
            return false;
        }
        return g.b(context, onAudioFocusChangeListener);
    }
}
