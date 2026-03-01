package mktvsmart.screen.dataconvert.model;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class DataConvertSortModel {
    private int mMacroFlag;
    private int mSortType;
    private int mTvState;
    private ArrayList<String> sortTypeList;

    public int getmSortType() {
        return this.mSortType;
    }

    public void setmSortType(int mSortType) {
        this.mSortType = mSortType;
    }

    public int getmTvState() {
        return this.mTvState;
    }

    public void setmTvState(int mTvState) {
        this.mTvState = mTvState;
    }

    public int getmMacroFlag() {
        return this.mMacroFlag;
    }

    public void setmMacroFlag(int mMacroFlag) {
        this.mMacroFlag = mMacroFlag;
    }

    public ArrayList<String> getSortTypeList() {
        return this.sortTypeList;
    }

    public void setSortTypeList(ArrayList<String> allSortTypes) {
        this.sortTypeList = allSortTypes;
    }
}
