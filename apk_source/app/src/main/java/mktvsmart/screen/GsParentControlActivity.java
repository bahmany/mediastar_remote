package mktvsmart.screen;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdView;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.dataconvert.model.DataConvertControlModel;
import mktvsmart.screen.dataconvert.model.DataConvertOneDataModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.AdsBinnerView;
import mktvsmart.screen.util.DialogBuilder;
import mktvsmart.screen.view.ListviewAdapter;
import mktvsmart.screen.view.SleepTimerWheel;
import mktvsmart.screen.view.Switch;

/* loaded from: classes.dex */
public class GsParentControlActivity extends Activity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final int DEFAULT_SLEEP_TIME = 60;
    private static final int FACTORY_DEFAULT = 6;
    private static final int FACTORY_DEFAULT_ALL = 0;
    private static final int FACTORY_DEFAULT_CHANNELS_ONLY = 1;
    private static final int FACTORY_DEFAULT_RADIO_CHANNEL_ONLY = 2;
    private static final int FACTORY_DEFAULT_SCRAMBLE_CHANNEL_ONLY = 3;
    private static final int PARENTAL_CONTROL = 4;
    private static final int SCREEN_LOCK = 5;
    private Button backButton;
    private RelativeLayout changePswOption;
    List<DataConvertControlModel> controlModels;
    private Dialog dialog;
    private RelativeLayout facoryDefaultOption;
    private InputMethodManager inputManager;
    private FrameLayout mAdSpaceFrame;
    private MessageProcessor msgProc;
    private RelativeLayout parentalCtrlOption;
    private DataParser parser;
    private Switch powerSwitch;
    Dialog pswInputDialog;
    private Switch screenLockSwitch;
    private int selectTime;
    private SharedPreferences settings;
    private RelativeLayout sleepTimerOption;
    private Switch sleepTimerSwitch;
    private Socket tcpSocket;
    private ADSProgressDialog waitDialog;
    private Dialog warningDialog;
    private int flag = -1;
    private boolean autoObtainSetting = false;
    private boolean mIsScreenLockChangedByStb = false;
    private boolean isSwitchOn = false;
    private AdView mAdView = null;

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(13, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.1
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                InputStream istream = new ByteArrayInputStream(recvData, 0, msg.arg1);
                try {
                    GsParentControlActivity.this.controlModels = GsParentControlActivity.this.parser.parse(istream, 11);
                    GsParentControlActivity.this.autoObtainSetting = true;
                    if (GsParentControlActivity.this.controlModels.get(0).getSleepSwitch() == 1) {
                        GsParentControlActivity.this.sleepTimerSwitch.setChecked(true);
                        GsParentControlActivity.this.settings.edit().putInt("SleepTimerMinute", GsParentControlActivity.this.controlModels.get(0).getSleepTime()).commit();
                    } else {
                        GsParentControlActivity.this.sleepTimerSwitch.setChecked(false);
                    }
                    GsParentControlActivity.this.screenLockSwitch.setChecked(GsParentControlActivity.this.controlModels.get(0).GetIsLockScreen() != 0);
                    GsParentControlActivity.this.powerSwitch.setChecked(GsParentControlActivity.this.controlModels.get(0).GetPowerOff() != 0);
                    GsParentControlActivity.this.autoObtainSetting = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.msgProc.setOnMessageProcess(2, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.2
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                InputStream istream = new ByteArrayInputStream(recvData, 0, msg.arg1);
                try {
                    GsParentControlActivity.this.controlModels = GsParentControlActivity.this.parser.parse(istream, 1);
                    LayoutInflater inflater = LayoutInflater.from(GsParentControlActivity.this);
                    View layout = inflater.inflate(R.layout.set_sleep, (ViewGroup) null);
                    Button sleepTimerSaveBtn = (Button) layout.findViewById(R.id.sleep_timer_save_btn);
                    Button sleepTimerCancelBtn = (Button) layout.findViewById(R.id.sleep_timer_cancel_btn);
                    SleepTimerWheel sleepTimerWheel = (SleepTimerWheel) layout.findViewById(R.id.sleep_wheel);
                    GsParentControlActivity.this.selectTime = GsParentControlActivity.this.settings.getInt("SleepTimerMinute", 60);
                    sleepTimerWheel.setOnChangeListener(new SleepTimerWheel.OnChangeListener() { // from class: mktvsmart.screen.GsParentControlActivity.2.1
                        @Override // mktvsmart.screen.view.SleepTimerWheel.OnChangeListener
                        public void onChange(int minute) {
                            if (GsParentControlActivity.this.selectTime >= 60) {
                                if ((GsParentControlActivity.this.selectTime != 120 || minute != 0) && ((GsParentControlActivity.this.selectTime != 60 && GsParentControlActivity.this.selectTime != 61) || (minute != 59 && minute != 58))) {
                                    GsParentControlActivity.this.selectTime = minute + 60 + 1;
                                } else {
                                    GsParentControlActivity.this.selectTime = minute + 1;
                                }
                            } else if (GsParentControlActivity.this.selectTime != 1 || minute != 59) {
                                GsParentControlActivity.this.selectTime = minute + 1;
                            } else {
                                GsParentControlActivity.this.selectTime = minute + 60 + 1;
                            }
                            System.out.println("selectTime   " + GsParentControlActivity.this.selectTime);
                        }
                    });
                    sleepTimerWheel.setMinute(GsParentControlActivity.this.settings.getInt("SleepTimerMinute", 60) - 1);
                    sleepTimerSaveBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsParentControlActivity.2.2
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) throws SocketException, UnsupportedEncodingException {
                            GsParentControlActivity.this.isSwitchOn = false;
                            GsParentControlActivity.this.settings.edit().putInt("SleepTimerMinute", GsParentControlActivity.this.selectTime).commit();
                            try {
                                List<DataConvertControlModel> controlList = new ArrayList<>();
                                DataConvertControlModel model = new DataConvertControlModel();
                                model.setSleepSwitch(1);
                                model.setSleepTime(GsParentControlActivity.this.settings.getInt("SleepTimerMinute", 60));
                                controlList.add(model);
                                byte[] data_buff = GsParentControlActivity.this.parser.serialize(controlList, GlobalConstantValue.GMS_MSG_DO_SLEEP_TIMER_SET).getBytes("UTF-8");
                                GsParentControlActivity.this.tcpSocket.setSoTimeout(3000);
                                GsSendSocket.sendSocketToStb(data_buff, GsParentControlActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_SLEEP_TIMER_SET);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            GsParentControlActivity.this.dialog.dismiss();
                        }
                    });
                    sleepTimerCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsParentControlActivity.2.3
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            if (GsParentControlActivity.this.isSwitchOn) {
                                GsParentControlActivity.this.isSwitchOn = false;
                                GsParentControlActivity.this.sleepTimerSwitch.setChecked(false);
                            }
                            GsParentControlActivity.this.dialog.dismiss();
                        }
                    });
                    GsParentControlActivity.this.dialog = new Dialog(GsParentControlActivity.this, R.style.dialog);
                    GsParentControlActivity.this.dialog.setContentView(layout);
                    GsParentControlActivity.this.dialog.setCanceledOnTouchOutside(false);
                    GsParentControlActivity.this.dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.msgProc.setOnMessageProcess(2027, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.3
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    if (GsParentControlActivity.this.pswInputDialog != null && GsParentControlActivity.this.pswInputDialog.isShowing()) {
                        GsParentControlActivity.this.pswInputDialog.dismiss();
                    }
                    if (GsParentControlActivity.this.screenLockSwitch.isChecked()) {
                        GsParentControlActivity.this.mIsScreenLockChangedByStb = true;
                        GsParentControlActivity.this.screenLockSwitch.setChecked(false);
                    } else {
                        GsParentControlActivity.this.mIsScreenLockChangedByStb = false;
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(20, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.4
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                Intent parentalCtrlIntent = new Intent(GsParentControlActivity.this, (Class<?>) GsLockEditActivity.class);
                parentalCtrlIntent.putExtra("ParentalControlData", recvData);
                parentalCtrlIntent.putExtra("DataLength", msg.arg1);
                GsParentControlActivity.this.startActivity(parentalCtrlIntent);
            }
        });
        this.msgProc.setOnMessageProcess(2006, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.5
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (GsParentControlActivity.this.pswInputDialog != null && GsParentControlActivity.this.pswInputDialog.isShowing()) {
                    GsParentControlActivity.this.pswInputDialog.dismiss();
                }
                if (GsParentControlActivity.this.waitDialog != null && GsParentControlActivity.this.waitDialog.isShowing()) {
                    GsParentControlActivity.this.waitDialog.dismiss();
                }
                GsParentControlActivity.this.inputPermissionPassword();
            }
        });
        this.msgProc.setOnMessageProcess(2007, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.6
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (GsParentControlActivity.this.waitDialog != null && GsParentControlActivity.this.waitDialog.isShowing()) {
                    GsParentControlActivity.this.waitDialog.dismiss();
                }
                if (GsParentControlActivity.this.pswInputDialog != null && GsParentControlActivity.this.pswInputDialog.isShowing()) {
                    GsParentControlActivity.this.pswInputDialog.dismiss();
                }
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.7
            List<String> verifyResult = null;

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                try {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
                    this.verifyResult = GsParentControlActivity.this.parser.parse(instream, 15);
                    if (GsParentControlActivity.this.waitDialog.isShowing()) {
                        GsParentControlActivity.this.waitDialog.dismiss();
                    }
                    if (Integer.parseInt(this.verifyResult.get(0)) != 0) {
                        switch (GsParentControlActivity.this.flag) {
                            case 4:
                                GsParentControlActivity.this.askParentalControl();
                                break;
                            case 6:
                                GsParentControlActivity.this.setFactoryDefault();
                                break;
                        }
                        GsParentControlActivity.this.flag = -1;
                        return;
                    }
                    GsParentControlActivity.this.inputPermissionPassword();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.msgProc.setOnMessageProcess(2008, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.8
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                GsParentControlActivity.this.askControlSetting();
            }
        });
        this.msgProc.setOnMessageProcess(2014, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.9
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GsParentControlActivity.this.tcpSocket, 13);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsParentControlActivity.10
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsParentControlActivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent = new Intent();
                intent.setClass(GsParentControlActivity.this, GsLoginListActivity.class);
                GsParentControlActivity.this.startActivity(intent);
                GsParentControlActivity.this.finish();
            }
        });
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 32:
            case 71:
            case 72:
            case 74:
                setContentView(R.layout.control_layout_tsc188);
                break;
            default:
                setContentView(R.layout.control_layout);
                break;
        }
        CreateSocket cSocket = new CreateSocket("", 0);
        try {
            this.tcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.parser = ParserFactory.getParser();
        setMessageProcess();
        findViews();
        this.settings = getSharedPreferences(GlobalConstantValue.PREFS_NAME, 0);
    }

    private void findViews() {
        this.backButton = (Button) findViewById(R.id.back_control);
        this.sleepTimerSwitch = (Switch) findViewById(R.id.sleep_timer_switch);
        this.screenLockSwitch = (Switch) findViewById(R.id.screen_lock_switch);
        this.powerSwitch = (Switch) findViewById(R.id.power_switch);
        this.sleepTimerOption = (RelativeLayout) findViewById(R.id.sleep_timer_option);
        this.parentalCtrlOption = (RelativeLayout) findViewById(R.id.parental_control_option);
        this.changePswOption = (RelativeLayout) findViewById(R.id.change_password_option);
        this.facoryDefaultOption = (RelativeLayout) findViewById(R.id.set_factory_default_option);
        AdView adView = new AdsBinnerView(this).getAdView();
        this.mAdView = adView;
        this.mAdView = adView;
        this.mAdSpaceFrame = (FrameLayout) findViewById(R.id.ad_space);
        this.mAdSpaceFrame.addView(this.mAdView, -2, -2);
        this.sleepTimerSwitch.setOnCheckedChangeListener(this);
        this.screenLockSwitch.setOnCheckedChangeListener(this);
        this.powerSwitch.setOnCheckedChangeListener(this);
        this.backButton.setOnClickListener(this);
        this.sleepTimerOption.setOnClickListener(this);
        this.parentalCtrlOption.setOnClickListener(this);
        this.changePswOption.setOnClickListener(this);
        this.facoryDefaultOption.setOnClickListener(this);
    }

    @Override // android.app.Activity
    protected void onResume() throws UnsupportedEncodingException {
        super.onResume();
        askControlSetting();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.msgProc.recycle();
        this.mAdSpaceFrame.removeView(this.mAdView);
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) throws SocketException, UnsupportedEncodingException {
        if (!this.autoObtainSetting && !this.mIsScreenLockChangedByStb) {
            switch (buttonView.getId()) {
                case R.id.sleep_timer_switch /* 2131493137 */:
                    setSleepTimer(isChecked ? 1 : 0);
                    break;
                case R.id.screen_lock_switch /* 2131493141 */:
                    if (isChecked) {
                        lockSTBScreen();
                        break;
                    } else {
                        this.flag = 5;
                        inputPermissionPassword();
                        break;
                    }
                case R.id.power_switch /* 2131493143 */:
                    sendPowerCommand();
                    break;
            }
        }
        this.mIsScreenLockChangedByStb = false;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) throws UnsupportedEncodingException {
        switch (v.getId()) {
            case R.id.back_control /* 2131493134 */:
                onBackPressed();
                break;
            case R.id.sleep_timer_option /* 2131493135 */:
                askSleepTimer();
                break;
            case R.id.parental_control_option /* 2131493138 */:
                this.flag = 4;
                inputPermissionPassword();
                break;
            case R.id.change_password_option /* 2131493139 */:
                askChangePassword();
                break;
            case R.id.set_factory_default_option /* 2131493144 */:
                this.flag = 6;
                inputPermissionPassword();
                break;
        }
    }

    private void setSleepTimer(int onOff) throws SocketException, UnsupportedEncodingException {
        try {
            List<DataConvertControlModel> controlList = new ArrayList<>();
            DataConvertControlModel model = new DataConvertControlModel();
            if (onOff == 1) {
                this.isSwitchOn = true;
                model.setSleepSwitch(1);
                askSleepTimer();
            } else {
                model.setSleepSwitch(0);
                controlList.add(model);
                byte[] data_buff = this.parser.serialize(controlList, GlobalConstantValue.GMS_MSG_DO_SLEEP_TIMER_SET).getBytes("UTF-8");
                this.tcpSocket.setSoTimeout(3000);
                GsSendSocket.sendSocketToStb(data_buff, this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_SLEEP_TIMER_SET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void askSleepTimer() throws UnsupportedEncodingException {
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void askParentalControl() throws UnsupportedEncodingException {
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 20);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void askControlSetting() throws UnsupportedEncodingException {
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 13);
    }

    private void askChangePassword() {
        Intent changePswIntent = new Intent(this, (Class<?>) GsChangePassword.class);
        startActivity(changePswIntent);
    }

    private void sendPowerCommand() throws UnsupportedEncodingException {
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_POWER_SWITCH);
    }

    private void lockSTBScreen() throws UnsupportedEncodingException {
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_SCREEN_LOCK);
    }

    private ArrayList<String> getFactoryData() throws Resources.NotFoundException {
        ArrayList<String> data = new ArrayList<>();
        int platformId = GMScreenGlobalInfo.getCurStbPlatform();
        if (platformId == 30 || platformId == 32 || platformId == 71 || platformId == 72 || platformId == 74) {
            for (String str : getResources().getStringArray(R.array.factory_option_trident8471)) {
                data.add(str);
            }
        } else {
            String[] factoryMenu = getResources().getStringArray(R.array.factory_option);
            for (String str2 : factoryMenu) {
                data.add(str2);
            }
        }
        return data;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFactoryDefault() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.factory_default_layout, (ViewGroup) null);
        ListView list = (ListView) layout.findViewById(R.id.factory_default_list);
        Button cancelButton = (Button) layout.findViewById(R.id.factory_default_cancel_btn);
        ListviewAdapter adapter = new ListviewAdapter(this, getFactoryData());
        list.setAdapter((ListAdapter) adapter);
        list.setOnItemClickListener(new AnonymousClass11());
        cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsParentControlActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsParentControlActivity.this.dialog.dismiss();
            }
        });
        this.dialog = new Dialog(this, R.style.dialog);
        this.dialog.setContentView(layout);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.show();
    }

    /* renamed from: mktvsmart.screen.GsParentControlActivity$11, reason: invalid class name */
    class AnonymousClass11 implements AdapterView.OnItemClickListener {
        int responseStyle = 9999;

        AnonymousClass11() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LayoutInflater inflater = LayoutInflater.from(GsParentControlActivity.this);
            View layout = inflater.inflate(R.layout.confirm_dialog, (ViewGroup) null);
            TextView title = (TextView) layout.findViewById(R.id.confirm_dialog_title);
            TextView content = (TextView) layout.findViewById(R.id.confirm_dialog_txt);
            Button yesBtn = (Button) layout.findViewById(R.id.confirm_dialog_yes_btn);
            Button noBtn = (Button) layout.findViewById(R.id.confirm_dialog_no_btn);
            title.setText(R.string.warning_dialog);
            switch (position) {
                case 0:
                    content.setText(R.string.factory_all_message);
                    this.responseStyle = GlobalConstantValue.GMS_MSG_DO_FACTORY_DEFAULT_ALL;
                    break;
                case 1:
                    content.setText(R.string.factory_channels_only_message);
                    this.responseStyle = GlobalConstantValue.GMS_MSG_DO_FACTORY_DEFAULT_CHANNEL;
                    break;
                case 2:
                    content.setText(R.string.factory_radio_channels_only_message);
                    this.responseStyle = GlobalConstantValue.GMS_MSG_DO_FACTORY_DEFAULT_RADIO;
                    break;
                case 3:
                    content.setText(R.string.factory_scramble_channels_only_message);
                    this.responseStyle = GlobalConstantValue.GMS_MSG_DO_FACTORY_DEFAULT_SCRAMBLE;
                    break;
            }
            yesBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsParentControlActivity.11.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) throws UnsupportedEncodingException {
                    GsSendSocket.sendOnlyCommandSocketToStb(GsParentControlActivity.this.tcpSocket, AnonymousClass11.this.responseStyle);
                    GsParentControlActivity.this.warningDialog.dismiss();
                }
            });
            noBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsParentControlActivity.11.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    GsParentControlActivity.this.warningDialog.dismiss();
                }
            });
            GsParentControlActivity.this.warningDialog = new Dialog(GsParentControlActivity.this, R.style.dialog);
            GsParentControlActivity.this.warningDialog.setContentView(layout);
            GsParentControlActivity.this.warningDialog.setCanceledOnTouchOutside(false);
            GsParentControlActivity.this.warningDialog.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void inputPermissionPassword() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout inputPswLayout = (LinearLayout) inflater.inflate(R.layout.input_passowrd_dialog, (ViewGroup) null);
        TextView name = (TextView) inputPswLayout.findViewById(R.id.input_password_title);
        final EditText edit = (EditText) inputPswLayout.findViewById(R.id.input_password_edittext);
        Button inputPswCancelBtn = (Button) inputPswLayout.findViewById(R.id.input_psw_cancel_btn);
        switch (this.flag) {
            case 4:
                name.setText(R.string.parental_control_input_password_text);
                break;
            case 5:
                name.setText(R.string.screen_lock_input_password_text);
                break;
            case 6:
                name.setText(R.string.factory_default_input_password_text);
                break;
            default:
                return;
        }
        edit.addTextChangedListener(new TextWatcher() { // from class: mktvsmart.screen.GsParentControlActivity.13
            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) throws SocketException, UnsupportedEncodingException {
                int responseStyle;
                int inputPswNum = edit.getText().toString().length();
                if (inputPswNum == GMScreenGlobalInfo.getmMaxPasswordNum()) {
                    List<DataConvertOneDataModel> lockModels = new ArrayList<>();
                    DataConvertOneDataModel model = new DataConvertOneDataModel();
                    try {
                        switch (GsParentControlActivity.this.flag) {
                            case 4:
                            case 6:
                                responseStyle = GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK;
                                break;
                            case 5:
                                responseStyle = GlobalConstantValue.GMS_MSG_DO_PLAYING_CHANNEL_PASSWORD_CHECK;
                                break;
                            default:
                                return;
                        }
                        GsParentControlActivity.this.parser = ParserFactory.getParser();
                        model.setData(edit.getText().toString());
                        lockModels.add(model);
                        byte[] data_buff = GsParentControlActivity.this.parser.serialize(lockModels, responseStyle).getBytes("UTF-8");
                        GsParentControlActivity.this.tcpSocket.setSoTimeout(3000);
                        GsSendSocket.sendSocketToStb(data_buff, GsParentControlActivity.this.tcpSocket, 0, data_buff.length, responseStyle);
                        GsParentControlActivity.this.inputManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GsParentControlActivity.this.pswInputDialog.dismiss();
                    GsParentControlActivity.this.waitDialog = DialogBuilder.showProgressDialog((Activity) GsParentControlActivity.this, R.string.verify_password, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut());
                }
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        });
        inputPswCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsParentControlActivity.14
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                switch (GsParentControlActivity.this.flag) {
                    case 4:
                    case 6:
                        GsParentControlActivity.this.pswInputDialog.dismiss();
                        break;
                    case 5:
                        GsParentControlActivity.this.pswInputDialog.dismiss();
                        GsParentControlActivity.this.autoObtainSetting = true;
                        GsParentControlActivity.this.screenLockSwitch.setChecked(true);
                        GsParentControlActivity.this.autoObtainSetting = false;
                        break;
                }
            }
        });
        this.pswInputDialog = new Dialog(this, R.style.dialog);
        this.pswInputDialog.setContentView(inputPswLayout);
        this.pswInputDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: mktvsmart.screen.GsParentControlActivity.15
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                switch (GsParentControlActivity.this.flag) {
                    case 4:
                    case 6:
                        GsParentControlActivity.this.pswInputDialog.dismiss();
                        break;
                    case 5:
                        GsParentControlActivity.this.pswInputDialog.dismiss();
                        GsParentControlActivity.this.autoObtainSetting = true;
                        GsParentControlActivity.this.screenLockSwitch.setChecked(true);
                        GsParentControlActivity.this.autoObtainSetting = false;
                        break;
                }
            }
        });
        this.pswInputDialog.setCanceledOnTouchOutside(false);
        this.pswInputDialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsParentControlActivity.16
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                GsParentControlActivity.this.inputManager = (InputMethodManager) edit.getContext().getSystemService("input_method");
                GsParentControlActivity.this.inputManager.showSoftInput(edit, 0);
            }
        }, 200L);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("GsParentControlActivity", "onConfigurationChanged");
    }
}
