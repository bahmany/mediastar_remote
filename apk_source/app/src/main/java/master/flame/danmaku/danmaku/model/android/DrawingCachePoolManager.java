package master.flame.danmaku.danmaku.model.android;

import master.flame.danmaku.danmaku.model.objectpool.PoolableManager;

/* loaded from: classes.dex */
public class DrawingCachePoolManager implements PoolableManager<DrawingCache> {
    @Override // master.flame.danmaku.danmaku.model.objectpool.PoolableManager
    public DrawingCache newInstance() {
        return null;
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.PoolableManager
    public void onAcquired(DrawingCache element) {
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.PoolableManager
    public void onReleased(DrawingCache element) {
    }
}
