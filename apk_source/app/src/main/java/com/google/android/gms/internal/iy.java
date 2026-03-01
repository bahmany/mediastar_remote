package com.google.android.gms.internal;

import android.graphics.Canvas;
import android.graphics.Path;
import android.net.Uri;
import android.widget.ImageView;

/* loaded from: classes.dex */
public final class iy extends ImageView {
    private Uri Lc;
    private int Ld;
    private int Le;
    private a Lf;
    private int Lg;
    private float Lh;

    public interface a {
        Path g(int i, int i2);
    }

    public void ay(int i) {
        this.Ld = i;
    }

    public void g(Uri uri) {
        this.Lc = uri;
    }

    public int gN() {
        return this.Ld;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.Lf != null) {
            canvas.clipPath(this.Lf.g(getWidth(), getHeight()));
        }
        super.onDraw(canvas);
        if (this.Le != 0) {
            canvas.drawColor(this.Le);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth;
        int measuredHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        switch (this.Lg) {
            case 1:
                measuredHeight = getMeasuredHeight();
                measuredWidth = (int) (measuredHeight * this.Lh);
                break;
            case 2:
                measuredWidth = getMeasuredWidth();
                measuredHeight = (int) (measuredWidth / this.Lh);
                break;
            default:
                return;
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }
}
