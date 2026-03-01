package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public class Duration {
    private float factor = 1.0f;
    private long mInitialDuration;
    public long value;

    public Duration(long initialDuration) {
        this.mInitialDuration = initialDuration;
        this.value = initialDuration;
    }

    public void setValue(long initialDuration) {
        this.mInitialDuration = initialDuration;
        this.value = (long) (this.mInitialDuration * this.factor);
    }

    public void setFactor(float f) {
        if (this.factor != f) {
            this.factor = f;
            this.value = (long) (this.mInitialDuration * f);
        }
    }
}
