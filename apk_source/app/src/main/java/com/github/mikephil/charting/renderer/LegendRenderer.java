package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class LegendRenderer extends Renderer {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendForm;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendPosition;
    protected Legend mLegend;
    protected Paint mLegendFormPaint;
    protected Paint mLegendLabelPaint;

    static /* synthetic */ int[] $SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendForm() {
        int[] iArr = $SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendForm;
        if (iArr == null) {
            iArr = new int[Legend.LegendForm.valuesCustom().length];
            try {
                iArr[Legend.LegendForm.CIRCLE.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Legend.LegendForm.LINE.ordinal()] = 3;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Legend.LegendForm.SQUARE.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendForm = iArr;
        }
        return iArr;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendPosition() {
        int[] iArr = $SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendPosition;
        if (iArr == null) {
            iArr = new int[Legend.LegendPosition.valuesCustom().length];
            try {
                iArr[Legend.LegendPosition.ABOVE_CHART_CENTER.ordinal()] = 12;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Legend.LegendPosition.ABOVE_CHART_LEFT.ordinal()] = 10;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Legend.LegendPosition.ABOVE_CHART_RIGHT.ordinal()] = 11;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Legend.LegendPosition.BELOW_CHART_CENTER.ordinal()] = 9;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Legend.LegendPosition.BELOW_CHART_LEFT.ordinal()] = 7;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[Legend.LegendPosition.BELOW_CHART_RIGHT.ordinal()] = 8;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[Legend.LegendPosition.LEFT_OF_CHART.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[Legend.LegendPosition.LEFT_OF_CHART_CENTER.ordinal()] = 5;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[Legend.LegendPosition.LEFT_OF_CHART_INSIDE.ordinal()] = 6;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[Legend.LegendPosition.PIECHART_CENTER.ordinal()] = 13;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[Legend.LegendPosition.RIGHT_OF_CHART.ordinal()] = 1;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[Legend.LegendPosition.RIGHT_OF_CHART_CENTER.ordinal()] = 2;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[Legend.LegendPosition.RIGHT_OF_CHART_INSIDE.ordinal()] = 3;
            } catch (NoSuchFieldError e13) {
            }
            $SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendPosition = iArr;
        }
        return iArr;
    }

    public LegendRenderer(ViewPortHandler viewPortHandler, Legend legend) {
        super(viewPortHandler);
        this.mLegend = legend;
        this.mLegendLabelPaint = new Paint(1);
        this.mLegendLabelPaint.setTextSize(Utils.convertDpToPixel(9.0f));
        this.mLegendLabelPaint.setTextAlign(Paint.Align.LEFT);
        this.mLegendFormPaint = new Paint(1);
        this.mLegendFormPaint.setStyle(Paint.Style.FILL);
        this.mLegendFormPaint.setStrokeWidth(3.0f);
    }

    public Paint getLabelPaint() {
        return this.mLegendLabelPaint;
    }

    public Paint getFormPaint() {
        return this.mLegendFormPaint;
    }

    public void computeLegend(ChartData<?> data) {
        if (!this.mLegend.isLegendCustom()) {
            List<String> labels = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();
            for (int i = 0; i < data.getDataSetCount(); i++) {
                DataSet<? extends Entry> dataSet = data.getDataSetByIndex(i);
                List<Integer> clrs = dataSet.getColors();
                int entryCount = dataSet.getEntryCount();
                if ((dataSet instanceof BarDataSet) && ((BarDataSet) dataSet).isStacked()) {
                    BarDataSet bds = (BarDataSet) dataSet;
                    String[] sLabels = bds.getStackLabels();
                    for (int j = 0; j < clrs.size() && j < bds.getStackSize(); j++) {
                        labels.add(sLabels[j % sLabels.length]);
                        colors.add(clrs.get(j));
                    }
                    if (bds.getLabel() != null) {
                        colors.add(-2);
                        labels.add(bds.getLabel());
                    }
                } else if (dataSet instanceof PieDataSet) {
                    List<String> xVals = data.getXVals();
                    PieDataSet pds = (PieDataSet) dataSet;
                    for (int j2 = 0; j2 < clrs.size() && j2 < entryCount && j2 < xVals.size(); j2++) {
                        labels.add(xVals.get(j2));
                        colors.add(clrs.get(j2));
                    }
                    if (pds.getLabel() != null) {
                        colors.add(-2);
                        labels.add(pds.getLabel());
                    }
                } else {
                    for (int j3 = 0; j3 < clrs.size() && j3 < entryCount; j3++) {
                        if (j3 < clrs.size() - 1 && j3 < entryCount - 1) {
                            labels.add(null);
                        } else {
                            String label = data.getDataSetByIndex(i).getLabel();
                            labels.add(label);
                        }
                        colors.add(clrs.get(j3));
                    }
                }
            }
            if (this.mLegend.getExtraColors() != null && this.mLegend.getExtraLabels() != null) {
                for (int color : this.mLegend.getExtraColors()) {
                    colors.add(Integer.valueOf(color));
                }
                Collections.addAll(labels, this.mLegend.getExtraLabels());
            }
            this.mLegend.setComputedColors(colors);
            this.mLegend.setComputedLabels(labels);
        }
        Typeface tf = this.mLegend.getTypeface();
        if (tf != null) {
            this.mLegendLabelPaint.setTypeface(tf);
        }
        this.mLegendLabelPaint.setTextSize(this.mLegend.getTextSize());
        this.mLegendLabelPaint.setColor(this.mLegend.getTextColor());
        this.mLegend.calculateDimensions(this.mLegendLabelPaint, this.mViewPortHandler);
    }

    public void renderLegend(Canvas c) {
        float originPosX;
        float posY;
        float f;
        float posX;
        float posY2;
        if (this.mLegend.isEnabled()) {
            Typeface tf = this.mLegend.getTypeface();
            if (tf != null) {
                this.mLegendLabelPaint.setTypeface(tf);
            }
            this.mLegendLabelPaint.setTextSize(this.mLegend.getTextSize());
            this.mLegendLabelPaint.setColor(this.mLegend.getTextColor());
            float labelLineHeight = Utils.getLineHeight(this.mLegendLabelPaint);
            float labelLineSpacing = Utils.getLineSpacing(this.mLegendLabelPaint) + this.mLegend.getYEntrySpace();
            float formYOffset = labelLineHeight - (Utils.calcTextHeight(this.mLegendLabelPaint, "ABC") / 2.0f);
            String[] labels = this.mLegend.getLabels();
            int[] colors = this.mLegend.getColors();
            float formToTextSpace = this.mLegend.getFormToTextSpace();
            float xEntrySpace = this.mLegend.getXEntrySpace();
            Legend.LegendDirection direction = this.mLegend.getDirection();
            float formSize = this.mLegend.getFormSize();
            float stackSpace = this.mLegend.getStackSpace();
            float yoffset = this.mLegend.getYOffset();
            float xoffset = this.mLegend.getXOffset();
            Legend.LegendPosition legendPosition = this.mLegend.getPosition();
            switch ($SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendPosition()[legendPosition.ordinal()]) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 13:
                    float stack = 0.0f;
                    boolean wasStacked = false;
                    if (legendPosition == Legend.LegendPosition.PIECHART_CENTER) {
                        posX = (this.mViewPortHandler.getChartWidth() / 2.0f) + (direction == Legend.LegendDirection.LEFT_TO_RIGHT ? (-this.mLegend.mTextWidthMax) / 2.0f : this.mLegend.mTextWidthMax / 2.0f);
                        posY2 = ((this.mViewPortHandler.getChartHeight() / 2.0f) - (this.mLegend.mNeededHeight / 2.0f)) + this.mLegend.getYOffset();
                    } else {
                        boolean isRightAligned = legendPosition == Legend.LegendPosition.RIGHT_OF_CHART || legendPosition == Legend.LegendPosition.RIGHT_OF_CHART_CENTER || legendPosition == Legend.LegendPosition.RIGHT_OF_CHART_INSIDE;
                        if (isRightAligned) {
                            posX = this.mViewPortHandler.getChartWidth() - xoffset;
                            if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) {
                                posX -= this.mLegend.mTextWidthMax;
                            }
                        } else {
                            posX = xoffset;
                            if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) {
                                posX += this.mLegend.mTextWidthMax;
                            }
                        }
                        if (legendPosition == Legend.LegendPosition.RIGHT_OF_CHART || legendPosition == Legend.LegendPosition.LEFT_OF_CHART) {
                            posY2 = this.mViewPortHandler.contentTop() + yoffset;
                        } else if (legendPosition == Legend.LegendPosition.RIGHT_OF_CHART_CENTER || legendPosition == Legend.LegendPosition.LEFT_OF_CHART_CENTER) {
                            posY2 = (this.mViewPortHandler.getChartHeight() / 2.0f) - (this.mLegend.mNeededHeight / 2.0f);
                        } else {
                            posY2 = this.mViewPortHandler.contentTop() + yoffset;
                        }
                    }
                    for (int i = 0; i < labels.length; i++) {
                        Boolean drawingForm = Boolean.valueOf(colors[i] != -2);
                        float x = posX;
                        if (drawingForm.booleanValue()) {
                            if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) {
                                x += stack;
                            } else {
                                x -= formSize - stack;
                            }
                            drawForm(c, x, posY2 + formYOffset, i, this.mLegend);
                            if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) {
                                x += formSize;
                            }
                        }
                        if (labels[i] != null) {
                            if (drawingForm.booleanValue() && !wasStacked) {
                                x += direction == Legend.LegendDirection.LEFT_TO_RIGHT ? formToTextSpace : -formToTextSpace;
                            } else if (wasStacked) {
                                x = posX;
                            }
                            if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) {
                                x -= Utils.calcTextWidth(this.mLegendLabelPaint, labels[i]);
                            }
                            if (!wasStacked) {
                                drawLabel(c, x, posY2 + labelLineHeight, labels[i]);
                            } else {
                                posY2 += labelLineHeight + labelLineSpacing;
                                drawLabel(c, x, posY2 + labelLineHeight, labels[i]);
                            }
                            posY2 += labelLineHeight + labelLineSpacing;
                            stack = 0.0f;
                        } else {
                            stack += formSize + stackSpace;
                            wasStacked = true;
                        }
                    }
                    break;
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    float contentWidth = this.mViewPortHandler.contentWidth();
                    if (legendPosition == Legend.LegendPosition.BELOW_CHART_LEFT || legendPosition == Legend.LegendPosition.ABOVE_CHART_LEFT) {
                        originPosX = this.mViewPortHandler.contentLeft() + xoffset;
                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) {
                            originPosX += this.mLegend.mNeededWidth;
                        }
                    } else if (legendPosition == Legend.LegendPosition.BELOW_CHART_RIGHT || legendPosition == Legend.LegendPosition.ABOVE_CHART_RIGHT) {
                        originPosX = this.mViewPortHandler.contentRight() - xoffset;
                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) {
                            originPosX -= this.mLegend.mNeededWidth;
                        }
                    } else {
                        originPosX = this.mViewPortHandler.contentLeft() + (contentWidth / 2.0f);
                    }
                    FSize[] calculatedLineSizes = this.mLegend.getCalculatedLineSizes();
                    FSize[] calculatedLabelSizes = this.mLegend.getCalculatedLabelSizes();
                    Boolean[] calculatedLabelBreakPoints = this.mLegend.getCalculatedLabelBreakPoints();
                    float posX2 = originPosX;
                    if (legendPosition == Legend.LegendPosition.ABOVE_CHART_LEFT || legendPosition == Legend.LegendPosition.ABOVE_CHART_RIGHT || legendPosition == Legend.LegendPosition.ABOVE_CHART_CENTER) {
                        posY = 0.0f;
                    } else {
                        posY = (this.mViewPortHandler.getChartHeight() - yoffset) - this.mLegend.mNeededHeight;
                    }
                    int lineIndex = 0;
                    int count = labels.length;
                    for (int i2 = 0; i2 < count; i2++) {
                        if (i2 < calculatedLabelBreakPoints.length && calculatedLabelBreakPoints[i2].booleanValue()) {
                            posX2 = originPosX;
                            posY += labelLineHeight + labelLineSpacing;
                        }
                        if (posX2 == originPosX && legendPosition == Legend.LegendPosition.BELOW_CHART_CENTER && lineIndex < calculatedLineSizes.length) {
                            posX2 += (direction == Legend.LegendDirection.RIGHT_TO_LEFT ? calculatedLineSizes[lineIndex].width : -calculatedLineSizes[lineIndex].width) / 2.0f;
                            lineIndex++;
                        }
                        boolean drawingForm2 = colors[i2] != -2;
                        boolean isStacked = labels[i2] == null;
                        if (drawingForm2) {
                            if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) {
                                posX2 -= formSize;
                            }
                            drawForm(c, posX2, posY + formYOffset, i2, this.mLegend);
                            if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) {
                                posX2 += formSize;
                            }
                        }
                        if (!isStacked) {
                            if (drawingForm2) {
                                posX2 += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -formToTextSpace : formToTextSpace;
                            }
                            if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) {
                                posX2 -= calculatedLabelSizes[i2].width;
                            }
                            drawLabel(c, posX2, posY + labelLineHeight, labels[i2]);
                            if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) {
                                posX2 += calculatedLabelSizes[i2].width;
                            }
                            f = direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -xEntrySpace : xEntrySpace;
                        } else {
                            f = direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -stackSpace : stackSpace;
                        }
                        posX2 += f;
                    }
                    break;
            }
        }
    }

    protected void drawForm(Canvas c, float x, float y, int index, Legend legend) {
        if (legend.getColors()[index] != -2) {
            this.mLegendFormPaint.setColor(legend.getColors()[index]);
            float formsize = legend.getFormSize();
            float half = formsize / 2.0f;
            switch ($SWITCH_TABLE$com$github$mikephil$charting$components$Legend$LegendForm()[legend.getForm().ordinal()]) {
                case 1:
                    c.drawRect(x, y - half, x + formsize, y + half, this.mLegendFormPaint);
                    break;
                case 2:
                    c.drawCircle(x + half, y, half, this.mLegendFormPaint);
                    break;
                case 3:
                    c.drawLine(x, y, x + formsize, y, this.mLegendFormPaint);
                    break;
            }
        }
    }

    protected void drawLabel(Canvas c, float x, float y, String label) {
        c.drawText(label, x, y, this.mLegendLabelPaint);
    }
}
