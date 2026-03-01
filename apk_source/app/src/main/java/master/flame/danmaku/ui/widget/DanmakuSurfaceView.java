package master.flame.danmaku.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.util.LinkedList;
import java.util.Locale;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.DrawHelper;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.controller.IDanmakuViewController;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.renderer.IRenderer;

/* loaded from: classes.dex */
public class DanmakuSurfaceView extends SurfaceView implements IDanmakuView, IDanmakuViewController, SurfaceHolder.Callback {
    private static final int MAX_RECORD_SIZE = 50;
    private static final int ONE_SECOND = 1000;
    public static final String TAG = "DanmakuSurfaceView";
    private DrawHandler handler;
    private boolean isSurfaceCreated;
    private DrawHandler.Callback mCallback;
    private boolean mDanmakuVisible;
    private LinkedList<Long> mDrawTimes;
    protected int mDrawingThreadType;
    private boolean mEnableDanmakuDrwaingCache;
    private HandlerThread mHandlerThread;
    private boolean mShowFps;
    private SurfaceHolder mSurfaceHolder;

    public DanmakuSurfaceView(Context context) {
        super(context);
        this.mEnableDanmakuDrwaingCache = true;
        this.mDanmakuVisible = true;
        this.mDrawingThreadType = 0;
        init();
    }

    private void init() {
        setZOrderMediaOverlay(true);
        setWillNotCacheDrawing(true);
        setDrawingCacheEnabled(false);
        setWillNotDraw(true);
        this.mSurfaceHolder = getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setFormat(-2);
        DrawHelper.useDrawColorToClearCanvas(true, true);
    }

    public DanmakuSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mEnableDanmakuDrwaingCache = true;
        this.mDanmakuVisible = true;
        this.mDrawingThreadType = 0;
        init();
    }

    public DanmakuSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mEnableDanmakuDrwaingCache = true;
        this.mDanmakuVisible = true;
        this.mDrawingThreadType = 0;
        init();
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void addDanmaku(BaseDanmaku item) {
        if (this.handler != null) {
            this.handler.addDanmaku(item);
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void removeAllDanmakus() {
        if (this.handler != null) {
            this.handler.removeAllDanmakus();
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void removeAllLiveDanmakus() {
        if (this.handler != null) {
            this.handler.removeAllLiveDanmakus();
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void setCallback(DrawHandler.Callback callback) {
        this.mCallback = callback;
        if (this.handler != null) {
            this.handler.setCallback(callback);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.isSurfaceCreated = true;
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            DrawHelper.clearCanvas(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (this.handler != null) {
            this.handler.notifyDispSizeChanged(width, height);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.isSurfaceCreated = false;
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void release() {
        stop();
        if (this.mDrawTimes != null) {
            this.mDrawTimes.clear();
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void stop() {
        stopDraw();
    }

    private void stopDraw() {
        if (this.handler != null) {
            this.handler.quit();
            this.handler = null;
        }
        if (this.mHandlerThread != null) {
            try {
                this.mHandlerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.mHandlerThread.quit();
            this.mHandlerThread = null;
        }
    }

    protected Looper getLooper(int type) {
        int priority;
        if (this.mHandlerThread != null) {
            this.mHandlerThread.quit();
            this.mHandlerThread = null;
        }
        switch (type) {
            case 1:
                return Looper.getMainLooper();
            case 2:
                priority = -8;
                break;
            case 3:
                priority = 19;
                break;
            default:
                priority = 0;
                break;
        }
        String threadName = "DFM Handler Thread #" + priority;
        this.mHandlerThread = new HandlerThread(threadName, priority);
        this.mHandlerThread.start();
        return this.mHandlerThread.getLooper();
    }

    private void prepare() {
        if (this.handler == null) {
            this.handler = new DrawHandler(getLooper(this.mDrawingThreadType), this, this.mDanmakuVisible);
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void prepare(BaseDanmakuParser parser) {
        prepare();
        this.handler.setParser(parser);
        this.handler.setCallback(this.mCallback);
        this.handler.prepare();
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public boolean isPrepared() {
        return this.handler != null && this.handler.isPrepared();
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void showFPS(boolean show) {
        this.mShowFps = show;
    }

    private float fps() {
        long lastTime = System.currentTimeMillis();
        this.mDrawTimes.addLast(Long.valueOf(lastTime));
        float dtime = lastTime - this.mDrawTimes.getFirst().longValue();
        int frames = this.mDrawTimes.size();
        if (frames > 50) {
            this.mDrawTimes.removeFirst();
        }
        if (dtime > 0.0f) {
            return (this.mDrawTimes.size() * 1000) / dtime;
        }
        return 0.0f;
    }

    @Override // master.flame.danmaku.controller.IDanmakuViewController
    public long drawDanmakus() {
        if (!this.isSurfaceCreated) {
            return 0L;
        }
        if (!isShown()) {
            return -1L;
        }
        long stime = System.currentTimeMillis();
        Canvas canvas = this.mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            if (this.handler != null) {
                IRenderer.RenderingState rs = this.handler.draw(canvas);
                if (this.mShowFps) {
                    if (this.mDrawTimes == null) {
                        this.mDrawTimes = new LinkedList<>();
                    }
                    long dtime = System.currentTimeMillis() - stime;
                    String fps = String.format(Locale.getDefault(), "fps %.2f,time:%d s,cache:%d,miss:%d", Float.valueOf(fps()), Long.valueOf(getCurrentTime() / 1000), Long.valueOf(rs.cacheHitCount), Long.valueOf(rs.cacheMissCount));
                    DrawHelper.drawFPS(canvas, fps);
                }
            }
            if (this.isSurfaceCreated) {
                this.mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        long dtime2 = System.currentTimeMillis() - stime;
        return dtime2;
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void toggle() {
        if (this.isSurfaceCreated) {
            if (this.handler == null) {
                start();
            } else if (this.handler.isStop()) {
                resume();
            } else {
                pause();
            }
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void pause() {
        if (this.handler != null) {
            this.handler.pause();
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void resume() {
        if (this.handler != null && this.handler.isPrepared()) {
            this.handler.resume();
        } else {
            restart();
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public boolean isPaused() {
        if (this.handler != null) {
            return this.handler.isStop();
        }
        return false;
    }

    public void restart() {
        stop();
        start();
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void start() {
        start(0L);
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void start(long postion) {
        if (this.handler == null) {
            prepare();
        } else {
            this.handler.removeCallbacksAndMessages(null);
        }
        this.handler.obtainMessage(1, Long.valueOf(postion)).sendToTarget();
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void seekTo(Long ms) {
        if (this.handler != null) {
            this.handler.seekTo(ms);
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void enableDanmakuDrawingCache(boolean enable) {
        this.mEnableDanmakuDrwaingCache = enable;
    }

    @Override // master.flame.danmaku.controller.IDanmakuView, master.flame.danmaku.controller.IDanmakuViewController
    public boolean isDanmakuDrawingCacheEnabled() {
        return this.mEnableDanmakuDrwaingCache;
    }

    @Override // master.flame.danmaku.controller.IDanmakuViewController
    public boolean isViewReady() {
        return this.isSurfaceCreated;
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public View getView() {
        return this;
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void show() {
        showAndResumeDrawTask(null);
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void showAndResumeDrawTask(Long position) {
        this.mDanmakuVisible = true;
        if (this.handler != null) {
            this.handler.showDanmakus(position);
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void hide() {
        this.mDanmakuVisible = false;
        if (this.handler != null) {
            this.handler.hideDanmakus(false);
        }
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public long hideAndPauseDrawTask() {
        this.mDanmakuVisible = false;
        if (this.handler == null) {
            return 0L;
        }
        return this.handler.hideDanmakus(true);
    }

    @Override // master.flame.danmaku.controller.IDanmakuViewController
    public void clear() {
        Canvas canvas;
        if (isViewReady() && (canvas = this.mSurfaceHolder.lockCanvas()) != null) {
            DrawHelper.clearCanvas(canvas);
            this.mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override // android.view.View, master.flame.danmaku.controller.IDanmakuView
    public boolean isShown() {
        return this.handler != null && isViewReady() && this.handler.getVisibility();
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void setDrawingThreadType(int type) {
        this.mDrawingThreadType = type;
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public long getCurrentTime() {
        if (this.handler != null) {
            return this.handler.getCurrentTime();
        }
        return 0L;
    }

    @Override // android.view.View, master.flame.danmaku.controller.IDanmakuView, master.flame.danmaku.controller.IDanmakuViewController
    public boolean isHardwareAccelerated() {
        return false;
    }

    @Override // master.flame.danmaku.controller.IDanmakuView
    public void clearDanmakusOnScreen() {
        if (this.handler != null) {
            this.handler.clearDanmakusOnScreen();
        }
    }
}
