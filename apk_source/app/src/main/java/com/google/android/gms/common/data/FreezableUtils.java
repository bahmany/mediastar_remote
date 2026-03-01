package com.google.android.gms.common.data;

import java.util.ArrayList;
import java.util.Iterator;
import javax.mail.internet.ParameterList;

/* loaded from: classes.dex */
public final class FreezableUtils {
    public static <T, E extends Freezable<T>> ArrayList<T> freeze(ArrayList<E> arrayList) {
        ParameterList.MultiValue multiValue = (ArrayList<T>) new ArrayList(arrayList.size());
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            multiValue.add(arrayList.get(i).freeze());
        }
        return multiValue;
    }

    public static <T, E extends Freezable<T>> ArrayList<T> freeze(E[] eArr) {
        ParameterList.MultiValue multiValue = (ArrayList<T>) new ArrayList(eArr.length);
        for (E e : eArr) {
            multiValue.add(e.freeze());
        }
        return multiValue;
    }

    public static <T, E extends Freezable<T>> ArrayList<T> freezeIterable(Iterable<E> iterable) {
        ParameterList.MultiValue multiValue = (ArrayList<T>) new ArrayList();
        Iterator<E> it = iterable.iterator();
        while (it.hasNext()) {
            multiValue.add(it.next().freeze());
        }
        return multiValue;
    }
}
