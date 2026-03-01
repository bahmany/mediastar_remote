package com.google.android.gms.internal;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/* loaded from: classes.dex */
public final class iw extends Drawable implements Drawable.Callback {
    private boolean KE;
    private int KK;
    private long KL;
    private int KM;
    private int KN;
    private int KO;
    private int KP;
    private boolean KQ;
    private b KR;
    private Drawable KS;
    private Drawable KT;
    private boolean KU;
    private boolean KV;
    private boolean KW;
    private int KX;
    private int mFrom;

    private static final class a extends Drawable {
        private static final a KY = new a();
        private static final C0065a KZ = new C0065a();

        /* renamed from: com.google.android.gms.internal.iw$a$a, reason: collision with other inner class name */
        private static final class C0065a extends Drawable.ConstantState {
            private C0065a() {
            }

            @Override // android.graphics.drawable.Drawable.ConstantState
            public int getChangingConfigurations() {
                return 0;
            }

            @Override // android.graphics.drawable.Drawable.ConstantState
            public Drawable newDrawable() {
                return a.KY;
            }
        }

        private a() {
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
        }

        @Override // android.graphics.drawable.Drawable
        public Drawable.ConstantState getConstantState() {
            return KZ;
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter cf) {
        }
    }

    static final class b extends Drawable.ConstantState {
        int La;
        int Lb;

        b(b bVar) {
            if (bVar != null) {
                this.La = bVar.La;
                this.Lb = bVar.Lb;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.La;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new iw(this);
        }
    }

    public iw(Drawable drawable, Drawable drawable2) {
        this(null);
        drawable = drawable == null ? a.KY : drawable;
        this.KS = drawable;
        drawable.setCallback(this);
        this.KR.Lb |= drawable.getChangingConfigurations();
        drawable2 = drawable2 == null ? a.KY : drawable2;
        this.KT = drawable2;
        drawable2.setCallback(this);
        this.KR.Lb |= drawable2.getChangingConfigurations();
    }

    iw(b bVar) {
        this.KK = 0;
        this.KN = 255;
        this.KP = 0;
        this.KE = true;
        this.KR = new b(bVar);
    }

    public boolean canConstantState() {
        if (!this.KU) {
            this.KV = (this.KS.getConstantState() == null || this.KT.getConstantState() == null) ? false : true;
            this.KU = true;
        }
        return this.KV;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        boolean z = false;
        switch (this.KK) {
            case 1:
                this.KL = SystemClock.uptimeMillis();
                this.KK = 2;
                break;
            case 2:
                if (this.KL >= 0) {
                    float fUptimeMillis = (SystemClock.uptimeMillis() - this.KL) / this.KO;
                    z = fUptimeMillis >= 1.0f;
                    if (z) {
                        this.KK = 0;
                    }
                    this.KP = (int) ((Math.min(fUptimeMillis, 1.0f) * (this.KM - this.mFrom)) + this.mFrom);
                }
            default:
                z = z;
                break;
        }
        int i = this.KP;
        boolean z2 = this.KE;
        Drawable drawable = this.KS;
        Drawable drawable2 = this.KT;
        if (z) {
            if (!z2 || i == 0) {
                drawable.draw(canvas);
            }
            if (i == this.KN) {
                drawable2.setAlpha(this.KN);
                drawable2.draw(canvas);
                return;
            }
            return;
        }
        if (z2) {
            drawable.setAlpha(this.KN - i);
        }
        drawable.draw(canvas);
        if (z2) {
            drawable.setAlpha(this.KN);
        }
        if (i > 0) {
            drawable2.setAlpha(i);
            drawable2.draw(canvas);
            drawable2.setAlpha(this.KN);
        }
        invalidateSelf();
    }

    public Drawable gL() {
        return this.KT;
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.KR.La | this.KR.Lb;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (!canConstantState()) {
            return null;
        }
        this.KR.La = getChangingConfigurations();
        return this.KR;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return Math.max(this.KS.getIntrinsicHeight(), this.KT.getIntrinsicHeight());
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return Math.max(this.KS.getIntrinsicWidth(), this.KT.getIntrinsicWidth());
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        if (!this.KW) {
            this.KX = Drawable.resolveOpacity(this.KS.getOpacity(), this.KT.getOpacity());
            this.KW = true;
        }
        return this.KX;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        Drawable.Callback callback;
        if (!kc.hB() || (callback = getCallback()) == null) {
            return;
        }
        callback.invalidateDrawable(this);
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.KQ && super.mutate() == this) {
            if (!canConstantState()) {
                throw new IllegalStateException("One or more children of this LayerDrawable does not have constant state; this drawable cannot be mutated.");
            }
            this.KS.mutate();
            this.KT.mutate();
            this.KQ = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        this.KS.setBounds(bounds);
        this.KT.setBounds(bounds);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        Drawable.Callback callback;
        if (!kc.hB() || (callback = getCallback()) == null) {
            return;
        }
        callback.scheduleDrawable(this, what, when);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (this.KP == this.KN) {
            this.KP = alpha;
        }
        this.KN = alpha;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.KS.setColorFilter(cf);
        this.KT.setColorFilter(cf);
    }

    public void startTransition(int durationMillis) {
        this.mFrom = 0;
        this.KM = this.KN;
        this.KP = 0;
        this.KO = durationMillis;
        this.KK = 1;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        Drawable.Callback callback;
        if (!kc.hB() || (callback = getCallback()) == null) {
            return;
        }
        callback.unscheduleDrawable(this, what);
    }
}
