package master.flame.danmaku.danmaku.model.android;

import master.flame.danmaku.danmaku.model.IDrawingCache;
import master.flame.danmaku.danmaku.model.objectpool.Poolable;

/* loaded from: classes.dex */
public class DrawingCache implements IDrawingCache<DrawingCacheHolder>, Poolable<DrawingCache> {
    private boolean mIsPooled;
    private DrawingCache mNextElement;
    private int mSize = 0;
    private int referenceCount = 0;
    private DrawingCacheHolder mHolder = new DrawingCacheHolder();

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public void build(int w, int h, int density, boolean checkSizeEquals) {
        DrawingCacheHolder holder = this.mHolder;
        if (holder == null) {
            holder = new DrawingCacheHolder(w, h, density);
        } else {
            holder.buildCache(w, h, density, checkSizeEquals);
        }
        this.mHolder = holder;
        this.mSize = this.mHolder.bitmap.getRowBytes() * this.mHolder.bitmap.getHeight();
    }

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public void erase() {
        DrawingCacheHolder holder = this.mHolder;
        if (holder != null) {
            holder.erase();
        }
    }

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public DrawingCacheHolder get() {
        if (this.mHolder == null || this.mHolder.bitmap == null) {
            return null;
        }
        return this.mHolder;
    }

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public void destroy() {
        if (this.mHolder != null) {
            this.mHolder.recycle();
        }
        this.mSize = 0;
        this.referenceCount = 0;
    }

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public int size() {
        if (this.mHolder != null) {
            return this.mSize;
        }
        return 0;
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.Poolable
    public void setNextPoolable(DrawingCache element) {
        this.mNextElement = element;
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.Poolable
    public DrawingCache getNextPoolable() {
        return this.mNextElement;
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.Poolable
    public boolean isPooled() {
        return this.mIsPooled;
    }

    @Override // master.flame.danmaku.danmaku.model.objectpool.Poolable
    public void setPooled(boolean isPooled) {
        this.mIsPooled = isPooled;
    }

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public boolean hasReferences() {
        return this.referenceCount > 0;
    }

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public void increaseReference() {
        this.referenceCount++;
    }

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public void decreaseReference() {
        this.referenceCount--;
    }

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public int width() {
        if (this.mHolder != null) {
            return this.mHolder.width;
        }
        return 0;
    }

    @Override // master.flame.danmaku.danmaku.model.IDrawingCache
    public int height() {
        if (this.mHolder != null) {
            return this.mHolder.height;
        }
        return 0;
    }
}
