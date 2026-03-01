package master.flame.danmaku.danmaku.model.objectpool;

import master.flame.danmaku.danmaku.model.objectpool.Poolable;

/* loaded from: classes.dex */
class FinitePool<T extends Poolable<T>> implements Pool<T> {
    private final boolean mInfinite;
    private final int mLimit;
    private final PoolableManager<T> mManager;
    private int mPoolCount;
    private T mRoot;

    FinitePool(PoolableManager<T> manager) {
        this.mManager = manager;
        this.mLimit = 0;
        this.mInfinite = true;
    }

    FinitePool(PoolableManager<T> manager, int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("The pool limit must be > 0");
        }
        this.mManager = manager;
        this.mLimit = limit;
        this.mInfinite = false;
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.Pool
    public T acquire() {
        T t;
        if (this.mRoot != null) {
            t = this.mRoot;
            this.mRoot = (T) t.getNextPoolable();
            this.mPoolCount--;
        } else {
            t = (T) this.mManager.newInstance();
        }
        if (t != null) {
            t.setNextPoolable(null);
            t.setPooled(false);
            this.mManager.onAcquired(t);
        }
        return t;
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.Pool
    public void release(T element) {
        if (!element.isPooled()) {
            if (this.mInfinite || this.mPoolCount < this.mLimit) {
                this.mPoolCount++;
                element.setNextPoolable(this.mRoot);
                element.setPooled(true);
                this.mRoot = element;
            }
            this.mManager.onReleased(element);
            return;
        }
        System.out.print("[FinitePool] Element is already in pool: " + element);
    }
}
