package master.flame.danmaku.controller;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import master.flame.danmaku.controller.IDrawTask;
import master.flame.danmaku.danmaku.model.AbsDisplayer;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakuIterator;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDrawingCache;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.DrawingCache;
import master.flame.danmaku.danmaku.model.android.DrawingCachePoolManager;
import master.flame.danmaku.danmaku.model.objectpool.Pool;
import master.flame.danmaku.danmaku.model.objectpool.Pools;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.renderer.IRenderer;
import master.flame.danmaku.danmaku.util.DanmakuUtils;
import tv.cjump.jni.NativeBitmapFactory;

/* loaded from: classes.dex */
public class CacheManagingDrawTask extends DrawTask {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int MAX_CACHE_SCREEN_SIZE = 3;
    private CacheManager mCacheManager;
    private DanmakuTimer mCacheTimer;
    private final Object mDrawingNotify;
    private int mMaxCacheSize;

    static {
        $assertionsDisabled = !CacheManagingDrawTask.class.desiredAssertionStatus();
    }

    public CacheManagingDrawTask(DanmakuTimer timer, Context context, AbsDisplayer<?> disp, IDrawTask.TaskListener taskListener, int maxCacheSize) throws Throwable {
        super(timer, context, disp, taskListener);
        this.mMaxCacheSize = 2;
        this.mDrawingNotify = new Object();
        NativeBitmapFactory.loadLibs();
        this.mMaxCacheSize = maxCacheSize;
        if (NativeBitmapFactory.isInNativeAlloc()) {
            this.mMaxCacheSize = maxCacheSize * 3;
        }
        this.mCacheManager = new CacheManager(maxCacheSize, 3);
    }

    @Override // master.flame.danmaku.controller.DrawTask
    protected void initTimer(DanmakuTimer timer) {
        this.mTimer = timer;
        this.mCacheTimer = new DanmakuTimer();
        this.mCacheTimer.update(timer.currMillisecond);
    }

    @Override // master.flame.danmaku.controller.DrawTask, master.flame.danmaku.controller.IDrawTask
    public void addDanmaku(BaseDanmaku danmaku) {
        if (this.mCacheManager != null) {
            this.mCacheManager.addDanmaku(danmaku);
        }
    }

    @Override // master.flame.danmaku.controller.DrawTask, master.flame.danmaku.controller.IDrawTask
    public IRenderer.RenderingState draw(AbsDisplayer<?> displayer) {
        IRenderer.RenderingState result;
        synchronized (this.danmakuList) {
            result = super.draw(displayer);
        }
        synchronized (this.mDrawingNotify) {
            this.mDrawingNotify.notify();
        }
        if (result != null && this.mCacheManager != null && result.incrementCount < -20) {
            this.mCacheManager.requestClearTimeout();
            this.mCacheManager.requestBuild(-DanmakuFactory.MAX_DANMAKU_DURATION);
        }
        return result;
    }

    @Override // master.flame.danmaku.controller.DrawTask, master.flame.danmaku.controller.IDrawTask
    public void reset() {
        if (this.mRenderer != null) {
            this.mRenderer.clear();
        }
    }

    @Override // master.flame.danmaku.controller.DrawTask, master.flame.danmaku.controller.IDrawTask
    public void seek(long mills) {
        super.seek(mills);
        this.mCacheManager.seek(mills);
    }

    @Override // master.flame.danmaku.controller.DrawTask, master.flame.danmaku.controller.IDrawTask
    public void start() throws Throwable {
        super.start();
        NativeBitmapFactory.loadLibs();
        if (this.mCacheManager == null) {
            this.mCacheManager = new CacheManager(this.mMaxCacheSize, 3);
            this.mCacheManager.begin();
        } else {
            this.mCacheManager.resume();
        }
    }

    @Override // master.flame.danmaku.controller.DrawTask, master.flame.danmaku.controller.IDrawTask
    public void quit() {
        super.quit();
        reset();
        if (this.mCacheManager != null) {
            this.mCacheManager.end();
            this.mCacheManager = null;
        }
        NativeBitmapFactory.releaseLibs();
    }

    @Override // master.flame.danmaku.controller.DrawTask, master.flame.danmaku.controller.IDrawTask
    public void prepare() {
        if (!$assertionsDisabled && this.mParser == null) {
            throw new AssertionError();
        }
        loadDanmakus(this.mParser);
        this.mCacheManager.begin();
    }

    public class CacheManager {
        public static final byte RESULT_FAILED = 1;
        public static final byte RESULT_FAILED_OVERSIZE = 2;
        public static final byte RESULT_SUCCESS = 0;
        private static final String TAG = "CacheManager";
        private CacheHandler mHandler;
        private int mMaxSize;
        private int mScreenSize;
        public HandlerThread mThread;
        Danmakus mCaches = new Danmakus(4);
        DrawingCachePoolManager mCachePoolManager = new DrawingCachePoolManager();
        Pool<DrawingCache> mCachePool = Pools.finitePool(this.mCachePoolManager, 800);
        int danmakuAddedCount = 0;
        private boolean mEndFlag = false;
        private int mRealSize = 0;

        public CacheManager(int maxSize, int screenSize) {
            this.mScreenSize = 3;
            this.mMaxSize = maxSize;
            this.mScreenSize = screenSize;
        }

        public void seek(long mills) {
            if (this.mHandler != null) {
                this.mHandler.requestCancelCaching();
                this.mHandler.removeMessages(3);
                this.mHandler.obtainMessage(5, Long.valueOf(mills)).sendToTarget();
            }
        }

        public void addDanmaku(BaseDanmaku danmaku) {
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(2, danmaku).sendToTarget();
            }
        }

        public void begin() {
            if (this.mThread == null) {
                this.mThread = new HandlerThread("DFM Cache-Building Thread");
                this.mThread.start();
            }
            if (this.mHandler == null) {
                this.mHandler = new CacheHandler(this.mThread.getLooper());
            }
            this.mHandler.begin();
        }

        public void end() {
            this.mEndFlag = true;
            synchronized (CacheManagingDrawTask.this.mDrawingNotify) {
                CacheManagingDrawTask.this.mDrawingNotify.notifyAll();
            }
            if (this.mHandler != null) {
                this.mHandler.pause();
                this.mHandler = null;
            }
            if (this.mThread != null) {
                try {
                    this.mThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.mThread.quit();
                this.mThread = null;
            }
        }

        public void resume() {
            if (this.mHandler != null) {
                this.mHandler.resume();
            } else {
                begin();
            }
        }

        public float getPoolPercent() {
            if (this.mMaxSize == 0) {
                return 0.0f;
            }
            return this.mRealSize / this.mMaxSize;
        }

        public boolean isPoolFull() {
            return this.mRealSize + 5120 >= this.mMaxSize;
        }

        public void evictAll() {
            if (this.mCaches != null) {
                IDanmakuIterator it = this.mCaches.iterator();
                while (it.hasNext()) {
                    BaseDanmaku danmaku = it.next();
                    entryRemoved(true, danmaku, null);
                }
                this.mCaches.clear();
            }
            this.mRealSize = 0;
        }

        public void evictAllNotInScreen() {
            evictAllNotInScreen(false);
        }

        public void evictAllNotInScreen(boolean removeAllReferences) {
            if (this.mCaches != null) {
                IDanmakuIterator it = this.mCaches.iterator();
                while (it.hasNext()) {
                    BaseDanmaku danmaku = it.next();
                    IDrawingCache<?> cache = danmaku.cache;
                    boolean hasReferences = cache != null && cache.hasReferences();
                    if (removeAllReferences && hasReferences) {
                        if (cache.get() != null) {
                            this.mRealSize -= cache.size();
                            cache.destroy();
                        }
                        entryRemoved(true, danmaku, null);
                        it.remove();
                    } else if (!danmaku.hasDrawingCache() || danmaku.isOutside()) {
                        entryRemoved(true, danmaku, null);
                        it.remove();
                    }
                }
            }
            this.mRealSize = 0;
        }

        protected void entryRemoved(boolean evicted, BaseDanmaku oldValue, BaseDanmaku newValue) {
            if (oldValue.cache != null) {
                if (oldValue.cache.hasReferences()) {
                    oldValue.cache.decreaseReference();
                    oldValue.cache = null;
                } else {
                    this.mRealSize -= sizeOf(oldValue);
                    oldValue.cache.destroy();
                    this.mCachePool.release((DrawingCache) oldValue.cache);
                    oldValue.cache = null;
                }
            }
        }

        protected int sizeOf(BaseDanmaku value) {
            if (value.cache == null || value.cache.hasReferences()) {
                return 0;
            }
            return value.cache.size();
        }

        public void clearCachePool() {
            while (true) {
                DrawingCache item = (DrawingCache) this.mCachePool.acquire();
                if (item != null) {
                    item.destroy();
                } else {
                    return;
                }
            }
        }

        public boolean push(BaseDanmaku item, int itemSize) {
            while (this.mRealSize + itemSize > this.mMaxSize && this.mCaches.size() > 0) {
                BaseDanmaku oldValue = this.mCaches.first();
                if (!oldValue.isTimeOut()) {
                    return false;
                }
                entryRemoved(false, oldValue, item);
                this.mCaches.removeItem(oldValue);
            }
            this.mCaches.addItem(item);
            this.mRealSize += itemSize;
            return true;
        }

        public void clearTimeOutCaches() {
            clearTimeOutCaches(CacheManagingDrawTask.this.mTimer.currMillisecond);
        }

        private void clearTimeOutCaches(long time) {
            IDanmakuIterator it = this.mCaches.iterator();
            while (it.hasNext() && !this.mEndFlag) {
                BaseDanmaku val = it.next();
                if (val.isTimeOut()) {
                    synchronized (CacheManagingDrawTask.this.mDrawingNotify) {
                        try {
                            CacheManagingDrawTask.this.mDrawingNotify.wait(30L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    entryRemoved(false, val, null);
                    it.remove();
                } else {
                    return;
                }
            }
        }

        public BaseDanmaku findReuseableCache(BaseDanmaku refDanmaku, boolean strictMode) {
            IDanmakuIterator it = this.mCaches.iterator();
            int slopPixel = 0;
            if (!strictMode) {
                slopPixel = CacheManagingDrawTask.this.mDisp.getSlopPixel() * 2;
            }
            while (it.hasNext()) {
                BaseDanmaku danmaku = it.next();
                if (danmaku.hasDrawingCache()) {
                    if (danmaku.paintWidth != refDanmaku.paintWidth || danmaku.paintHeight != refDanmaku.paintHeight || danmaku.underlineColor != refDanmaku.underlineColor || danmaku.borderColor != refDanmaku.borderColor || danmaku.textColor != refDanmaku.textColor || !danmaku.text.equals(refDanmaku.text)) {
                        if (!strictMode) {
                            if (!danmaku.isTimeOut()) {
                                break;
                            }
                            if (danmaku.cache.hasReferences()) {
                                continue;
                            } else {
                                float widthGap = danmaku.cache.width() - refDanmaku.paintWidth;
                                float heightGap = danmaku.cache.height() - refDanmaku.paintHeight;
                                if (widthGap >= 0.0f && widthGap <= slopPixel && heightGap >= 0.0f && heightGap <= slopPixel) {
                                    return danmaku;
                                }
                            }
                        } else {
                            continue;
                        }
                    } else {
                        return danmaku;
                    }
                }
            }
            return null;
        }

        public class CacheHandler extends Handler {
            public static final int ADD_DANMAKKU = 2;
            public static final int BUILD_CACHES = 3;
            public static final int CLEAR_ALL_CACHES = 7;
            public static final int CLEAR_OUTSIDE_CACHES = 8;
            public static final int CLEAR_OUTSIDE_CACHES_AND_RESET = 9;
            public static final int CLEAR_TIMEOUT_CACHES = 4;
            public static final int DISPATCH_ACTIONS = 16;
            private static final int PREPARE = 1;
            public static final int QUIT = 6;
            public static final int SEEK = 5;
            private boolean mCancelFlag;
            private boolean mPause;
            private boolean mSeekedFlag;

            public CacheHandler(Looper looper) {
                super(looper);
            }

            public void requestCancelCaching() {
                this.mCancelFlag = true;
            }

            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case 1:
                        CacheManager.this.evictAllNotInScreen();
                        for (int i = 0; i < 300; i++) {
                            CacheManager.this.mCachePool.release(new DrawingCache());
                        }
                        break;
                    case 2:
                        synchronized (CacheManagingDrawTask.this.danmakuList) {
                            BaseDanmaku item = (BaseDanmaku) msg.obj;
                            if (!item.isTimeOut()) {
                                if (!item.hasDrawingCache()) {
                                    buildCache(item);
                                }
                                if (item.isLive) {
                                    CacheManagingDrawTask.this.mCacheTimer.update(CacheManagingDrawTask.this.mTimer.currMillisecond + (DanmakuFactory.MAX_DANMAKU_DURATION * CacheManager.this.mScreenSize));
                                }
                                CacheManagingDrawTask.super.addDanmaku(item);
                                return;
                            }
                            return;
                        }
                    case 3:
                        removeMessages(3);
                        boolean repositioned = !(CacheManagingDrawTask.this.mTaskListener == null || CacheManagingDrawTask.this.mReadyState) || this.mSeekedFlag;
                        prepareCaches(repositioned);
                        if (repositioned) {
                            this.mSeekedFlag = false;
                        }
                        if (CacheManagingDrawTask.this.mTaskListener != null && !CacheManagingDrawTask.this.mReadyState) {
                            CacheManagingDrawTask.this.mTaskListener.ready();
                            CacheManagingDrawTask.this.mReadyState = true;
                            return;
                        }
                        return;
                    case 4:
                        CacheManager.this.clearTimeOutCaches();
                        return;
                    case 5:
                        Long seekMills = (Long) msg.obj;
                        if (seekMills != null) {
                            CacheManagingDrawTask.this.mCacheTimer.update(seekMills.longValue());
                            this.mSeekedFlag = true;
                            CacheManager.this.evictAllNotInScreen();
                            resume();
                            return;
                        }
                        return;
                    case 6:
                        removeCallbacksAndMessages(null);
                        this.mPause = true;
                        CacheManager.this.evictAll();
                        CacheManager.this.clearCachePool();
                        getLooper().quit();
                        return;
                    case 7:
                        CacheManager.this.evictAll();
                        CacheManagingDrawTask.this.mCacheTimer.update(CacheManagingDrawTask.this.mTimer.currMillisecond - DanmakuFactory.MAX_DANMAKU_DURATION);
                        this.mSeekedFlag = true;
                        return;
                    case 8:
                        CacheManager.this.evictAllNotInScreen(true);
                        CacheManagingDrawTask.this.mCacheTimer.update(CacheManagingDrawTask.this.mTimer.currMillisecond);
                        return;
                    case 9:
                        CacheManager.this.evictAllNotInScreen(true);
                        CacheManagingDrawTask.this.mCacheTimer.update(CacheManagingDrawTask.this.mTimer.currMillisecond);
                        CacheManagingDrawTask.this.requestClear();
                        return;
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    default:
                        return;
                    case 16:
                        break;
                }
                long delayed = dispatchAction();
                if (delayed <= 0) {
                    delayed = DanmakuFactory.MAX_DANMAKU_DURATION / 2;
                }
                sendEmptyMessageDelayed(16, delayed);
            }

            private long dispatchAction() {
                long gapTime;
                float level = CacheManager.this.getPoolPercent();
                BaseDanmaku firstCache = CacheManager.this.mCaches.first();
                if (firstCache != null) {
                    gapTime = firstCache.time - CacheManagingDrawTask.this.mTimer.currMillisecond;
                } else {
                    gapTime = 0;
                }
                long doubleScreenDuration = DanmakuFactory.MAX_DANMAKU_DURATION * 2;
                if (level < 0.6f && gapTime > DanmakuFactory.MAX_DANMAKU_DURATION) {
                    CacheManagingDrawTask.this.mCacheTimer.update(CacheManagingDrawTask.this.mTimer.currMillisecond);
                    removeMessages(3);
                    sendEmptyMessage(3);
                    return 0L;
                }
                if (level > 0.4f && gapTime < (-doubleScreenDuration)) {
                    removeMessages(4);
                    sendEmptyMessage(4);
                    return 0L;
                }
                if (level < 0.9f) {
                    long deltaTime = CacheManagingDrawTask.this.mCacheTimer.currMillisecond - CacheManagingDrawTask.this.mTimer.currMillisecond;
                    if (deltaTime < 0) {
                        CacheManagingDrawTask.this.mCacheTimer.update(CacheManagingDrawTask.this.mTimer.currMillisecond);
                        sendEmptyMessage(8);
                        sendEmptyMessage(3);
                        return 0L;
                    }
                    if (deltaTime > doubleScreenDuration) {
                        return 0L;
                    }
                    removeMessages(3);
                    sendEmptyMessage(3);
                    return 0L;
                }
                return 0L;
            }

            private void releaseDanmakuCache(BaseDanmaku item, DrawingCache cache) {
                if (cache == null) {
                    cache = (DrawingCache) item.cache;
                }
                item.cache = null;
                if (cache != null) {
                    cache.destroy();
                    CacheManager.this.mCachePool.release(cache);
                }
            }

            private long prepareCaches(boolean repositioned) {
                long curr = CacheManagingDrawTask.this.mCacheTimer.currMillisecond;
                long end = curr + (DanmakuFactory.MAX_DANMAKU_DURATION * CacheManager.this.mScreenSize * 3);
                if (end < CacheManagingDrawTask.this.mTimer.currMillisecond) {
                    return 0L;
                }
                long startTime = System.currentTimeMillis();
                IDanmakus danmakus = CacheManagingDrawTask.this.danmakuList.subnew(curr, end);
                if (danmakus == null || danmakus.isEmpty()) {
                    CacheManagingDrawTask.this.mCacheTimer.update(end);
                    return 0L;
                }
                BaseDanmaku first = danmakus.first();
                BaseDanmaku last = danmakus.last();
                long deltaTime = first.time - CacheManagingDrawTask.this.mTimer.currMillisecond;
                long sleepTime = 30 + ((10 * deltaTime) / DanmakuFactory.MAX_DANMAKU_DURATION);
                long sleepTime2 = Math.min(100L, sleepTime);
                if (repositioned) {
                    sleepTime2 = 0;
                }
                IDanmakuIterator itr = danmakus.iterator();
                BaseDanmaku item = null;
                int count = 0;
                int orderInScreen = 0;
                int currScreenIndex = 0;
                int sizeInScreen = danmakus.size();
                while (!this.mPause && !this.mCancelFlag) {
                    boolean hasNext = itr.hasNext();
                    if (!hasNext) {
                        break;
                    }
                    item = itr.next();
                    count++;
                    if (last.time < CacheManagingDrawTask.this.mTimer.currMillisecond) {
                        break;
                    }
                    if (!item.hasDrawingCache() && (repositioned || (!item.isTimeOut() && item.isOutside()))) {
                        boolean skip = DanmakuFilters.getDefault().filter(item, orderInScreen, sizeInScreen, null, true);
                        if (skip) {
                            continue;
                        } else {
                            if (item.getType() == 1) {
                                int screenIndex = (int) ((item.time - curr) / DanmakuFactory.MAX_DANMAKU_DURATION);
                                if (currScreenIndex == screenIndex) {
                                    orderInScreen++;
                                } else {
                                    orderInScreen = 0;
                                    currScreenIndex = screenIndex;
                                }
                            }
                            if (!repositioned) {
                                try {
                                    synchronized (CacheManagingDrawTask.this.mDrawingNotify) {
                                        CacheManagingDrawTask.this.mDrawingNotify.wait(sleepTime2);
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (buildCache(item) == 1 || (!repositioned && System.currentTimeMillis() - startTime >= DanmakuFactory.COMMON_DANMAKU_DURATION * CacheManager.this.mScreenSize)) {
                                break;
                            }
                        }
                    }
                }
                long consumingTime = System.currentTimeMillis() - startTime;
                if (item != null) {
                    CacheManagingDrawTask.this.mCacheTimer.update(item.time);
                    return consumingTime;
                }
                CacheManagingDrawTask.this.mCacheTimer.update(end);
                return consumingTime;
            }

            private byte buildCache(BaseDanmaku item) {
                byte b;
                if (!item.isMeasured()) {
                    item.measure(CacheManagingDrawTask.this.mDisp);
                }
                DrawingCache cache = null;
                try {
                    BaseDanmaku danmaku = CacheManager.this.findReuseableCache(item, true);
                    if (danmaku != null) {
                        cache = (DrawingCache) danmaku.cache;
                    }
                    if (cache == null) {
                        BaseDanmaku danmaku2 = CacheManager.this.findReuseableCache(item, false);
                        if (danmaku2 != null) {
                            cache = (DrawingCache) danmaku2.cache;
                        }
                        if (cache != null) {
                            danmaku2.cache = null;
                            item.cache = DanmakuUtils.buildDanmakuDrawingCache(item, CacheManagingDrawTask.this.mDisp, cache);
                            CacheManagingDrawTask.this.mCacheManager.push(item, 0);
                            return (byte) 0;
                        }
                        int cacheSize = DanmakuUtils.getCacheSize((int) item.paintWidth, (int) item.paintHeight);
                        if (CacheManager.this.mRealSize + cacheSize > CacheManager.this.mMaxSize) {
                            return (byte) 1;
                        }
                        DrawingCache cache2 = (DrawingCache) CacheManager.this.mCachePool.acquire();
                        synchronized (CacheManagingDrawTask.this.danmakuList) {
                            DrawingCache cache3 = DanmakuUtils.buildDanmakuDrawingCache(item, CacheManagingDrawTask.this.mDisp, cache2);
                            item.cache = cache3;
                            boolean pushed = CacheManagingDrawTask.this.mCacheManager.push(item, CacheManager.this.sizeOf(item));
                            if (!pushed) {
                                releaseDanmakuCache(item, cache3);
                            }
                            b = pushed ? (byte) 0 : (byte) 1;
                        }
                        return b;
                    }
                    cache.increaseReference();
                    item.cache = cache;
                    CacheManagingDrawTask.this.mCacheManager.push(item, CacheManager.this.sizeOf(item));
                    return (byte) 0;
                } catch (Exception e) {
                    releaseDanmakuCache(item, null);
                    return (byte) 1;
                } catch (OutOfMemoryError e2) {
                    releaseDanmakuCache(item, null);
                    return (byte) 1;
                }
            }

            public void begin() {
                sendEmptyMessage(1);
                sendEmptyMessageDelayed(4, DanmakuFactory.MAX_DANMAKU_DURATION);
            }

            public void pause() {
                this.mPause = true;
                removeCallbacksAndMessages(null);
                sendEmptyMessage(6);
            }

            public void resume() {
                this.mCancelFlag = false;
                this.mPause = false;
                removeMessages(16);
                sendEmptyMessage(16);
                sendEmptyMessageDelayed(4, DanmakuFactory.MAX_DANMAKU_DURATION);
            }

            public boolean isPause() {
                return this.mPause;
            }

            public void requestBuildCacheAndDraw(long correctionTime) {
                removeMessages(3);
                this.mSeekedFlag = true;
                this.mCancelFlag = false;
                CacheManagingDrawTask.this.mCacheTimer.update(CacheManagingDrawTask.this.mTimer.currMillisecond + correctionTime);
                sendEmptyMessage(3);
            }
        }

        public long getFirstCacheTime() {
            BaseDanmaku firstItem;
            if (this.mCaches == null || this.mCaches.size() <= 0 || (firstItem = this.mCaches.first()) == null) {
                return 0L;
            }
            return firstItem.time;
        }

        public void requestBuild(long correctionTime) {
            if (this.mHandler != null) {
                this.mHandler.requestBuildCacheAndDraw(correctionTime);
            }
        }

        public void requestClearAll() {
            if (this.mHandler != null) {
                this.mHandler.removeMessages(3);
                this.mHandler.requestCancelCaching();
                this.mHandler.removeMessages(7);
                this.mHandler.sendEmptyMessage(7);
            }
        }

        public void requestClearUnused() {
            if (this.mHandler != null) {
                this.mHandler.removeMessages(9);
                this.mHandler.sendEmptyMessage(9);
            }
        }

        public void requestClearTimeout() {
            if (this.mHandler != null) {
                this.mHandler.removeMessages(4);
                this.mHandler.sendEmptyMessage(4);
            }
        }

        public void post(Runnable runnable) {
            if (this.mHandler != null) {
                this.mHandler.post(runnable);
            }
        }
    }

    @Override // master.flame.danmaku.controller.DrawTask, master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig.ConfigChangedCallback
    public boolean onDanmakuConfigChanged(DanmakuGlobalConfig config, DanmakuGlobalConfig.DanmakuConfigTag tag, Object... values) {
        if (!super.handleOnDanmakuConfigChanged(config, tag, values)) {
            if (DanmakuGlobalConfig.DanmakuConfigTag.SCROLL_SPEED_FACTOR.equals(tag)) {
                this.mDisp.resetSlopPixel(DanmakuGlobalConfig.DEFAULT.scaleTextSize);
                requestClear();
            } else if (tag.isVisibilityRelatedTag()) {
                if (values != null && values.length > 0 && values[0] != null && ((!(values[0] instanceof Boolean) || ((Boolean) values[0]).booleanValue()) && this.mCacheManager != null)) {
                    this.mCacheManager.requestBuild(0L);
                }
                requestClear();
            } else if (DanmakuGlobalConfig.DanmakuConfigTag.TRANSPARENCY.equals(tag) || DanmakuGlobalConfig.DanmakuConfigTag.SCALE_TEXTSIZE.equals(tag) || DanmakuGlobalConfig.DanmakuConfigTag.DANMAKU_STYLE.equals(tag)) {
                if (DanmakuGlobalConfig.DanmakuConfigTag.SCALE_TEXTSIZE.equals(tag)) {
                    this.mDisp.resetSlopPixel(DanmakuGlobalConfig.DEFAULT.scaleTextSize);
                }
                if (this.mCacheManager != null) {
                    this.mCacheManager.requestClearAll();
                    this.mCacheManager.requestBuild(-DanmakuFactory.MAX_DANMAKU_DURATION);
                }
            } else if (this.mCacheManager != null) {
                this.mCacheManager.requestClearUnused();
                this.mCacheManager.requestBuild(0L);
            }
        }
        if (this.mTaskListener != null && this.mCacheManager != null) {
            this.mCacheManager.post(new Runnable() { // from class: master.flame.danmaku.controller.CacheManagingDrawTask.1
                AnonymousClass1() {
                }

                @Override // java.lang.Runnable
                public void run() {
                    CacheManagingDrawTask.this.mTaskListener.onDanmakuConfigChanged();
                }
            });
            return true;
        }
        return true;
    }

    /* renamed from: master.flame.danmaku.controller.CacheManagingDrawTask$1 */
    class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            CacheManagingDrawTask.this.mTaskListener.onDanmakuConfigChanged();
        }
    }
}
