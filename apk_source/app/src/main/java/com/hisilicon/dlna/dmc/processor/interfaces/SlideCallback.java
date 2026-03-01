package com.hisilicon.dlna.dmc.processor.interfaces;

import android.view.MotionEvent;
import android.view.View;

/* loaded from: classes.dex */
public interface SlideCallback {
    void deltaY(int i);

    void onTap();

    boolean onTouch(View view, MotionEvent motionEvent);

    void slideDownToUp();

    void slideUpToDown();
}
