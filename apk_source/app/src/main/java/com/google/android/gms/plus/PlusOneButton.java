package com.google.android.gms.plus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.q;
import com.google.android.gms.plus.internal.g;

/* loaded from: classes.dex */
public final class PlusOneButton extends FrameLayout {
    public static final int ANNOTATION_BUBBLE = 1;
    public static final int ANNOTATION_INLINE = 2;
    public static final int ANNOTATION_NONE = 0;
    public static final int DEFAULT_ACTIVITY_REQUEST_CODE = -1;
    public static final int SIZE_MEDIUM = 1;
    public static final int SIZE_SMALL = 0;
    public static final int SIZE_STANDARD = 3;
    public static final int SIZE_TALL = 2;
    private View ala;
    private int alb;
    private int alc;
    private OnPlusOneClickListener ald;
    private int mSize;
    private String uR;

    protected class DefaultOnPlusOneClickListener implements View.OnClickListener, OnPlusOneClickListener {
        private final OnPlusOneClickListener ale;

        public DefaultOnPlusOneClickListener(OnPlusOneClickListener proxy) {
            this.ale = proxy;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = (Intent) PlusOneButton.this.ala.getTag();
            if (this.ale != null) {
                this.ale.onPlusOneClick(intent);
            } else {
                onPlusOneClick(intent);
            }
        }

        @Override // com.google.android.gms.plus.PlusOneButton.OnPlusOneClickListener
        public void onPlusOneClick(Intent intent) {
            Context context = PlusOneButton.this.getContext();
            if (!(context instanceof Activity) || intent == null) {
                return;
            }
            ((Activity) context).startActivityForResult(intent, PlusOneButton.this.alc);
        }
    }

    public interface OnPlusOneClickListener {
        void onPlusOneClick(Intent intent);
    }

    public PlusOneButton(Context context) {
        this(context, null);
    }

    public PlusOneButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSize = getSize(context, attrs);
        this.alb = getAnnotation(context, attrs);
        this.alc = -1;
        G(getContext());
        if (isInEditMode()) {
        }
    }

    private void G(Context context) {
        if (this.ala != null) {
            removeView(this.ala);
        }
        this.ala = g.a(context, this.mSize, this.alb, this.uR, this.alc);
        setOnPlusOneClickListener(this.ald);
        addView(this.ala);
    }

    protected static int getAnnotation(Context context, AttributeSet attrs) throws Resources.NotFoundException {
        String strA = q.a("http://schemas.android.com/apk/lib/com.google.android.gms.plus", "annotation", context, attrs, true, false, "PlusOneButton");
        if ("INLINE".equalsIgnoreCase(strA)) {
            return 2;
        }
        return !"NONE".equalsIgnoreCase(strA) ? 1 : 0;
    }

    protected static int getSize(Context context, AttributeSet attrs) throws Resources.NotFoundException {
        String strA = q.a("http://schemas.android.com/apk/lib/com.google.android.gms.plus", "size", context, attrs, true, false, "PlusOneButton");
        if ("SMALL".equalsIgnoreCase(strA)) {
            return 0;
        }
        if ("MEDIUM".equalsIgnoreCase(strA)) {
            return 1;
        }
        return "TALL".equalsIgnoreCase(strA) ? 2 : 3;
    }

    public void initialize(String url, int activityRequestCode) {
        n.a(getContext() instanceof Activity, "To use this method, the PlusOneButton must be placed in an Activity. Use initialize(String, OnPlusOneClickListener).");
        this.uR = url;
        this.alc = activityRequestCode;
        G(getContext());
    }

    public void initialize(String url, OnPlusOneClickListener plusOneClickListener) {
        this.uR = url;
        this.alc = 0;
        G(getContext());
        setOnPlusOneClickListener(plusOneClickListener);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.ala.layout(0, 0, right - left, bottom - top);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View view = this.ala;
        measureChild(view, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    public void setAnnotation(int annotation) {
        this.alb = annotation;
        G(getContext());
    }

    public void setOnPlusOneClickListener(OnPlusOneClickListener listener) {
        this.ald = listener;
        this.ala.setOnClickListener(new DefaultOnPlusOneClickListener(listener));
    }

    public void setSize(int size) {
        this.mSize = size;
        G(getContext());
    }
}
