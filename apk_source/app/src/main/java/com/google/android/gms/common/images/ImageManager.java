package com.google.android.gms.common.images;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import com.google.android.gms.common.images.a;
import com.google.android.gms.internal.iz;
import com.google.android.gms.internal.ja;
import com.google.android.gms.internal.kc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public final class ImageManager {
    private static final Object Kl = new Object();
    private static HashSet<Uri> Km = new HashSet<>();
    private static ImageManager Kn;
    private static ImageManager Ko;
    private final b Kq;
    private final iz Kr;
    private final Map<com.google.android.gms.common.images.a, ImageReceiver> Ks;
    private final Map<Uri, ImageReceiver> Kt;
    private final Map<Uri, Long> Ku;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService Kp = Executors.newFixedThreadPool(4);

    private final class ImageReceiver extends ResultReceiver {
        private final ArrayList<com.google.android.gms.common.images.a> Kv;
        private final Uri mUri;

        ImageReceiver(Uri uri) {
            super(new Handler(Looper.getMainLooper()));
            this.mUri = uri;
            this.Kv = new ArrayList<>();
        }

        public void b(com.google.android.gms.common.images.a aVar) {
            com.google.android.gms.common.internal.a.aT("ImageReceiver.addImageRequest() must be called in the main thread");
            this.Kv.add(aVar);
        }

        public void c(com.google.android.gms.common.images.a aVar) {
            com.google.android.gms.common.internal.a.aT("ImageReceiver.removeImageRequest() must be called in the main thread");
            this.Kv.remove(aVar);
        }

        public void gK() {
            Intent intent = new Intent("com.google.android.gms.common.images.LOAD_IMAGE");
            intent.putExtra("com.google.android.gms.extras.uri", this.mUri);
            intent.putExtra("com.google.android.gms.extras.resultReceiver", this);
            intent.putExtra("com.google.android.gms.extras.priority", 3);
            ImageManager.this.mContext.sendBroadcast(intent);
        }

        @Override // android.os.ResultReceiver
        public void onReceiveResult(int resultCode, Bundle resultData) {
            ImageManager.this.Kp.execute(ImageManager.this.new c(this.mUri, (ParcelFileDescriptor) resultData.getParcelable("com.google.android.gms.extra.fileDescriptor")));
        }
    }

    public interface OnImageLoadedListener {
        void onImageLoaded(Uri uri, Drawable drawable, boolean z);
    }

    private static final class a {
        static int a(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }

    private static final class b extends ja<a.C0004a, Bitmap> {
        public b(Context context) {
            super(I(context));
        }

        private static int I(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            return (int) (((((context.getApplicationInfo().flags & 1048576) != 0) && kc.hB()) ? a.a(activityManager) : activityManager.getMemoryClass()) * 1048576 * 0.33f);
        }

        @Override // com.google.android.gms.internal.ja
        /* renamed from: a */
        public int sizeOf(a.C0004a c0004a, Bitmap bitmap) {
            return bitmap.getHeight() * bitmap.getRowBytes();
        }

        @Override // com.google.android.gms.internal.ja
        /* renamed from: a */
        public void entryRemoved(boolean z, a.C0004a c0004a, Bitmap bitmap, Bitmap bitmap2) {
            super.entryRemoved(z, c0004a, bitmap, bitmap2);
        }
    }

    private final class c implements Runnable {
        private final ParcelFileDescriptor Kx;
        private final Uri mUri;

        public c(Uri uri, ParcelFileDescriptor parcelFileDescriptor) {
            this.mUri = uri;
            this.Kx = parcelFileDescriptor;
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException, IOException {
            com.google.android.gms.common.internal.a.aU("LoadBitmapFromDiskRunnable can't be executed in the main thread");
            boolean z = false;
            Bitmap bitmapDecodeFileDescriptor = null;
            if (this.Kx != null) {
                try {
                    bitmapDecodeFileDescriptor = BitmapFactory.decodeFileDescriptor(this.Kx.getFileDescriptor());
                } catch (OutOfMemoryError e) {
                    Log.e("ImageManager", "OOM while loading bitmap for uri: " + this.mUri, e);
                    z = true;
                }
                try {
                    this.Kx.close();
                } catch (IOException e2) {
                    Log.e("ImageManager", "closed failed", e2);
                }
            }
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ImageManager.this.mHandler.post(ImageManager.this.new f(this.mUri, bitmapDecodeFileDescriptor, z, countDownLatch));
            try {
                countDownLatch.await();
            } catch (InterruptedException e3) {
                Log.w("ImageManager", "Latch interrupted while posting " + this.mUri);
            }
        }
    }

    private final class d implements Runnable {
        private final com.google.android.gms.common.images.a Ky;

        public d(com.google.android.gms.common.images.a aVar) {
            this.Ky = aVar;
        }

        @Override // java.lang.Runnable
        public void run() {
            com.google.android.gms.common.internal.a.aT("LoadImageRunnable must be executed on the main thread");
            ImageReceiver imageReceiver = (ImageReceiver) ImageManager.this.Ks.get(this.Ky);
            if (imageReceiver != null) {
                ImageManager.this.Ks.remove(this.Ky);
                imageReceiver.c(this.Ky);
            }
            a.C0004a c0004a = this.Ky.KA;
            if (c0004a.uri == null) {
                this.Ky.a(ImageManager.this.mContext, ImageManager.this.Kr, true);
                return;
            }
            Bitmap bitmapA = ImageManager.this.a(c0004a);
            if (bitmapA != null) {
                this.Ky.a(ImageManager.this.mContext, bitmapA, true);
                return;
            }
            Long l = (Long) ImageManager.this.Ku.get(c0004a.uri);
            if (l != null) {
                if (SystemClock.elapsedRealtime() - l.longValue() < 3600000) {
                    this.Ky.a(ImageManager.this.mContext, ImageManager.this.Kr, true);
                    return;
                }
                ImageManager.this.Ku.remove(c0004a.uri);
            }
            this.Ky.a(ImageManager.this.mContext, ImageManager.this.Kr);
            ImageReceiver imageReceiver2 = (ImageReceiver) ImageManager.this.Kt.get(c0004a.uri);
            if (imageReceiver2 == null) {
                imageReceiver2 = ImageManager.this.new ImageReceiver(c0004a.uri);
                ImageManager.this.Kt.put(c0004a.uri, imageReceiver2);
            }
            imageReceiver2.b(this.Ky);
            if (!(this.Ky instanceof a.c)) {
                ImageManager.this.Ks.put(this.Ky, imageReceiver2);
            }
            synchronized (ImageManager.Kl) {
                if (!ImageManager.Km.contains(c0004a.uri)) {
                    ImageManager.Km.add(c0004a.uri);
                    imageReceiver2.gK();
                }
            }
        }
    }

    private static final class e implements ComponentCallbacks2 {
        private final b Kq;

        public e(b bVar) {
            this.Kq = bVar;
        }

        @Override // android.content.ComponentCallbacks
        public void onConfigurationChanged(Configuration newConfig) {
        }

        @Override // android.content.ComponentCallbacks
        public void onLowMemory() {
            this.Kq.evictAll();
        }

        @Override // android.content.ComponentCallbacks2
        public void onTrimMemory(int level) {
            if (level >= 60) {
                this.Kq.evictAll();
            } else if (level >= 20) {
                this.Kq.trimToSize(this.Kq.size() / 2);
            }
        }
    }

    private final class f implements Runnable {
        private boolean Kz;
        private final Bitmap mBitmap;
        private final Uri mUri;
        private final CountDownLatch mg;

        public f(Uri uri, Bitmap bitmap, boolean z, CountDownLatch countDownLatch) {
            this.mUri = uri;
            this.mBitmap = bitmap;
            this.Kz = z;
            this.mg = countDownLatch;
        }

        private void a(ImageReceiver imageReceiver, boolean z) {
            ArrayList arrayList = imageReceiver.Kv;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                com.google.android.gms.common.images.a aVar = (com.google.android.gms.common.images.a) arrayList.get(i);
                if (z) {
                    aVar.a(ImageManager.this.mContext, this.mBitmap, false);
                } else {
                    ImageManager.this.Ku.put(this.mUri, Long.valueOf(SystemClock.elapsedRealtime()));
                    aVar.a(ImageManager.this.mContext, ImageManager.this.Kr, false);
                }
                if (!(aVar instanceof a.c)) {
                    ImageManager.this.Ks.remove(aVar);
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            com.google.android.gms.common.internal.a.aT("OnBitmapLoadedRunnable must be executed in the main thread");
            boolean z = this.mBitmap != null;
            if (ImageManager.this.Kq != null) {
                if (this.Kz) {
                    ImageManager.this.Kq.evictAll();
                    System.gc();
                    this.Kz = false;
                    ImageManager.this.mHandler.post(this);
                    return;
                }
                if (z) {
                    ImageManager.this.Kq.put(new a.C0004a(this.mUri), this.mBitmap);
                }
            }
            ImageReceiver imageReceiver = (ImageReceiver) ImageManager.this.Kt.remove(this.mUri);
            if (imageReceiver != null) {
                a(imageReceiver, z);
            }
            this.mg.countDown();
            synchronized (ImageManager.Kl) {
                ImageManager.Km.remove(this.mUri);
            }
        }
    }

    private ImageManager(Context context, boolean withMemoryCache) {
        this.mContext = context.getApplicationContext();
        if (withMemoryCache) {
            this.Kq = new b(this.mContext);
            if (kc.hE()) {
                gH();
            }
        } else {
            this.Kq = null;
        }
        this.Kr = new iz();
        this.Ks = new HashMap();
        this.Kt = new HashMap();
        this.Ku = new HashMap();
    }

    public Bitmap a(a.C0004a c0004a) {
        if (this.Kq == null) {
            return null;
        }
        return this.Kq.get(c0004a);
    }

    public static ImageManager c(Context context, boolean z) {
        if (z) {
            if (Ko == null) {
                Ko = new ImageManager(context, true);
            }
            return Ko;
        }
        if (Kn == null) {
            Kn = new ImageManager(context, false);
        }
        return Kn;
    }

    public static ImageManager create(Context context) {
        return c(context, false);
    }

    private void gH() {
        this.mContext.registerComponentCallbacks(new e(this.Kq));
    }

    public void a(com.google.android.gms.common.images.a aVar) {
        com.google.android.gms.common.internal.a.aT("ImageManager.loadImage() must be called in the main thread");
        new d(aVar).run();
    }

    public void loadImage(ImageView imageView, int resId) {
        a(new a.b(imageView, resId));
    }

    public void loadImage(ImageView imageView, Uri uri) {
        a(new a.b(imageView, uri));
    }

    public void loadImage(ImageView imageView, Uri uri, int defaultResId) {
        a.b bVar = new a.b(imageView, uri);
        bVar.aw(defaultResId);
        a(bVar);
    }

    public void loadImage(OnImageLoadedListener listener, Uri uri) {
        a(new a.c(listener, uri));
    }

    public void loadImage(OnImageLoadedListener listener, Uri uri, int defaultResId) {
        a.c cVar = new a.c(listener, uri);
        cVar.aw(defaultResId);
        a(cVar);
    }
}
