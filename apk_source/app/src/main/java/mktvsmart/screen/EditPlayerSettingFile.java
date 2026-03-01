package mktvsmart.screen;

import android.content.Context;
import android.content.SharedPreferences;

/* loaded from: classes.dex */
public class EditPlayerSettingFile {
    private static final String SHAREDPREFERENCES_NAME = "player_setting";
    private Context mContext;
    private final String PLAYER_PKG_NAME = "player_pkg_name";
    private final String PLAYER_BUILT_IN_FLAG = "player_built_in_flag";

    public EditPlayerSettingFile(Context mContext) {
        this.mContext = mContext;
    }

    public String getPlayerPkgName() {
        return this.mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 1).getString("player_pkg_name", "");
    }

    public void setPlayerPkgName(String pkgName) {
        SharedPreferences settings = this.mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("player_pkg_name", pkgName);
        editor.commit();
    }

    public boolean getPlayerBuiltInFlag() {
        return this.mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 1).getBoolean("player_built_in_flag", false);
    }

    public void setPlayerBuiltInFlag(boolean flag) {
        SharedPreferences settings = this.mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("player_built_in_flag", flag);
        editor.commit();
    }
}
