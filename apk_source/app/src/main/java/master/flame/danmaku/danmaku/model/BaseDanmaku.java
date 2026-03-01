package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public abstract class BaseDanmaku {
    public static final String DANMAKU_BR_CHAR = "/n";
    public static final int INVISIBLE = 0;
    public static final int TYPE_FIX_BOTTOM = 4;
    public static final int TYPE_FIX_TOP = 5;
    public static final int TYPE_MOVEABLE_XXX = 0;
    public static final int TYPE_SCROLL_LR = 6;
    public static final int TYPE_SCROLL_RL = 1;
    public static final int TYPE_SPECIAL = 7;
    public static final int VISIBLE = 1;
    public IDrawingCache<?> cache;
    public Duration duration;
    public int index;
    public boolean isGuest;
    public boolean isLive;
    public String[] lines;
    protected DanmakuTimer mTimer;
    public float rotationY;
    public float rotationZ;
    public String text;
    public int textColor;
    public int textShadowColor;
    public long time;
    public String userHash;
    public int visibility;
    public int underlineColor = 0;
    public float textSize = -1.0f;
    public int borderColor = 0;
    public int padding = 0;
    public byte priority = 0;
    public float paintWidth = -1.0f;
    public float paintHeight = -1.0f;
    private int visibleResetFlag = 0;
    private int measureResetFlag = 0;
    public int userId = 0;
    protected int alpha = AlphaValue.MAX;

    public abstract float getBottom();

    public abstract float getLeft();

    public abstract float[] getRectAtTime(IDisplayer iDisplayer, long j);

    public abstract float getRight();

    public abstract float getTop();

    public abstract int getType();

    public abstract void layout(IDisplayer iDisplayer, float f, float f2);

    public long getDuration() {
        return this.duration.value;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int draw(IDisplayer displayer) {
        return displayer.draw(this);
    }

    public boolean isMeasured() {
        return this.paintWidth >= 0.0f && this.paintHeight >= 0.0f && this.measureResetFlag == GlobalFlagValues.MEASURE_RESET_FLAG;
    }

    public void measure(IDisplayer displayer) {
        displayer.measure(this);
        this.measureResetFlag = GlobalFlagValues.MEASURE_RESET_FLAG;
    }

    public boolean hasDrawingCache() {
        return (this.cache == null || this.cache.get() == null) ? false : true;
    }

    public boolean isShown() {
        return this.visibility == 1 && this.visibleResetFlag == GlobalFlagValues.VISIBLE_RESET_FLAG;
    }

    public boolean isTimeOut() {
        return this.mTimer == null || isTimeOut(this.mTimer.currMillisecond);
    }

    public boolean isTimeOut(long ctime) {
        return ctime - this.time >= this.duration.value;
    }

    public boolean isOutside() {
        return this.mTimer == null || isOutside(this.mTimer.currMillisecond);
    }

    public boolean isOutside(long ctime) {
        long dtime = ctime - this.time;
        return dtime <= 0 || dtime >= this.duration.value;
    }

    public boolean isLate() {
        return this.mTimer == null || this.mTimer.currMillisecond < this.time;
    }

    public void setVisibility(boolean b) {
        if (b) {
            this.visibleResetFlag = GlobalFlagValues.VISIBLE_RESET_FLAG;
            this.visibility = 1;
        } else {
            this.visibility = 0;
        }
    }

    public DanmakuTimer getTimer() {
        return this.mTimer;
    }

    public void setTimer(DanmakuTimer timer) {
        this.mTimer = timer;
    }

    public int getAlpha() {
        return this.alpha;
    }
}
