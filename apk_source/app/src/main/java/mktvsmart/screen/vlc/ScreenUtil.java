package mktvsmart.screen.vlc;

import android.content.Context;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class ScreenUtil {
    public static int getScreenOrientation(Context context) throws NoSuchMethodException, SecurityException {
        WindowManager wm = (WindowManager) context.getSystemService("window");
        Display display = wm.getDefaultDisplay();
        int rot = getScreenRotation(context);
        boolean defaultWide = display.getWidth() > display.getHeight();
        if (rot == 1 || rot == 3) {
            defaultWide = !defaultWide;
        }
        if (defaultWide) {
            switch (rot) {
                case 0:
                default:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return Build.VERSION.SDK_INT >= 8 ? 8 : 0;
                case 3:
                    return Build.VERSION.SDK_INT >= 8 ? 9 : 1;
            }
        }
        switch (rot) {
            case 0:
                return 1;
            case 1:
            default:
                return 0;
            case 2:
                return Build.VERSION.SDK_INT < 8 ? 1 : 9;
            case 3:
                return Build.VERSION.SDK_INT < 8 ? 0 : 8;
        }
    }

    public static int getScreenRotation(Context context) throws NoSuchMethodException, SecurityException {
        WindowManager wm = (WindowManager) context.getSystemService("window");
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 8) {
            try {
                Method m = display.getClass().getDeclaredMethod("getRotation", new Class[0]);
                return ((Integer) m.invoke(display, new Object[0])).intValue();
            } catch (Exception e) {
                return 0;
            }
        }
        return display.getOrientation();
    }
}
