package master.flame.danmaku.danmaku.renderer.android;

import master.flame.danmaku.controller.DanmakuFilters;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakuIterator;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.renderer.IRenderer;
import master.flame.danmaku.danmaku.renderer.Renderer;

/* loaded from: classes.dex */
public class DanmakuRenderer extends Renderer {
    private final DanmakuTimer mStartTimer = new DanmakuTimer();
    private final IRenderer.RenderingState mRenderingState = new IRenderer.RenderingState();

    @Override // master.flame.danmaku.danmaku.renderer.IRenderer
    public void clear() {
        DanmakusRetainer.clear();
        DanmakuFilters.getDefault().clear();
    }

    @Override // master.flame.danmaku.danmaku.renderer.IRenderer
    public void release() {
        DanmakusRetainer.release();
        DanmakuFilters.getDefault().release();
    }

    @Override // master.flame.danmaku.danmaku.renderer.IRenderer
    public IRenderer.RenderingState draw(IDisplayer disp, IDanmakus danmakus, long startRenderTime) {
        int lastTotalDanmakuCount = this.mRenderingState.totalDanmakuCount;
        this.mRenderingState.reset();
        IDanmakuIterator itr = danmakus.iterator();
        int orderInScreen = 0;
        this.mStartTimer.update(System.currentTimeMillis());
        int sizeInScreen = danmakus.size();
        BaseDanmaku drawItem = null;
        while (itr.hasNext()) {
            drawItem = itr.next();
            if (drawItem.isLate()) {
                break;
            }
            if (drawItem.time >= startRenderTime && (drawItem.priority != 0 || !DanmakuFilters.getDefault().filter(drawItem, orderInScreen, sizeInScreen, this.mStartTimer, false))) {
                if (drawItem.getType() == 1) {
                    orderInScreen++;
                }
                if (!drawItem.isMeasured()) {
                    drawItem.measure(disp);
                }
                DanmakusRetainer.fix(drawItem, disp);
                if (!drawItem.isOutside() && drawItem.isShown() && (drawItem.lines != null || drawItem.getBottom() <= disp.getHeight())) {
                    int renderingType = drawItem.draw(disp);
                    if (renderingType == 1) {
                        this.mRenderingState.cacheHitCount++;
                    } else if (renderingType == 2) {
                        this.mRenderingState.cacheMissCount++;
                    }
                    this.mRenderingState.addCount(drawItem.getType(), 1);
                    this.mRenderingState.addTotalCount(1);
                }
            }
        }
        this.mRenderingState.nothingRendered = this.mRenderingState.totalDanmakuCount == 0;
        this.mRenderingState.endTime = drawItem != null ? drawItem.time : -1L;
        if (this.mRenderingState.nothingRendered) {
            this.mRenderingState.beginTime = -1L;
        }
        this.mRenderingState.incrementCount = this.mRenderingState.totalDanmakuCount - lastTotalDanmakuCount;
        this.mRenderingState.consumingTime = this.mStartTimer.update(System.currentTimeMillis());
        return this.mRenderingState;
    }
}
