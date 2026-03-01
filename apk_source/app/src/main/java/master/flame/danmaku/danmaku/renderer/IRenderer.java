package master.flame.danmaku.danmaku.renderer;

import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;

/* loaded from: classes.dex */
public interface IRenderer {
    public static final int CACHE_RENDERING = 1;
    public static final int NOTHING_RENDERING = 0;
    public static final int TEXT_RENDERING = 2;

    void clear();

    RenderingState draw(IDisplayer iDisplayer, IDanmakus iDanmakus, long j);

    void release();

    public static class Area {
        private int mMaxHeight;
        private int mMaxWidth;
        public final float[] mRefreshRect = new float[4];

        public void setEdge(int maxWidth, int maxHeight) {
            this.mMaxWidth = maxWidth;
            this.mMaxHeight = maxHeight;
        }

        public void reset() {
            set(this.mMaxWidth, this.mMaxHeight, 0.0f, 0.0f);
        }

        public void resizeToMax() {
            set(0.0f, 0.0f, this.mMaxWidth, this.mMaxHeight);
        }

        public void set(float left, float top, float right, float bottom) {
            this.mRefreshRect[0] = left;
            this.mRefreshRect[1] = top;
            this.mRefreshRect[2] = right;
            this.mRefreshRect[3] = bottom;
        }
    }

    public static class RenderingState {
        public static final int UNKNOWN_TIME = -1;
        public long beginTime;
        public long cacheHitCount;
        public long cacheMissCount;
        public long consumingTime;
        public long endTime;
        public int fbDanmakuCount;
        public int ftDanmakuCount;
        public int incrementCount;
        public int l2rDanmakuCount;
        public boolean nothingRendered;
        public int r2lDanmakuCount;
        public int specialDanmakuCount;
        public long sysTime;
        public int totalDanmakuCount;

        public int addTotalCount(int count) {
            this.totalDanmakuCount += count;
            return this.totalDanmakuCount;
        }

        public int addCount(int type, int count) {
            switch (type) {
                case 1:
                    this.r2lDanmakuCount += count;
                    return this.r2lDanmakuCount;
                case 2:
                case 3:
                default:
                    return 0;
                case 4:
                    this.fbDanmakuCount += count;
                    return this.fbDanmakuCount;
                case 5:
                    this.ftDanmakuCount += count;
                    return this.ftDanmakuCount;
                case 6:
                    this.l2rDanmakuCount += count;
                    return this.l2rDanmakuCount;
                case 7:
                    this.specialDanmakuCount += count;
                    return this.specialDanmakuCount;
            }
        }

        public void reset() {
            this.totalDanmakuCount = 0;
            this.specialDanmakuCount = 0;
            this.fbDanmakuCount = 0;
            this.ftDanmakuCount = 0;
            this.l2rDanmakuCount = 0;
            this.r2lDanmakuCount = 0;
            this.consumingTime = 0L;
            this.endTime = 0L;
            this.beginTime = 0L;
            this.sysTime = 0L;
            this.nothingRendered = false;
        }

        public void set(RenderingState other) {
            if (other != null) {
                this.r2lDanmakuCount = other.r2lDanmakuCount;
                this.l2rDanmakuCount = other.l2rDanmakuCount;
                this.ftDanmakuCount = other.ftDanmakuCount;
                this.fbDanmakuCount = other.fbDanmakuCount;
                this.specialDanmakuCount = other.specialDanmakuCount;
                this.totalDanmakuCount = other.totalDanmakuCount;
                this.incrementCount = other.incrementCount;
                this.consumingTime = other.consumingTime;
                this.beginTime = other.beginTime;
                this.endTime = other.endTime;
                this.nothingRendered = other.nothingRendered;
                this.sysTime = other.sysTime;
                this.cacheHitCount = other.cacheHitCount;
                this.cacheMissCount = other.cacheMissCount;
            }
        }
    }
}
