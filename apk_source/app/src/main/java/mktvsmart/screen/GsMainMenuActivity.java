package mktvsmart.screen;

import android.app.ActivityGroup;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import com.google.android.gms.games.GamesStatusCodes;
import com.hisilicon.multiscreen.protocol.utils.ServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import mktvsmart.screen.channel.GsChannelListActivity;
import mktvsmart.screen.dataconvert.model.DataConvertFavorModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.gchat.ui.GChatActivity;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.sliderlayout.LeftSliderLayout;
import mktvsmart.screen.socketthread.SocketReceiveThread;
import mktvsmart.screen.statistics.StatisticsTask;
import mktvsmart.screen.vlc.LivePlayActivity;

/* loaded from: classes.dex */
public class GsMainMenuActivity extends ActivityGroup {
    private static final int SLIDE_LIST_MAIN_PAGE = 0;
    private static final int SLIDE_LIST_PARENTAL_CONTROL = 1;
    private static final int SLIDE_LIST_SIMPLE_REMOTE = 4;
    private static final int SLIDE_LIST_SOFTWARE_UPDATE = 2;
    private static final int SLIDE_LIST_STB_DEBUG = 3;
    View TabHostView;
    View currentView;
    Intent intent;
    LeftSliderLayout leftSliderLayout;
    private Dialog mDialog;
    private StatisticsTask mUserStatisticTask;
    ViewGroup mainContent;
    private MessageProcessor msgProc;
    private SocketReceiveThread recvThread;
    private String[] sliderOption;
    TabHost tabHost;
    private int currentPosition = 0;
    private int[] items_img = {R.drawable.mainpage, R.drawable.control_icon, R.drawable.swupdate_icon, R.drawable.debug_icon, R.drawable.remote_icon};
    private boolean bExistApp = false;

    private boolean isApplicationBroughtToBackground(Context context) throws SecurityException {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!isApplicationBroughtToBackground(this) && !GsChannelListActivity.enable_edit && event.getKeyCode() == 4 && event.getAction() == 1) {
            if (this.leftSliderLayout.isOpen()) {
                this.leftSliderLayout.close();
                return true;
            }
            LayoutInflater inflater = LayoutInflater.from(this);
            View layout = inflater.inflate(R.layout.exit_or_login_layout, (ViewGroup) null);
            RelativeLayout loginListRela = (RelativeLayout) layout.findViewById(R.id.login_list_relative);
            RelativeLayout exitAppRela = (RelativeLayout) layout.findViewById(R.id.exit_app_relative);
            RelativeLayout cancelRelative = (RelativeLayout) layout.findViewById(R.id.cancel_relative);
            TextView exitAppNameView = (TextView) layout.findViewById(R.id.exit_app_name);
            exitAppNameView.setText(String.valueOf(getResources().getString(R.string.exit)) + " " + getResources().getString(R.string.app_name));
            loginListRela.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsMainMenuActivity.1
                AnonymousClass1() {
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    GMScreenGlobalInfo.setmSendEmailFinished(true);
                    GsMainMenuActivity.this.mDialog.dismiss();
                    Intent intent = new Intent();
                    intent.setClass(GsMainMenuActivity.this, GsLoginListActivity.class);
                    GsMainMenuActivity.this.startActivity(intent);
                    GsMainMenuActivity.this.finish();
                }
            });
            exitAppRela.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsMainMenuActivity.2
                AnonymousClass2() {
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    GMScreenGlobalInfo.setmSendEmailFinished(true);
                    GsMainMenuActivity.this.mDialog.dismiss();
                    GsMainMenuActivity.this.bExistApp = true;
                    GsMainMenuActivity.this.finish();
                }
            });
            cancelRelative.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsMainMenuActivity.3
                AnonymousClass3() {
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    GsMainMenuActivity.this.mDialog.dismiss();
                }
            });
            this.mDialog = new Dialog(this, R.style.dialog);
            this.mDialog.setContentView(layout);
            if (this.mDialog.isShowing()) {
                return true;
            }
            this.mDialog.show();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /* renamed from: mktvsmart.screen.GsMainMenuActivity$1 */
    class AnonymousClass1 implements View.OnClickListener {
        AnonymousClass1() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            GMScreenGlobalInfo.setmSendEmailFinished(true);
            GsMainMenuActivity.this.mDialog.dismiss();
            Intent intent = new Intent();
            intent.setClass(GsMainMenuActivity.this, GsLoginListActivity.class);
            GsMainMenuActivity.this.startActivity(intent);
            GsMainMenuActivity.this.finish();
        }
    }

    /* renamed from: mktvsmart.screen.GsMainMenuActivity$2 */
    class AnonymousClass2 implements View.OnClickListener {
        AnonymousClass2() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            GMScreenGlobalInfo.setmSendEmailFinished(true);
            GsMainMenuActivity.this.mDialog.dismiss();
            GsMainMenuActivity.this.bExistApp = true;
            GsMainMenuActivity.this.finish();
        }
    }

    /* renamed from: mktvsmart.screen.GsMainMenuActivity$3 */
    class AnonymousClass3 implements View.OnClickListener {
        AnonymousClass3() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            GsMainMenuActivity.this.mDialog.dismiss();
        }
    }

    private void setBackgroundMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(25, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsMainMenuActivity.4
            AnonymousClass4() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) throws IOException {
                byte[] socketRecvBytes = msg.getData().getByteArray("ReceivedData");
                int dataLength = msg.arg1;
                if (Environment.getExternalStorageState().equals("mounted")) {
                    try {
                        File sdFile = Environment.getExternalStorageDirectory();
                        String appFolderPath = String.valueOf(sdFile.getAbsolutePath()) + File.separator + GsMainMenuActivity.this.getString(R.string.app_name);
                        File debugFilePath = new File(appFolderPath);
                        if (!debugFilePath.exists()) {
                            debugFilePath.mkdir();
                        }
                        if (GlobalConstantValue.SEND_RS232_FILENAME != 0) {
                            File debugDataFile = new File(debugFilePath, GlobalConstantValue.SEND_RS232_FILENAME);
                            if (!debugDataFile.exists()) {
                                debugDataFile.createNewFile();
                            }
                            FileOutputStream out = new FileOutputStream(debugDataFile, true);
                            out.write(socketRecvBytes, 0, dataLength);
                            out.flush();
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(12, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsMainMenuActivity.5
            AnonymousClass5() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    DataParser parser = ParserFactory.getParser();
                    try {
                        InputStream istream = new ByteArrayInputStream(recvData, 0, msg.arg1);
                        List<?> list = parser.parse(istream, 10);
                        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                            case 30:
                            case 31:
                            case 32:
                            case 71:
                            case 72:
                            case 74:
                                GMScreenGlobalInfo.favGroups.clear();
                                GMScreenGlobalInfo.favGroups.addAll(list);
                                break;
                            default:
                                for (int loop = 0; loop < list.size(); loop++) {
                                    DataConvertFavorModel model = (DataConvertFavorModel) list.get(loop);
                                    model.SetFavorIndex(loop);
                                    if (!GMScreenGlobalInfo.favType.get(loop).equals(model.GetFavorName())) {
                                        GMScreenGlobalInfo.favType.remove(loop);
                                        GMScreenGlobalInfo.favType.add(loop, model.GetFavorName());
                                    }
                                }
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(2011, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsMainMenuActivity.6
            AnonymousClass6() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                ActivityManager am = (ActivityManager) GsMainMenuActivity.this.getSystemService("activity");
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                if (!cn.getClassName().equalsIgnoreCase(LivePlayActivity.class.getName()) && !cn.getClassName().equalsIgnoreCase(GChatActivity.class.getName())) {
                    Intent intent = new Intent(GsMainMenuActivity.this.getApplicationContext(), (Class<?>) SoftKeyboardActivity.class);
                    GsMainMenuActivity.this.startActivity(intent);
                }
            }
        });
        this.msgProc.setOnMessageProcess(2010, this, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.GsMainMenuActivity.7
            AnonymousClass7() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) {
                try {
                    CreateSocket cSocket = new CreateSocket(null, 0);
                    Socket tcpSocket = cSocket.GetSocket();
                    GsSendSocket.sendOnlyCommandSocketToStb(tcpSocket, 25);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* renamed from: mktvsmart.screen.GsMainMenuActivity$4 */
    class AnonymousClass4 implements MessageProcessor.PerformOnBackground {
        AnonymousClass4() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) throws IOException {
            byte[] socketRecvBytes = msg.getData().getByteArray("ReceivedData");
            int dataLength = msg.arg1;
            if (Environment.getExternalStorageState().equals("mounted")) {
                try {
                    File sdFile = Environment.getExternalStorageDirectory();
                    String appFolderPath = String.valueOf(sdFile.getAbsolutePath()) + File.separator + GsMainMenuActivity.this.getString(R.string.app_name);
                    File debugFilePath = new File(appFolderPath);
                    if (!debugFilePath.exists()) {
                        debugFilePath.mkdir();
                    }
                    if (GlobalConstantValue.SEND_RS232_FILENAME != 0) {
                        File debugDataFile = new File(debugFilePath, GlobalConstantValue.SEND_RS232_FILENAME);
                        if (!debugDataFile.exists()) {
                            debugDataFile.createNewFile();
                        }
                        FileOutputStream out = new FileOutputStream(debugDataFile, true);
                        out.write(socketRecvBytes, 0, dataLength);
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsMainMenuActivity$5 */
    class AnonymousClass5 implements MessageProcessor.PerformOnBackground {
        AnonymousClass5() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) {
            if (msg.arg1 > 0) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                DataParser parser = ParserFactory.getParser();
                try {
                    InputStream istream = new ByteArrayInputStream(recvData, 0, msg.arg1);
                    List<?> list = parser.parse(istream, 10);
                    switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                        case 30:
                        case 31:
                        case 32:
                        case 71:
                        case 72:
                        case 74:
                            GMScreenGlobalInfo.favGroups.clear();
                            GMScreenGlobalInfo.favGroups.addAll(list);
                            break;
                        default:
                            for (int loop = 0; loop < list.size(); loop++) {
                                DataConvertFavorModel model = (DataConvertFavorModel) list.get(loop);
                                model.SetFavorIndex(loop);
                                if (!GMScreenGlobalInfo.favType.get(loop).equals(model.GetFavorName())) {
                                    GMScreenGlobalInfo.favType.remove(loop);
                                    GMScreenGlobalInfo.favType.add(loop, model.GetFavorName());
                                }
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsMainMenuActivity$6 */
    class AnonymousClass6 implements MessageProcessor.PerformOnForeground {
        AnonymousClass6() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            ActivityManager am = (ActivityManager) GsMainMenuActivity.this.getSystemService("activity");
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if (!cn.getClassName().equalsIgnoreCase(LivePlayActivity.class.getName()) && !cn.getClassName().equalsIgnoreCase(GChatActivity.class.getName())) {
                Intent intent = new Intent(GsMainMenuActivity.this.getApplicationContext(), (Class<?>) SoftKeyboardActivity.class);
                GsMainMenuActivity.this.startActivity(intent);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsMainMenuActivity$7 */
    class AnonymousClass7 implements MessageProcessor.PerformOnBackground {
        AnonymousClass7() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) {
            try {
                CreateSocket cSocket = new CreateSocket(null, 0);
                Socket tcpSocket = cSocket.GetSocket();
                GsSendSocket.sendOnlyCommandSocketToStb(tcpSocket, 25);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // android.app.ActivityGroup, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws IOException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        setBackgroundMessageProcess();
        this.leftSliderLayout = (LeftSliderLayout) findViewById(R.id.main_slider_layout);
        this.mainContent = (ViewGroup) this.leftSliderLayout.findViewById(R.id.main_slider_main);
        Intent i = new Intent(this, (Class<?>) GsMainTabHostActivity.class);
        View v = getLocalActivityManager().startActivity(GsMainTabHostActivity.class.getName(), i).getDecorView();
        this.mainContent.removeAllViews();
        this.mainContent.addView(v);
        try {
            CreateSocket cSocket = new CreateSocket(null, 0);
            Socket tcpSocket = cSocket.GetSocket();
            tcpSocket.setSoTimeout(GamesStatusCodes.STATUS_MILESTONE_CLAIMED_PREVIOUSLY);
            InputStream inStream = tcpSocket.getInputStream();
            this.recvThread = new SocketReceiveThread(inStream);
            this.recvThread.start();
            this.mUserStatisticTask = new StatisticsTask(getApplicationContext(), GMScreenGlobalInfo.getCurStbInfo().getStb_sn_disp());
            this.mUserStatisticTask.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.ActivityGroup, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        CreateSocket cs = new CreateSocket(null, 0);
        cs.DestroySocket();
        if (this.recvThread != null) {
            this.recvThread.interrupt();
        }
        if (this.mUserStatisticTask != null) {
            this.mUserStatisticTask.interrupt();
        }
        if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
            ServiceUtil.stopMultiScreenControlService(this);
        }
        if (this.bExistApp) {
            Process.killProcess(Process.myPid());
        }
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return GsMainMenuActivity.this.sliderOption.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return GsMainMenuActivity.this.sliderOption[position];
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View convertView2 = this.inflater.inflate(R.layout.slider_menu, (ViewGroup) null, false);
            ImageView img = (ImageView) convertView2.findViewById(R.id.icon);
            TextView tv2 = (TextView) convertView2.findViewById(R.id.timer_option);
            tv2.setText(GsMainMenuActivity.this.sliderOption[position]);
            img.setImageResource(GsMainMenuActivity.this.items_img[position]);
            if (position == GsMainMenuActivity.this.currentPosition) {
                convertView2.setBackgroundResource(R.drawable.slider_menu_select);
                tv2.setTextColor(-1);
            }
            return convertView2;
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("GsMainMenuActivity", "onConfigurationChanged");
    }
}
