package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public abstract class AbsDisplayer<T> implements IDisplayer {
    public abstract T getExtraData();

    public abstract void setExtraData(T t);

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public boolean isHardwareAccelerated() {
        return false;
    }
}
