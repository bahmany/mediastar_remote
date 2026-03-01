package mktvsmart.screen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import java.util.Calendar;
import mktvsmart.screen.wheel.widget.ArrayWheelAdapter;
import mktvsmart.screen.wheel.widget.OnWheelChangedListener;
import mktvsmart.screen.wheel.widget.WheelView;

/* loaded from: classes.dex */
public class AgeRatingWheel extends LinearLayout {
    private final String[] adapterItems;
    private WheelView ageRating;
    private Calendar calendar;
    private OnChangeListener onChangeListener;
    private OnWheelChangedListener onSleepMinsChangedListener;

    public interface OnChangeListener {
        void onChange(int i);
    }

    public AgeRatingWheel(Context context) {
        super(context);
        this.calendar = Calendar.getInstance();
        this.adapterItems = new String[]{"OFF", ContentTree.IMAGE_ID, ContentTree.ALL_VIDEO_ID, ContentTree.ALL_AUDIO_ID, ContentTree.ALL_IMAGE_ID, ContentTree.VIDEO_FOLDER_ID, ContentTree.AUDIO_FOLDER_ID, ContentTree.IMAGE_FOLDER_ID, ContentTree.PLAYLIST_ID, "11", "12", "13", "14", "15", "16", "17", "18"};
        this.onSleepMinsChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.AgeRatingWheel.1
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView mins, int oldValue, int newValue) {
                AgeRatingWheel.this.calendar.set(12, newValue);
                if (AgeRatingWheel.this.onChangeListener != null) {
                    AgeRatingWheel.this.onChangeListener.onChange(AgeRatingWheel.this.getRating());
                }
            }
        };
        init(context);
    }

    public AgeRatingWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.calendar = Calendar.getInstance();
        this.adapterItems = new String[]{"OFF", ContentTree.IMAGE_ID, ContentTree.ALL_VIDEO_ID, ContentTree.ALL_AUDIO_ID, ContentTree.ALL_IMAGE_ID, ContentTree.VIDEO_FOLDER_ID, ContentTree.AUDIO_FOLDER_ID, ContentTree.IMAGE_FOLDER_ID, ContentTree.PLAYLIST_ID, "11", "12", "13", "14", "15", "16", "17", "18"};
        this.onSleepMinsChangedListener = new OnWheelChangedListener() { // from class: mktvsmart.screen.view.AgeRatingWheel.1
            @Override // mktvsmart.screen.wheel.widget.OnWheelChangedListener
            public void onChanged(WheelView mins, int oldValue, int newValue) {
                AgeRatingWheel.this.calendar.set(12, newValue);
                if (AgeRatingWheel.this.onChangeListener != null) {
                    AgeRatingWheel.this.onChangeListener.onChange(AgeRatingWheel.this.getRating());
                }
            }
        };
        init(context);
    }

    private void init(Context context) {
        this.ageRating = new WheelView(context);
        this.ageRating.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        this.ageRating.setAdapter(new ArrayWheelAdapter(this.adapterItems));
        this.ageRating.setVisibleItems(3);
        this.ageRating.setCyclic(true);
        this.ageRating.addChangingListener(this.onSleepMinsChangedListener);
        addView(this.ageRating);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void setRating(int rating) {
        this.ageRating.setCurrentItem(rating);
    }

    public int getRating() {
        return this.calendar.get(12);
    }
}
