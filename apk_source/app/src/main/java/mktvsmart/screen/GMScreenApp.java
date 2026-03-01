package mktvsmart.screen;

import android.app.Application;
import android.content.Context;
import com.hisilicon.multiscreen.mybox.HiMultiscreen;
import mktvsmart.screen.exception.CrashHandler;
import mktvsmart.screen.util.AdsControllor;
import org.videolan.vlc.util.VLCInstance;

/* loaded from: classes.dex */
public class GMScreenApp extends Application {
    private static Context appContext;
    private static boolean bHandleStopException = true;

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        VLCInstance.setApp(this);
        if (bHandleStopException) {
            Thread.currentThread().setUncaughtExceptionHandler(new CrashHandler(this));
        }
        HiMultiscreen.initialize(this);
        new Thread() { // from class: mktvsmart.screen.GMScreenApp.1
            AnonymousClass1() {
            }

            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                super.run();
                VLCInstance.initialize(GMScreenApp.this, R.raw.libvlc);
            }
        }.start();
        setAppContext(this);
        AdsControllor.obtain();
    }

    /* renamed from: mktvsmart.screen.GMScreenApp$1 */
    class AnonymousClass1 extends Thread {
        AnonymousClass1() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            VLCInstance.initialize(GMScreenApp.this, R.raw.libvlc);
        }
    }

    public static void setAppContext(Context context) {
        appContext = context;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
