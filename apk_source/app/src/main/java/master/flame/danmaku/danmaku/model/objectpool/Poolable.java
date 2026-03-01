package master.flame.danmaku.danmaku.model.objectpool;

/* loaded from: classes.dex */
public interface Poolable<T> {
    T getNextPoolable();

    boolean isPooled();

    void setNextPoolable(T t);

    void setPooled(boolean z);
}
