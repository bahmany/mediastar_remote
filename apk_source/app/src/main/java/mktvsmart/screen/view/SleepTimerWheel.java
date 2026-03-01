package mktvsmart.screen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.util.Calendar;
import mktvsmart.screen.wheel.widget.NumericWheelAdapter;
import mktvsmart.screen.wheel.widget.OnWheelChangedListener;
import mktvsmart.screen.wheel.widget.WheelView;

/* loaded from: classes.dex */
public class SleepTimerWheel extends LinearLayout {
    private Calendar calendar;
    private OnChangeListener onChangeListener;
    private OnWheelChangedListener onSleepMinsChangedListener;
    private WheelView sleepMins;

    public interface OnChangeListener {
        void onChange(int i);
    }

    public SleepTimerWheel(Context context) {
        super(context);
        this.calendar = Calendar.getInstance();
        this.onSleepMinsChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.SleepTimerWheel.1
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView mins, int oldValue, int newValue) {
                SleepTimerWheel.this.calendar.set(12, newValue);
                if (SleepTimerWheel.this.onChangeListener != null) {
                    SleepTimerWheel.this.onChangeListener.onChange(SleepTimerWheel.this.getMinute());
                }
            }
        };
        init(context);
    }

    public SleepTimerWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.calendar = Calendar.getInstance();
        this.onSleepMinsChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.SleepTimerWheel.1
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView mins, int oldValue, int newValue) {
                SleepTimerWheel.this.calendar.set(12, newValue);
                if (SleepTimerWheel.this.onChangeListener != null) {
                    SleepTimerWheel.this.onChangeListener.onChange(SleepTimerWheel.this.getMinute());
                }
            }
        };
        init(context);
    }

    private void init(Context context) {
        this.sleepMins = new WheelView(context);
        this.sleepMins.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        this.sleepMins.setAdapter(new NumericWheelAdapter(1, KeyInfo.KEYCODE_ASK));
        this.sleepMins.setVisibleItems(3);
        this.sleepMins.setCyclic(true);
        this.sleepMins.addChangingListener(this.onSleepMinsChangedListener);
        addView(this.sleepMins);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void setMinute(int minute) {
        this.sleepMins.setCurrentItem(minute);
    }

    public int getMinute() {
        return this.calendar.get(12);
    }
}
