package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.interfaces.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.utils.SelectionDetail;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CombinedHighlighter extends ChartHighlighter<BarLineScatterCandleBubbleDataProvider> {
    public CombinedHighlighter(BarLineScatterCandleBubbleDataProvider chart) {
        super(chart);
    }

    @Override // com.github.mikephil.charting.highlight.ChartHighlighter
    protected List<SelectionDetail> getSelectionDetailsAtIndex(int xIndex) {
        CombinedData data = (CombinedData) this.mChart.getData();
        List<ChartData> dataObjects = data.getAllData();
        List<SelectionDetail> vals = new ArrayList<>();
        float[] pts = new float[2];
        for (int i = 0; i < dataObjects.size(); i++) {
            for (int j = 0; j < dataObjects.get(i).getDataSetCount(); j++) {
                DataSet<?> dataSet = dataObjects.get(i).getDataSetByIndex(j);
                if (dataSet.isHighlightEnabled()) {
                    float yVal = dataSet.getYValForXIndex(xIndex);
                    if (yVal != Float.NaN) {
                        pts[1] = yVal;
                        this.mChart.getTransformer(dataSet.getAxisDependency()).pointValuesToPixel(pts);
                        if (!Float.isNaN(pts[1])) {
                            vals.add(new SelectionDetail(pts[1], j, dataSet));
                        }
                    }
                }
            }
        }
        return vals;
    }
}
