package mktvsmart.screen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import java.util.Calendar;
import mktvsmart.screen.wheel.widget.NumericWheelAdapter;
import mktvsmart.screen.wheel.widget.OnWheelChangedListener;
import mktvsmart.screen.wheel.widget.WheelView;

/* loaded from: classes.dex */
public class TimePicker extends LinearLayout {
    private Calendar calendar;
    private WheelView hours;
    private boolean isHourOfDay;
    private WheelView mins;
    private OnChangeListener onChangeListener;
    private OnWheelChangedListener onHoursChangedListener;
    private OnWheelChangedListener onMinsChangedListener;

    public interface OnChangeListener {
        void onChange(int i, int i2);
    }

    public TimePicker(Context context) {
        super(context);
        this.calendar = Calendar.getInstance();
        this.isHourOfDay = true;
        this.onHoursChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.TimePicker.1
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView hours, int oldValue, int newValue) {
                TimePicker.this.calendar.set(11, newValue);
                if (TimePicker.this.onChangeListener != null) {
                    TimePicker.this.onChangeListener.onChange(TimePicker.this.getHourOfDay(), TimePicker.this.getMinute());
                }
            }
        };
        this.onMinsChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.TimePicker.2
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView mins, int oldValue, int newValue) {
                TimePicker.this.calendar.set(12, newValue);
                if (TimePicker.this.onChangeListener != null) {
                    TimePicker.this.onChangeListener.onChange(TimePicker.this.getHourOfDay(), TimePicker.this.getMinute());
                }
            }
        };
        init(context);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.calendar = Calendar.getInstance();
        this.isHourOfDay = true;
        this.onHoursChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.TimePicker.1
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView hours, int oldValue, int newValue) {
                TimePicker.this.calendar.set(11, newValue);
                if (TimePicker.this.onChangeListener != null) {
                    TimePicker.this.onChangeListener.onChange(TimePicker.this.getHourOfDay(), TimePicker.this.getMinute());
                }
            }
        };
        this.onMinsChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.TimePicker.2
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView mins, int oldValue, int newValue) {
                TimePicker.this.calendar.set(12, newValue);
                if (TimePicker.this.onChangeListener != null) {
                    TimePicker.this.onChangeListener.onChange(TimePicker.this.getHourOfDay(), TimePicker.this.getMinute());
                }
            }
        };
        init(context);
    }

    private void init(Context context) {
        this.hours = new WheelView(context);
        LinearLayout.LayoutParams lparams_hours = new LinearLayout.LayoutParams(-2, -2);
        lparams_hours.setMargins(0, 0, 24, 0);
        this.hours.setLayoutParams(lparams_hours);
        this.hours.setAdapter(new NumericWheelAdapter(0, 23));
        this.hours.setVisibleItems(3);
        this.hours.setCyclic(true);
        this.hours.addChangingListener(this.onHoursChangedListener);
        addView(this.hours);
        this.mins = new WheelView(context);
        this.mins.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        this.mins.setAdapter(new NumericWheelAdapter(0, 59));
        this.mins.setVisibleItems(3);
        this.mins.setCyclic(true);
        this.mins.addChangingListener(this.onMinsChangedListener);
        addView(this.mins);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void setHourOfDay(int hour) {
        this.hours.setCurrentItem(hour);
    }

    public int getHourOfDay() {
        return this.calendar.get(11);
    }

    public void setMinute(int minute) {
        this.mins.setCurrentItem(minute);
    }

    public int getMinute() {
        return this.calendar.get(12);
    }
}
