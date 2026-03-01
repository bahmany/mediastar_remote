package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public interface IDanmakuIterator {
    boolean hasNext();

    BaseDanmaku next();

    void remove();

    void reset();
}
