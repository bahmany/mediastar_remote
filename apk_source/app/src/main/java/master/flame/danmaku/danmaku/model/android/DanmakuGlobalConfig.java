package master.flame.danmaku.danmaku.model.android;

import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import master.flame.danmaku.controller.DanmakuFilters;
import master.flame.danmaku.danmaku.model.AlphaValue;
import master.flame.danmaku.danmaku.model.GlobalFlagValues;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;

/* loaded from: classes.dex */
public class DanmakuGlobalConfig {
    public static final int DANMAKU_STYLE_DEFAULT = -1;
    public static final int DANMAKU_STYLE_NONE = 0;
    public static final int DANMAKU_STYLE_PROJECTION = 3;
    public static final int DANMAKU_STYLE_SHADOW = 1;
    public static final int DANMAKU_STYLE_STROKEN = 2;
    public static DanmakuGlobalConfig DEFAULT = new DanmakuGlobalConfig();
    private ArrayList<ConfigChangedCallback> mCallbackList;
    public Typeface mFont = null;
    public int transparency = AlphaValue.MAX;
    public boolean isTranslucent = false;
    public float scaleTextSize = 1.0f;
    public boolean isTextScaled = false;
    public boolean FTDanmakuVisibility = true;
    public boolean FBDanmakuVisibility = true;
    public boolean L2RDanmakuVisibility = true;
    public boolean R2LDanmakuVisibility = true;
    public boolean SecialDanmakuVisibility = true;
    List<Integer> mFilterTypes = new ArrayList();
    public int maximumNumsInScreen = -1;
    public float scrollSpeedFactor = 1.0f;
    public int refreshRateMS = 15;
    public BorderType shadowType = BorderType.SHADOW;
    public int shadowRadius = 3;
    List<Integer> mColorValueWhiteList = new ArrayList();
    List<Integer> mUserIdBlackList = new ArrayList();
    List<String> mUserHashBlackList = new ArrayList();
    private boolean mBlockGuestDanmaku = false;
    private boolean mDuplicateMergingEnable = false;

    public enum BorderType {
        NONE,
        SHADOW,
        STROKEN;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static BorderType[] valuesCustom() {
            BorderType[] borderTypeArrValuesCustom = values();
            int length = borderTypeArrValuesCustom.length;
            BorderType[] borderTypeArr = new BorderType[length];
            System.arraycopy(borderTypeArrValuesCustom, 0, borderTypeArr, 0, length);
            return borderTypeArr;
        }
    }

    public interface ConfigChangedCallback {
        boolean onDanmakuConfigChanged(DanmakuGlobalConfig danmakuGlobalConfig, DanmakuConfigTag danmakuConfigTag, Object... objArr);
    }

    public enum DanmakuConfigTag {
        FT_DANMAKU_VISIBILITY,
        FB_DANMAKU_VISIBILITY,
        L2R_DANMAKU_VISIBILITY,
        R2L_DANMAKU_VISIBILIY,
        SPECIAL_DANMAKU_VISIBILITY,
        TYPEFACE,
        TRANSPARENCY,
        SCALE_TEXTSIZE,
        MAXIMUM_NUMS_IN_SCREEN,
        DANMAKU_STYLE,
        DANMAKU_BOLD,
        COLOR_VALUE_WHITE_LIST,
        USER_ID_BLACK_LIST,
        USER_HASH_BLACK_LIST,
        SCROLL_SPEED_FACTOR,
        BLOCK_GUEST_DANMAKU,
        DUPLICATE_MERGING_ENABLED;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static DanmakuConfigTag[] valuesCustom() {
            DanmakuConfigTag[] danmakuConfigTagArrValuesCustom = values();
            int length = danmakuConfigTagArrValuesCustom.length;
            DanmakuConfigTag[] danmakuConfigTagArr = new DanmakuConfigTag[length];
            System.arraycopy(danmakuConfigTagArrValuesCustom, 0, danmakuConfigTagArr, 0, length);
            return danmakuConfigTagArr;
        }

        public boolean isVisibilityRelatedTag() {
            return equals(FT_DANMAKU_VISIBILITY) || equals(FB_DANMAKU_VISIBILITY) || equals(L2R_DANMAKU_VISIBILITY) || equals(R2L_DANMAKU_VISIBILIY) || equals(SPECIAL_DANMAKU_VISIBILITY) || equals(COLOR_VALUE_WHITE_LIST) || equals(USER_ID_BLACK_LIST);
        }
    }

    public DanmakuGlobalConfig setTypeface(Typeface font) {
        if (this.mFont != font) {
            this.mFont = font;
            AndroidDisplayer.clearTextHeightCache();
            AndroidDisplayer.setTypeFace(font);
            notifyConfigureChanged(DanmakuConfigTag.TYPEFACE, new Object[0]);
        }
        return this;
    }

    public DanmakuGlobalConfig setDanmakuTransparency(float p) {
        int newTransparency = (int) (AlphaValue.MAX * p);
        if (newTransparency != this.transparency) {
            this.transparency = newTransparency;
            this.isTranslucent = newTransparency != AlphaValue.MAX;
            notifyConfigureChanged(DanmakuConfigTag.TRANSPARENCY, Float.valueOf(p));
        }
        return this;
    }

    public DanmakuGlobalConfig setScaleTextSize(float p) {
        if (this.scaleTextSize != p) {
            this.scaleTextSize = p;
            AndroidDisplayer.clearTextHeightCache();
            GlobalFlagValues.updateMeasureFlag();
            GlobalFlagValues.updateVisibleFlag();
            notifyConfigureChanged(DanmakuConfigTag.SCALE_TEXTSIZE, Float.valueOf(p));
        }
        this.isTextScaled = this.scaleTextSize != 1.0f;
        return this;
    }

    public boolean getFTDanmakuVisibility() {
        return this.FTDanmakuVisibility;
    }

    public DanmakuGlobalConfig setFTDanmakuVisibility(boolean visible) {
        setDanmakuVisible(visible, 5);
        setFilterData(DanmakuFilters.TAG_TYPE_DANMAKU_FILTER, this.mFilterTypes);
        if (this.FTDanmakuVisibility != visible) {
            this.FTDanmakuVisibility = visible;
            notifyConfigureChanged(DanmakuConfigTag.FT_DANMAKU_VISIBILITY, Boolean.valueOf(visible));
        }
        return this;
    }

    private <T> void setFilterData(String tag, T data) {
        DanmakuFilters.getDefault().get(tag).setData(data);
    }

    private void setDanmakuVisible(boolean visible, int type) {
        if (visible) {
            this.mFilterTypes.remove(Integer.valueOf(type));
        } else if (!this.mFilterTypes.contains(Integer.valueOf(type))) {
            this.mFilterTypes.add(Integer.valueOf(type));
        }
    }

    public boolean getFBDanmakuVisibility() {
        return this.FBDanmakuVisibility;
    }

    public DanmakuGlobalConfig setFBDanmakuVisibility(boolean visible) {
        setDanmakuVisible(visible, 4);
        setFilterData(DanmakuFilters.TAG_TYPE_DANMAKU_FILTER, this.mFilterTypes);
        if (this.FBDanmakuVisibility != visible) {
            this.FBDanmakuVisibility = visible;
            notifyConfigureChanged(DanmakuConfigTag.FB_DANMAKU_VISIBILITY, Boolean.valueOf(visible));
        }
        return this;
    }

    public boolean getL2RDanmakuVisibility() {
        return this.L2RDanmakuVisibility;
    }

    public DanmakuGlobalConfig setL2RDanmakuVisibility(boolean visible) {
        setDanmakuVisible(visible, 6);
        setFilterData(DanmakuFilters.TAG_TYPE_DANMAKU_FILTER, this.mFilterTypes);
        if (this.L2RDanmakuVisibility != visible) {
            this.L2RDanmakuVisibility = visible;
            notifyConfigureChanged(DanmakuConfigTag.L2R_DANMAKU_VISIBILITY, Boolean.valueOf(visible));
        }
        return this;
    }

    public boolean getR2LDanmakuVisibility() {
        return this.R2LDanmakuVisibility;
    }

    public DanmakuGlobalConfig setR2LDanmakuVisibility(boolean visible) {
        setDanmakuVisible(visible, 1);
        setFilterData(DanmakuFilters.TAG_TYPE_DANMAKU_FILTER, this.mFilterTypes);
        if (this.R2LDanmakuVisibility != visible) {
            this.R2LDanmakuVisibility = visible;
            notifyConfigureChanged(DanmakuConfigTag.R2L_DANMAKU_VISIBILIY, Boolean.valueOf(visible));
        }
        return this;
    }

    public boolean getSecialDanmakuVisibility() {
        return this.SecialDanmakuVisibility;
    }

    public DanmakuGlobalConfig setSpecialDanmakuVisibility(boolean visible) {
        setDanmakuVisible(visible, 7);
        setFilterData(DanmakuFilters.TAG_TYPE_DANMAKU_FILTER, this.mFilterTypes);
        if (this.SecialDanmakuVisibility != visible) {
            this.SecialDanmakuVisibility = visible;
            notifyConfigureChanged(DanmakuConfigTag.SPECIAL_DANMAKU_VISIBILITY, Boolean.valueOf(visible));
        }
        return this;
    }

    public DanmakuGlobalConfig setMaximumVisibleSizeInScreen(int maxSize) throws Exception {
        this.maximumNumsInScreen = maxSize;
        if (maxSize == 0) {
            DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_QUANTITY_DANMAKU_FILTER);
            DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_ELAPSED_TIME_FILTER);
            notifyConfigureChanged(DanmakuConfigTag.MAXIMUM_NUMS_IN_SCREEN, Integer.valueOf(maxSize));
        } else if (maxSize == -1) {
            DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_QUANTITY_DANMAKU_FILTER);
            DanmakuFilters.getDefault().registerFilter(DanmakuFilters.TAG_ELAPSED_TIME_FILTER);
            notifyConfigureChanged(DanmakuConfigTag.MAXIMUM_NUMS_IN_SCREEN, Integer.valueOf(maxSize));
        } else {
            setFilterData(DanmakuFilters.TAG_QUANTITY_DANMAKU_FILTER, Integer.valueOf(maxSize));
            notifyConfigureChanged(DanmakuConfigTag.MAXIMUM_NUMS_IN_SCREEN, Integer.valueOf(maxSize));
        }
        return this;
    }

    public DanmakuGlobalConfig setDanmakuStyle(int style, float... values) {
        switch (style) {
            case -1:
            case 2:
                AndroidDisplayer.CONFIG_HAS_SHADOW = false;
                AndroidDisplayer.CONFIG_HAS_STROKE = true;
                AndroidDisplayer.CONFIG_HAS_PROJECTION = false;
                AndroidDisplayer.setPaintStorkeWidth(values[0]);
                break;
            case 0:
                AndroidDisplayer.CONFIG_HAS_SHADOW = false;
                AndroidDisplayer.CONFIG_HAS_STROKE = false;
                AndroidDisplayer.CONFIG_HAS_PROJECTION = false;
                break;
            case 1:
                AndroidDisplayer.CONFIG_HAS_SHADOW = true;
                AndroidDisplayer.CONFIG_HAS_STROKE = false;
                AndroidDisplayer.CONFIG_HAS_PROJECTION = false;
                AndroidDisplayer.setShadowRadius(values[0]);
                break;
            case 3:
                AndroidDisplayer.CONFIG_HAS_SHADOW = false;
                AndroidDisplayer.CONFIG_HAS_STROKE = false;
                AndroidDisplayer.CONFIG_HAS_PROJECTION = true;
                AndroidDisplayer.setProjectionConfig(values[0], values[1], (int) values[2]);
                break;
        }
        notifyConfigureChanged(DanmakuConfigTag.DANMAKU_STYLE, Integer.valueOf(style), Float.valueOf(values[0]));
        return this;
    }

    public DanmakuGlobalConfig setDanmakuBold(boolean bold) {
        AndroidDisplayer.setFakeBoldText(bold);
        notifyConfigureChanged(DanmakuConfigTag.DANMAKU_BOLD, Boolean.valueOf(bold));
        return this;
    }

    public DanmakuGlobalConfig setColorValueWhiteList(Integer... colors) {
        this.mColorValueWhiteList.clear();
        if (colors == null || colors.length == 0) {
            DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_TEXT_COLOR_DANMAKU_FILTER);
        } else {
            Collections.addAll(this.mColorValueWhiteList, colors);
            setFilterData(DanmakuFilters.TAG_TEXT_COLOR_DANMAKU_FILTER, this.mColorValueWhiteList);
        }
        notifyConfigureChanged(DanmakuConfigTag.COLOR_VALUE_WHITE_LIST, this.mColorValueWhiteList);
        return this;
    }

    public List<Integer> getColorValueWhiteList() {
        return this.mColorValueWhiteList;
    }

    public DanmakuGlobalConfig setUserHashBlackList(String... hashes) {
        this.mUserHashBlackList.clear();
        if (hashes == null || hashes.length == 0) {
            DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_USER_HASH_FILTER);
        } else {
            Collections.addAll(this.mUserHashBlackList, hashes);
            setFilterData(DanmakuFilters.TAG_USER_HASH_FILTER, this.mUserHashBlackList);
        }
        notifyConfigureChanged(DanmakuConfigTag.USER_HASH_BLACK_LIST, this.mUserHashBlackList);
        return this;
    }

    public DanmakuGlobalConfig removeUserHashBlackList(String... hashes) {
        if (hashes != null && hashes.length != 0) {
            for (String hash : hashes) {
                this.mUserHashBlackList.remove(hash);
            }
            setFilterData(DanmakuFilters.TAG_USER_HASH_FILTER, this.mUserHashBlackList);
            notifyConfigureChanged(DanmakuConfigTag.USER_HASH_BLACK_LIST, this.mUserHashBlackList);
        }
        return this;
    }

    public DanmakuGlobalConfig addUserHashBlackList(String... hashes) {
        if (hashes != null && hashes.length != 0) {
            Collections.addAll(this.mUserHashBlackList, hashes);
            setFilterData(DanmakuFilters.TAG_USER_HASH_FILTER, this.mUserHashBlackList);
            notifyConfigureChanged(DanmakuConfigTag.USER_HASH_BLACK_LIST, this.mUserHashBlackList);
        }
        return this;
    }

    public List<String> getUserHashBlackList() {
        return this.mUserHashBlackList;
    }

    public DanmakuGlobalConfig setUserIdBlackList(Integer... ids) {
        this.mUserIdBlackList.clear();
        if (ids == null || ids.length == 0) {
            DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_USER_ID_FILTER);
        } else {
            Collections.addAll(this.mUserIdBlackList, ids);
            setFilterData(DanmakuFilters.TAG_USER_ID_FILTER, this.mUserIdBlackList);
        }
        notifyConfigureChanged(DanmakuConfigTag.USER_ID_BLACK_LIST, this.mUserIdBlackList);
        return this;
    }

    public DanmakuGlobalConfig removeUserIdBlackList(Integer... ids) {
        if (ids != null && ids.length != 0) {
            for (Integer id : ids) {
                this.mUserIdBlackList.remove(id);
            }
            setFilterData(DanmakuFilters.TAG_USER_ID_FILTER, this.mUserIdBlackList);
            notifyConfigureChanged(DanmakuConfigTag.USER_ID_BLACK_LIST, this.mUserIdBlackList);
        }
        return this;
    }

    public DanmakuGlobalConfig addUserIdBlackList(Integer... ids) {
        if (ids != null && ids.length != 0) {
            Collections.addAll(this.mUserIdBlackList, ids);
            setFilterData(DanmakuFilters.TAG_USER_ID_FILTER, this.mUserIdBlackList);
            notifyConfigureChanged(DanmakuConfigTag.USER_ID_BLACK_LIST, this.mUserIdBlackList);
        }
        return this;
    }

    public List<Integer> getUserIdBlackList() {
        return this.mUserIdBlackList;
    }

    public DanmakuGlobalConfig blockGuestDanmaku(boolean block) {
        if (this.mBlockGuestDanmaku != block) {
            this.mBlockGuestDanmaku = block;
            if (block) {
                setFilterData(DanmakuFilters.TAG_GUEST_FILTER, Boolean.valueOf(block));
            } else {
                DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_GUEST_FILTER);
            }
            notifyConfigureChanged(DanmakuConfigTag.BLOCK_GUEST_DANMAKU, Boolean.valueOf(block));
        }
        return this;
    }

    public DanmakuGlobalConfig setScrollSpeedFactor(float p) {
        if (this.scrollSpeedFactor != p) {
            this.scrollSpeedFactor = p;
            DanmakuFactory.updateDurationFactor(p);
            GlobalFlagValues.updateMeasureFlag();
            GlobalFlagValues.updateVisibleFlag();
            notifyConfigureChanged(DanmakuConfigTag.SCROLL_SPEED_FACTOR, Float.valueOf(p));
        }
        return this;
    }

    public DanmakuGlobalConfig setDuplicateMergingEnabled(boolean enable) {
        if (this.mDuplicateMergingEnable != enable) {
            this.mDuplicateMergingEnable = enable;
            notifyConfigureChanged(DanmakuConfigTag.DUPLICATE_MERGING_ENABLED, Boolean.valueOf(enable));
        }
        return this;
    }

    public boolean isDuplicateMergingEnabled() {
        return this.mDuplicateMergingEnable;
    }

    public void registerConfigChangedCallback(ConfigChangedCallback listener) {
        if (this.mCallbackList == null) {
            this.mCallbackList = new ArrayList<>();
        }
        this.mCallbackList.add(listener);
    }

    public void unregisterConfigChangedCallback(ConfigChangedCallback listener) {
        if (this.mCallbackList != null) {
            this.mCallbackList.remove(listener);
        }
    }

    private void notifyConfigureChanged(DanmakuConfigTag tag, Object... values) {
        if (this.mCallbackList != null) {
            Iterator<ConfigChangedCallback> it = this.mCallbackList.iterator();
            while (it.hasNext()) {
                ConfigChangedCallback cb = it.next();
                cb.onDanmakuConfigChanged(this, tag, values);
            }
        }
    }
}
