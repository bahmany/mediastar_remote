package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public class L2RDanmaku extends R2LDanmaku {
    public L2RDanmaku(Duration duration) {
        super(duration);
    }

    @Override // master.flame.danmaku.danmaku.model.R2LDanmaku, master.flame.danmaku.danmaku.model.BaseDanmaku
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

    @Override // master.flame.danmaku.danmaku.model.R2LDanmaku, master.flame.danmaku.danmaku.model.BaseDanmaku
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

    @Override // master.flame.danmaku.danmaku.model.R2LDanmaku
    protected float getAccurateLeft(IDisplayer displayer, long currTime) {
        long elapsedTime = currTime - this.time;
        return elapsedTime >= this.duration.value ? displayer.getWidth() : (this.mStepX * elapsedTime) - this.paintWidth;
    }

    @Override // master.flame.danmaku.danmaku.model.R2LDanmaku, master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getLeft() {
        return this.x;
    }

    @Override // master.flame.danmaku.danmaku.model.R2LDanmaku, master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getTop() {
        return this.y;
    }

    @Override // master.flame.danmaku.danmaku.model.R2LDanmaku, master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getRight() {
        return this.x + this.paintWidth;
    }

    @Override // master.flame.danmaku.danmaku.model.R2LDanmaku, master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getBottom() {
        return this.y + this.paintHeight;
    }

    @Override // master.flame.danmaku.danmaku.model.R2LDanmaku, master.flame.danmaku.danmaku.model.BaseDanmaku
    public int getType() {
        return 6;
    }
}
