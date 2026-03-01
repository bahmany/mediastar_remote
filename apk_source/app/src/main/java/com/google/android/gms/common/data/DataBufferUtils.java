package com.google.android.gms.common.data;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Iterator;
import javax.mail.internet.ParameterList;

/* loaded from: classes.dex */
public final class DataBufferUtils {
    private DataBufferUtils() {
    }

    public static <T, E extends Freezable<T>> ArrayList<T> freezeAndClose(DataBuffer<E> dataBuffer) {
        ParameterList.MultiValue multiValue = (ArrayList<T>) new ArrayList(dataBuffer.getCount());
        try {
            Iterator<E> it = dataBuffer.iterator();
            while (it.hasNext()) {
                multiValue.add(it.next().freeze());
            }
            return multiValue;
        } finally {
            dataBuffer.close();
        }
    }

    public static boolean hasData(DataBuffer<?> buffer) {
        return buffer != null && buffer.getCount() > 0;
    }

    public static boolean hasNextPage(DataBuffer<?> buffer) {
        Bundle bundleGz = buffer.gz();
        return (bundleGz == null || bundleGz.getString("next_page_token") == null) ? false : true;
    }

    public static boolean hasPrevPage(DataBuffer<?> buffer) {
        Bundle bundleGz = buffer.gz();
        return (bundleGz == null || bundleGz.getString("prev_page_token") == null) ? false : true;
    }
}
