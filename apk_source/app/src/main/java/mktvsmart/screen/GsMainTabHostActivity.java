package mktvsmart.screen;

import android.app.ActivityGroup;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mktvsmart.screen.channel.ChannelData;
import mktvsmart.screen.channel.GsChannelListActivity;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertSatModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.filebroswer.GsFileBroswerActivity;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.pvr2small.GsPvr2SmallActivity;
import mktvsmart.screen.spectrum.GsSpectrumActivity;

/* loaded from: classes.dex */
public class GsMainTabHostActivity extends ActivityGroup implements View.OnClickListener {
    private Dialog mSatInfoDialog;
    private MessageProcessor msgProc;
    private List<DataConvertSatModel> satList;
    private TabHost tabHost;
    private Socket tcpSocket;
    private final int CHANNEL_TAB = 0;
    private final int REMOTE_TAB = 1;
    private final int TIMER_TAB = 2;
    private final int MORE_TAB = 3;
    private final int SAT_MENU = 0;
    private final int PVR_MENU = 1;
    private final int PARENTAL_CONTROL_MENU = 2;
    private final int DEBUG_MENU = 3;
    private final int STB_INFO_MENU = 4;
    private final int MY_FILE_MENU = 5;
    private final int G_SETTING = 6;
    private final int G_SPECTRUM = 7;
    private final int MORE_MENU_MARGIN = 15;
    private String[] tabSpecTag = {"ChannelTag", "RemoteTag", "TimerTag", "MoreTag"};
    private Integer[] moreTableIconTittle1 = {Integer.valueOf(R.string.popup_window_option_sat_info), Integer.valueOf(R.string.popup_window_option_pvr_to_small_info), Integer.valueOf(R.string.popup_window_option_parental_control), Integer.valueOf(R.string.popup_window_option_debug), Integer.valueOf(R.string.popup_window_option_stb_info), Integer.valueOf(R.string.popup_window_option_record_files), Integer.valueOf(R.string.popup_window_option_g_setting), Integer.valueOf(R.string.popup_window_option_spectrum)};
    private Integer[] moreTableIconImage1 = {Integer.valueOf(R.drawable.satellite_icon_small), Integer.valueOf(R.drawable.pvr2small_icon_small), Integer.valueOf(R.drawable.control_icon_small), Integer.valueOf(R.drawable.debug_icon_small), Integer.valueOf(R.drawable.stb_info_icon), Integer.valueOf(R.drawable.file_icon_small), Integer.valueOf(R.drawable.icon_g_setting), Integer.valueOf(R.drawable.spectrum)};
    private HashMap<String, Integer> tabUnclickedIcon = new HashMap<>();
    private HashMap<String, Integer> tabClickedIcon = new HashMap<>();
    private HashMap<String, Intent> tabContent = new HashMap<>();
    private PopupWindow moreMenu = null;
    private int currentTab = 0;
    private int lastTab = 0;
    private SatListAdapter mSatListAdapter = null;
    private boolean activityFlag = true;
    private HorizontalScrollView horizontalScrollView = null;

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(2015, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsMainTabHostActivity.1
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsMainTabHostActivity.this, R.string.str_become_master, 1).show();
                ImageView moreTab = (ImageView) GsMainTabHostActivity.this.tabHost.getTabWidget().getChildAt(1);
                moreTab.setEnabled(true);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_REFRESH_SAT_LIST, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsMainTabHostActivity.2
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                ChannelData channelDataObject = ChannelData.getInstance();
                List<DataConvertChannelModel> channelList = channelDataObject.getChannelListByTvRadioType();
                GsMainTabHostActivity.this.satList = ChannelData.getSatList(channelDataObject.getmAllSatList(), channelList, GsMainTabHostActivity.this.getResources().getString(R.string.All_Satellites));
                if (GsMainTabHostActivity.this.mSatListAdapter != null) {
                    GsMainTabHostActivity.this.mSatListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override // android.app.ActivityGroup, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_above);
        this.tabHost = (TabHost) findViewById(R.id.tabhost);
        this.tabHost.setup(getLocalActivityManager());
        LayoutInflater.from(this);
        setMessageProcess();
        try {
            this.tcpSocket = new CreateSocket(null, 0).GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.tabUnclickedIcon.put(this.tabSpecTag[0], Integer.valueOf(R.drawable.channel_tab_not_selected));
        this.tabUnclickedIcon.put(this.tabSpecTag[1], Integer.valueOf(R.drawable.remote_tab_not_selected));
        this.tabUnclickedIcon.put(this.tabSpecTag[2], Integer.valueOf(R.drawable.timer_tab_not_selected));
        this.tabUnclickedIcon.put(this.tabSpecTag[3], Integer.valueOf(R.drawable.more_tab_not_selected));
        this.tabClickedIcon.put(this.tabSpecTag[0], Integer.valueOf(R.drawable.channel_tab_selected));
        this.tabClickedIcon.put(this.tabSpecTag[1], Integer.valueOf(R.drawable.remote_tab_selected));
        this.tabClickedIcon.put(this.tabSpecTag[2], Integer.valueOf(R.drawable.timer_tab_selected));
        this.tabClickedIcon.put(this.tabSpecTag[3], Integer.valueOf(R.drawable.more_tab_selected));
        this.tabContent.put(this.tabSpecTag[0], new Intent().setClass(this, GsChannelListActivity.class));
        this.tabContent.put(this.tabSpecTag[1], new Intent().setClass(this, GsRemoteControlActivity.class));
        this.tabContent.put(this.tabSpecTag[2], new Intent().setClass(this, SubEventTimerDispAcitivity.class));
        this.tabContent.put(this.tabSpecTag[3], new Intent().setClass(this, GsParentControlActivity.class));
        ImageView tabView = new ImageView(this);
        tabView.setBackgroundResource(this.tabClickedIcon.get(this.tabSpecTag[0]).intValue());
        TabHost.TabSpec specChannelList = this.tabHost.newTabSpec(this.tabSpecTag[0]);
        specChannelList.setIndicator(tabView);
        specChannelList.setContent(this.tabContent.get(this.tabSpecTag[0]));
        this.tabHost.addTab(specChannelList);
        ImageView tabView2 = new ImageView(this);
        tabView2.setBackgroundResource(this.tabUnclickedIcon.get(this.tabSpecTag[1]).intValue());
        TabHost.TabSpec specRemoteCtrl = this.tabHost.newTabSpec(this.tabSpecTag[1]);
        if (GMScreenGlobalInfo.isClientTypeSlave()) {
            tabView2.setEnabled(false);
        }
        specRemoteCtrl.setIndicator(tabView2);
        specRemoteCtrl.setContent(this.tabContent.get(this.tabSpecTag[1]));
        this.tabHost.addTab(specRemoteCtrl);
        ImageView tabView3 = new ImageView(this);
        tabView3.setBackgroundResource(this.tabUnclickedIcon.get(this.tabSpecTag[2]).intValue());
        TabHost.TabSpec specEvtTimer = this.tabHost.newTabSpec(this.tabSpecTag[2]);
        specEvtTimer.setIndicator(tabView3);
        specEvtTimer.setContent(this.tabContent.get(this.tabSpecTag[2]));
        this.tabHost.addTab(specEvtTimer);
        ImageView tabView4 = new ImageView(this);
        tabView4.setBackgroundResource(this.tabUnclickedIcon.get(this.tabSpecTag[3]).intValue());
        TabHost.TabSpec specMoreMenu = this.tabHost.newTabSpec(this.tabSpecTag[3]);
        specMoreMenu.setIndicator(tabView4);
        specMoreMenu.setContent(this.tabContent.get(this.tabSpecTag[3]));
        this.tabHost.addTab(specMoreMenu);
        this.tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() { // from class: mktvsmart.screen.GsMainTabHostActivity.3
            @Override // android.widget.TabHost.OnTabChangeListener
            public void onTabChanged(String tabId) throws UnsupportedEncodingException {
                ImageView tabView5 = (ImageView) GsMainTabHostActivity.this.tabHost.getTabWidget().getChildAt(GsMainTabHostActivity.this.lastTab);
                tabView5.setBackgroundResource(((Integer) GsMainTabHostActivity.this.tabUnclickedIcon.get(GsMainTabHostActivity.this.tabSpecTag[GsMainTabHostActivity.this.lastTab])).intValue());
                if (!tabId.equals(GsMainTabHostActivity.this.tabSpecTag[0])) {
                    if (!tabId.equals(GsMainTabHostActivity.this.tabSpecTag[1])) {
                        if (tabId.equals(GsMainTabHostActivity.this.tabSpecTag[2])) {
                            GsMainTabHostActivity.this.currentTab = 2;
                        }
                    } else {
                        GsMainTabHostActivity.this.currentTab = 1;
                        GsSendSocket.sendOnlyCommandSocketToStb(GsMainTabHostActivity.this.tcpSocket, 19);
                    }
                } else {
                    GsMainTabHostActivity.this.currentTab = 0;
                }
                ImageView tabView6 = (ImageView) GsMainTabHostActivity.this.tabHost.getTabWidget().getChildAt(GsMainTabHostActivity.this.currentTab);
                tabView6.setBackgroundResource(((Integer) GsMainTabHostActivity.this.tabClickedIcon.get(GsMainTabHostActivity.this.tabSpecTag[GsMainTabHostActivity.this.currentTab])).intValue());
                GsMainTabHostActivity.this.lastTab = GsMainTabHostActivity.this.currentTab;
            }
        });
        final ImageView moreTab = (ImageView) this.tabHost.getTabWidget().getChildAt(3);
        moreTab.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsMainTabHostActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                moreTab.setBackgroundResource(((Integer) GsMainTabHostActivity.this.tabClickedIcon.get(GsMainTabHostActivity.this.tabSpecTag[3])).intValue());
                ImageView view = (ImageView) GsMainTabHostActivity.this.tabHost.getTabWidget().getChildAt(GsMainTabHostActivity.this.lastTab);
                view.setBackgroundResource(((Integer) GsMainTabHostActivity.this.tabUnclickedIcon.get(GsMainTabHostActivity.this.tabSpecTag[GsMainTabHostActivity.this.lastTab])).intValue());
                GsMainTabHostActivity.this.showPopupWindow(v);
            }
        });
        this.tabHost.setCurrentTab(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPopupWindow(View parent) {
        LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        View layout = inflater.inflate(R.layout.more_table_window_layout, (ViewGroup) null);
        LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.linearLayoutMenu);
        this.horizontalScrollView = (HorizontalScrollView) layout.findViewById(R.id.horizonMenu);
        for (int i = 0; i < this.moreTableIconTittle1.length; i++) {
            boolean skipFlag = false;
            switch (i) {
                case 0:
                    if (this.lastTab != 0 || GMScreenGlobalInfo.isClientTypeSlave() || GMScreenGlobalInfo.getCurStbInfo().getmSatEnable() != 1) {
                        skipFlag = true;
                        break;
                    }
                    break;
                case 1:
                    if (GMScreenGlobalInfo.getmPvr2ipServerSupport() != 1 || GMScreenGlobalInfo.getCurStbInfo().getmSat2ipEnable() == 2) {
                        skipFlag = true;
                        break;
                    }
                    break;
                case 2:
                    if (GMScreenGlobalInfo.isClientTypeSlave()) {
                        skipFlag = true;
                        break;
                    }
                    break;
                case 3:
                    boolean debugMenuEnable = true;
                    switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                        case 30:
                        case 32:
                        case 71:
                        case 72:
                        case 74:
                            debugMenuEnable = false;
                            break;
                    }
                    if (GMScreenGlobalInfo.isClientTypeSlave() || !debugMenuEnable) {
                        skipFlag = true;
                        break;
                    }
                case 7:
                    skipFlag = true;
                    break;
            }
            if (!skipFlag) {
                LinearLayout.LayoutParams smallImageLayoutParames = new LinearLayout.LayoutParams(-2, -2, 1.0f);
                smallImageLayoutParames.gravity = 1;
                smallImageLayoutParames.topMargin = 15;
                LinearLayout.LayoutParams smallTextLayoutParames = new LinearLayout.LayoutParams(-2, -2, 1.0f);
                smallTextLayoutParames.gravity = 1;
                smallTextLayoutParames.bottomMargin = 15;
                LinearLayout menuLinerLayout01 = new LinearLayout(this);
                menuLinerLayout01.setBackgroundResource(R.drawable.popup_window_list_item_background);
                ImageView smallImageIcon = new ImageView(this);
                smallImageIcon.setImageResource(this.moreTableIconImage1[i].intValue());
                TextView smallText = new TextView(this);
                smallText.setText(this.moreTableIconTittle1[i].intValue());
                smallText.setTextColor(-1);
                menuLinerLayout01.setOrientation(1);
                menuLinerLayout01.setTag(Integer.valueOf(i));
                menuLinerLayout01.addView(smallImageIcon, smallImageLayoutParames);
                menuLinerLayout01.addView(smallText, smallTextLayoutParames);
                linearLayout.addView(menuLinerLayout01);
            }
        }
        LinearLayout mLinearLayout = (LinearLayout) this.horizontalScrollView.getChildAt(0);
        for (int index = 0; index < mLinearLayout.getChildCount(); index++) {
            View child = mLinearLayout.getChildAt(index);
            child.setOnClickListener(this);
        }
        this.moreMenu = new PopupWindow(layout, -2, -2, true);
        this.moreMenu.setFocusable(true);
        this.moreMenu.setOutsideTouchable(true);
        this.moreMenu.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: mktvsmart.screen.GsMainTabHostActivity.5
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                ImageView tabView = (ImageView) GsMainTabHostActivity.this.tabHost.getTabWidget().getChildAt(3);
                tabView.setBackgroundResource(((Integer) GsMainTabHostActivity.this.tabUnclickedIcon.get(GsMainTabHostActivity.this.tabSpecTag[3])).intValue());
                ImageView tabView2 = (ImageView) GsMainTabHostActivity.this.tabHost.getTabWidget().getChildAt(GsMainTabHostActivity.this.lastTab);
                tabView2.setBackgroundResource(((Integer) GsMainTabHostActivity.this.tabClickedIcon.get(GsMainTabHostActivity.this.tabSpecTag[GsMainTabHostActivity.this.lastTab])).intValue());
            }
        });
        this.moreMenu.setBackgroundDrawable(new BitmapDrawable());
        this.moreMenu.setTouchInterceptor(new View.OnTouchListener() { // from class: mktvsmart.screen.GsMainTabHostActivity.6
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != 4) {
                    return false;
                }
                GsMainTabHostActivity.this.moreMenu.dismiss();
                return true;
            }
        });
        if (!this.moreMenu.isShowing() && this.activityFlag) {
            this.moreMenu.showAsDropDown(parent, -this.moreMenu.getWidth(), parent.getTop() + this.moreMenu.getHeight() + 5);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (((Integer) v.getTag()).intValue()) {
            case 0:
                this.moreMenu.dismiss();
                LayoutInflater inflater = LayoutInflater.from(this);
                View layout = inflater.inflate(R.layout.pop_window_list_layout, (ViewGroup) null);
                TextView popWindowTitle = (TextView) layout.findViewById(R.id.pop_window_title);
                popWindowTitle.setText(R.string.sat_info_title);
                ListView satInfoList = (ListView) layout.findViewById(R.id.pop_window_list);
                Button cancelBtn = (Button) layout.findViewById(R.id.pop_window_cancel_btn);
                List<DataConvertChannelModel> channelList = ChannelData.getInstance().getChannelListByTvRadioType();
                this.satList = ChannelData.getSatList(ChannelData.getInstance().getmAllSatList(), channelList, getResources().getString(R.string.All_Satellites));
                this.mSatListAdapter = new SatListAdapter(layout.getContext(), this.satList);
                this.mSatListAdapter.setTextColor(getResources().getColor(R.color.white));
                satInfoList.setAdapter((ListAdapter) this.mSatListAdapter);
                cancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsMainTabHostActivity.7
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        GsMainTabHostActivity.this.mSatInfoDialog.dismiss();
                    }
                });
                this.mSatInfoDialog = new Dialog(getParent(), R.style.dialog);
                this.mSatInfoDialog.setContentView(layout);
                if (!this.mSatInfoDialog.isShowing()) {
                    this.mSatInfoDialog.show();
                }
                satInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.GsMainTabHostActivity.8
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) throws UnsupportedEncodingException {
                        List<DataConvertSatModel> satModels = new ArrayList<>();
                        DataConvertSatModel model = new DataConvertSatModel();
                        GMScreenGlobalInfo.setmSatIndexSelected(((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatIndex());
                        model.setmSatIndex(((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatIndex());
                        satModels.add(model);
                        DataParser parser = ParserFactory.getParser();
                        try {
                            byte[] dataBuff = parser.serialize(satModels, GlobalConstantValue.GMS_MSG_DO_SAT_SELECTED_CHANGE).getBytes("UTF-8");
                            GsSendSocket.sendSocketToStb(dataBuff, GsMainTabHostActivity.this.tcpSocket, 0, dataBuff.length, GlobalConstantValue.GMS_MSG_DO_SAT_SELECTED_CHANGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        GsMainTabHostActivity.this.mSatInfoDialog.dismiss();
                    }
                });
                break;
            case 1:
                Intent intent = new Intent(this, (Class<?>) GsPvr2SmallActivity.class);
                startActivity(intent);
                break;
            case 2:
                Intent intent2 = new Intent(this, (Class<?>) GsParentControlActivity.class);
                startActivity(intent2);
                break;
            case 3:
                Intent intent3 = new Intent(this, (Class<?>) GsDebugActivity.class);
                startActivity(intent3);
                break;
            case 4:
                Intent intent4 = new Intent(this, (Class<?>) GsStbInfoActivity.class);
                startActivity(intent4);
                break;
            case 5:
                Intent intent5 = new Intent(this, (Class<?>) GsFileBroswerActivity.class);
                startActivity(intent5);
                break;
            case 6:
                Intent intent6 = new Intent(this, (Class<?>) GsGSettingActivity.class);
                startActivity(intent6);
                break;
            case 7:
                Intent intent7 = new Intent(this, (Class<?>) GsSpectrumActivity.class);
                startActivity(intent7);
                break;
        }
    }

    @Override // android.app.ActivityGroup, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.activityFlag = true;
    }

    @Override // android.app.ActivityGroup, android.app.Activity
    protected void onPause() {
        super.onPause();
        this.activityFlag = false;
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OnTabActivityResultListener onTabAactivityResultListener = (OnTabActivityResultListener) getLocalActivityManager().getCurrentActivity();
        onTabAactivityResultListener.onTabActivityResult(requestCode, resultCode, data);
    }

    private class SatListAdapter extends BaseAdapter {
        private int commonTextColor;
        LayoutInflater inflater;
        private List<DataConvertSatModel> mSatModel;

        public SatListAdapter(Context context, List<DataConvertSatModel> satModel) {
            this.inflater = LayoutInflater.from(context);
            this.mSatModel = satModel;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return GsMainTabHostActivity.this.satList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return ((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatName();
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.pop_window_item_layout, (ViewGroup) null);
            }
            RelativeLayout relativeSatName = (RelativeLayout) convertView.findViewById(R.id.pop_window_list_name);
            ImageView satSelect = (ImageView) convertView.findViewById(R.id.list_select);
            TextView satName = (TextView) convertView.findViewById(R.id.list_name);
            if (((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatIndex() != GMScreenGlobalInfo.getIndexOfAllSat()) {
                if (((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatDir() == 0) {
                    satName.setText(String.valueOf(((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatName()) + "(" + (((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatAngle() / 10) + "." + (((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatAngle() % 10) + "E)");
                } else {
                    satName.setText(String.valueOf(((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatName()) + "(" + (((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatAngle() / 10) + "." + (((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatAngle() % 10) + "W)");
                }
            } else {
                satName.setText(((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatName());
            }
            relativeSatName.setBackgroundResource(R.drawable.disp_channel);
            if (((DataConvertSatModel) GsMainTabHostActivity.this.satList.get(position)).getmSatIndex() == GMScreenGlobalInfo.getmSatIndexSelected()) {
                satSelect.setImageResource(R.drawable.list_selected);
            } else {
                satSelect.setImageResource(R.drawable.list_unselected);
            }
            return convertView;
        }

        public void setTextColor(int color) {
            setCommonTextColor(color);
        }

        public void setCommonTextColor(int commonTextColor) {
            this.commonTextColor = commonTextColor;
        }
    }

    public void setNavigationVisible(boolean visible) {
        if (visible) {
            findViewById(android.R.id.tabs).setVisibility(0);
        } else {
            findViewById(android.R.id.tabs).setVisibility(8);
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("GsMainTabHostActivity", "onConfigurationChanged");
    }
}
