package mktvsmart.screen;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import mktvsmart.screen.dataconvert.model.DataConvertControlModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.view.AgeRatingWheel;
import mktvsmart.screen.view.Switch;

/* loaded from: classes.dex */
public class GsLockEditActivity extends Activity {
    private int ageRating;
    private RelativeLayout ageRatingLayout;
    private TextView ageRatingText;
    private Button cancelButton;
    private List<DataConvertControlModel> controlModels;
    private Dialog dialog;
    private RelativeLayout editChannelLayout;
    private Switch editChannelLockSwitch;
    private TextView editChannelLockText;
    private RelativeLayout installationLockLayout;
    private Switch installationLockSwitch;
    private TextView installationLockText;
    private RelativeLayout networkLockLayout;
    private Switch networkLockSwitch;
    private TextView networkLockText;
    private DataParser parser;
    private RelativeLayout passwordLockLayout;
    private Switch passwordLockSwitch;
    private TextView passwordLockText;
    private Button saveButton;
    private RelativeLayout serviceLockLayout;
    private Switch serviceLockSwitch;
    private TextView serviceLockText;
    private RelativeLayout settingsLockLayout;
    private Switch settingsLockSwitch;
    private TextView settingsLockText;
    private Socket tcpSocket;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_edit_layout);
        CreateSocket cSocket = new CreateSocket("", 0);
        try {
            this.tcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.parser = ParserFactory.getParser();
        byte[] data = getIntent().getByteArrayExtra("ParentalControlData");
        int dataLength = getIntent().getIntExtra("DataLength", -1);
        InputStream inStream = new ByteArrayInputStream(data, 0, dataLength);
        try {
            this.controlModels = this.parser.parse(inStream, 2);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        this.serviceLockText = (TextView) findViewById(R.id.service_lock_txt);
        this.installationLockText = (TextView) findViewById(R.id.installation_lock_txt);
        this.editChannelLockText = (TextView) findViewById(R.id.edit_channel_lock_txt);
        this.settingsLockText = (TextView) findViewById(R.id.settings_lock_txt);
        this.networkLockText = (TextView) findViewById(R.id.network_lock_txt);
        this.ageRatingText = (TextView) findViewById(R.id.age_rating_txt);
        this.passwordLockSwitch = (Switch) findViewById(R.id.password_lock_switch);
        this.serviceLockSwitch = (Switch) findViewById(R.id.service_lock_switch);
        this.installationLockSwitch = (Switch) findViewById(R.id.installation_lock_switch);
        this.editChannelLockSwitch = (Switch) findViewById(R.id.edit_channel_lock_switch);
        this.settingsLockSwitch = (Switch) findViewById(R.id.settings_lock_switch);
        this.networkLockSwitch = (Switch) findViewById(R.id.network_lock_switch);
        this.serviceLockLayout = (RelativeLayout) findViewById(R.id.service_lock_layout);
        this.installationLockLayout = (RelativeLayout) findViewById(R.id.installation_lock_layout);
        this.editChannelLayout = (RelativeLayout) findViewById(R.id.edit_channel_lock_layout);
        this.settingsLockLayout = (RelativeLayout) findViewById(R.id.settings_lock_layout);
        this.networkLockLayout = (RelativeLayout) findViewById(R.id.network_lock_layout);
        this.ageRatingLayout = (RelativeLayout) findViewById(R.id.age_rating_layout);
        GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
        int platformID = login.getPlatform_id();
        switch (platformID) {
            case 30:
                this.passwordLockText = (TextView) findViewById(R.id.password_lock_txt);
                this.passwordLockLayout = (RelativeLayout) findViewById(R.id.password_lock_layout);
                this.editChannelLockText.setText("Channel Manager Lock");
                this.settingsLockText.setText("System Lock");
                this.passwordLockLayout.setVisibility(8);
                this.installationLockLayout.setBackgroundResource(R.drawable.add_time_mid_back_bg);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.installationLockLayout.getLayoutParams();
                lp.topMargin = 0;
                this.installationLockLayout.setLayoutParams(lp);
                this.ageRatingLayout.setVisibility(8);
                break;
            case 32:
            case 40:
            case 41:
            case 42:
            case 44:
                this.serviceLockLayout.setVisibility(8);
                this.networkLockLayout.setVisibility(8);
                this.ageRatingLayout.setVisibility(8);
                this.settingsLockLayout.setBackgroundResource(R.drawable.add_time_down_back_bg);
                break;
            default:
                this.serviceLockLayout.setVisibility(8);
                this.networkLockLayout.setVisibility(8);
                break;
        }
        this.saveButton = (Button) findViewById(R.id.lock_edit_save_btn);
        this.cancelButton = (Button) findViewById(R.id.lock_edit_cancel_btn);
        this.passwordLockSwitch.setChecked(this.controlModels.get(0).GetPswLockSwitch() != 0);
        this.serviceLockSwitch.setChecked(this.controlModels.get(0).GetServiceLockSwitch() != 0);
        this.installationLockSwitch.setChecked(this.controlModels.get(0).GetInstallLockSwitch() != 0);
        this.editChannelLockSwitch.setChecked(this.controlModels.get(0).GetEditChannelLockSwitch() != 0);
        this.settingsLockSwitch.setChecked(this.controlModels.get(0).GetSettingsLockSwitch() != 0);
        this.networkLockSwitch.setChecked(this.controlModels.get(0).GetNetworkLockSwitch() != 0);
        this.ageRating = this.controlModels.get(0).GetAgeRatingSwitch();
        if (login.getPlatform_id() == 30) {
            setSubEntryEnable(true);
        } else if (!this.passwordLockSwitch.isChecked()) {
            setSubEntryEnable(false);
        }
        this.passwordLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: mktvsmart.screen.GsLockEditActivity.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GsLockEditActivity.this.setSubEntryEnable(isChecked);
            }
        });
        this.ageRatingLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLockEditActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(GsLockEditActivity.this);
                LinearLayout ageRatingView = (LinearLayout) inflater.inflate(R.layout.age_rating_layout, (ViewGroup) null);
                final AgeRatingWheel ageRatingWheel = (AgeRatingWheel) ageRatingView.findViewById(R.id.age_rating_whell);
                Button ageRatingSaveButton = (Button) ageRatingView.findViewById(R.id.age_rating_save_btn);
                Button ageRatingCancelButton = (Button) ageRatingView.findViewById(R.id.age_rating_cancel_btn);
                ageRatingWheel.setRating(GsLockEditActivity.this.ageRating);
                ageRatingSaveButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLockEditActivity.2.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        GsLockEditActivity.this.ageRating = ageRatingWheel.getRating();
                        GsLockEditActivity.this.dialog.dismiss();
                    }
                });
                ageRatingCancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLockEditActivity.2.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        GsLockEditActivity.this.dialog.dismiss();
                    }
                });
                GsLockEditActivity.this.dialog = new Dialog(GsLockEditActivity.this, R.style.dialog);
                GsLockEditActivity.this.dialog.setContentView(ageRatingView);
                GsLockEditActivity.this.dialog.setCanceledOnTouchOutside(false);
                GsLockEditActivity.this.dialog.show();
            }
        });
        this.saveButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLockEditActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) throws SocketException, UnsupportedEncodingException {
                GsLockEditActivity.this.setParentalControl();
                GsLockEditActivity.this.onBackPressed();
            }
        });
        this.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLockEditActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsLockEditActivity.this.onBackPressed();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSubEntryEnable(boolean flag) {
        this.serviceLockText.setEnabled(flag);
        this.installationLockText.setEnabled(flag);
        this.editChannelLockText.setEnabled(flag);
        this.settingsLockText.setEnabled(flag);
        this.networkLockText.setEnabled(flag);
        this.ageRatingText.setEnabled(flag);
        this.serviceLockSwitch.setEnabled(flag);
        this.installationLockSwitch.setEnabled(flag);
        this.editChannelLockSwitch.setEnabled(flag);
        this.settingsLockSwitch.setEnabled(flag);
        this.networkLockSwitch.setEnabled(flag);
        this.serviceLockLayout.setEnabled(flag);
        this.installationLockLayout.setEnabled(flag);
        this.editChannelLayout.setEnabled(flag);
        this.settingsLockLayout.setEnabled(flag);
        this.networkLockLayout.setEnabled(flag);
        this.ageRatingLayout.setEnabled(flag);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setParentalControl() throws SocketException, UnsupportedEncodingException {
        try {
            this.controlModels.get(0).SetPswLockSwitch(!this.passwordLockSwitch.isChecked() ? 0 : 1);
            this.controlModels.get(0).SetServiceLockSwitch(!this.serviceLockSwitch.isChecked() ? 0 : 1);
            this.controlModels.get(0).SetInstallLockSwitch(!this.installationLockSwitch.isChecked() ? 0 : 1);
            this.controlModels.get(0).SetEditChannelLockSwitch(!this.editChannelLockSwitch.isChecked() ? 0 : 1);
            this.controlModels.get(0).SetSettingsLockSwitch(!this.settingsLockSwitch.isChecked() ? 0 : 1);
            this.controlModels.get(0).SetNetworkLockSwitch(this.networkLockSwitch.isChecked() ? 1 : 0);
            this.controlModels.get(0).SetAgeRatingSwitch(this.ageRating);
            this.parser = ParserFactory.getParser();
            byte[] dataBuffer = this.parser.serialize(this.controlModels, GlobalConstantValue.GMS_MSG_DO_PASSWORD_SWITCH_SET).getBytes("UTF-8");
            this.tcpSocket.setSoTimeout(3000);
            GsSendSocket.sendSocketToStb(dataBuffer, this.tcpSocket, 0, dataBuffer.length, GlobalConstantValue.GMS_MSG_DO_PASSWORD_SWITCH_SET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
