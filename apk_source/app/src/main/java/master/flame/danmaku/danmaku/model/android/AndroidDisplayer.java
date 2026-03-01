package master.flame.danmaku.danmaku.model.android;

import android.annotation.SuppressLint;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import com.alibaba.fastjson.asm.Opcodes;
import java.util.HashMap;
import java.util.Map;
import master.flame.danmaku.danmaku.model.AbsDisplayer;
import master.flame.danmaku.danmaku.model.AlphaValue;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import org.cybergarage.multiscreenhttp.HTTPStatus;

/* loaded from: classes.dex */
public class AndroidDisplayer extends AbsDisplayer<Canvas> {
    private static Paint ALPHA_PAINT = null;
    private static Paint BORDER_PAINT = null;
    public static final int BORDER_WIDTH = 4;
    public static TextPaint PAINT_DUPLICATE;
    private static Paint UNDERLINE_PAINT;
    private static float sLastScaleTextSize;
    public Canvas canvas;
    private int height;
    private int width;
    private static final Map<Float, Float> sTextHeightCache = new HashMap();
    private static final Map<Float, Float> sCachedScaleSize = new HashMap(10);
    public static int UNDERLINE_HEIGHT = 4;
    private static float SHADOW_RADIUS = 4.0f;
    private static float STROKE_WIDTH = 3.5f;
    private static float sProjectionOffsetX = 1.0f;
    private static float sProjectionOffsetY = 1.0f;
    private static int sProjectionAlpha = HTTPStatus.NO_CONTENT;
    public static boolean CONFIG_HAS_SHADOW = false;
    private static boolean HAS_SHADOW = CONFIG_HAS_SHADOW;
    public static boolean CONFIG_HAS_STROKE = true;
    private static boolean HAS_STROKE = CONFIG_HAS_STROKE;
    public static boolean CONFIG_HAS_PROJECTION = false;
    private static boolean HAS_PROJECTION = CONFIG_HAS_PROJECTION;
    public static boolean CONFIG_ANTI_ALIAS = true;
    private static boolean ANTI_ALIAS = CONFIG_ANTI_ALIAS;
    public static TextPaint PAINT = new TextPaint();
    private Camera camera = new Camera();
    private Matrix matrix = new Matrix();
    private int HIT_CACHE_COUNT = 0;
    private int NO_CACHE_COUNT = 0;
    private float density = 1.0f;
    private int densityDpi = Opcodes.IF_ICMPNE;
    private float scaledDensity = 1.0f;
    private int mSlopPixel = 0;
    private boolean mIsHardwareAccelerated = true;
    private int mMaximumBitmapWidth = 2048;
    private int mMaximumBitmapHeight = 2048;

    static {
        PAINT.setStrokeWidth(STROKE_WIDTH);
        PAINT_DUPLICATE = new TextPaint(PAINT);
        ALPHA_PAINT = new Paint();
        UNDERLINE_PAINT = new Paint();
        UNDERLINE_PAINT.setStrokeWidth(UNDERLINE_HEIGHT);
        UNDERLINE_PAINT.setStyle(Paint.Style.STROKE);
        BORDER_PAINT = new Paint();
        BORDER_PAINT.setStyle(Paint.Style.STROKE);
        BORDER_PAINT.setStrokeWidth(4.0f);
    }

    @SuppressLint({"NewApi"})
    private static final int getMaximumBitmapWidth(Canvas c) {
        return Build.VERSION.SDK_INT >= 14 ? c.getMaximumBitmapWidth() : c.getWidth();
    }

    @SuppressLint({"NewApi"})
    private static final int getMaximumBitmapHeight(Canvas c) {
        return Build.VERSION.SDK_INT >= 14 ? c.getMaximumBitmapHeight() : c.getHeight();
    }

    public static void setTypeFace(Typeface font) {
        if (PAINT != null) {
            PAINT.setTypeface(font);
        }
    }

    public static void setShadowRadius(float s) {
        SHADOW_RADIUS = s;
    }

    public static void setPaintStorkeWidth(float s) {
        PAINT.setStrokeWidth(s);
        STROKE_WIDTH = s;
    }

    public static void setProjectionConfig(float offsetX, float offsetY, int alpha) {
        if (sProjectionOffsetX != offsetX || sProjectionOffsetY != offsetY || sProjectionAlpha != alpha) {
            if (offsetX <= 1.0f) {
                offsetX = 1.0f;
            }
            sProjectionOffsetX = offsetX;
            if (offsetY <= 1.0f) {
                offsetY = 1.0f;
            }
            sProjectionOffsetY = offsetY;
            if (alpha < 0) {
                alpha = 0;
            } else if (alpha > 255) {
                alpha = 255;
            }
            sProjectionAlpha = alpha;
        }
    }

    public static void setFakeBoldText(boolean fakeBoldText) {
        PAINT.setFakeBoldText(fakeBoldText);
    }

    private void update(Canvas c) {
        this.canvas = c;
        if (c != null) {
            this.width = c.getWidth();
            this.height = c.getHeight();
            if (this.mIsHardwareAccelerated) {
                this.mMaximumBitmapWidth = getMaximumBitmapWidth(c);
                this.mMaximumBitmapHeight = getMaximumBitmapHeight(c);
            }
        }
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public int getWidth() {
        return this.width;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public int getHeight() {
        return this.height;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public float getDensity() {
        return this.density;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public int getDensityDpi() {
        return this.densityDpi;
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x004d  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x007b  */
    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int draw(master.flame.danmaku.danmaku.model.BaseDanmaku r12) {
        /*
            r11 = this;
            r10 = 0
            r6 = 0
            float r7 = r12.getTop()
            float r4 = r12.getLeft()
            android.graphics.Canvas r8 = r11.canvas
            if (r8 == 0) goto L1f
            r1 = 0
            r5 = 0
            int r8 = r12.getType()
            r9 = 7
            if (r8 != r9) goto L43
            int r8 = r12.getAlpha()
            int r9 = master.flame.danmaku.danmaku.model.AlphaValue.TRANSPARENT
            if (r8 != r9) goto L20
        L1f:
            return r6
        L20:
            float r8 = r12.rotationZ
            int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r8 != 0) goto L2c
            float r8 = r12.rotationY
            int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r8 == 0) goto L32
        L2c:
            android.graphics.Canvas r8 = r11.canvas
            r11.saveCanvas(r12, r8, r4, r7)
            r5 = 1
        L32:
            int r0 = r12.getAlpha()
            int r8 = master.flame.danmaku.danmaku.model.AlphaValue.MAX
            if (r0 == r8) goto L43
            android.graphics.Paint r1 = master.flame.danmaku.danmaku.model.android.AndroidDisplayer.ALPHA_PAINT
            int r8 = r12.getAlpha()
            r1.setAlpha(r8)
        L43:
            if (r1 == 0) goto L4d
            int r8 = r1.getAlpha()
            int r9 = master.flame.danmaku.danmaku.model.AlphaValue.TRANSPARENT
            if (r8 == r9) goto L1f
        L4d:
            r2 = 0
            r6 = 1
            boolean r8 = r12.hasDrawingCache()
            if (r8 == 0) goto L65
            master.flame.danmaku.danmaku.model.IDrawingCache<?> r8 = r12.cache
            master.flame.danmaku.danmaku.model.android.DrawingCache r8 = (master.flame.danmaku.danmaku.model.android.DrawingCache) r8
            master.flame.danmaku.danmaku.model.android.DrawingCacheHolder r3 = r8.get()
            if (r3 == 0) goto L65
            android.graphics.Canvas r8 = r11.canvas
            boolean r2 = r3.draw(r8, r4, r7, r1)
        L65:
            if (r2 != 0) goto L79
            if (r1 == 0) goto L81
            android.text.TextPaint r8 = master.flame.danmaku.danmaku.model.android.AndroidDisplayer.PAINT
            int r9 = r1.getAlpha()
            r8.setAlpha(r9)
        L72:
            android.graphics.Canvas r8 = r11.canvas
            r9 = 1
            drawDanmaku(r12, r8, r4, r7, r9)
            r6 = 2
        L79:
            if (r5 == 0) goto L1f
            android.graphics.Canvas r8 = r11.canvas
            r11.restoreCanvas(r8)
            goto L1f
        L81:
            android.text.TextPaint r8 = master.flame.danmaku.danmaku.model.android.AndroidDisplayer.PAINT
            r11.resetPaintAlpha(r8)
            goto L72
        */
        throw new UnsupportedOperationException("Method not decompiled: master.flame.danmaku.danmaku.model.android.AndroidDisplayer.draw(master.flame.danmaku.danmaku.model.BaseDanmaku):int");
    }

    private void resetPaintAlpha(Paint paint) {
        if (paint.getAlpha() != AlphaValue.MAX) {
            paint.setAlpha(AlphaValue.MAX);
        }
    }

    private void restoreCanvas(Canvas canvas) {
        canvas.restore();
    }

    private int saveCanvas(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
        this.camera.save();
        this.camera.rotateY(-danmaku.rotationY);
        this.camera.rotateZ(-danmaku.rotationZ);
        this.camera.getMatrix(this.matrix);
        this.matrix.preTranslate(-left, -top);
        this.matrix.postTranslate(left, top);
        this.camera.restore();
        int count = canvas.save();
        canvas.concat(this.matrix);
        return count;
    }

    public static void drawDanmaku(BaseDanmaku danmaku, Canvas canvas, float left, float top, boolean quick) {
        float left2 = left + danmaku.padding;
        float top2 = top + danmaku.padding;
        if (danmaku.borderColor != 0) {
            left2 += 4.0f;
            top2 += 4.0f;
        }
        HAS_STROKE = CONFIG_HAS_STROKE;
        HAS_SHADOW = CONFIG_HAS_SHADOW;
        HAS_PROJECTION = CONFIG_HAS_PROJECTION;
        ANTI_ALIAS = !quick && CONFIG_ANTI_ALIAS;
        TextPaint paint = getPaint(danmaku, quick);
        if (danmaku.lines != null) {
            String[] lines = danmaku.lines;
            if (lines.length == 1) {
                if (hasStroke(danmaku)) {
                    applyPaintConfig(danmaku, paint, true);
                    float strokeLeft = left2;
                    float strokeTop = top2 - paint.ascent();
                    if (HAS_PROJECTION) {
                        strokeLeft += sProjectionOffsetX;
                        strokeTop += sProjectionOffsetY;
                    }
                    canvas.drawText(lines[0], strokeLeft, strokeTop, paint);
                }
                applyPaintConfig(danmaku, paint, false);
                canvas.drawText(lines[0], left2, top2 - paint.ascent(), paint);
            } else {
                float textHeight = (danmaku.paintHeight - (danmaku.padding * 2)) / lines.length;
                for (int t = 0; t < lines.length; t++) {
                    if (lines[t] != null && lines[t].length() != 0) {
                        if (hasStroke(danmaku)) {
                            applyPaintConfig(danmaku, paint, true);
                            float strokeLeft2 = left2;
                            float strokeTop2 = ((t * textHeight) + top2) - paint.ascent();
                            if (HAS_PROJECTION) {
                                strokeLeft2 += sProjectionOffsetX;
                                strokeTop2 += sProjectionOffsetY;
                            }
                            canvas.drawText(lines[t], strokeLeft2, strokeTop2, paint);
                        }
                        applyPaintConfig(danmaku, paint, false);
                        canvas.drawText(lines[t], left2, ((t * textHeight) + top2) - paint.ascent(), paint);
                    }
                }
            }
        } else {
            if (hasStroke(danmaku)) {
                applyPaintConfig(danmaku, paint, true);
                float strokeLeft3 = left2;
                float strokeTop3 = top2 - paint.ascent();
                if (HAS_PROJECTION) {
                    strokeLeft3 += sProjectionOffsetX;
                    strokeTop3 += sProjectionOffsetY;
                }
                canvas.drawText(danmaku.text, strokeLeft3, strokeTop3, paint);
            }
            applyPaintConfig(danmaku, paint, false);
            canvas.drawText(danmaku.text, left2, top2 - paint.ascent(), paint);
        }
        if (danmaku.underlineColor != 0) {
            Paint linePaint = getUnderlinePaint(danmaku);
            float bottom = (danmaku.paintHeight + top) - UNDERLINE_HEIGHT;
            canvas.drawLine(left, bottom, left + danmaku.paintWidth, bottom, linePaint);
        }
        if (danmaku.borderColor != 0) {
            Paint borderPaint = getBorderPaint(danmaku);
            canvas.drawRect(left, top, left + danmaku.paintWidth, top + danmaku.paintHeight, borderPaint);
        }
    }

    private static boolean hasStroke(BaseDanmaku danmaku) {
        return (HAS_STROKE || HAS_PROJECTION) && STROKE_WIDTH > 0.0f && danmaku.textShadowColor != 0;
    }

    public static Paint getBorderPaint(BaseDanmaku danmaku) {
        BORDER_PAINT.setColor(danmaku.borderColor);
        return BORDER_PAINT;
    }

    public static Paint getUnderlinePaint(BaseDanmaku danmaku) {
        UNDERLINE_PAINT.setColor(danmaku.underlineColor);
        return UNDERLINE_PAINT;
    }

    private static TextPaint getPaint(BaseDanmaku danmaku, boolean quick) {
        TextPaint paint;
        if (quick) {
            paint = PAINT_DUPLICATE;
            paint.set(PAINT);
        } else {
            paint = PAINT;
        }
        paint.setTextSize(danmaku.textSize);
        applyTextScaleConfig(danmaku, paint);
        if (!HAS_SHADOW || SHADOW_RADIUS <= 0.0f || danmaku.textShadowColor == 0) {
            paint.clearShadowLayer();
        } else {
            paint.setShadowLayer(SHADOW_RADIUS, 0.0f, 0.0f, danmaku.textShadowColor);
        }
        paint.setAntiAlias(ANTI_ALIAS);
        return paint;
    }

    public static TextPaint getPaint(BaseDanmaku danmaku) {
        return getPaint(danmaku, false);
    }

    private static void applyPaintConfig(BaseDanmaku danmaku, Paint paint, boolean stroke) {
        if (DanmakuGlobalConfig.DEFAULT.isTranslucent) {
            if (stroke) {
                paint.setStyle(HAS_PROJECTION ? Paint.Style.FILL : Paint.Style.STROKE);
                paint.setColor(danmaku.textShadowColor & ViewCompat.MEASURED_SIZE_MASK);
                int alpha = HAS_PROJECTION ? (int) (sProjectionAlpha * (DanmakuGlobalConfig.DEFAULT.transparency / AlphaValue.MAX)) : DanmakuGlobalConfig.DEFAULT.transparency;
                paint.setAlpha(alpha);
                return;
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(danmaku.textColor & ViewCompat.MEASURED_SIZE_MASK);
            paint.setAlpha(DanmakuGlobalConfig.DEFAULT.transparency);
            return;
        }
        if (stroke) {
            paint.setStyle(HAS_PROJECTION ? Paint.Style.FILL : Paint.Style.STROKE);
            paint.setColor(danmaku.textShadowColor & ViewCompat.MEASURED_SIZE_MASK);
            int alpha2 = HAS_PROJECTION ? sProjectionAlpha : AlphaValue.MAX;
            paint.setAlpha(alpha2);
            return;
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(danmaku.textColor & ViewCompat.MEASURED_SIZE_MASK);
        paint.setAlpha(AlphaValue.MAX);
    }

    private static void applyTextScaleConfig(BaseDanmaku danmaku, Paint paint) {
        if (DanmakuGlobalConfig.DEFAULT.isTextScaled) {
            Float size = sCachedScaleSize.get(Float.valueOf(danmaku.textSize));
            if (size == null || sLastScaleTextSize != DanmakuGlobalConfig.DEFAULT.scaleTextSize) {
                sLastScaleTextSize = DanmakuGlobalConfig.DEFAULT.scaleTextSize;
                size = Float.valueOf(danmaku.textSize * DanmakuGlobalConfig.DEFAULT.scaleTextSize);
                sCachedScaleSize.put(Float.valueOf(danmaku.textSize), size);
            }
            paint.setTextSize(size.floatValue());
        }
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public void measure(BaseDanmaku danmaku) {
        TextPaint paint = getPaint(danmaku);
        if (HAS_STROKE) {
            applyPaintConfig(danmaku, paint, true);
        }
        calcPaintWH(danmaku, paint);
        if (HAS_STROKE) {
            applyPaintConfig(danmaku, paint, false);
        }
    }

    private void calcPaintWH(BaseDanmaku danmaku, TextPaint paint) {
        float w = 0.0f;
        Float textHeight = Float.valueOf(getTextHeight(paint));
        if (danmaku.lines == null) {
            float w2 = danmaku.text == null ? 0.0f : paint.measureText(danmaku.text);
            setDanmakuPaintWidthAndHeight(danmaku, w2, textHeight.floatValue());
            return;
        }
        for (String tempStr : danmaku.lines) {
            if (tempStr.length() > 0) {
                float tr = paint.measureText(tempStr);
                w = Math.max(tr, w);
            }
        }
        setDanmakuPaintWidthAndHeight(danmaku, w, danmaku.lines.length * textHeight.floatValue());
    }

    private void setDanmakuPaintWidthAndHeight(BaseDanmaku danmaku, float w, float h) {
        float pw = w + (danmaku.padding * 2);
        float ph = h + (danmaku.padding * 2);
        if (danmaku.borderColor != 0) {
            pw += 8.0f;
            ph += 8.0f;
        }
        danmaku.paintWidth = getStrokeWidth() + pw;
        danmaku.paintHeight = ph;
    }

    private static float getTextHeight(TextPaint paint) {
        Float textSize = Float.valueOf(paint.getTextSize());
        Float textHeight = sTextHeightCache.get(textSize);
        if (textHeight == null) {
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            textHeight = Float.valueOf((fontMetrics.descent - fontMetrics.ascent) + fontMetrics.leading);
            sTextHeightCache.put(textSize, textHeight);
        }
        return textHeight.floatValue();
    }

    public static void clearTextHeightCache() {
        sTextHeightCache.clear();
        sCachedScaleSize.clear();
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public float getScaledDensity() {
        return this.scaledDensity;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public void resetSlopPixel(float factor) {
        Math.max(this.density, this.scaledDensity);
        float d = Math.max(factor, getWidth() / 682.0f);
        float slop = d * 25.0f;
        this.mSlopPixel = (int) slop;
        if (factor > 1.0f) {
            this.mSlopPixel = (int) (slop * factor);
        }
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public int getSlopPixel() {
        return this.mSlopPixel;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public void setDensities(float density, int densityDpi, float scaledDensity) {
        this.density = density;
        this.densityDpi = densityDpi;
        this.scaledDensity = scaledDensity;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override // master.flame.danmaku.danmaku.model.AbsDisplayer
    public void setExtraData(Canvas data) {
        update(data);
    }

    @Override // master.flame.danmaku.danmaku.model.AbsDisplayer
    public Canvas getExtraData() {
        return this.canvas;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public float getStrokeWidth() {
        if (HAS_SHADOW && HAS_STROKE) {
            return Math.max(SHADOW_RADIUS, STROKE_WIDTH);
        }
        if (HAS_SHADOW) {
            return SHADOW_RADIUS;
        }
        if (HAS_STROKE) {
            return STROKE_WIDTH;
        }
        return 0.0f;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public void setHardwareAccelerated(boolean enable) {
        this.mIsHardwareAccelerated = enable;
    }

    @Override // master.flame.danmaku.danmaku.model.AbsDisplayer, master.flame.danmaku.danmaku.model.IDisplayer
    public boolean isHardwareAccelerated() {
        return this.mIsHardwareAccelerated;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public int getMaximumCacheWidth() {
        return this.mMaximumBitmapWidth;
    }

    @Override // master.flame.danmaku.danmaku.model.IDisplayer
    public int getMaximumCacheHeight() {
        return this.mMaximumBitmapHeight;
    }
}
