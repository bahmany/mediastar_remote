package mktvsmart.screen.dataconvert.model;

/* loaded from: classes.dex */
public class DataConvertChannelTypeModel {
    public static int current_channel_tv_radio_type;
    private int isFavList;
    private int selectListType;
    private int tvRadioKeyPress;

    public static int getCurrent_channel_tv_radio_type() {
        return current_channel_tv_radio_type;
    }

    public static void setCurrent_channel_tv_radio_type(int current_channel_tv_radio_type2) {
        current_channel_tv_radio_type = current_channel_tv_radio_type2;
    }

    public int getTvRadioKeyPress() {
        return this.tvRadioKeyPress;
    }

    public void setTvRadioKeyPress(int tvRadioKeyPress) {
        this.tvRadioKeyPress = tvRadioKeyPress;
    }

    public int getIsFavList() {
        return this.isFavList;
    }

    public void setIsFavList(int flag) {
        this.isFavList = flag;
    }

    public int getSelectListType() {
        return this.selectListType;
    }

    public void setSelectListType(int type) {
        this.selectListType = type;
    }
}
