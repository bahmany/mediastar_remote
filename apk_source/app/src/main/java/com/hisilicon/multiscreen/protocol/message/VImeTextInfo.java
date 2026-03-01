package com.hisilicon.multiscreen.protocol.message;

/* loaded from: classes.dex */
public class VImeTextInfo {
    public static final String INPUT_CURSOR = "cursor";
    public static final String INPUT_OPTION = "imeOptions";
    public static final String INPUT_TEXT = "text";
    public static final String INPUT_TYPE = "inputType";
    public static final String REAL_END_SELECTION = "realEndSelection";
    public static final String REAL_START_SELECTION = "realStartSelection";
    public static final String REAL_TEXT = "realText ";
    public static final String SRC_END_SELECTION = "srcEndSelection";
    public static final String SRC_START_SELECTION = "srcStartSelection";
    public static final String SRC_TEXT = "srcText";
    public static final String TEXT_SEQUENCE = "sequence";
    private int mImeOption;
    private int mInputType;
    private int mRealEndSelection;
    private int mRealStartSelection;
    private String mRealText;
    private int mSrcEndSelection;
    private int mSrcStartSelection;
    private String mSrcText;

    public VImeTextInfo() {
        this.mInputType = 0;
        this.mImeOption = 0;
        this.mSrcText = "";
        this.mRealText = "";
        this.mSrcStartSelection = 0;
        this.mSrcEndSelection = 0;
        this.mRealStartSelection = 0;
        this.mRealEndSelection = 0;
    }

    public VImeTextInfo(int inputType, int imeOption, String realText, int realStartSelection, int realEndSelection) {
        this.mInputType = 0;
        this.mImeOption = 0;
        this.mSrcText = "";
        this.mRealText = "";
        this.mSrcStartSelection = 0;
        this.mSrcEndSelection = 0;
        this.mRealStartSelection = 0;
        this.mRealEndSelection = 0;
        this.mInputType = inputType;
        this.mImeOption = imeOption;
        this.mRealText = realText;
        this.mRealStartSelection = realStartSelection;
        this.mRealEndSelection = realEndSelection;
    }

    public VImeTextInfo(String realText, int realStartSelection, int realEndSelection) {
        this.mInputType = 0;
        this.mImeOption = 0;
        this.mSrcText = "";
        this.mRealText = "";
        this.mSrcStartSelection = 0;
        this.mSrcEndSelection = 0;
        this.mRealStartSelection = 0;
        this.mRealEndSelection = 0;
        this.mRealText = realText;
        this.mRealStartSelection = realStartSelection;
        this.mRealEndSelection = realEndSelection;
    }

    public VImeTextInfo(String srcText, int srcStartSelection, int srcEndSelection, String realText, int realStartSelection, int realEndSelection) {
        this.mInputType = 0;
        this.mImeOption = 0;
        this.mSrcText = "";
        this.mRealText = "";
        this.mSrcStartSelection = 0;
        this.mSrcEndSelection = 0;
        this.mRealStartSelection = 0;
        this.mRealEndSelection = 0;
        this.mSrcText = srcText;
        this.mSrcStartSelection = srcStartSelection;
        this.mSrcEndSelection = srcEndSelection;
        this.mRealText = realText;
        this.mRealStartSelection = realStartSelection;
        this.mRealEndSelection = realEndSelection;
    }

    public int getInputType() {
        return this.mInputType;
    }

    public void setInputType(int inputType) {
        this.mInputType = inputType;
    }

    public int getImeOption() {
        return this.mImeOption;
    }

    public void setImeOption(int imeOption) {
        this.mImeOption = imeOption;
    }

    public String getSrcText() {
        return this.mSrcText;
    }

    public void setSrcText(String srcText) {
        this.mSrcText = srcText;
    }

    public int getSrcStartSelection() {
        return this.mSrcStartSelection;
    }

    public void setSrcStartSelection(int srcStartCursor) {
        this.mSrcStartSelection = srcStartCursor;
    }

    public int getSrcEndSelection() {
        return this.mSrcEndSelection;
    }

    public void setSrcEndSelection(int srcEndSelection) {
        this.mSrcEndSelection = srcEndSelection;
    }

    public String getRealText() {
        return this.mRealText;
    }

    public void setRealText(String realText) {
        this.mRealText = realText;
    }

    public int getRealStartSelection() {
        return this.mRealStartSelection;
    }

    public void setRealStartSelection(int realStartSelection) {
        this.mRealStartSelection = realStartSelection;
    }

    public int getRealEndSelection() {
        return this.mRealEndSelection;
    }

    public void setRealEndSelection(int realEndSelection) {
        this.mRealEndSelection = realEndSelection;
    }
}
