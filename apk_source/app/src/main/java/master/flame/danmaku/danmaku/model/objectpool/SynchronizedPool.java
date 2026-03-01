package master.flame.danmaku.danmaku.model.objectpool;

import master.flame.danmaku.danmaku.model.objectpool.Poolable;

/* loaded from: classes.dex */
class SynchronizedPool<T extends Poolable<T>> implements Pool<T> {
    private final Object mLock;
    private final Pool<T> mPool;

    public SynchronizedPool(Pool<T> pool) {
        this.mPool = pool;
        this.mLock = this;
    }

    public SynchronizedPool(Pool<T> pool, Object lock) {
        this.mPool = pool;
        this.mLock = lock;
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.Pool
    public T acquire() {
        T t;
        synchronized (this.mLock) {
            t = (T) this.mPool.acquire();
        }
        return t;
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.Pool
    public void release(T element) {
        synchronized (this.mLock) {
            this.mPool.release(element);
        }
    }
}
