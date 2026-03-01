package mktvsmart.screen;

import android.content.Context;
import android.content.SharedPreferences;

/* loaded from: classes.dex */
public class EditPhoneAndSmsRemindSettingFile {
    private static final String SHAREDPREFERENCES_NAME = "phone_and_sms_remind_setting_file";
    private final String PHONE_AND_SMS_REMIND_SETTING = "phone_and_sms_remind_setting";
    private Context mContext;

    public EditPhoneAndSmsRemindSettingFile(Context mContext) {
        this.mContext = mContext;
    }

    public boolean getPhoneAndSmsRemindSetting() {
        return this.mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 1).getBoolean("phone_and_sms_remind_setting", false);
    }

    public void setPhoneAndSmsRemindSetting(boolean phoneAndSmsRemindSetting) {
        SharedPreferences settings = this.mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("phone_and_sms_remind_setting", phoneAndSmsRemindSetting);
        editor.commit();
    }
}
