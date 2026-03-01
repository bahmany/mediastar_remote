package mktvsmart.screen;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.GamesStatusCodes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.dataconvert.model.DataConvertDebugModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.mail.Mail;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.util.AdsBinnerView;
import mktvsmart.screen.util.GmscreenDataFolderUtil;
import mktvsmart.screen.view.Switch;
import mktvsmart.screen.zip.ZipUnzipFiles;

/* loaded from: classes.dex */
public class GsDebugActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int BMP_HEADER_SIZE = 54;
    private static final int NOTIFY_ID = 0;
    private static final int SCREENSHOT_HEIGHT = 60;
    private CreateSocket cSocket;
    private PendingIntent contentIntent;
    private Switch debugOnOff;
    InputMethodManager inputManager;
    private boolean isDebugEnabled;
    private FrameLayout mAdSpaceFrame;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private MessageProcessor msgProc;
    private RelativeLayout restartStbOption;
    private TextView restartStbText;
    private Button returnButton;
    private RelativeLayout sendChannelDataOption;
    private TextView sendChannelDataText;
    Dialog sendEmailDialog;
    private boolean sendEmailFinished;
    private RelativeLayout sendFlashDataOption;
    private TextView sendFlashDataText;
    private RelativeLayout sendRS232OutputOption;
    private TextView sendRS232OutputText;
    private RelativeLayout sendSTBScreenShotsOption;
    private TextView sendSTBScreenShotsText;
    private RelativeLayout sendUserDataOption;
    private TextView sendUserDataText;
    private SharedPreferences settings;
    private Socket tcpSocket;
    private int currentFlashAddress = 0;
    private AdView mAdView = null;
    private int mScreenshotDataCount = 0;
    private MessageProcessor.PerformOnBackground debugPob = new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsDebugActivity.1
        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) throws IOException {
            if (msg.arg1 > 0) {
                byte[] socketRecvBytes = msg.getData().getByteArray("ReceivedData");
                int dataLength = msg.arg1;
                String uncompressFileName = GsDebugActivity.this.getFileNameByMsgId(msg.what);
                GsDebugActivity.this.writeDataToFile(uncompressFileName, socketRecvBytes, dataLength);
                GsDebugActivity.this.handleDebugDataAndSendEmail(uncompressFileName);
                return;
            }
            GsDebugActivity.this.finishNOtification(GsDebugActivity.this.getString(R.string.send_email_error));
            GsDebugActivity.this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_SEND_EMAIL_FINISH);
            GMScreenGlobalInfo.setmSendEmailFinished(true);
        }
    };
    private MessageProcessor.PerformOnBackground screenshotDataPob = new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsDebugActivity.2
        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) throws IOException {
            if (msg.arg1 > 0) {
                byte[] socketRecvBytes = msg.getData().getByteArray("ReceivedData");
                int dataLength = msg.arg1;
                String uncompressFileName = GsDebugActivity.this.getFileNameByMsgId(msg.what);
                GsDebugActivity.this.writeDataToFile(uncompressFileName, socketRecvBytes, dataLength);
                GsDebugActivity gsDebugActivity = GsDebugActivity.this;
                gsDebugActivity.mScreenshotDataCount--;
                if (GsDebugActivity.this.mScreenshotDataCount == 0) {
                    Intent intent = new Intent();
                    intent.putExtra(GsSTBScreenShotPictureActivity.START_SCREENSHOT_PATH, uncompressFileName);
                    intent.setClass(GsDebugActivity.this, GsSTBScreenShotPictureActivity.class);
                    GsDebugActivity.this.startActivity(intent);
                    GsDebugActivity.this.handleDebugDataAndSendEmail(uncompressFileName);
                    return;
                }
                return;
            }
            GsDebugActivity.this.finishNOtification(GsDebugActivity.this.getString(R.string.send_email_error));
            GsDebugActivity.this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_SEND_EMAIL_FINISH);
            GMScreenGlobalInfo.setmSendEmailFinished(true);
        }
    };
    private MessageProcessor.PerformOnBackground screenshotHeaderPob = new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsDebugActivity.3
        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) throws IOException {
            if (msg.arg1 <= 0) {
                GsDebugActivity.this.finishNOtification(GsDebugActivity.this.getString(R.string.send_email_error));
                GsDebugActivity.this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_SEND_EMAIL_FINISH);
                GMScreenGlobalInfo.setmSendEmailFinished(true);
                return;
            }
            byte[] socketRecvBytes = msg.getData().getByteArray("ReceivedData");
            int dataLength = msg.arg1;
            String uncompressFileName = GsDebugActivity.this.getFileNameByMsgId(msg.what);
            GsDebugActivity.this.writeDataToFile(uncompressFileName, socketRecvBytes, dataLength);
            if (dataLength != 54) {
                GsDebugActivity.this.handleDebugDataAndSendEmail(uncompressFileName);
                return;
            }
            int screenHeight = (((socketRecvBytes[25] & 255) << 24) & ViewCompat.MEASURED_STATE_MASK) | (((socketRecvBytes[24] & 255) << 16) & 16711680) | (((socketRecvBytes[23] & 255) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (socketRecvBytes[22] & 255);
            int index = screenHeight;
            while (index - 60 >= 0) {
                GsDebugActivity.this.requestScreenshotData(index - 60, 60);
                index -= 60;
            }
            if (index - 60 >= 0 || index <= 0) {
                return;
            }
            GsDebugActivity.this.requestScreenshotData(0, index);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public String getFileNameByMsgId(int msgId) {
        switch (msgId) {
            case 7:
                return GlobalConstantValue.SEND_USER_FILENAME;
            case 8:
                return GlobalConstantValue.SEND_CHANNEL_FILENAME;
            case 9:
            case 21:
                return GlobalConstantValue.SEND_FLASH_FILENAME;
            case 10:
            case 29:
                return GlobalConstantValue.SEND_STB_SCREENSHOTS_FILENAME;
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeDataToFile(String fileName, byte[] dataBuffer, int dataLength) throws IOException {
        if (fileName != null && Environment.getExternalStorageState().equals("mounted")) {
            GmscreenDataFolderUtil.checkExsitAndCreatGmscreenDataDir();
            File debugDataFile = new File(GmscreenDataFolderUtil.getGmscreenDataFolderPath(), fileName);
            try {
                if (!debugDataFile.exists()) {
                    debugDataFile.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(debugDataFile, true);
                out.write(dataBuffer, 0, dataLength);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestScreenshotData(int marginTop, int height) throws UnsupportedEncodingException {
        List<Map<String, String>> screenShotValue = new ArrayList<>();
        Map<String, String> screenshotData = new HashMap<>();
        screenshotData.put("margin_top", new StringBuilder(String.valueOf(marginTop)).toString());
        screenshotData.put("height", new StringBuilder(String.valueOf(height)).toString());
        screenShotValue.add(screenshotData);
        DataParser parser = ParserFactory.getParser();
        try {
            byte[] dataBuff = parser.serialize(screenShotValue, 29).getBytes("UTF-8");
            GsSendSocket.sendSocketToStb(dataBuff, this.tcpSocket, 0, dataBuff.length, 29);
            this.mScreenshotDataCount++;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_SEND_EMAIL_FINISH, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsDebugActivity.4
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsDebugActivity.this.setAllEnable(true);
                GsDebugActivity.this.debugOnOff.setEnabled(true);
            }
        });
        this.msgProc.setOnMessageProcess(4097, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsDebugActivity.5
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) {
                GsDebugActivity.this.startNotification();
            }
        });
        this.msgProc.setOnMessageProcess(9, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsDebugActivity.6
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) throws IOException {
                byte[] socketRecvBytes = msg.getData().getByteArray("ReceivedData");
                int dataLength = msg.arg1;
                GsDebugActivity.this.currentFlashAddress += dataLength;
                String flashDataFileName = GsDebugActivity.this.getFileNameByMsgId(msg.what);
                if (dataLength != 0) {
                    GsDebugActivity.this.writeDataToFile(flashDataFileName, socketRecvBytes, dataLength);
                }
                System.out.println("flash data dataLength : " + dataLength);
                if (dataLength == GMScreenGlobalInfo.getMaxDebugDataLenthPerRequest()) {
                    System.out.println("flash data currentFlashAddress : 0x" + Integer.toHexString(GsDebugActivity.this.currentFlashAddress));
                    GsDebugActivity.this.requestLargeDataFromTo(GsDebugActivity.this.currentFlashAddress, GsDebugActivity.this.currentFlashAddress + GMScreenGlobalInfo.getMaxDebugDataLenthPerRequest());
                } else {
                    GsDebugActivity.this.handleDebugDataAndSendEmail(flashDataFileName);
                    GsDebugActivity.this.currentFlashAddress = 0;
                }
            }
        });
        this.msgProc.setOnMessageProcess(8, this.debugPob);
        this.msgProc.setOnMessageProcess(7, this.debugPob);
        this.msgProc.setOnMessageProcess(10, this.screenshotHeaderPob);
        this.msgProc.setOnMessageProcess(29, this.screenshotDataPob);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_COMPRESS_DEBUG_FILE, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsDebugActivity.7
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) {
                GsDebugActivity.this.startNotification();
                GsDebugActivity.this.handleDebugDataAndSendEmail(GlobalConstantValue.SEND_RS232_FILENAME);
            }
        });
        this.msgProc.setOnMessageProcess(21, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsDebugActivity.8
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) throws IOException {
                byte[] socketRecvBytes = msg.getData().getByteArray("ReceivedData");
                int dataLength = msg.arg1;
                String flashDataFileName = GsDebugActivity.this.getFileNameByMsgId(msg.what);
                if (dataLength > 0) {
                    GmscreenDataFolderUtil.deleteFileByName(flashDataFileName);
                    GsDebugActivity.this.writeDataToFile(flashDataFileName, socketRecvBytes, dataLength);
                    GsDebugActivity.this.requestLargeDataFromTo(0, GMScreenGlobalInfo.getMaxDebugDataLenthPerRequest());
                }
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsDebugActivity.9
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsDebugActivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent = new Intent();
                intent.setClass(GsDebugActivity.this, GsLoginListActivity.class);
                GsDebugActivity.this.startActivity(intent);
                GsDebugActivity.this.finish();
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_SEND_EMAIL_EXCEPTION, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsDebugActivity.10
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) {
                GsDebugActivity.this.sendEmailFinished = GMScreenGlobalInfo.ismSendEmailFinished();
                if (GsDebugActivity.this.sendEmailFinished) {
                    return;
                }
                GsDebugActivity.this.finishNOtification(GsDebugActivity.this.getString(R.string.send_email_error));
                GsDebugActivity.this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_SEND_EMAIL_FINISH);
                GMScreenGlobalInfo.setmSendEmailFinished(true);
            }
        });
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) throws SocketException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_layout);
        this.currentFlashAddress = 0;
        setMessageProcess();
        try {
            this.cSocket = new CreateSocket(null, 0);
            this.tcpSocket = this.cSocket.GetSocket();
            this.tcpSocket.setSoTimeout(GamesStatusCodes.STATUS_MILESTONE_CLAIMED_PREVIOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
        this.settings = getSharedPreferences(GlobalConstantValue.PREFS_NAME, 0);
        this.mNotificationManager = (NotificationManager) getSystemService("notification");
        this.sendFlashDataText = (TextView) findViewById(R.id.debug_send_flash_data_txt);
        this.sendUserDataText = (TextView) findViewById(R.id.debug_send_user_data_txt);
        this.sendChannelDataText = (TextView) findViewById(R.id.debug_send_channel_data_txt);
        this.sendRS232OutputText = (TextView) findViewById(R.id.debug_send_rs232_output_txt);
        this.sendSTBScreenShotsText = (TextView) findViewById(R.id.debug_send_stb_screenshots_txt);
        this.restartStbText = (TextView) findViewById(R.id.debug_restart_stb_txt);
        this.sendFlashDataOption = (RelativeLayout) findViewById(R.id.debug_send_flash_data_layout);
        this.sendUserDataOption = (RelativeLayout) findViewById(R.id.debug_send_user_data_layout);
        this.sendChannelDataOption = (RelativeLayout) findViewById(R.id.debug_send_channel_data_layout);
        this.sendRS232OutputOption = (RelativeLayout) findViewById(R.id.debug_send_rs232_output_layout);
        this.sendSTBScreenShotsOption = (RelativeLayout) findViewById(R.id.debug_send_stb_screenshots_layout);
        this.restartStbOption = (RelativeLayout) findViewById(R.id.debug_restart_stb_layout);
        this.mAdSpaceFrame = (FrameLayout) findViewById(R.id.ad_space);
        if (login.getPlatform_id() == 30 || login.getPlatform_id() == 41 || login.getPlatform_id() == 40 || login.getPlatform_id() == 42 || login.getPlatform_id() == 44) {
            this.sendChannelDataOption.setVisibility(8);
            this.sendRS232OutputOption.setVisibility(8);
            this.sendFlashDataOption.setVisibility(8);
        }
        this.debugOnOff = (Switch) findViewById(R.id.debug_onoff);
        this.returnButton = (Button) findViewById(R.id.back_debug);
        this.sendFlashDataOption.setOnClickListener(this);
        this.sendUserDataOption.setOnClickListener(this);
        this.sendChannelDataOption.setOnClickListener(this);
        this.sendRS232OutputOption.setOnClickListener(this);
        this.sendSTBScreenShotsOption.setOnClickListener(this);
        this.restartStbOption.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsDebugActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View v) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GsDebugActivity.this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_STB_RESTART);
            }
        });
        this.debugOnOff.setOnCheckedChangeListener(this);
        this.returnButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsDebugActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsDebugActivity.this.finishActivity(0);
                GsDebugActivity.this.onBackPressed();
            }
        });
        this.mAdView = new AdsBinnerView(this).getAdView();
        this.mAdSpaceFrame.addView(this.mAdView, -2, -2);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.isDebugEnabled = this.settings.getBoolean("IsDebugEnabled", false);
        this.sendEmailFinished = GMScreenGlobalInfo.ismSendEmailFinished();
        this.debugOnOff.setChecked(this.isDebugEnabled);
        if (this.isDebugEnabled && this.sendEmailFinished) {
            setAllEnable(true);
            return;
        }
        setAllEnable(false);
        if (this.sendEmailFinished) {
            this.debugOnOff.setEnabled(true);
        } else {
            this.debugOnOff.setEnabled(false);
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.msgProc.recycle();
        this.mAdSpaceFrame.removeView(this.mAdView);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int viewId = view.getId();
        setAllEnable(false);
        this.debugOnOff.setEnabled(false);
        String sendToAddress = this.settings.getString("SendToAddress", null);
        EditText editText = new EditText(this);
        editText.setInputType(32);
        editText.setText(sendToAddress == null ? "" : sendToAddress);
        if (sendToAddress != null) {
            editText.setSelection(sendToAddress.length());
        }
        showSendEmailDialog(viewId);
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DataParser parser = ParserFactory.getParser();
        List<DataConvertDebugModel> lists = new ArrayList<>();
        DataConvertDebugModel list = new DataConvertDebugModel();
        list.setDebugValue(isChecked ? 1 : 0);
        lists.add(list);
        try {
            String request = parser.serialize(lists, GlobalConstantValue.GMS_MSG_DO_RS232_DEBUG_ENABLE);
            GsSendSocket.sendSocketToStb(request.getBytes(), this.tcpSocket, 0, request.getBytes().length, GlobalConstantValue.GMS_MSG_DO_RS232_DEBUG_ENABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.settings.edit().putBoolean("IsDebugEnabled", isChecked).commit();
        if (!isChecked) {
            String appFolderPath = String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()) + File.separator + getString(R.string.app_name);
            File rs232File = new File(appFolderPath, GlobalConstantValue.SEND_RS232_FILENAME);
            if (rs232File.exists()) {
                rs232File.delete();
            }
            setAllEnable(false);
            return;
        }
        setAllEnable(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNotification() {
        this.mNotification = new Notification(R.drawable.ic_launcher, getString(R.string.start_debug), System.currentTimeMillis());
        this.mNotification.defaults |= 1;
        this.mNotification.defaults |= 2;
        this.mNotification.defaults |= 16;
        this.mNotification.flags &= -3;
        Intent intent = new Intent(getApplicationContext(), (Class<?>) GsDebugActivity.class);
        this.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 134217728);
        this.mNotification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), getString(R.string.debug_processing), this.contentIntent);
        this.mNotification.contentIntent = this.contentIntent;
        this.mNotificationManager.notify(0, this.mNotification);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishNOtification(String resultString) {
        this.mNotification.flags &= -3;
        this.mNotification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), resultString, this.contentIntent);
        this.mNotificationManager.notify(0, this.mNotification);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDebugDataAndSendEmail(String uncompressFileName) {
        String resultString;
        if (uncompressFileName != null && Environment.getExternalStorageState().equals("mounted")) {
            String appFolderPath = GmscreenDataFolderUtil.getGmscreenDataFolderPath();
            try {
                String fullPath = String.valueOf(appFolderPath) + File.separator + uncompressFileName;
                File compressFile = new ZipUnzipFiles(fullPath).zipFiles(true);
                String sendToAddress = this.settings.getString("SendToAddress", null);
                String[] addresses = new String[1];
                if (sendToAddress == null) {
                    sendToAddress = "";
                }
                addresses[0] = sendToAddress;
                if (GlobalConstantValue.LOCAL_EMAIL_ADDRESS != 0 && GlobalConstantValue.LOCAL_EMAIL_PASSWORD != 0) {
                    Mail userEmail = new Mail(GlobalConstantValue.LOCAL_EMAIL_ADDRESS, GlobalConstantValue.LOCAL_EMAIL_PASSWORD);
                    userEmail.setTo(addresses);
                    userEmail.setFrom(GlobalConstantValue.LOCAL_EMAIL_ADDRESS);
                    try {
                        String filename = compressFile.getAbsolutePath().substring(appFolderPath.length() + 1);
                        userEmail.addAttachment(appFolderPath, filename);
                        userEmail.setSubject("Debug");
                        userEmail.setBody("\tDebug send email.\r\n\r\nFrom winkey");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (userEmail.send()) {
                            resultString = getString(R.string.send_email_success);
                        } else {
                            resultString = getString(R.string.send_email_fail);
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        resultString = getString(R.string.send_email_error);
                    }
                    finishNOtification(resultString);
                    this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_SEND_EMAIL_FINISH);
                    GMScreenGlobalInfo.setmSendEmailFinished(true);
                    if (compressFile.exists()) {
                        compressFile.delete();
                    }
                }
            } catch (FileNotFoundException e3) {
                e3.printStackTrace();
                this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_SEND_EMAIL_EXCEPTION);
            } catch (IOException e4) {
                e4.printStackTrace();
                this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_SEND_EMAIL_EXCEPTION);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAllEnable(boolean flag) {
        this.sendFlashDataText.setEnabled(flag);
        this.sendUserDataText.setEnabled(flag);
        this.sendChannelDataText.setEnabled(flag);
        this.sendRS232OutputText.setEnabled(flag);
        this.sendSTBScreenShotsText.setEnabled(flag);
        this.restartStbText.setEnabled(flag);
        this.sendFlashDataOption.setEnabled(flag);
        this.sendUserDataOption.setEnabled(flag);
        this.sendChannelDataOption.setEnabled(flag);
        this.sendRS232OutputOption.setEnabled(flag);
        this.sendSTBScreenShotsOption.setEnabled(flag);
        this.restartStbOption.setEnabled(flag);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestLargeDataFromTo(int fromAdress, int toAdress) throws UnsupportedEncodingException {
        try {
            ArrayList<DataConvertDebugModel> requsetDebugDataList = new ArrayList<>();
            DataConvertDebugModel debugDataRequest = new DataConvertDebugModel();
            debugDataRequest.setRequestDataFrom(fromAdress);
            debugDataRequest.setRequestDataTo(toAdress);
            requsetDebugDataList.add(debugDataRequest);
            DataParser parser = ParserFactory.getParser();
            byte[] dataBuff = parser.serialize(requsetDebugDataList, 9).getBytes("UTF-8");
            GsSendSocket.sendSocketToStb(dataBuff, this.tcpSocket, 0, dataBuff.length, 9);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void continueToDebug(int id) throws UnsupportedEncodingException {
        int responseStyle = 9999;
        switch (id) {
            case R.id.debug_send_user_data_layout /* 2131493148 */:
                responseStyle = 7;
                break;
            case R.id.debug_send_channel_data_layout /* 2131493150 */:
                responseStyle = 8;
                break;
            case R.id.debug_send_rs232_output_layout /* 2131493152 */:
                responseStyle = 6;
                break;
            case R.id.debug_send_flash_data_layout /* 2131493154 */:
                responseStyle = 21;
                break;
            case R.id.debug_send_stb_screenshots_layout /* 2131493156 */:
                responseStyle = 10;
                break;
        }
        GMScreenGlobalInfo.setmSendEmailFinished(false);
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, responseStyle);
        if (id == R.id.debug_send_rs232_output_layout) {
            this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_COMPRESS_DEBUG_FILE);
        } else {
            this.msgProc.postEmptyMessage(4097);
        }
    }

    private void showSendEmailDialog(final int id) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.input_text_dialog, (ViewGroup) null);
        TextView title = (TextView) layout.findViewById(R.id.input_text_title);
        TextView content = (TextView) layout.findViewById(R.id.input_text_dialog_txt);
        final EditText edit = (EditText) layout.findViewById(R.id.input_text_edittext);
        Button confirmButton = (Button) layout.findViewById(R.id.input_text_confirm_btn);
        Button cancelButton = (Button) layout.findViewById(R.id.input_text_cancel_btn);
        title.setText(R.string.send_email_title);
        content.setText(R.string.title_send_email_to);
        String sendToAddress = this.settings.getString("SendToAddress", null);
        edit.setText(sendToAddress == null ? "" : sendToAddress);
        if (sendToAddress != null) {
            edit.setSelection(sendToAddress.length());
        }
        confirmButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsDebugActivity.13
            @Override // android.view.View.OnClickListener
            public void onClick(View v) throws UnsupportedEncodingException {
                GsDebugActivity.this.continueToDebug(id);
                GsDebugActivity.this.settings.edit().putString("SendToAddress", edit.getText().toString()).commit();
                GsDebugActivity.this.sendEmailDialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsDebugActivity.14
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsDebugActivity.this.setAllEnable(true);
                GsDebugActivity.this.debugOnOff.setEnabled(true);
                GsDebugActivity.this.sendEmailDialog.dismiss();
            }
        });
        this.sendEmailDialog = new Dialog(this, R.style.dialog);
        this.sendEmailDialog.setContentView(layout);
        this.sendEmailDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: mktvsmart.screen.GsDebugActivity.15
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                GsDebugActivity.this.setAllEnable(true);
                GsDebugActivity.this.debugOnOff.setEnabled(true);
                GsDebugActivity.this.sendEmailDialog.dismiss();
            }
        });
        this.sendEmailDialog.setCanceledOnTouchOutside(false);
        this.sendEmailDialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsDebugActivity.16
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                GsDebugActivity.this.inputManager = (InputMethodManager) edit.getContext().getSystemService("input_method");
                GsDebugActivity.this.inputManager.showSoftInput(edit, 0);
            }
        }, 200L);
    }
}
