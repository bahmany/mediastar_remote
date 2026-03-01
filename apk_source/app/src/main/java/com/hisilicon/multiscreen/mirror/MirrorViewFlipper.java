package com.hisilicon.multiscreen.mirror;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/* loaded from: classes.dex */
public class MirrorViewFlipper extends ViewFlipper {
    public MirrorViewFlipper(Context context) {
        super(context);
    }

    public MirrorViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.ViewFlipper, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
            stopFlipping();
        }
    }
}
