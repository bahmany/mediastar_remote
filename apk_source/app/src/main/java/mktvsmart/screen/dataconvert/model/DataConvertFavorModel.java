package mktvsmart.screen.dataconvert.model;

/* loaded from: classes.dex */
public class DataConvertFavorModel {
    public static int favorNum;
    int favNameID;
    int favorIndex;
    String favorName;

    public String GetFavorName() {
        return this.favorName;
    }

    public void SetFavorName(String name) {
        this.favorName = name;
    }

    public int GetFavorIndex() {
        return this.favorIndex;
    }

    public void SetFavorIndex(int index) {
        this.favorIndex = index;
    }

    public void setFavorTypeID(int favTypeID) {
        this.favNameID = favTypeID;
    }

    public int getFavorTypeID() {
        return this.favNameID;
    }
}
