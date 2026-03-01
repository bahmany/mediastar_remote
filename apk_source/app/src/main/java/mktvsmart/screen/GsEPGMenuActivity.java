package mktvsmart.screen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import mktvsmart.screen.CommonCofirmDialog;
import mktvsmart.screen.dataconvert.model.DataConvertTimeModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.view.ListviewAdapter;

/* loaded from: classes.dex */
public class GsEPGMenuActivity extends Activity {
    private static final int CONTEXT_MENU_ITEM_RECORD = 0;
    private static final int CONTEXT_MENU_ITEM_VIEW = 1;
    public static final int REQUSET = 1;
    private static final int dayOfOneWeek = 7;
    private event_list__adapter adapter;
    private Button btnReturn;
    private int curDayofMonth;
    private int curDayofWeek;
    private int curHour;
    private int curMinute;
    private int curMonth;
    private int curYear;
    private TextView epgTitleText;
    private ViewPager epgViewPager;
    private List<View> epgViews;
    private DataConvertTimeModel event;
    private ListView mListView;
    private MessageProcessor msgProc;
    private PagerTabStrip pagerTabStrip;
    private DataParser parser;
    private GsEPGTableChannel programEPG;
    private String strEPGTittle;
    private Socket tcpSocket;
    private int timeDurationOfSTBWithMobile;
    private String[] strTabStrip = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private String[] strMonth = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};
    private int longClickPosition = -1;
    private int clickPositionToDetail = -1;
    private int currentSelectTimerType = 0;
    private List<DataConvertTimeModel> mAddEvent = null;
    private Dialog mDialog = null;
    private boolean mFlag = false;
    private List<String> mTimerIndexmodels = null;
    private MessageProcessor.PerformOnForeground epgMenuEditEventTimerPof = new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsEPGMenuActivity.1
        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            switch (msg.arg2) {
                case 0:
                    Toast.makeText(GsEPGMenuActivity.this, R.string.operate_success, 0).show();
                    GsEPGMenuActivity.this.refreshEventTimerType(GsEPGMenuActivity.this.longClickPosition, GsEPGMenuActivity.this.currentSelectTimerType);
                    GsEPGMenuActivity.this.longClickPosition = -1;
                    GsEPGMenuActivity.this.currentSelectTimerType = 0;
                    break;
                case 1:
                    Toast.makeText(GsEPGMenuActivity.this, R.string.operate_fail, 0).show();
                    GsEPGMenuActivity.this.longClickPosition = -1;
                    GsEPGMenuActivity.this.currentSelectTimerType = 0;
                    break;
                case 15:
                    if (msg.arg1 > 0) {
                        Bundle data = msg.getData();
                        byte[] recv_data = data.getByteArray("ReceivedData");
                        InputStream istream = new ByteArrayInputStream(recv_data, 0, msg.arg1);
                        try {
                            GsEPGMenuActivity.this.mTimerIndexmodels = GsEPGMenuActivity.this.parser.parse(istream, 15);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (GsEPGMenuActivity.this.mFlag) {
                            CommonCofirmDialog showDownDialog = new CommonCofirmDialog(GsEPGMenuActivity.this);
                            showDownDialog.setmTitle(GsEPGMenuActivity.this.getResources().getString(R.string.warning_dialog));
                            showDownDialog.setmContent(GsEPGMenuActivity.this.getResources().getString(R.string.str_timer_repeat));
                            showDownDialog.setOnButtonClickListener(GsEPGMenuActivity.this.mWarningDialogOnClickListener);
                            showDownDialog.show();
                            GsEPGMenuActivity.this.mFlag = false;
                            break;
                        }
                    }
                    break;
            }
        }
    };
    private CommonCofirmDialog.OnButtonClickListener mWarningDialogOnClickListener = new CommonCofirmDialog.OnButtonClickListener() { // from class: mktvsmart.screen.GsEPGMenuActivity.2
        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedConfirm() throws SocketException, UnsupportedEncodingException {
            try {
                ((DataConvertTimeModel) GsEPGMenuActivity.this.mAddEvent.get(0)).SetTimerIndex(Integer.parseInt((String) GsEPGMenuActivity.this.mTimerIndexmodels.get(0)));
                byte[] data_buff = GsEPGMenuActivity.this.parser.serialize(GsEPGMenuActivity.this.mAddEvent, GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE).getBytes("UTF-8");
                GsEPGMenuActivity.this.tcpSocket.setSoTimeout(3000);
                GsSendSocket.sendSocketToStb(data_buff, GsEPGMenuActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedCancel() {
            GsEPGMenuActivity.this.longClickPosition = -1;
            GsEPGMenuActivity.this.currentSelectTimerType = 0;
        }
    };

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD, this, this.epgMenuEditEventTimerPof);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE, this, this.epgMenuEditEventTimerPof);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsEPGMenuActivity.3
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsEPGMenuActivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent = new Intent();
                intent.setClass(GsEPGMenuActivity.this, GsLoginListActivity.class);
                GsEPGMenuActivity.this.startActivity(intent);
                GsEPGMenuActivity.this.finish();
            }
        });
        this.msgProc.setOnMessageProcess(2015, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsEPGMenuActivity.4
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsEPGMenuActivity.this, R.string.str_become_master, 1).show();
                for (int index = 0; index < GsEPGMenuActivity.this.epgViews.size(); index++) {
                    ((View) GsEPGMenuActivity.this.epgViews.get(index)).setLongClickable(false);
                }
            }
        });
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws SocketException, Resources.NotFoundException, NumberFormatException {
        MyViewPagerAdapter myViewPagerAdapter = null;
        super.onCreate(savedInstanceState);
        Calendar c = Calendar.getInstance();
        this.curYear = c.get(1);
        this.curMonth = c.get(2);
        this.curDayofWeek = c.get(7);
        this.curDayofMonth = c.get(5);
        this.curHour = c.get(11);
        this.curMinute = c.get(12);
        setContentView(R.layout.epg_viewpager);
        this.epgTitleText = (TextView) findViewById(R.id.epg_title);
        this.btnReturn = (Button) findViewById(R.id.btnEventListReturn);
        setMessageProcess();
        this.parser = ParserFactory.getParser();
        try {
            CreateSocket cSocket = new CreateSocket("", 0);
            this.tcpSocket = cSocket.GetSocket();
            this.tcpSocket.setSoTimeout(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GsSession session = GsSession.getSession();
        this.programEPG = (GsEPGTableChannel) session.get("EPG_PROGRAM_TABLE");
        this.curDayofWeek = ((this.curDayofWeek + (this.programEPG.getTodayDate() - this.curDayofMonth)) + 7) % 7;
        this.timeDurationOfSTBWithMobile = ((this.curHour * 60) + this.curMinute) - this.programEPG.getCurrentEpgTime();
        this.epgViews = new ArrayList();
        this.epgViewPager = (ViewPager) findViewById(R.id.viewpager);
        this.pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTab);
        this.pagerTabStrip.setTextSpacing(20);
        this.pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.tabStripBG));
        this.pagerTabStrip.setTextColor(getResources().getColor(R.color.white));
        this.pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.tabIndicator));
        LayoutInflater mInflater = LayoutInflater.from(this);
        for (int i = 0; i < 7; i++) {
            this.mListView = (ListView) mInflater.inflate(R.layout.epg_layout, (ViewGroup) null);
            event_list__adapter adapter = new event_list__adapter(this, i);
            this.mListView.setAdapter((ListAdapter) adapter);
            adjustSelectionOfEpgListView();
            try {
                this.mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: mktvsmart.screen.GsEPGMenuActivity.5
                    @Override // android.widget.AdapterView.OnItemLongClickListener
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        GsEPGMenuActivity.this.longClickPosition = position;
                        final Dialog contextMenuDialog = new Dialog(GsEPGMenuActivity.this, R.style.dialog);
                        LayoutInflater inflater = LayoutInflater.from(GsEPGMenuActivity.this);
                        View contextMenuLayout = inflater.inflate(R.layout.epg_context_menu_dialog, (ViewGroup) null);
                        ListView list = (ListView) contextMenuLayout.findViewById(R.id.epg_context_menu_list);
                        Button cancelButton = (Button) contextMenuLayout.findViewById(R.id.epg_context_menu_cancel_btn);
                        ListviewAdapter adapter2 = new ListviewAdapter(GsEPGMenuActivity.this, GsEPGMenuActivity.this.getContextMenuData());
                        list.setAdapter((ListAdapter) adapter2);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.GsEPGMenuActivity.5.1
                            @Override // android.widget.AdapterView.OnItemClickListener
                            public void onItemClick(AdapterView<?> parent, View view, int position2, long id) throws NumberFormatException, UnsupportedEncodingException {
                                try {
                                    GsEPGEvent eventTemp = GsEPGMenuActivity.this.programEPG.epgChannelEvent[GsEPGMenuActivity.this.epgViewPager.getCurrentItem()].getArrayList().get(GsEPGMenuActivity.this.longClickPosition);
                                    int time_start = Integer.parseInt(eventTemp.getStartTime());
                                    int time_end = Integer.parseInt(eventTemp.getEndTime());
                                    Calendar calTemp = Calendar.getInstance();
                                    calTemp.set(GsEPGMenuActivity.this.curYear, GsEPGMenuActivity.this.curMonth, GsEPGMenuActivity.this.programEPG.getTodayDate(), 0, 0, 0);
                                    calTemp.add(5, GsEPGMenuActivity.this.epgViewPager.getCurrentItem());
                                    int tempMonth = calTemp.get(2);
                                    int tempDay = calTemp.get(5);
                                    GsEPGMenuActivity.this.event = new DataConvertTimeModel();
                                    GsEPGMenuActivity.this.event.SetTimeProgramName(GsEPGMenuActivity.this.programEPG.getProgramName());
                                    GsEPGMenuActivity.this.event.setProgramId(GsEPGMenuActivity.this.programEPG.getProgramId());
                                    GsEPGMenuActivity.this.event.SetTimeMonth(tempMonth + 1);
                                    GsEPGMenuActivity.this.event.SetTimeDay(tempDay);
                                    GsEPGMenuActivity.this.event.SetStartHour(time_start / 100);
                                    GsEPGMenuActivity.this.event.SetStartMin(time_start % 100);
                                    GsEPGMenuActivity.this.event.SetEndHour(time_end / 100);
                                    GsEPGMenuActivity.this.event.SetEndMin(time_end % 100);
                                    switch (position2) {
                                        case 0:
                                            GsEPGMenuActivity.this.currentSelectTimerType = 2;
                                            GsEPGMenuActivity.this.event.SetTimerStatus(2);
                                            break;
                                        case 1:
                                            GsEPGMenuActivity.this.currentSelectTimerType = 1;
                                            GsEPGMenuActivity.this.event.SetTimerStatus(0);
                                            break;
                                    }
                                    GsEPGMenuActivity.this.event.SetTimerRepeat(0);
                                    GsEPGMenuActivity.this.mFlag = true;
                                    GsEPGMenuActivity.this.mAddEvent = new ArrayList();
                                    GsEPGMenuActivity.this.mAddEvent.add(GsEPGMenuActivity.this.event);
                                    byte[] sendData = GsEPGMenuActivity.this.parser.serialize(GsEPGMenuActivity.this.mAddEvent, GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD).getBytes("UTF-8");
                                    GsSendSocket.sendSocketToStb(sendData, GsEPGMenuActivity.this.tcpSocket, 0, sendData.length, GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD);
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                                if (contextMenuDialog != null && contextMenuDialog.isShowing()) {
                                    contextMenuDialog.dismiss();
                                }
                            }
                        });
                        cancelButton.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsEPGMenuActivity.5.2
                            @Override // android.view.View.OnClickListener
                            public void onClick(View v) {
                                if (contextMenuDialog != null && contextMenuDialog.isShowing()) {
                                    contextMenuDialog.dismiss();
                                }
                            }
                        });
                        contextMenuDialog.setContentView(contextMenuLayout);
                        contextMenuDialog.setCanceledOnTouchOutside(false);
                        contextMenuDialog.show();
                        return true;
                    }
                });
                this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.GsEPGMenuActivity.6
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        try {
                            GsSession session2 = GsSession.getSession();
                            Calendar calTemp = Calendar.getInstance();
                            calTemp.set(GsEPGMenuActivity.this.curYear, GsEPGMenuActivity.this.curMonth, GsEPGMenuActivity.this.programEPG.getTodayDate(), 0, 0, 0);
                            calTemp.add(5, GsEPGMenuActivity.this.epgViewPager.getCurrentItem());
                            int tempMonth = calTemp.get(2);
                            int tempDay = calTemp.get(5);
                            GsEPGEvent eventClick = GsEPGMenuActivity.this.programEPG.epgChannelEvent[GsEPGMenuActivity.this.epgViewPager.getCurrentItem()].getArrayList().get(arg2);
                            eventClick.setEventMonth(tempMonth);
                            eventClick.setEventDate(tempDay);
                            eventClick.setProgramName(GsEPGMenuActivity.this.programEPG.getProgramName());
                            eventClick.setProgramId(GsEPGMenuActivity.this.programEPG.getProgramId());
                            GsEPGMenuActivity.this.clickPositionToDetail = arg2;
                            session2.put("EPG_PROGRAM_EVENT", eventClick);
                            Intent intent = new Intent();
                            intent.setClass(GsEPGMenuActivity.this, GsEventDetailActivity.class);
                            GsEPGMenuActivity.this.startActivityForResult(intent, 1);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                });
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.epgViews.add(this.mListView);
        }
        this.epgViewPager.setAdapter(new MyViewPagerAdapter(this, myViewPagerAdapter));
        this.btnReturn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsEPGMenuActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    GsEPGMenuActivity.this.finishActivity(0);
                    GsEPGMenuActivity.this.onBackPressed();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        });
        if (GMScreenGlobalInfo.isClientTypeSlave()) {
            for (int index = 0; index < this.epgViews.size(); index++) {
                this.epgViews.get(index).setLongClickable(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<String> getContextMenuData() {
        ArrayList<String> data = new ArrayList<>();
        data.add(getString(R.string.timer_type_record));
        data.add(getString(R.string.timer_type_view));
        return data;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshEventTimerType(int position, int currentTimerType) {
        if (position != -1 && currentTimerType != 0) {
            GsEPGEvent eventTemp = this.programEPG.epgChannelEvent[this.epgViewPager.getCurrentItem()].getArrayList().get(position);
            eventTemp.setEpgTimerType(currentTimerType);
            this.programEPG.epgChannelEvent[this.epgViewPager.getCurrentItem()].getArrayList().set(position, eventTemp);
            this.adapter = (event_list__adapter) ((ListView) this.epgViews.get(this.epgViewPager.getCurrentItem())).getAdapter();
            this.adapter.notifyDataSetChanged();
        }
    }

    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        this.msgProc.recycle();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            int timerType = data.getIntExtra(GsEventDetailActivity.EPG_TIMER_TYPE_ID, 0);
            refreshEventTimerType(this.clickPositionToDetail, timerType);
            this.clickPositionToDetail = -1;
        }
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private MyViewPagerAdapter() {
        }

        /* synthetic */ MyViewPagerAdapter(GsEPGMenuActivity gsEPGMenuActivity, MyViewPagerAdapter myViewPagerAdapter) {
            this();
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return GsEPGMenuActivity.this.epgViews.size();
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getItemPosition(Object object) {
            return -2;
        }

        @Override // android.support.v4.view.PagerAdapter
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override // android.support.v4.view.PagerAdapter
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) GsEPGMenuActivity.this.epgViews.get(position));
        }

        @Override // android.support.v4.view.PagerAdapter
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView((View) GsEPGMenuActivity.this.epgViews.get(position));
            return GsEPGMenuActivity.this.epgViews.get(position);
        }

        @Override // android.support.v4.view.PagerAdapter
        public CharSequence getPageTitle(int position) {
            Calendar calTemp = Calendar.getInstance();
            calTemp.set(GsEPGMenuActivity.this.curYear, GsEPGMenuActivity.this.curMonth, GsEPGMenuActivity.this.programEPG.getTodayDate(), 0, 0, 0);
            calTemp.add(5, GsEPGMenuActivity.this.epgViewPager.getCurrentItem());
            int tempMonth = calTemp.get(2);
            int tempDay = calTemp.get(5);
            GsEPGMenuActivity.this.strEPGTittle = String.valueOf(GsEPGMenuActivity.this.strMonth[tempMonth]) + " " + tempDay;
            GsEPGMenuActivity.this.epgTitleText.setText(GsEPGMenuActivity.this.strEPGTittle);
            GsEPGMenuActivity.this.adapter = (event_list__adapter) ((ListView) GsEPGMenuActivity.this.epgViews.get(GsEPGMenuActivity.this.epgViewPager.getCurrentItem())).getAdapter();
            GsEPGMenuActivity.this.adapter.notifyDataSetChanged();
            return GsEPGMenuActivity.this.strTabStrip[(((GsEPGMenuActivity.this.curDayofWeek + position) - 1) + 7) % 7];
        }
    }

    private class event_list__adapter extends BaseAdapter {
        int currentEpgDayIndex;
        LayoutInflater inflater;

        private class ViewHolder {
            public ImageView epgTimerType;
            public TextView eventName;
            public TextView eventTime;
            public LinearLayout linearLayoutEventTime;
            public RelativeLayout relativeLayoutEventName;

            private ViewHolder() {
                this.eventName = null;
            }

            /* synthetic */ ViewHolder(event_list__adapter event_list__adapterVar, ViewHolder viewHolder) {
                this();
            }
        }

        public event_list__adapter(Context context, int epgDayIndex) {
            this.inflater = LayoutInflater.from(context);
            this.currentEpgDayIndex = epgDayIndex;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return GsEPGMenuActivity.this.programEPG.epgChannelEvent[this.currentEpgDayIndex].getArrayList().size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return GsEPGMenuActivity.this.programEPG.epgChannelEvent[this.currentEpgDayIndex].getArrayList().get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) throws NumberFormatException {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(this, null);
                convertView = this.inflater.inflate(R.layout.epg_item_layout, (ViewGroup) null);
                holder.eventTime = (TextView) convertView.findViewById(R.id.event_time);
                holder.eventName = (TextView) convertView.findViewById(R.id.event_name);
                holder.epgTimerType = (ImageView) convertView.findViewById(R.id.image_epg_timer_type);
                holder.linearLayoutEventTime = (LinearLayout) convertView.findViewById(R.id.linear_layout_event_time);
                holder.relativeLayoutEventName = (RelativeLayout) convertView.findViewById(R.id.linear_layout_event_name);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GsEPGEvent eventTemp = (GsEPGEvent) getItem(position);
            int time_start = Integer.parseInt(eventTemp.getStartTime());
            int time_end = Integer.parseInt(eventTemp.getEndTime());
            String stringTime = String.format(Locale.ENGLISH, "%02d:%02d--%02d:%02d", Integer.valueOf((time_start / 100) % 24), Integer.valueOf((time_start % 100) % 60), Integer.valueOf((time_end / 100) % 24), Integer.valueOf((time_end % 100) % 60));
            String stringName = eventTemp.getEventTitle()[0];
            holder.eventTime.setText(stringTime);
            holder.eventName.setText(stringName);
            switch (eventTemp.getEpgTimerType()) {
                case 0:
                    holder.epgTimerType.setVisibility(4);
                    break;
                case 1:
                    holder.epgTimerType.setBackgroundResource(R.drawable.event_view_tiemr);
                    holder.epgTimerType.setVisibility(0);
                    break;
                case 2:
                    holder.epgTimerType.setBackgroundResource(R.drawable.event_record_tiemr);
                    holder.epgTimerType.setVisibility(0);
                    break;
            }
            holder.relativeLayoutEventName.setBackgroundResource(R.drawable.disp_channel);
            holder.linearLayoutEventTime.setBackgroundResource(R.drawable.disp_index);
            holder.eventName.setTextColor(GsEPGMenuActivity.this.getResources().getColor(R.color.black));
            if (GsEPGMenuActivity.this.epgViewPager.getCurrentItem() == 0) {
                Calendar calTemp = Calendar.getInstance();
                calTemp.add(12, -GsEPGMenuActivity.this.timeDurationOfSTBWithMobile);
                GsEPGMenuActivity.this.curHour = calTemp.get(11);
                GsEPGMenuActivity.this.curMinute = calTemp.get(12);
                int currentTime = (GsEPGMenuActivity.this.curHour * 100) + GsEPGMenuActivity.this.curMinute;
                if (currentTime >= time_start && currentTime < time_end % 2400) {
                    holder.relativeLayoutEventName.setBackgroundResource(R.drawable.list_item_focus);
                    holder.linearLayoutEventTime.setBackgroundResource(R.drawable.list_item_index_focus);
                    holder.eventName.setTextColor(GsEPGMenuActivity.this.getResources().getColor(R.color.white));
                }
            }
            convertView.setTag(holder);
            return convertView;
        }
    }

    public void adjustSelectionOfEpgListView() throws NumberFormatException {
        int position = 0;
        if (this.epgViewPager.getCurrentItem() == 0) {
            Iterator<GsEPGEvent> it = this.programEPG.epgChannelEvent[0].getArrayList().iterator();
            while (it.hasNext()) {
                GsEPGEvent eventTemp = it.next();
                int time_start = Integer.parseInt(eventTemp.getStartTime());
                int time_end = Integer.parseInt(eventTemp.getEndTime());
                Calendar calTemp = Calendar.getInstance();
                calTemp.add(12, -this.timeDurationOfSTBWithMobile);
                this.curHour = calTemp.get(11);
                this.curMinute = calTemp.get(12);
                int currentTime = (this.curHour * 100) + this.curMinute;
                if (currentTime >= time_start && currentTime <= time_end % 2400) {
                    if (position < 5) {
                        this.mListView.setSelection(0);
                    } else {
                        this.mListView.setSelection(position - 3);
                    }
                }
                position++;
            }
            return;
        }
        this.mListView.setSelection(0);
    }
}
