package mktvsmart.screen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import java.util.Calendar;
import mktvsmart.screen.wheel.widget.NumericWheelAdapter;
import mktvsmart.screen.wheel.widget.OnWheelChangedListener;
import mktvsmart.screen.wheel.widget.WheelView;

/* loaded from: classes.dex */
public class DatePicker extends LinearLayout {
    private Calendar calendar;
    private WheelView days;
    private WheelView months;
    private OnChangeListener onChangeListener;
    private OnWheelChangedListener onDaysChangedListener;
    private OnWheelChangedListener onMonthsChangedListener;

    public interface OnChangeListener {
        void onChange(int i, int i2, int i3);
    }

    public DatePicker(Context context) {
        super(context);
        this.calendar = Calendar.getInstance();
        this.onMonthsChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.DatePicker.1
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView month, int oldValue, int newValue) {
                DatePicker.this.calendar.set(2, (newValue + 1) - 1);
                if (DatePicker.this.onChangeListener != null) {
                    DatePicker.this.onChangeListener.onChange(DatePicker.this.getMonth(), DatePicker.this.getDay(), DatePicker.this.getDayOfWeek());
                }
            }
        };
        this.onDaysChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.DatePicker.2
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView day, int oldValue, int newValue) {
                DatePicker.this.calendar.set(5, newValue + 1);
                if (DatePicker.this.onChangeListener != null) {
                    DatePicker.this.onChangeListener.onChange(DatePicker.this.getMonth(), DatePicker.this.getDay(), DatePicker.this.getDayOfWeek());
                }
                DatePicker.this.days.setAdapter(new NumericWheelAdapter(1, DatePicker.this.calendar.getActualMaximum(5)));
            }
        };
        init(context);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.calendar = Calendar.getInstance();
        this.onMonthsChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.DatePicker.1
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView month, int oldValue, int newValue) {
                DatePicker.this.calendar.set(2, (newValue + 1) - 1);
                if (DatePicker.this.onChangeListener != null) {
                    DatePicker.this.onChangeListener.onChange(DatePicker.this.getMonth(), DatePicker.this.getDay(), DatePicker.this.getDayOfWeek());
                }
            }
        };
        this.onDaysChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.DatePicker.2
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView day, int oldValue, int newValue) {
                DatePicker.this.calendar.set(5, newValue + 1);
                if (DatePicker.this.onChangeListener != null) {
                    DatePicker.this.onChangeListener.onChange(DatePicker.this.getMonth(), DatePicker.this.getDay(), DatePicker.this.getDayOfWeek());
                }
                DatePicker.this.days.setAdapter(new NumericWheelAdapter(1, DatePicker.this.calendar.getActualMaximum(5)));
            }
        };
        init(context);
    }

    private void init(Context context) {
        this.months = new WheelView(context);
        LinearLayout.LayoutParams lparams_month = new LinearLayout.LayoutParams(-2, -2);
        lparams_month.setMargins(0, 0, 24, 0);
        this.months.setLayoutParams(lparams_month);
        this.months.setAdapter(new NumericWheelAdapter(1, 12));
        this.months.setVisibleItems(3);
        this.months.setCyclic(true);
        this.months.addChangingListener(this.onMonthsChangedListener);
        addView(this.months);
        this.days = new WheelView(context);
        this.days.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        int maxday_of_month = this.calendar.getActualMaximum(5);
        this.days.setAdapter(new NumericWheelAdapter(1, maxday_of_month));
        this.days.setVisibleItems(3);
        this.days.setCyclic(true);
        this.days.addChangingListener(this.onDaysChangedListener);
        addView(this.days);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void setMonth(int month) {
        this.months.setCurrentItem(month - 1);
    }

    public int getMonth() {
        return this.months.getCurrentItem() + 1;
    }

    public void setDay(int day) {
        this.days.setCurrentItem(day - 1);
    }

    public int getDay() {
        return this.days.getCurrentItem() + 1;
    }

    public int getDayOfWeek() {
        return this.calendar.get(7);
    }

    public static String getDayOfWeekCN(int day_of_week) {
        switch (day_of_week) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wes";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return null;
        }
    }
}
