package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public class FTDanmaku extends BaseDanmaku {
    private int mLastDispWidth;
    private float mLastLeft;
    private float mLastPaintWidth;
    private float x = 0.0f;
    protected float y = -1.0f;
    private float[] RECT = null;

    public FTDanmaku(Duration duration) {
        this.duration = duration;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public void layout(IDisplayer displayer, float x, float y) {
        if (this.mTimer != null) {
            long deltaDuration = this.mTimer.currMillisecond - this.time;
            if (deltaDuration > 0 && deltaDuration < this.duration.value) {
                if (!isShown()) {
                    this.x = getLeft(displayer);
                    this.y = y;
                    setVisibility(true);
                    return;
                }
                return;
            }
            setVisibility(false);
            this.y = -1.0f;
            this.x = displayer.getWidth();
        }
    }

    protected float getLeft(IDisplayer displayer) {
        if (this.mLastDispWidth == displayer.getWidth() && this.mLastPaintWidth == this.paintWidth) {
            return this.mLastLeft;
        }
        float left = (displayer.getWidth() - this.paintWidth) / 2.0f;
        this.mLastDispWidth = displayer.getWidth();
        this.mLastPaintWidth = this.paintWidth;
        this.mLastLeft = left;
        return left;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float[] getRectAtTime(IDisplayer displayer, long time) {
        if (!isMeasured()) {
            return null;
        }
        float left = getLeft(displayer);
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
        return 5;
    }
}
