package mktvsmart.screen.dataconvert.model;

/* loaded from: classes.dex */
public class DataConvertFavChannelModel {
    private String ProgramId;
    private int selectListType;
    private int tvState;

    public int getChannelTpye() {
        return this.tvState;
    }

    public void setChannelTpye(int type) {
        this.tvState = type;
    }

    public int getSelectListType() {
        return this.selectListType;
    }

    public void setSelectListType(int type) {
        this.selectListType = type;
    }

    public String GetProgramId() {
        return this.ProgramId;
    }

    public void SetProgramId(String id) {
        this.ProgramId = id;
    }
}
