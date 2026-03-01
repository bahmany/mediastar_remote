package master.flame.danmaku.danmaku.util;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.AndroidDisplayer;
import master.flame.danmaku.danmaku.model.android.DrawingCache;
import master.flame.danmaku.danmaku.model.android.DrawingCacheHolder;

/* loaded from: classes.dex */
public class DanmakuUtils {
    public static boolean willHitInDuration(IDisplayer disp, BaseDanmaku d1, BaseDanmaku d2, long duration, long currTime) {
        int type1 = d1.getType();
        int type2 = d2.getType();
        if (type1 != type2 || d1.isOutside()) {
            return false;
        }
        long dTime = d2.time - d1.time;
        if (dTime < 0) {
            return true;
        }
        if (Math.abs(dTime) >= duration || d1.isTimeOut() || d2.isTimeOut()) {
            return false;
        }
        return type1 == 5 || type1 == 4 || checkHitAtTime(disp, d1, d2, currTime) || checkHitAtTime(disp, d1, d2, d1.time + d1.getDuration());
    }

    private static boolean checkHitAtTime(IDisplayer disp, BaseDanmaku d1, BaseDanmaku d2, long time) {
        float[] rectArr1 = d1.getRectAtTime(disp, time);
        float[] rectArr2 = d2.getRectAtTime(disp, time);
        if (rectArr1 == null || rectArr2 == null) {
            return false;
        }
        return checkHit(d1.getType(), d2.getType(), rectArr1, rectArr2);
    }

    private static boolean checkHit(int type1, int type2, float[] rectArr1, float[] rectArr2) {
        if (type1 != type2) {
            return false;
        }
        return type1 == 1 ? rectArr2[0] < rectArr1[2] : type1 == 6 && rectArr2[2] > rectArr1[0];
    }

    public static DrawingCache buildDanmakuDrawingCache(BaseDanmaku danmaku, IDisplayer disp, DrawingCache cache) {
        if (cache == null) {
            cache = new DrawingCache();
        }
        cache.build((int) Math.ceil(danmaku.paintWidth), (int) Math.ceil(danmaku.paintHeight), disp.getDensityDpi(), false);
        DrawingCacheHolder holder = cache.get();
        if (holder != null) {
            AndroidDisplayer.drawDanmaku(danmaku, holder.canvas, 0.0f, 0.0f, false);
            if (disp.isHardwareAccelerated()) {
                holder.splitWith(disp.getWidth(), disp.getHeight(), disp.getMaximumCacheWidth(), disp.getMaximumCacheHeight());
            }
        }
        return cache;
    }

    public static int getCacheSize(int w, int h) {
        return w * h * 4;
    }

    public static final boolean isDuplicate(BaseDanmaku obj1, BaseDanmaku obj2) {
        if (obj1 == obj2) {
            return false;
        }
        if (obj1.text == obj2.text) {
            return true;
        }
        return obj1.text != null && obj1.text.equals(obj2.text);
    }

    public static final int compare(BaseDanmaku obj1, BaseDanmaku obj2) {
        if (obj1 == obj2) {
            return 0;
        }
        if (obj1 == null) {
            return -1;
        }
        if (obj2 == null) {
            return 1;
        }
        long val = obj1.time - obj2.time;
        if (val > 0) {
            return 1;
        }
        if (val < 0) {
            return -1;
        }
        int result = obj1.index - obj2.index;
        if (result > 0) {
            return 1;
        }
        if (result < 0) {
            return -1;
        }
        int result2 = obj1.getType() - obj2.getType();
        if (result2 > 0) {
            return 1;
        }
        if (result2 < 0 || obj1.text == null) {
            return -1;
        }
        if (obj2.text == null) {
            return 1;
        }
        int r = obj1.text.compareTo(obj2.text);
        if (r != 0) {
            return r;
        }
        int r2 = obj1.textColor - obj2.textColor;
        if (r2 != 0) {
            return r2 >= 0 ? 1 : -1;
        }
        int r3 = obj1.index - obj2.index;
        if (r3 != 0) {
            return r3 >= 0 ? 1 : -1;
        }
        return obj1.hashCode() - obj1.hashCode();
    }

    public static final boolean isOverSize(IDisplayer disp, BaseDanmaku item) {
        return disp.isHardwareAccelerated() && (item.paintWidth > ((float) disp.getMaximumCacheWidth()) || item.paintHeight > ((float) disp.getMaximumCacheHeight()));
    }
}
