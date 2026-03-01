package mktvsmart.screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import mktvsmart.screen.CommonCofirmDialog;
import mktvsmart.screen.channel.ChannelData;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertTimeModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.view.DatePicker;
import mktvsmart.screen.view.IncludeIndexListViewAdapter;
import mktvsmart.screen.view.ListviewAdapter;
import mktvsmart.screen.view.Switch;
import mktvsmart.screen.view.TimePicker;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class SubTimerAddTimerActivity extends Activity {
    private int TimerStopType;
    private Button cancelBtn;
    private ArrayList<String> channel;
    private RelativeLayout channelLayout;
    private RelativeLayout dateLayout;
    private DataConvertTimeModel event;
    private String mode;
    private MessageProcessor msgProc;
    private DataParser parser;
    private TextView recordChannel;
    private TextView recordDate;
    private Switch recordMode;
    private TextView recordStart;
    private TextView recordStop;
    private TextView recordTitle;
    private RelativeLayout repeatLayout;
    private TextView repeatMode;
    private Button saveBtn;
    private Switch standbyMode;
    private RelativeLayout startLayout;
    private TextView stbTime;
    private RelativeLayout stopLayout;
    private Socket tcpSocket;
    private ArrayList<String> repeatStr = new ArrayList<String>() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.1
        {
            add("1X");
            add("Daily");
            add("Weekly");
            add("Weekdays");
            add("Weekends");
        }
    };
    private int statusValue = 0;
    private int channelPos = 0;
    private int VisibleItemCounts = 0;
    private int lastItem = 0;
    private List<String> mTimerIndexmodels = null;
    private Dialog dialog = null;
    List<DataConvertTimeModel> addEvent = new ArrayList();
    private MessageProcessor.PerformOnForeground subTimerAddTimerPof = new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.2
        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            switch (msg.arg2) {
                case 0:
                    Toast.makeText(SubTimerAddTimerActivity.this, R.string.operate_success, 0).show();
                    SubTimerAddTimerActivity.this.finish();
                    break;
                case 1:
                    Toast.makeText(SubTimerAddTimerActivity.this, R.string.operate_fail, 0).show();
                    SubTimerAddTimerActivity.this.finish();
                    break;
                case 15:
                    if (msg.arg1 > 0) {
                        Bundle data = msg.getData();
                        byte[] recv_data = data.getByteArray("ReceivedData");
                        InputStream istream = new ByteArrayInputStream(recv_data, 0, msg.arg1);
                        try {
                            SubTimerAddTimerActivity.this.mTimerIndexmodels = SubTimerAddTimerActivity.this.parser.parse(istream, 15);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        CommonCofirmDialog showDownDialog = new CommonCofirmDialog(SubTimerAddTimerActivity.this);
                        showDownDialog.setmTitle(SubTimerAddTimerActivity.this.getResources().getString(R.string.warning_dialog));
                        showDownDialog.setmContent(SubTimerAddTimerActivity.this.getResources().getString(R.string.str_timer_repeat));
                        showDownDialog.setOnButtonClickListener(SubTimerAddTimerActivity.this.mDownDialogOnClickListener);
                        showDownDialog.show();
                        break;
                    }
                    break;
            }
        }
    };
    private CommonCofirmDialog.OnButtonClickListener mDownDialogOnClickListener = new CommonCofirmDialog.OnButtonClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.3
        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedConfirm() throws SocketException, UnsupportedEncodingException {
            try {
                SubTimerAddTimerActivity.this.addEvent.get(0).SetTimerIndex(Integer.parseInt((String) SubTimerAddTimerActivity.this.mTimerIndexmodels.get(0)));
                byte[] data_buff = SubTimerAddTimerActivity.this.parser.serialize(SubTimerAddTimerActivity.this.addEvent, GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE).getBytes("UTF-8");
                SubTimerAddTimerActivity.this.tcpSocket.setSoTimeout(3000);
                GsSendSocket.sendSocketToStb(data_buff, SubTimerAddTimerActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SubTimerAddTimerActivity.this.finish();
        }

        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedCancel() {
            SubTimerAddTimerActivity.this.finishActivity(0);
            SubTimerAddTimerActivity.this.onBackPressed();
        }
    };

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD, this, this.subTimerAddTimerPof);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_EDIT, this, this.subTimerAddTimerPof);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE, this, this.subTimerAddTimerPof);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.4
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(SubTimerAddTimerActivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent = new Intent();
                intent.setClass(SubTimerAddTimerActivity.this, GsLoginListActivity.class);
                SubTimerAddTimerActivity.this.startActivity(intent);
                SubTimerAddTimerActivity.this.finish();
            }
        });
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws SocketException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        findViews();
        setMessageProcess();
        this.mode = getIntent().getStringExtra("operatorSchema");
        this.parser = ParserFactory.getParser();
        try {
            CreateSocket cSocket = new CreateSocket("", 0);
            this.tcpSocket = cSocket.GetSocket();
            this.tcpSocket.setSoTimeout(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.channel = GetCurChannelList(ChannelData.getInstance().getChannelListByTvRadioType());
        if (this.mode.equals("edit")) {
            this.recordTitle.setText(getResources().getText(R.string.edit_timer));
            this.event = (DataConvertTimeModel) getIntent().getSerializableExtra("CurTimer");
            int totalNum = ChannelData.getInstance().getChannelListByTvRadioType().size();
            this.channelPos = 0;
            while (this.channelPos < totalNum && !ChannelData.getInstance().getChannelListByTvRadioType().get(this.channelPos).GetProgramId().equals(this.event.getProgramId())) {
                this.channelPos++;
            }
            if (this.channelPos == totalNum) {
                this.channelPos = 0;
            }
            this.recordChannel.setText(this.event.GetTimeProgramName());
            this.repeatMode.setText(this.repeatStr.get(this.event.GetTimerRepeat()));
            this.recordDate.setText(new StringBuilder(String.valueOf(this.event.GetTimeMonth())).toString().concat(ServiceReference.DELIMITER).concat(new StringBuilder(String.valueOf(this.event.GetTimeDay())).toString()));
            this.recordStart.setText(new StringBuilder(String.valueOf(this.event.GetStartHour())).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(this.event.GetStartMin()))));
            if (this.event.GetEndHour() >= 24) {
                int endHpur = this.event.GetEndHour() % 24;
                this.recordStop.setText(new StringBuilder(String.valueOf(endHpur)).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(this.event.GetEndMin()))).concat("+ 1D"));
            } else {
                this.recordStop.setText(new StringBuilder(String.valueOf(this.event.GetEndHour())).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(this.event.GetEndMin()))));
            }
            if ((this.event.GetTimerStatus() & 2) == 2) {
                this.recordMode.setChecked(true);
            } else {
                this.recordMode.setChecked(false);
            }
            if ((this.event.GetTimerStatus() & 8) == 8) {
                this.standbyMode.setChecked(true);
            } else {
                this.standbyMode.setChecked(false);
            }
        } else {
            this.channelPos = 0;
            for (DataConvertChannelModel model : ChannelData.getInstance().getChannelListByTvRadioType()) {
                if (model.getIsPlaying() == 1) {
                    break;
                } else {
                    this.channelPos++;
                }
            }
            if (this.channelPos == ChannelData.getInstance().getChannelListByTvRadioType().size()) {
                this.channelPos = 0;
            }
            this.recordTitle.setText(getResources().getText(R.string.add_timer));
            this.recordChannel.setText(ChannelData.getInstance().getChannelListByTvRadioType().get(this.channelPos).getProgramName());
            this.repeatMode.setText(this.repeatStr.get(0));
            this.standbyMode.setChecked(false);
            this.recordMode.setChecked(false);
            int timerPos = getIntent().getIntExtra("timerSize", 0);
            this.event = new DataConvertTimeModel();
            this.event.SetTimerIndex(timerPos);
            this.event.setProgramId(ChannelData.getInstance().getChannelListByTvRadioType().get(this.channelPos).GetProgramId());
            this.event.SetTimeProgramName(ChannelData.getInstance().getChannelListByTvRadioType().get(this.channelPos).getProgramName());
            this.event.SetTimeMonth(DataConvertTimeModel.stbMonth);
            this.event.SetTimeDay(DataConvertTimeModel.stbDay);
            this.stbTime.setText(new StringBuilder(String.valueOf(DataConvertTimeModel.stbHour)).toString().concat(":").concat(String.format("%1$,02d", Integer.valueOf(DataConvertTimeModel.stbMin))));
            this.event.SetStartHour(DataConvertTimeModel.stbHour);
            this.event.SetStartMin(DataConvertTimeModel.stbMin);
            this.event.SetEndHour(DataConvertTimeModel.stbHour);
            this.event.SetEndMin(DataConvertTimeModel.stbMin);
            this.event.SetTimerRepeat(0);
            this.event.SetTimerStatus(0);
            this.recordDate.setText(new StringBuilder(String.valueOf(DataConvertTimeModel.stbMonth)).toString().concat(ServiceReference.DELIMITER).concat(new StringBuilder(String.valueOf(DataConvertTimeModel.stbDay)).toString()));
            this.recordStart.setText(new StringBuilder(String.valueOf(DataConvertTimeModel.stbHour)).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(DataConvertTimeModel.stbMin))));
            this.recordStop.setText(new StringBuilder(String.valueOf(DataConvertTimeModel.stbHour)).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(DataConvertTimeModel.stbMin))));
        }
        setOnListens();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.msgProc.recycle();
    }

    private void findViews() {
        this.recordTitle = (TextView) findViewById(R.id.add_event_title);
        this.recordChannel = (TextView) findViewById(R.id.channel_selected);
        this.stbTime = (TextView) findViewById(R.id.stb_time);
        this.recordDate = (TextView) findViewById(R.id.timer_date);
        this.recordStart = (TextView) findViewById(R.id.timer_start);
        this.recordStop = (TextView) findViewById(R.id.timer_stop);
        this.repeatMode = (TextView) findViewById(R.id.repeat_mode);
        this.standbyMode = (Switch) findViewById(R.id.standby_switch);
        this.recordMode = (Switch) findViewById(R.id.record_switch);
        this.saveBtn = (Button) findViewById(R.id.add_save);
        this.cancelBtn = (Button) findViewById(R.id.add_cancel);
        this.channelLayout = (RelativeLayout) findViewById(R.id.channel_click);
        this.dateLayout = (RelativeLayout) findViewById(R.id.date_click);
        this.startLayout = (RelativeLayout) findViewById(R.id.start_click);
        this.stopLayout = (RelativeLayout) findViewById(R.id.stop_click);
        this.repeatLayout = (RelativeLayout) findViewById(R.id.repeat_click);
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 30:
            case 32:
            case 71:
            case 72:
            case 74:
                ((TextView) findViewById(R.id.power_action)).setText(R.string.str_power_off);
                break;
        }
    }

    private void setOnListens() {
        this.channelLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(SubTimerAddTimerActivity.this);
                View view = inflater.inflate(R.layout.spinner_dialog, (ViewGroup) null);
                final ListView listview = (ListView) view.findViewById(R.id.formcustomspinner_list);
                Button cancelButton = (Button) view.findViewById(R.id.spiner_dialog_cancel_btn);
                IncludeIndexListViewAdapter adapters = new IncludeIndexListViewAdapter(SubTimerAddTimerActivity.this, ChannelData.getInstance().getChannelListByTvRadioType(), true);
                adapters.setCurPos(SubTimerAddTimerActivity.this.channelPos);
                listview.setAdapter((ListAdapter) adapters);
                adapters.notifyDataSetChanged();
                if (SubTimerAddTimerActivity.this.channelPos > 5) {
                    listview.setSelection(SubTimerAddTimerActivity.this.channelPos - 3);
                } else {
                    listview.setSelection(0);
                }
                listview.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.5.1
                    @Override // android.widget.AbsListView.OnScrollListener
                    public void onScrollStateChanged(AbsListView view2, int scrollState) {
                        if (scrollState == 0) {
                            listview.setSelection((SubTimerAddTimerActivity.this.lastItem - SubTimerAddTimerActivity.this.VisibleItemCounts) + 1);
                        }
                    }

                    @Override // android.widget.AbsListView.OnScrollListener
                    public void onScroll(AbsListView view2, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        SubTimerAddTimerActivity.this.VisibleItemCounts = visibleItemCount;
                    }
                });
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.5.2
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                        SubTimerAddTimerActivity.this.channelPos = position;
                        SubTimerAddTimerActivity.this.recordChannel.setText((CharSequence) SubTimerAddTimerActivity.this.channel.get(position));
                        SubTimerAddTimerActivity.this.event.setProgramId(ChannelData.getInstance().getChannelListByTvRadioType().get(position).GetProgramId());
                        SubTimerAddTimerActivity.this.event.SetTimeProgramName(ChannelData.getInstance().getChannelListByTvRadioType().get(position).getProgramName());
                        if (SubTimerAddTimerActivity.this.dialog != null) {
                            SubTimerAddTimerActivity.this.dialog.dismiss();
                            SubTimerAddTimerActivity.this.dialog = null;
                        }
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.5.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        if (SubTimerAddTimerActivity.this.dialog != null && SubTimerAddTimerActivity.this.dialog.isShowing()) {
                            SubTimerAddTimerActivity.this.dialog.dismiss();
                        }
                    }
                });
                SubTimerAddTimerActivity.this.dialog = new Dialog(SubTimerAddTimerActivity.this, R.style.dialog);
                SubTimerAddTimerActivity.this.dialog.setContentView(view);
                SubTimerAddTimerActivity.this.dialog.setCanceledOnTouchOutside(true);
                SubTimerAddTimerActivity.this.dialog.show();
            }
        });
        this.repeatLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(SubTimerAddTimerActivity.this);
                View view = inflater.inflate(R.layout.spinner_dialog, (ViewGroup) null);
                ListView listview = (ListView) view.findViewById(R.id.formcustomspinner_list);
                Button cancelButton = (Button) view.findViewById(R.id.spiner_dialog_cancel_btn);
                ListviewAdapter adapters = new ListviewAdapter(SubTimerAddTimerActivity.this, SubTimerAddTimerActivity.this.repeatStr);
                adapters.setCurPos(SubTimerAddTimerActivity.this.event.GetTimerRepeat());
                listview.setAdapter((ListAdapter) adapters);
                adapters.notifyDataSetChanged();
                listview.setSelection(SubTimerAddTimerActivity.this.event.GetTimerRepeat());
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.6.1
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                        SubTimerAddTimerActivity.this.repeatMode.setText((CharSequence) SubTimerAddTimerActivity.this.repeatStr.get(position));
                        SubTimerAddTimerActivity.this.event.SetTimerRepeat(position);
                        if (SubTimerAddTimerActivity.this.dialog != null && SubTimerAddTimerActivity.this.dialog.isShowing()) {
                            SubTimerAddTimerActivity.this.dialog.dismiss();
                            SubTimerAddTimerActivity.this.dialog = null;
                        }
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.6.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        if (SubTimerAddTimerActivity.this.dialog != null && SubTimerAddTimerActivity.this.dialog.isShowing()) {
                            SubTimerAddTimerActivity.this.dialog.dismiss();
                        }
                    }
                });
                SubTimerAddTimerActivity.this.dialog = new Dialog(SubTimerAddTimerActivity.this, R.style.dialog);
                SubTimerAddTimerActivity.this.dialog.setContentView(view);
                SubTimerAddTimerActivity.this.dialog.setCanceledOnTouchOutside(true);
                SubTimerAddTimerActivity.this.dialog.show();
            }
        });
        this.standbyMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.7
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SubTimerAddTimerActivity.this.statusValue = SubTimerAddTimerActivity.this.event.GetTimerStatus() | 8;
                } else {
                    SubTimerAddTimerActivity.this.statusValue = SubTimerAddTimerActivity.this.event.GetTimerStatus() & KeyInfo.KEYCODE_V;
                }
                SubTimerAddTimerActivity.this.event.SetTimerStatus(SubTimerAddTimerActivity.this.statusValue);
            }
        });
        this.recordMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.8
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SubTimerAddTimerActivity.this.statusValue = SubTimerAddTimerActivity.this.event.GetTimerStatus() | 2;
                } else {
                    SubTimerAddTimerActivity.this.statusValue = SubTimerAddTimerActivity.this.event.GetTimerStatus() & 253;
                }
                SubTimerAddTimerActivity.this.event.SetTimerStatus(SubTimerAddTimerActivity.this.statusValue);
            }
        });
        this.dateLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SubTimerAddTimerActivity.this.selectDateDialog();
            }
        });
        this.startLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SubTimerAddTimerActivity.this.selectTimeDialog(0);
            }
        });
        this.stopLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SubTimerAddTimerActivity.this.selectTimeDialog(1);
            }
        });
        this.saveBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View v) throws UnsupportedEncodingException {
                int responseStyle;
                boolean bValid = SubTimerAddTimerActivity.this.checkTimerEventValidity(SubTimerAddTimerActivity.this.event);
                System.out.println("bValid = " + bValid);
                if (bValid) {
                    try {
                        if (SubTimerAddTimerActivity.this.mode.equals(ProductAction.ACTION_ADD)) {
                            responseStyle = GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD;
                        } else {
                            responseStyle = GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_EDIT;
                        }
                        SubTimerAddTimerActivity.this.addEvent.add(SubTimerAddTimerActivity.this.event);
                        byte[] sendData = SubTimerAddTimerActivity.this.parser.serialize(SubTimerAddTimerActivity.this.addEvent, responseStyle).getBytes("UTF-8");
                        GsSendSocket.sendSocketToStb(sendData, SubTimerAddTimerActivity.this.tcpSocket, 0, sendData.length, responseStyle);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                SubTimerAddTimerActivity.this.showDialog(0);
            }
        });
        this.cancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.13
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SubTimerAddTimerActivity.this.finishActivity(0);
                SubTimerAddTimerActivity.this.onBackPressed();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkTimerEventValidity(DataConvertTimeModel event) {
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(1);
        calendar.set(curYear, event.GetTimeMonth(), event.GetTimeDay(), event.GetStartHour(), event.GetStartMin());
        long start = calendar.getTimeInMillis();
        calendar.set(curYear, event.GetTimeMonth(), event.GetTimeDay(), event.GetEndHour(), event.GetEndMin());
        long end = calendar.getTimeInMillis();
        calendar.set(curYear, DataConvertTimeModel.stbMonth, DataConvertTimeModel.stbDay, DataConvertTimeModel.stbHour, DataConvertTimeModel.stbMin);
        long current = calendar.getTimeInMillis();
        if (start < end && start >= current) {
            return true;
        }
        return false;
    }

    @Override // android.app.Activity
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                return new AlertDialog.Builder(this).setTitle("Invalid Timer").setMessage("The end time is earlier than the start").setNegativeButton("Reset", new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.14
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
            default:
                return super.onCreateDialog(id);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectDateDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.set_date, (ViewGroup) null);
        Button dateSaveBtn = (Button) layout.findViewById(R.id.set_date_save_btn);
        Button dateCancelBtn = (Button) layout.findViewById(R.id.set_date_cancel_btn);
        DatePicker datepicker = (DatePicker) layout.findViewById(R.id.date_wheel);
        datepicker.setMonth(this.event.GetTimeMonth());
        datepicker.setDay(this.event.GetTimeDay());
        datepicker.setOnChangeListener(new DatePicker.OnChangeListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.15
            @Override // mktvsmart.screen.view.DatePicker.OnChangeListener
            public void onChange(int month, int day, int day_of_week) {
                SubTimerAddTimerActivity.this.event.SetTimeMonth(month);
                SubTimerAddTimerActivity.this.event.SetTimeDay(day);
            }
        });
        dateSaveBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.16
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SubTimerAddTimerActivity.this.recordDate.setText(new StringBuilder(String.valueOf(SubTimerAddTimerActivity.this.event.GetTimeMonth())).toString().concat(ServiceReference.DELIMITER).concat(new StringBuilder(String.valueOf(SubTimerAddTimerActivity.this.event.GetTimeDay())).toString()));
                SubTimerAddTimerActivity.this.dialog.dismiss();
            }
        });
        dateCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.17
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SubTimerAddTimerActivity.this.dialog.dismiss();
            }
        });
        this.dialog = new Dialog(this, R.style.dialog);
        this.dialog.setContentView(layout);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectTimeDialog(int type) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.set_time, (ViewGroup) null);
        Button timeSaveBtn = (Button) layout.findViewById(R.id.set_time_save_btn);
        Button timeCancelBtn = (Button) layout.findViewById(R.id.set_time_cancel_btn);
        TimePicker timepicker = (TimePicker) layout.findViewById(R.id.time_wheel);
        this.TimerStopType = type;
        switch (type) {
            case 0:
                timepicker.setHourOfDay(this.event.GetStartHour());
                timepicker.setMinute(this.event.GetStartMin());
                break;
            case 1:
                timepicker.setHourOfDay(this.event.GetEndHour());
                timepicker.setMinute(this.event.GetEndMin());
                break;
        }
        timepicker.setOnChangeListener(new TimePicker.OnChangeListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.18
            @Override // mktvsmart.screen.view.TimePicker.OnChangeListener
            public void onChange(int hour, int munite) {
                if (SubTimerAddTimerActivity.this.TimerStopType == 0) {
                    SubTimerAddTimerActivity.this.event.SetStartHour(hour);
                    SubTimerAddTimerActivity.this.event.SetStartMin(munite);
                } else {
                    SubTimerAddTimerActivity.this.event.SetEndHour(hour);
                    SubTimerAddTimerActivity.this.event.SetEndMin(munite);
                }
            }
        });
        timeSaveBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.19
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                switch (SubTimerAddTimerActivity.this.TimerStopType) {
                    case 0:
                        SubTimerAddTimerActivity.this.recordStart.setText(new StringBuilder(String.valueOf(SubTimerAddTimerActivity.this.event.GetStartHour())).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(SubTimerAddTimerActivity.this.event.GetStartMin()))));
                        break;
                    case 1:
                        SubTimerAddTimerActivity.this.recordStop.setText(new StringBuilder(String.valueOf(SubTimerAddTimerActivity.this.event.GetEndHour())).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(SubTimerAddTimerActivity.this.event.GetEndMin()))));
                        break;
                }
                SubTimerAddTimerActivity.this.dialog.dismiss();
            }
        });
        timeCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubTimerAddTimerActivity.20
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SubTimerAddTimerActivity.this.dialog.dismiss();
            }
        });
        this.dialog = new Dialog(this, R.style.dialog);
        this.dialog.setContentView(layout);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.show();
    }

    private ArrayList<String> GetCurChannelList(List<DataConvertChannelModel> list) {
        ArrayList<String> data = new ArrayList<>();
        for (DataConvertChannelModel model : list) {
            data.add(model.getProgramName());
        }
        return data;
    }
}
