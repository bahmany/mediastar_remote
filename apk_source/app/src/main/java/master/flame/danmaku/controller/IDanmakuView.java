package master.flame.danmaku.controller;

import android.view.View;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/* loaded from: classes.dex */
public interface IDanmakuView {
    public static final int THREAD_TYPE_HIGH_PRIORITY = 2;
    public static final int THREAD_TYPE_LOW_PRIORITY = 3;
    public static final int THREAD_TYPE_MAIN_THREAD = 1;
    public static final int THREAD_TYPE_NORMAL_PRIORITY = 0;

    void addDanmaku(BaseDanmaku baseDanmaku);

    void clearDanmakusOnScreen();

    void enableDanmakuDrawingCache(boolean z);

    long getCurrentTime();

    int getHeight();

    View getView();

    int getWidth();

    void hide();

    long hideAndPauseDrawTask();

    boolean isDanmakuDrawingCacheEnabled();

    boolean isHardwareAccelerated();

    boolean isPaused();

    boolean isPrepared();

    boolean isShown();

    void pause();

    void prepare(BaseDanmakuParser baseDanmakuParser);

    void release();

    void removeAllDanmakus();

    void removeAllLiveDanmakus();

    void resume();

    void seekTo(Long l);

    void setCallback(DrawHandler.Callback callback);

    void setDrawingThreadType(int i);

    void setVisibility(int i);

    void show();

    void showAndResumeDrawTask(Long l);

    void showFPS(boolean z);

    void start();

    void start(long j);

    void stop();

    void toggle();
}
