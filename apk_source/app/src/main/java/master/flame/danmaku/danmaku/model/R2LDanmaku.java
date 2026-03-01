package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public class R2LDanmaku extends BaseDanmaku {
    protected static final long CORDON_RENDERING_TIME = 40;
    protected static final long MAX_RENDERING_TIME = 100;
    protected int mDistance;
    protected long mLastTime;
    protected float mStepX;
    protected float x = 0.0f;
    protected float y = -1.0f;
    protected float[] RECT = null;

    public R2LDanmaku(Duration duration) {
        this.duration = duration;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public void layout(IDisplayer displayer, float x, float y) {
        if (this.mTimer != null) {
            long currMS = this.mTimer.currMillisecond;
            long deltaDuration = currMS - this.time;
            if (deltaDuration > 0 && deltaDuration < this.duration.value) {
                this.x = getAccurateLeft(displayer, currMS);
                if (!isShown()) {
                    this.y = y;
                    setVisibility(true);
                }
                this.mLastTime = currMS;
                return;
            }
            this.mLastTime = currMS;
        }
        setVisibility(false);
    }

    protected float getAccurateLeft(IDisplayer displayer, long currTime) {
        long elapsedTime = currTime - this.time;
        return elapsedTime >= this.duration.value ? -this.paintWidth : displayer.getWidth() - (elapsedTime * this.mStepX);
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float[] getRectAtTime(IDisplayer displayer, long time) {
        if (!isMeasured()) {
            return null;
        }
        float left = getAccurateLeft(displayer, time);
        if (this.RECT == null) {
            this.RECT = new float[4];
        }
        this.RECT[0] = left;
        this.RECT[1] = this.y;
        this.RECT[2] = this.paintWidth + left;
        this.RECT[3] = this.y + this.paintHeight;
        return this.RECT;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getLeft() {
        return this.x;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getTop() {
        return this.y;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getRight() {
        return this.x + this.paintWidth;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getBottom() {
        return this.y + this.paintHeight;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public int getType() {
        return 1;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public void measure(IDisplayer displayer) {
        super.measure(displayer);
        this.mDistance = (int) (displayer.getWidth() + this.paintWidth);
        this.mStepX = this.mDistance / this.duration.value;
    }
}
