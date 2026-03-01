package com.github.mikephil.charting.components;

import com.github.mikephil.charting.formatter.DefaultXAxisValueFormatter;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class XAxis extends AxisBase {
    protected List<String> mValues = new ArrayList();
    public int mLabelWidth = 1;
    public int mLabelHeight = 1;
    public int mLabelRotatedWidth = 1;
    public int mLabelRotatedHeight = 1;
    protected float mLabelRotationAngle = 0.0f;
    private int mSpaceBetweenLabels = 4;
    public int mAxisLabelModulus = 1;
    private boolean mIsAxisModulusCustom = false;
    public int mYAxisLabelModulus = 1;
    private boolean mAvoidFirstLastClipping = false;
    protected XAxisValueFormatter mXAxisValueFormatter = new DefaultXAxisValueFormatter();
    private XAxisPosition mPosition = XAxisPosition.TOP;

    public enum XAxisPosition {
        TOP,
        BOTTOM,
        BOTH_SIDED,
        TOP_INSIDE,
        BOTTOM_INSIDE;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static XAxisPosition[] valuesCustom() {
            XAxisPosition[] xAxisPositionArrValuesCustom = values();
            int length = xAxisPositionArrValuesCustom.length;
            XAxisPosition[] xAxisPositionArr = new XAxisPosition[length];
            System.arraycopy(xAxisPositionArrValuesCustom, 0, xAxisPositionArr, 0, length);
            return xAxisPositionArr;
        }
    }

    public XAxis() {
        this.mYOffset = Utils.convertDpToPixel(4.0f);
    }

    public XAxisPosition getPosition() {
        return this.mPosition;
    }

    public void setPosition(XAxisPosition pos) {
        this.mPosition = pos;
    }

    public float getLabelRotationAngle() {
        return this.mLabelRotationAngle;
    }

    public void setLabelRotationAngle(float angle) {
        this.mLabelRotationAngle = angle;
    }

    public void setSpaceBetweenLabels(int spaceCharacters) {
        this.mSpaceBetweenLabels = spaceCharacters;
    }

    public void setLabelsToSkip(int count) {
        if (count < 0) {
            count = 0;
        }
        this.mIsAxisModulusCustom = true;
        this.mAxisLabelModulus = count + 1;
    }

    public void resetLabelsToSkip() {
        this.mIsAxisModulusCustom = false;
    }

    public boolean isAxisModulusCustom() {
        return this.mIsAxisModulusCustom;
    }

    public int getSpaceBetweenLabels() {
        return this.mSpaceBetweenLabels;
    }

    public void setAvoidFirstLastClipping(boolean enabled) {
        this.mAvoidFirstLastClipping = enabled;
    }

    public boolean isAvoidFirstLastClippingEnabled() {
        return this.mAvoidFirstLastClipping;
    }

    public void setValues(List<String> values) {
        this.mValues = values;
    }

    public List<String> getValues() {
        return this.mValues;
    }

    public void setValueFormatter(XAxisValueFormatter formatter) {
        if (formatter == null) {
            this.mXAxisValueFormatter = new DefaultXAxisValueFormatter();
        } else {
            this.mXAxisValueFormatter = formatter;
        }
    }

    public XAxisValueFormatter getValueFormatter() {
        return this.mXAxisValueFormatter;
    }

    @Override // com.github.mikephil.charting.components.AxisBase
    public String getLongestLabel() {
        String longest = "";
        for (int i = 0; i < this.mValues.size(); i++) {
            String text = this.mValues.get(i);
            if (longest.length() < text.length()) {
                longest = text;
            }
        }
        return longest;
    }
}
