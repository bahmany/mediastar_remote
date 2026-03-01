package master.flame.danmaku.danmaku.model.android;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Danmaku;
import master.flame.danmaku.danmaku.model.IDanmakuIterator;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

/* loaded from: classes.dex */
public class Danmakus implements IDanmakus {
    public static final int ST_BY_LIST = 4;
    public static final int ST_BY_TIME = 0;
    public static final int ST_BY_YPOS = 1;
    public static final int ST_BY_YPOS_DESC = 2;
    private BaseDanmaku endItem;
    private BaseDanmaku endSubItem;
    public Collection<BaseDanmaku> items;
    private DanmakuIterator iterator;
    private BaseComparator mComparator;
    private boolean mDuplicateMergingEnabled;
    private int mSize;
    private int mSortType;
    private BaseDanmaku startItem;
    private BaseDanmaku startSubItem;
    private Danmakus subItems;

    public Danmakus() {
        this(0, false);
    }

    public Danmakus(int sortType) {
        this(sortType, false);
    }

    public Danmakus(int sortType, boolean duplicateMergingEnabled) {
        this.mSize = 0;
        this.mSortType = 0;
        BaseComparator comparator = null;
        if (sortType == 0) {
            comparator = new TimeComparator(duplicateMergingEnabled);
        } else if (sortType == 1) {
            comparator = new YPosComparator(duplicateMergingEnabled);
        } else if (sortType == 2) {
            comparator = new YPosDescComparator(duplicateMergingEnabled);
        }
        if (sortType == 4) {
            this.items = new LinkedList();
        } else {
            this.mDuplicateMergingEnabled = duplicateMergingEnabled;
            comparator.setDuplicateMergingEnabled(duplicateMergingEnabled);
            this.items = new TreeSet(comparator);
            this.mComparator = comparator;
        }
        this.mSortType = sortType;
        this.mSize = 0;
        this.iterator = new DanmakuIterator(this.items);
    }

    public Danmakus(Collection<BaseDanmaku> items) {
        this.mSize = 0;
        this.mSortType = 0;
        setItems(items);
    }

    public Danmakus(boolean duplicateMergingEnabled) {
        this(0, duplicateMergingEnabled);
    }

    public void setItems(Collection<BaseDanmaku> items) {
        if (this.mDuplicateMergingEnabled && this.mSortType != 4) {
            this.items.clear();
            this.items.addAll(items);
            items = this.items;
        } else {
            this.items = items;
        }
        if (items instanceof List) {
            this.mSortType = 4;
        }
        this.mSize = items == null ? 0 : items.size();
        if (this.iterator == null) {
            this.iterator = new DanmakuIterator(items);
        } else {
            this.iterator.setDatas(items);
        }
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public IDanmakuIterator iterator() {
        this.iterator.reset();
        return this.iterator;
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public boolean addItem(BaseDanmaku item) {
        if (this.items != null) {
            try {
                if (this.items.add(item)) {
                    this.mSize++;
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public boolean removeItem(BaseDanmaku item) {
        if (item == null) {
            return false;
        }
        if (item.isOutside()) {
            item.setVisibility(false);
        }
        if (!this.items.remove(item)) {
            return false;
        }
        this.mSize--;
        return true;
    }

    private Collection<BaseDanmaku> subset(long startTime, long endTime) {
        if (this.mSortType == 4 || this.items == null || this.items.size() == 0) {
            return null;
        }
        if (this.subItems == null) {
            this.subItems = new Danmakus(this.mDuplicateMergingEnabled);
        }
        if (this.startSubItem == null) {
            this.startSubItem = createItem("start");
        }
        if (this.endSubItem == null) {
            this.endSubItem = createItem("end");
        }
        this.startSubItem.time = startTime;
        this.endSubItem.time = endTime;
        return ((SortedSet) this.items).subSet(this.startSubItem, this.endSubItem);
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public IDanmakus subnew(long startTime, long endTime) {
        Collection<BaseDanmaku> subset = subset(startTime, endTime);
        return new Danmakus(subset);
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public IDanmakus sub(long startTime, long endTime) {
        if (this.mSortType == 4 || this.items == null || this.items.size() == 0) {
            return null;
        }
        if (this.subItems == null) {
            this.subItems = new Danmakus(this.mDuplicateMergingEnabled);
        }
        if (this.startItem == null) {
            this.startItem = createItem("start");
        }
        if (this.endItem == null) {
            this.endItem = createItem("end");
        }
        if (this.subItems != null) {
            long dtime = startTime - this.startItem.time;
            if (dtime >= 0 && endTime <= this.endItem.time) {
                return this.subItems;
            }
        }
        this.startItem.time = startTime;
        this.endItem.time = endTime;
        this.subItems.setItems(((SortedSet) this.items).subSet(this.startItem, this.endItem));
        return this.subItems;
    }

    private BaseDanmaku createItem(String text) {
        return new Danmaku(text);
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public int size() {
        return this.mSize;
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public void clear() {
        if (this.items != null) {
            this.items.clear();
            this.mSize = 0;
        }
        if (this.subItems != null) {
            this.subItems.clear();
        }
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public BaseDanmaku first() {
        if (this.items != null && !this.items.isEmpty()) {
            if (this.mSortType == 4) {
                return (BaseDanmaku) ((LinkedList) this.items).getFirst();
            }
            return (BaseDanmaku) ((SortedSet) this.items).first();
        }
        return null;
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public BaseDanmaku last() {
        if (this.items != null && !this.items.isEmpty()) {
            if (this.mSortType == 4) {
                return (BaseDanmaku) ((LinkedList) this.items).getLast();
            }
            return (BaseDanmaku) ((SortedSet) this.items).last();
        }
        return null;
    }

    private class DanmakuIterator implements IDanmakuIterator {
        private Iterator<BaseDanmaku> it;
        private Collection<BaseDanmaku> mData;
        private boolean mIteratorUsed;

        public DanmakuIterator(Collection<BaseDanmaku> datas) {
            setDatas(datas);
        }

        @Override // master.flame.danmaku.danmaku.model.IDanmakuIterator
        public synchronized void reset() {
            if (this.mIteratorUsed || this.it == null) {
                if (this.mData != null && Danmakus.this.mSize > 0) {
                    this.it = this.mData.iterator();
                } else {
                    this.it = null;
                }
            }
        }

        public synchronized void setDatas(Collection<BaseDanmaku> datas) {
            if (this.mData != datas) {
                this.mIteratorUsed = false;
                this.it = null;
            }
            this.mData = datas;
        }

        @Override // master.flame.danmaku.danmaku.model.IDanmakuIterator
        public synchronized BaseDanmaku next() {
            this.mIteratorUsed = true;
            return this.it != null ? this.it.next() : null;
        }

        /* JADX WARN: Removed duplicated region for block: B:10:0x0010  */
        @Override // master.flame.danmaku.danmaku.model.IDanmakuIterator
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public synchronized boolean hasNext() {
            /*
                r1 = this;
                monitor-enter(r1)
                java.util.Iterator<master.flame.danmaku.danmaku.model.BaseDanmaku> r0 = r1.it     // Catch: java.lang.Throwable -> L12
                if (r0 == 0) goto L10
                java.util.Iterator<master.flame.danmaku.danmaku.model.BaseDanmaku> r0 = r1.it     // Catch: java.lang.Throwable -> L12
                boolean r0 = r0.hasNext()     // Catch: java.lang.Throwable -> L12
                if (r0 == 0) goto L10
                r0 = 1
            Le:
                monitor-exit(r1)
                return r0
            L10:
                r0 = 0
                goto Le
            L12:
                r0 = move-exception
                monitor-exit(r1)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: master.flame.danmaku.danmaku.model.android.Danmakus.DanmakuIterator.hasNext():boolean");
        }

        @Override // master.flame.danmaku.danmaku.model.IDanmakuIterator
        public synchronized void remove() {
            this.mIteratorUsed = true;
            if (this.it != null) {
                this.it.remove();
            }
        }
    }

    private class BaseComparator implements Comparator<BaseDanmaku> {
        protected boolean mDuplicateMergingEnable;

        public BaseComparator(boolean duplicateMergingEnabled) {
            setDuplicateMergingEnabled(duplicateMergingEnabled);
        }

        public void setDuplicateMergingEnabled(boolean enable) {
            this.mDuplicateMergingEnable = enable;
        }

        @Override // java.util.Comparator
        public int compare(BaseDanmaku obj1, BaseDanmaku obj2) {
            if (this.mDuplicateMergingEnable && DanmakuUtils.isDuplicate(obj1, obj2)) {
                return 0;
            }
            return DanmakuUtils.compare(obj1, obj2);
        }
    }

    private class TimeComparator extends BaseComparator {
        public TimeComparator(boolean duplicateMergingEnabled) {
            super(duplicateMergingEnabled);
        }

        @Override // master.flame.danmaku.danmaku.model.android.Danmakus.BaseComparator
        public int compare(BaseDanmaku obj1, BaseDanmaku obj2) {
            return super.compare(obj1, obj2);
        }
    }

    private class YPosComparator extends BaseComparator {
        public YPosComparator(boolean duplicateMergingEnabled) {
            super(duplicateMergingEnabled);
        }

        @Override // master.flame.danmaku.danmaku.model.android.Danmakus.BaseComparator
        public int compare(BaseDanmaku obj1, BaseDanmaku obj2) {
            if (this.mDuplicateMergingEnable && DanmakuUtils.isDuplicate(obj1, obj2)) {
                return 0;
            }
            int result = Float.compare(obj1.getTop(), obj2.getTop());
            return result == 0 ? DanmakuUtils.compare(obj1, obj2) : result;
        }
    }

    private class YPosDescComparator extends BaseComparator {
        public YPosDescComparator(boolean duplicateMergingEnabled) {
            super(duplicateMergingEnabled);
        }

        @Override // master.flame.danmaku.danmaku.model.android.Danmakus.BaseComparator
        public int compare(BaseDanmaku obj1, BaseDanmaku obj2) {
            if (this.mDuplicateMergingEnable && DanmakuUtils.isDuplicate(obj1, obj2)) {
                return 0;
            }
            int result = Float.compare(obj2.getTop(), obj1.getTop());
            return result == 0 ? DanmakuUtils.compare(obj1, obj2) : result;
        }
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public boolean contains(BaseDanmaku item) {
        return this.items != null && this.items.contains(item);
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public boolean isEmpty() {
        return this.items == null || this.items.isEmpty();
    }

    private void setDuplicateMergingEnabled(boolean enable) {
        this.mComparator.setDuplicateMergingEnabled(enable);
        this.mDuplicateMergingEnabled = enable;
    }

    @Override // master.flame.danmaku.danmaku.model.IDanmakus
    public void setSubItemsDuplicateMergingEnabled(boolean enable) {
        this.mDuplicateMergingEnabled = enable;
        this.endItem = null;
        this.startItem = null;
        if (this.subItems == null) {
            this.subItems = new Danmakus(enable);
        }
        this.subItems.setDuplicateMergingEnabled(enable);
    }
}
