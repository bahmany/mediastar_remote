package mktvsmart.screen.pvr2small;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.alitech.dvbtoip.DVBtoIP;
import com.voicetechnology.rtspclient.test.Sat2IP_Rtsp;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import mktvsmart.screen.CommonCofirmDialog;
import mktvsmart.screen.CommonErrorDialog;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.FindPlayerAndPlayChannel;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsLoginListActivity;
import mktvsmart.screen.GsSendSocket;
import mktvsmart.screen.R;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;
import mktvsmart.screen.vlc.LivePlayActivity;

/* loaded from: classes.dex */
public class GsPvr2SmallActivity extends Activity implements Sat2IP_Rtsp.EndOfFileListener {
    private static Sat2IP_Rtsp sRtsp;
    private Pvr2smallData mPvr2SmallData;
    private ListView mPvr2SmallMenu;
    private Handler mainHandler;
    private MessageProcessor msgProc;
    private Socket tcpSocket;
    private ADSProgressDialog waitDialog;
    private pvr_list_adapter mPvrListAdapter = null;
    private final int PLAY_TIMEOUT = 2;
    private Handler.Callback mMsgHandle = new Handler.Callback() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    GsPvr2SmallActivity.this.stopStream();
                    GsPvr2SmallActivity.this.promptDialog(R.string.fail, R.string.str_load_data_fail);
                    break;
            }
            return true;
        }
    };
    private MessageProcessor.PerformOnBackground post = new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.2
        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) throws UnsupportedEncodingException {
            int responseStyle = 9999;
            switch (msg.what) {
                case 2023:
                    GsPvr2SmallActivity.this.mPvr2SmallData.clearPvr2smallList();
                case 2020:
                case 2021:
                case 2022:
                    responseStyle = 27;
                    break;
            }
            GsSendSocket.sendOnlyCommandSocketToStb(GsPvr2SmallActivity.this.tcpSocket, responseStyle);
        }
    };
    Runnable timeOutRun = new Runnable() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.3
        @Override // java.lang.Runnable
        public void run() {
            GsPvr2SmallActivity.this.mainHandler.sendEmptyMessage(2);
        }
    };
    private FindPlayerAndPlayChannel.PlayByDesignatedPlayer mPlayByDesignatedPlayer = new FindPlayerAndPlayChannel.PlayByDesignatedPlayer() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.4
        @Override // mktvsmart.screen.FindPlayerAndPlayChannel.PlayByDesignatedPlayer
        public void designatedBuiltInPlayer(int position) {
            GMScreenGlobalInfo.playType = 1;
            Intent mediaIntent = new Intent("android.intent.action.VIEW");
            mediaIntent.setClass(GsPvr2SmallActivity.this, LivePlayActivity.class);
            mediaIntent.putExtra("position", position);
            mediaIntent.setFlags(268435456);
            GsPvr2SmallActivity.this.getApplication().startActivity(mediaIntent);
        }

        @Override // mktvsmart.screen.FindPlayerAndPlayChannel.PlayByDesignatedPlayer
        public void designatedExternalPlayer(int position, Intent intent) {
            GsPvr2SmallActivity.this.startPlayStream(position, intent);
        }

        @Override // mktvsmart.screen.FindPlayerAndPlayChannel.PlayByDesignatedPlayer
        public void playerNotExist() {
            CommonCofirmDialog showDownDialog = new CommonCofirmDialog(GsPvr2SmallActivity.this);
            showDownDialog.setmTitle(GsPvr2SmallActivity.this.getResources().getString(R.string.install_mx_package_dialog_title));
            showDownDialog.setmContent(GsPvr2SmallActivity.this.getResources().getString(R.string.install_mx_package_dialog_message));
            showDownDialog.setOnButtonClickListener(GsPvr2SmallActivity.this.mDownDialogOnClickListener);
            showDownDialog.show();
        }
    };
    private CommonCofirmDialog.OnButtonClickListener mDownDialogOnClickListener = new CommonCofirmDialog.OnButtonClickListener() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.5
        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedConfirm() {
            Uri uri = Uri.parse("market://details?id=com.mxtech.videoplayer.ad");
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            try {
                GsPvr2SmallActivity.this.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedCancel() {
        }
    };

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(27, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.6
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (GsPvr2SmallActivity.this.waitDialog.isShowing()) {
                    GsPvr2SmallActivity.this.waitDialog.dismiss();
                }
                if (msg.arg1 <= 0) {
                    if (GsPvr2SmallActivity.this.mPvrListAdapter != null) {
                        GsPvr2SmallActivity.this.mPvrListAdapter.notifyDataSetChanged();
                    }
                    if (msg.arg2 != 18) {
                        if (msg.arg2 != 19) {
                            if (msg.arg2 == 20) {
                                GsPvr2SmallActivity.this.promptDialog(R.string.not_ready_usb, R.string.not_ready_usb_content);
                                return;
                            } else {
                                GsPvr2SmallActivity.this.promptDialog(R.string.fail, R.string.got_pvr_list_fail);
                                return;
                            }
                        }
                        GsPvr2SmallActivity.this.promptDialog(R.string.not_connect_usb, R.string.not_connect_usb_content);
                        return;
                    }
                    GsPvr2SmallActivity.this.promptDialog(R.string.not_support_usb, R.string.not_support_usb_content);
                    return;
                }
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                GsPvr2SmallActivity.this.mPvr2SmallData.initPvr2SmallList(recvData);
                GsPvr2SmallActivity.this.mPvrListAdapter = GsPvr2SmallActivity.this.new pvr_list_adapter(GsPvr2SmallActivity.this);
                GsPvr2SmallActivity.this.mPvr2SmallMenu.setAdapter((ListAdapter) GsPvr2SmallActivity.this.mPvrListAdapter);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.7
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsPvr2SmallActivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent = new Intent();
                intent.setClass(GsPvr2SmallActivity.this, GsLoginListActivity.class);
                GsPvr2SmallActivity.this.startActivity(intent);
                GsPvr2SmallActivity.this.finish();
            }
        });
        this.msgProc.setOnMessageProcess(2020, this.post);
        this.msgProc.setOnMessageProcess(2021, this.post);
        this.msgProc.setOnMessageProcess(2022, this.post);
        this.msgProc.setOnMessageProcess(2023, this.post);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String netAddress = intent.getStringExtra("Address");
        int netPort = intent.getIntExtra("Port", GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
        try {
            CreateSocket cSocket = new CreateSocket(netAddress, netPort);
            this.tcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.pvr_to_small_layout);
        this.mPvr2SmallMenu = (ListView) findViewById(R.id.pvr_list);
        Button backButton = (Button) findViewById(R.id.back_pvr);
        Button refreshButton = (Button) findViewById(R.id.refresh_button);
        this.mPvr2SmallData = Pvr2smallData.getInstance();
        setMessageProcess();
        backButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsPvr2SmallActivity.this.onBackPressed();
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GsPvr2SmallActivity.this.tcpSocket, 27);
                GsPvr2SmallActivity.this.displayDialog(R.string.loading_data, R.string.please_wait);
            }
        });
        this.mPvr2SmallMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.10
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GsPvr2SmallActivity.this.play(position);
            }
        });
        this.mainHandler = new Handler(this.mMsgHandle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopStream() {
        if (sRtsp != null) {
            sRtsp.teardown();
            sRtsp = null;
            DVBtoIP.destroyResourceForPlayer();
            GMScreenGlobalInfo.playType = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlayStream(final int position, final Intent intent) {
        displayDialog(R.string.str_open_channel, R.string.please_wait);
        new Thread(new Runnable() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.11
            @Override // java.lang.Runnable
            public void run() {
                GsPvr2SmallActivity.sRtsp = new Sat2IP_Rtsp();
                GsPvr2SmallActivity.sRtsp.set_eof_listener(GsPvr2SmallActivity.this);
                if (GsPvr2SmallActivity.this.mPvr2SmallData.getPvr2smallList().get(position).getmPvrCrypto() == 1) {
                    DVBtoIP.getChannelUserKey(GsPvr2SmallActivity.this.tcpSocket.getInetAddress().toString().substring(1));
                }
                String base = Pvr2smallData.getInstance().getPlayUrlBase(position, GsPvr2SmallActivity.this.tcpSocket.getInetAddress().toString());
                String query = Pvr2smallData.getInstance().getPlayUrlQuery();
                GMScreenGlobalInfo.playType = 1;
                boolean isSetupOk = GsPvr2SmallActivity.sRtsp.setup_blocked(base, query);
                if (!isSetupOk) {
                    if (GsPvr2SmallActivity.this.waitDialog.isShowing()) {
                        GsPvr2SmallActivity.this.waitDialog.dismiss();
                    }
                    CommonErrorDialog errorDialog = new CommonErrorDialog(GsPvr2SmallActivity.this.getParent());
                    errorDialog.setmContent(GsPvr2SmallActivity.this.getResources().getString(R.string.error_message_server_unavailable));
                    errorDialog.show();
                    GsPvr2SmallActivity.sRtsp = null;
                    return;
                }
                DVBtoIP.initResourceForPlayer(GsPvr2SmallActivity.sRtsp.get_rtp_port(), FindPlayerAndPlayChannel.getRtspPipeFilePath(GsPvr2SmallActivity.this), 1, GMScreenGlobalInfo.getKeyWay());
                GsPvr2SmallActivity gsPvr2SmallActivity = GsPvr2SmallActivity.this;
                final Intent intent2 = intent;
                gsPvr2SmallActivity.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.11.1
                    @Override // java.lang.Runnable
                    public void run() throws Resources.NotFoundException {
                        if (GsPvr2SmallActivity.this.waitDialog.isShowing()) {
                            GsPvr2SmallActivity.this.waitDialog.dismiss();
                        }
                        try {
                            GsPvr2SmallActivity.this.startActivity(intent2);
                        } catch (ActivityNotFoundException e) {
                            System.out.println("MX Player activity not found");
                            GsPvr2SmallActivity.this.stopStream();
                        }
                        Toast toast = Toast.makeText(GsPvr2SmallActivity.this, R.string.waiting_to_play_stream_hint, 1);
                        toast.show();
                    }
                });
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void play(int position) {
        FindPlayerAndPlayChannel findPlayerAndPlayerChannel = new FindPlayerAndPlayChannel(this);
        findPlayerAndPlayerChannel.implementPlayByDesignatedPlayer(this.mPlayByDesignatedPlayer);
        findPlayerAndPlayerChannel.selectPlayer(position);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        stopStream();
    }

    @Override // android.app.Activity
    protected void onResume() throws UnsupportedEncodingException {
        super.onResume();
        stopStream();
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 27);
        displayDialog(R.string.loading_data, R.string.please_wait);
    }

    private class pvr_list_adapter extends BaseAdapter {
        LayoutInflater inflater;

        public pvr_list_adapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            if (GsPvr2SmallActivity.this.mPvr2SmallData.getPvr2smallList() != null) {
                return GsPvr2SmallActivity.this.mPvr2SmallData.getPvr2smallList().size();
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return GsPvr2SmallActivity.this.mPvr2SmallData.getPvr2smallList().get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) throws NumberFormatException {
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.pvr_item_layout, parent, false);
            }
            TextView pvr_index = (TextView) convertView.findViewById(R.id.pvr_index);
            TextView pvr_name = (TextView) convertView.findViewById(R.id.pvr_name);
            TextView pvr_time = (TextView) convertView.findViewById(R.id.pvr_time);
            TextView pvr_size = (TextView) convertView.findViewById(R.id.pvr_size);
            pvr_index.setText(new StringBuilder(String.valueOf(position + 1)).toString());
            if (GsPvr2SmallActivity.this.mPvr2SmallData.getPvr2smallList().get(position).getmPvrCrypto() == 1) {
                pvr_name.setText("$" + GsPvr2SmallActivity.this.mPvr2SmallData.getPvr2smallList().get(position).getProgramName());
            } else {
                pvr_name.setText(GsPvr2SmallActivity.this.mPvr2SmallData.getPvr2smallList().get(position).getProgramName());
            }
            int totalSecond = Integer.parseInt(GsPvr2SmallActivity.this.mPvr2SmallData.getPvr2smallList().get(position).getmPvrDuration());
            int hour = totalSecond / 3600;
            int minute = (totalSecond % 3600) / 60;
            int second = totalSecond % 60;
            String duration = String.format("%02d:%02d:%02d", Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second));
            pvr_time.setText(GsPvr2SmallActivity.this.mPvr2SmallData.getPvr2smallList().get(position).getmPvrTime());
            pvr_size.setText(duration);
            return convertView;
        }
    }

    @Override // com.voicetechnology.rtspclient.test.Sat2IP_Rtsp.EndOfFileListener
    public void onEndOfFile() {
        stopStream();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void promptDialog(int notSupportUsb, int notSupportUsbContent) {
        final Dialog promptDialog = new Dialog(this, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.prompt_dialog, (ViewGroup) null);
        TextView title = (TextView) layout.findViewById(R.id.prompt_dialog_title);
        TextView content = (TextView) layout.findViewById(R.id.prompt_dialog_txt);
        Button okBtn = (Button) layout.findViewById(R.id.prompt_dialog_ok_btn);
        title.setText(notSupportUsb);
        content.setText(notSupportUsbContent);
        okBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.pvr2small.GsPvr2SmallActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                promptDialog.dismiss();
            }
        });
        promptDialog.setContentView(layout);
        promptDialog.setCanceledOnTouchOutside(true);
        promptDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayDialog(int title, int message) {
        if (this.waitDialog != null && this.waitDialog.isShowing()) {
            this.waitDialog.dismiss();
        }
        this.waitDialog = DialogBuilder.showProgressDialog((Activity) this, title, message, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), this.timeOutRun);
    }
}
