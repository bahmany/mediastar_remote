package com.github.mikephil.charting.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import com.github.mikephil.charting.animation.Easing;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public class ChartAnimator {
    private ValueAnimator.AnimatorUpdateListener mListener;
    protected float mPhaseY = 1.0f;
    protected float mPhaseX = 1.0f;

    public ChartAnimator() {
    }

    public ChartAnimator(ValueAnimator.AnimatorUpdateListener listener) {
        this.mListener = listener;
    }

    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easingX, EasingFunction easingY) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0.0f, 1.0f);
            animatorY.setInterpolator(easingY);
            animatorY.setDuration(durationMillisY);
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0.0f, 1.0f);
            animatorX.setInterpolator(easingX);
            animatorX.setDuration(durationMillisX);
            if (durationMillisX > durationMillisY) {
                animatorX.addUpdateListener(this.mListener);
            } else {
                animatorY.addUpdateListener(this.mListener);
            }
            animatorX.start();
            animatorY.start();
        }
    }

    public void animateX(int durationMillis, EasingFunction easing) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0.0f, 1.0f);
            animatorX.setInterpolator(easing);
            animatorX.setDuration(durationMillis);
            animatorX.addUpdateListener(this.mListener);
            animatorX.start();
        }
    }

    public void animateY(int durationMillis, EasingFunction easing) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0.0f, 1.0f);
            animatorY.setInterpolator(easing);
            animatorY.setDuration(durationMillis);
            animatorY.addUpdateListener(this.mListener);
            animatorY.start();
        }
    }

    public void animateXY(int durationMillisX, int durationMillisY, Easing.EasingOption easingX, Easing.EasingOption easingY) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0.0f, 1.0f);
            animatorY.setInterpolator(Easing.getEasingFunctionFromOption(easingY));
            animatorY.setDuration(durationMillisY);
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0.0f, 1.0f);
            animatorX.setInterpolator(Easing.getEasingFunctionFromOption(easingX));
            animatorX.setDuration(durationMillisX);
            if (durationMillisX > durationMillisY) {
                animatorX.addUpdateListener(this.mListener);
            } else {
                animatorY.addUpdateListener(this.mListener);
            }
            animatorX.start();
            animatorY.start();
        }
    }

    public void animateX(int durationMillis, Easing.EasingOption easing) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0.0f, 1.0f);
            animatorX.setInterpolator(Easing.getEasingFunctionFromOption(easing));
            animatorX.setDuration(durationMillis);
            animatorX.addUpdateListener(this.mListener);
            animatorX.start();
        }
    }

    public void animateY(int durationMillis, Easing.EasingOption easing) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0.0f, 1.0f);
            animatorY.setInterpolator(Easing.getEasingFunctionFromOption(easing));
            animatorY.setDuration(durationMillis);
            animatorY.addUpdateListener(this.mListener);
            animatorY.start();
        }
    }

    public void animateXY(int durationMillisX, int durationMillisY) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0.0f, 1.0f);
            animatorY.setDuration(durationMillisY);
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0.0f, 1.0f);
            animatorX.setDuration(durationMillisX);
            if (durationMillisX > durationMillisY) {
                animatorX.addUpdateListener(this.mListener);
            } else {
                animatorY.addUpdateListener(this.mListener);
            }
            animatorX.start();
            animatorY.start();
        }
    }

    public void animateX(int durationMillis) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0.0f, 1.0f);
            animatorX.setDuration(durationMillis);
            animatorX.addUpdateListener(this.mListener);
            animatorX.start();
        }
    }

    public void animateY(int durationMillis) {
        if (Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0.0f, 1.0f);
            animatorY.setDuration(durationMillis);
            animatorY.addUpdateListener(this.mListener);
            animatorY.start();
        }
    }

    public float getPhaseY() {
        return this.mPhaseY;
    }

    public void setPhaseY(float phase) {
        this.mPhaseY = phase;
    }

    public float getPhaseX() {
        return this.mPhaseX;
    }

    public void setPhaseX(float phase) {
        this.mPhaseX = phase;
    }
}
