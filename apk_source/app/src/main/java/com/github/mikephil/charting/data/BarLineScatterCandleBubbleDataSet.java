package com.github.mikephil.charting.data;

import android.graphics.Color;
import com.alibaba.fastjson.asm.Opcodes;
import com.github.mikephil.charting.data.Entry;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.util.List;

/* loaded from: classes.dex */
public abstract class BarLineScatterCandleBubbleDataSet<T extends Entry> extends DataSet<T> {
    protected int mHighLightColor;

    public BarLineScatterCandleBubbleDataSet(List<T> yVals, String label) {
        super(yVals, label);
        this.mHighLightColor = Color.rgb(255, Opcodes.NEW, KeyInfo.KEYCODE_VOLUME_UP);
    }

    public void setHighLightColor(int color) {
        this.mHighLightColor = color;
    }

    public int getHighLightColor() {
        return this.mHighLightColor;
    }
}
