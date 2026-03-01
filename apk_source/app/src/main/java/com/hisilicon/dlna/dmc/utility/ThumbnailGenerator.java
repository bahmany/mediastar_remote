package com.hisilicon.dlna.dmc.utility;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.media.TransportMediator;
import com.hisilicon.dlna.dmc.processor.interfaces.ImageCallback;
import com.hisilicon.multiscreen.mybox.HiMultiscreen;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class ThumbnailGenerator {
    private static ThumbnailGenerator instance;
    private ThreadPoolExecutor threadPool;
    private Object lock = new Object();
    private volatile boolean mAllowLoad = true;
    private volatile boolean firstLoad = true;
    private volatile int mStartLoadLimit = 0;
    private volatile int mStopLoadLimit = 0;
    final Handler handler = new Handler();

    public interface OnImageLoadListener {
        void onError(Integer num);

        void onImageLoad(Integer num, Bitmap bitmap);
    }

    private ThumbnailGenerator() {
        this.threadPool = null;
        this.threadPool = new ThreadPoolExecutor(4, 10, 3L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public static ThumbnailGenerator getInstance() {
        if (instance == null) {
            instance = new ThumbnailGenerator();
        }
        return instance;
    }

    public void addThread(Thread thread) {
        this.threadPool.execute(thread);
    }

    public Bitmap decodeFileBitmap(File file) throws MyException {
        try {
            Bitmap bitmap = GlobalCache.getBitmap(file.getAbsolutePath());
            if (bitmap != null && !bitmap.isRecycled()) {
                return bitmap;
            }
            Bitmap bitmap2 = decodeFileBitmap(file, Utility.getImageDimension(), Utility.getImageDimension());
            if (bitmap2 != null && !bitmap2.isRecycled()) {
                GlobalCache.putToCache(file.getAbsolutePath(), bitmap2);
                return bitmap2;
            }
            return null;
        } catch (Error ex) {
            System.gc();
            throw new MyException(ex);
        } catch (Exception ex2) {
            throw new MyException(ex2);
        }
    }

    public Bitmap decodeFileBitmap(File file, int width, int height) throws MyException {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDither = false;
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            opts.inJustDecodeBounds = false;
            int pictureW = opts.outWidth;
            int pictureH = opts.outHeight;
            int sampleSize = 1;
            int sampleSizeW = (int) Math.ceil(pictureW / width);
            int sampleSizeH = (int) Math.ceil(pictureH / height);
            if (sampleSizeW > 1 || sampleSizeH > 1) {
                sampleSize = Math.max(sampleSizeW, sampleSizeH);
            }
            opts.inSampleSize = sampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    return bitmap;
                }
            }
            return null;
        } catch (Error ex) {
            System.gc();
            throw new MyException(ex);
        } catch (Exception ex2) {
            throw new MyException(ex2);
        }
    }

    public static int getPhotoDegree(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt("Orientation", 1);
            switch (orientation) {
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float degress) throws MyException {
        try {
            int bmpW = bitmap.getWidth();
            int bmpH = bitmap.getHeight();
            Matrix mt = new Matrix();
            mt.setRotate(degress);
            return Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, mt, true);
        } catch (Error e) {
            throw new MyException(e);
        } catch (Exception e2) {
            throw new MyException(e2);
        }
    }

    private Bitmap decodeFileBitmap(InputStream stream) {
        if (stream != null) {
            return BitmapFactory.decodeStream(stream);
        }
        return null;
    }

    private Bitmap decodeFileBitmap1(byte[] bytes) throws MyException {
        if (bytes == null) {
            throw new MyException("bytes is null");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            options.inJustDecodeBounds = false;
            int pictureW = options.outWidth;
            int pictureH = options.outHeight;
            int sampleSize = 1;
            int sampleSizeW = (int) Math.ceil(pictureW / Utility.getImageDimension());
            int sampleSizeH = (int) Math.ceil(pictureH / Utility.getImageDimension());
            if (sampleSizeW > 1 || sampleSizeH > 1) {
                sampleSize = Math.max(sampleSizeW, sampleSizeH);
            }
            options.inSampleSize = sampleSize;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        } catch (Error ex) {
            ex.printStackTrace();
            System.gc();
            throw new MyException(ex);
        } catch (Exception ex2) {
            ex2.printStackTrace();
            throw new MyException(ex2);
        }
    }

    public Bitmap returnBitMap(final String strImagLink, final ImageCallback callback) {
        if (strImagLink == null || !strImagLink.endsWith("")) {
            return null;
        }
        Bitmap bitmap = GlobalCache.getBitmap(strImagLink);
        if (bitmap == null || bitmap.isRecycled()) {
            Handler handler = new Handler() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.1
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        callback.imageLoaded((Bitmap) msg.obj, strImagLink);
                    } else {
                        callback.imageLoaded(null, null);
                    }
                }
            };
            this.threadPool.execute(new getImagThread(strImagLink, handler));
            return null;
        }
        return bitmap;
    }

    class getImagThread implements Runnable {
        private Handler handler;
        private String strImagLink;

        public getImagThread(String url, Handler hd) {
            this.strImagLink = url;
            this.handler = hd;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                Bitmap bitmap = ThumbnailGenerator.this.getBitmapFromURL(this.strImagLink);
                if (bitmap != null && !bitmap.isRecycled()) {
                    this.handler.sendMessage(this.handler.obtainMessage(0, bitmap));
                } else {
                    this.handler.sendMessage(this.handler.obtainMessage(1, null));
                }
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
        if (startLoadLimit <= stopLoadLimit) {
            this.mStartLoadLimit = startLoadLimit;
            this.mStopLoadLimit = stopLoadLimit;
        }
    }

    public void restore() {
        this.mAllowLoad = true;
        this.firstLoad = true;
        this.mStartLoadLimit = 0;
        this.mStopLoadLimit = 0;
        unlock();
    }

    public void lock() {
        this.mAllowLoad = false;
        this.firstLoad = false;
    }

    public void unlock() {
        this.mAllowLoad = true;
        synchronized (this.lock) {
            this.lock.notifyAll();
        }
    }

    public void returnImage(final int position, final String imageUrl, final ImageCallback imageCallback) {
        if (imageUrl != null && !"".equals(imageUrl)) {
            this.threadPool.execute(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.2
                @Override // java.lang.Runnable
                public void run() {
                    if (!ThumbnailGenerator.this.mAllowLoad) {
                        synchronized (ThumbnailGenerator.this.lock) {
                            try {
                                ThumbnailGenerator.this.lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (ThumbnailGenerator.this.mAllowLoad && ThumbnailGenerator.this.firstLoad) {
                        ThumbnailGenerator.this.loadImage(position, imageUrl, imageCallback);
                    }
                    if (!ThumbnailGenerator.this.mAllowLoad || position < ThumbnailGenerator.this.mStartLoadLimit || position > ThumbnailGenerator.this.mStopLoadLimit) {
                        return;
                    }
                    ThumbnailGenerator.this.loadImage(position, imageUrl, imageCallback);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadImage(int position, String imageUrl, ImageCallback imageCallback) {
        Bitmap bitmap = GlobalCache.getBitmap(imageUrl);
        if (bitmap != null && !bitmap.isRecycled()) {
            handleBitmap(Integer.valueOf(position), imageUrl, bitmap, imageCallback);
            return;
        }
        try {
            Bitmap bitmapFromUrl = getBitmapFromURL(imageUrl);
            handleBitmap(Integer.valueOf(position), imageUrl, bitmapFromUrl, imageCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromURL(String imageUrl) throws MyException, IOException {
        if (imageUrl == null || "".equals(imageUrl)) {
            return null;
        }
        try {
            InputStream inputStream = new URL(imageUrl).openStream();
            Bitmap bitmap = decodeFileBitmap(inputStream);
            inputStream.close();
            if (bitmap == null || bitmap.isRecycled()) {
                return null;
            }
            GlobalCache.putToCache(imageUrl, bitmap);
            return bitmap;
        } catch (Error e) {
            throw new MyException(e);
        } catch (MalformedURLException e2) {
            throw new MyException(e2);
        } catch (IOException e3) {
            throw new MyException(e3);
        } catch (Exception e4) {
            throw new MyException(e4);
        }
    }

    public void returnThumbImage(final int position, final String imageUrl, final ImageCallback imageCallback) {
        if (imageUrl != null && !"".equals(imageUrl)) {
            this.threadPool.execute(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.3
                @Override // java.lang.Runnable
                public void run() throws Throwable {
                    if (!ThumbnailGenerator.this.mAllowLoad) {
                        synchronized (ThumbnailGenerator.this.lock) {
                            try {
                                ThumbnailGenerator.this.lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (ThumbnailGenerator.this.mAllowLoad && ThumbnailGenerator.this.firstLoad) {
                        ThumbnailGenerator.this.loadImage(position, imageUrl, TransportMediator.KEYCODE_MEDIA_RECORD, TransportMediator.KEYCODE_MEDIA_RECORD, imageCallback);
                    }
                    if (!ThumbnailGenerator.this.mAllowLoad || position < ThumbnailGenerator.this.mStartLoadLimit || position > ThumbnailGenerator.this.mStopLoadLimit) {
                        return;
                    }
                    ThumbnailGenerator.this.loadImage(position, imageUrl, TransportMediator.KEYCODE_MEDIA_RECORD, TransportMediator.KEYCODE_MEDIA_RECORD, imageCallback);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadImage(int position, String imageUrl, int width, int height, ImageCallback imageCallback) throws Throwable {
        Bitmap bitmap = GlobalCache.getBitmap(imageUrl);
        if (bitmap != null && !bitmap.isRecycled()) {
            handleBitmap(Integer.valueOf(position), imageUrl, bitmap, imageCallback);
            return;
        }
        try {
            Bitmap bitmapFromUrl = getBitmapFromURL(imageUrl, width, height);
            if (bitmap != null && !bitmap.isRecycled()) {
                GlobalCache.putToCache(imageUrl, bitmap);
            }
            handleBitmap(Integer.valueOf(position), imageUrl, bitmapFromUrl, imageCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromURL(String imageUrl, int width, int height) throws Throwable {
        if (imageUrl == null || "".equals(imageUrl)) {
            return null;
        }
        try {
            InputStream inputStream = new URL(imageUrl).openStream();
            File tmp = new File(String.valueOf(Utility.CACHE_ROOT) + File.separator + imageUrl.lastIndexOf(ServiceReference.DELIMITER));
            writeToFile(inputStream, tmp);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            Bitmap bitmap = decodeFileBitmap(tmp, width, height);
            tmp.delete();
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    return bitmap;
                }
            }
            return null;
        } catch (Error e2) {
            System.gc();
            throw new MyException(e2);
        } catch (Exception e3) {
            throw new MyException(e3);
        }
    }

    public void writeToFile(InputStream inputstream, File file) throws Throwable {
        BufferedOutputStream outputBuffer;
        if (inputstream == null) {
            throw new MyException("stream is null");
        }
        BufferedInputStream inputBuffer = null;
        BufferedOutputStream outputBuffer2 = null;
        try {
            try {
                BufferedInputStream inputBuffer2 = new BufferedInputStream(inputstream);
                try {
                    if (file.exists()) {
                        file.delete();
                        file.createNewFile();
                    } else {
                        file.createNewFile();
                    }
                    outputBuffer = new BufferedOutputStream(new FileOutputStream(file));
                } catch (Error e) {
                    err = e;
                } catch (Exception e2) {
                    ex = e2;
                } catch (Throwable th) {
                    th = th;
                    inputBuffer = inputBuffer2;
                }
                try {
                    byte[] arrayOfByte = new byte[524288];
                    while (true) {
                        int len = inputBuffer2.read(arrayOfByte);
                        if (len == -1) {
                            break;
                        } else {
                            outputBuffer.write(arrayOfByte, 0, len);
                        }
                    }
                    outputBuffer.flush();
                    if (outputBuffer != null) {
                        try {
                            outputBuffer.close();
                        } catch (IOException e3) {
                        }
                    }
                    if (inputBuffer2 != null) {
                        try {
                            inputBuffer2.close();
                        } catch (IOException e4) {
                        }
                    }
                } catch (Error e5) {
                    err = e5;
                    System.gc();
                    throw new MyException(err);
                } catch (Exception e6) {
                    ex = e6;
                    throw new MyException(ex);
                } catch (Throwable th2) {
                    th = th2;
                    outputBuffer2 = outputBuffer;
                    inputBuffer = inputBuffer2;
                    if (outputBuffer2 != null) {
                        try {
                            outputBuffer2.close();
                        } catch (IOException e7) {
                        }
                    }
                    if (inputBuffer != null) {
                        try {
                            inputBuffer.close();
                            throw th;
                        } catch (IOException e8) {
                            throw th;
                        }
                    }
                    throw th;
                }
            } catch (Error e9) {
                err = e9;
            } catch (Exception e10) {
                ex = e10;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public void returnImage(final String imageUrl, final ImageCallback imageCallback) {
        if (imageUrl != null && !"".equals(imageUrl)) {
            this.threadPool.execute(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.4
                @Override // java.lang.Runnable
                public void run() {
                    final Bitmap bitmap = GlobalCache.getBitmap(imageUrl);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        Handler handler = ThumbnailGenerator.this.handler;
                        final ImageCallback imageCallback2 = imageCallback;
                        final String str = imageUrl;
                        handler.post(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.4.1
                            @Override // java.lang.Runnable
                            public void run() {
                                imageCallback2.imageLoaded(bitmap, str);
                            }
                        });
                        return;
                    }
                    try {
                        final Bitmap bitmapFromUrl = ThumbnailGenerator.this.getBitmapFromURL(imageUrl);
                        Handler handler2 = ThumbnailGenerator.this.handler;
                        final ImageCallback imageCallback3 = imageCallback;
                        final String str2 = imageUrl;
                        handler2.post(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.4.2
                            @Override // java.lang.Runnable
                            public void run() {
                                imageCallback3.imageLoaded(bitmapFromUrl, str2);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void returnLocalImageThumb(final int position, final long videoId, final String imageUrl, final ImageCallback imageCallback) {
        this.threadPool.execute(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.5
            @Override // java.lang.Runnable
            public void run() {
                if (!ThumbnailGenerator.this.mAllowLoad) {
                    synchronized (ThumbnailGenerator.this.lock) {
                        try {
                            ThumbnailGenerator.this.lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (ThumbnailGenerator.this.mAllowLoad && ThumbnailGenerator.this.firstLoad) {
                    ThumbnailGenerator.this.loadLocalImageThumb(position, videoId, imageUrl, imageCallback);
                }
                if (!ThumbnailGenerator.this.mAllowLoad || position < ThumbnailGenerator.this.mStartLoadLimit || position > ThumbnailGenerator.this.mStopLoadLimit) {
                    return;
                }
                ThumbnailGenerator.this.loadLocalImageThumb(position, videoId, imageUrl, imageCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadLocalImageThumb(int position, long videoId, String imageUrl, ImageCallback imageCallback) {
        Bitmap bitmap = GlobalCache.getBitmap(imageUrl);
        if (bitmap != null && !bitmap.isRecycled()) {
            handleBitmap(Integer.valueOf(position), imageUrl, bitmap, imageCallback);
            return;
        }
        try {
            Bitmap bitmapFromUrl = getLocalImageThumb(imageUrl, videoId, 3);
            handleBitmap(Integer.valueOf(position), imageUrl, bitmapFromUrl, imageCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Bitmap getLocalImageThumb(String imageUrl, long id, int kind) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Thumbnails.getThumbnail(HiMultiscreen.getApplication().getContentResolver(), id, kind, null);
        } catch (Error ex) {
            ex.printStackTrace();
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
        if (bitmap == null || bitmap.isRecycled()) {
            bitmap = null;
        } else {
            GlobalCache.putToCache(imageUrl, bitmap);
        }
        return bitmap;
    }

    public void returnLocalVideoThumb(final long videoId, final String imageUrl, final ImageCallback imageCallback) {
        if (imageUrl != null && !"".equals(imageUrl)) {
            this.threadPool.execute(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.6
                @Override // java.lang.Runnable
                public void run() {
                    final Bitmap bitmap = GlobalCache.getBitmap(imageUrl);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        Handler handler = ThumbnailGenerator.this.handler;
                        final ImageCallback imageCallback2 = imageCallback;
                        final String str = imageUrl;
                        handler.post(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.6.1
                            @Override // java.lang.Runnable
                            public void run() {
                                imageCallback2.imageLoaded(bitmap, str);
                            }
                        });
                        return;
                    }
                    try {
                        final Bitmap bitmapFromLocal = ThumbnailGenerator.this.getLocalVideoThumb(imageUrl, videoId, 3);
                        Handler handler2 = ThumbnailGenerator.this.handler;
                        final ImageCallback imageCallback3 = imageCallback;
                        final String str2 = imageUrl;
                        handler2.post(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.6.2
                            @Override // java.lang.Runnable
                            public void run() {
                                imageCallback3.imageLoaded(bitmapFromLocal, str2);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void returnLocalVideoThumb(final int position, final long videoId, final String imageUrl, final ImageCallback imageCallback) {
        this.threadPool.execute(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.7
            @Override // java.lang.Runnable
            public void run() {
                if (!ThumbnailGenerator.this.mAllowLoad) {
                    synchronized (ThumbnailGenerator.this.lock) {
                        try {
                            ThumbnailGenerator.this.lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (ThumbnailGenerator.this.mAllowLoad && ThumbnailGenerator.this.firstLoad) {
                    ThumbnailGenerator.this.loadLocalVideoThumb(position, videoId, imageUrl, imageCallback);
                }
                if (!ThumbnailGenerator.this.mAllowLoad || position < ThumbnailGenerator.this.mStartLoadLimit || position > ThumbnailGenerator.this.mStopLoadLimit) {
                    return;
                }
                ThumbnailGenerator.this.loadLocalVideoThumb(position, videoId, imageUrl, imageCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadLocalVideoThumb(int position, long videoId, String imageUrl, ImageCallback imageCallback) {
        if (imageUrl != null && !"".equals(imageUrl)) {
            Bitmap bitmap = GlobalCache.getBitmap(imageUrl);
            if (bitmap != null && !bitmap.isRecycled()) {
                handleBitmap(Integer.valueOf(position), imageUrl, bitmap, imageCallback);
                return;
            }
            try {
                Bitmap bitmapFromLocal = getLocalVideoThumb(imageUrl, videoId, 3);
                handleBitmap(Integer.valueOf(position), imageUrl, bitmapFromLocal, imageCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Bitmap getLocalVideoThumb(String strImagLink, long id, int kind) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Video.Thumbnails.getThumbnail(HiMultiscreen.getApplication().getContentResolver(), id, kind, null);
        } catch (Error ex) {
            ex.printStackTrace();
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
        if (bitmap == null || bitmap.isRecycled()) {
            bitmap = null;
        } else {
            GlobalCache.putToCache(strImagLink, bitmap);
        }
        return bitmap;
    }

    public void returnVideoThumbFromURL(final int position, final String imageUrl, final ImageCallback imageCallback) {
        if (imageUrl != null && !"".equals(imageUrl)) {
            this.threadPool.execute(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.8
                @Override // java.lang.Runnable
                public void run() {
                    if (!ThumbnailGenerator.this.mAllowLoad) {
                        synchronized (ThumbnailGenerator.this.lock) {
                            try {
                                ThumbnailGenerator.this.lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (ThumbnailGenerator.this.mAllowLoad && ThumbnailGenerator.this.firstLoad) {
                        ThumbnailGenerator.this.loadVideoThumbFromURL(position, imageUrl, imageCallback);
                    }
                    if (!ThumbnailGenerator.this.mAllowLoad || position < ThumbnailGenerator.this.mStartLoadLimit || position > ThumbnailGenerator.this.mStopLoadLimit) {
                        return;
                    }
                    ThumbnailGenerator.this.loadVideoThumbFromURL(position, imageUrl, imageCallback);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadVideoThumbFromURL(int position, String imageUrl, ImageCallback imageCallback) {
        Bitmap bitmap = GlobalCache.getBitmap(imageUrl);
        if (bitmap != null && !bitmap.isRecycled()) {
            handleBitmap(Integer.valueOf(position), imageUrl, bitmap, imageCallback);
            return;
        }
        try {
            Bitmap d = getVideoThumbnailBitmapFromURL(imageUrl);
            handleBitmap(Integer.valueOf(position), imageUrl, d, imageCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getVideoThumbnailBitmapFromURL(String imageUrl) throws IOException {
        if (imageUrl == null || "".equals(imageUrl)) {
            return null;
        }
        try {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(imageUrl, 3);
            if (bitmap != null && !bitmap.isRecycled()) {
                GlobalCache.putToCache(imageUrl, bitmap);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleBitmap(final Integer mt, final String strImagLink, final Bitmap bitmap, final ImageCallback imageCallback) {
        this.handler.post(new Runnable() { // from class: com.hisilicon.dlna.dmc.utility.ThumbnailGenerator.9
            @Override // java.lang.Runnable
            public void run() {
                if (ThumbnailGenerator.this.firstLoad || (mt.intValue() >= ThumbnailGenerator.this.mStartLoadLimit && mt.intValue() <= ThumbnailGenerator.this.mStopLoadLimit)) {
                    imageCallback.imageLoaded(bitmap, strImagLink);
                }
            }
        });
    }

    public String[] queryImageThumbIdAndData(String[] selectionArgs) {
        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id", "_data"};
        String[] str = queryThumbnail(uri, projection, "image_id=?", selectionArgs);
        return str;
    }

    public String[] queryVideoThumbIdAndData(String[] selectionArgs) {
        Uri uri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id", "_data"};
        String[] str = queryThumbnail(uri, projection, "video_id=?", selectionArgs);
        return str;
    }

    public String[] queryAudioThumbIdAndData(String[] selectionArgs) {
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id", "album_art"};
        String[] str = queryThumbnail(uri, projection, "_id=?", selectionArgs);
        return str;
    }

    public String[] queryThumbnail(Uri uri, String[] projection, String selection, String[] selectionArgs) {
        Cursor thumbCursor = HiMultiscreen.getResolver().query(uri, projection, selection, selectionArgs, null);
        String[] str = new String[2];
        if (thumbCursor != null && thumbCursor.getCount() != 0) {
            thumbCursor.moveToFirst();
            int columnId = thumbCursor.getColumnIndex(projection[0]);
            int columnData = thumbCursor.getColumnIndex(projection[1]);
            str[0] = new StringBuilder(String.valueOf(thumbCursor.getLong(columnId))).toString();
            str[1] = thumbCursor.getString(columnData);
        }
        if (thumbCursor != null) {
            thumbCursor.close();
        }
        return str;
    }
}
