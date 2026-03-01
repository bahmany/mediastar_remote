package master.flame.danmaku.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakuIterator;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.Danmakus;

/* loaded from: classes.dex */
public class DanmakuFilters {
    public static final String TAG_DUPLICATE_FILTER = "1017_Filter";
    public static final String TAG_ELAPSED_TIME_FILTER = "1012_Filter";
    public static final String TAG_GUEST_FILTER = "1016_Filter";
    public static final String TAG_QUANTITY_DANMAKU_FILTER = "1011_Filter";
    public static final String TAG_TEXT_COLOR_DANMAKU_FILTER = "1013_Filter";
    public static final String TAG_TYPE_DANMAKU_FILTER = "1010_Filter";
    public static final String TAG_USER_HASH_FILTER = "1015_Filter";
    public static final String TAG_USER_ID_FILTER = "1014_Filter";
    public final Exception filterException = new Exception("not suuport this filter tag");
    IDanmakuFilter<?>[] mFilterArray = new IDanmakuFilter[0];
    private static DanmakuFilters instance = null;
    private static final Map<String, IDanmakuFilter<?>> filters = Collections.synchronizedSortedMap(new TreeMap());

    public interface IDanmakuFilter<T> {
        void clear();

        boolean filter(BaseDanmaku baseDanmaku, int i, int i2, DanmakuTimer danmakuTimer, boolean z);

        void reset();

        void setData(T t);
    }

    public static abstract class BaseDanmakuFilter<T> implements IDanmakuFilter<T> {
        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void clear() {
        }
    }

    public static class TypeDanmakuFilter extends BaseDanmakuFilter<List<Integer>> {
        final List<Integer> mFilterTypes = Collections.synchronizedList(new ArrayList());

        public void enableType(Integer type) {
            if (!this.mFilterTypes.contains(type)) {
                this.mFilterTypes.add(type);
            }
        }

        public void disableType(Integer type) {
            if (this.mFilterTypes.contains(type)) {
                this.mFilterTypes.remove(type);
            }
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public boolean filter(BaseDanmaku danmaku, int orderInScreen, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask) {
            return danmaku != null && this.mFilterTypes.contains(Integer.valueOf(danmaku.getType()));
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void setData(List<Integer> data) {
            reset();
            if (data != null) {
                for (Integer i : data) {
                    enableType(i);
                }
            }
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void reset() {
            this.mFilterTypes.clear();
        }
    }

    public static class QuantityDanmakuFilter extends BaseDanmakuFilter<Integer> {
        protected int mMaximumSize = -1;
        protected final IDanmakus danmakus = new Danmakus();
        protected BaseDanmaku mLastSkipped = null;

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public synchronized boolean filter(BaseDanmaku danmaku, int orderInScreen, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask) {
            boolean z = true;
            synchronized (this) {
                BaseDanmaku last = this.danmakus.last();
                if (last != null && last.isTimeOut()) {
                    this.danmakus.clear();
                }
                if (this.mMaximumSize <= 0 || danmaku.getType() != 1) {
                    z = false;
                } else if (!this.danmakus.contains(danmaku)) {
                    if (totalsizeInScreen < this.mMaximumSize || danmaku.isShown() || (this.mLastSkipped != null && danmaku.time - this.mLastSkipped.time > 500)) {
                        this.mLastSkipped = danmaku;
                        z = false;
                    } else if (orderInScreen > this.mMaximumSize && !danmaku.isTimeOut()) {
                        this.danmakus.addItem(danmaku);
                    } else {
                        this.mLastSkipped = danmaku;
                        z = false;
                    }
                }
            }
            return z;
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void setData(Integer data) {
            reset();
            if (data != null && data.intValue() != this.mMaximumSize) {
                this.mMaximumSize = data.intValue();
            }
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public synchronized void reset() {
            this.danmakus.clear();
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.BaseDanmakuFilter, master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void clear() {
            reset();
        }
    }

    public static class ElapsedTimeFilter extends BaseDanmakuFilter<Object> {
        long mMaxTime = 20;
        protected final IDanmakus danmakus = new Danmakus();

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public synchronized boolean filter(BaseDanmaku danmaku, int orderInScreen, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask) {
            boolean z = true;
            synchronized (this) {
                if (this.danmakus.last() != null && this.danmakus.last().isTimeOut()) {
                    this.danmakus.clear();
                }
                if (!this.danmakus.contains(danmaku)) {
                    if (timer == null || !danmaku.isOutside()) {
                        z = false;
                    } else {
                        long elapsedTime = System.currentTimeMillis() - timer.currMillisecond;
                        if (elapsedTime >= this.mMaxTime) {
                            this.danmakus.addItem(danmaku);
                        } else {
                            z = false;
                        }
                    }
                }
            }
            return z;
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void setData(Object data) {
            reset();
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public synchronized void reset() {
            this.danmakus.clear();
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.BaseDanmakuFilter, master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void clear() {
            reset();
        }
    }

    public static class TextColorFilter extends BaseDanmakuFilter<List<Integer>> {
        public List<Integer> mWhiteList = new ArrayList();

        private void addToWhiteList(Integer color) {
            if (!this.mWhiteList.contains(color)) {
                this.mWhiteList.add(color);
            }
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public boolean filter(BaseDanmaku danmaku, int index, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask) {
            return (danmaku == null || this.mWhiteList.contains(Integer.valueOf(danmaku.textColor))) ? false : true;
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void setData(List<Integer> data) {
            reset();
            if (data != null) {
                for (Integer i : data) {
                    addToWhiteList(i);
                }
            }
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void reset() {
            this.mWhiteList.clear();
        }
    }

    public static abstract class UserFilter<T> extends BaseDanmakuFilter<List<T>> {
        public List<T> mBlackList = new ArrayList();

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public abstract boolean filter(BaseDanmaku baseDanmaku, int i, int i2, DanmakuTimer danmakuTimer, boolean z);

        private void addToBlackList(T id) {
            if (!this.mBlackList.contains(id)) {
                this.mBlackList.add(id);
            }
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void setData(List<T> data) {
            reset();
            if (data != null) {
                for (T i : data) {
                    addToBlackList(i);
                }
            }
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void reset() {
            this.mBlackList.clear();
        }
    }

    public static class UserIdFilter extends UserFilter<Integer> {
        @Override // master.flame.danmaku.controller.DanmakuFilters.UserFilter, master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public boolean filter(BaseDanmaku danmaku, int index, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask) {
            return danmaku != null && this.mBlackList.contains(Integer.valueOf(danmaku.userId));
        }
    }

    public static class UserHashFilter extends UserFilter<String> {
        @Override // master.flame.danmaku.controller.DanmakuFilters.UserFilter, master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public boolean filter(BaseDanmaku danmaku, int index, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask) {
            return danmaku != null && this.mBlackList.contains(danmaku.userHash);
        }
    }

    public static class GuestFilter extends BaseDanmakuFilter<Boolean> {
        private Boolean mBlock = false;

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public boolean filter(BaseDanmaku danmaku, int index, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask) {
            if (this.mBlock.booleanValue()) {
                return danmaku.isGuest;
            }
            return false;
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void setData(Boolean data) {
            this.mBlock = data;
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void reset() {
            this.mBlock = false;
        }
    }

    public static class DuplicateMergingFilter extends BaseDanmakuFilter<Void> {
        protected final IDanmakus blockedDanmakus = new Danmakus(4);
        protected final LinkedHashMap<String, BaseDanmaku> currentDanmakus = new LinkedHashMap<>();
        private final IDanmakus passedDanmakus = new Danmakus(4);

        private final void removeTimeoutDanmakus(IDanmakus danmakus, long limitTime) {
            IDanmakuIterator it = danmakus.iterator();
            long startTime = System.currentTimeMillis();
            while (it.hasNext()) {
                try {
                    BaseDanmaku item = it.next();
                    if (item.isTimeOut()) {
                        it.remove();
                        if (System.currentTimeMillis() - startTime > limitTime) {
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
            }
        }

        private void removeTimeoutDanmakus(LinkedHashMap<String, BaseDanmaku> danmakus, int limitTime) {
            Iterator<Map.Entry<String, BaseDanmaku>> it = danmakus.entrySet().iterator();
            long startTime = System.currentTimeMillis();
            while (it.hasNext()) {
                try {
                    Map.Entry<String, BaseDanmaku> entry = it.next();
                    BaseDanmaku item = entry.getValue();
                    if (item.isTimeOut()) {
                        it.remove();
                        if (System.currentTimeMillis() - startTime > limitTime) {
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
            }
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public synchronized boolean filter(BaseDanmaku danmaku, int index, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask) {
            boolean z = true;
            synchronized (this) {
                removeTimeoutDanmakus(this.blockedDanmakus, 2L);
                removeTimeoutDanmakus(this.passedDanmakus, 2L);
                removeTimeoutDanmakus(this.currentDanmakus, 3);
                if (!this.blockedDanmakus.contains(danmaku) || danmaku.isOutside()) {
                    if (this.passedDanmakus.contains(danmaku)) {
                        z = false;
                    } else if (this.currentDanmakus.containsKey(danmaku.text)) {
                        this.currentDanmakus.put(danmaku.text, danmaku);
                        this.blockedDanmakus.removeItem(danmaku);
                        this.blockedDanmakus.addItem(danmaku);
                    } else {
                        this.currentDanmakus.put(danmaku.text, danmaku);
                        this.passedDanmakus.addItem(danmaku);
                        z = false;
                    }
                }
            }
            return z;
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void setData(Void data) {
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public synchronized void reset() {
            this.passedDanmakus.clear();
            this.blockedDanmakus.clear();
            this.currentDanmakus.clear();
        }

        @Override // master.flame.danmaku.controller.DanmakuFilters.BaseDanmakuFilter, master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter
        public void clear() {
            reset();
        }
    }

    public boolean filter(BaseDanmaku danmaku, int index, int totalsizeInScreen, DanmakuTimer timer, boolean fromCachingTask) {
        for (IDanmakuFilter<?> f : this.mFilterArray) {
            if (f != null && f.filter(danmaku, index, totalsizeInScreen, timer, fromCachingTask)) {
                return true;
            }
        }
        return false;
    }

    public IDanmakuFilter<?> get(String tag) {
        IDanmakuFilter<?> f = filters.get(tag);
        if (f == null) {
            return registerFilter(tag);
        }
        return f;
    }

    public IDanmakuFilter<?> registerFilter(String tag) throws Exception {
        if (tag == null) {
            throwFilterException();
            return null;
        }
        IDanmakuFilter<?> filter = filters.get(tag);
        if (filter == null) {
            if (TAG_TYPE_DANMAKU_FILTER.equals(tag)) {
                filter = new TypeDanmakuFilter();
            } else if (TAG_QUANTITY_DANMAKU_FILTER.equals(tag)) {
                filter = new QuantityDanmakuFilter();
            } else if (TAG_ELAPSED_TIME_FILTER.equals(tag)) {
                filter = new ElapsedTimeFilter();
            } else if (TAG_TEXT_COLOR_DANMAKU_FILTER.equals(tag)) {
                filter = new TextColorFilter();
            } else if (TAG_USER_ID_FILTER.equals(tag)) {
                filter = new UserIdFilter();
            } else if (TAG_USER_HASH_FILTER.equals(tag)) {
                filter = new UserHashFilter();
            } else if (TAG_GUEST_FILTER.equals(tag)) {
                filter = new GuestFilter();
            } else if (TAG_DUPLICATE_FILTER.equals(tag)) {
                filter = new DuplicateMergingFilter();
            }
        }
        if (filter == null) {
            throwFilterException();
            return null;
        }
        filter.setData(null);
        filters.put(tag, filter);
        this.mFilterArray = (IDanmakuFilter[]) filters.values().toArray(this.mFilterArray);
        return filter;
    }

    public void unregisterFilter(String tag) {
        IDanmakuFilter<?> f = filters.remove(tag);
        if (f != null) {
            f.clear();
            this.mFilterArray = (IDanmakuFilter[]) filters.values().toArray(this.mFilterArray);
        }
    }

    public void clear() {
        for (IDanmakuFilter<?> f : this.mFilterArray) {
            if (f != null) {
                f.clear();
            }
        }
    }

    public void reset() {
        for (IDanmakuFilter<?> f : this.mFilterArray) {
            if (f != null) {
                f.reset();
            }
        }
    }

    public void release() {
        clear();
        filters.clear();
        this.mFilterArray = new IDanmakuFilter[0];
    }

    private void throwFilterException() throws Exception {
        try {
            throw this.filterException;
        } catch (Exception e) {
        }
    }

    public static DanmakuFilters getDefault() {
        if (instance == null) {
            instance = new DanmakuFilters();
        }
        return instance;
    }
}
