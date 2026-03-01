package master.flame.danmaku.danmaku.parser;

import android.text.TextUtils;
import java.lang.reflect.Array;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.FBDanmaku;
import master.flame.danmaku.danmaku.model.FTDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakuIterator;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.L2RDanmaku;
import master.flame.danmaku.danmaku.model.R2LDanmaku;
import master.flame.danmaku.danmaku.model.SpecialDanmaku;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.Danmakus;

/* loaded from: classes.dex */
public class DanmakuFactory {
    public static final float BILI_PLAYER_HEIGHT = 438.0f;
    public static final float BILI_PLAYER_WIDTH = 682.0f;
    public static final int DANMAKU_MEDIUM_TEXTSIZE = 25;
    public static final long MAX_DANMAKU_DURATION_HIGH_DENSITY = 9000;
    public static Duration MAX_Duration_Fix_Danmaku = null;
    public static Duration MAX_Duration_Scroll_Danmaku = null;
    public static Duration MAX_Duration_Special_Danmaku = null;
    public static final float OLD_BILI_PLAYER_HEIGHT = 385.0f;
    public static final float OLD_BILI_PLAYER_WIDTH = 539.0f;
    public static IDisplayer sLastDisp;
    public static int CURRENT_DISP_WIDTH = 0;
    public static int CURRENT_DISP_HEIGHT = 0;
    private static float CURRENT_DISP_SIZE_FACTOR = 1.0f;
    public static final long COMMON_DANMAKU_DURATION = 3800;
    public static long REAL_DANMAKU_DURATION = COMMON_DANMAKU_DURATION;
    public static final long MIN_DANMAKU_DURATION = 4000;
    public static long MAX_DANMAKU_DURATION = MIN_DANMAKU_DURATION;
    public static IDanmakus sSpecialDanmakus = new Danmakus();

    public static void resetDurationsData() {
        sLastDisp = null;
        CURRENT_DISP_HEIGHT = 0;
        CURRENT_DISP_WIDTH = 0;
        sSpecialDanmakus.clear();
        MAX_Duration_Scroll_Danmaku = null;
        MAX_Duration_Fix_Danmaku = null;
        MAX_Duration_Special_Danmaku = null;
        MAX_DANMAKU_DURATION = MIN_DANMAKU_DURATION;
    }

    public static void notifyDispSizeChanged(IDisplayer disp) {
        if (disp != null) {
            sLastDisp = disp;
        }
        createDanmaku(1, disp);
    }

    public static BaseDanmaku createDanmaku(int type) {
        return createDanmaku(type, sLastDisp);
    }

    public static BaseDanmaku createDanmaku(int type, IDisplayer disp) {
        if (disp == null) {
            return null;
        }
        sLastDisp = disp;
        return createDanmaku(type, disp.getWidth(), disp.getHeight(), CURRENT_DISP_SIZE_FACTOR);
    }

    public static BaseDanmaku createDanmaku(int type, IDisplayer disp, float viewportScale) {
        if (disp == null) {
            return null;
        }
        sLastDisp = disp;
        return createDanmaku(type, disp.getWidth(), disp.getHeight(), viewportScale);
    }

    public static BaseDanmaku createDanmaku(int type, int viewportWidth, int viewportHeight, float viewportScale) {
        return createDanmaku(type, viewportWidth, viewportHeight, viewportScale);
    }

    public static BaseDanmaku createDanmaku(int type, float viewportWidth, float viewportHeight, float viewportSizeFactor) {
        boolean sizeChanged = updateViewportState(viewportWidth, viewportHeight, viewportSizeFactor);
        if (MAX_Duration_Scroll_Danmaku == null) {
            MAX_Duration_Scroll_Danmaku = new Duration(REAL_DANMAKU_DURATION);
            MAX_Duration_Scroll_Danmaku.setFactor(DanmakuGlobalConfig.DEFAULT.scrollSpeedFactor);
        } else if (sizeChanged) {
            MAX_Duration_Scroll_Danmaku.setValue(REAL_DANMAKU_DURATION);
        }
        if (MAX_Duration_Fix_Danmaku == null) {
            MAX_Duration_Fix_Danmaku = new Duration(COMMON_DANMAKU_DURATION);
        }
        if (sizeChanged && viewportWidth > 0.0f) {
            updateMaxDanmakuDuration();
            float scaleX = 1.0f;
            float scaleY = 1.0f;
            if (CURRENT_DISP_WIDTH > 0 && CURRENT_DISP_HEIGHT > 0) {
                scaleX = viewportWidth / CURRENT_DISP_WIDTH;
                scaleY = viewportHeight / CURRENT_DISP_HEIGHT;
            }
            if (viewportHeight > 0.0f) {
                updateSpecialDanmakusDate(scaleX, scaleY);
            }
        }
        switch (type) {
            case 1:
                return new R2LDanmaku(MAX_Duration_Scroll_Danmaku);
            case 2:
            case 3:
            default:
                return null;
            case 4:
                return new FBDanmaku(MAX_Duration_Fix_Danmaku);
            case 5:
                return new FTDanmaku(MAX_Duration_Fix_Danmaku);
            case 6:
                return new L2RDanmaku(MAX_Duration_Scroll_Danmaku);
            case 7:
                BaseDanmaku instance = new SpecialDanmaku();
                sSpecialDanmakus.addItem(instance);
                return instance;
        }
    }

    public static boolean updateViewportState(float viewportWidth, float viewportHeight, float viewportSizeFactor) {
        if (CURRENT_DISP_WIDTH == ((int) viewportWidth) && CURRENT_DISP_HEIGHT == ((int) viewportHeight) && CURRENT_DISP_SIZE_FACTOR == viewportSizeFactor) {
            return false;
        }
        REAL_DANMAKU_DURATION = (long) (3800.0f * ((viewportSizeFactor * viewportWidth) / 682.0f));
        REAL_DANMAKU_DURATION = Math.min(MAX_DANMAKU_DURATION_HIGH_DENSITY, REAL_DANMAKU_DURATION);
        REAL_DANMAKU_DURATION = Math.max(MIN_DANMAKU_DURATION, REAL_DANMAKU_DURATION);
        CURRENT_DISP_WIDTH = (int) viewportWidth;
        CURRENT_DISP_HEIGHT = (int) viewportHeight;
        CURRENT_DISP_SIZE_FACTOR = viewportSizeFactor;
        return true;
    }

    private static void updateSpecialDanmakusDate(float scaleX, float scaleY) {
        IDanmakus list = sSpecialDanmakus;
        IDanmakuIterator it = list.iterator();
        while (it.hasNext()) {
            SpecialDanmaku speicalDanmaku = (SpecialDanmaku) it.next();
            fillTranslationData(speicalDanmaku, speicalDanmaku.beginX, speicalDanmaku.beginY, speicalDanmaku.endX, speicalDanmaku.endY, speicalDanmaku.translationDuration, speicalDanmaku.translationStartDelay, scaleX, scaleY);
            SpecialDanmaku.LinePath[] linePaths = speicalDanmaku.linePaths;
            if (linePaths != null && linePaths.length > 0) {
                int length = linePaths.length;
                float[][] points = (float[][]) Array.newInstance((Class<?>) Float.TYPE, length + 1, 2);
                for (int j = 0; j < length; j++) {
                    points[j] = linePaths[j].getBeginPoint();
                    points[j + 1] = linePaths[j].getEndPoint();
                }
                fillLinePathData(speicalDanmaku, points, scaleX, scaleY);
            }
        }
    }

    public static void updateMaxDanmakuDuration() {
        long maxScrollDuration = MAX_Duration_Scroll_Danmaku == null ? 0L : MAX_Duration_Scroll_Danmaku.value;
        long maxFixDuration = MAX_Duration_Fix_Danmaku == null ? 0L : MAX_Duration_Fix_Danmaku.value;
        long maxSpecialDuration = MAX_Duration_Special_Danmaku == null ? 0L : MAX_Duration_Special_Danmaku.value;
        MAX_DANMAKU_DURATION = Math.max(maxScrollDuration, maxFixDuration);
        MAX_DANMAKU_DURATION = Math.max(MAX_DANMAKU_DURATION, maxSpecialDuration);
        MAX_DANMAKU_DURATION = Math.max(COMMON_DANMAKU_DURATION, MAX_DANMAKU_DURATION);
        MAX_DANMAKU_DURATION = Math.max(REAL_DANMAKU_DURATION, MAX_DANMAKU_DURATION);
    }

    public static void updateDurationFactor(float f) {
        if (MAX_Duration_Scroll_Danmaku != null && MAX_Duration_Fix_Danmaku != null) {
            MAX_Duration_Scroll_Danmaku.setFactor(f);
            updateMaxDanmakuDuration();
        }
    }

    public static void fillText(BaseDanmaku danmaku, String text) {
        danmaku.text = text;
        if (!TextUtils.isEmpty(text) && text.contains(BaseDanmaku.DANMAKU_BR_CHAR)) {
            String[] lines = danmaku.text.split(BaseDanmaku.DANMAKU_BR_CHAR, -1);
            if (lines.length > 1) {
                danmaku.lines = lines;
            }
        }
    }

    public static void fillTranslationData(BaseDanmaku item, float beginX, float beginY, float endX, float endY, long translationDuration, long translationStartDelay, float scaleX, float scaleY) {
        if (item.getType() == 7) {
            ((SpecialDanmaku) item).setTranslationData(beginX * scaleX, beginY * scaleY, endX * scaleX, endY * scaleY, translationDuration, translationStartDelay);
            updateSpecicalDanmakuDuration(item);
        }
    }

    public static void fillLinePathData(BaseDanmaku item, float[][] points, float scaleX, float scaleY) {
        if (item.getType() == 7 && points.length != 0 && points[0].length == 2) {
            for (int i = 0; i < points.length; i++) {
                float[] fArr = points[i];
                fArr[0] = fArr[0] * scaleX;
                float[] fArr2 = points[i];
                fArr2[1] = fArr2[1] * scaleY;
            }
            ((SpecialDanmaku) item).setLinePathData(points);
        }
    }

    public static void fillAlphaData(BaseDanmaku item, int beginAlpha, int endAlpha, long alphaDuraion) {
        if (item.getType() == 7) {
            ((SpecialDanmaku) item).setAlphaData(beginAlpha, endAlpha, alphaDuraion);
            updateSpecicalDanmakuDuration(item);
        }
    }

    private static void updateSpecicalDanmakuDuration(BaseDanmaku item) {
        if (MAX_Duration_Special_Danmaku == null || (item.duration != null && item.duration.value > MAX_Duration_Special_Danmaku.value)) {
            MAX_Duration_Special_Danmaku = item.duration;
            updateMaxDanmakuDuration();
        }
    }
}
