package master.flame.danmaku.controller;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.internal.view.SupportMenu;

/* loaded from: classes.dex */
public class DrawHelper {
    public static Paint PAINT_FPS;
    public static RectF RECT;
    private static boolean USE_DRAWCOLOR_TO_CLEAR_CANVAS = true;
    private static boolean USE_DRAWCOLOR_MODE_CLEAR = true;
    public static Paint PAINT = new Paint();

    static {
        PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        PAINT.setColor(0);
        RECT = new RectF();
    }

    public static void useDrawColorToClearCanvas(boolean use, boolean useClearMode) {
        USE_DRAWCOLOR_TO_CLEAR_CANVAS = use;
        USE_DRAWCOLOR_MODE_CLEAR = useClearMode;
    }

    public static void drawFPS(Canvas canvas, String text) {
        if (PAINT_FPS == null) {
            PAINT_FPS = new Paint();
            PAINT_FPS.setColor(SupportMenu.CATEGORY_MASK);
            PAINT_FPS.setTextSize(30.0f);
        }
        int top = canvas.getHeight() - 50;
        clearCanvas(canvas, 10.0f, top - 50, (int) (PAINT_FPS.measureText(text) + 20.0f), canvas.getHeight());
        canvas.drawText(text, 10.0f, top, PAINT_FPS);
    }

    public static void clearCanvas(Canvas canvas) {
        if (USE_DRAWCOLOR_TO_CLEAR_CANVAS) {
            if (USE_DRAWCOLOR_MODE_CLEAR) {
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                return;
            } else {
                canvas.drawColor(0);
                return;
            }
        }
        RECT.set(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight());
        clearCanvas(canvas, RECT);
    }

    public static void fillTransparent(Canvas canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

    public static void clearCanvas(Canvas canvas, float left, float top, float right, float bottom) {
        RECT.set(left, top, right, bottom);
        clearCanvas(canvas, RECT);
    }

    private static void clearCanvas(Canvas canvas, RectF rect) {
        if (rect.width() > 0.0f && rect.height() > 0.0f) {
            canvas.drawRect(rect, PAINT);
        }
    }
}
