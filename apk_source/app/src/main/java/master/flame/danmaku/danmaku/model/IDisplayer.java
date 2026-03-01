package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public interface IDisplayer {
    int draw(BaseDanmaku baseDanmaku);

    float getDensity();

    int getDensityDpi();

    int getHeight();

    int getMaximumCacheHeight();

    int getMaximumCacheWidth();

    float getScaledDensity();

    int getSlopPixel();

    float getStrokeWidth();

    int getWidth();

    boolean isHardwareAccelerated();

    void measure(BaseDanmaku baseDanmaku);

    void resetSlopPixel(float f);

    void setDensities(float f, int i, float f2);

    void setHardwareAccelerated(boolean z);

    void setSize(int i, int i2);
}
