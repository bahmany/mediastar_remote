package mktvsmart.screen.wheel.widget;

/* loaded from: classes.dex */
public class NumericWheelAdapter implements WheelAdapter {
    public static final int DEFAULT_MAX_VALUE = 9;
    private static final int DEFAULT_MIN_VALUE = 0;
    private String format;
    private int maxValue;
    private int minValue;

    public NumericWheelAdapter() {
        this(0, 9);
    }

    public NumericWheelAdapter(int minValue, int maxValue) {
        this(minValue, maxValue, null);
    }

    public NumericWheelAdapter(int minValue, int maxValue, String format) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }

    @Override // mktvsmart.screen.wheel.widget.WheelAdapter
    public String getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = this.minValue + index;
            return this.format != null ? String.format(this.format, Integer.valueOf(value)) : Integer.toString(value);
        }
        return null;
    }

    @Override // mktvsmart.screen.wheel.widget.WheelAdapter
    public int getItemsCount() {
        return (this.maxValue - this.minValue) + 1;
    }

    @Override // mktvsmart.screen.wheel.widget.WheelAdapter
    public int getMaximumLength() {
        int max = Math.max(Math.abs(this.maxValue), Math.abs(this.minValue));
        int maxLen = Integer.toString(max).length();
        if (this.minValue < 0) {
            return maxLen + 1;
        }
        return maxLen;
    }
}
