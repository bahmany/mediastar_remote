package com.google.android.gms.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.o;
import com.google.android.gms.common.internal.p;
import com.google.android.gms.dynamic.g;

/* loaded from: classes.dex */
public final class SignInButton extends FrameLayout implements View.OnClickListener {
    public static final int COLOR_DARK = 0;
    public static final int COLOR_LIGHT = 1;
    public static final int SIZE_ICON_ONLY = 2;
    public static final int SIZE_STANDARD = 0;
    public static final int SIZE_WIDE = 1;
    private View Ih;
    private View.OnClickListener Ii;
    private int mColor;
    private int mSize;

    public SignInButton(Context context) {
        this(context, null);
    }

    public SignInButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignInButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.Ii = null;
        setStyle(0, 0);
    }

    private void G(Context context) {
        if (this.Ih != null) {
            removeView(this.Ih);
        }
        try {
            this.Ih = o.b(context, this.mSize, this.mColor);
        } catch (g.a e) {
            Log.w("SignInButton", "Sign in button not found, using placeholder instead");
            this.Ih = a(context, this.mSize, this.mColor);
        }
        addView(this.Ih);
        this.Ih.setEnabled(isEnabled());
        this.Ih.setOnClickListener(this);
    }

    private static Button a(Context context, int i, int i2) {
        p pVar = new p(context);
        pVar.a(context.getResources(), i, i2);
        return pVar;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.Ii == null || view != this.Ih) {
            return;
        }
        this.Ii.onClick(this);
    }

    public void setColorScheme(int colorScheme) {
        setStyle(this.mSize, colorScheme);
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.Ih.setEnabled(enabled);
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener listener) {
        this.Ii = listener;
        if (this.Ih != null) {
            this.Ih.setOnClickListener(this);
        }
    }

    public void setSize(int buttonSize) {
        setStyle(buttonSize, this.mColor);
    }

    public void setStyle(int buttonSize, int colorScheme) {
        n.a(buttonSize >= 0 && buttonSize < 3, "Unknown button size %d", Integer.valueOf(buttonSize));
        n.a(colorScheme >= 0 && colorScheme < 2, "Unknown color scheme %s", Integer.valueOf(colorScheme));
        this.mSize = buttonSize;
        this.mColor = colorScheme;
        G(getContext());
    }
}
