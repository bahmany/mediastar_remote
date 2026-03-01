package mktvsmart.screen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import mktvsmart.screen.hisientry.HiMscreenConnectService;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.socketthread.UdpSocketReceiveBroadcastThread;
import mktvsmart.screen.update.UpdateManager;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;
import mktvsmart.screen.util.NetWorkUtils;

/* loaded from: classes.dex */
public class GsLoginListActivity extends Activity {
    private String Address;
    private histroyAdapter adapter;
    private UdpSocketReceiveBroadcastThread broadcastRecvThread;
    private Dialog dialog;
    private RelativeLayout layoutSwitchLoginStyle;
    private Button loginHistory;
    private ListView loginList;
    private Button mAboutButton;
    private GsMobileLoginInfo mLoginTemp;
    private UpdateManager mUpdateManager;
    private MessageProcessor msgProc;
    private ADSProgressDialog waitDialog;
    private ArrayList<GsMobileLoginInfo> stbInfoList = new ArrayList<>();
    private ArrayList<GsMobileLoginInfo> historyStbInfoList = new ArrayList<>();
    private EditLoginHistoryFile mEditLoginHistoryFile = new EditLoginHistoryFile(this);
    private boolean bExist = false;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_login_menu);
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.recycle();
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_BROADCAST_LOGIN_INFO_UPDATED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsLoginListActivity.1
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                try {
                    GsLoginListActivity.this.stbInfoList = (ArrayList) msg.obj;
                    SimpleAdapter adapter = new SimpleAdapter(GsLoginListActivity.this, GsLoginListActivity.this.GetData(GsLoginListActivity.this.stbInfoList), R.layout.auto_login_item, new String[]{"string_time", "string_name"}, new int[]{R.id.login_item_model, R.id.login_item_sn});
                    GsLoginListActivity.this.loginList.setAdapter((ListAdapter) adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.msgProc.setOnMessageProcess(0, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsLoginListActivity.2
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    GsLoginListActivity.this.mEditLoginHistoryFile.putListToFile(GsLoginListActivity.this.mLoginTemp, GsLoginListActivity.this.historyStbInfoList);
                    Intent intent = new Intent();
                    intent.putExtra("Address", GsLoginListActivity.this.Address);
                    intent.putExtra("Port", GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
                    intent.setClass(GsLoginListActivity.this, GsMainMenuActivity.class);
                    GsLoginListActivity.this.startActivity(intent);
                    if (GsLoginListActivity.this.waitDialog != null && GsLoginListActivity.this.waitDialog.isShowing()) {
                        GsLoginListActivity.this.waitDialog.dismiss();
                    }
                    GsLoginListActivity.this.finish();
                }
            }
        });
        this.mEditLoginHistoryFile.getListFromFile(this.historyStbInfoList);
        this.layoutSwitchLoginStyle = (RelativeLayout) findViewById(R.id.layoutLoginSwitch);
        this.loginHistory = (Button) findViewById(R.id.pointer_history);
        this.mAboutButton = (Button) findViewById(R.id.about_button);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        this.loginHistory.setOnClickListener(new AnonymousClass3(d));
        this.mAboutButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLoginListActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent = new Intent(GsLoginListActivity.this, (Class<?>) GsAboutMeActivity.class);
                GsLoginListActivity.this.startActivity(intent);
            }
        });
        this.layoutSwitchLoginStyle.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLoginListActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(GsLoginListActivity.this, GsLoginActivity.class);
                GsLoginListActivity.this.startActivity(intent);
                GsLoginListActivity.this.finish();
            }
        });
        this.loginList = (ListView) findViewById(R.id.listLogin);
        SimpleAdapter adapter = new SimpleAdapter(this, GetData(this.stbInfoList), R.layout.auto_login_item, new String[]{"string_time", "string_name"}, new int[]{R.id.login_item_model, R.id.login_item_sn});
        this.loginList.setAdapter((ListAdapter) adapter);
        try {
            this.loginList.setOnItemClickListener(new AnonymousClass6());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mUpdateManager = new UpdateManager(this);
    }

    /* renamed from: mktvsmart.screen.GsLoginListActivity$3, reason: invalid class name */
    class AnonymousClass3 implements View.OnClickListener {
        private final /* synthetic */ Display val$d;

        AnonymousClass3(Display display) {
            this.val$d = display;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            LayoutInflater inflater = LayoutInflater.from(GsLoginListActivity.this);
            View layout = inflater.inflate(R.layout.login_history_layout, (ViewGroup) null);
            ListView list = (ListView) layout.findViewById(R.id.login_history_list);
            Button cancelButton = (Button) layout.findViewById(R.id.history_cancel_btn);
            GsLoginListActivity.this.adapter = GsLoginListActivity.this.new histroyAdapter(GsLoginListActivity.this);
            list.setAdapter((ListAdapter) GsLoginListActivity.this.adapter);
            list.setOnItemClickListener(new AnonymousClass1());
            cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsLoginListActivity.3.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v2) {
                    GsLoginListActivity.this.dialog.dismiss();
                }
            });
            GsLoginListActivity.this.dialog = new Dialog(GsLoginListActivity.this, R.style.dialog);
            GsLoginListActivity.this.dialog.setContentView(layout, new ViewGroup.LayoutParams(-1, (int) (this.val$d.getHeight() * 0.9d)));
            GsLoginListActivity.this.dialog.setCanceledOnTouchOutside(false);
            GsLoginListActivity.this.dialog.show();
        }

        /* renamed from: mktvsmart.screen.GsLoginListActivity$3$1, reason: invalid class name */
        class AnonymousClass1 implements AdapterView.OnItemClickListener {
            AnonymousClass1() {
            }

            /* JADX WARN: Type inference failed for: r2v21, types: [mktvsmart.screen.GsLoginListActivity$3$1$1] */
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean verifyStbValid = GsLoginListActivity.this.VerifyStbValid((GsMobileLoginInfo) GsLoginListActivity.this.historyStbInfoList.get(position));
                if (verifyStbValid) {
                    GsLoginListActivity.this.waitDialog = DialogBuilder.showProgressDialog((Context) GsLoginListActivity.this, GsLoginListActivity.this.getString(R.string.Logining), GsLoginListActivity.this.getString(R.string.please_wait), false);
                    GsLoginListActivity.this.mLoginTemp = (GsMobileLoginInfo) GsLoginListActivity.this.historyStbInfoList.get(position);
                    try {
                        GsLoginListActivity.this.Address = ((GsMobileLoginInfo) GsLoginListActivity.this.historyStbInfoList.get(position)).getStb_ip_address_disp();
                        new Thread() { // from class: mktvsmart.screen.GsLoginListActivity.3.1.1
                            @Override // java.lang.Thread, java.lang.Runnable
                            public void run() throws InterruptedException, IOException {
                                final GsMobileLoginInfo loginInfoTemp;
                                super.run();
                                Looper.prepare();
                                System.out.println("click ip Address : " + GsLoginListActivity.this.Address);
                                if (NetWorkUtils.isInnerIP(GsLoginListActivity.this.Address)) {
                                    loginInfoTemp = GsConnectToSTB.connecttoserver(GsLoginListActivity.this.Address, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM, 0);
                                } else {
                                    loginInfoTemp = GsConnectToSTB.upnpConnectToServer(GsLoginListActivity.this.Address, GsLoginListActivity.this.mLoginTemp.getUpnpPort(), 0);
                                }
                                GMScreenGlobalInfo.setmCurStbInfo(loginInfoTemp);
                                if (loginInfoTemp.getmConnectStatus() < 0) {
                                    GsLoginListActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginListActivity.3.1.1.1
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            if (GsLoginListActivity.this.waitDialog.isShowing()) {
                                                GsLoginListActivity.this.waitDialog.dismiss();
                                            }
                                            GsConnectToSTB.makeTextForConnectError(GsLoginListActivity.this, loginInfoTemp.getmConnectStatus());
                                        }
                                    });
                                } else if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                                    Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                                    Intent serviceintent = new Intent(GsLoginListActivity.this, (Class<?>) HiMscreenConnectService.class);
                                    serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                                    serviceintent.putExtra("address", GsLoginListActivity.this.Address);
                                    GsLoginListActivity.this.startService(serviceintent);
                                }
                                Message isLogin = Message.obtain();
                                isLogin.what = 0;
                                isLogin.arg1 = loginInfoTemp.getmConnectStatus();
                                GsLoginListActivity.this.msgProc.postMessage(isLogin);
                                if (GsLoginListActivity.this.dialog != null) {
                                    GsLoginListActivity.this.dialog.dismiss();
                                    GsLoginListActivity.this.dialog = null;
                                }
                            }
                        }.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsLoginListActivity$6, reason: invalid class name */
    class AnonymousClass6 implements AdapterView.OnItemClickListener {
        AnonymousClass6() {
        }

        /* JADX WARN: Type inference failed for: r1v9, types: [mktvsmart.screen.GsLoginListActivity$6$1] */
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            try {
                GsLoginListActivity.this.waitDialog = DialogBuilder.showProgressDialog((Context) GsLoginListActivity.this, GsLoginListActivity.this.getString(R.string.Logining), GsLoginListActivity.this.getString(R.string.please_wait), false);
                GsLoginListActivity.this.mLoginTemp = (GsMobileLoginInfo) GsLoginListActivity.this.stbInfoList.get(arg2);
                GsLoginListActivity.this.mLoginTemp.setmIpLoginMark(0);
                System.out.println("click ip : " + GsLoginListActivity.this.mLoginTemp.getStb_ip_address_disp());
                GsLoginListActivity.this.Address = GsLoginListActivity.this.mLoginTemp.getStb_ip_address_disp();
                new Thread() { // from class: mktvsmart.screen.GsLoginListActivity.6.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() throws InterruptedException, IOException {
                        super.run();
                        Looper.prepare();
                        System.out.println("click ip Address : " + GsLoginListActivity.this.Address);
                        final GsMobileLoginInfo loginInfoTemp = GsConnectToSTB.connecttoserver(GsLoginListActivity.this.Address, GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM, 0);
                        GMScreenGlobalInfo.setmCurStbInfo(loginInfoTemp);
                        if (loginInfoTemp.getmConnectStatus() < 0) {
                            GsLoginListActivity.this.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.GsLoginListActivity.6.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    if (GsLoginListActivity.this.waitDialog.isShowing()) {
                                        GsLoginListActivity.this.waitDialog.dismiss();
                                    }
                                    GsConnectToSTB.makeTextForConnectError(GsLoginListActivity.this, loginInfoTemp.getmConnectStatus());
                                }
                            });
                        } else if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                            Log.d("GsLoginActivity", "startService HiMscreenConnectService");
                            Intent serviceintent = new Intent(GsLoginListActivity.this, (Class<?>) HiMscreenConnectService.class);
                            serviceintent.setAction(HiMscreenConnectService.CONNECT_ACTION);
                            serviceintent.putExtra("address", GsLoginListActivity.this.Address);
                            GsLoginListActivity.this.startService(serviceintent);
                        }
                        Message isLogin = Message.obtain();
                        isLogin.what = 0;
                        isLogin.arg1 = loginInfoTemp.getmConnectStatus();
                        GsLoginListActivity.this.msgProc.postMessage(isLogin);
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (!UpdateManager.isUpdateCancle() && UpdateManager.isUpdateEnable(this) && getResources().getBoolean(R.bool.update_flag)) {
            this.mUpdateManager.checkUpdate(this, true);
        }
        this.broadcastRecvThread = new UdpSocketReceiveBroadcastThread();
        this.broadcastRecvThread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<Map<String, Object>> GetData(ArrayList<GsMobileLoginInfo> stbInfoList) {
        List<Map<String, Object>> data = new ArrayList<>();
        int visibleStbNum = stbInfoList.size();
        for (int index = 0; index < visibleStbNum; index++) {
            Map<String, Object> map = new HashMap<>();
            GsMobileLoginInfo loginTemp = stbInfoList.get(index);
            String stringModel = loginTemp.getModel_name();
            String stringSn = loginTemp.getStb_sn_disp();
            map.put("string_time", stringModel);
            map.put("string_name", stringSn);
            data.add(map);
        }
        return data;
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.broadcastRecvThread.interrupt();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.msgProc.recycle();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);
            if (getResources().getConfiguration().orientation == 2) {
                Log.v("Himi", "onConfigurationChanged_ORIENTATION_LANDSCAPE");
            } else if (getResources().getConfiguration().orientation == 1) {
                Log.v("Himi", "onConfigurationChanged_ORIENTATION_PORTRAIT");
            }
        } catch (Exception e) {
        }
    }

    private int judgeNetworkType(GsMobileLoginInfo info) {
        String[] strArr = new String[4];
        String[] array = info.getStb_ip_address_disp().split("\\.");
        int[] intArray = new int[array.length];
        for (int pos = 0; pos < array.length; pos++) {
            intArray[pos] = Integer.parseInt(array[pos]);
        }
        return (intArray[0] == 10 || (intArray[0] == 172 && intArray[1] >= 16 && intArray[1] <= 31) || (intArray[0] == 192 && intArray[1] == 168)) ? 0 : 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean VerifyStbValid(GsMobileLoginInfo info) {
        int networkType = judgeNetworkType(info);
        if (networkType == 1) {
            return true;
        }
        Iterator<GsMobileLoginInfo> it = this.stbInfoList.iterator();
        while (it.hasNext()) {
            GsMobileLoginInfo model = it.next();
            if (model.getStb_sn_disp().equals(info.getStb_sn_disp())) {
                return true;
            }
        }
        return false;
    }

    private class histroyAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public histroyAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return GsLoginListActivity.this.historyStbInfoList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return GsLoginListActivity.this.historyStbInfoList.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.auto_login_item, (ViewGroup) null);
            }
            LinearLayout loginBody = (LinearLayout) convertView.findViewById(R.id.login_body);
            TextView modelString = (TextView) convertView.findViewById(R.id.login_item_model);
            TextView snString = (TextView) convertView.findViewById(R.id.login_item_sn);
            if (position == parent.getChildCount()) {
                ViewGroup.LayoutParams lp = modelString.getLayoutParams();
                lp.width = KeyInfo.KEYCODE_DOUBLE_QUOTATION;
                lp.height = -1;
                modelString.setLayoutParams(lp);
                snString.setLayoutParams(lp);
                modelString.setTextSize(14.0f);
                snString.setTextSize(14.0f);
                modelString.setText(((GsMobileLoginInfo) GsLoginListActivity.this.historyStbInfoList.get(position)).getModel_name());
                snString.setText(((GsMobileLoginInfo) GsLoginListActivity.this.historyStbInfoList.get(position)).getStb_sn_disp());
                if (!GsLoginListActivity.this.VerifyStbValid((GsMobileLoginInfo) GsLoginListActivity.this.historyStbInfoList.get(position))) {
                    modelString.setTextColor(GsLoginListActivity.this.getResources().getColor(R.color.unable_text_color));
                    snString.setTextColor(GsLoginListActivity.this.getResources().getColor(R.color.unable_text_color));
                    loginBody.setBackgroundResource(R.drawable.login_list_item_bg_grey);
                }
            }
            return convertView;
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        Process.killProcess(Process.myPid());
    }
}
