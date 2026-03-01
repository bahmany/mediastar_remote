package master.flame.danmaku.controller;

import android.content.Context;
import android.graphics.Canvas;
import master.flame.danmaku.controller.IDrawTask;
import master.flame.danmaku.danmaku.model.AbsDisplayer;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.GlobalFlagValues;
import master.flame.danmaku.danmaku.model.IDanmakuIterator;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.renderer.IRenderer;
import master.flame.danmaku.danmaku.renderer.android.DanmakuRenderer;
import master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer;

/* loaded from: classes.dex */
public class DrawTask implements IDrawTask, DanmakuGlobalConfig.ConfigChangedCallback {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected boolean clearRetainerFlag;
    protected IDanmakus danmakuList;
    Context mContext;
    protected AbsDisplayer<?> mDisp;
    private boolean mIsHidden;
    private long mLastBeginMills;
    private long mLastEndMills;
    protected BaseDanmakuParser mParser;
    protected boolean mReadyState;
    IDrawTask.TaskListener mTaskListener;
    DanmakuTimer mTimer;
    private IDanmakus danmakus = new Danmakus(4);
    private long mStartRenderTime = 0;
    private IRenderer.RenderingState mRenderingState = new IRenderer.RenderingState();
    IRenderer mRenderer = new DanmakuRenderer();

    static {
        $assertionsDisabled = !DrawTask.class.desiredAssertionStatus();
    }

    public DrawTask(DanmakuTimer timer, Context context, AbsDisplayer<?> disp, IDrawTask.TaskListener taskListener) throws Exception {
        this.mTaskListener = taskListener;
        this.mContext = context;
        this.mDisp = disp;
        initTimer(timer);
        Boolean enable = Boolean.valueOf(DanmakuGlobalConfig.DEFAULT.isDuplicateMergingEnabled());
        if (enable != null) {
            if (enable.booleanValue()) {
                DanmakuFilters.getDefault().registerFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
            } else {
                DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
            }
        }
    }

    protected void initTimer(DanmakuTimer timer) {
        this.mTimer = timer;
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void addDanmaku(BaseDanmaku item) {
        boolean added;
        if (this.danmakuList != null) {
            synchronized (this.danmakuList) {
                if (item.isLive) {
                    removeUnusedLiveDanmakusIn(10);
                }
                item.index = this.danmakuList.size();
                if (this.mLastBeginMills <= item.time && item.time <= this.mLastEndMills) {
                    synchronized (this.danmakus) {
                        this.danmakus.addItem(item);
                    }
                } else if (item.isLive) {
                    this.mLastEndMills = 0L;
                    this.mLastBeginMills = 0L;
                }
                added = this.danmakuList.addItem(item);
            }
            if (added && this.mTaskListener != null) {
                this.mTaskListener.onDanmakuAdd(item);
            }
        }
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void removeAllDanmakus() {
        if (this.danmakuList != null && !this.danmakuList.isEmpty()) {
            synchronized (this.danmakuList) {
                this.danmakuList.clear();
            }
        }
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void removeAllLiveDanmakus() {
        if (this.danmakus != null && !this.danmakus.isEmpty()) {
            synchronized (this.danmakus) {
                IDanmakuIterator it = this.danmakus.iterator();
                while (it.hasNext()) {
                    if (it.next().isLive) {
                        it.remove();
                    }
                }
            }
        }
    }

    protected void removeUnusedLiveDanmakusIn(int msec) {
        if (this.danmakuList != null && !this.danmakuList.isEmpty()) {
            synchronized (this.danmakuList) {
                long startTime = System.currentTimeMillis();
                IDanmakuIterator it = this.danmakuList.iterator();
                while (it.hasNext()) {
                    BaseDanmaku danmaku = it.next();
                    boolean isTimeout = danmaku.isTimeOut();
                    if (isTimeout && danmaku.isLive) {
                        it.remove();
                    }
                    if (!isTimeout || System.currentTimeMillis() - startTime > msec) {
                        break;
                    }
                }
            }
        }
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public IRenderer.RenderingState draw(AbsDisplayer<?> displayer) {
        return drawDanmakus(displayer, this.mTimer);
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void reset() {
        if (this.danmakus != null) {
            this.danmakus.clear();
        }
        if (this.mRenderer != null) {
            this.mRenderer.clear();
        }
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void seek(long mills) {
        reset();
        GlobalFlagValues.updateVisibleFlag();
        if (mills < 1000) {
            mills = 0;
        }
        this.mStartRenderTime = mills;
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void clearDanmakusOnScreen(long currMillis) {
        reset();
        GlobalFlagValues.updateVisibleFlag();
        this.mStartRenderTime = currMillis;
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void start() {
        DanmakuGlobalConfig.DEFAULT.registerConfigChangedCallback(this);
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void quit() {
        if (this.mRenderer != null) {
            this.mRenderer.release();
        }
        DanmakuGlobalConfig.DEFAULT.unregisterConfigChangedCallback(this);
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void prepare() {
        if (!$assertionsDisabled && this.mParser == null) {
            throw new AssertionError();
        }
        loadDanmakus(this.mParser);
        if (this.mTaskListener != null) {
            this.mTaskListener.ready();
            this.mReadyState = true;
        }
    }

    protected void loadDanmakus(BaseDanmakuParser parser) {
        this.danmakuList = parser.setDisplayer(this.mDisp).setTimer(this.mTimer).getDanmakus();
        GlobalFlagValues.resetAll();
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void setParser(BaseDanmakuParser parser) {
        this.mParser = parser;
        this.mReadyState = false;
    }

    protected IRenderer.RenderingState drawDanmakus(AbsDisplayer<?> disp, DanmakuTimer timer) {
        if (this.clearRetainerFlag) {
            DanmakusRetainer.clear();
            this.clearRetainerFlag = false;
        }
        if (this.danmakuList != null) {
            Canvas canvas = (Canvas) disp.getExtraData();
            DrawHelper.clearCanvas(canvas);
            if (this.mIsHidden) {
                return this.mRenderingState;
            }
            long beginMills = (timer.currMillisecond - DanmakuFactory.MAX_DANMAKU_DURATION) - 100;
            long endMills = timer.currMillisecond + DanmakuFactory.MAX_DANMAKU_DURATION;
            if (this.mLastBeginMills > beginMills || timer.currMillisecond > this.mLastEndMills) {
                IDanmakus subDanmakus = this.danmakuList.sub(beginMills, endMills);
                if (subDanmakus != null) {
                    this.danmakus = subDanmakus;
                } else {
                    this.danmakus.clear();
                }
                this.mLastBeginMills = beginMills;
                this.mLastEndMills = endMills;
            } else {
                beginMills = this.mLastBeginMills;
                endMills = this.mLastEndMills;
            }
            if (this.danmakus != null && !this.danmakus.isEmpty()) {
                IRenderer.RenderingState renderingState = this.mRenderer.draw(this.mDisp, this.danmakus, this.mStartRenderTime);
                this.mRenderingState = renderingState;
                if (!renderingState.nothingRendered) {
                    return renderingState;
                }
                if (renderingState.beginTime == -1) {
                    renderingState.beginTime = beginMills;
                }
                if (renderingState.endTime == -1) {
                    renderingState.endTime = endMills;
                    return renderingState;
                }
                return renderingState;
            }
            this.mRenderingState.nothingRendered = true;
            this.mRenderingState.beginTime = beginMills;
            this.mRenderingState.endTime = endMills;
            return this.mRenderingState;
        }
        return null;
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void requestClear() {
        this.mLastEndMills = 0L;
        this.mLastBeginMills = 0L;
        this.mIsHidden = false;
    }

    public void requestClearRetainer() {
        this.clearRetainerFlag = true;
    }

    public boolean onDanmakuConfigChanged(DanmakuGlobalConfig config, DanmakuGlobalConfig.DanmakuConfigTag tag, Object... values) throws Exception {
        boolean handled = handleOnDanmakuConfigChanged(config, tag, values);
        if (this.mTaskListener != null) {
            this.mTaskListener.onDanmakuConfigChanged();
        }
        return handled;
    }

    protected boolean handleOnDanmakuConfigChanged(DanmakuGlobalConfig config, DanmakuGlobalConfig.DanmakuConfigTag tag, Object[] values) throws Exception {
        if (tag == null || DanmakuGlobalConfig.DanmakuConfigTag.MAXIMUM_NUMS_IN_SCREEN.equals(tag)) {
            return true;
        }
        if (DanmakuGlobalConfig.DanmakuConfigTag.DUPLICATE_MERGING_ENABLED.equals(tag)) {
            Boolean enable = (Boolean) values[0];
            if (enable == null) {
                return false;
            }
            if (enable.booleanValue()) {
                DanmakuFilters.getDefault().registerFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
            } else {
                DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
            }
            return true;
        }
        if (!DanmakuGlobalConfig.DanmakuConfigTag.SCALE_TEXTSIZE.equals(tag) && !DanmakuGlobalConfig.DanmakuConfigTag.SCROLL_SPEED_FACTOR.equals(tag)) {
            return false;
        }
        requestClearRetainer();
        return false;
    }

    @Override // master.flame.danmaku.controller.IDrawTask
    public void requestHide() {
        this.mIsHidden = true;
    }
}
