package mktvsmart.screen.util;

import android.content.Context;
import android.graphics.Color;
import android.view.WindowManager;
import android.widget.TextView;

/* compiled from: AdsControllor.java */
/* loaded from: classes.dex */
class AdCoverWindow {
    private boolean isShowing = false;
    private TextView mainView;
    private WindowManager wManager;
    private WindowManager.LayoutParams wmParams;

    public AdCoverWindow(Context ctx) {
        this.mainView = new TextView(ctx);
        this.mainView.setBackgroundColor(Color.parseColor("#0e74de"));
        this.mainView.setText("Please wait");
        this.mainView.setTextColor(-1);
        this.mainView.setPadding(5, 5, 5, 5);
        this.mainView.setTextSize(2, 20.0f);
        this.wManager = (WindowManager) ctx.getSystemService("window");
        this.wmParams = new WindowManager.LayoutParams();
        this.wmParams.type = 2002;
        this.wmParams.format = 1;
        this.wmParams.width = -2;
        this.wmParams.height = -2;
        this.wmParams.gravity = 51;
        this.wmParams.flags = 8;
    }

    public void show() {
        if (!this.isShowing) {
            this.wManager.addView(this.mainView, this.wmParams);
            this.isShowing = true;
        }
    }

    public void dismiss() {
        if (this.isShowing) {
            this.wManager.removeView(this.mainView);
            this.isShowing = false;
        }
    }

    public boolean isShowing() {
        return this.isShowing;
    }

    public void setMessage(String text) {
        this.mainView.setText(text);
    }
}
