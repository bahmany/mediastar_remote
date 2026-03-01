package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public interface IDrawingCache<T> {
    void build(int i, int i2, int i3, boolean z);

    void decreaseReference();

    void destroy();

    void erase();

    T get();

    boolean hasReferences();

    int height();

    void increaseReference();

    int size();

    int width();
}
