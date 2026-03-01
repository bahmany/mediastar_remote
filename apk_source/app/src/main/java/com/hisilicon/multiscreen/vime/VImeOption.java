package com.hisilicon.multiscreen.vime;

/* loaded from: classes.dex */
public enum VImeOption {
    ACTION_DONE("DONE", 6),
    ACTION_GO("GO", 2),
    ACTION_NEXT("NEXT", 5),
    ACTION_NONE("EndInput", 1),
    ACTION_SEARCH("SEARCH", 3),
    ACTION_SEND("SEND", 4),
    ACTION_UNSPECIFIED("UNSPECIFIED", 0),
    FLAG_NO_ACCESSORY_ACTION("EndInput", 536870912),
    FLAG_NO_ENTER_ACTION("EndInput", 1073741824),
    HIDE_KEYBOARD("FinishKeyboard", -1);

    private final int mOptionIndex;
    private final String mOptionName;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static VImeOption[] valuesCustom() {
        VImeOption[] vImeOptionArrValuesCustom = values();
        int length = vImeOptionArrValuesCustom.length;
        VImeOption[] vImeOptionArr = new VImeOption[length];
        System.arraycopy(vImeOptionArrValuesCustom, 0, vImeOptionArr, 0, length);
        return vImeOptionArr;
    }

    VImeOption(String name, int index) {
        this.mOptionName = name;
        this.mOptionIndex = index;
    }

    public static VImeOption getOption(int index) {
        for (VImeOption option : valuesCustom()) {
            if (option.getIndex() == index) {
                return option;
            }
        }
        return null;
    }

    public String getName() {
        return this.mOptionName;
    }

    public int getIndex() {
        return this.mOptionIndex;
    }
}
