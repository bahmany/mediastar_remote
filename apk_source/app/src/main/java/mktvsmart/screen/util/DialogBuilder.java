package mktvsmart.screen.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class DialogBuilder {
    public static ADSProgressDialog showProgressDialog(Activity mActivity, int title, int message, boolean cancelable, long timeout) {
        return showProgressDialog(mActivity, title, message, cancelable, timeout, (Runnable) null);
    }

    public static ADSProgressDialog showProgressDialog(Activity mActivity, String title, String message, boolean cancelable, long timeout) {
        return showProgressDialog(mActivity, title, message, cancelable, timeout, (Runnable) null);
    }

    public static ADSProgressDialog showProgressDialog(Activity mActivity, int title, int message, boolean cancelable, long timeout, int timeoutMsg) {
        return showProgressDialog(mActivity, mActivity.getString(title), mActivity.getString(message), cancelable, timeout, mActivity.getString(timeoutMsg));
    }

    public static ADSProgressDialog showProgressDialog(final Activity mActivity, String title, String message, boolean cancelable, long timeout, final String timeoutMsg) {
        Runnable run = null;
        if (timeoutMsg != null) {
            run = new Runnable() { // from class: mktvsmart.screen.util.DialogBuilder.1
                @Override // java.lang.Runnable
                public void run() {
                    Toast.makeText(mActivity, timeoutMsg, 0).show();
                }
            };
        }
        return showProgressDialog(mActivity, title, message, cancelable, timeout, run);
    }

    public static ADSProgressDialog showProgressDialog(Activity mActivity, int title, int message, boolean cancelable, long timeout, Runnable run) {
        return showProgressDialog(mActivity, mActivity.getString(title), mActivity.getString(message), cancelable, timeout, run);
    }

    public static ADSProgressDialog showProgressDialog(final Activity mActivity, String title, String message, boolean cancelable, long timeout, final Runnable run) {
        final ADSProgressDialog dialog = showProgressDialog(mActivity, title, message, cancelable);
        Timer waitType = new Timer();
        waitType.schedule(new TimerTask() { // from class: mktvsmart.screen.util.DialogBuilder.2
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (dialog.isShowing() && run != null) {
                    Log.e("1111111", "timeout runable run");
                    Activity activity = mActivity;
                    final ADSProgressDialog aDSProgressDialog = dialog;
                    final Runnable runnable = run;
                    activity.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.util.DialogBuilder.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            aDSProgressDialog.dismiss();
                            runnable.run();
                        }
                    });
                }
            }
        }, timeout);
        return dialog;
    }

    public static ADSProgressDialog showProgressDialog(Activity mActivity, int title, int message, boolean cancelable) {
        return showProgressDialog(mActivity, mActivity.getString(title), mActivity.getString(message), cancelable);
    }

    public static ADSProgressDialog showProgressDialog(Context mContext, String title, String message, boolean cancelable) {
        ADSProgressDialog dialog = new ADSProgressDialog(mContext);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }
}
