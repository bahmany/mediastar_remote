package mktvsmart.screen.spectrum;

import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.text.DecimalFormat;

/* loaded from: classes.dex */
public class MyXAxisValueFormatter implements XAxisValueFormatter {
    private DecimalFormat mFormat = new DecimalFormat("0.0");

    @Override // com.github.mikephil.charting.formatter.XAxisValueFormatter
    public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
        return this.mFormat.format(Double.valueOf(original));
    }
}
