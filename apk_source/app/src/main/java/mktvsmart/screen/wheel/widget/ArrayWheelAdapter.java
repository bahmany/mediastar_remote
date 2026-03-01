package mktvsmart.screen.wheel.widget;

/* loaded from: classes.dex */
public class ArrayWheelAdapter<T> implements WheelAdapter {
    public static final int DEFAULT_LENGTH = -1;
    private T[] items;
    private int length;

    public ArrayWheelAdapter(T[] tArr, int length) {
        this.items = tArr;
        this.length = length;
    }

    public ArrayWheelAdapter(T[] tArr) {
        this(tArr, -1);
    }

    @Override // mktvsmart.screen.wheel.widget.WheelAdapter
    public String getItem(int index) {
        if (index < 0 || index >= this.items.length) {
            return null;
        }
        return this.items[index].toString();
    }

    @Override // mktvsmart.screen.wheel.widget.WheelAdapter
    public int getItemsCount() {
        return this.items.length;
    }

    @Override // mktvsmart.screen.wheel.widget.WheelAdapter
    public int getMaximumLength() {
        return this.length;
    }
}
