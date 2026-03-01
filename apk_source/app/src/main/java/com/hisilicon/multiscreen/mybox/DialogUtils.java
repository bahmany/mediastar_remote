package com.hisilicon.multiscreen.mybox;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/* loaded from: classes.dex */
public class DialogUtils {
    static Context mContext = null;

    public static void setCurContext(Context context) {
        mContext = context;
    }

    public static void showDialog(Context ctx, String title, String msg, DialogInterface.OnClickListener listener) {
        if (ctx != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton("OK", listener);
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public static void showMenuDialog(Context ctx, String title, String[] items, DialogInterface.OnClickListener listener) {
        if (ctx != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(title).setItems(items, listener);
            builder.create().show();
        }
    }

    public static ProgressDialog showProgressDialog(Context ctx, String title, String msg) {
        if (ctx == null) {
            return null;
        }
        ProgressDialog pd = new ProgressDialog(ctx);
        pd.setTitle(title);
        pd.setCancelable(true);
        pd.setMessage(msg);
        pd.show();
        return pd;
    }

    public static void showToast(String message, Context ctx) {
        if (ctx != null) {
            FrameLayout frameLayout = new FrameLayout(ctx);
            LinearLayout linearLayout = new LinearLayout(ctx);
            TextView textView = new TextView(ctx);
            linearLayout.setOrientation(0);
            textView.setGravity(16);
            textView.setText(message);
            linearLayout.addView(textView, new LinearLayout.LayoutParams(-2, -2));
            frameLayout.addView(linearLayout, new LinearLayout.LayoutParams(-2, -2));
            frameLayout.setBackgroundResource(R.drawable.toast_frame);
            Toast toast = new Toast(ctx);
            toast.setView(frameLayout);
            toast.setDuration(0);
            toast.show();
        }
    }

    public static void showToastLong(int msgId, Context ctx) {
        if (ctx != null) {
            FrameLayout frameLayout = new FrameLayout(ctx);
            LinearLayout linearLayout = new LinearLayout(ctx);
            TextView textView = new TextView(ctx);
            linearLayout.setOrientation(0);
            textView.setGravity(16);
            textView.setText(msgId);
            linearLayout.addView(textView, new LinearLayout.LayoutParams(-2, -2));
            frameLayout.addView(linearLayout, new LinearLayout.LayoutParams(-2, -2));
            frameLayout.setBackgroundResource(R.drawable.toast_frame);
            Toast toast = new Toast(ctx);
            toast.setView(frameLayout);
            toast.setDuration(1);
            toast.show();
        }
    }

    public static void showToastLong(String msg, Context ctx) {
        if (ctx != null) {
            FrameLayout frameLayout = new FrameLayout(ctx);
            LinearLayout linearLayout = new LinearLayout(ctx);
            TextView textView = new TextView(ctx);
            linearLayout.setOrientation(0);
            textView.setGravity(16);
            textView.setText(msg);
            linearLayout.addView(textView, new LinearLayout.LayoutParams(-2, -2));
            frameLayout.addView(linearLayout, new LinearLayout.LayoutParams(-2, -2));
            frameLayout.setBackgroundResource(R.drawable.toast_frame);
            Toast toast = new Toast(ctx);
            toast.setView(frameLayout);
            toast.setDuration(1);
            toast.show();
        }
    }

    public static void showOutsideCancelDialog(Context ctx, String title, String msg, DialogInterface.OnClickListener okListener, DialogInterface.OnDismissListener dismissListener) {
        if (ctx != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton("OK", okListener);
            AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(dismissListener);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    public static void showOutsideCancelDialog(Context ctx, int titleId, int msgId, DialogInterface.OnClickListener okListener) {
        if (ctx != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(titleId);
            builder.setMessage(msgId);
            builder.setPositiveButton("OK", okListener);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    public static void showCancelDialog(Context ctx, int titleId, int msgId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancleListener) {
        if (ctx != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(titleId);
            builder.setMessage(msgId);
            builder.setPositiveButton("OK", okListener);
            builder.setNegativeButton("Cancel", cancleListener);
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    public static void showDialogNoCancelable(Context ctx, String title, String msg, DialogInterface.OnClickListener listener) {
        if (ctx != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton("OK", listener);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public static Dialog showDialogNoCancelable(Context ctx, int titleId, int msgId, DialogInterface.OnClickListener listener) {
        if (ctx == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(titleId);
        builder.setMessage(msgId);
        builder.setPositiveButton("OK", listener);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }
}
