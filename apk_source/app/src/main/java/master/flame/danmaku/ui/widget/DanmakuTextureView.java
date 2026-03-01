package master.flame.danmaku.ui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.TextureView;
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

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public class DanmakuTextureView extends TextureView implements IDanmakuView, IDanmakuViewController, TextureView.SurfaceTextureListener {
    private static final int MAX_RECORD_SIZE = 50;
    private static final int ONE_SECOND = 1000;
    public static final String TAG = "DanmakuTextureView";
    private DrawHandler handler;
    private boolean isSurfaceCreated;
    private DrawHandler.Callback mCallback;
    private boolean mDanmakuVisible;
    private LinkedList<Long> mDrawTimes;
    protected int mDrawingThreadType;
    private boolean mEnableDanmakuDrwaingCache;
    private HandlerThread mHandlerThread;
    private boolean mShowFps;

    public DanmakuTextureView(Context context) {
        super(context);
        this.mEnableDanmakuDrwaingCache = true;
        this.mDanmakuVisible = true;
        this.mDrawingThreadType = 0;
        init();
    }

    @TargetApi(11)
    private void init() {
        setLayerType(2, null);
        setOpaque(false);
        setWillNotCacheDrawing(true);
        setDrawingCacheEnabled(false);
        setWillNotDraw(true);
        setSurfaceTextureListener(this);
        DrawHelper.useDrawColorToClearCanvas(true, true);
    }

    public DanmakuTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mEnableDanmakuDrwaingCache = true;
        this.mDanmakuVisible = true;
        this.mDrawingThreadType = 0;
        init();
    }

    public DanmakuTextureView(Context context, AttributeSet attrs, int defStyle) {
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

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.isSurfaceCreated = true;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public synchronized boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        this.isSurfaceCreated = false;
        return true;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (this.handler != null) {
            this.handler.notifyDispSizeChanged(width, height);
        }
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
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
    public synchronized long drawDanmakus() {
        long dtime;
        if (!this.isSurfaceCreated) {
            dtime = 0;
        } else {
            long stime = System.currentTimeMillis();
            if (!isShown()) {
                dtime = -1;
            } else {
                Canvas canvas = lockCanvas();
                if (canvas != null) {
                    if (this.handler != null) {
                        IRenderer.RenderingState rs = this.handler.draw(canvas);
                        if (this.mShowFps) {
                            if (this.mDrawTimes == null) {
                                this.mDrawTimes = new LinkedList<>();
                            }
                            long dtime2 = System.currentTimeMillis() - stime;
                            String fps = String.format(Locale.getDefault(), "fps %.2f,time:%d s,cache:%d,miss:%d", Float.valueOf(fps()), Long.valueOf(getCurrentTime() / 1000), Long.valueOf(rs.cacheHitCount), Long.valueOf(rs.cacheMissCount));
                            DrawHelper.drawFPS(canvas, fps);
                        }
                    }
                    if (this.isSurfaceCreated) {
                        unlockCanvasAndPost(canvas);
                    }
                }
                dtime = System.currentTimeMillis() - stime;
            }
        }
        return dtime;
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
        if (this.handler != null && this.mHandlerThread != null && this.handler.isPrepared()) {
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
    public synchronized void clear() {
        Canvas canvas;
        if (isViewReady() && (canvas = lockCanvas()) != null) {
            DrawHelper.clearCanvas(canvas);
            unlockCanvasAndPost(canvas);
        }
    }

    @Override // android.view.View, master.flame.danmaku.controller.IDanmakuView
    public boolean isShown() {
        if (this.handler == null || !isViewReady()) {
            return false;
        }
        return this.handler.getVisibility();
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
