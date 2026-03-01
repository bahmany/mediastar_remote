package com.hisilicon.dlna.dmc.utility;

import android.graphics.Bitmap;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/* loaded from: classes.dex */
public class Cache {
    private int CACHE_CAPACITY_OF_FOCUS;
    private Map<String, SoftReference<Bitmap>> mImageCache;
    private LinkedList<String> mImageUrlList;

    public Cache(int CACHE_CAPACITY) {
        this.CACHE_CAPACITY_OF_FOCUS = 20;
        this.mImageUrlList = null;
        this.mImageCache = null;
        this.CACHE_CAPACITY_OF_FOCUS = CACHE_CAPACITY;
        this.mImageUrlList = new LinkedList<>();
        this.mImageCache = Collections.synchronizedMap(new HashMap(CACHE_CAPACITY));
    }

    public Bitmap getBitmap(String imageUrl) {
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

    private Bitmap get(String imageUrl) {
        Bitmap bitmap;
        SoftReference<Bitmap> softReference = this.mImageCache.get(imageUrl);
        if (softReference == null || (bitmap = softReference.get()) == null || bitmap.isRecycled()) {
            return null;
        }
        return bitmap;
    }

    private void updateUrlList(String imageUrl) {
        synchronized (this.mImageUrlList) {
            if (this.mImageUrlList.contains(imageUrl)) {
                this.mImageUrlList.remove(imageUrl);
            }
            this.mImageUrlList.addLast(imageUrl);
        }
    }

    public void putToCache(String imageUrl, Bitmap bitmap) {
        if (imageUrl != null && !imageUrl.trim().equals("")) {
            updateUrlList(imageUrl);
            putToImageCache(imageUrl, bitmap);
            recycleBitamps();
        }
    }

    private void putToImageCache(String imageUrl, Bitmap bitmap) {
        synchronized (this.mImageCache) {
            this.mImageCache.put(imageUrl, new SoftReference<>(bitmap));
        }
    }

    private void recycleBitamps() {
        int size = this.mImageUrlList.size();
        if (size > this.CACHE_CAPACITY_OF_FOCUS) {
            for (int i = 0; i < size - this.CACHE_CAPACITY_OF_FOCUS; i++) {
                recycleFirst();
            }
            System.gc();
        }
    }

    private void recycleFirst() {
        Bitmap bitmap;
        String imageUrl = getFirstUrl();
        SoftReference<Bitmap> softRef = removeFromImageCache(imageUrl);
        removeFirstUrl();
        if (softRef != null && (bitmap = softRef.get()) != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private String getFirstUrl() {
        return this.mImageUrlList.getFirst();
    }

    private String removeFirstUrl() {
        String strRemoveFirst;
        synchronized (this.mImageUrlList) {
            strRemoveFirst = this.mImageUrlList.removeFirst();
        }
        return strRemoveFirst;
    }

    private SoftReference<Bitmap> removeFromImageCache(String imageUrl) {
        SoftReference<Bitmap> softReferenceRemove;
        synchronized (this.mImageCache) {
            softReferenceRemove = this.mImageCache.remove(imageUrl);
        }
        return softReferenceRemove;
    }

    public synchronized void clear() {
        this.mImageCache.clear();
        this.mImageUrlList.clear();
        System.gc();
    }
}
