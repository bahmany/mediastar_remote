package com.hisilicon.dlna.dmc.utility;

import android.graphics.Bitmap;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/* loaded from: classes.dex */
public class GlobalCache {
    private static final int CACHE_CAPACITY_OF_FOCUS = 50;
    private static volatile Map<String, SoftReference<Bitmap>> mImageCache = Collections.synchronizedMap(new HashMap());
    private static volatile LinkedList<String> mImageUrlList = new LinkedList<>();

    public static Bitmap getBitmap(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().equals("")) {
            return null;
        }
        Bitmap bitmap = get(imageUrl);
        if (bitmap != null && !bitmap.isRecycled()) {
            updateUrlList(imageUrl);
            return bitmap;
        }
        return bitmap;
    }

    private static Bitmap get(String imageUrl) {
        Bitmap bitmap;
        SoftReference<Bitmap> softReference = mImageCache.get(imageUrl);
        if (softReference == null || (bitmap = softReference.get()) == null || bitmap.isRecycled()) {
            return null;
        }
        return bitmap;
    }

    private static void updateUrlList(String imageUrl) {
        synchronized (mImageUrlList) {
            if (mImageUrlList.contains(imageUrl)) {
                mImageUrlList.remove(imageUrl);
            }
            mImageUrlList.addLast(imageUrl);
        }
    }

    public static void putToCache(String imageUrl, Bitmap bitmap) {
        if (imageUrl != null && !imageUrl.trim().equals("")) {
            updateUrlList(imageUrl);
            putToImageCache(imageUrl, bitmap);
            recycleBitamps();
        }
    }

    private static void putToImageCache(String imageUrl, Bitmap bitmap) {
        synchronized (mImageCache) {
            mImageCache.put(imageUrl, new SoftReference<>(bitmap));
        }
    }

    private static void recycleBitamps() {
        int size = mImageUrlList.size();
        if (size > 50) {
            for (int i = 0; i < size - 50; i++) {
                recycleBitmap();
            }
            System.gc();
        }
    }

    private static void recycleBitmap() {
        Bitmap bitmap;
        String imageUrl = getFirstUrl();
        SoftReference<Bitmap> softRef = removeFromImageCache(imageUrl);
        removeFirstUrl();
        if (softRef != null && (bitmap = softRef.get()) != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private static String getFirstUrl() {
        return mImageUrlList.getFirst();
    }

    private static String removeFirstUrl() {
        String strRemoveFirst;
        synchronized (mImageUrlList) {
            strRemoveFirst = mImageUrlList.removeFirst();
        }
        return strRemoveFirst;
    }

    private static SoftReference<Bitmap> removeFromImageCache(String imageUrl) {
        SoftReference<Bitmap> softReferenceRemove;
        synchronized (mImageCache) {
            softReferenceRemove = mImageCache.remove(imageUrl);
        }
        return softReferenceRemove;
    }

    public static synchronized void clear() {
        mImageCache.clear();
        mImageUrlList.clear();
        System.gc();
    }
}
