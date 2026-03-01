package mktvsmart.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.hisientry.HiMscreenConnectService;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.socketthread.UdpSocketReceiveBroadcastThread;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;
import mktvsmart.screen.util.NetWorkUtils;
import org.json.JSONException;

/* loaded from: classes.dex */
public class GsLoginActivity extends Activity {
    private UdpSocketReceiveBroadcastThread mBroadcastRecvThread;
    private Button mConnect;
    private PopupWindow mIpHistoryListPopupWindow;
    private ListView mLoginHistoryListView;
    private MessageProcessor mMsgProc;
    private EditText mPort;
    private View mPreviousView;
    private Button mPullDownButton;
    private RelativeLayout mSwitchLogin;
    private EditText mTextIPOrSn;
    private ADSProgressDialog mWaitDialog;
    private ArrayList<GsMobileLoginInfo> mHistoryStbInfoList = new ArrayList<>();
    private ArrayList<GsMobileLoginInfo> mIpLoginInfoList = new ArrayList<>();
    private IpHistoryListViewAdapter mIpHistoryListViewAdapter = null;
    private EditLoginHistoryFile mEditLoginHistoryFile = new EditLoginHistoryFile(this);
    private ArrayList<GsMobileLoginInfo> mPnpDeveiceList = new ArrayList<>();
    private ArrayList<GsMobileLoginInfo> mStbInfoList = new ArrayList<>();

    private void setMessageProcess() {
        this.mMsgProc = MessageProcessor.obtain();
        this.mMsgProc.recycle();
        this.mMsgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_BROADCAST_LOGIN_INFO_UPDATED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsLoginActivity.1
            AnonymousClass1() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                try {
                    GsLoginActivity.this.mStbInfoList = (ArrayList) msg.obj;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.mMsgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_GET_IP_BY_SN, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsLoginActivity.2
            AnonymousClass2() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                String ipAddress = (String) msg.obj;
                if (ipAddress != null) {
                    GsLoginActivity.this.connectStbByIp(ipAddress);
                } else {
                    GsLoginActivity.this.dismissWaitDialog();
                    GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, -9);
                }
            }
        });
    }

    /* renamed from: mktvsmart.screen.GsLoginActivity$1 */
    class AnonymousClass1 implements MessageProcessor.PerformOnForeground {
        AnonymousClass1() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            try {
                GsLoginActivity.this.mStbInfoList = (ArrayList) msg.obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsLoginActivity$2 */
    class AnonymousClass2 implements MessageProcessor.PerformOnForeground {
        AnonymousClass2() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            String ipAddress = (String) msg.obj;
            if (ipAddress != null) {
                GsLoginActivity.this.connectStbByIp(ipAddress);
            } else {
                GsLoginActivity.this.dismissWaitDialog();
                GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, -9);
            }
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        setMessageProcess();
        this.mEditLoginHistoryFile.getListFromFile(this.mHistoryStbInfoList);
        this.mEditLoginHistoryFile.getIpLoginHistoryList(this.mIpLoginInfoList);
        findviews();
        setonTouch();
        Intent intent = getIntent();
        String ipAdress = intent.getStringExtra("ipAdress");
        if (ipAdress != null) {
            this.mTextIPOrSn.setText(ipAdress);
            this.mWaitDialog = DialogBuilder.showProgressDialog((Context) this, getString(R.string.Logining), getString(R.string.please_wait), false);
            connectStbByIp(ipAdress);
        }
        this.mSwitchLogin.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLoginActivity.3
            AnonymousClass3() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(GsLoginActivity.this, GsLoginListActivity.class);
                GsLoginActivity.this.startActivity(intent2);
                GsLoginActivity.this.finish();
            }
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsLoginActivity.4
            AnonymousClass4() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) GsLoginActivity.this.mTextIPOrSn.getContext().getSystemService("input_method");
                inputManager.showSoftInput(GsLoginActivity.this.mTextIPOrSn, 0);
            }
        }, 200L);
        this.mPullDownButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLoginActivity.5
            AnonymousClass5() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsLoginActivity.this.mPreviousView = v;
                GsLoginActivity.this.mPullDownButton.setBackgroundResource(R.drawable.ip_history_up_arrow);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService("input_method");
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                LayoutInflater inflater = (LayoutInflater) GsLoginActivity.this.getSystemService("layout_inflater");
                View loginWindow = inflater.inflate(R.layout.ip_login_history_layout, (ViewGroup) null);
                GsLoginActivity.this.mLoginHistoryListView = (ListView) loginWindow.findViewById(R.id.ip_login_history_list);
                GsLoginActivity.this.mIpHistoryListViewAdapter = GsLoginActivity.this.new IpHistoryListViewAdapter(loginWindow.getContext(), GsLoginActivity.this.mIpLoginInfoList);
                GsLoginActivity.this.mLoginHistoryListView.setAdapter((ListAdapter) GsLoginActivity.this.mIpHistoryListViewAdapter);
                GsLoginActivity.this.mLoginHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.GsLoginActivity.5.1
                    AnonymousClass1() {
                    }

                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GsLoginActivity.this.mTextIPOrSn.setText(((GsMobileLoginInfo) GsLoginActivity.this.mIpLoginInfoList.get(position)).getStb_ip_address_disp());
                        GsLoginActivity.this.mTextIPOrSn.setSelection(((GsMobileLoginInfo) GsLoginActivity.this.mIpLoginInfoList.get(position)).getStb_ip_address_disp().length());
                        GsLoginActivity.this.mIpHistoryListPopupWindow.dismiss();
                    }
                });
                GsLoginActivity.this.mIpHistoryListPopupWindow = new PopupWindow(loginWindow, GsLoginActivity.this.mTextIPOrSn.getWidth(), -2, true);
                GsLoginActivity.this.mIpHistoryListPopupWindow.setOutsideTouchable(true);
                GsLoginActivity.this.mIpHistoryListPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                GsLoginActivity.this.mIpHistoryListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: mktvsmart.screen.GsLoginActivity.5.2
                    AnonymousClass2() {
                    }

                    @Override // android.widget.PopupWindow.OnDismissListener
                    public void onDismiss() {
                        GsLoginActivity.this.mPullDownButton.setBackgroundResource(R.drawable.ip_history_down_arrow);
                        InputMethodManager imm2 = (InputMethodManager) GsLoginActivity.this.mPreviousView.getContext().getSystemService("input_method");
                        imm2.toggleSoftInput(0, 2);
                    }
                });
                if (!GsLoginActivity.this.mIpHistoryListPopupWindow.isShowing()) {
                    GsLoginActivity.this.mIpHistoryListPopupWindow.showAsDropDown(GsLoginActivity.this.mTextIPOrSn, 0, 0);
                }
            }

            /* renamed from: mktvsmart.screen.GsLoginActivity$5$1 */
            class AnonymousClass1 implements AdapterView.OnItemClickListener {
                AnonymousClass1() {
                }

                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GsLoginActivity.this.mTextIPOrSn.setText(((GsMobileLoginInfo) GsLoginActivity.this.mIpLoginInfoList.get(position)).getStb_ip_address_disp());
                    GsLoginActivity.this.mTextIPOrSn.setSelection(((GsMobileLoginInfo) GsLoginActivity.this.mIpLoginInfoList.get(position)).getStb_ip_address_disp().length());
                    GsLoginActivity.this.mIpHistoryListPopupWindow.dismiss();
                }
            }

            /* renamed from: mktvsmart.screen.GsLoginActivity$5$2 */
            class AnonymousClass2 implements PopupWindow.OnDismissListener {
                AnonymousClass2() {
                }

                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    GsLoginActivity.this.mPullDownButton.setBackgroundResource(R.drawable.ip_history_down_arrow);
                    InputMethodManager imm2 = (InputMethodManager) GsLoginActivity.this.mPreviousView.getContext().getSystemService("input_method");
                    imm2.toggleSoftInput(0, 2);
                }
            }
        });
    }

    /* renamed from: mktvsmart.screen.GsLoginActivity$3 */
    class AnonymousClass3 implements View.OnClickListener {
        AnonymousClass3() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            Intent intent2 = new Intent();
            intent2.setClass(GsLoginActivity.this, GsLoginListActivity.class);
            GsLoginActivity.this.startActivity(intent2);
            GsLoginActivity.this.finish();
        }
    }

    /* renamed from: mktvsmart.screen.GsLoginActivity$4 */
    class AnonymousClass4 extends TimerTask {
        AnonymousClass4() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            InputMethodManager inputManager = (InputMethodManager) GsLoginActivity.this.mTextIPOrSn.getContext().getSystemService("input_method");
            inputManager.showSoftInput(GsLoginActivity.this.mTextIPOrSn, 0);
        }
    }

    /* renamed from: mktvsmart.screen.GsLoginActivity$5 */
    class AnonymousClass5 implements View.OnClickListener {
        AnonymousClass5() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            GsLoginActivity.this.mPreviousView = v;
            GsLoginActivity.this.mPullDownButton.setBackgroundResource(R.drawable.ip_history_up_arrow);
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService("input_method");
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            LayoutInflater inflater = (LayoutInflater) GsLoginActivity.this.getSystemService("layout_inflater");
            View loginWindow = inflater.inflate(R.layout.ip_login_history_layout, (ViewGroup) null);
            GsLoginActivity.this.mLoginHistoryListView = (ListView) loginWindow.findViewById(R.id.ip_login_history_list);
            GsLoginActivity.this.mIpHistoryListViewAdapter = GsLoginActivity.this.new IpHistoryListViewAdapter(loginWindow.getContext(), GsLoginActivity.this.mIpLoginInfoList);
            GsLoginActivity.this.mLoginHistoryListView.setAdapter((ListAdapter) GsLoginActivity.this.mIpHistoryListViewAdapter);
            GsLoginActivity.this.mLoginHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.GsLoginActivity.5.1
                AnonymousClass1() {
                }

                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GsLoginActivity.this.mTextIPOrSn.setText(((GsMobileLoginInfo) GsLoginActivity.this.mIpLoginInfoList.get(position)).getStb_ip_address_disp());
                    GsLoginActivity.this.mTextIPOrSn.setSelection(((GsMobileLoginInfo) GsLoginActivity.this.mIpLoginInfoList.get(position)).getStb_ip_address_disp().length());
                    GsLoginActivity.this.mIpHistoryListPopupWindow.dismiss();
                }
            });
            GsLoginActivity.this.mIpHistoryListPopupWindow = new PopupWindow(loginWindow, GsLoginActivity.this.mTextIPOrSn.getWidth(), -2, true);
            GsLoginActivity.this.mIpHistoryListPopupWindow.setOutsideTouchable(true);
            GsLoginActivity.this.mIpHistoryListPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            GsLoginActivity.this.mIpHistoryListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: mktvsmart.screen.GsLoginActivity.5.2
                AnonymousClass2() {
                }

                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    GsLoginActivity.this.mPullDownButton.setBackgroundResource(R.drawable.ip_history_down_arrow);
                    InputMethodManager imm2 = (InputMethodManager) GsLoginActivity.this.mPreviousView.getContext().getSystemService("input_method");
                    imm2.toggleSoftInput(0, 2);
                }
            });
            if (!GsLoginActivity.this.mIpHistoryListPopupWindow.isShowing()) {
                GsLoginActivity.this.mIpHistoryListPopupWindow.showAsDropDown(GsLoginActivity.this.mTextIPOrSn, 0, 0);
            }
        }

        /* renamed from: mktvsmart.screen.GsLoginActivity$5$1 */
        class AnonymousClass1 implements AdapterView.OnItemClickListener {
            AnonymousClass1() {
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GsLoginActivity.this.mTextIPOrSn.setText(((GsMobileLoginInfo) GsLoginActivity.this.mIpLoginInfoList.get(position)).getStb_ip_address_disp());
                GsLoginActivity.this.mTextIPOrSn.setSelection(((GsMobileLoginInfo) GsLoginActivity.this.mIpLoginInfoList.get(position)).getStb_ip_address_disp().length());
                GsLoginActivity.this.mIpHistoryListPopupWindow.dismiss();
            }
        }

        /* renamed from: mktvsmart.screen.GsLoginActivity$5$2 */
        class AnonymousClass2 implements PopupWindow.OnDismissListener {
            AnonymousClass2() {
            }

            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                GsLoginActivity.this.mPullDownButton.setBackgroundResource(R.drawable.ip_history_down_arrow);
                InputMethodManager imm2 = (InputMethodManager) GsLoginActivity.this.mPreviousView.getContext().getSystemService("input_method");
                imm2.toggleSoftInput(0, 2);
            }
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mBroadcastRecvThread = new UdpSocketReceiveBroadcastThread();
        this.mBroadcastRecvThread.start();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.mBroadcastRecvThread.interrupt();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.mMsgProc.recycle();
        Log.d("GsLoginActivity", "isFinishing " + isFinishing());
    }

    public void findviews() {
        this.mTextIPOrSn = (EditText) findViewById(R.id.EditIP);
        this.mPort = (EditText) findViewById(R.id.EditPort);
        this.mConnect = (Button) findViewById(R.id.connect);
        this.mPullDownButton = (Button) findViewById(R.id.ip_history_down_arrow);
        this.mSwitchLogin = (RelativeLayout) findViewById(R.id.layoutLoginSwitchBack);
        this.mPort.setText("20000");
        if (this.mIpLoginInfoList.size() > 0) {
            this.mTextIPOrSn.setText(this.mIpLoginInfoList.get(0).getStb_ip_address_disp());
            this.mTextIPOrSn.setSelection(this.mIpLoginInfoList.get(0).getStb_ip_address_disp().length());
        }
    }

    /* renamed from: mktvsmart.screen.GsLoginActivity$6 */
    class AnonymousClass6 implements View.OnTouchListener {
        AnonymousClass6() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            int even = event.getAction();
            switch (even) {
                case 0:
                    GsLoginActivity.this.mConnect.setBackgroundDrawable(GsLoginActivity.this.getResources().getDrawable(R.drawable.logined));
                    break;
                case 1:
                    int inputType = GsLoginActivity.this.checkInputText(GsLoginActivity.this.mTextIPOrSn.getText().toString());
                    if (inputType == 0) {
                        Toast.makeText(GsLoginActivity.this, GsLoginActivity.this.getResources().getString(R.string.prompt_input_ip_or_port), 0).show();
                    } else {
                        Log.i("test", "test receiveServerBack = haha test1");
                        GsLoginActivity.this.mWaitDialog = DialogBuilder.showProgressDialog((Context) GsLoginActivity.this, GsLoginActivity.this.getString(R.string.Logining), GsLoginActivity.this.getString(R.string.please_wait), false);
                        String textIpOrSn = GsLoginActivity.this.mTextIPOrSn.getText().toString();
                        if (inputType == 2) {
                            Log.i("test", "test receiveServerBack = haha test2");
                            int index = GsLoginActivity.this.getSnInBroadcastIndex(textIpOrSn);
                            if (index != -1) {
                                Log.i("test", "test receiveServerBack = haha test3");
                                String ipAddress = ((GsMobileLoginInfo) GsLoginActivity.this.mStbInfoList.get(index)).getStb_ip_address_disp();
                                GsLoginActivity.this.connectStbByIp(ipAddress);
                            } else {
                                Log.i("test", "test receiveServerBack = haha test4");
                                new Thread() { // from class: mktvsmart.screen.GsLoginActivity.6.1
                                    private final /* synthetic */ String val$textIpOrSn;

                                    AnonymousClass1(String textIpOrSn2) {
                                        str = textIpOrSn2;
                                    }

                                    @Override // java.lang.Thread, java.lang.Runnable
                                    public void run() throws JSONException, IOException {
                                        super.run();
                                        Log.i("test", "test receiveServerBack = haha test5");
                                        System.out.println("textIpOrSn " + str);
                                        String ipAddress2 = NetWorkUtils.getWanIpBySN(str);
                                        Log.i("test", "test receiveServerBack = haha test6");
                                        System.out.println("obtain ip by sn " + ipAddress2);
                                        Message dataMessage = Message.obtain();
                                        dataMessage.what = GlobalConstantValue.GSCMD_GET_IP_BY_SN;
                                        dataMessage.arg1 = 0;
                                        dataMessage.obj = ipAddress2;
                                        GsLoginActivity.this.mMsgProc.postMessage(dataMessage);
                                    }
                                }.start();
                            }
                        } else {
                            GsLoginActivity.this.connectStbByIp(textIpOrSn2);
                        }
                    }
                    GsLoginActivity.this.mConnect.setBackgroundDrawable(GsLoginActivity.this.getResources().getDrawable(R.drawable.login));
                    break;
            }
            return true;
        }

        /* renamed from: mktvsmart.screen.GsLoginActivity$6$1 */
        class AnonymousClass1 extends Thread {
            private final /* synthetic */ String val$textIpOrSn;

            AnonymousClass1(String textIpOrSn2) {
                str = textIpOrSn2;
            }

            @Override // java.lang.Thread, java.lang.Runnable
            public void run() throws JSONException, IOException {
                super.run();
                Log.i("test", "test receiveServerBack = haha test5");
                System.out.println("textIpOrSn " + str);
                String ipAddress2 = NetWorkUtils.getWanIpBySN(str);
                Log.i("test", "test receiveServerBack = haha test6");
                System.out.println("obtain ip by sn " + ipAddress2);
                Message dataMessage = Message.obtain();
                dataMessage.what = GlobalConstantValue.GSCMD_GET_IP_BY_SN;
                dataMessage.arg1 = 0;
                dataMessage.obj = ipAddress2;
                GsLoginActivity.this.mMsgProc.postMessage(dataMessage);
            }
        }
    }

    public void setonTouch() {
        this.mConnect.setOnTouchListener(new View.OnTouchListener() { // from class: mktvsmart.screen.GsLoginActivity.6
            AnonymousClass6() {
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                int even = event.getAction();
                switch (even) {
                    case 0:
                        GsLoginActivity.this.mConnect.setBackgroundDrawable(GsLoginActivity.this.getResources().getDrawable(R.drawable.logined));
                        break;
                    case 1:
                        int inputType = GsLoginActivity.this.checkInputText(GsLoginActivity.this.mTextIPOrSn.getText().toString());
                        if (inputType == 0) {
                            Toast.makeText(GsLoginActivity.this, GsLoginActivity.this.getResources().getString(R.string.prompt_input_ip_or_port), 0).show();
                        } else {
                            Log.i("test", "test receiveServerBack = haha test1");
                            GsLoginActivity.this.mWaitDialog = DialogBuilder.showProgressDialog((Context) GsLoginActivity.this, GsLoginActivity.this.getString(R.string.Logining), GsLoginActivity.this.getString(R.string.please_wait), false);
                            String textIpOrSn2 = GsLoginActivity.this.mTextIPOrSn.getText().toString();
                            if (inputType == 2) {
                                Log.i("test", "test receiveServerBack = haha test2");
                                int index = GsLoginActivity.this.getSnInBroadcastIndex(textIpOrSn2);
                                if (index != -1) {
                                    Log.i("test", "test receiveServerBack = haha test3");
                                    String ipAddress = ((GsMobileLoginInfo) GsLoginActivity.this.mStbInfoList.get(index)).getStb_ip_address_disp();
                                    GsLoginActivity.this.connectStbByIp(ipAddress);
                                } else {
                                    Log.i("test", "test receiveServerBack = haha test4");
                                    new Thread() { // from class: mktvsmart.screen.GsLoginActivity.6.1
                                        private final /* synthetic */ String val$textIpOrSn;

                                        AnonymousClass1(String textIpOrSn22) {
                                            str = textIpOrSn22;
                                        }

                                        @Override // java.lang.Thread, java.lang.Runnable
                                        public void run() throws JSONException, IOException {
                                            super.run();
                                            Log.i("test", "test receiveServerBack = haha test5");
                                            System.out.println("textIpOrSn " + str);
                                            String ipAddress2 = NetWorkUtils.getWanIpBySN(str);
                                            Log.i("test", "test receiveServerBack = haha test6");
                                            System.out.println("obtain ip by sn " + ipAddress2);
                                            Message dataMessage = Message.obtain();
                                            dataMessage.what = GlobalConstantValue.GSCMD_GET_IP_BY_SN;
                                            dataMessage.arg1 = 0;
                                            dataMessage.obj = ipAddress2;
                                            GsLoginActivity.this.mMsgProc.postMessage(dataMessage);
                                        }
                                    }.start();
                                }
                            } else {
                                GsLoginActivity.this.connectStbByIp(textIpOrSn22);
                            }
                        }
                        GsLoginActivity.this.mConnect.setBackgroundDrawable(GsLoginActivity.this.getResources().getDrawable(R.drawable.login));
                        break;
                }
                return true;
            }

            /* renamed from: mktvsmart.screen.GsLoginActivity$6$1 */
            class AnonymousClass1 extends Thread {
                private final /* synthetic */ String val$textIpOrSn;

                AnonymousClass1(String textIpOrSn22) {
                    str = textIpOrSn22;
                }

                @Override // java.lang.Thread, java.lang.Runnable
                public void run() throws JSONException, IOException {
                    super.run();
                    Log.i("test", "test receiveServerBack = haha test5");
                    System.out.println("textIpOrSn " + str);
                    String ipAddress2 = NetWorkUtils.getWanIpBySN(str);
                    Log.i("test", "test receiveServerBack = haha test6");
                    System.out.println("obtain ip by sn " + ipAddress2);
                    Message dataMessage = Message.obtain();
                    dataMessage.what = GlobalConstantValue.GSCMD_GET_IP_BY_SN;
                    dataMessage.arg1 = 0;
                    dataMessage.obj = ipAddress2;
                    GsLoginActivity.this.mMsgProc.postMessage(dataMessage);
                }
            }
        });
    }

    /* renamed from: mktvsmart.screen.GsLoginActivity$7 */
    class AnonymousClass7 extends Thread {
        private final /* synthetic */ String val$ipAddress;

        AnonymousClass7(String str) {
            str = str;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws InterruptedException, IOException {
            super.run();
            System.out.println("GsConnectToSTB.connecttoserver");
            GsMobileLoginInfo loginInfoTemp = GsConnectToSTB.connecttoserver(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM, 1);
            GsLoginActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginActivity.7.1
                private final /* synthetic */ String val$ipAddress;
                private final /* synthetic */ GsMobileLoginInfo val$loginInfoTemp;

                AnonymousClass1(GsMobileLoginInfo loginInfoTemp2, String str) {
                    gsMobileLoginInfo = loginInfoTemp2;
                    str = str;
                }

                @Override // java.lang.Runnable
                public void run() {
                    if (gsMobileLoginInfo.getmConnectStatus() <= 0) {
                        if (gsMobileLoginInfo.getmConnectStatus() >= 0) {
                            return;
                        }
                        GsLoginActivity.this.dismissWaitDialog();
                        GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, gsMobileLoginInfo.getmConnectStatus());
                        return;
                    }
                    GsLoginActivity.this.preserveLoginInfo(gsMobileLoginInfo);
                    Log.d("cur_stb_info Platform_id", new StringBuilder().append(gsMobileLoginInfo.getPlatform_id()).toString());
                    if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                        Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                        Intent serviceintent = new Intent(GsLoginActivity.this, (Class<?>) HiMscreenConnectService.class);
                        serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                        serviceintent.putExtra("address", str);
                        GsLoginActivity.this.startService(serviceintent);
                    }
                    GsLoginActivity.this.switchActivity(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
                }
            });
        }

        /* renamed from: mktvsmart.screen.GsLoginActivity$7$1 */
        class AnonymousClass1 implements Runnable {
            private final /* synthetic */ String val$ipAddress;
            private final /* synthetic */ GsMobileLoginInfo val$loginInfoTemp;

            AnonymousClass1(GsMobileLoginInfo loginInfoTemp2, String str) {
                gsMobileLoginInfo = loginInfoTemp2;
                str = str;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (gsMobileLoginInfo.getmConnectStatus() <= 0) {
                    if (gsMobileLoginInfo.getmConnectStatus() >= 0) {
                        return;
                    }
                    GsLoginActivity.this.dismissWaitDialog();
                    GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, gsMobileLoginInfo.getmConnectStatus());
                    return;
                }
                GsLoginActivity.this.preserveLoginInfo(gsMobileLoginInfo);
                Log.d("cur_stb_info Platform_id", new StringBuilder().append(gsMobileLoginInfo.getPlatform_id()).toString());
                if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                    Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                    Intent serviceintent = new Intent(GsLoginActivity.this, (Class<?>) HiMscreenConnectService.class);
                    serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                    serviceintent.putExtra("address", str);
                    GsLoginActivity.this.startService(serviceintent);
                }
                GsLoginActivity.this.switchActivity(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
            }
        }
    }

    private void innerIpConnect(String ipStr) {
        new Thread() { // from class: mktvsmart.screen.GsLoginActivity.7
            private final /* synthetic */ String val$ipAddress;

            AnonymousClass7(String ipStr2) {
                str = ipStr2;
            }

            @Override // java.lang.Thread, java.lang.Runnable
            public void run() throws InterruptedException, IOException {
                super.run();
                System.out.println("GsConnectToSTB.connecttoserver");
                GsMobileLoginInfo loginInfoTemp2 = GsConnectToSTB.connecttoserver(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM, 1);
                GsLoginActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginActivity.7.1
                    private final /* synthetic */ String val$ipAddress;
                    private final /* synthetic */ GsMobileLoginInfo val$loginInfoTemp;

                    AnonymousClass1(GsMobileLoginInfo loginInfoTemp22, String str) {
                        gsMobileLoginInfo = loginInfoTemp22;
                        str = str;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        if (gsMobileLoginInfo.getmConnectStatus() <= 0) {
                            if (gsMobileLoginInfo.getmConnectStatus() >= 0) {
                                return;
                            }
                            GsLoginActivity.this.dismissWaitDialog();
                            GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, gsMobileLoginInfo.getmConnectStatus());
                            return;
                        }
                        GsLoginActivity.this.preserveLoginInfo(gsMobileLoginInfo);
                        Log.d("cur_stb_info Platform_id", new StringBuilder().append(gsMobileLoginInfo.getPlatform_id()).toString());
                        if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                            Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                            Intent serviceintent = new Intent(GsLoginActivity.this, (Class<?>) HiMscreenConnectService.class);
                            serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                            serviceintent.putExtra("address", str);
                            GsLoginActivity.this.startService(serviceintent);
                        }
                        GsLoginActivity.this.switchActivity(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
                    }
                });
            }

            /* renamed from: mktvsmart.screen.GsLoginActivity$7$1 */
            class AnonymousClass1 implements Runnable {
                private final /* synthetic */ String val$ipAddress;
                private final /* synthetic */ GsMobileLoginInfo val$loginInfoTemp;

                AnonymousClass1(GsMobileLoginInfo loginInfoTemp22, String str) {
                    gsMobileLoginInfo = loginInfoTemp22;
                    str = str;
                }

                @Override // java.lang.Runnable
                public void run() {
                    if (gsMobileLoginInfo.getmConnectStatus() <= 0) {
                        if (gsMobileLoginInfo.getmConnectStatus() >= 0) {
                            return;
                        }
                        GsLoginActivity.this.dismissWaitDialog();
                        GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, gsMobileLoginInfo.getmConnectStatus());
                        return;
                    }
                    GsLoginActivity.this.preserveLoginInfo(gsMobileLoginInfo);
                    Log.d("cur_stb_info Platform_id", new StringBuilder().append(gsMobileLoginInfo.getPlatform_id()).toString());
                    if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                        Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                        Intent serviceintent = new Intent(GsLoginActivity.this, (Class<?>) HiMscreenConnectService.class);
                        serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                        serviceintent.putExtra("address", str);
                        GsLoginActivity.this.startService(serviceintent);
                    }
                    GsLoginActivity.this.switchActivity(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
                }
            }
        }.start();
    }

    /* renamed from: mktvsmart.screen.GsLoginActivity$8 */
    class AnonymousClass8 extends Thread {
        private final /* synthetic */ String val$ipAddress;
        private final /* synthetic */ String val$ipStr;

        AnonymousClass8(String str, String str2) {
            str = str;
            str = str2;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws InterruptedException, IOException {
            super.run();
            Looper.prepare();
            GsLoginActivity.this.mPnpDeveiceList = GsLoginActivity.this.getUpnpDeveiceList(str);
            if (GsLoginActivity.this.mPnpDeveiceList.size() > 0) {
                if (GsLoginActivity.this.mPnpDeveiceList.size() == 1) {
                    String addressTemp = ((GsMobileLoginInfo) GsLoginActivity.this.mPnpDeveiceList.get(0)).getUpnpIp();
                    int portTemp = ((GsMobileLoginInfo) GsLoginActivity.this.mPnpDeveiceList.get(0)).getUpnpPort();
                    GsMobileLoginInfo loginInfoTemp = GsConnectToSTB.upnpConnectToServer(addressTemp, portTemp, 0);
                    GsLoginActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginActivity.8.2
                        private final /* synthetic */ String val$ipAddress;
                        private final /* synthetic */ String val$ipStr;
                        private final /* synthetic */ GsMobileLoginInfo val$loginInfoTemp;

                        AnonymousClass2(GsMobileLoginInfo loginInfoTemp2, String str, String str2) {
                            gsMobileLoginInfo = loginInfoTemp2;
                            str = str;
                            str = str2;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            if (gsMobileLoginInfo.getmConnectStatus() > 0) {
                                gsMobileLoginInfo.setmIpLoginMark(1);
                                gsMobileLoginInfo.setStb_ip_address_disp(str);
                                GsLoginActivity.this.preserveLoginInfo(gsMobileLoginInfo);
                                Log.d("cur_stb_info Platform_id", new StringBuilder().append(gsMobileLoginInfo.getPlatform_id()).toString());
                                if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                                    Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                                    Intent serviceintent = new Intent(GsLoginActivity.this, (Class<?>) HiMscreenConnectService.class);
                                    serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                                    serviceintent.putExtra("address", str);
                                    GsLoginActivity.this.startService(serviceintent);
                                }
                                GsLoginActivity.this.switchActivity(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
                                return;
                            }
                            if (gsMobileLoginInfo.getmConnectStatus() >= 0) {
                                return;
                            }
                            GsLoginActivity.this.dismissWaitDialog();
                            GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, gsMobileLoginInfo.getmConnectStatus());
                        }
                    });
                    return;
                }
                GsLoginActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginActivity.8.3
                    private final /* synthetic */ String val$ipAddress;

                    AnonymousClass3(String str) {
                        str = str;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        GsLoginActivity.this.dismissWaitDialog();
                        GsUpnpDeveiceListDialog tempUpnpDeviceListDialog = new GsUpnpDeveiceListDialog(GsLoginActivity.this, GsLoginActivity.this, GsLoginActivity.this.mPnpDeveiceList, str);
                        tempUpnpDeviceListDialog.show();
                        GsLoginActivity.this.mPnpDeveiceList.removeAll(GsLoginActivity.this.mPnpDeveiceList);
                    }
                });
                return;
            }
            GsLoginActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginActivity.8.1
                AnonymousClass1() {
                }

                @Override // java.lang.Runnable
                public void run() {
                    GsLoginActivity.this.dismissWaitDialog();
                    GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, -2);
                }
            });
        }

        /* renamed from: mktvsmart.screen.GsLoginActivity$8$1 */
        class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
            }

            @Override // java.lang.Runnable
            public void run() {
                GsLoginActivity.this.dismissWaitDialog();
                GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, -2);
            }
        }

        /* renamed from: mktvsmart.screen.GsLoginActivity$8$2 */
        class AnonymousClass2 implements Runnable {
            private final /* synthetic */ String val$ipAddress;
            private final /* synthetic */ String val$ipStr;
            private final /* synthetic */ GsMobileLoginInfo val$loginInfoTemp;

            AnonymousClass2(GsMobileLoginInfo loginInfoTemp2, String str, String str2) {
                gsMobileLoginInfo = loginInfoTemp2;
                str = str;
                str = str2;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (gsMobileLoginInfo.getmConnectStatus() > 0) {
                    gsMobileLoginInfo.setmIpLoginMark(1);
                    gsMobileLoginInfo.setStb_ip_address_disp(str);
                    GsLoginActivity.this.preserveLoginInfo(gsMobileLoginInfo);
                    Log.d("cur_stb_info Platform_id", new StringBuilder().append(gsMobileLoginInfo.getPlatform_id()).toString());
                    if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                        Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                        Intent serviceintent = new Intent(GsLoginActivity.this, (Class<?>) HiMscreenConnectService.class);
                        serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                        serviceintent.putExtra("address", str);
                        GsLoginActivity.this.startService(serviceintent);
                    }
                    GsLoginActivity.this.switchActivity(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
                    return;
                }
                if (gsMobileLoginInfo.getmConnectStatus() >= 0) {
                    return;
                }
                GsLoginActivity.this.dismissWaitDialog();
                GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, gsMobileLoginInfo.getmConnectStatus());
            }
        }

        /* renamed from: mktvsmart.screen.GsLoginActivity$8$3 */
        class AnonymousClass3 implements Runnable {
            private final /* synthetic */ String val$ipAddress;

            AnonymousClass3(String str) {
                str = str;
            }

            @Override // java.lang.Runnable
            public void run() {
                GsLoginActivity.this.dismissWaitDialog();
                GsUpnpDeveiceListDialog tempUpnpDeviceListDialog = new GsUpnpDeveiceListDialog(GsLoginActivity.this, GsLoginActivity.this, GsLoginActivity.this.mPnpDeveiceList, str);
                tempUpnpDeviceListDialog.show();
                GsLoginActivity.this.mPnpDeveiceList.removeAll(GsLoginActivity.this.mPnpDeveiceList);
            }
        }
    }

    private void externalIpConnect(String ipStr) {
        new Thread() { // from class: mktvsmart.screen.GsLoginActivity.8
            private final /* synthetic */ String val$ipAddress;
            private final /* synthetic */ String val$ipStr;

            AnonymousClass8(String ipStr2, String ipStr22) {
                str = ipStr22;
                str = ipStr22;
            }

            @Override // java.lang.Thread, java.lang.Runnable
            public void run() throws InterruptedException, IOException {
                super.run();
                Looper.prepare();
                GsLoginActivity.this.mPnpDeveiceList = GsLoginActivity.this.getUpnpDeveiceList(str);
                if (GsLoginActivity.this.mPnpDeveiceList.size() > 0) {
                    if (GsLoginActivity.this.mPnpDeveiceList.size() == 1) {
                        String addressTemp = ((GsMobileLoginInfo) GsLoginActivity.this.mPnpDeveiceList.get(0)).getUpnpIp();
                        int portTemp = ((GsMobileLoginInfo) GsLoginActivity.this.mPnpDeveiceList.get(0)).getUpnpPort();
                        GsMobileLoginInfo loginInfoTemp2 = GsConnectToSTB.upnpConnectToServer(addressTemp, portTemp, 0);
                        GsLoginActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginActivity.8.2
                            private final /* synthetic */ String val$ipAddress;
                            private final /* synthetic */ String val$ipStr;
                            private final /* synthetic */ GsMobileLoginInfo val$loginInfoTemp;

                            AnonymousClass2(GsMobileLoginInfo loginInfoTemp22, String str, String str2) {
                                gsMobileLoginInfo = loginInfoTemp22;
                                str = str;
                                str = str2;
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                if (gsMobileLoginInfo.getmConnectStatus() > 0) {
                                    gsMobileLoginInfo.setmIpLoginMark(1);
                                    gsMobileLoginInfo.setStb_ip_address_disp(str);
                                    GsLoginActivity.this.preserveLoginInfo(gsMobileLoginInfo);
                                    Log.d("cur_stb_info Platform_id", new StringBuilder().append(gsMobileLoginInfo.getPlatform_id()).toString());
                                    if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                                        Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                                        Intent serviceintent = new Intent(GsLoginActivity.this, (Class<?>) HiMscreenConnectService.class);
                                        serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                                        serviceintent.putExtra("address", str);
                                        GsLoginActivity.this.startService(serviceintent);
                                    }
                                    GsLoginActivity.this.switchActivity(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
                                    return;
                                }
                                if (gsMobileLoginInfo.getmConnectStatus() >= 0) {
                                    return;
                                }
                                GsLoginActivity.this.dismissWaitDialog();
                                GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, gsMobileLoginInfo.getmConnectStatus());
                            }
                        });
                        return;
                    }
                    GsLoginActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginActivity.8.3
                        private final /* synthetic */ String val$ipAddress;

                        AnonymousClass3(String str) {
                            str = str;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            GsLoginActivity.this.dismissWaitDialog();
                            GsUpnpDeveiceListDialog tempUpnpDeviceListDialog = new GsUpnpDeveiceListDialog(GsLoginActivity.this, GsLoginActivity.this, GsLoginActivity.this.mPnpDeveiceList, str);
                            tempUpnpDeviceListDialog.show();
                            GsLoginActivity.this.mPnpDeveiceList.removeAll(GsLoginActivity.this.mPnpDeveiceList);
                        }
                    });
                    return;
                }
                GsLoginActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginActivity.8.1
                    AnonymousClass1() {
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        GsLoginActivity.this.dismissWaitDialog();
                        GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, -2);
                    }
                });
            }

            /* renamed from: mktvsmart.screen.GsLoginActivity$8$1 */
            class AnonymousClass1 implements Runnable {
                AnonymousClass1() {
                }

                @Override // java.lang.Runnable
                public void run() {
                    GsLoginActivity.this.dismissWaitDialog();
                    GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, -2);
                }
            }

            /* renamed from: mktvsmart.screen.GsLoginActivity$8$2 */
            class AnonymousClass2 implements Runnable {
                private final /* synthetic */ String val$ipAddress;
                private final /* synthetic */ String val$ipStr;
                private final /* synthetic */ GsMobileLoginInfo val$loginInfoTemp;

                AnonymousClass2(GsMobileLoginInfo loginInfoTemp22, String str, String str2) {
                    gsMobileLoginInfo = loginInfoTemp22;
                    str = str;
                    str = str2;
                }

                @Override // java.lang.Runnable
                public void run() {
                    if (gsMobileLoginInfo.getmConnectStatus() > 0) {
                        gsMobileLoginInfo.setmIpLoginMark(1);
                        gsMobileLoginInfo.setStb_ip_address_disp(str);
                        GsLoginActivity.this.preserveLoginInfo(gsMobileLoginInfo);
                        Log.d("cur_stb_info Platform_id", new StringBuilder().append(gsMobileLoginInfo.getPlatform_id()).toString());
                        if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                            Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                            Intent serviceintent = new Intent(GsLoginActivity.this, (Class<?>) HiMscreenConnectService.class);
                            serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                            serviceintent.putExtra("address", str);
                            GsLoginActivity.this.startService(serviceintent);
                        }
                        GsLoginActivity.this.switchActivity(str, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
                        return;
                    }
                    if (gsMobileLoginInfo.getmConnectStatus() >= 0) {
                        return;
                    }
                    GsLoginActivity.this.dismissWaitDialog();
                    GsConnectToSTB.makeTextForConnectError(GsLoginActivity.this, gsMobileLoginInfo.getmConnectStatus());
                }
            }

            /* renamed from: mktvsmart.screen.GsLoginActivity$8$3 */
            class AnonymousClass3 implements Runnable {
                private final /* synthetic */ String val$ipAddress;

                AnonymousClass3(String str) {
                    str = str;
                }

                @Override // java.lang.Runnable
                public void run() {
                    GsLoginActivity.this.dismissWaitDialog();
                    GsUpnpDeveiceListDialog tempUpnpDeviceListDialog = new GsUpnpDeveiceListDialog(GsLoginActivity.this, GsLoginActivity.this, GsLoginActivity.this.mPnpDeveiceList, str);
                    tempUpnpDeviceListDialog.show();
                    GsLoginActivity.this.mPnpDeveiceList.removeAll(GsLoginActivity.this.mPnpDeveiceList);
                }
            }
        }.start();
    }

    private ArrayList<Integer> getPortList() throws NumberFormatException {
        int port = Integer.parseInt(this.mPort.getText().toString());
        ArrayList<Integer> portList = new ArrayList<>();
        portList.add(Integer.valueOf(port));
        for (int index = 20001; index <= 20005; index++) {
            portList.add(Integer.valueOf(index));
        }
        return portList;
    }

    public ArrayList<GsMobileLoginInfo> getUpnpDeveiceList(String ipStr) throws InterruptedException, NumberFormatException, IOException {
        ArrayList<Integer> portList = getPortList();
        ArrayList<GsMobileLoginInfo> upnpDeveiceList = new ArrayList<>();
        for (int index = 0; index < portList.size(); index++) {
            GsMobileLoginInfo loginInfoTemp = GsConnectToSTB.upnpGetDeviceList(ipStr, portList.get(index).intValue(), 1);
            if (loginInfoTemp.getmConnectStatus() > 0) {
                loginInfoTemp.setUpnpIp(ipStr);
                loginInfoTemp.setStb_ip_address_disp(ipStr);
                loginInfoTemp.setUpnpPort(portList.get(index).intValue());
                upnpDeveiceList.add(loginInfoTemp);
            }
        }
        return upnpDeveiceList;
    }

    public void preserveLoginInfo(GsMobileLoginInfo loginInfo) {
        this.mEditLoginHistoryFile.putListToFile(loginInfo, this.mHistoryStbInfoList);
        GMScreenGlobalInfo.setmCurStbInfo(loginInfo);
    }

    public void switchActivity(String ipAddress, int port) {
        Intent intent = new Intent();
        intent.putExtra("Address", ipAddress);
        intent.putExtra("Port", port);
        intent.setClass(this, GsMainMenuActivity.class);
        startActivity(intent);
        dismissWaitDialog();
        finish();
    }

    public void connectStbByIp(String ipStr) {
        if (NetWorkUtils.isInnerIP(ipStr)) {
            innerIpConnect(ipStr);
        } else {
            externalIpConnect(ipStr);
        }
    }

    public void dismissWaitDialog() {
        if (this.mWaitDialog.isShowing()) {
            this.mWaitDialog.dismiss();
        }
    }

    public int checkInputText(String inputStr) {
        if (inputStr == null || inputStr.length() <= 0) {
            return 0;
        }
        if (NetWorkUtils.isValidIp(inputStr)) {
            return 1;
        }
        return isValidSN(inputStr) ? 2 : 0;
    }

    private boolean isValidSN(String text) {
        return text != null && text.trim().matches("\\d{12}");
    }

    public int getSnInBroadcastIndex(String snStr) {
        for (int index = 0; index < this.mStbInfoList.size(); index++) {
            if (snStr.equals(this.mStbInfoList.get(index).getStb_sn_disp())) {
                int ret = index;
                return ret;
            }
        }
        return -1;
    }

    private class IpHistoryListViewAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<GsMobileLoginInfo> listView;

        private class ViewHolder {
            public LinearLayout spinnerBack;
            public TextView textView;

            private ViewHolder() {
            }

            /* synthetic */ ViewHolder(IpHistoryListViewAdapter ipHistoryListViewAdapter, ViewHolder viewHolder) {
                this();
            }
        }

        public IpHistoryListViewAdapter(Context context, ArrayList<GsMobileLoginInfo> listView) {
            this.listView = new ArrayList<>();
            this.context = context;
            this.listView = listView;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.listView.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.listView.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder(this, null);
                LayoutInflater inflater = LayoutInflater.from(this.context);
                convertView = inflater.inflate(R.layout.ip_item_layout, (ViewGroup) null);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.ipItemText);
                viewHolder.spinnerBack = (LinearLayout) convertView.findViewById(R.id.ipItemBack);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(this.listView.get(position).getStb_ip_address_disp());
            if (GsLoginActivity.this.mTextIPOrSn.getText().toString().equals(this.listView.get(position).getStb_ip_address_disp())) {
                viewHolder.spinnerBack.setBackgroundResource(R.drawable.list_item_focus);
                viewHolder.textView.setTextColor(GsLoginActivity.this.getResources().getColor(R.color.white));
            } else {
                viewHolder.spinnerBack.setBackgroundResource(R.drawable.disp_channel);
            }
            return convertView;
        }
    }
}
