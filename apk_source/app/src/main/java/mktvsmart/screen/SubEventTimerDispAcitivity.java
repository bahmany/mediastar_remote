package mktvsmart.screen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.CommonCofirmDialog;
import mktvsmart.screen.channel.ChannelData;
import mktvsmart.screen.dataconvert.model.DataConvertTimeModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;
import mktvsmart.screen.view.ListviewAdapter;
import org.slf4j.Marker;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class SubEventTimerDispAcitivity extends Activity implements OnTabActivityResultListener {
    private static final int CONTEXT_MENU_ITEM_DELETE = 0;
    private static final int CONTEXT_MENU_ITEM_EDIT = 1;
    private Timer Systime;
    private TimerTask Systimer;
    private Button addTimer;
    private Dialog contextMenuDialog;
    private ListView eventTimer;
    InputStream in;
    private Intent intent;
    private MessageProcessor msgProc;
    private DataParser parser;
    private int position;
    Socket tcpSocket;
    private List<DataConvertTimeModel> timeModels;
    private Button timerReturn;
    private Button timerfold;
    private ADSProgressDialog waitDialog;
    private list_single_adapter adapter = null;
    private boolean enable_update_timer = true;
    private boolean isTimerUnfold = false;
    private int firstStbtime = 0;
    private int firstTimerRecv = 0;
    private int maxTimerPos = 0;
    private int maxTimerLen = 0;
    private int progressLen = 0;
    private CommonCofirmDialog.OnButtonClickListener mEventDeleteDialogOnClickListener = new CommonCofirmDialog.OnButtonClickListener() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.1
        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedConfirm() throws UnsupportedEncodingException {
            int index = 0;
            try {
                List<DataConvertTimeModel> eventList = new ArrayList<>();
                Iterator it = SubEventTimerDispAcitivity.this.timeModels.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    DataConvertTimeModel model = (DataConvertTimeModel) it.next();
                    if (index == SubEventTimerDispAcitivity.this.position) {
                        eventList.add(model);
                        break;
                    }
                    index++;
                }
                SubEventTimerDispAcitivity.this.timeModels.remove(SubEventTimerDispAcitivity.this.position);
                String data = SubEventTimerDispAcitivity.this.parser.serialize(eventList, GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_DELETE);
                byte[] data_buff = data.getBytes("UTF-8");
                GsSendSocket.sendSocketToStb(data_buff, SubEventTimerDispAcitivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_DELETE);
                SubEventTimerDispAcitivity.this.adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedCancel() {
        }
    };

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(1, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.2
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recv_data = data.getByteArray("ReceivedData");
                    InputStream istream = new ByteArrayInputStream(recv_data, 0, msg.arg1);
                    try {
                        SubEventTimerDispAcitivity.this.timeModels = SubEventTimerDispAcitivity.this.parser.parse(istream, 1);
                        SubEventTimerDispAcitivity.this.firstTimerRecv++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (SubEventTimerDispAcitivity.this.isTimerUnfold) {
                        for (DataConvertTimeModel model : SubEventTimerDispAcitivity.this.timeModels) {
                            model.setShowDetail(false);
                        }
                        SubEventTimerDispAcitivity.this.timerfold.setBackgroundResource(R.drawable.open_timer);
                        SubEventTimerDispAcitivity.this.isTimerUnfold = false;
                    }
                    SubEventTimerDispAcitivity.this.notifyTimerChanged();
                }
            }
        });
        this.msgProc.setOnMessageProcess(11, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.3
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recv_data = data.getByteArray("ReceivedData");
                    InputStream istream = new ByteArrayInputStream(recv_data, 0, msg.arg1);
                    try {
                        SubEventTimerDispAcitivity.this.parser.parse(istream, 9);
                        SubEventTimerDispAcitivity.this.firstStbtime++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SubEventTimerDispAcitivity.this.notifyTimerChanged();
                }
            }
        });
        this.msgProc.setOnMessageProcess(2005, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.4
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(SubEventTimerDispAcitivity.this.tcpSocket, 1);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.5
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(SubEventTimerDispAcitivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent = new Intent();
                intent.setClass(SubEventTimerDispAcitivity.this, GsLoginListActivity.class);
                SubEventTimerDispAcitivity.this.startActivity(intent);
                SubEventTimerDispAcitivity.this.finish();
            }
        });
        this.msgProc.setOnMessageProcess(2015, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.6
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(SubEventTimerDispAcitivity.this, R.string.str_become_master, 1).show();
                SubEventTimerDispAcitivity.this.addTimer.setEnabled(true);
                SubEventTimerDispAcitivity.this.eventTimer.setLongClickable(true);
            }
        });
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws SocketException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_timer_layout);
        this.waitDialog = DialogBuilder.showProgressDialog(getParent(), R.string.loading_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut());
        setMessageProcess();
        this.eventTimer = (ListView) findViewById(R.id.event_list);
        this.addTimer = (Button) findViewById(R.id.add_timer);
        this.timerfold = (Button) findViewById(R.id.unfold_detail);
        this.timerReturn = (Button) findViewById(R.id.back_timer);
        int spaceLen = (int) ((2.0f * getResources().getDisplayMetrics().density) + 0.5f + (BitmapFactory.decodeResource(getResources(), R.drawable.timer_corner_left).getWidth() * 2));
        this.progressLen = BitmapFactory.decodeResource(getResources(), R.drawable.timer_progress_back).getWidth() - spaceLen;
        if (ChannelData.getInstance().getChannelListByTvRadioType() == null || ChannelData.getInstance().getChannelListByTvRadioType().size() == 0) {
            this.addTimer.setVisibility(4);
            this.timerfold.setVisibility(4);
        } else {
            this.addTimer.setVisibility(0);
            this.timerfold.setVisibility(0);
        }
        try {
            CreateSocket cSocket = new CreateSocket("", 0);
            this.tcpSocket = cSocket.GetSocket();
            this.tcpSocket.setSoTimeout(3000);
            this.in = this.tcpSocket.getInputStream();
            this.parser = ParserFactory.getParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.eventTimer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.7
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater inflater = LayoutInflater.from(SubEventTimerDispAcitivity.this.getParent());
                View contextMenuLayout = inflater.inflate(R.layout.event_timer_context_menu_dialog, (ViewGroup) null);
                ListView list = (ListView) contextMenuLayout.findViewById(R.id.event_timer_context_menu_list);
                Button cancelButton = (Button) contextMenuLayout.findViewById(R.id.event_timer_context_menu_cancel_btn);
                ListviewAdapter adapter = new ListviewAdapter(SubEventTimerDispAcitivity.this.getParent(), SubEventTimerDispAcitivity.this.getContextMenuData());
                list.setAdapter((ListAdapter) adapter);
                SubEventTimerDispAcitivity.this.position = position;
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.7.1
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> parent2, View view2, int position2, long id2) {
                        switch (position2) {
                            case 0:
                                CommonCofirmDialog showDownDialog = new CommonCofirmDialog(SubEventTimerDispAcitivity.this.getParent());
                                showDownDialog.setmTitle(SubEventTimerDispAcitivity.this.getResources().getString(R.string.event_delete));
                                showDownDialog.setmContent(SubEventTimerDispAcitivity.this.getResources().getString(R.string.sure_to_delete_event_timer));
                                showDownDialog.setOnButtonClickListener(SubEventTimerDispAcitivity.this.mEventDeleteDialogOnClickListener);
                                showDownDialog.show();
                                break;
                            case 1:
                                ((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(SubEventTimerDispAcitivity.this.position)).SetTimerIndex(SubEventTimerDispAcitivity.this.position);
                                SubEventTimerDispAcitivity.this.intent = new Intent();
                                SubEventTimerDispAcitivity.this.intent.putExtra("operatorSchema", "edit");
                                SubEventTimerDispAcitivity.this.intent.putExtra("CurTimer", (Serializable) SubEventTimerDispAcitivity.this.timeModels.get(SubEventTimerDispAcitivity.this.position));
                                SubEventTimerDispAcitivity.this.intent.setClass(SubEventTimerDispAcitivity.this, SubTimerAddTimerActivity.class);
                                SubEventTimerDispAcitivity.this.getParent().startActivityForResult(SubEventTimerDispAcitivity.this.intent, 0);
                                break;
                        }
                        if (SubEventTimerDispAcitivity.this.contextMenuDialog != null && SubEventTimerDispAcitivity.this.contextMenuDialog.isShowing()) {
                            SubEventTimerDispAcitivity.this.contextMenuDialog.dismiss();
                        }
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.7.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (SubEventTimerDispAcitivity.this.contextMenuDialog != null && SubEventTimerDispAcitivity.this.contextMenuDialog.isShowing()) {
                            SubEventTimerDispAcitivity.this.contextMenuDialog.dismiss();
                        }
                    }
                });
                SubEventTimerDispAcitivity.this.contextMenuDialog = new Dialog(SubEventTimerDispAcitivity.this.getParent(), R.style.dialog);
                SubEventTimerDispAcitivity.this.contextMenuDialog.setCanceledOnTouchOutside(false);
                SubEventTimerDispAcitivity.this.contextMenuDialog.setContentView(contextMenuLayout);
                SubEventTimerDispAcitivity.this.contextMenuDialog.show();
                return true;
            }
        });
        this.eventTimer.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.8
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubEventTimerDispAcitivity.this.adapter.openDetail(position);
            }
        });
        this.timerReturn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SubEventTimerDispAcitivity.this.finishActivity(0);
                SubEventTimerDispAcitivity.this.onBackPressed();
            }
        });
        this.timerfold.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (!SubEventTimerDispAcitivity.this.isTimerUnfold) {
                    for (DataConvertTimeModel model : SubEventTimerDispAcitivity.this.timeModels) {
                        model.setShowDetail(true);
                    }
                    SubEventTimerDispAcitivity.this.timerfold.setBackgroundResource(R.drawable.close_timer);
                    SubEventTimerDispAcitivity.this.isTimerUnfold = true;
                } else {
                    for (DataConvertTimeModel model2 : SubEventTimerDispAcitivity.this.timeModels) {
                        model2.setShowDetail(false);
                    }
                    SubEventTimerDispAcitivity.this.timerfold.setBackgroundResource(R.drawable.open_timer);
                    SubEventTimerDispAcitivity.this.isTimerUnfold = false;
                }
                SubEventTimerDispAcitivity.this.adapter.notifyDataSetChanged();
            }
        });
        this.addTimer.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SubEventTimerDispAcitivity.this.intent = new Intent();
                SubEventTimerDispAcitivity.this.intent.putExtra("timerSize", SubEventTimerDispAcitivity.this.timeModels.size());
                SubEventTimerDispAcitivity.this.intent.putExtra("operatorSchema", ProductAction.ACTION_ADD);
                SubEventTimerDispAcitivity.this.intent.setClass(SubEventTimerDispAcitivity.this, SubTimerAddTimerActivity.class);
                SubEventTimerDispAcitivity.this.getParent().startActivityForResult(SubEventTimerDispAcitivity.this.intent, 0);
            }
        });
        if (GMScreenGlobalInfo.isClientTypeSlave()) {
            this.addTimer.setEnabled(false);
            this.eventTimer.setLongClickable(false);
        }
    }

    @Override // mktvsmart.screen.OnTabActivityResultListener
    public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.Systimer.cancel();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        try {
            if (this.enable_update_timer || this.timeModels == null) {
                GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 1);
                this.enable_update_timer = false;
            }
            this.Systime = new Timer();
            this.Systimer = new TimerTask() { // from class: mktvsmart.screen.SubEventTimerDispAcitivity.12
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() throws UnsupportedEncodingException {
                    GsSendSocket.sendOnlyCommandSocketToStb(SubEventTimerDispAcitivity.this.tcpSocket, 11);
                }
            };
            this.Systime.schedule(this.Systimer, new Date(), 60000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.msgProc.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyTimerChanged() {
        if (this.firstTimerRecv >= 1 && this.firstStbtime >= 1) {
            for (DataConvertTimeModel model : this.timeModels) {
                Date startDate = new Date();
                Date endDate = new Date();
                Date endDate2 = new Date();
                Calendar c = Calendar.getInstance();
                int year = c.get(1);
                startDate.setYear(year);
                startDate.setMonth(DataConvertTimeModel.stbMonth - 1);
                startDate.setDate(DataConvertTimeModel.stbDay);
                startDate.setHours(DataConvertTimeModel.stbHour);
                startDate.setMinutes(DataConvertTimeModel.stbMin);
                endDate2.setYear(year);
                endDate2.setMonth(model.GetTimeMonth() - 1);
                endDate2.setDate(model.GetTimeDay());
                endDate2.setHours(model.GetEndHour());
                endDate2.setMinutes(model.GetEndMin());
                endDate.setMonth(model.GetTimeMonth() - 1);
                endDate.setDate(model.GetTimeDay());
                switch (model.GetTimerRepeat()) {
                    case 1:
                        if (GetTimerDayVer2(startDate, endDate2) == 1) {
                            endDate2.setMonth(DataConvertTimeModel.stbMonth - 1);
                            endDate2.setDate(DataConvertTimeModel.stbDay);
                            if (GetTimerMin(startDate, endDate2) > 0) {
                                endDate.setMonth(DataConvertTimeModel.stbMonth - 1);
                                endDate.setDate(DataConvertTimeModel.stbDay);
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 2:
                        if (GetTimerDayVer2(startDate, endDate2) == 7) {
                            endDate2.setMonth(DataConvertTimeModel.stbMonth - 1);
                            endDate2.setDate(DataConvertTimeModel.stbDay);
                            if (GetTimerMin(startDate, endDate2) > 0) {
                                endDate.setMonth(DataConvertTimeModel.stbMonth - 1);
                                endDate.setDate(DataConvertTimeModel.stbDay);
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                }
                endDate.setYear(year);
                endDate.setHours(model.GetStartHour());
                endDate.setMinutes(model.GetStartMin());
                int daylen = GetTimerDay(startDate, endDate);
                int hourlen = GetTimerHour(startDate, endDate);
                int minlen = GetTimerMin(startDate, endDate);
                model.SetTimerDayLen(daylen);
                model.SetTimerHourLen(hourlen);
                model.SetTimerMinLen(minlen);
            }
            if (!((this.firstTimerRecv >= 1) & (this.firstStbtime > 1))) {
                if (!((this.firstTimerRecv > 1) & (this.firstStbtime >= 1))) {
                    if (this.firstStbtime == 1 && this.firstTimerRecv == 1) {
                        this.adapter = new list_single_adapter(this);
                        this.eventTimer.setAdapter((ListAdapter) this.adapter);
                        if (this.waitDialog.isShowing()) {
                            this.waitDialog.dismiss();
                        }
                        this.maxTimerPos = 0;
                        if (this.timeModels != null && this.timeModels.size() != 0) {
                            System.out.println("Timer Num:" + this.timeModels.size());
                            for (int i = 1; i < this.timeModels.size() - 1; i++) {
                                this.maxTimerPos = GetMaxTimer(this.maxTimerPos, i);
                            }
                            this.maxTimerLen = GetTotalMinLen(this.maxTimerPos);
                            return;
                        }
                        return;
                    }
                    return;
                }
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    public int GetTotalMinLen(int position) {
        int totalMin;
        if (this.timeModels.get(position).GetTimerDayLen() >= 1) {
            totalMin = 1440;
        } else {
            totalMin = (this.timeModels.get(position).GetTimerHourLen() * 60) + this.timeModels.get(position).GetTimerMinLen();
        }
        if (totalMin < 0) {
            return 0;
        }
        return totalMin;
    }

    public int GetTimerDay(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(11, startDate.getHours());
        fromCalendar.set(12, startDate.getMinutes());
        fromCalendar.set(13, 0);
        fromCalendar.set(14, 0);
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(11, endDate.getHours());
        toCalendar.set(12, endDate.getMinutes());
        toCalendar.set(13, 0);
        toCalendar.set(14, 0);
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / 86400000);
    }

    public int GetTimerDayVer2(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(11, 0);
        fromCalendar.set(12, 0);
        fromCalendar.set(13, 0);
        fromCalendar.set(14, 0);
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(11, 0);
        toCalendar.set(12, 0);
        toCalendar.set(13, 0);
        toCalendar.set(14, 0);
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / 86400000);
    }

    public int GetTimerHour(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(11, startDate.getHours());
        fromCalendar.set(12, startDate.getMinutes());
        fromCalendar.set(13, 0);
        fromCalendar.set(14, 0);
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(11, endDate.getHours());
        toCalendar.set(12, endDate.getMinutes());
        toCalendar.set(13, 0);
        toCalendar.set(14, 0);
        int len = (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / 3600000);
        return len % 24;
    }

    public int GetTimerMin(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(11, startDate.getHours());
        fromCalendar.set(12, startDate.getMinutes());
        fromCalendar.set(13, 0);
        fromCalendar.set(14, 0);
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(11, endDate.getHours());
        toCalendar.set(12, endDate.getMinutes());
        toCalendar.set(13, 0);
        toCalendar.set(14, 0);
        int len = (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / 60000);
        return len % 60;
    }

    private int GetMaxTimer(int timera, int timerb) {
        if (this.timeModels.get(timera).GetTimeDay() <= this.timeModels.get(timerb).GetTimeDay()) {
            if (this.timeModels.get(timera).GetTimeDay() < this.timeModels.get(timerb).GetTimeDay()) {
                return timerb;
            }
            if (this.timeModels.get(timera).GetTimerHourLen() <= this.timeModels.get(timerb).GetTimerHourLen()) {
                return (this.timeModels.get(timera).GetTimerHourLen() >= this.timeModels.get(timerb).GetTimerHourLen() && this.timeModels.get(timera).GetTimerMinLen() > this.timeModels.get(timerb).GetTimerMinLen()) ? timera : timerb;
            }
            return timera;
        }
        return timera;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<String> getContextMenuData() {
        ArrayList<String> data = new ArrayList<>();
        data.add(getString(R.string.delete));
        data.add(getString(R.string.edit_str));
        return data;
    }

    private class list_single_adapter extends BaseAdapter {
        LayoutInflater inflater;

        public list_single_adapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            if (SubEventTimerDispAcitivity.this.timeModels != null) {
                return SubEventTimerDispAcitivity.this.timeModels.size();
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return SubEventTimerDispAcitivity.this.timeModels.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public void openDetail(int position) {
            int openTimerNum = 0;
            if (!((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetShowDetail()) {
                ((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).setShowDetail(true);
            } else {
                ((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).setShowDetail(false);
            }
            for (DataConvertTimeModel model : SubEventTimerDispAcitivity.this.timeModels) {
                if (model.GetShowDetail()) {
                    openTimerNum++;
                }
            }
            if (openTimerNum != 0) {
                if (openTimerNum == SubEventTimerDispAcitivity.this.timeModels.size()) {
                    SubEventTimerDispAcitivity.this.timerfold.setBackgroundResource(R.drawable.close_timer);
                    SubEventTimerDispAcitivity.this.isTimerUnfold = true;
                }
            } else {
                SubEventTimerDispAcitivity.this.timerfold.setBackgroundResource(R.drawable.open_timer);
                SubEventTimerDispAcitivity.this.isTimerUnfold = false;
            }
            notifyDataSetChanged();
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.timer_setting, (ViewGroup) null);
            }
            TextView timerIndex = (TextView) convertView.findViewById(R.id.timer_index);
            TextView progName = (TextView) convertView.findViewById(R.id.bt1);
            TextView remindTime = (TextView) convertView.findViewById(R.id.remind_time);
            ImageView timerMore = (ImageView) convertView.findViewById(R.id.timer_more);
            timerMore.setTag(Integer.valueOf(position));
            TableLayout timerDetailList = (TableLayout) convertView.findViewById(R.id.timer_detail_list);
            ImageView listProgress = (ImageView) convertView.findViewById(R.id.timer_progress);
            TextView recordDate = (TextView) convertView.findViewById(R.id.bt2);
            TextView recordStart = (TextView) convertView.findViewById(R.id.bt3);
            TextView recordStop = (TextView) convertView.findViewById(R.id.bt4);
            TextView recordRepeat = (TextView) convertView.findViewById(R.id.bt5);
            TextView recordStatus = (TextView) convertView.findViewById(R.id.bt6);
            convertView.setTag(Integer.valueOf(position));
            timerIndex.setText(new StringBuilder(String.valueOf(position + 1)).toString());
            progName.setText(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimeProgramName());
            int timerSchedule = ((100 - ((SubEventTimerDispAcitivity.this.GetTotalMinLen(position) * 100) / 1440)) * SubEventTimerDispAcitivity.this.progressLen) / 100;
            ViewGroup.LayoutParams parms = listProgress.getLayoutParams();
            if (timerSchedule == 0) {
                parms.width = 0;
                listProgress.setLayoutParams(parms);
            } else {
                parms.width = timerSchedule;
                listProgress.setLayoutParams(parms);
            }
            if (((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerDayLen() > 0) {
                remindTime.setText(new StringBuilder(String.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerHourLen())).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerMinLen()))).concat(Marker.ANY_NON_NULL_MARKER).concat(new StringBuilder(String.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerDayLen())).toString()).concat("D"));
            } else if (((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerHourLen() > 0 || ((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerMinLen() > 0) {
                remindTime.setText(new StringBuilder(String.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerHourLen())).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerMinLen()))));
            } else {
                remindTime.setText("on-going");
            }
            if (((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetShowDetail()) {
                timerMore.setImageResource(R.drawable.more_arrow_down);
                timerDetailList.setVisibility(0);
                recordDate.setText(new StringBuilder(String.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimeMonth())).toString().concat(ServiceReference.DELIMITER).concat(new StringBuilder(String.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimeDay())).toString()));
                recordStart.setText(new StringBuilder(String.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetStartHour())).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetStartMin()))));
                if (((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetEndHour() >= 24) {
                    int endHpur = ((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetEndHour() % 24;
                    recordStop.setText(new StringBuilder(String.valueOf(endHpur)).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetEndMin()))).concat("+ 1D"));
                } else {
                    recordStop.setText(new StringBuilder(String.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetEndHour())).toString().concat(":").concat(String.format("%1$02d", Integer.valueOf(((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetEndMin()))));
                }
                switch (((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerRepeat()) {
                    case 0:
                        recordRepeat.setText("1X");
                        break;
                    case 1:
                        recordRepeat.setText("Daily");
                        break;
                    case 2:
                        recordRepeat.setText("Weekly");
                        break;
                    case 3:
                        recordRepeat.setText("1-5");
                        break;
                    case 4:
                        recordRepeat.setText("6-7");
                        break;
                }
            } else {
                timerMore.setImageResource(R.drawable.more_arrow);
                timerDetailList.setVisibility(8);
            }
            if ((((DataConvertTimeModel) SubEventTimerDispAcitivity.this.timeModels.get(position)).GetTimerStatus() & 2) == 2) {
                recordStatus.setText("Yes");
                progName.setTextColor(SubEventTimerDispAcitivity.this.getResources().getColor(R.color.red));
            } else {
                recordStatus.setText("NO");
                progName.setTextColor(SubEventTimerDispAcitivity.this.getResources().getColor(R.color.green));
            }
            return convertView;
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("SubEventTimerDispActivity", "onConfigurationChanged");
    }
}
