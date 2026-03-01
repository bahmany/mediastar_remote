package com.github.mikephil.charting.charts;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.animation.EasingFunction;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.ChartHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.ChartInterface;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.LegendRenderer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.plus.PlusShare;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.teleal.cling.model.ServiceReference;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public abstract class Chart<T extends ChartData<? extends DataSet<? extends Entry>>> extends ViewGroup implements ChartInterface {
    public static final String LOG_TAG = "MPAndroidChart";
    public static final int PAINT_CENTER_TEXT = 14;
    public static final int PAINT_DESCRIPTION = 11;
    public static final int PAINT_GRID_BACKGROUND = 4;
    public static final int PAINT_HOLE = 13;
    public static final int PAINT_INFO = 7;
    public static final int PAINT_LEGEND_LABEL = 18;
    protected ChartAnimator mAnimator;
    protected ChartTouchListener mChartTouchListener;
    protected T mData;
    protected boolean mDataNotSet;
    protected ValueFormatter mDefaultFormatter;
    protected float mDeltaX;
    protected Paint mDescPaint;
    protected String mDescription;
    private PointF mDescriptionPosition;
    private boolean mDragDecelerationEnabled;
    private float mDragDecelerationFrictionCoef;
    protected boolean mDrawMarkerViews;
    protected Paint mDrawPaint;
    private float mExtraBottomOffset;
    private float mExtraLeftOffset;
    private float mExtraRightOffset;
    private float mExtraTopOffset;
    private OnChartGestureListener mGestureListener;
    protected boolean mHighLightPerTapEnabled;
    protected ChartHighlighter mHighlighter;
    protected Highlight[] mIndicesToHighlight;
    protected Paint mInfoPaint;
    protected ArrayList<Runnable> mJobs;
    protected Legend mLegend;
    protected LegendRenderer mLegendRenderer;
    protected boolean mLogEnabled;
    protected MarkerView mMarkerView;
    private String mNoDataText;
    private String mNoDataTextDescription;
    private boolean mOffsetsCalculated;
    protected DataRenderer mRenderer;
    protected OnChartValueSelectedListener mSelectionListener;
    protected boolean mTouchEnabled;
    protected ViewPortHandler mViewPortHandler;
    protected float mXChartMax;
    protected float mXChartMin;

    protected abstract void calcMinMax();

    protected abstract void calculateOffsets();

    protected abstract float[] getMarkerPosition(Entry entry, Highlight highlight);

    public abstract void notifyDataSetChanged();

    public Chart(Context context) {
        super(context);
        this.mLogEnabled = false;
        this.mData = null;
        this.mHighLightPerTapEnabled = true;
        this.mDragDecelerationEnabled = true;
        this.mDragDecelerationFrictionCoef = 0.9f;
        this.mDescription = "Description";
        this.mDataNotSet = true;
        this.mDeltaX = 1.0f;
        this.mXChartMin = 0.0f;
        this.mXChartMax = 0.0f;
        this.mTouchEnabled = true;
        this.mNoDataText = "No chart data available.";
        this.mExtraTopOffset = 0.0f;
        this.mExtraRightOffset = 0.0f;
        this.mExtraBottomOffset = 0.0f;
        this.mExtraLeftOffset = 0.0f;
        this.mOffsetsCalculated = false;
        this.mDrawMarkerViews = true;
        this.mJobs = new ArrayList<>();
        init();
    }

    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLogEnabled = false;
        this.mData = null;
        this.mHighLightPerTapEnabled = true;
        this.mDragDecelerationEnabled = true;
        this.mDragDecelerationFrictionCoef = 0.9f;
        this.mDescription = "Description";
        this.mDataNotSet = true;
        this.mDeltaX = 1.0f;
        this.mXChartMin = 0.0f;
        this.mXChartMax = 0.0f;
        this.mTouchEnabled = true;
        this.mNoDataText = "No chart data available.";
        this.mExtraTopOffset = 0.0f;
        this.mExtraRightOffset = 0.0f;
        this.mExtraBottomOffset = 0.0f;
        this.mExtraLeftOffset = 0.0f;
        this.mOffsetsCalculated = false;
        this.mDrawMarkerViews = true;
        this.mJobs = new ArrayList<>();
        init();
    }

    public Chart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLogEnabled = false;
        this.mData = null;
        this.mHighLightPerTapEnabled = true;
        this.mDragDecelerationEnabled = true;
        this.mDragDecelerationFrictionCoef = 0.9f;
        this.mDescription = "Description";
        this.mDataNotSet = true;
        this.mDeltaX = 1.0f;
        this.mXChartMin = 0.0f;
        this.mXChartMax = 0.0f;
        this.mTouchEnabled = true;
        this.mNoDataText = "No chart data available.";
        this.mExtraTopOffset = 0.0f;
        this.mExtraRightOffset = 0.0f;
        this.mExtraBottomOffset = 0.0f;
        this.mExtraLeftOffset = 0.0f;
        this.mOffsetsCalculated = false;
        this.mDrawMarkerViews = true;
        this.mJobs = new ArrayList<>();
        init();
    }

    protected void init() {
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT < 11) {
            this.mAnimator = new ChartAnimator();
        } else {
            this.mAnimator = new ChartAnimator(new ValueAnimator.AnimatorUpdateListener() { // from class: com.github.mikephil.charting.charts.Chart.1
                AnonymousClass1() {
                }

                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    Chart.this.postInvalidate();
                }
            });
        }
        Utils.init(getContext());
        this.mDefaultFormatter = new DefaultValueFormatter(1);
        this.mViewPortHandler = new ViewPortHandler();
        this.mLegend = new Legend();
        this.mLegendRenderer = new LegendRenderer(this.mViewPortHandler, this.mLegend);
        this.mDescPaint = new Paint(1);
        this.mDescPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mDescPaint.setTextAlign(Paint.Align.RIGHT);
        this.mDescPaint.setTextSize(Utils.convertDpToPixel(9.0f));
        this.mInfoPaint = new Paint(1);
        this.mInfoPaint.setColor(Color.rgb(KeyInfo.KEYCODE_V, 189, 51));
        this.mInfoPaint.setTextAlign(Paint.Align.CENTER);
        this.mInfoPaint.setTextSize(Utils.convertDpToPixel(12.0f));
        this.mDrawPaint = new Paint(4);
        if (this.mLogEnabled) {
            Log.i("", "Chart.init()");
        }
    }

    /* renamed from: com.github.mikephil.charting.charts.Chart$1 */
    class AnonymousClass1 implements ValueAnimator.AnimatorUpdateListener {
        AnonymousClass1() {
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            Chart.this.postInvalidate();
        }
    }

    public void setData(T data) {
        if (data == null) {
            Log.e(LOG_TAG, "Cannot set data for chart. Provided data object is null.");
            return;
        }
        this.mDataNotSet = false;
        this.mOffsetsCalculated = false;
        this.mData = data;
        calculateFormatter(data.getYMin(), data.getYMax());
        for (DataSet<?> set : this.mData.getDataSets()) {
            if (set.needsDefaultFormatter()) {
                set.setValueFormatter(this.mDefaultFormatter);
            }
        }
        notifyDataSetChanged();
        if (this.mLogEnabled) {
            Log.i(LOG_TAG, "Data is set.");
        }
    }

    public void clear() {
        this.mData = null;
        this.mDataNotSet = true;
        this.mIndicesToHighlight = null;
        invalidate();
    }

    public void clearValues() {
        this.mData.clearValues();
        invalidate();
    }

    public boolean isEmpty() {
        return this.mData == null || this.mData.getYValCount() <= 0;
    }

    protected void calculateFormatter(float min, float max) {
        float reference;
        if (this.mData == null || this.mData.getXValCount() < 2) {
            reference = Math.max(Math.abs(min), Math.abs(max));
        } else {
            reference = Math.abs(max - min);
        }
        int digits = Utils.getDecimals(reference);
        this.mDefaultFormatter = new DefaultValueFormatter(digits);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mDataNotSet || this.mData == null || this.mData.getYValCount() <= 0) {
            canvas.drawText(this.mNoDataText, getWidth() / 2, getHeight() / 2, this.mInfoPaint);
            if (!TextUtils.isEmpty(this.mNoDataTextDescription)) {
                float textOffset = (-this.mInfoPaint.ascent()) + this.mInfoPaint.descent();
                canvas.drawText(this.mNoDataTextDescription, getWidth() / 2, (getHeight() / 2) + textOffset, this.mInfoPaint);
                return;
            }
            return;
        }
        if (!this.mOffsetsCalculated) {
            calculateOffsets();
            this.mOffsetsCalculated = true;
        }
    }

    protected void drawDescription(Canvas c) {
        if (!this.mDescription.equals("")) {
            if (this.mDescriptionPosition == null) {
                c.drawText(this.mDescription, (getWidth() - this.mViewPortHandler.offsetRight()) - 10.0f, (getHeight() - this.mViewPortHandler.offsetBottom()) - 10.0f, this.mDescPaint);
            } else {
                c.drawText(this.mDescription, this.mDescriptionPosition.x, this.mDescriptionPosition.y, this.mDescPaint);
            }
        }
    }

    public Highlight[] getHighlighted() {
        return this.mIndicesToHighlight;
    }

    public boolean isHighlightPerTapEnabled() {
        return this.mHighLightPerTapEnabled;
    }

    public void setHighlightPerTapEnabled(boolean enabled) {
        this.mHighLightPerTapEnabled = enabled;
    }

    public boolean valuesToHighlight() {
        return (this.mIndicesToHighlight == null || this.mIndicesToHighlight.length <= 0 || this.mIndicesToHighlight[0] == null) ? false : true;
    }

    public void highlightValues(Highlight[] highs) {
        this.mIndicesToHighlight = highs;
        if (highs == null || highs.length <= 0 || highs[0] == null) {
            this.mChartTouchListener.setLastHighlighted(null);
        } else {
            this.mChartTouchListener.setLastHighlighted(highs[0]);
        }
        invalidate();
    }

    public void highlightValue(int xIndex, int dataSetIndex) {
        if (xIndex < 0 || dataSetIndex < 0 || xIndex >= this.mData.getXValCount() || dataSetIndex >= this.mData.getDataSetCount()) {
            highlightValues(null);
        } else {
            highlightValues(new Highlight[]{new Highlight(xIndex, dataSetIndex)});
        }
    }

    public void highlightValue(Highlight highlight) {
        highlightValue(highlight, false);
    }

    public void highlightValue(Highlight high, boolean callListener) {
        Entry e = null;
        if (high == null) {
            this.mIndicesToHighlight = null;
        } else {
            if (this.mLogEnabled) {
                Log.i(LOG_TAG, "Highlighted: " + high.toString());
            }
            e = this.mData.getEntryForHighlight(high);
            if (e == null || e.getXIndex() != high.getXIndex()) {
                this.mIndicesToHighlight = null;
                high = null;
            } else {
                this.mIndicesToHighlight = new Highlight[]{high};
            }
        }
        if (callListener && this.mSelectionListener != null) {
            if (!valuesToHighlight()) {
                this.mSelectionListener.onNothingSelected();
            } else {
                this.mSelectionListener.onValueSelected(e, high.getDataSetIndex(), high);
            }
        }
        invalidate();
    }

    @Deprecated
    public void highlightTouch(Highlight high) {
        highlightValue(high, true);
    }

    public void setOnTouchListener(ChartTouchListener l) {
        this.mChartTouchListener = l;
    }

    protected void drawMarkers(Canvas canvas) {
        Entry e;
        if (this.mMarkerView != null && this.mDrawMarkerViews && valuesToHighlight()) {
            for (int i = 0; i < this.mIndicesToHighlight.length; i++) {
                Highlight highlight = this.mIndicesToHighlight[i];
                int xIndex = highlight.getXIndex();
                highlight.getDataSetIndex();
                if (xIndex <= this.mDeltaX && xIndex <= this.mDeltaX * this.mAnimator.getPhaseX() && (e = this.mData.getEntryForHighlight(this.mIndicesToHighlight[i])) != null && e.getXIndex() == this.mIndicesToHighlight[i].getXIndex()) {
                    float[] pos = getMarkerPosition(e, highlight);
                    if (this.mViewPortHandler.isInBounds(pos[0], pos[1])) {
                        this.mMarkerView.refreshContent(e, highlight);
                        this.mMarkerView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                        this.mMarkerView.layout(0, 0, this.mMarkerView.getMeasuredWidth(), this.mMarkerView.getMeasuredHeight());
                        if (pos[1] - this.mMarkerView.getHeight() <= 0.0f) {
                            float y = this.mMarkerView.getHeight() - pos[1];
                            this.mMarkerView.draw(canvas, pos[0], pos[1] + y);
                        } else {
                            this.mMarkerView.draw(canvas, pos[0], pos[1]);
                        }
                    }
                }
            }
        }
    }

    public ChartAnimator getAnimator() {
        return this.mAnimator;
    }

    public boolean isDragDecelerationEnabled() {
        return this.mDragDecelerationEnabled;
    }

    public void setDragDecelerationEnabled(boolean enabled) {
        this.mDragDecelerationEnabled = enabled;
    }

    public float getDragDecelerationFrictionCoef() {
        return this.mDragDecelerationFrictionCoef;
    }

    public void setDragDecelerationFrictionCoef(float newValue) {
        if (newValue < 0.0f) {
            newValue = 0.0f;
        }
        if (newValue >= 1.0f) {
            newValue = 0.999f;
        }
        this.mDragDecelerationFrictionCoef = newValue;
    }

    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easingX, EasingFunction easingY) {
        this.mAnimator.animateXY(durationMillisX, durationMillisY, easingX, easingY);
    }

    public void animateX(int durationMillis, EasingFunction easing) {
        this.mAnimator.animateX(durationMillis, easing);
    }

    public void animateY(int durationMillis, EasingFunction easing) {
        this.mAnimator.animateY(durationMillis, easing);
    }

    public void animateXY(int durationMillisX, int durationMillisY, Easing.EasingOption easingX, Easing.EasingOption easingY) {
        this.mAnimator.animateXY(durationMillisX, durationMillisY, easingX, easingY);
    }

    public void animateX(int durationMillis, Easing.EasingOption easing) {
        this.mAnimator.animateX(durationMillis, easing);
    }

    public void animateY(int durationMillis, Easing.EasingOption easing) {
        this.mAnimator.animateY(durationMillis, easing);
    }

    public void animateX(int durationMillis) {
        this.mAnimator.animateX(durationMillis);
    }

    public void animateY(int durationMillis) {
        this.mAnimator.animateY(durationMillis);
    }

    public void animateXY(int durationMillisX, int durationMillisY) {
        this.mAnimator.animateXY(durationMillisX, durationMillisY);
    }

    @Override // com.github.mikephil.charting.interfaces.ChartInterface
    public ValueFormatter getDefaultValueFormatter() {
        return this.mDefaultFormatter;
    }

    public void setOnChartValueSelectedListener(OnChartValueSelectedListener l) {
        this.mSelectionListener = l;
    }

    public void setOnChartGestureListener(OnChartGestureListener l) {
        this.mGestureListener = l;
    }

    public OnChartGestureListener getOnChartGestureListener() {
        return this.mGestureListener;
    }

    public float getYMax() {
        return this.mData.getYMax();
    }

    public float getYMin() {
        return this.mData.getYMin();
    }

    @Override // com.github.mikephil.charting.interfaces.ChartInterface
    public float getXChartMax() {
        return this.mXChartMax;
    }

    @Override // com.github.mikephil.charting.interfaces.ChartInterface
    public float getXChartMin() {
        return this.mXChartMin;
    }

    @Override // com.github.mikephil.charting.interfaces.ChartInterface
    public int getXValCount() {
        return this.mData.getXValCount();
    }

    public int getValueCount() {
        return this.mData.getYValCount();
    }

    public PointF getCenter() {
        return new PointF(getWidth() / 2.0f, getHeight() / 2.0f);
    }

    @Override // com.github.mikephil.charting.interfaces.ChartInterface
    public PointF getCenterOffsets() {
        return this.mViewPortHandler.getContentCenter();
    }

    public void setDescription(String desc) {
        if (desc == null) {
            desc = "";
        }
        this.mDescription = desc;
    }

    public void setDescriptionPosition(float x, float y) {
        this.mDescriptionPosition = new PointF(x, y);
    }

    public void setDescriptionTypeface(Typeface t) {
        this.mDescPaint.setTypeface(t);
    }

    public void setDescriptionTextSize(float size) {
        if (size > 16.0f) {
            size = 16.0f;
        }
        if (size < 6.0f) {
            size = 6.0f;
        }
        this.mDescPaint.setTextSize(Utils.convertDpToPixel(size));
    }

    public void setDescriptionColor(int color) {
        this.mDescPaint.setColor(color);
    }

    public void setExtraOffsets(float left, float top, float right, float bottom) {
        setExtraLeftOffset(left);
        setExtraTopOffset(top);
        setExtraRightOffset(right);
        setExtraBottomOffset(bottom);
    }

    public void setExtraTopOffset(float offset) {
        this.mExtraTopOffset = Utils.convertDpToPixel(offset);
    }

    public float getExtraTopOffset() {
        return this.mExtraTopOffset;
    }

    public void setExtraRightOffset(float offset) {
        this.mExtraRightOffset = Utils.convertDpToPixel(offset);
    }

    public float getExtraRightOffset() {
        return this.mExtraRightOffset;
    }

    public void setExtraBottomOffset(float offset) {
        this.mExtraBottomOffset = Utils.convertDpToPixel(offset);
    }

    public float getExtraBottomOffset() {
        return this.mExtraBottomOffset;
    }

    public void setExtraLeftOffset(float offset) {
        this.mExtraLeftOffset = Utils.convertDpToPixel(offset);
    }

    public float getExtraLeftOffset() {
        return this.mExtraLeftOffset;
    }

    public void setLogEnabled(boolean enabled) {
        this.mLogEnabled = enabled;
    }

    public boolean isLogEnabled() {
        return this.mLogEnabled;
    }

    public void setNoDataText(String text) {
        this.mNoDataText = text;
    }

    public void setNoDataTextDescription(String text) {
        this.mNoDataTextDescription = text;
    }

    public void setTouchEnabled(boolean enabled) {
        this.mTouchEnabled = enabled;
    }

    public void setMarkerView(MarkerView v) {
        this.mMarkerView = v;
    }

    public MarkerView getMarkerView() {
        return this.mMarkerView;
    }

    public Legend getLegend() {
        return this.mLegend;
    }

    public LegendRenderer getLegendRenderer() {
        return this.mLegendRenderer;
    }

    @Override // com.github.mikephil.charting.interfaces.ChartInterface
    public RectF getContentRect() {
        return this.mViewPortHandler.getContentRect();
    }

    public void disableScroll() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    public void enableScroll() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(false);
        }
    }

    public void setPaint(Paint p, int which) {
        switch (which) {
            case 7:
                this.mInfoPaint = p;
                break;
            case 11:
                this.mDescPaint = p;
                break;
        }
    }

    public Paint getPaint(int which) {
        switch (which) {
            case 7:
                return this.mInfoPaint;
            case 11:
                return this.mDescPaint;
            default:
                return null;
        }
    }

    public boolean isDrawMarkerViewEnabled() {
        return this.mDrawMarkerViews;
    }

    public void setDrawMarkerViews(boolean enabled) {
        this.mDrawMarkerViews = enabled;
    }

    public String getXValue(int index) {
        if (this.mData == null || this.mData.getXValCount() <= index) {
            return null;
        }
        return this.mData.getXVals().get(index);
    }

    public List<Entry> getEntriesAtIndex(int xIndex) {
        List<Entry> vals = new ArrayList<>();
        for (int i = 0; i < this.mData.getDataSetCount(); i++) {
            DataSet<? extends Entry> set = this.mData.getDataSetByIndex(i);
            Entry e = set.getEntryForXIndex(xIndex);
            if (e != null) {
                vals.add(e);
            }
        }
        return vals;
    }

    public T getData() {
        return this.mData;
    }

    public float getPercentOfTotal(float val) {
        return (val / this.mData.getYValueSum()) * 100.0f;
    }

    public ViewPortHandler getViewPortHandler() {
        return this.mViewPortHandler;
    }

    public DataRenderer getRenderer() {
        return this.mRenderer;
    }

    public void setRenderer(DataRenderer renderer) {
        if (renderer != null) {
            this.mRenderer = renderer;
        }
    }

    @Override // com.github.mikephil.charting.interfaces.ChartInterface
    public PointF getCenterOfView() {
        return getCenter();
    }

    public Bitmap getChartBitmap() {
        Bitmap returnedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(-1);
        }
        draw(canvas);
        return returnedBitmap;
    }

    public boolean saveToPath(String title, String pathOnSD) throws IOException {
        Bitmap b = getChartBitmap();
        try {
            OutputStream stream = new FileOutputStream(String.valueOf(Environment.getExternalStorageDirectory().getPath()) + pathOnSD + ServiceReference.DELIMITER + title + ".png");
            try {
                b.compress(Bitmap.CompressFormat.PNG, 40, stream);
                stream.close();
                return true;
            } catch (Exception e) {
                e = e;
                e.printStackTrace();
                return false;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public boolean saveToGallery(String fileName, int quality) throws IOException {
        FileOutputStream out;
        if (quality < 0 || quality > 100) {
            quality = 50;
        }
        long currentTime = System.currentTimeMillis();
        File extBaseDir = Environment.getExternalStorageDirectory();
        File file = new File(String.valueOf(extBaseDir.getAbsolutePath()) + "/DCIM");
        if (!file.exists() && !file.mkdirs()) {
            return false;
        }
        String filePath = String.valueOf(file.getAbsolutePath()) + ServiceReference.DELIMITER + fileName;
        try {
            out = new FileOutputStream(filePath);
        } catch (IOException e) {
            e = e;
        }
        try {
            Bitmap b = getChartBitmap();
            b.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
            long size = new File(filePath).length();
            ContentValues values = new ContentValues(8);
            values.put("title", fileName);
            values.put("_display_name", fileName);
            values.put("date_added", Long.valueOf(currentTime));
            values.put("mime_type", "image/jpeg");
            values.put(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION, "MPAndroidChart-Library Save");
            values.put("orientation", (Integer) 0);
            values.put("_data", filePath);
            values.put("_size", Long.valueOf(size));
            return getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) != null;
        } catch (IOException e2) {
            e = e2;
            e.printStackTrace();
            return false;
        }
    }

    public void addJob(Runnable job) {
        this.mJobs.add(job);
    }

    public void removeJob(Runnable job) {
        this.mJobs.remove(job);
    }

    public void clearAllJobs() {
        this.mJobs.clear();
    }

    public ArrayList<Runnable> getJobs() {
        return this.mJobs;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(left, top, right, bottom);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int) Utils.convertDpToPixel(50.0f);
        setMeasuredDimension(Math.max(getSuggestedMinimumWidth(), resolveSize(size, widthMeasureSpec)), Math.max(getSuggestedMinimumHeight(), resolveSize(size, heightMeasureSpec)));
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (this.mLogEnabled) {
            Log.i(LOG_TAG, "OnSizeChanged()");
        }
        if (w > 0 && h > 0 && w < 10000 && h < 10000) {
            this.mViewPortHandler.setChartDimens(w, h);
            if (this.mLogEnabled) {
                Log.i(LOG_TAG, "Setting chart dimens, width: " + w + ", height: " + h);
            }
            Iterator<Runnable> it = this.mJobs.iterator();
            while (it.hasNext()) {
                Runnable r = it.next();
                post(r);
            }
            this.mJobs.clear();
        }
        notifyDataSetChanged();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setHardwareAccelerationEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= 11) {
            if (enabled) {
                setLayerType(2, null);
                return;
            } else {
                setLayerType(1, null);
                return;
            }
        }
        Log.e(LOG_TAG, "Cannot enable/disable hardware acceleration for devices below API level 11.");
    }
}
