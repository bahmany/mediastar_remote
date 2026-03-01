package mktvsmart.screen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mktvsmart.screen.CommonCofirmDialog;
import mktvsmart.screen.dataconvert.model.DataConvertTimeModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.view.ListviewAdapter;

/* loaded from: classes.dex */
public class GsEventDetailActivity extends Activity {
    private static final int CONTEXT_MENU_ITEM_RECORD = 0;
    private static final int CONTEXT_MENU_ITEM_VIEW = 1;
    public static final String EPG_TIMER_TYPE_ID = "EPG_TIMER_TYPE";
    private Button btnAddTimer;
    private Button btnReturn;
    private GsEPGEvent currentEvent;
    private DataConvertTimeModel event;
    private EditText eventDetail;
    private Intent intent;
    private MessageProcessor msgProc;
    private DataParser parser;
    private Socket tcpSocket;
    private TextView textEventDetailTime;
    private int current_event_language_index = 0;
    private int currentSelectTimerType = 0;
    private List<DataConvertTimeModel> mAddEvent = new ArrayList();
    private Dialog mDialog = null;
    private List<String> mTimerIndexmodels = null;
    private MessageProcessor.PerformOnForeground epgEventDetailEditEventTimerPof = new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsEventDetailActivity.1
        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            switch (msg.arg2) {
                case 0:
                    Toast.makeText(GsEventDetailActivity.this, R.string.operate_success, 0).show();
                    GsEventDetailActivity.this.intent = new Intent();
                    GsEventDetailActivity.this.intent.putExtra(GsEventDetailActivity.EPG_TIMER_TYPE_ID, GsEventDetailActivity.this.currentSelectTimerType);
                    GsEventDetailActivity.this.setResult(-1, GsEventDetailActivity.this.intent);
                    GsEventDetailActivity.this.currentSelectTimerType = 0;
                    break;
                case 1:
                    Toast.makeText(GsEventDetailActivity.this, R.string.operate_fail, 0).show();
                    GsEventDetailActivity.this.currentSelectTimerType = 0;
                    break;
                case 15:
                    if (msg.arg1 > 0) {
                        Bundle data = msg.getData();
                        byte[] recv_data = data.getByteArray("ReceivedData");
                        InputStream istream = new ByteArrayInputStream(recv_data, 0, msg.arg1);
                        try {
                            GsEventDetailActivity.this.mTimerIndexmodels = GsEventDetailActivity.this.parser.parse(istream, 15);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        CommonCofirmDialog showDownDialog = new CommonCofirmDialog(GsEventDetailActivity.this);
                        showDownDialog.setmTitle(GsEventDetailActivity.this.getResources().getString(R.string.warning_dialog));
                        showDownDialog.setmContent(GsEventDetailActivity.this.getResources().getString(R.string.str_timer_repeat));
                        showDownDialog.setOnButtonClickListener(GsEventDetailActivity.this.mWarningDialogOnClickListener);
                        showDownDialog.show();
                        break;
                    }
                    break;
            }
        }
    };
    private CommonCofirmDialog.OnButtonClickListener mWarningDialogOnClickListener = new CommonCofirmDialog.OnButtonClickListener() { // from class: mktvsmart.screen.GsEventDetailActivity.2
        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedConfirm() throws SocketException, UnsupportedEncodingException {
            try {
                ((DataConvertTimeModel) GsEventDetailActivity.this.mAddEvent.get(0)).SetTimerIndex(Integer.parseInt((String) GsEventDetailActivity.this.mTimerIndexmodels.get(0)));
                byte[] data_buff = GsEventDetailActivity.this.parser.serialize(GsEventDetailActivity.this.mAddEvent, GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE).getBytes("UTF-8");
                GsEventDetailActivity.this.tcpSocket.setSoTimeout(3000);
                GsSendSocket.sendSocketToStb(data_buff, GsEventDetailActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedCancel() {
            GsEventDetailActivity.this.currentSelectTimerType = 0;
        }
    };

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD, this, this.epgEventDetailEditEventTimerPof);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE, this, this.epgEventDetailEditEventTimerPof);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsEventDetailActivity.3
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsEventDetailActivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent = new Intent();
                intent.setClass(GsEventDetailActivity.this, GsLoginListActivity.class);
                GsEventDetailActivity.this.startActivity(intent);
                GsEventDetailActivity.this.finish();
            }
        });
        this.msgProc.setOnMessageProcess(2015, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsEventDetailActivity.4
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsEventDetailActivity.this, R.string.str_become_master, 1).show();
                GsEventDetailActivity.this.btnAddTimer.setEnabled(true);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<String> getContextMenuData() {
        ArrayList<String> data = new ArrayList<>();
        data.add(getString(R.string.timer_type_record));
        data.add(getString(R.string.timer_type_view));
        return data;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws SocketException, NumberFormatException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_event_detail_layout);
        this.eventDetail = (EditText) findViewById(R.id.textEvnetDetail);
        this.eventDetail.setFocusable(false);
        this.textEventDetailTime = (TextView) findViewById(R.id.event_detail_time);
        setMessageProcess();
        GsSession session = GsSession.getSession();
        this.currentEvent = (GsEPGEvent) session.get("EPG_PROGRAM_EVENT");
        this.parser = ParserFactory.getParser();
        try {
            CreateSocket cSocket = new CreateSocket("", 0);
            this.tcpSocket = cSocket.GetSocket();
            this.tcpSocket.setSoTimeout(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.eventDetail.setText(String.valueOf(this.currentEvent.getEventTitle()[this.current_event_language_index]) + "\n\n" + this.currentEvent.getEventSubTitle()[this.current_event_language_index] + "\n\n" + this.currentEvent.getEventDesc()[this.current_event_language_index]);
        int time_start = Integer.parseInt(this.currentEvent.getStartTime());
        int time_end = Integer.parseInt(this.currentEvent.getEndTime());
        String stringTime = String.format(Locale.ENGLISH, "%02d:%02d--%02d:%02d", Integer.valueOf(time_start / 100), Integer.valueOf(time_start % 100), Integer.valueOf(time_end / 100), Integer.valueOf(time_end % 100));
        this.textEventDetailTime.setText(stringTime);
        this.btnReturn = (Button) findViewById(R.id.btnEventDetailReturn);
        this.btnReturn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsEventDetailActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    GsEventDetailActivity.this.onBackPressed();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
        this.btnAddTimer = (Button) findViewById(R.id.btnEventEditTimer);
        this.btnAddTimer.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsEventDetailActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                final Dialog contextMenuDialog = new Dialog(GsEventDetailActivity.this, R.style.dialog);
                LayoutInflater inflater = LayoutInflater.from(GsEventDetailActivity.this);
                View contextMenuLayout = inflater.inflate(R.layout.epg_context_menu_dialog, (ViewGroup) null);
                ListView list = (ListView) contextMenuLayout.findViewById(R.id.epg_context_menu_list);
                Button cancelButton = (Button) contextMenuLayout.findViewById(R.id.epg_context_menu_cancel_btn);
                ListviewAdapter adapter = new ListviewAdapter(GsEventDetailActivity.this, GsEventDetailActivity.this.getContextMenuData());
                list.setAdapter((ListAdapter) adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.GsEventDetailActivity.6.1
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) throws NumberFormatException, UnsupportedEncodingException {
                        try {
                            int time_start2 = Integer.parseInt(GsEventDetailActivity.this.currentEvent.getStartTime());
                            int time_end2 = Integer.parseInt(GsEventDetailActivity.this.currentEvent.getEndTime());
                            GsEventDetailActivity.this.event = new DataConvertTimeModel();
                            GsEventDetailActivity.this.event.SetTimeProgramName(GsEventDetailActivity.this.currentEvent.getProgramName());
                            GsEventDetailActivity.this.event.setProgramId(GsEventDetailActivity.this.currentEvent.getProgramId());
                            GsEventDetailActivity.this.event.SetTimeMonth(GsEventDetailActivity.this.currentEvent.getEventMonth() + 1);
                            GsEventDetailActivity.this.event.SetTimeDay(GsEventDetailActivity.this.currentEvent.getEventDate());
                            GsEventDetailActivity.this.event.SetStartHour(time_start2 / 100);
                            GsEventDetailActivity.this.event.SetStartMin(time_start2 % 100);
                            GsEventDetailActivity.this.event.SetEndHour(time_end2 / 100);
                            GsEventDetailActivity.this.event.SetEndMin(time_end2 % 100);
                            switch (position) {
                                case 0:
                                    GsEventDetailActivity.this.currentSelectTimerType = 2;
                                    GsEventDetailActivity.this.event.SetTimerStatus(2);
                                    break;
                                case 1:
                                    GsEventDetailActivity.this.currentSelectTimerType = 1;
                                    GsEventDetailActivity.this.event.SetTimerStatus(0);
                                    break;
                            }
                            GsEventDetailActivity.this.event.SetTimerRepeat(0);
                            GsEventDetailActivity.this.mAddEvent.add(GsEventDetailActivity.this.event);
                            byte[] sendData = GsEventDetailActivity.this.parser.serialize(GsEventDetailActivity.this.mAddEvent, GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD).getBytes("UTF-8");
                            GsSendSocket.sendSocketToStb(sendData, GsEventDetailActivity.this.tcpSocket, 0, sendData.length, GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        if (contextMenuDialog != null && contextMenuDialog.isShowing()) {
                            contextMenuDialog.dismiss();
                        }
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsEventDetailActivity.6.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        if (contextMenuDialog != null && contextMenuDialog.isShowing()) {
                            contextMenuDialog.dismiss();
                        }
                    }
                });
                contextMenuDialog.setContentView(contextMenuLayout);
                contextMenuDialog.setCanceledOnTouchOutside(false);
                contextMenuDialog.show();
            }
        });
        if (GMScreenGlobalInfo.isClientTypeSlave()) {
            this.btnAddTimer.setEnabled(false);
        }
    }

    @Override // android.app.Activity
    protected void onResume() throws UnsupportedEncodingException {
        super.onResume();
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 11);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.msgProc.recycle();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
