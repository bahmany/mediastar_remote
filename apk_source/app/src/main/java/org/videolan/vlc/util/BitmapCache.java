package org.videolan.vlc.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;

/* loaded from: classes.dex */
public class BitmapCache {
    private static final boolean LOG_ENABLED = false;
    public static final String TAG = "VLC/BitmapCache";
    private static BitmapCache mInstance;
    private final LruCache<String, Bitmap> mMemCache;

    public static BitmapCache getInstance() {
        if (mInstance == null) {
            mInstance = new BitmapCache();
        }
        return mInstance;
    }

    private BitmapCache() {
        Context context = VLCInstance.getAppContext();
        int memClass = ((ActivityManager) context.getSystemService("activity")).getMemoryClass();
        int cacheSize = (1048576 * memClass) / 5;
        Log.d(TAG, "LRUCache size sets to " + cacheSize);
        this.mMemCache = new LruCache<String, Bitmap>(cacheSize) { // from class: org.videolan.vlc.util.BitmapCache.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.support.v4.util.LruCache
            public int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public Bitmap getBitmapFromMemCache(String key) {
        Bitmap b = this.mMemCache.get(key);
        if (b != null && b.isRecycled()) {
            this.mMemCache.remove(key);
            return null;
        }
        return b;
    }

    public void addBitmapToMemCache(String key, Bitmap bitmap) {
        if (key != null && bitmap != null && getBitmapFromMemCache(key) == null) {
            this.mMemCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(int resId) {
        return getBitmapFromMemCache("res:" + resId);
    }

    private void addBitmapToMemCache(int resId, Bitmap bitmap) {
        addBitmapToMemCache("res:" + resId, bitmap);
    }

    public void clear() {
        this.mMemCache.evictAll();
    }

    public static Bitmap GetFromResource(View v, int resId) {
        BitmapCache cache = getInstance();
        Bitmap bitmap = cache.getBitmapFromMemCache(resId);
        if (bitmap == null) {
            Bitmap bitmap2 = BitmapFactory.decodeResource(v.getResources(), resId);
            cache.addBitmapToMemCache(resId, bitmap2);
            return bitmap2;
        }
        return bitmap;
    }
}
