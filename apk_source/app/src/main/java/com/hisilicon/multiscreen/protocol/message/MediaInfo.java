package com.hisilicon.multiscreen.protocol.message;

import com.hisilicon.multiscreen.protocol.utils.LogTool;
import java.io.DataInputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class MediaInfo {
    private static final int MAX_ACTOR_CODE = 1024;
    private static final int MAX_BKIMAGE_CODE = 1024;
    private static final int MAX_CATEGORY_CODE = 128;
    private static final int MAX_DESCRIPTION_CODE = 1024;
    private static final int MAX_DIRECTOR = 256;
    private static final int MAX_DURATION = 32;
    private static final int MAX_ITEMNAME_CODE = 128;
    private static final int MAX_LANGUAGE = 32;
    private static final int MAX_PROGRAM_CODE = 128;
    private static final int MAX_REGION = 128;
    static final String Tag = "MEDIA_INFO";
    private String mActor;
    private String mBKImage1;
    private String mBKImage2;
    private String mCategoryCode;
    private String mDescription;
    private String mDirector;
    private String mDuration;
    private String mItemName;
    private int mItemType;
    private String mLanguage;
    private int mPosition;
    private String mProgramCode;
    private int mRatingLevel;
    private String mRegion;
    private int mStatus;
    private String mUrl;

    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getProgramCode() {
        return this.mProgramCode;
    }

    public void setProgramCode(String programCode) {
        if (programCode.length() > 128) {
            LogTool.e("programCode exceed max length");
        }
        this.mProgramCode = programCode.substring(0, 128);
    }

    public String getItemName() {
        return this.mItemName;
    }

    public void setItem_name(String itemName) {
        if (itemName.length() > 128) {
            LogTool.e("itemName exceed max length");
        }
        this.mItemName = itemName.substring(0, 128);
    }

    public String getBKImage1() {
        return this.mBKImage1;
    }

    public void setBKImage1(String BKImage1) {
        if (BKImage1.length() > 1024) {
            LogTool.e("BKImage1 exceed max length");
        }
        this.mBKImage1 = BKImage1.substring(0, 1024);
    }

    public String getBKImage2() {
        return this.mBKImage2;
    }

    public void setBKImage2(String BKImage2) {
        if (BKImage2.length() > 1024) {
            LogTool.e("BKImage2 exceed max length");
        }
        this.mBKImage2 = BKImage2.substring(0, 1024);
    }

    public String getDescription() {
        return this.mDescription;
    }

    public void setDescription(String Description) {
        if (Description.length() > 1024) {
            LogTool.e("Description exceed max length");
        }
        this.mDescription = Description.substring(0, 1024);
    }

    public String getActor() {
        return this.mActor;
    }

    public void setActor(String Actor) {
        if (Actor.length() > 1024) {
            LogTool.e("Actor exceed max length");
        }
        this.mActor = Actor.substring(0, 1024);
    }

    public String getDirector() {
        return this.mDirector;
    }

    public void setDirector(String Director) {
        if (Director.length() > 256) {
            LogTool.e("Director exceed max length");
        }
        this.mDirector = Director.substring(0, 256);
    }

    public String getRegion() {
        return this.mRegion;
    }

    public void setRegion(String Region) {
        if (Region.length() > 128) {
            LogTool.e("Region exceed max length");
        }
        this.mRegion = Region.substring(0, 128);
    }

    public String getLanguage() {
        return this.mLanguage;
    }

    public void setLanguage(String Language) {
        if (Language.length() > 32) {
            LogTool.e("Language exceed max length");
        }
        this.mLanguage = Language.substring(0, 32);
    }

    public String getCategoryCode() {
        return this.mCategoryCode;
    }

    public void setCategoryCode(String CategoryCode) {
        if (CategoryCode.length() > 128) {
            LogTool.e("CategoryCode exceed max length");
        }
        this.mCategoryCode = CategoryCode.substring(0, 128);
    }

    public String getDuration() {
        return this.mDuration;
    }

    public void setDuration(String Duration) {
        if (Duration.length() > 32) {
            LogTool.e("Duration exceed max length");
        }
        this.mDuration = Duration.substring(0, 32);
    }

    public int getRatingLevel() {
        return this.mRatingLevel;
    }

    public void setRatingLevel(int RatingLevel) {
        this.mRatingLevel = RatingLevel;
    }

    public int getItemType() {
        return this.mItemType;
    }

    public void setItemType(int ItemType) {
        this.mItemType = ItemType;
    }

    public int getPosition() {
        return this.mPosition;
    }

    public void setPosition(int Position) {
        this.mPosition = Position;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public void setStatus(int Status) {
        this.mStatus = Status;
    }

    protected void readMediaInfo(DataInputStream in) throws IOException {
        try {
            this.mStatus = in.readInt();
            int programCodeLen = in.readInt();
            if (programCodeLen > 0) {
                byte[] buf = new byte[programCodeLen];
                in.read(buf);
                this.mProgramCode = new String(buf);
            }
            this.mItemType = in.readInt();
            int itemNameLen = in.readInt();
            if (itemNameLen > 0) {
                byte[] buf2 = new byte[itemNameLen];
                in.read(buf2);
                this.mItemName = new String(buf2);
            }
            int BKImage1Len = in.readInt();
            if (BKImage1Len > 0) {
                byte[] buf3 = new byte[BKImage1Len];
                in.read(buf3);
                this.mBKImage1 = new String(buf3);
            }
            int BKImage2Len = in.readInt();
            if (BKImage2Len > 0) {
                byte[] buf4 = new byte[BKImage2Len];
                in.read(buf4);
                this.mBKImage2 = new String(buf4);
            }
            this.mRatingLevel = in.readInt();
            int DescriptionLen = in.readInt();
            if (DescriptionLen > 0) {
                byte[] buf5 = new byte[DescriptionLen];
                in.read(buf5);
                this.mDescription = new String(buf5);
            }
            int ActorLen = in.readInt();
            if (ActorLen > 0) {
                byte[] buf6 = new byte[ActorLen];
                in.read(buf6);
                this.mActor = new String(buf6);
            }
            int DirectorLen = in.readInt();
            if (DirectorLen > 0) {
                byte[] buf7 = new byte[DirectorLen];
                in.read(buf7);
                this.mDirector = new String(buf7);
            }
            int RegionLen = in.readInt();
            if (RegionLen > 0) {
                byte[] buf8 = new byte[RegionLen];
                in.read(buf8);
                this.mRegion = new String(buf8);
            }
            int LanguageLen = in.readInt();
            if (LanguageLen > 0) {
                byte[] buf9 = new byte[LanguageLen];
                in.read(buf9);
                this.mLanguage = new String(buf9);
            }
            int CategoryCodeLen = in.readInt();
            if (CategoryCodeLen > 0) {
                byte[] buf10 = new byte[CategoryCodeLen];
                in.read(buf10);
                this.mCategoryCode = new String(buf10);
            }
            int DurationLen = in.readInt();
            if (DurationLen > 0) {
                byte[] buf11 = new byte[DurationLen];
                in.read(buf11);
                this.mDuration = new String(buf11);
            }
            this.mPosition = in.readInt();
        } catch (IOException e) {
            LogTool.e(e.getMessage());
        }
    }

    protected void mediaInfoPrint() {
        LogTool.v("recv media info status:" + this.mStatus);
        LogTool.v("programCode:" + this.mProgramCode);
        LogTool.v("ItemType:" + this.mItemType);
        LogTool.v("itemName:" + this.mItemName);
        LogTool.v("BKImage1:" + this.mBKImage1);
        LogTool.v("BKImage2:" + this.mBKImage2);
        LogTool.v("Description:" + this.mDescription);
        LogTool.v("Actor:" + this.mActor);
        LogTool.v("Director:" + this.mDirector);
        LogTool.v("Region:" + this.mRegion);
        LogTool.v("Language" + this.mLanguage);
        LogTool.v("Duration" + this.mDuration);
        LogTool.v("Position" + this.mPosition);
        LogTool.v("RatingLevel:" + this.mRatingLevel);
        LogTool.v("CategoryCode:" + this.mCategoryCode);
    }
}
