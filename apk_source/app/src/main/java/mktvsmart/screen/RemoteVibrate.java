package mktvsmart.screen;

import android.app.Activity;
import android.os.Vibrator;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;

/* loaded from: classes.dex */
public class RemoteVibrate {
    public static void Vibrate(Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(MultiSettingActivity.VIBRATOR_STATUS_KEY);
        vib.vibrate(milliseconds);
    }

    public static void Vibrate(Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(MultiSettingActivity.VIBRATOR_STATUS_KEY);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }
}
