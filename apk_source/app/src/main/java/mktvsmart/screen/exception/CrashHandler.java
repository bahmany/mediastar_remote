package mktvsmart.screen.exception;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import java.lang.Thread;
import java.lang.ref.WeakReference;
import mktvsmart.screen.GMScreenApp;
import mktvsmart.screen.GMScreenGlobalInfo;

/* loaded from: classes.dex */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private WeakReference<Activity> lastActivityCreated;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

    @SuppressLint({"NewApi"})
    public CrashHandler(Context context) {
        this.mContext = null;
        this.lastActivityCreated = null;
        this.mContext = context;
        if ((this.mContext instanceof Application) && Build.VERSION.SDK_INT >= 14) {
            this.lastActivityCreated = new WeakReference<>(null);
            ((Application) this.mContext).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() { // from class: mktvsmart.screen.exception.CrashHandler.1
                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityStopped(Activity activity) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityStarted(Activity activity) {
                    Log.d("ActivityLifecycleCallbacks", activity.getClass() + " onStart");
                    CrashHandler.this.lastActivityCreated = new WeakReference(activity);
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityResumed(Activity activity) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityPaused(Activity activity) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityDestroyed(Activity activity) {
                }

                @Override // android.app.Application.ActivityLifecycleCallbacks
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                }
            });
        }
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable ex) {
        StackTraceElement[] elements;
        String temp;
        if (this.lastActivityCreated != null) {
            Log.d("UncaughtException", "ex = " + ex);
            if (ex != null) {
                Log.d("UncaughtException", "error message = " + ex.getMessage());
                StringBuilder sb = new StringBuilder();
                String temp2 = ex.getMessage();
                if (temp2 != null) {
                    sb.append("<br>").append(temp2).append("</br>");
                }
                sb.append("<br><font color=\"red\">").append(thread.getName()).append(" Trace: </font>").append("</br>");
                StackTraceElement[] elements2 = ex.getStackTrace();
                if (elements2 != null) {
                    for (StackTraceElement element : elements2) {
                        String temp3 = element.toString();
                        if (temp3 != null) {
                            sb.append("<br>").append(temp3).append("</br>");
                        }
                    }
                }
                sb.append("<br><font color=\"red\">").append("Cause: </font>").append("</br>");
                Throwable theCause = ex.getCause();
                if (theCause != null && (temp = theCause.toString()) != null) {
                    sb.append("<br>").append(temp).append("</br>");
                }
                sb.append("<br><font color=\"red\">").append("Cause Stack: </font>").append("</br>");
                Throwable theCause2 = ex.getCause();
                if (theCause2 != null && (elements = theCause2.getStackTrace()) != null) {
                    for (StackTraceElement element2 : elements) {
                        String temp4 = element2.toString();
                        if (temp4 != null) {
                            sb.append("<br>").append(temp4).append("</br>");
                        }
                    }
                }
                Intent intent = new Intent(GMScreenApp.getAppContext(), (Class<?>) ReportPage.class);
                intent.setFlags(268435456);
                intent.putExtra("PID", GMScreenGlobalInfo.getCurStbPlatform());
                intent.putExtra(ReportPage.REPORT_CONTENT, sb.toString());
                GMScreenApp.getAppContext().startActivity(intent);
                if (this.lastActivityCreated.get() != null) {
                    this.lastActivityCreated.get().finish();
                }
                Process.killProcess(Process.myPid());
                System.exit(10);
                return;
            }
            if (this.mDefaultHandler != null) {
                this.mDefaultHandler.uncaughtException(thread, ex);
                return;
            }
            return;
        }
        if (this.mDefaultHandler != null) {
            this.mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    public void uncaughtException(Exception e) {
        uncaughtException(Thread.currentThread(), e);
    }
}
