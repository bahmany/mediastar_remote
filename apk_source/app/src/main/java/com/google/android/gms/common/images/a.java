package com.google.android.gms.common.images;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.internal.iw;
import com.google.android.gms.internal.ix;
import com.google.android.gms.internal.iy;
import com.google.android.gms.internal.iz;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public abstract class a {
    final C0004a KA;
    protected int KC;
    protected ImageManager.OnImageLoadedListener KD;
    protected int KG;
    protected int KB = 0;
    private boolean KE = true;
    private boolean KF = false;

    /* renamed from: com.google.android.gms.common.images.a$a, reason: collision with other inner class name */
    static final class C0004a {
        public final Uri uri;

        public C0004a(Uri uri) {
            this.uri = uri;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof C0004a)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            return m.equal(((C0004a) obj).uri, this.uri);
        }

        public int hashCode() {
            return m.hashCode(this.uri);
        }
    }

    public static final class b extends a {
        private WeakReference<ImageView> KH;

        public b(ImageView imageView, int i) {
            super(null, i);
            com.google.android.gms.common.internal.a.f(imageView);
            this.KH = new WeakReference<>(imageView);
        }

        public b(ImageView imageView, Uri uri) {
            super(uri, 0);
            com.google.android.gms.common.internal.a.f(imageView);
            this.KH = new WeakReference<>(imageView);
        }

        private void a(ImageView imageView, Drawable drawable, boolean z, boolean z2, boolean z3) {
            boolean z4 = (z2 || z3) ? false : true;
            if (z4 && (imageView instanceof iy)) {
                int iGN = ((iy) imageView).gN();
                if (this.KC != 0 && iGN == this.KC) {
                    return;
                }
            }
            boolean zB = b(z, z2);
            Drawable drawableA = zB ? a(imageView.getDrawable(), drawable) : drawable;
            imageView.setImageDrawable(drawableA);
            if (imageView instanceof iy) {
                iy iyVar = (iy) imageView;
                iyVar.g(z3 ? this.KA.uri : null);
                iyVar.ay(z4 ? this.KC : 0);
            }
            if (zB) {
                ((iw) drawableA).startTransition(KeyInfo.KEYCODE_M);
            }
        }

        @Override // com.google.android.gms.common.images.a
        protected void a(Drawable drawable, boolean z, boolean z2, boolean z3) {
            ImageView imageView = this.KH.get();
            if (imageView != null) {
                a(imageView, drawable, z, z2, z3);
            }
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof b)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            ImageView imageView = this.KH.get();
            ImageView imageView2 = ((b) obj).KH.get();
            return (imageView2 == null || imageView == null || !m.equal(imageView2, imageView)) ? false : true;
        }

        public int hashCode() {
            return 0;
        }
    }

    public static final class c extends a {
        private WeakReference<ImageManager.OnImageLoadedListener> KI;

        public c(ImageManager.OnImageLoadedListener onImageLoadedListener, Uri uri) {
            super(uri, 0);
            com.google.android.gms.common.internal.a.f(onImageLoadedListener);
            this.KI = new WeakReference<>(onImageLoadedListener);
        }

        @Override // com.google.android.gms.common.images.a
        protected void a(Drawable drawable, boolean z, boolean z2, boolean z3) {
            ImageManager.OnImageLoadedListener onImageLoadedListener;
            if (z2 || (onImageLoadedListener = this.KI.get()) == null) {
                return;
            }
            onImageLoadedListener.onImageLoaded(this.KA.uri, drawable, z3);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof c)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            c cVar = (c) obj;
            ImageManager.OnImageLoadedListener onImageLoadedListener = this.KI.get();
            ImageManager.OnImageLoadedListener onImageLoadedListener2 = cVar.KI.get();
            return onImageLoadedListener2 != null && onImageLoadedListener != null && m.equal(onImageLoadedListener2, onImageLoadedListener) && m.equal(cVar.KA, this.KA);
        }

        public int hashCode() {
            return m.hashCode(this.KA);
        }
    }

    public a(Uri uri, int i) {
        this.KC = 0;
        this.KA = new C0004a(uri);
        this.KC = i;
    }

    private Drawable a(Context context, iz izVar, int i) throws Resources.NotFoundException {
        Resources resources = context.getResources();
        if (this.KG <= 0) {
            return resources.getDrawable(i);
        }
        iz.a aVar = new iz.a(i, this.KG);
        Drawable drawable = izVar.get(aVar);
        if (drawable != null) {
            return drawable;
        }
        Drawable drawable2 = resources.getDrawable(i);
        if ((this.KG & 1) != 0) {
            drawable2 = a(resources, drawable2);
        }
        izVar.put(aVar, drawable2);
        return drawable2;
    }

    protected Drawable a(Resources resources, Drawable drawable) {
        return ix.a(resources, drawable);
    }

    protected iw a(Drawable drawable, Drawable drawable2) {
        if (drawable == null) {
            drawable = null;
        } else if (drawable instanceof iw) {
            drawable = ((iw) drawable).gL();
        }
        return new iw(drawable, drawable2);
    }

    void a(Context context, Bitmap bitmap, boolean z) {
        com.google.android.gms.common.internal.a.f(bitmap);
        if ((this.KG & 1) != 0) {
            bitmap = ix.a(bitmap);
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        if (this.KD != null) {
            this.KD.onImageLoaded(this.KA.uri, bitmapDrawable, true);
        }
        a(bitmapDrawable, z, false, true);
    }

    void a(Context context, iz izVar) {
        a(this.KB != 0 ? a(context, izVar, this.KB) : null, false, true, false);
    }

    void a(Context context, iz izVar, boolean z) {
        Drawable drawableA = this.KC != 0 ? a(context, izVar, this.KC) : null;
        if (this.KD != null) {
            this.KD.onImageLoaded(this.KA.uri, drawableA, false);
        }
        a(drawableA, z, false, false);
    }

    protected abstract void a(Drawable drawable, boolean z, boolean z2, boolean z3);

    public void aw(int i) {
        this.KC = i;
    }

    protected boolean b(boolean z, boolean z2) {
        return this.KE && !z2 && (!z || this.KF);
    }
}
