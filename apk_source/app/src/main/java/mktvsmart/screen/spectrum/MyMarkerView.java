package mktvsmart.screen.spectrum;

import android.content.Context;
import android.widget.ImageView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class MyMarkerView extends MarkerView {
    private ImageView mImageView;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        this.mImageView = (ImageView) findViewById(R.id.mark_iv);
    }

    @Override // com.github.mikephil.charting.components.MarkerView
    public void refreshContent(Entry e, Highlight highlight) {
    }

    @Override // com.github.mikephil.charting.components.MarkerView
    public int getXOffset(float xpos) {
        return -(getWidth() / 2);
    }

    @Override // com.github.mikephil.charting.components.MarkerView
    public int getYOffset(float ypos) {
        return -getHeight();
    }
}
