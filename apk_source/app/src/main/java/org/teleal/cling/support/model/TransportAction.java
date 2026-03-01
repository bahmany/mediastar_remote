package org.teleal.cling.support.model;

import java.util.ArrayList;
import java.util.List;
import org.teleal.cling.model.ModelUtil;

/* loaded from: classes.dex */
public enum TransportAction {
    Play,
    Stop,
    Pause,
    Seek,
    Next,
    Previous,
    Record;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static TransportAction[] valuesCustom() {
        TransportAction[] transportActionArrValuesCustom = values();
        int length = transportActionArrValuesCustom.length;
        TransportAction[] transportActionArr = new TransportAction[length];
        System.arraycopy(transportActionArrValuesCustom, 0, transportActionArr, 0, length);
        return transportActionArr;
    }

    public static TransportAction[] valueOfCommaSeparatedList(String s) {
        String[] strings = ModelUtil.fromCommaSeparatedList(s);
        if (strings == null) {
            return new TransportAction[0];
        }
        List<TransportAction> result = new ArrayList<>();
        for (String taString : strings) {
            for (TransportAction ta : valuesCustom()) {
                if (ta.name().equals(taString)) {
                    result.add(ta);
                }
            }
        }
        return (TransportAction[]) result.toArray(new TransportAction[result.size()]);
    }
}
