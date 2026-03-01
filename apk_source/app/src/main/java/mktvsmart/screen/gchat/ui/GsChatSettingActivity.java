package mktvsmart.screen.gchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsSendSocket;
import mktvsmart.screen.R;
import mktvsmart.screen.dataconvert.model.DataConvertUsernameModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.gchat.bean.GsChatSetting;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;
import mktvsmart.screen.view.Switch;

/* loaded from: classes.dex */
public class GsChatSettingActivity extends Activity {
    private static final int GCHAT_SHOW_WINDOW_HIDE = 0;
    private static final int GCHAT_SHOW_WINDOW_SHOW = 1;
    private static final int GCHAT_WINDOW_POSITION_BOTTOM_LEFT = 2;
    private static final int GCHAT_WINDOW_POSITION_BOTTOM_RIGHT = 3;
    private static final int GCHAT_WINDOW_POSITION_TOP_LEFT = 0;
    private static final int GCHAT_WINDOW_POSITION_TOP_RIGHT = 1;
    private static final int GCHAT_WINDOW_SIZE_LARGE = 2;
    private static final int GCHAT_WINDOW_SIZE_MEDIUM = 1;
    private static final int GCHAT_WINDOW_SIZE_SMALL = 0;
    private static final int GCHAT_WINDOW_TRANSPARENCY_MIN = 20;
    private Button mBackBtn;
    private LinearLayout mSettingView;
    private Button mShowPositionBottomLeft;
    private Button mShowPositionBottomRight;
    private LinearLayout mShowPositionLayout;
    private Button mShowPositionTopLeft;
    private Button mShowPositionTopRight;
    private Button mShowSizeLargeBtn;
    private LinearLayout mShowSizeLayout;
    private Button mShowSizeMediumBtn;
    private Button mShowSizeSmallBtn;
    private SeekBar mShowTransparency;
    private LinearLayout mShowTransparencyLayout;
    private Switch mShowWindowSwitch;
    private Socket mTcpSocket;
    private String mUsername;
    private LinearLayout mUsernameLayout;
    private TextView mUsernameView;
    private ADSProgressDialog mWaitDialog;
    private MessageProcessor msgProc;
    private DataParser mParser = ParserFactory.getParser();
    private GsChatSetting mChatSetting = GsChatSetting.getInstance();
    Runnable mRequestDataFailRunnable = new Runnable() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.1
        @Override // java.lang.Runnable
        public void run() {
            Toast.makeText(GsChatSettingActivity.this, GsChatSettingActivity.this.getResources().getString(R.string.str_load_data_fail), 0).show();
            GsChatSettingActivity.this.onBackPressed();
        }
    };
    private View.OnClickListener showSizeBtnOnClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.2
        @Override // android.view.View.OnClickListener
        public void onClick(View v) throws SocketException, UnsupportedEncodingException {
            int showSize = ((Integer) v.getTag()).intValue();
            if (showSize != GsChatSettingActivity.this.mChatSetting.getWindowSize()) {
                GsChatSettingActivity.this.changeShowSize(showSize);
                GsChatSettingActivity.this.mChatSetting.setWindowSize(showSize);
                GsChatSettingActivity.this.sendChatSetting();
            }
        }
    };
    private View.OnClickListener showPositionBtnOnClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.3
        @Override // android.view.View.OnClickListener
        public void onClick(View v) throws SocketException, UnsupportedEncodingException {
            int showPosition = ((Integer) v.getTag()).intValue();
            if (showPosition != GsChatSettingActivity.this.mChatSetting.getWindowPosition()) {
                GsChatSettingActivity.this.changeShowPosition(showPosition);
                GsChatSettingActivity.this.mChatSetting.setWindowPosition(showPosition);
                GsChatSettingActivity.this.sendChatSetting();
            }
        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarOnChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.4
        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) throws SocketException, UnsupportedEncodingException {
            int newTransparency = seekBar.getProgress() * 10;
            if (newTransparency < 20) {
                newTransparency = 20;
                seekBar.setProgress(2);
            }
            if (newTransparency != GsChatSettingActivity.this.mChatSetting.getWindowTransparency()) {
                GsChatSettingActivity.this.mChatSetting.setWindowTransparency(newTransparency);
                GsChatSettingActivity.this.sendChatSetting();
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws UnsupportedEncodingException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gchat_setting_menu);
        initViews();
        setViewListener();
        try {
            CreateSocket cSocket = new CreateSocket(null, 0);
            this.mTcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        GsSendSocket.sendOnlyCommandSocketToStb(this.mTcpSocket, 103);
        setMessageProcess();
        this.mWaitDialog = DialogBuilder.showProgressDialog((Activity) this, R.string.loading_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), this.mRequestDataFailRunnable);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        MessageProcessor.obtain().removeProcessCallback(this);
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != 0 || !isOutOfBounds(this, event)) {
            return super.onTouchEvent(event);
        }
        finish();
        return true;
    }

    private boolean isOutOfBounds(Activity context, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        return x < (-slop) || y < (-slop) || x > this.mSettingView.getWidth() + slop || y > this.mSettingView.getHeight() + slop;
    }

    private void initViews() {
        this.mShowWindowSwitch = (Switch) findViewById(R.id.gchat_show_window_switch);
        this.mBackBtn = (Button) findViewById(R.id.back_btn);
        this.mShowSizeLargeBtn = (Button) findViewById(R.id.gchat_show_size_large);
        this.mShowSizeMediumBtn = (Button) findViewById(R.id.gchat_show_size_medium);
        this.mShowSizeSmallBtn = (Button) findViewById(R.id.gchat_show_size_small);
        this.mShowPositionTopLeft = (Button) findViewById(R.id.gchat_show_position_top_left);
        this.mShowPositionTopRight = (Button) findViewById(R.id.gchat_show_position_top_right);
        this.mShowPositionBottomLeft = (Button) findViewById(R.id.gchat_show_position_bottom_left);
        this.mShowPositionBottomRight = (Button) findViewById(R.id.gchat_show_position_bottom_right);
        this.mShowTransparency = (SeekBar) findViewById(R.id.gchat_show_transparency_seekbar);
        this.mUsernameView = (TextView) findViewById(R.id.my_name);
        this.mSettingView = (LinearLayout) findViewById(R.id.gchat_setting_view);
        this.mShowSizeLayout = (LinearLayout) findViewById(R.id.gchat_show_size);
        this.mShowPositionLayout = (LinearLayout) findViewById(R.id.gchat_show_position);
        this.mShowTransparencyLayout = (LinearLayout) findViewById(R.id.gchat_show_transparency);
        this.mUsernameLayout = (LinearLayout) findViewById(R.id.gchat_username);
        this.mShowSizeLargeBtn.setTag(2);
        this.mShowSizeMediumBtn.setTag(1);
        this.mShowSizeSmallBtn.setTag(0);
        this.mShowPositionTopLeft.setTag(0);
        this.mShowPositionTopRight.setTag(1);
        this.mShowPositionBottomLeft.setTag(2);
        this.mShowPositionBottomRight.setTag(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initDatas() {
        this.mUsernameView.setText(this.mChatSetting.getUsername());
        if (this.mChatSetting.getSHowWindow() == 0) {
            this.mShowWindowSwitch.setChecked(false);
        } else if (this.mChatSetting.getSHowWindow() == 1) {
            this.mShowWindowSwitch.setChecked(true);
        }
        changeShowWindowSwitch(this.mShowWindowSwitch.isChecked());
        changeShowSize(this.mChatSetting.getWindowSize());
        changeShowPosition(this.mChatSetting.getWindowPosition());
        this.mShowTransparency.setProgress(this.mChatSetting.getWindowTransparency() / 10);
    }

    private void setViewListener() {
        this.mShowSizeLargeBtn.setOnClickListener(this.showSizeBtnOnClickListener);
        this.mShowSizeMediumBtn.setOnClickListener(this.showSizeBtnOnClickListener);
        this.mShowSizeSmallBtn.setOnClickListener(this.showSizeBtnOnClickListener);
        this.mShowPositionTopLeft.setOnClickListener(this.showPositionBtnOnClickListener);
        this.mShowPositionTopRight.setOnClickListener(this.showPositionBtnOnClickListener);
        this.mShowPositionBottomLeft.setOnClickListener(this.showPositionBtnOnClickListener);
        this.mShowPositionBottomRight.setOnClickListener(this.showPositionBtnOnClickListener);
        this.mShowTransparency.setOnSeekBarChangeListener(this.seekBarOnChangeListener);
        this.mUsernameLayout.setOnClickListener(getOnRenameListener());
        this.mBackBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsChatSettingActivity.this.onBackPressed();
            }
        });
        this.mShowWindowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.6
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) throws SocketException, UnsupportedEncodingException {
                if (!buttonView.isPressed()) {
                    return;
                }
                GsChatSettingActivity.this.changeShowWindowSwitch(isChecked);
                if (isChecked) {
                    GsChatSettingActivity.this.mChatSetting.setShowWindow(1);
                } else {
                    GsChatSettingActivity.this.mChatSetting.setShowWindow(0);
                }
                GsChatSettingActivity.this.sendChatSetting();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeShowSize(int showSize) {
        Button[] showSizeBtn = {this.mShowSizeSmallBtn, this.mShowSizeMediumBtn, this.mShowSizeLargeBtn};
        int[] showSizeBtnBg = {R.drawable.gchat_setting_show_size_small, R.drawable.gchat_setting_show_size_medium, R.drawable.gchat_setting_show_size_large};
        int[] showSizeBtnSelectedBg = {R.drawable.gchat_setting_show_size_small_selected, R.drawable.gchat_setting_show_size_medium_selected, R.drawable.gchat_setting_show_size_large_selected};
        for (int i = 0; i < showSizeBtn.length; i++) {
            if (i == showSize) {
                showSizeBtn[i].setBackgroundResource(showSizeBtnSelectedBg[i]);
            } else {
                showSizeBtn[i].setBackgroundResource(showSizeBtnBg[i]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeShowWindowSwitch(boolean showWindow) {
        if (showWindow) {
            this.mShowSizeLayout.setVisibility(0);
            this.mShowPositionLayout.setVisibility(0);
            this.mShowTransparencyLayout.setVisibility(0);
        } else {
            this.mShowSizeLayout.setVisibility(8);
            this.mShowPositionLayout.setVisibility(8);
            this.mShowTransparencyLayout.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeShowPosition(int showPosition) {
        Button[] showPositionBtn = {this.mShowPositionTopLeft, this.mShowPositionTopRight, this.mShowPositionBottomLeft, this.mShowPositionBottomRight};
        int[] showPositionBtnBg = {R.drawable.gchat_setting_show_position_top_left, R.drawable.gchat_setting_show_position_top_right, R.drawable.gchat_setting_show_position_bottom_left, R.drawable.gchat_setting_show_position_bottom_right};
        int[] showPositionBtnSelectedBg = {R.drawable.gchat_setting_show_position_top_left_selected, R.drawable.gchat_setting_show_position_top_right_selected, R.drawable.gchat_setting_show_position_bottom_left_selected, R.drawable.gchat_setting_show_position_bottom_right_selected};
        for (int i = 0; i < showPositionBtn.length; i++) {
            if (i == showPosition) {
                showPositionBtn[i].setBackgroundResource(showPositionBtnSelectedBg[i]);
            } else {
                showPositionBtn[i].setBackgroundResource(showPositionBtnBg[i]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendChatSetting() throws SocketException, UnsupportedEncodingException {
        List<GsChatSetting> models = new ArrayList<>();
        models.clear();
        models.add(this.mChatSetting);
        try {
            byte[] data_buff = this.mParser.serialize(models, GlobalConstantValue.GMS_MSG_GCHAT_DO_CHANGE_SETTING).getBytes("UTF-8");
            this.mTcpSocket.setSoTimeout(3000);
            GsSendSocket.sendSocketToStb(data_buff, this.mTcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_GCHAT_DO_CHANGE_SETTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.recycle();
        this.msgProc.setOnMessageProcess(2100, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.7
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsChatSettingActivity.this.finish();
            }
        });
        this.msgProc.setOnMessageProcess(103, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.8
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                byte[] recvData;
                if (msg.arg1 > 0) {
                    try {
                        Bundle data = msg.getData();
                        recvData = data.getByteArray("ReceivedData");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (recvData != null) {
                        InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        GsChatSettingActivity.this.mParser.parse(istream, 25);
                        GsChatSettingActivity.this.initDatas();
                        GsSendSocket.sendOnlyCommandSocketToStb(GsChatSettingActivity.this.mTcpSocket, 105);
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(105, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.9
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    try {
                        Bundle data = msg.getData();
                        byte[] recvData = data.getByteArray("ReceivedData");
                        if (recvData != null) {
                            InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                            GsChatSettingActivity.this.mChatSetting.setUsername(((DataConvertUsernameModel) GsChatSettingActivity.this.mParser.parse(istream, 27).get(0)).getUsername());
                            GsChatSettingActivity.this.mUsernameView.setText(GsChatSettingActivity.this.mChatSetting.getUsername());
                            if (GsChatSettingActivity.this.mWaitDialog != null && GsChatSettingActivity.this.mWaitDialog.isShowing()) {
                                GsChatSettingActivity.this.mWaitDialog.dismiss();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_GCHAT_DO_USER_RENAME, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.10
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg2 == 0) {
                    if (GsChatSettingActivity.this.mWaitDialog != null && GsChatSettingActivity.this.mWaitDialog.isShowing()) {
                        GsChatSettingActivity.this.mWaitDialog.dismiss();
                    }
                    GsChatSettingActivity.this.mUsernameView.setText(GsChatSettingActivity.this.mUsername);
                    GsChatSettingActivity.this.mChatSetting.setUsername(GsChatSettingActivity.this.mUsername);
                    Toast.makeText(GsChatSettingActivity.this, "Rename Success !", 0).show();
                }
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_GCHAT_NOTIFY_UI_SETTING_CHANGED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.11
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GsChatSettingActivity.this.mTcpSocket, 103);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.12
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsChatSettingActivity.this.finish();
            }
        });
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatSettingActivity$13, reason: invalid class name */
    class AnonymousClass13 implements View.OnClickListener {
        InputMethodManager inputManager;
        Dialog renameInputDialog;

        AnonymousClass13() {
            this.renameInputDialog = new Dialog(GsChatSettingActivity.this, R.style.dialog);
            this.inputManager = (InputMethodManager) GsChatSettingActivity.this.getSystemService("input_method");
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            LayoutInflater inflater = LayoutInflater.from(GsChatSettingActivity.this);
            LinearLayout renameLayout = (LinearLayout) inflater.inflate(R.layout.input_rename_dialog, (ViewGroup) null);
            final EditText inputName = (EditText) renameLayout.findViewById(R.id.input_name_edittext);
            Button renameSaveBtn = (Button) renameLayout.findViewById(R.id.input_name_confirm_btn);
            Button renameCancelBtn = (Button) renameLayout.findViewById(R.id.input_name_cancel_btn);
            inputName.setText(GsChatSettingActivity.this.mChatSetting.getUsername());
            Selection.selectAll(inputName.getText());
            renameSaveBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.13.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v2) throws SocketException, UnsupportedEncodingException {
                    GsChatSettingActivity.this.mUsername = inputName.getText().toString();
                    if (GsChatSettingActivity.this.mUsername.length() > 0 && !GsChatSettingActivity.this.mUsername.equals(GsChatSettingActivity.this.mChatSetting.getUsername())) {
                        DataConvertUsernameModel newUsername = new DataConvertUsernameModel();
                        newUsername.setUsername(GsChatSettingActivity.this.mUsername);
                        List<DataConvertUsernameModel> models = new ArrayList<>();
                        models.add(newUsername);
                        try {
                            byte[] data_buff = GsChatSettingActivity.this.mParser.serialize(models, GlobalConstantValue.GMS_MSG_GCHAT_DO_USER_RENAME).getBytes("UTF-8");
                            GsChatSettingActivity.this.mTcpSocket.setSoTimeout(3000);
                            GsSendSocket.sendSocketToStb(data_buff, GsChatSettingActivity.this.mTcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_GCHAT_DO_USER_RENAME);
                            GsChatSettingActivity.this.mWaitDialog = DialogBuilder.showProgressDialog((Activity) GsChatSettingActivity.this, R.string.loading_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_load_data_fail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    AnonymousClass13.this.renameInputDialog.dismiss();
                }
            });
            renameCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.13.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v2) {
                    AnonymousClass13.this.inputManager.hideSoftInputFromWindow(inputName.getWindowToken(), 0);
                    AnonymousClass13.this.renameInputDialog.dismiss();
                }
            });
            this.renameInputDialog.setContentView(renameLayout);
            this.renameInputDialog.setCanceledOnTouchOutside(false);
            this.renameInputDialog.show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() { // from class: mktvsmart.screen.gchat.ui.GsChatSettingActivity.13.3
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    AnonymousClass13.this.inputManager = (InputMethodManager) inputName.getContext().getSystemService("input_method");
                    AnonymousClass13.this.inputManager.showSoftInput(inputName, 0);
                }
            }, 200L);
        }
    }

    private View.OnClickListener getOnRenameListener() {
        return new AnonymousClass13();
    }
}
