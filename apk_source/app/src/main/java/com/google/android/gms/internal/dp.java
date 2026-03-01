package com.google.android.gms.internal;

import android.R;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

@ez
/* loaded from: classes.dex */
public final class dp extends FrameLayout implements View.OnClickListener {
    private final Activity nr;
    private final ImageButton sg;

    public dp(Activity activity, int i) {
        super(activity);
        this.nr = activity;
        setOnClickListener(this);
        this.sg = new ImageButton(activity);
        this.sg.setImageResource(R.drawable.btn_dialog);
        this.sg.setBackgroundColor(0);
        this.sg.setOnClickListener(this);
        this.sg.setPadding(0, 0, 0, 0);
        this.sg.setContentDescription("Interstitial close button");
        int iA = gr.a(activity, i);
        addView(this.sg, new FrameLayout.LayoutParams(iA, iA, 17));
    }

    public void o(boolean z) {
        this.sg.setVisibility(z ? 4 : 0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.nr.finish();
    }
}
