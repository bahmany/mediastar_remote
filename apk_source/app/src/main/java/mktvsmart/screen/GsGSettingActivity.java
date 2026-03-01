package mktvsmart.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.view.Switch;

/* loaded from: classes.dex */
public class GsGSettingActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private MessageProcessor mMsgProc;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        this.mMsgProc = MessageProcessor.obtain();
    }

    private void initView() {
        setContentView(R.layout.my_setting_layout);
        Button backBtn = (Button) findViewById(R.id.back_btn);
        Switch phoneAndSmsRemindSwitch = (Switch) findViewById(R.id.phone_and_sms_switch);
        RelativeLayout playerSettingLayout = (RelativeLayout) findViewById(R.id.player_setting_option);
        RelativeLayout aboutMeLayout = (RelativeLayout) findViewById(R.id.about_me_option);
        boolean phoneAndSmsRemindSetting = new EditPhoneAndSmsRemindSettingFile(this).getPhoneAndSmsRemindSetting();
        phoneAndSmsRemindSwitch.setChecked(phoneAndSmsRemindSetting);
        phoneAndSmsRemindSwitch.setOnCheckedChangeListener(this);
        playerSettingLayout.setOnClickListener(this);
        aboutMeLayout.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.back_btn /* 2131492959 */:
                onBackPressed();
                break;
            case R.id.player_setting_option /* 2131493347 */:
                intent.setClass(this, GsPlayerSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.about_me_option /* 2131493348 */:
                intent.setClass(this, GsAboutMeActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.phone_and_sms_switch /* 2131493346 */:
                new EditPhoneAndSmsRemindSettingFile(this).setPhoneAndSmsRemindSetting(isChecked);
                this.mMsgProc.postEmptyMessage(GlobalConstantValue.GSCMD_NOTIFY_CALL_AND_SMS_REMIND_CHANGED);
                break;
        }
    }
}
