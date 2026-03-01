package com.hisilicon.multiscreen.protocol.message;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class Argument {
    private int mValueNum = 0;
    private ArrayList<ArgumentValue> mValueList = new ArrayList<>();

    public void setArgumentValueNum(int valueNum) {
        this.mValueNum = valueNum;
    }

    public int getArgumentValueNum() {
        return this.mValueNum;
    }

    public void addArgumentValue(ArgumentValue value) {
        if (this.mValueList == null) {
            this.mValueList = new ArrayList<>();
        }
        this.mValueList.add(value);
    }

    public boolean removeArgumentValue(int index) {
        if (this.mValueList == null || index >= this.mValueList.size()) {
            return false;
        }
        this.mValueList.remove(index);
        return true;
    }

    public boolean removeArgumentValue(ArgumentValue argumentValue) {
        if (this.mValueList == null || this.mValueList.size() <= 0 || !this.mValueList.contains(argumentValue)) {
            return false;
        }
        this.mValueList.remove(argumentValue);
        return true;
    }

    public ArgumentValue getArgumentValue(int index) {
        if (this.mValueList != null && index < this.mValueList.size()) {
            return this.mValueList.get(index);
        }
        return null;
    }

    public ArrayList<ArgumentValue> getArgumentValueList() {
        return this.mValueList;
    }

    public int countArgumentValueNum() {
        return this.mValueList.size();
    }
}
