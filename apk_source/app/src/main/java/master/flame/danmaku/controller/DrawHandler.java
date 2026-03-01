package master.flame.danmaku.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import java.util.LinkedList;
import master.flame.danmaku.controller.IDrawTask;
import master.flame.danmaku.danmaku.model.AbsDisplayer;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.AndroidDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.renderer.IRenderer;
import master.flame.danmaku.danmaku.util.AndroidUtils;
import tv.cjump.jni.DeviceUtils;

/* loaded from: classes.dex */
public class DrawHandler extends Handler {
    private static final int CLEAR_DANMAKUS_ON_SCREEN = 13;
    private static final int HIDE_DANMAKUS = 9;
    private static final long INDEFINITE_TIME = 10000000;
    private static final int MAX_RECORD_SIZE = 500;
    private static final int NOTIFY_DISP_SIZE_CHANGED = 10;
    private static final int NOTIFY_RENDERING = 11;
    private static final int PAUSE = 7;
    public static final int PREPARE = 5;
    private static final int QUIT = 6;
    public static final int RESUME = 3;
    public static final int SEEK_POS = 4;
    private static final int SHOW_DANMAKUS = 8;
    public static final int START = 1;
    public static final int UPDATE = 2;
    private static final int UPDATE_WHEN_PAUSED = 12;
    public IDrawTask drawTask;
    private Callback mCallback;
    private long mCordonTime;
    private long mCordonTime2;
    private IDanmakuViewController mDanmakuView;
    private boolean mDanmakusVisible;
    private AbsDisplayer<Canvas> mDisp;
    private LinkedList<Long> mDrawTimes;
    private long mFrameUpdateRate;
    private boolean mIdleSleep;
    private boolean mInSeekingAction;
    private boolean mInSyncAction;
    private boolean mInWaitingState;
    private long mLastDeltaTime;
    private BaseDanmakuParser mParser;
    private boolean mReady;
    private long mRemainingTime;
    private final IRenderer.RenderingState mRenderingState;
    private int mSkipFrames;
    private UpdateThread mThread;
    private long mThresholdTime;
    private long mTimeBase;
    private final boolean mUpdateInNewThread;
    private long pausedPosition;
    private boolean quitFlag;
    private DanmakuTimer timer;

    public interface Callback {
        void prepared();

        void updateTimer(DanmakuTimer danmakuTimer);
    }

    public DrawHandler(Looper looper, IDanmakuViewController view, boolean danmakuVisibile) {
        super(looper);
        this.pausedPosition = 0L;
        this.quitFlag = true;
        this.timer = new DanmakuTimer();
        this.mDanmakusVisible = true;
        this.mRenderingState = new IRenderer.RenderingState();
        this.mDrawTimes = new LinkedList<>();
        this.mCordonTime = 30L;
        this.mCordonTime2 = 60L;
        this.mFrameUpdateRate = 16L;
        this.mUpdateInNewThread = Runtime.getRuntime().availableProcessors() > 3;
        this.mIdleSleep = DeviceUtils.isProblemBoxDevice() ? false : true;
        bindView(view);
        if (danmakuVisibile) {
            showDanmakus(null);
        } else {
            hideDanmakus(false);
        }
        this.mDanmakusVisible = danmakuVisibile;
    }

    private void bindView(IDanmakuViewController view) {
        this.mDanmakuView = view;
    }

    public void setParser(BaseDanmakuParser parser) {
        this.mParser = parser;
    }

    public void setCallback(Callback cb) {
        this.mCallback = cb;
    }

    public void quit() {
        sendEmptyMessage(6);
    }

    public boolean isStop() {
        return this.quitFlag;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0035  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0063  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0153  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0164  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0173  */
    @Override // android.os.Handler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void handleMessage(android.os.Message r15) {
        /*
            Method dump skipped, instructions count: 482
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: master.flame.danmaku.controller.DrawHandler.handleMessage(android.os.Message):void");
    }

    private void quitUpdateThread() {
        if (this.mThread != null) {
            synchronized (this.drawTask) {
                this.drawTask.notifyAll();
            }
            this.mThread.quit();
            try {
                this.mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.mThread = null;
        }
    }

    private void updateInCurrentThread() {
        if (!this.quitFlag) {
            long startMS = System.currentTimeMillis();
            long d = syncTimer(startMS);
            if (d < 0) {
                removeMessages(2);
                sendEmptyMessageDelayed(2, 60 - d);
                return;
            }
            long d2 = this.mDanmakuView.drawDanmakus();
            removeMessages(2);
            if (!this.mDanmakusVisible) {
                waitRendering(INDEFINITE_TIME);
                return;
            }
            if (this.mRenderingState.nothingRendered && this.mIdleSleep) {
                long dTime = this.mRenderingState.endTime - this.timer.currMillisecond;
                if (dTime > 500) {
                    waitRendering(dTime - 400);
                    return;
                }
            }
            if (d2 < this.mFrameUpdateRate) {
                sendEmptyMessageDelayed(2, this.mFrameUpdateRate - d2);
            } else {
                sendEmptyMessage(2);
            }
        }
    }

    private void updateInNewThread() {
        if (this.mThread == null) {
            this.mThread = new UpdateThread("DFM Update") { // from class: master.flame.danmaku.controller.DrawHandler.2
                @Override // master.flame.danmaku.controller.UpdateThread, java.lang.Thread, java.lang.Runnable
                public void run() {
                    long lastTime = System.currentTimeMillis();
                    while (!isQuited() && !DrawHandler.this.quitFlag) {
                        long startMS = System.currentTimeMillis();
                        long diffTime = DrawHandler.this.mFrameUpdateRate - (System.currentTimeMillis() - lastTime);
                        if (diffTime > 1) {
                            SystemClock.sleep(1L);
                        } else {
                            lastTime = startMS;
                            long d = DrawHandler.this.syncTimer(startMS);
                            if (d >= 0) {
                                DrawHandler.this.mDanmakuView.drawDanmakus();
                                if (DrawHandler.this.mDanmakusVisible) {
                                    if (DrawHandler.this.mRenderingState.nothingRendered && DrawHandler.this.mIdleSleep) {
                                        long dTime = DrawHandler.this.mRenderingState.endTime - DrawHandler.this.timer.currMillisecond;
                                        if (dTime > 500) {
                                            DrawHandler.this.notifyRendering();
                                            DrawHandler.this.waitRendering(dTime - 400);
                                        }
                                    }
                                } else {
                                    DrawHandler.this.waitRendering(DrawHandler.INDEFINITE_TIME);
                                }
                            } else {
                                SystemClock.sleep(60 - d);
                            }
                        }
                    }
                }
            };
            this.mThread.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final long syncTimer(long startMS) {
        long gapTime;
        if (this.mInSeekingAction || this.mInSyncAction) {
            return 0L;
        }
        this.mInSyncAction = true;
        long d = 0;
        long time = startMS - this.mTimeBase;
        if (!this.mDanmakusVisible || this.mRenderingState.nothingRendered || this.mInWaitingState) {
            this.timer.update(time);
            this.mRemainingTime = 0L;
        } else {
            long gapTime2 = time - this.timer.currMillisecond;
            long averageTime = Math.max(this.mFrameUpdateRate, getAverageRenderingTime());
            if (gapTime2 > 2000 || this.mRenderingState.consumingTime > this.mCordonTime || averageTime > this.mCordonTime) {
                d = gapTime2;
                gapTime = 0;
            } else {
                long d2 = averageTime + (gapTime2 / this.mFrameUpdateRate);
                d = Math.min(this.mCordonTime, Math.max(this.mFrameUpdateRate, d2));
                long a = d - this.mLastDeltaTime;
                if (Math.abs(a) < 4 && d > this.mFrameUpdateRate && this.mLastDeltaTime > this.mFrameUpdateRate) {
                    d = this.mLastDeltaTime;
                }
                gapTime = gapTime2 - d;
            }
            this.mLastDeltaTime = d;
            this.mRemainingTime = gapTime;
            this.timer.add(d);
        }
        if (this.mCallback != null) {
            this.mCallback.updateTimer(this.timer);
        }
        this.mInSyncAction = false;
        return d;
    }

    private void syncTimerIfNeeded() {
        if (this.mInWaitingState) {
            syncTimer(System.currentTimeMillis());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initRenderingConfigs() {
        this.mCordonTime = Math.max(33L, (long) (16 * 2.5f));
        this.mCordonTime2 = this.mCordonTime * 2;
        this.mFrameUpdateRate = Math.max(16L, (16 / 15) * 15);
        this.mLastDeltaTime = this.mFrameUpdateRate;
        this.mThresholdTime = this.mFrameUpdateRate + 3;
    }

    private void prepare(final Runnable runnable) {
        if (this.drawTask == null) {
            this.drawTask = createDrawTask(this.mDanmakuView.isDanmakuDrawingCacheEnabled(), this.timer, this.mDanmakuView.getContext(), this.mDanmakuView.getWidth(), this.mDanmakuView.getHeight(), this.mDanmakuView.isHardwareAccelerated(), new IDrawTask.TaskListener() { // from class: master.flame.danmaku.controller.DrawHandler.3
                @Override // master.flame.danmaku.controller.IDrawTask.TaskListener
                public void ready() {
                    DrawHandler.this.initRenderingConfigs();
                    runnable.run();
                }

                @Override // master.flame.danmaku.controller.IDrawTask.TaskListener
                public void onDanmakuAdd(BaseDanmaku danmaku) {
                    DrawHandler.this.obtainMessage(11).sendToTarget();
                }

                @Override // master.flame.danmaku.controller.IDrawTask.TaskListener
                public void onDanmakuConfigChanged() {
                    if (DrawHandler.this.quitFlag && DrawHandler.this.mDanmakusVisible) {
                        DrawHandler.this.obtainMessage(12).sendToTarget();
                    }
                }
            });
        } else {
            runnable.run();
        }
    }

    public boolean isPrepared() {
        return this.mReady;
    }

    private IDrawTask createDrawTask(boolean useDrwaingCache, DanmakuTimer timer, Context context, int width, int height, boolean isHardwareAccelerated, IDrawTask.TaskListener taskListener) {
        IDrawTask task;
        this.mDisp = new AndroidDisplayer();
        this.mDisp.setSize(width, height);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.mDisp.setDensities(displayMetrics.density, displayMetrics.densityDpi, displayMetrics.scaledDensity);
        this.mDisp.resetSlopPixel(DanmakuGlobalConfig.DEFAULT.scaleTextSize);
        this.mDisp.setHardwareAccelerated(isHardwareAccelerated);
        obtainMessage(10, false).sendToTarget();
        if (useDrwaingCache) {
            task = new CacheManagingDrawTask(timer, context, this.mDisp, taskListener, (1048576 * AndroidUtils.getMemoryClass(context)) / 3);
        } else {
            task = new DrawTask(timer, context, this.mDisp, taskListener);
        }
        task.setParser(this.mParser);
        task.prepare();
        return task;
    }

    public void seekTo(Long ms) {
        this.mInSeekingAction = true;
        removeMessages(2);
        removeMessages(3);
        removeMessages(4);
        obtainMessage(4, ms).sendToTarget();
    }

    public void addDanmaku(BaseDanmaku item) {
        if (this.drawTask != null) {
            item.setTimer(this.timer);
            this.drawTask.addDanmaku(item);
            obtainMessage(11).sendToTarget();
        }
    }

    public void resume() {
        sendEmptyMessage(3);
    }

    public void prepare() {
        sendEmptyMessage(5);
    }

    public void pause() {
        syncTimerIfNeeded();
        sendEmptyMessage(7);
    }

    public void showDanmakus(Long position) {
        if (!this.mDanmakusVisible) {
            removeMessages(8);
            removeMessages(9);
            obtainMessage(8, position).sendToTarget();
        }
    }

    public long hideDanmakus(boolean quitDrawTask) {
        if (!this.mDanmakusVisible) {
            return this.timer.currMillisecond;
        }
        removeMessages(8);
        removeMessages(9);
        obtainMessage(9, Boolean.valueOf(quitDrawTask)).sendToTarget();
        return this.timer.currMillisecond;
    }

    public boolean getVisibility() {
        return this.mDanmakusVisible;
    }

    public IRenderer.RenderingState draw(Canvas canvas) {
        if (this.drawTask == null) {
            return this.mRenderingState;
        }
        this.mDisp.setExtraData(canvas);
        this.mRenderingState.set(this.drawTask.draw(this.mDisp));
        recordRenderingTime();
        return this.mRenderingState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyRendering() {
        if (this.mInWaitingState) {
            if (this.drawTask != null) {
                this.drawTask.requestClear();
            }
            this.mSkipFrames = 0;
            if (this.mUpdateInNewThread) {
                synchronized (this) {
                    this.mDrawTimes.clear();
                }
                synchronized (this.drawTask) {
                    this.drawTask.notifyAll();
                }
            } else {
                this.mDrawTimes.clear();
                removeMessages(2);
                sendEmptyMessage(2);
            }
            this.mInWaitingState = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void waitRendering(long dTime) {
        this.mRenderingState.sysTime = System.currentTimeMillis();
        this.mInWaitingState = true;
        if (!this.mUpdateInNewThread) {
            if (dTime == INDEFINITE_TIME) {
                removeMessages(11);
                removeMessages(2);
                return;
            } else {
                removeMessages(11);
                removeMessages(2);
                sendEmptyMessageDelayed(11, dTime);
                return;
            }
        }
        try {
            synchronized (this.drawTask) {
                if (dTime == INDEFINITE_TIME) {
                    this.drawTask.wait();
                } else {
                    this.drawTask.wait(dTime);
                }
                sendEmptyMessage(11);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized long getAverageRenderingTime() {
        long j;
        int frames = this.mDrawTimes.size();
        if (frames <= 0) {
            j = 0;
        } else {
            long dtime = this.mDrawTimes.getLast().longValue() - this.mDrawTimes.getFirst().longValue();
            j = dtime / frames;
        }
        return j;
    }

    private synchronized void recordRenderingTime() {
        long lastTime = System.currentTimeMillis();
        this.mDrawTimes.addLast(Long.valueOf(lastTime));
        int frames = this.mDrawTimes.size();
        if (frames > 500) {
            this.mDrawTimes.removeFirst();
        }
    }

    public IDisplayer getDisplayer() {
        return this.mDisp;
    }

    public void notifyDispSizeChanged(int width, int height) {
        if (this.mDisp != null) {
            if (this.mDisp.getWidth() != width || this.mDisp.getHeight() != height) {
                this.mDisp.setSize(width, height);
                obtainMessage(10, true).sendToTarget();
            }
        }
    }

    public void removeAllDanmakus() {
        if (this.drawTask != null) {
            this.drawTask.removeAllDanmakus();
        }
    }

    public void removeAllLiveDanmakus() {
        if (this.drawTask != null) {
            this.drawTask.removeAllLiveDanmakus();
        }
    }

    public long getCurrentTime() {
        return (this.quitFlag || !this.mInWaitingState) ? this.timer.currMillisecond - this.mRemainingTime : System.currentTimeMillis() - this.mTimeBase;
    }

    public void clearDanmakusOnScreen() {
        obtainMessage(13).sendToTarget();
    }
}
