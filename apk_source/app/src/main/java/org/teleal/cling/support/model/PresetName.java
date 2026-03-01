package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum PresetName {
    FactoryDefault;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static PresetName[] valuesCustom() {
        PresetName[] presetNameArrValuesCustom = values();
        int length = presetNameArrValuesCustom.length;
        PresetName[] presetNameArr = new PresetName[length];
        System.arraycopy(presetNameArrValuesCustom, 0, presetNameArr, 0, length);
        return presetNameArr;
    }
}
