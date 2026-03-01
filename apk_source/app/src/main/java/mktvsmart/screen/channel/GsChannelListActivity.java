package mktvsmart.screen.channel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import com.alitech.dvbtoip.DVBtoIP;
import com.google.android.gms.games.GamesStatusCodes;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.voicetechnology.rtspclient.test.Sat2IP_Rtsp;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.CommonCofirmDialog;
import mktvsmart.screen.CommonErrorDialog;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.EditPhoneAndSmsRemindSettingFile;
import mktvsmart.screen.FindPlayerAndPlayChannel;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsEPGMenuActivity;
import mktvsmart.screen.GsEPGTableChannel;
import mktvsmart.screen.GsEditFavorMenu;
import mktvsmart.screen.GsLoginListActivity;
import mktvsmart.screen.GsMobileLoginInfo;
import mktvsmart.screen.GsSendSocket;
import mktvsmart.screen.GsSession;
import mktvsmart.screen.OnTabActivityResultListener;
import mktvsmart.screen.R;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertChannelTypeModel;
import mktvsmart.screen.dataconvert.model.DataConvertControlModel;
import mktvsmart.screen.dataconvert.model.DataConvertEditChannelLockModel;
import mktvsmart.screen.dataconvert.model.DataConvertFavorModel;
import mktvsmart.screen.dataconvert.model.DataConvertOneDataModel;
import mktvsmart.screen.dataconvert.model.DataConvertSortModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.exception.ProgramNotFoundException;
import mktvsmart.screen.gchat.ui.GChatActivity;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;
import mktvsmart.screen.view.ListviewAdapter;
import mktvsmart.screen.vlc.LivePlayActivity;

/* loaded from: classes.dex */
public class GsChannelListActivity extends Activity implements OnTabActivityResultListener {
    private static final int ALL_ADJACENT_MATCH_CHANNEL_NAME_PRIORITY = -1;
    private static final int MATCH_CHANNEL_DISP_INDEX_PRIORITY = -2;
    private static final int NO_ADJACENT_MATCH_CHANNEL_NAME_PRIORITY = 1;
    private static final int SEARCH_BY_DISP_INDEX_AND_PROG_NAME = 0;
    private static final int SEARCH_BY_PROG_NAME = 1;
    private static Sat2IP_Rtsp sRtsp;
    private ListView ChannelListView;
    private Button DoneBtn;
    private String NetAddress;
    private int NetPort;
    private Button TypeSwitch;
    private ImageView allSelectedBtn;
    private LinearLayout allSelectedBtnLayout;
    AlertDialog.Builder build;
    private SparseIntArray channelListTypeMap;
    private ImageView channelTypeArrow;
    private PopupWindow channelTypePopupWindow;
    CheckBox childFav;
    CheckBox cultureFav;
    private byte[] dataBuff;
    Dialog deleteChannelDialog;
    private Button editBtn;
    private ImageView editDeleteIcon;
    private LinearLayout editDeleteMenu;
    private TextView editDeleteText;
    private ImageView editFavorIcon;
    private LinearLayout editFavorMenu;
    private TextView editFavorText;
    private ImageView editLockIcon;
    private LinearLayout editLockMenu;
    private TextView editLockText;
    private LinearLayout editMenu;
    private ImageView editMoveIcon;
    private LinearLayout editMoveMenu;
    private TextView editMoveText;
    private ImageView editRenameIcon;
    private LinearLayout editRenameMenu;
    private TextView editRenameText;
    CheckBox eduaFav;
    private String epg_program_name;
    private String epg_program_sat_tp_id;
    InputStream in;
    private ChannelData mChannelData;
    private ImageView mClearSearchKeyword;
    private ImageView mEditSortIcon;
    private LinearLayout mEditSortMenu;
    private TextView mEditSortText;
    private Dialog mFavorRenameDialog;
    private boolean[] mIsChoice;
    private Intent mPlayIntent;
    private ContentResolver mResolver;
    private Button mSearchCancelBtn;
    private EditText mSearchChannelEdit;
    private LinearLayout mSearchChannelLayout;
    private LinearLayout mSearchFailedPrompt;
    private Dialog mSortTypePopupWindow;
    private Uri mUri;
    private Handler mainHandler;
    CheckBox moviesFav;
    private MessageProcessor msgProc;
    CheckBox musFav;
    CheckBox newsFav;
    private DataParser parser;
    Dialog pswInputDialog;
    Dialog renameInputDialog;
    CheckBox sportsFav;
    private TabHost tabHost;
    private TabWidget tabWidget;
    private Socket tcpSocket;
    private TextView titleText;
    private int visibleItemCount;
    private ADSProgressDialog waitDialog;
    CheckBox weatherFav;
    private static final Collection<String> targetApplications = list(GMScreenGlobalInfo.DEFAULT_EXTERNAL_PLAYER);
    public static boolean enable_edit = false;
    private final int M_DOUBLE_CLICK_TIME_INTERVAL = 1000;
    private final int M_FIRST_CLICK = 1;
    private final int M_SECOND_CLICK = 2;
    private final double M_EDIT_SEARCH_FRAGMENT_WIDTH_PROPORTION = 0.72d;
    private final double M_SEARCH_CANCEL_LAYOUT_WIDTH_PROPORTION = 0.18d;
    private final double M_SEARCH_FRAGMENT_WIDTH_PROPORTION = 0.9d;
    private final double M_BIG_SEPARATION_DISTANCE = 0.05d;
    private final double M_SMALL_SEPARATION_DISTANCE = 0.03d;
    private String mSearchChannelKeywords = "";
    private list_single_button_adapter channelListAdapter = null;
    private ListviewAdapter typeAdapter = null;
    private List<DataConvertChannelModel> favorModels = null;
    private List<DataConvertControlModel> controlModels = null;
    private List<DataConvertChannelModel> mCurrentChannelList = null;
    private List<DataConvertChannelModel> mOriginalChannelListModels = null;
    private List<DataConvertChannelModel> mSearchChannelListModels = null;
    private int currentChannelListType = 0;
    private int preChannelListType = 0;
    private int expandPosition = -1;
    int favMark = 0;
    int channelTypeFavMark = 0;
    boolean isFavorChange = true;
    boolean isSat2ipStarted = false;
    private String currentSat2ipChannelProgramId = "";
    int loop = 0;
    int sat2ipPlayPosition = 0;
    private int lastItem = 0;
    private boolean isEnableMove = false;
    private int longClickPos = -1;
    private boolean allSelectedBtn_selected = false;
    private InputMethodManager inputManager = null;
    private boolean repeatPassword = false;
    private final int editLockChannel = 1;
    private final int playingLockChannel = 2;
    private final int mobilePlayLockChannel = 3;
    private int passwordType = 0;
    private boolean isInForeground = false;
    private sort_adapter mSortAdapter = null;
    private boolean mGetChannelListWhenLogin = true;
    private int mFirstVisibleChannelIdex = 0;
    private int mLastVisibleChannelIndex = 0;
    private int mSearchMode = 0;
    private boolean mEnterSearchFlag = false;
    private boolean mChannelListChangeFlag = false;
    private grid_adapter mFavGridAdater = null;
    private int[] mFavValueArray = {1, 2, 4, 8, 16, 32, 64, 128};
    private int mFavValue = 0;
    private final int PLAY_TIMEOUT = 2;
    private final int PLAY_WITH_OTHER_PLAYER = 3;
    private final int PLAY_FAILURE = 4;
    private boolean bPlayWithOherPlayer = false;
    private int mFirstChannelListClickTime = 0;
    private int mSecChannelListClickTime = 0;
    private int mChannelListClickCount = 0;
    private MyPhoneStateListener mPhoneListener = new MyPhoneStateListener(this, null);
    private SMSContentObserver mSMSContentObserver = new SMSContentObserver();
    private Handler.Callback mMsgHandle = new Handler.Callback() { // from class: mktvsmart.screen.channel.GsChannelListActivity.1
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) throws Resources.NotFoundException, UnsupportedEncodingException {
            switch (msg.what) {
                case 2:
                    GsChannelListActivity.this.stopStream();
                    Toast.makeText(GsChannelListActivity.this, R.string.str_load_data_fail, 0).show();
                    return true;
                case 3:
                    if (GsChannelListActivity.this.waitDialog.isShowing()) {
                        GsChannelListActivity.this.waitDialog.dismiss();
                    }
                    try {
                        GsChannelListActivity.this.startActivity((Intent) msg.obj);
                    } catch (ActivityNotFoundException e) {
                        System.out.println("Player activity not found");
                        GsChannelListActivity.this.stopStream();
                        GsChannelListActivity.this.mainHandler.sendMessage(GsChannelListActivity.this.mainHandler.obtainMessage(4, "Player not found"));
                    }
                    Toast toast = Toast.makeText(GsChannelListActivity.this.getParent(), R.string.waiting_to_play_stream_hint, 1);
                    toast.show();
                    GsChannelListActivity.this.isSat2ipStarted = true;
                    return true;
                case 4:
                    if (GsChannelListActivity.this.waitDialog.isShowing()) {
                        GsChannelListActivity.this.waitDialog.dismiss();
                    }
                    CommonErrorDialog errorDialog = new CommonErrorDialog(GsChannelListActivity.this.getParent());
                    errorDialog.setmContent((String) msg.obj);
                    errorDialog.show();
                    return true;
                default:
                    return true;
            }
        }
    };
    Runnable timeOutRun = new Runnable() { // from class: mktvsmart.screen.channel.GsChannelListActivity.2
        @Override // java.lang.Runnable
        public void run() {
            GsChannelListActivity.this.mainHandler.sendEmptyMessage(2);
        }
    };
    private CommonCofirmDialog.OnButtonClickListener mDownDialogOnClickListener = new CommonCofirmDialog.OnButtonClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.3
        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedConfirm() {
            Uri uri = Uri.parse("market://details?id=com.mxtech.videoplayer.ad");
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            try {
                GsChannelListActivity.this.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedCancel() {
        }
    };
    private FindPlayerAndPlayChannel.PlayByDesignatedPlayer mPlayByDesignatedPlayer = new FindPlayerAndPlayChannel.PlayByDesignatedPlayer() { // from class: mktvsmart.screen.channel.GsChannelListActivity.4
        @Override // mktvsmart.screen.FindPlayerAndPlayChannel.PlayByDesignatedPlayer
        public void designatedBuiltInPlayer(int position) {
            GsChannelListActivity.this.otherPlatformPlay(position);
        }

        @Override // mktvsmart.screen.FindPlayerAndPlayChannel.PlayByDesignatedPlayer
        public void designatedExternalPlayer(int position, Intent intent) {
            GsChannelListActivity.this.startPlayStream(position, intent);
        }

        @Override // mktvsmart.screen.FindPlayerAndPlayChannel.PlayByDesignatedPlayer
        public void playerNotExist() {
            CommonCofirmDialog showDownDialog = new CommonCofirmDialog(GsChannelListActivity.this.getParent());
            showDownDialog.setmTitle(GsChannelListActivity.this.getResources().getString(R.string.install_mx_package_dialog_title));
            showDownDialog.setmContent(GsChannelListActivity.this.getResources().getString(R.string.install_mx_package_dialog_message));
            showDownDialog.setOnButtonClickListener(GsChannelListActivity.this.mDownDialogOnClickListener);
            showDownDialog.show();
        }
    };
    private MessageProcessor.PerformOnBackground post = new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.5
        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) throws UnsupportedEncodingException {
            int responseStyle = 9999;
            switch (msg.what) {
                case 2001:
                    responseStyle = 3;
                    break;
                case 2004:
                    responseStyle = 17;
                    break;
                case 2009:
                    responseStyle = 23;
                    break;
                case 2013:
                    responseStyle = 12;
                    break;
                case 2019:
                    responseStyle = 22;
                    break;
            }
            GsSendSocket.sendOnlyCommandSocketToStb(GsChannelListActivity.this.tcpSocket, responseStyle);
        }
    };
    private MessageProcessor.PerformOnForeground requestAllChannelWhenSTBChannelListChanged = new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.6
        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            switch (msg.what) {
                case 2025:
                    if (GMScreenGlobalInfo.isSdsOpen() != 1) {
                        GMScreenGlobalInfo.setSdsOpen(1);
                        break;
                    } else {
                        return;
                    }
                case 2026:
                    if (GMScreenGlobalInfo.isSdsOpen() != 0) {
                        GMScreenGlobalInfo.setSdsOpen(0);
                        break;
                    } else {
                        return;
                    }
            }
            GsChannelListActivity.this.mChannelData.clearTVRadioProgramList();
            GsChannelListActivity.this.requestProgramListFromTo(0, GMScreenGlobalInfo.getMaxProgramNumPerRequest() - 1);
        }
    };
    private CommonCofirmDialog.OnButtonClickListener mStbInStandbyOnClickListener = new CommonCofirmDialog.OnButtonClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.7
        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedConfirm() throws UnsupportedEncodingException {
            GsSendSocket.sendOnlyCommandSocketToStb(GsChannelListActivity.this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_POWER_SWITCH);
        }

        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedCancel() {
        }
    };
    private CommonCofirmDialog.OnButtonClickListener mDeleteMenuOnClickListener = new CommonCofirmDialog.OnButtonClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.8
        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedConfirm() throws SocketException, UnsupportedEncodingException {
            try {
                List<DataConvertChannelModel> deleteModels = new ArrayList<>();
                if (GsChannelListActivity.enable_edit || GsChannelListActivity.this.expandPosition == -1) {
                    for (DataConvertChannelModel model : GsChannelListActivity.this.mCurrentChannelList) {
                        if (model.getSelectedFlag()) {
                            deleteModels.add(model);
                        }
                    }
                } else {
                    deleteModels.add((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(GsChannelListActivity.this.expandPosition));
                }
                byte[] data_buff = GsChannelListActivity.this.parser.serialize(deleteModels, 1002).getBytes("UTF-8");
                GsChannelListActivity.this.tcpSocket.setSoTimeout(3000);
                GsSendSocket.sendSocketToStb(data_buff, GsChannelListActivity.this.tcpSocket, 0, data_buff.length, 1002);
                GsChannelListActivity.this.initItemChecked();
                GsChannelListActivity.this.allSelectedBtn.setImageResource(R.drawable.none_selected);
                GsChannelListActivity.this.allSelectedBtn_selected = false;
                GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (GsChannelListActivity.this.waitDialog.isShowing()) {
                GsChannelListActivity.this.waitDialog.dismiss();
            }
            GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog(GsChannelListActivity.this.getParent(), R.string.deleting_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_operate_fail);
            GsChannelListActivity.this.allSelectedBtn.setImageResource(R.drawable.none_selected);
            GsChannelListActivity.this.allSelectedBtn_selected = false;
        }

        @Override // mktvsmart.screen.CommonCofirmDialog.OnButtonClickListener
        public void onClickedCancel() {
        }
    };
    private View.OnClickListener mRenameMenuOnClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.9
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            DataConvertChannelModel renameModel = null;
            if (GsChannelListActivity.enable_edit || GsChannelListActivity.this.expandPosition == -1) {
                Iterator it = GsChannelListActivity.this.mCurrentChannelList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    DataConvertChannelModel model = (DataConvertChannelModel) it.next();
                    if (model.getSelectedFlag()) {
                        renameModel = model;
                        break;
                    }
                }
            } else {
                renameModel = (DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(GsChannelListActivity.this.expandPosition);
            }
            LayoutInflater inflater = LayoutInflater.from(GsChannelListActivity.this.findViewById(R.id.prog_name).getRootView().getContext());
            LinearLayout renameLayout = (LinearLayout) inflater.inflate(R.layout.input_rename_dialog, (ViewGroup) null);
            final EditText inputName = (EditText) renameLayout.findViewById(R.id.input_name_edittext);
            Button renameSaveBtn = (Button) renameLayout.findViewById(R.id.input_name_confirm_btn);
            Button renameCancelBtn = (Button) renameLayout.findViewById(R.id.input_name_cancel_btn);
            inputName.setText(renameModel.getProgramName());
            Selection.selectAll(inputName.getText());
            final DataConvertChannelModel channelModel = renameModel;
            renameSaveBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.9.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v2) throws SocketException, UnsupportedEncodingException {
                    try {
                        DataConvertChannelModel renameModel2 = channelModel;
                        renameModel2.setProgramName(inputName.getText().toString());
                        List<DataConvertChannelModel> channelList = new ArrayList<>();
                        channelList.add(renameModel2);
                        byte[] data_buff = GsChannelListActivity.this.parser.serialize(channelList, 1001).getBytes("UTF-8");
                        GsChannelListActivity.this.tcpSocket.setSoTimeout(3000);
                        GsSendSocket.sendSocketToStb(data_buff, GsChannelListActivity.this.tcpSocket, 0, data_buff.length, 1001);
                        GsChannelListActivity.this.initItemChecked();
                        GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GsChannelListActivity.this.inputManager.hideSoftInputFromWindow(inputName.getWindowToken(), 0);
                    GsChannelListActivity.this.renameInputDialog.dismiss();
                }
            });
            renameCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.9.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v2) {
                    GsChannelListActivity.this.inputManager.hideSoftInputFromWindow(inputName.getWindowToken(), 0);
                    GsChannelListActivity.this.renameInputDialog.dismiss();
                }
            });
            GsChannelListActivity.this.renameInputDialog = new Dialog(GsChannelListActivity.this.findViewById(R.id.prog_index).getRootView().getContext(), R.style.dialog);
            GsChannelListActivity.this.renameInputDialog.setContentView(renameLayout);
            GsChannelListActivity.this.renameInputDialog.setCanceledOnTouchOutside(false);
            GsChannelListActivity.this.renameInputDialog.show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() { // from class: mktvsmart.screen.channel.GsChannelListActivity.9.3
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    GsChannelListActivity.this.inputManager = (InputMethodManager) inputName.getContext().getSystemService("input_method");
                    GsChannelListActivity.this.inputManager.showSoftInput(inputName, 0);
                }
            }, 200L);
        }
    };

    private static Collection<String> list(String... values) {
        return Collections.unmodifiableCollection(Arrays.asList(values));
    }

    private void disableSomeFunctionWhenSlave() {
        this.editBtn.setEnabled(false);
        this.TypeSwitch.setEnabled(false);
        this.titleText.setClickable(false);
        this.channelTypeArrow.setVisibility(4);
        this.ChannelListView.setLongClickable(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableSomeFucitonWhenBecomeMaster() {
        this.editBtn.setEnabled(true);
        this.TypeSwitch.setEnabled(true);
        this.titleText.setClickable(true);
        this.channelTypeArrow.setVisibility(0);
        this.ChannelListView.setLongClickable(true);
        this.channelListAdapter = new list_single_button_adapter(this);
        this.ChannelListView.setAdapter((ListAdapter) this.channelListAdapter);
        this.channelListAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentChannelListDispIndex() {
        for (int i = 0; i < this.mCurrentChannelList.size(); i++) {
            this.mCurrentChannelList.get(i).setCurrentChannelListDispIndex(i + 1);
        }
    }

    private int calculateChannelInsertPosition(DataConvertChannelModel needInsertChannel, List<DataConvertChannelModel> matchChannelListModels) {
        int position = 0;
        for (int i = 0; i < matchChannelListModels.size(); i++) {
            switch (needInsertChannel.getSearchChannelSortPriority()) {
                case -2:
                    if (-2 == matchChannelListModels.get(i).getSearchChannelSortPriority()) {
                        position = i + 1;
                        break;
                    } else {
                        int i2 = position;
                        return i2;
                    }
                case -1:
                    if (-2 == matchChannelListModels.get(i).getSearchChannelSortPriority()) {
                        position++;
                        break;
                    } else if (-1 == matchChannelListModels.get(i).getSearchChannelSortPriority()) {
                        if (needInsertChannel.getMatchChannelNameIndexArray()[0] >= matchChannelListModels.get(i).getMatchChannelNameIndexArray()[0]) {
                            position = i + 1;
                            break;
                        } else {
                            return i;
                        }
                    } else {
                        int i3 = position;
                        return i3;
                    }
                case 0:
                default:
                    if (-2 == matchChannelListModels.get(i).getSearchChannelSortPriority() || -1 == matchChannelListModels.get(i).getSearchChannelSortPriority()) {
                        position++;
                        break;
                    } else if (needInsertChannel.getSearchChannelSortPriority() <= matchChannelListModels.get(i).getSearchChannelSortPriority()) {
                        if (needInsertChannel.getSearchChannelSortPriority() == matchChannelListModels.get(i).getSearchChannelSortPriority()) {
                            if (needInsertChannel.getMatchChannelNameIndexArray()[0] >= matchChannelListModels.get(i).getMatchChannelNameIndexArray()[0]) {
                                position++;
                                break;
                            } else {
                                int i4 = position;
                                return i4;
                            }
                        } else {
                            position++;
                            break;
                        }
                    } else {
                        int i5 = position;
                        return i5;
                    }
                case 1:
                    DataConvertChannelModel model = matchChannelListModels.get((matchChannelListModels.size() - i) - 1);
                    if (1 == model.getSearchChannelSortPriority()) {
                        int j = 0;
                        while (true) {
                            if (j < needInsertChannel.getMatchChannelNameIndexArray().length) {
                                if (needInsertChannel.getMatchChannelNameIndexArray()[j] > model.getMatchChannelNameIndexArray()[j]) {
                                    int i6 = matchChannelListModels.size() - position;
                                    return i6;
                                }
                                if (needInsertChannel.getMatchChannelNameIndexArray()[j] >= model.getMatchChannelNameIndexArray()[j]) {
                                    j++;
                                } else {
                                    position++;
                                    if (i == matchChannelListModels.size() - 1) {
                                        int i7 = matchChannelListModels.size() - position;
                                        return i7;
                                    }
                                }
                            }
                        }
                        if (j == needInsertChannel.getMatchChannelNameIndexArray().length) {
                            int i8 = matchChannelListModels.size() - position;
                            return i8;
                        }
                        break;
                    } else {
                        int i9 = matchChannelListModels.size() - position;
                        return i9;
                    }
            }
        }
        int i10 = position;
        return i10;
    }

    private int getSearchChannelSortPriority(int[] matchCharacterIndex) {
        int[] adjacentNumArray = new int[matchCharacterIndex.length - 1];
        int adjacentNumMaxIndex = 0;
        int j = 0;
        for (int i = 0; i < matchCharacterIndex.length - 1; i++) {
            if (matchCharacterIndex[i] == matchCharacterIndex[i + 1] - 1) {
                adjacentNumArray[j] = adjacentNumArray[j] + 1;
            } else {
                j++;
            }
        }
        int i2 = 0;
        while (i2 < adjacentNumArray.length - 1) {
            if (adjacentNumArray[adjacentNumMaxIndex] < adjacentNumArray[i2 + 1]) {
                adjacentNumMaxIndex = i2 + 1;
                i2 = adjacentNumMaxIndex;
            } else {
                i2++;
            }
        }
        return adjacentNumArray[adjacentNumMaxIndex] + 1;
    }

    private boolean isChannelDispIndexMatched(String dispIndexString, String inputTextString) {
        if (inputTextString.length() > dispIndexString.length()) {
            return false;
        }
        for (int i = 0; i < inputTextString.length(); i++) {
            if (inputTextString.charAt(i) != dispIndexString.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasMatchStringIndexFound(String channelName, String inputTextString, int[] matchCharacterIndex) {
        boolean matchFlag = false;
        int preMatchIndex = 0;
        for (int i = 0; i < inputTextString.length(); i++) {
            int j = preMatchIndex;
            while (true) {
                if (j >= channelName.length()) {
                    break;
                }
                if ('0' <= inputTextString.charAt(i) && '9' >= inputTextString.charAt(i)) {
                    if (inputTextString.charAt(i) != channelName.charAt(j)) {
                        j++;
                    } else {
                        matchCharacterIndex[i] = j;
                        preMatchIndex = j + 1;
                        matchFlag = true;
                        break;
                    }
                } else {
                    if (inputTextString.charAt(i) == channelName.charAt(j) || ((char) (inputTextString.charAt(i) - ' ')) == channelName.charAt(j) || ((char) (inputTextString.charAt(i) + ' ')) == channelName.charAt(j)) {
                        break;
                    }
                    j++;
                }
            }
            matchCharacterIndex[i] = j;
            preMatchIndex = j + 1;
            matchFlag = true;
            if (!matchFlag) {
                return false;
            }
            matchFlag = false;
        }
        return true;
    }

    private void displayOrDimissLayout(LinearLayout linearLayout, ImageView imageView, int viewLinearState, int viewImageState) {
        if (linearLayout != null) {
            linearLayout.setVisibility(viewLinearState);
        }
        if (imageView != null) {
            imageView.setVisibility(viewImageState);
        }
        this.channelListAdapter.notifyDataSetChanged();
    }

    private void judgeSearchMode(int inputTextLength, String inputStr) throws NumberFormatException {
        for (int i = 0; i < inputTextLength; i++) {
            if ('0' > inputStr.charAt(i) || '9' < inputStr.charAt(i)) {
                this.mSearchMode = 1;
                return;
            }
        }
        int index = Integer.parseInt(inputStr);
        if (index > this.mOriginalChannelListModels.size() || index == 0) {
            this.mSearchMode = 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void findChannel() throws NumberFormatException {
        List<DataConvertChannelModel> matchChannelListModels = new ArrayList<>();
        String inputStr = this.mSearchChannelEdit.getText().toString();
        this.mSearchMode = 0;
        int inputTextLength = inputStr.length();
        if (!inputStr.equals(this.mSearchChannelKeywords) || this.mChannelListChangeFlag) {
            if (inputTextLength == 0) {
                this.mSearchChannelKeywords = "";
                this.mCurrentChannelList = this.mOriginalChannelListModels;
                this.mSearchChannelListModels = this.mCurrentChannelList;
                displayOrDimissLayout(this.mSearchFailedPrompt, this.mClearSearchKeyword, 8, 8);
                adjustSelectionOfChannelListView(true);
                return;
            }
            if (this.mSearchChannelKeywords.length() >= inputTextLength) {
                this.mSearchChannelListModels = this.mOriginalChannelListModels;
            }
            if (this.mSearchChannelListModels.size() == 0) {
                displayOrDimissLayout(this.mSearchFailedPrompt, this.mClearSearchKeyword, 0, 0);
                return;
            }
            this.mSearchChannelKeywords = inputStr;
            displayOrDimissLayout(null, this.mClearSearchKeyword, 0, 0);
            judgeSearchMode(inputTextLength, inputStr);
            for (DataConvertChannelModel model : this.mSearchChannelListModels) {
                boolean matchFlag = false;
                int[] matchCharacterIndex = new int[inputTextLength];
                String dispIndexString = new StringBuilder(String.valueOf(model.getCurrentChannelListDispIndex())).toString();
                if (this.mSearchMode == 0 && (matchFlag = isChannelDispIndexMatched(dispIndexString, inputStr))) {
                    model.setSearchChannelSortPriority(-2);
                }
                if (!matchFlag) {
                    int allMatchIndex = model.getProgramName().toUpperCase().indexOf(inputStr.toUpperCase());
                    if (-1 != allMatchIndex) {
                        matchFlag = true;
                        matchCharacterIndex[0] = allMatchIndex;
                        model.setMatchChannelNameIndexArray(matchCharacterIndex);
                        model.setSearchChannelSortPriority(-1);
                    }
                    if (!matchFlag && 1 != inputTextLength && (matchFlag = hasMatchStringIndexFound(model.getProgramName(), inputStr, matchCharacterIndex))) {
                        model.setMatchChannelNameIndexArray(matchCharacterIndex);
                        int priority = getSearchChannelSortPriority(matchCharacterIndex);
                        model.setSearchChannelSortPriority(priority);
                    }
                }
                if (matchFlag) {
                    int location = calculateChannelInsertPosition(model, matchChannelListModels);
                    matchChannelListModels.add(location, model);
                }
            }
            this.mSearchChannelListModels = matchChannelListModels;
            this.mCurrentChannelList = this.mSearchChannelListModels;
            if (this.mCurrentChannelList.isEmpty()) {
                displayOrDimissLayout(this.mSearchFailedPrompt, null, 0, 0);
            } else {
                displayOrDimissLayout(this.mSearchFailedPrompt, null, 8, 0);
                this.ChannelListView.setSelection(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int clickPosition2ListType(int position) {
        int tempMark = this.channelTypeFavMark;
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 30:
            case 31:
            case 32:
            case 71:
            case 72:
            case 74:
                int listType = this.channelListTypeMap.get(position, 0);
                return listType;
            default:
                if (DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type() == 0) {
                    if (position >= 0 && position <= 3) {
                        return position;
                    }
                    if (position < 4) {
                        return -1;
                    }
                    int selectPosition = position - 3;
                    int listType2 = 3;
                    while (selectPosition > 0) {
                        if ((tempMark & 1) > 0) {
                            selectPosition--;
                        }
                        tempMark >>>= 1;
                        listType2++;
                    }
                    return listType2;
                }
                if (position >= 0 && position < 3) {
                    return position;
                }
                if (position < 3) {
                    return -1;
                }
                int selectPosition2 = position - 2;
                int listType3 = 3;
                while (selectPosition2 > 0) {
                    if ((tempMark & 1) > 0) {
                        selectPosition2--;
                    }
                    tempMark >>>= 1;
                    listType3++;
                }
                return listType3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ChangeChannelListType(int channelListType, boolean needForceChange) throws NumberFormatException {
        List<DataConvertChannelModel> tempList;
        if ((channelListType != this.preChannelListType || needForceChange) && (tempList = this.mChannelData.getChannelListByProgramType(this.mChannelData.getChannelListByTvRadioType(), channelListType)) != null) {
            this.mCurrentChannelList = tempList;
            setCurrentChannelListDispIndex();
            this.mOriginalChannelListModels = this.mCurrentChannelList;
            if (this.mEnterSearchFlag) {
                this.mChannelListChangeFlag = true;
                findChannel();
            } else {
                if (this.ChannelListView.getAdapter() != null && (this.ChannelListView.getAdapter() instanceof list_single_button_adapter)) {
                    ((list_single_button_adapter) this.ChannelListView.getAdapter()).notifyDataSetChanged();
                }
                this.ChannelListView.post(new Runnable() { // from class: mktvsmart.screen.channel.GsChannelListActivity.10
                    @Override // java.lang.Runnable
                    public void run() {
                        GsChannelListActivity.this.adjustSelectionOfChannelListView(true);
                    }
                });
            }
            switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                case 30:
                case 31:
                case 32:
                case 71:
                case 72:
                case 74:
                    if (channelListType >= 0 && channelListType <= 3) {
                        this.titleText.setText(getResources().getStringArray(R.array.channel_list_type_string)[channelListType]);
                        break;
                    } else if (channelListType >= 4) {
                        for (DataConvertFavorModel favGroup : GMScreenGlobalInfo.favGroups) {
                            if (favGroup.getFavorTypeID() == channelListType - 4) {
                                this.titleText.setText(favGroup.GetFavorName());
                            }
                        }
                        break;
                    }
                    break;
                default:
                    if (channelListType >= 0 && channelListType <= 3) {
                        this.titleText.setText(getResources().getStringArray(R.array.channel_list_type_string)[channelListType]);
                        break;
                    } else if (channelListType >= 4 && channelListType <= 11) {
                        this.titleText.setText(GMScreenGlobalInfo.favType.get(channelListType - 4));
                        break;
                    }
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void LoadData() {
        this.ChannelListView.setSelection((this.lastItem - this.visibleItemCount) + 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int GetSelectedNum(List<DataConvertChannelModel> models) {
        int sum = 0;
        for (DataConvertChannelModel model : models) {
            if (model.getSelectedFlag()) {
                sum++;
            }
        }
        return sum;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exitEditMode() throws NumberFormatException {
        this.editMenu.setVisibility(8);
        this.tabWidget.setVisibility(0);
        this.allSelectedBtn.setVisibility(8);
        this.ChannelListView.setLongClickable(true);
        enable_edit = false;
        this.isEnableMove = false;
        ChangeChannelListType(this.preChannelListType, true);
        this.currentChannelListType = this.preChannelListType;
        this.titleText.setClickable(true);
        this.channelTypeArrow.setVisibility(0);
        this.editBtn.setVisibility(0);
        this.DoneBtn.setVisibility(8);
        this.TypeSwitch.setVisibility(0);
        initItemChecked();
        this.channelListAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterEditMode() throws NumberFormatException {
        this.editMenu.setVisibility(0);
        this.tabWidget.setVisibility(8);
        this.allSelectedBtn.setImageResource(R.drawable.none_selected);
        this.allSelectedBtn.setVisibility(0);
        this.ChannelListView.setLongClickable(false);
        enable_edit = true;
        this.preChannelListType = this.currentChannelListType;
        ChangeChannelListType(0, false);
        this.currentChannelListType = 0;
        this.titleText.setClickable(false);
        this.channelTypeArrow.setVisibility(4);
        this.allSelectedBtn_selected = false;
        this.editBtn.setVisibility(8);
        this.DoneBtn.setVisibility(0);
        this.TypeSwitch.setVisibility(8);
        this.channelListAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> getChannelTypeData() throws Resources.NotFoundException {
        int loop;
        ArrayList<String> data = new ArrayList<>();
        String[] channelTypeStr = getResources().getStringArray(R.array.channel_list_type_string);
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 30:
            case 31:
            case 32:
            case 71:
            case 72:
            case 74:
                if (this.channelListTypeMap == null) {
                    this.channelListTypeMap = new SparseIntArray();
                }
                this.channelListTypeMap.clear();
                if (DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type() == 0) {
                    loop = 0;
                    while (loop < channelTypeStr.length) {
                        data.add(channelTypeStr[loop]);
                        this.channelListTypeMap.append(loop, loop);
                        loop++;
                    }
                } else {
                    loop = 0;
                    while (loop < channelTypeStr.length - 1) {
                        data.add(channelTypeStr[loop]);
                        this.channelListTypeMap.append(loop, loop);
                        loop++;
                    }
                }
                for (int i = 0; i < GMScreenGlobalInfo.favGroups.size(); i++) {
                    Iterator<DataConvertChannelModel> it = this.mChannelData.getChannelListByTvRadioType().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        DataConvertChannelModel model = it.next();
                        if (model.mfavGroupIDs.contains(Integer.valueOf(GMScreenGlobalInfo.favGroups.get(i).getFavorTypeID()))) {
                            data.add(GMScreenGlobalInfo.favGroups.get(i).GetFavorName());
                            this.channelListTypeMap.append(loop, GMScreenGlobalInfo.favGroups.get(i).getFavorTypeID() + 4);
                            loop++;
                        }
                    }
                }
                return data;
            default:
                if (DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type() == 0) {
                    for (String str : channelTypeStr) {
                        data.add(str);
                    }
                } else {
                    for (int loop2 = 0; loop2 < channelTypeStr.length - 1; loop2++) {
                        data.add(channelTypeStr[loop2]);
                    }
                }
                this.channelTypeFavMark = 0;
                for (DataConvertChannelModel model2 : this.mChannelData.getChannelListByTvRadioType()) {
                    this.channelTypeFavMark |= model2.GetFavMark();
                }
                int tempMark = this.channelTypeFavMark;
                int loop3 = 0;
                while (tempMark > 0) {
                    if ((tempMark & 1) > 0) {
                        data.add(GMScreenGlobalInfo.favType.get(loop3));
                    }
                    tempMark >>>= 1;
                    loop3++;
                }
                return data;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void synchronizeSTBChannelType(int selectType) {
        try {
            this.parser = ParserFactory.getParser();
            List<DataConvertChannelTypeModel> channelTypeModels = new ArrayList<>();
            DataConvertChannelTypeModel model = new DataConvertChannelTypeModel();
            switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                case 30:
                case 31:
                case 32:
                case 71:
                case 72:
                case 74:
                    if (selectType >= 0 && selectType <= 3) {
                        model.setIsFavList(0);
                        model.setSelectListType(selectType);
                        break;
                    } else if (selectType >= 4) {
                        model.setIsFavList(1);
                        model.setSelectListType(selectType - 4);
                        break;
                    } else {
                        return;
                    }
                default:
                    if (selectType >= 0 && selectType <= 3) {
                        model.setIsFavList(0);
                        model.setSelectListType(selectType);
                        break;
                    } else if (selectType >= 4 && selectType <= 11) {
                        model.setIsFavList(1);
                        model.setSelectListType(selectType - 4);
                        break;
                    } else {
                        return;
                    }
                    break;
            }
            channelTypeModels.add(model);
            this.dataBuff = this.parser.serialize(channelTypeModels, GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED).getBytes("UTF-8");
            GsSendSocket.sendSocketToStb(this.dataBuff, this.tcpSocket, 0, this.dataBuff.length, GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dpValue * scale) + 0.5f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showChannelTypePopupWindow(View parent) {
        LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        View layout = inflater.inflate(R.layout.channel_type_popup_window_layout, (ViewGroup) null);
        ListView ChannelTypeList = (ListView) layout.findViewById(R.id.channel_type_list);
        if (getChannelTypeData().size() < 4) {
            ChannelTypeList.setLayoutParams(new LinearLayout.LayoutParams(dip2px(this, 200.0f), dip2px(this, 150.0f)));
        } else {
            ChannelTypeList.setLayoutParams(new LinearLayout.LayoutParams(dip2px(this, 200.0f), dip2px(this, 205.0f)));
        }
        this.typeAdapter = new ListviewAdapter(layout.getContext(), getChannelTypeData());
        this.typeAdapter.setTextColor(getResources().getColor(R.color.white));
        this.typeAdapter.setItemBackgroundResource(R.drawable.popup_window_item_bg);
        ChannelTypeList.setAdapter((ListAdapter) this.typeAdapter);
        ChannelTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.11
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) throws NumberFormatException {
                GsChannelListActivity.this.preChannelListType = GsChannelListActivity.this.currentChannelListType;
                GsChannelListActivity.this.currentChannelListType = GsChannelListActivity.this.clickPosition2ListType(position);
                GsChannelListActivity.this.ChangeChannelListType(GsChannelListActivity.this.currentChannelListType, false);
                GsChannelListActivity.this.channelTypePopupWindow.dismiss();
                GsChannelListActivity.this.channelTypeArrow.setBackgroundResource(R.drawable.channel_type_right_arrow);
                GsChannelListActivity.this.synchronizeSTBChannelType(GsChannelListActivity.this.currentChannelListType);
            }
        });
        this.channelTypePopupWindow = new PopupWindow(layout, -2, -2, true);
        this.channelTypePopupWindow.setFocusable(true);
        this.channelTypePopupWindow.setOutsideTouchable(true);
        this.channelTypePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.12
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                GsChannelListActivity.this.channelTypeArrow.setBackgroundResource(R.drawable.channel_type_right_arrow);
            }
        });
        this.channelTypePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        this.channelTypePopupWindow.setTouchInterceptor(new View.OnTouchListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.13
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != 4) {
                    return false;
                }
                GsChannelListActivity.this.channelTypePopupWindow.dismiss();
                GsChannelListActivity.this.channelTypeArrow.setBackgroundResource(R.drawable.channel_type_right_arrow);
                return true;
            }
        });
        if (!this.channelTypePopupWindow.isShowing()) {
            View channelTitle = findViewById(R.id.channel_title);
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int y = frame.top + channelTitle.getHeight();
            int x = (getWindowManager().getDefaultDisplay().getWidth() - ChannelTypeList.getLayoutParams().width) / 2;
            this.channelTypePopupWindow.showAtLocation(parent, 0, x, y + 1);
            this.channelTypeArrow.setBackgroundResource(R.drawable.channel_type_down_arrow);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlayStream(int position, Intent intent) {
        if (this.waitDialog.isShowing()) {
            this.waitDialog.dismiss();
        }
        this.waitDialog = DialogBuilder.showProgressDialog(getParent(), R.string.str_open_channel, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), this.timeOutRun);
        abtainPlayUrl(position, intent);
    }

    private void abtainPlayUrl(final int location, final Intent intent) {
        Runnable run = new Runnable() { // from class: mktvsmart.screen.channel.GsChannelListActivity.14
            @Override // java.lang.Runnable
            public void run() throws SocketException, NumberFormatException, UnsupportedEncodingException {
                switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                    case 8:
                    case 9:
                    case 14:
                        GsChannelListActivity.sRtsp = new Sat2IP_Rtsp();
                        String base = Sat2ipUtil.getRtspUriBase(GsChannelListActivity.this.tcpSocket.getInetAddress().toString());
                        String query = Sat2ipUtil.getRtspUriQuery((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(location));
                        GMScreenGlobalInfo.playType = 2;
                        boolean isSetupOk = GsChannelListActivity.sRtsp.setup_blocked(base, query);
                        if (!isSetupOk) {
                            DataConvertChannelModel map = (DataConvertChannelModel) GsChannelListActivity.this.channelListAdapter.getItem(location);
                            int isProgramScramble = map.GetIsProgramScramble();
                            if (isProgramScramble == 0) {
                                GsChannelListActivity.this.mainHandler.sendMessage(GsChannelListActivity.this.mainHandler.obtainMessage(4, GsChannelListActivity.this.getString(R.string.error_message_server_unavailable)));
                            }
                            GsChannelListActivity.sRtsp = null;
                            return;
                        }
                        GsChannelListActivity.this.sendSat2ipChannelRequestToStb(location);
                        DVBtoIP.initResourceForPlayer(GsChannelListActivity.sRtsp.get_rtp_port(), FindPlayerAndPlayChannel.getRtspPipeFilePath(GsChannelListActivity.this), 2, GMScreenGlobalInfo.getKeyWay());
                        intent.setDataAndType(Uri.parse("file://" + FindPlayerAndPlayChannel.getRtspPipeFilePath(GsChannelListActivity.this)), "video/*");
                        GsChannelListActivity.this.mainHandler.sendMessage(GsChannelListActivity.this.mainHandler.obtainMessage(3, intent));
                        break;
                    default:
                        GMScreenGlobalInfo.playType = 2;
                        GsChannelListActivity.this.sendSat2ipChannelRequestToStb(location);
                        GsChannelListActivity.this.mPlayIntent = intent;
                        break;
                }
                GsChannelListActivity.this.bPlayWithOherPlayer = true;
            }
        };
        new Thread(run).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSat2ipChannelRequestToStb(int location) throws SocketException, UnsupportedEncodingException {
        ArrayList<DataConvertChannelModel> sat2ipChannels = new ArrayList<>();
        this.currentSat2ipChannelProgramId = ((DataConvertChannelModel) this.channelListAdapter.getItem(location)).GetProgramId();
        try {
            DataConvertChannelModel sat2ipChannel = (DataConvertChannelModel) this.channelListAdapter.getItem(location);
            sat2ipChannels.add(sat2ipChannel);
            byte[] data_buff = this.parser.serialize(sat2ipChannels, GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY).getBytes("UTF-8");
            this.tcpSocket.setSoTimeout(3000);
            GsSendSocket.sendSocketToStb(data_buff, this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY);
        } catch (ProgramNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playStream(int position) {
        FindPlayerAndPlayChannel findPlayerAndPlayerChannel = new FindPlayerAndPlayChannel(getParent());
        findPlayerAndPlayerChannel.implementPlayByDesignatedPlayer(this.mPlayByDesignatedPlayer);
        findPlayerAndPlayerChannel.selectPlayer(position);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void otherPlatformPlay(int position) {
        int pisitionInChannelList = this.mChannelData.getIndexByProgIdInCurTvRadioProgList(((DataConvertChannelModel) this.channelListAdapter.getItem(position)).GetProgramId());
        GMScreenGlobalInfo.playType = 2;
        this.bPlayWithOherPlayer = false;
        Intent mediaIntent = new Intent("android.intent.action.VIEW");
        mediaIntent.setClass(getParent(), LivePlayActivity.class);
        mediaIntent.putExtra("position", pisitionInChannelList);
        mediaIntent.setFlags(268435456);
        getApplication().startActivity(mediaIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopStream() throws UnsupportedEncodingException {
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 8:
            case 9:
                if (sRtsp != null) {
                    sRtsp.teardown();
                    sRtsp = null;
                    DVBtoIP.destroyResourceForPlayer();
                    this.isSat2ipStarted = false;
                    this.currentSat2ipChannelProgramId = "";
                    GMScreenGlobalInfo.playType = 0;
                    GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_SAT2IP_PLAY_STOP);
                    break;
                }
                break;
            default:
                GMScreenGlobalInfo.playType = 0;
                GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_SAT2IP_PLAY_STOP);
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void trueNewChannelInStb(int position) throws SocketException, UnsupportedEncodingException {
        try {
            DataConvertChannelModel map = (DataConvertChannelModel) this.channelListAdapter.getItem(position);
            String name = map.getProgramName();
            String satTPProgramID = map.GetProgramId();
            DataConvertChannelModel initData = new DataConvertChannelModel();
            List<DataConvertChannelModel> models = new ArrayList<>();
            this.parser = ParserFactory.getParser();
            initData.SetProgramIndex(position);
            initData.setProgramName(name);
            initData.SetProgramId(satTPProgramID);
            initData.setChannelTpye(map.getChannelTpye());
            models.add(initData);
            byte[] data_buff = this.parser.serialize(models, 1000).getBytes("UTF-8");
            this.tcpSocket.setSoTimeout(3000);
            GsSendSocket.sendSocketToStb(data_buff, this.tcpSocket, 0, data_buff.length, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustSelectionOfChannelListView(boolean initFocusPosition) {
        int index = 0;
        Iterator<DataConvertChannelModel> it = this.mCurrentChannelList.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            DataConvertChannelModel model = it.next();
            if (model.getIsPlaying() == 1) {
                if (initFocusPosition) {
                    if (index > 5) {
                        this.ChannelListView.setSelection(index - 3);
                    } else {
                        this.ChannelListView.setSelection(0);
                    }
                } else if (index < this.mFirstVisibleChannelIdex || index > this.mLastVisibleChannelIndex) {
                    if (index > this.mLastVisibleChannelIndex - this.mFirstVisibleChannelIdex) {
                        int position = index - ((this.mLastVisibleChannelIndex - this.mFirstVisibleChannelIdex) / 2);
                        this.ChannelListView.setSelection(position);
                    } else {
                        this.ChannelListView.setSelection(0);
                    }
                }
            } else {
                index++;
            }
        }
        if (index == this.mCurrentChannelList.size()) {
            this.ChannelListView.setSelection(0);
        }
    }

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.recycle();
        this.msgProc.setOnMessageProcess(0, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.15
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws NumberFormatException {
                if (msg.arg1 > 0) {
                    int oldTotalProgramNum = GsChannelListActivity.this.mChannelData.getTotalProgramNum();
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData != null) {
                        List<DataConvertChannelModel> tempChannelListModelss = GsChannelListActivity.this.mChannelData.initChannelListData(recvData);
                        if (GsChannelListActivity.this.waitDialog.isShowing()) {
                            GsChannelListActivity.this.waitDialog.dismiss();
                        }
                        if (tempChannelListModelss.size() != 0 || oldTotalProgramNum != GsChannelListActivity.this.mChannelData.getTotalProgramNum()) {
                            GsChannelListActivity.this.mCurrentChannelList = GsChannelListActivity.this.mChannelData.getChannelListByProgramType(GsChannelListActivity.this.mChannelData.getChannelListByTvRadioType(), GsChannelListActivity.this.currentChannelListType);
                            if (GsChannelListActivity.this.mCurrentChannelList == null || GsChannelListActivity.this.mCurrentChannelList.isEmpty()) {
                                GsChannelListActivity.this.mCurrentChannelList = GsChannelListActivity.this.mChannelData.getChannelListByProgramType(GsChannelListActivity.this.mChannelData.getChannelListByTvRadioType(), 0);
                                GsChannelListActivity.this.currentChannelListType = 0;
                                GsChannelListActivity.this.titleText.setText(GsChannelListActivity.this.getResources().getStringArray(R.array.channel_list_type_string)[GsChannelListActivity.this.currentChannelListType]);
                            }
                            if (GsChannelListActivity.this.mGetChannelListWhenLogin) {
                                switch (DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type()) {
                                    case 0:
                                        GsChannelListActivity.this.TypeSwitch.setText(GsChannelListActivity.this.getResources().getText(R.string.type_radio));
                                        break;
                                    case 1:
                                        GsChannelListActivity.this.TypeSwitch.setText(GsChannelListActivity.this.getResources().getText(R.string.type_tv));
                                        break;
                                }
                                switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                                    case 30:
                                    case 31:
                                    case 32:
                                    case 71:
                                    case 72:
                                    case 74:
                                        if (GsChannelListActivity.this.currentChannelListType < 0 || GsChannelListActivity.this.currentChannelListType > 3) {
                                            if (GsChannelListActivity.this.currentChannelListType >= 4) {
                                                for (DataConvertFavorModel favGroup : GMScreenGlobalInfo.favGroups) {
                                                    if (favGroup.getFavorTypeID() == GsChannelListActivity.this.currentChannelListType - 4) {
                                                        GsChannelListActivity.this.titleText.setText(favGroup.GetFavorName());
                                                    }
                                                }
                                                break;
                                            }
                                        } else {
                                            GsChannelListActivity.this.titleText.setText(GsChannelListActivity.this.getResources().getStringArray(R.array.channel_list_type_string)[GsChannelListActivity.this.currentChannelListType]);
                                            break;
                                        }
                                        break;
                                    default:
                                        if (GsChannelListActivity.this.currentChannelListType < 0 || GsChannelListActivity.this.currentChannelListType > 3) {
                                            if (GsChannelListActivity.this.currentChannelListType >= 4 && GsChannelListActivity.this.currentChannelListType <= 11) {
                                                GsChannelListActivity.this.titleText.setText(GMScreenGlobalInfo.favType.get(GsChannelListActivity.this.currentChannelListType - 4));
                                                break;
                                            }
                                        } else {
                                            GsChannelListActivity.this.titleText.setText(GsChannelListActivity.this.getResources().getStringArray(R.array.channel_list_type_string)[GsChannelListActivity.this.currentChannelListType]);
                                            break;
                                        }
                                        break;
                                }
                            }
                            GsChannelListActivity.this.setCurrentChannelListDispIndex();
                            GsChannelListActivity.this.mOriginalChannelListModels = GsChannelListActivity.this.mCurrentChannelList;
                            if (!GsChannelListActivity.this.mEnterSearchFlag) {
                                GsChannelListActivity.this.channelListAdapter = GsChannelListActivity.this.new list_single_button_adapter(GsChannelListActivity.this);
                                GsChannelListActivity.this.ChannelListView.setAdapter((ListAdapter) GsChannelListActivity.this.channelListAdapter);
                                GsChannelListActivity.this.adjustSelectionOfChannelListView(true);
                            } else {
                                GsChannelListActivity.this.mChannelListChangeFlag = true;
                                GsChannelListActivity.this.findChannel();
                            }
                            if (GsChannelListActivity.this.mGetChannelListWhenLogin) {
                                boolean flag = false;
                                Iterator<DataConvertChannelModel> it = GsChannelListActivity.this.mChannelData.getmTvChannelList().iterator();
                                while (true) {
                                    if (it.hasNext()) {
                                        DataConvertChannelModel model = it.next();
                                        if (model.getmWillBePlayed() == 1 && model.getLockMark() == 1) {
                                            if (GsChannelListActivity.this.isInForeground) {
                                                GsChannelListActivity.this.passwordType = 2;
                                                GsChannelListActivity.this.inputPermissionPassword();
                                                flag = true;
                                            }
                                        }
                                    }
                                }
                                if (!flag) {
                                    Iterator<DataConvertChannelModel> it2 = GsChannelListActivity.this.mChannelData.getmRadioChannelList().iterator();
                                    while (true) {
                                        if (it2.hasNext()) {
                                            DataConvertChannelModel model2 = it2.next();
                                            if (model2.getmWillBePlayed() == 1 && model2.getLockMark() == 1) {
                                                if (GsChannelListActivity.this.isInForeground) {
                                                    GsChannelListActivity.this.passwordType = 2;
                                                    GsChannelListActivity.this.inputPermissionPassword();
                                                }
                                            }
                                        }
                                    }
                                }
                                GsChannelListActivity.this.mGetChannelListWhenLogin = false;
                            }
                            if (tempChannelListModelss == null || tempChannelListModelss.size() != GMScreenGlobalInfo.getMaxProgramNumPerRequest()) {
                                if (GMScreenGlobalInfo.getCurStbInfo().getmSatEnable() == 1) {
                                    GsChannelListActivity.this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_REFRESH_SAT_LIST);
                                    return;
                                }
                                return;
                            }
                            GsChannelListActivity.this.requestProgramListFromTo(GsChannelListActivity.this.mChannelData.getTotalProgramNum(), (GsChannelListActivity.this.mChannelData.getTotalProgramNum() + GMScreenGlobalInfo.getMaxProgramNumPerRequest()) - 1);
                            return;
                        }
                        if (tempChannelListModelss.size() == 0) {
                            Toast.makeText(GsChannelListActivity.this, R.string.stb_have_no_channels, 0).show();
                            if (!GsChannelListActivity.this.mGetChannelListWhenLogin) {
                                GsChannelListActivity.this.mCurrentChannelList.removeAll(GsChannelListActivity.this.mCurrentChannelList);
                                GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    Log.e("GSChannelListActivity", "recvData = " + recvData);
                    return;
                }
                if (msg.arg1 != 0 || msg.arg2 != -1) {
                    return;
                }
                GsChannelListActivity.this.requestProgramListFromTo(GsChannelListActivity.this.mChannelData.getTotalProgramNum(), (GsChannelListActivity.this.mChannelData.getTotalProgramNum() + GMScreenGlobalInfo.getMaxProgramNumPerRequest()) - 1);
            }
        });
        this.msgProc.setOnMessageProcess(5, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.16
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    try {
                        Bundle data = msg.getData();
                        byte[] recvData = data.getByteArray("ReceivedData");
                        if (recvData == null) {
                            Log.e("GSChannelListActivity", "recvData = " + recvData);
                            return;
                        }
                        InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        System.out.println("epg_program data length: " + recvData.length);
                        List<?> list = GsChannelListActivity.this.parser.parse(istream, 6);
                        byte todayDate = ((GsEPGTableChannel) list.get(0)).getTodayDate();
                        System.out.println("todayDate:" + ((int) todayDate));
                        ((GsEPGTableChannel) list.get(0)).setProgramId(GsChannelListActivity.this.epg_program_sat_tp_id);
                        ((GsEPGTableChannel) list.get(0)).setProgramName(GsChannelListActivity.this.epg_program_name);
                        GsSession session = GsSession.getSession();
                        session.put("EPG_PROGRAM_TABLE", list.get(0));
                        if (GsChannelListActivity.this.waitDialog.isShowing()) {
                            GsChannelListActivity.this.waitDialog.dismiss();
                        }
                        Intent mIntent = new Intent(GsChannelListActivity.this, (Class<?>) GsEPGMenuActivity.class);
                        GsChannelListActivity.this.startActivityForResult(mIntent, 0);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                if (msg.arg2 == 3) {
                    if (GsChannelListActivity.this.waitDialog.isShowing()) {
                        GsChannelListActivity.this.waitDialog.dismiss();
                    }
                    Toast.makeText(GsChannelListActivity.this, R.string.stb_no_enough_memory, 0).show();
                }
            }
        });
        this.msgProc.setOnMessageProcess(3, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.17
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData == null) {
                        Log.e("GSChannelListActivity", "recvData = " + recvData);
                        return;
                    }
                    DataParser parser = ParserFactory.getParser();
                    List<?> list = null;
                    try {
                        InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        list = parser.parse(istream, 15);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String playingProgId = (String) list.get(0);
                    if (GsChannelListActivity.this.mChannelData.getmTvChannelList() != null) {
                        if (GsChannelListActivity.this.isSat2ipStarted) {
                            try {
                                DataConvertChannelModel sat2ipChannel = GsChannelListActivity.this.mChannelData.getProgramByProgramId(GsChannelListActivity.this.currentSat2ipChannelProgramId);
                                DataConvertChannelModel playingChannel = GsChannelListActivity.this.mChannelData.getProgramByProgramId((String) list.get(0));
                                if (!GsChannelListActivity.this.mChannelData.canSat2ipChannelPlay(playingChannel, sat2ipChannel)) {
                                    GsChannelListActivity.this.stopStream();
                                    Toast.makeText(GsChannelListActivity.this, R.string.tune_to_different_tp, 0).show();
                                }
                            } catch (ProgramNotFoundException e2) {
                                Log.d("ProgramNotFoundException", e2.getMessage());
                                e2.printStackTrace();
                            }
                        }
                        GsChannelListActivity.this.loop = 0;
                        while (GsChannelListActivity.this.loop < GsChannelListActivity.this.mChannelData.getmTvChannelList().size()) {
                            DataConvertChannelModel tempPrgoram = GsChannelListActivity.this.mChannelData.getmTvChannelList().get(GsChannelListActivity.this.loop);
                            if (playingProgId.equals(tempPrgoram.GetProgramId())) {
                                tempPrgoram.setIsPlaying(1);
                            } else {
                                tempPrgoram.setIsPlaying(0);
                            }
                            GsChannelListActivity.this.loop++;
                        }
                        GsChannelListActivity.this.loop = 0;
                        while (GsChannelListActivity.this.loop < GsChannelListActivity.this.mChannelData.getmRadioChannelList().size()) {
                            DataConvertChannelModel tempPrgoram2 = GsChannelListActivity.this.mChannelData.getmRadioChannelList().get(GsChannelListActivity.this.loop);
                            if (playingProgId.equals(tempPrgoram2.GetProgramId())) {
                                tempPrgoram2.setIsPlaying(1);
                            } else {
                                tempPrgoram2.setIsPlaying(0);
                            }
                            GsChannelListActivity.this.loop++;
                        }
                    }
                    if (GsChannelListActivity.this.channelListAdapter != null) {
                        GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                        GsChannelListActivity.this.adjustSelectionOfChannelListView(false);
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.18
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsChannelListActivity.this, R.string.return_login_list_reason, 0).show();
                GsChannelListActivity.enable_edit = false;
                Intent intent = new Intent();
                intent.setClass(GsChannelListActivity.this, GsLoginListActivity.class);
                GsChannelListActivity.this.startActivity(intent);
                GsChannelListActivity.this.finish();
            }
        });
        this.msgProc.setOnMessageProcess(20, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.19
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                if (msg.arg2 == 5) {
                    GsSendSocket.sendOnlyCommandSocketToStb(GsChannelListActivity.this.tcpSocket, 20);
                    return;
                }
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                if (recvData == null) {
                    Log.e("GSChannelListActivity", "recvData = " + recvData);
                    return;
                }
                InputStream inStream = new ByteArrayInputStream(recvData, 0, recvData.length);
                try {
                    GsChannelListActivity.this.controlModels = GsChannelListActivity.this.parser.parse(inStream, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.msgProc.setOnMessageProcess(18, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.20
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws Resources.NotFoundException {
                ArrayList<String> sortTypeArray;
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData == null) {
                        Log.e("GSChannelListActivity", "recvData = " + recvData);
                        return;
                    }
                    List<?> list = null;
                    GsChannelListActivity.this.parser = ParserFactory.getParser();
                    try {
                        InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        list = GsChannelListActivity.this.parser.parse(instream, 13);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int sortType = ((DataConvertSortModel) list.get(0)).getmSortType();
                    LayoutInflater inflater = LayoutInflater.from(GsChannelListActivity.this);
                    View layout = inflater.inflate(R.layout.pop_window_list_layout, (ViewGroup) null);
                    TextView popWindowTitle = (TextView) layout.findViewById(R.id.pop_window_title);
                    popWindowTitle.setText(R.string.sort_title);
                    ListView sortTypeList = (ListView) layout.findViewById(R.id.pop_window_list);
                    Button cancelBtn = (Button) layout.findViewById(R.id.pop_window_cancel_btn);
                    switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                        case 20:
                        case 21:
                        case 30:
                        case 32:
                        case 71:
                        case 72:
                        case 74:
                            sortTypeArray = ((DataConvertSortModel) list.get(0)).getSortTypeList();
                            break;
                        default:
                            sortTypeArray = GsChannelListActivity.this.getSortTypeArray();
                            break;
                    }
                    GsChannelListActivity.this.mSortAdapter = GsChannelListActivity.this.new sort_adapter(layout.getContext(), sortType, sortTypeArray, ((DataConvertSortModel) list.get(0)).getmMacroFlag());
                    GsChannelListActivity.this.mSortAdapter.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.white));
                    sortTypeList.setAdapter((ListAdapter) GsChannelListActivity.this.mSortAdapter);
                    cancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.20.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            GsChannelListActivity.this.mSortTypePopupWindow.dismiss();
                        }
                    });
                    sortTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.20.2
                        @Override // android.widget.AdapterView.OnItemClickListener
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            List<DataConvertSortModel> sortTypeModels = new ArrayList<>();
                            DataConvertSortModel model = new DataConvertSortModel();
                            model.setmSortType(position);
                            model.setmTvState(DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type());
                            sortTypeModels.add(model);
                            try {
                                GsChannelListActivity.this.dataBuff = GsChannelListActivity.this.parser.serialize(sortTypeModels, GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_SORT).getBytes("UTF-8");
                                GsSendSocket.sendSocketToStb(GsChannelListActivity.this.dataBuff, GsChannelListActivity.this.tcpSocket, 0, GsChannelListActivity.this.dataBuff.length, GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_SORT);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            GsChannelListActivity.this.mSortTypePopupWindow.dismiss();
                            if (GsChannelListActivity.this.waitDialog.isShowing()) {
                                GsChannelListActivity.this.waitDialog.dismiss();
                            }
                            GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog(GsChannelListActivity.this.getParent(), R.string.sorting_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_operate_fail);
                        }
                    });
                    GsChannelListActivity.this.mSortTypePopupWindow = new Dialog(GsChannelListActivity.this.getParent(), R.style.dialog);
                    GsChannelListActivity.this.mSortTypePopupWindow.setContentView(layout);
                    if (!GsChannelListActivity.this.mSortTypePopupWindow.isShowing()) {
                        GsChannelListActivity.this.mSortTypePopupWindow.show();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(16, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.21
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData == null) {
                        Log.e("GSChannelListActivity", "recvData = " + recvData);
                        return;
                    }
                    InputStream inStream = new ByteArrayInputStream(recvData, 0, recvData.length);
                    try {
                        GsChannelListActivity.this.currentChannelListType = Integer.parseInt((String) GsChannelListActivity.this.parser.parse(inStream, 17).get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(73, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.22
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (!GsChannelListActivity.this.waitDialog.isShowing()) {
                    GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog(GsChannelListActivity.this.getParent(), R.string.update_channel_list, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), new Runnable() { // from class: mktvsmart.screen.channel.GsChannelListActivity.22.1
                        @Override // java.lang.Runnable
                        public void run() {
                            GsChannelListActivity.this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_UPDATE_CHANNEL_LIST_FAILED);
                        }
                    });
                }
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_UPDATE_CHANNEL_LIST_FAILED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.23
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsChannelListActivity.this, "update failed!", 0).show();
            }
        });
        this.msgProc.setOnMessageProcess(17, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.24
            List<String> tvRadioTypeModels = null;

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                try {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData == null) {
                        Log.e("GSChannelListActivity", "recvData = " + recvData);
                        return;
                    }
                    InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
                    this.tvRadioTypeModels = GsChannelListActivity.this.parser.parse(instream, 15);
                    if (Integer.parseInt(this.tvRadioTypeModels.get(0)) != DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type()) {
                        switch (DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type()) {
                            case 0:
                                GsChannelListActivity.this.TypeSwitch.setText(GsChannelListActivity.this.getResources().getText(R.string.type_tv));
                                break;
                            case 1:
                                GsChannelListActivity.this.TypeSwitch.setText(GsChannelListActivity.this.getResources().getText(R.string.type_radio));
                                break;
                        }
                        DataConvertChannelTypeModel.setCurrent_channel_tv_radio_type(Integer.parseInt(this.tvRadioTypeModels.get(0)));
                        GsChannelListActivity.this.currentChannelListType = 0;
                        GsChannelListActivity.this.mCurrentChannelList = GsChannelListActivity.this.mChannelData.getChannelListByProgramType(GsChannelListActivity.this.mChannelData.getChannelListByTvRadioType(), GsChannelListActivity.this.currentChannelListType);
                        if (GsChannelListActivity.this.mEnterSearchFlag) {
                            GsChannelListActivity.this.mChannelListChangeFlag = true;
                        }
                    }
                    GsChannelListActivity.this.setCurrentChannelListDispIndex();
                    GsChannelListActivity.this.mOriginalChannelListModels = GsChannelListActivity.this.mCurrentChannelList;
                    if (!GsChannelListActivity.this.mEnterSearchFlag) {
                        GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                        GsChannelListActivity.this.adjustSelectionOfChannelListView(false);
                    } else {
                        GsChannelListActivity.this.findChannel();
                    }
                    GsChannelListActivity.this.titleText.setText(GsChannelListActivity.this.getResources().getStringArray(R.array.channel_list_type_string)[GsChannelListActivity.this.currentChannelListType]);
                    if (GsChannelListActivity.this.waitDialog.isShowing()) {
                        GsChannelListActivity.this.waitDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.msgProc.setOnMessageProcess(2001, this.post);
        this.msgProc.setOnMessageProcess(2004, this.post);
        this.msgProc.setOnMessageProcess(2002, this, this.requestAllChannelWhenSTBChannelListChanged);
        this.msgProc.setOnMessageProcess(2025, this, this.requestAllChannelWhenSTBChannelListChanged);
        this.msgProc.setOnMessageProcess(2026, this, this.requestAllChannelWhenSTBChannelListChanged);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.25
            List<String> verifyResult = null;

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws SocketException, UnsupportedEncodingException {
                if (GsChannelListActivity.this.isInForeground) {
                    try {
                        Bundle data = msg.getData();
                        byte[] recvData = data.getByteArray("ReceivedData");
                        if (recvData == null) {
                            Log.e("GSChannelListActivity", "recvData = " + recvData);
                        }
                        InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        this.verifyResult = GsChannelListActivity.this.parser.parse(instream, 15);
                        if (GsChannelListActivity.this.waitDialog.isShowing()) {
                            GsChannelListActivity.this.waitDialog.dismiss();
                        }
                        if (Integer.parseInt(this.verifyResult.get(0)) == 0) {
                            GsChannelListActivity.this.repeatPassword = true;
                            GsChannelListActivity.this.inputPermissionPassword();
                            return;
                        }
                        switch (GsChannelListActivity.this.passwordType) {
                            case 1:
                                List<DataConvertEditChannelLockModel> lockModels = new ArrayList<>();
                                if (GsChannelListActivity.this.expandPosition == -1 || GsChannelListActivity.enable_edit) {
                                    for (DataConvertChannelModel model : GsChannelListActivity.this.mCurrentChannelList) {
                                        if (model.getSelectedFlag()) {
                                            DataConvertEditChannelLockModel channelLockModel = new DataConvertEditChannelLockModel();
                                            channelLockModel.setProgramId(model.GetProgramId());
                                            channelLockModel.setmChannelType(model.getChannelTpye());
                                            lockModels.add(channelLockModel);
                                        }
                                    }
                                } else {
                                    DataConvertChannelModel model2 = (DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(GsChannelListActivity.this.expandPosition);
                                    DataConvertEditChannelLockModel channelLockModel2 = new DataConvertEditChannelLockModel();
                                    channelLockModel2.setProgramId(model2.GetProgramId());
                                    channelLockModel2.setmChannelType(model2.getChannelTpye());
                                    lockModels.add(channelLockModel2);
                                }
                                try {
                                    byte[] data_buff = GsChannelListActivity.this.parser.serialize(lockModels, GlobalConstantValue.GMS_MSG_DO_CHANNEL_LOCK).getBytes("UTF-8");
                                    GsChannelListActivity.this.tcpSocket.setSoTimeout(3000);
                                    GsSendSocket.sendSocketToStb(data_buff, GsChannelListActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_CHANNEL_LOCK);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (GsChannelListActivity.this.waitDialog.isShowing()) {
                                    GsChannelListActivity.this.waitDialog.dismiss();
                                }
                                GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog((Context) GsChannelListActivity.this.getParent(), GsChannelListActivity.this.getString(R.string.adding_lock), GsChannelListActivity.this.getString(R.string.please_wait), true);
                                GsChannelListActivity.this.passwordType = 0;
                                break;
                            case 2:
                            default:
                                GsChannelListActivity.this.passwordType = 0;
                                break;
                            case 3:
                                GsChannelListActivity.this.playStream(GsChannelListActivity.this.sat2ipPlayPosition);
                                GsChannelListActivity.this.passwordType = 0;
                                break;
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(2006, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.26
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (GsChannelListActivity.this.isInForeground) {
                    GsChannelListActivity.this.passwordType = 2;
                    if (GsChannelListActivity.this.pswInputDialog != null && GsChannelListActivity.this.pswInputDialog.isShowing()) {
                        GsChannelListActivity.this.pswInputDialog.dismiss();
                    }
                    if (GsChannelListActivity.this.waitDialog != null && GsChannelListActivity.this.waitDialog.isShowing()) {
                        GsChannelListActivity.this.waitDialog.dismiss();
                    }
                    GsChannelListActivity.this.inputPermissionPassword();
                }
            }
        });
        this.msgProc.setOnMessageProcess(2007, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.27
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (GsChannelListActivity.this.isInForeground) {
                    GsChannelListActivity.this.passwordType = 0;
                    if (GsChannelListActivity.this.pswInputDialog != null && GsChannelListActivity.this.pswInputDialog.isShowing()) {
                        GsChannelListActivity.this.pswInputDialog.dismiss();
                    }
                    if (GsChannelListActivity.this.waitDialog != null && GsChannelListActivity.this.waitDialog.isShowing()) {
                        GsChannelListActivity.this.waitDialog.dismiss();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(22, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.28
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData != null) {
                        GsChannelListActivity.this.mChannelData.initSatList(recvData);
                    } else {
                        Log.e("GSChannelListActivity", "recvData = " + recvData);
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(24, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.29
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData != null) {
                        GsChannelListActivity.this.mChannelData.initTpList(recvData);
                    } else {
                        Log.e("GSChannelListActivity", "recvData = " + recvData);
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(23, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.30
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData == null) {
                        Log.e("GSChannelListActivity", "recvData = " + recvData);
                        return;
                    }
                    GsChannelListActivity.this.parser = ParserFactory.getParser();
                    new ArrayList();
                    try {
                        InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        GMScreenGlobalInfo.setmSatIndexSelected(Integer.parseInt((String) GsChannelListActivity.this.parser.parse(instream, 15).get(0)));
                        GsChannelListActivity.this.mCurrentChannelList = GsChannelListActivity.this.mChannelData.getChannelListByProgramType(GsChannelListActivity.this.mChannelData.getChannelListByTvRadioType(), GsChannelListActivity.this.currentChannelListType);
                        GsChannelListActivity.this.setCurrentChannelListDispIndex();
                        GsChannelListActivity.this.mOriginalChannelListModels = GsChannelListActivity.this.mCurrentChannelList;
                        if (!GsChannelListActivity.this.mEnterSearchFlag) {
                            if (GsChannelListActivity.this.channelListAdapter != null) {
                                GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                                GsChannelListActivity.this.adjustSelectionOfChannelListView(false);
                            }
                        } else {
                            GsChannelListActivity.this.findChannel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(2015, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.31
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsChannelListActivity.this, R.string.str_become_master, 1).show();
                GsChannelListActivity.this.enableSomeFucitonWhenBecomeMaster();
            }
        });
        this.msgProc.setOnMessageProcess(1000, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.32
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                switch (msg.arg2) {
                    case 16:
                        Toast.makeText(GsChannelListActivity.this, R.string.switch_channel_failed_because_stb_channel_recorded, 0).show();
                        break;
                    case 17:
                        Toast.makeText(GsChannelListActivity.this, R.string.switch_channel_failed_because_stb_is_menu_state, 0).show();
                        break;
                }
            }
        });
        this.msgProc.setOnMessageProcess(2009, this.post);
        this.msgProc.setOnMessageProcess(2013, this.post);
        this.msgProc.setOnMessageProcess(2019, this.post);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_PLAY_SAT2IP_CHANNEL_FAILED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.33
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsChannelListActivity.this, R.string.can_not_play_scramble_channel, 2000).show();
            }
        });
        this.msgProc.setOnMessageProcess(1002, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.34
            /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:18:0x008b -> B:21:0x0024). Please report as a decompilation issue!!! */
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws NumberFormatException {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData == null) {
                        Log.e("GSChannelListActivity", "recvData = " + recvData);
                        return;
                    }
                    GsChannelListActivity.this.parser = ParserFactory.getParser();
                    new ArrayList();
                    if (GsChannelListActivity.this.waitDialog.isShowing()) {
                        GsChannelListActivity.this.waitDialog.dismiss();
                    }
                    try {
                        InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        int exist_delete_sat2ip_channel_num = Integer.parseInt((String) GsChannelListActivity.this.parser.parse(instream, 15).get(0));
                        if (exist_delete_sat2ip_channel_num != 0) {
                            if (exist_delete_sat2ip_channel_num == 1) {
                                Toast.makeText(GsChannelListActivity.this, String.valueOf(exist_delete_sat2ip_channel_num) + " channel is playing by the mobile, it can not be deleted! ", 0).show();
                            } else {
                                Toast.makeText(GsChannelListActivity.this, String.valueOf(exist_delete_sat2ip_channel_num) + " channels are playing by the mobile, they can not be deleted! ", 0).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(2014, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.35
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GsChannelListActivity.this.tcpSocket, 20);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_CALL_AND_SMS_REMIND_CHANGED, this, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.36
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) {
                boolean phoneAndSmsRemindSetting = new EditPhoneAndSmsRemindSettingFile(GsChannelListActivity.this).getPhoneAndSmsRemindSetting();
                if (!phoneAndSmsRemindSetting) {
                    ((TelephonyManager) GsChannelListActivity.this.getSystemService("phone")).listen(GsChannelListActivity.this.mPhoneListener, 0);
                    GsChannelListActivity.this.mResolver.unregisterContentObserver(GsChannelListActivity.this.mSMSContentObserver);
                } else {
                    GsChannelListActivity.this.setPhoneAndSmsListener();
                }
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_GCHAT_DO_START, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.37
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (GsChannelListActivity.this.waitDialog.isShowing()) {
                    GsChannelListActivity.this.waitDialog.dismiss();
                }
                if (msg.arg1 > 0) {
                    try {
                        Bundle data = msg.getData();
                        GsChannelListActivity.this.parser = ParserFactory.getParser();
                        byte[] recvData = data.getByteArray("ReceivedData");
                        if (recvData != null) {
                            InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                            GsChannelListActivity.this.parser.parse(istream, 21);
                            Intent intent = new Intent();
                            intent.setClass(GsChannelListActivity.this, GChatActivity.class);
                            GsChannelListActivity.this.startActivity(intent);
                            return;
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                Toast.makeText(GsChannelListActivity.this, R.string.start_gchat_fail, 0).show();
            }
        });
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 8:
            case 9:
            case 14:
                break;
            default:
                this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.channel.GsChannelListActivity.38
                    @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
                    public void doInForeground(Message msg) {
                        Bundle data = msg.getData();
                        byte[] recvData = data.getByteArray("ReceivedData");
                        if (recvData != null && GsChannelListActivity.this.bPlayWithOherPlayer) {
                            GsChannelListActivity.this.parser = ParserFactory.getParser();
                            Log.d("GsChannelListActivity", new String(recvData));
                            InputStream istream = new ByteArrayInputStream(recvData, 0, msg.arg1);
                            try {
                                Map<String, Object> map = (Map) GsChannelListActivity.this.parser.parse(istream, 16).get(0);
                                if (map.get("success") == null) {
                                    GsChannelListActivity.this.mainHandler.sendMessage(GsChannelListActivity.this.mainHandler.obtainMessage(4, GsChannelListActivity.this.getString(R.string.error_message_server_unavailable)));
                                    return;
                                }
                                int iState = ((Integer) map.get("success")).intValue();
                                if (iState != 1) {
                                    String errorMsg = GsChannelListActivity.this.getString(R.string.error_message_server_unavailable);
                                    if (map.get("errormsg") != null) {
                                        errorMsg = (String) map.get("errormsg");
                                    } else if (map.get("url") == null || ((String) map.get("url")).length() <= 0) {
                                        errorMsg = "Get play url fail";
                                    }
                                    GsChannelListActivity.this.mainHandler.sendMessage(GsChannelListActivity.this.mainHandler.obtainMessage(4, errorMsg));
                                    return;
                                }
                                String url = (String) map.get("url");
                                if (url == null) {
                                    GsChannelListActivity.this.mainHandler.sendMessage(GsChannelListActivity.this.mainHandler.obtainMessage(4, GsChannelListActivity.this.getString(R.string.error_message_server_unavailable)));
                                    return;
                                }
                                Log.d("GsChannelListActivity", "play url : " + url);
                                if (GsChannelListActivity.this.mPlayIntent != null) {
                                    GsChannelListActivity.this.mPlayIntent.setDataAndType(Uri.parse(url), "video/*");
                                    GsChannelListActivity.this.mainHandler.sendMessage(GsChannelListActivity.this.mainHandler.obtainMessage(3, GsChannelListActivity.this.mPlayIntent));
                                }
                            } catch (Exception e) {
                                GsChannelListActivity.this.mainHandler.sendMessage(GsChannelListActivity.this.mainHandler.obtainMessage(4, GsChannelListActivity.this.getString(R.string.error_message_server_unavailable)));
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPhoneAndSmsListener() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService("phone");
        telephonyManager.listen(this.mPhoneListener, 32);
        this.mUri = Uri.parse("content://sms");
        this.mResolver = getContentResolver();
        this.mResolver.registerContentObserver(this.mUri, true, this.mSMSContentObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDisplayFavValue() {
        int selectChannelNum = 0;
        for (DataConvertChannelModel model : this.mCurrentChannelList) {
            if (model.getSelectedFlag()) {
                this.favorModels.add(model);
                selectChannelNum++;
            }
        }
        int[] favMarkArray = new int[selectChannelNum];
        for (int i = 0; i < this.favorModels.size(); i++) {
            favMarkArray[i] = this.favorModels.get(i).GetFavMark();
        }
        for (int i2 = 0; i2 < this.mIsChoice.length; i2++) {
            int j = 0;
            while (true) {
                if (j >= selectChannelNum) {
                    break;
                }
                if ((favMarkArray[j] & this.mFavValueArray[i2]) == this.mFavValueArray[i2]) {
                    if (j != selectChannelNum - 1) {
                        j++;
                    } else {
                        this.mIsChoice[i2] = true;
                        break;
                    }
                } else {
                    this.mIsChoice[i2] = false;
                    break;
                }
            }
            if (this.mIsChoice[i2]) {
                this.mFavValue |= this.mFavValueArray[i2];
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestProgramListFromTo(int fromIndex, int toIndex) {
        try {
            ArrayList<DataConvertChannelModel> requsetChannelFromTo = new ArrayList<>();
            DataConvertChannelModel dataChannelFrom = new DataConvertChannelModel();
            DataConvertChannelModel dataChannelTo = new DataConvertChannelModel();
            dataChannelFrom.SetProgramIndex(fromIndex);
            dataChannelTo.SetProgramIndex(toIndex);
            requsetChannelFromTo.add(dataChannelFrom);
            requsetChannelFromTo.add(dataChannelTo);
            this.parser = ParserFactory.getParser();
            this.dataBuff = this.parser.serialize(requsetChannelFromTo, 0).getBytes("UTF-8");
            GsSendSocket.sendSocketToStb(this.dataBuff, this.tcpSocket, 0, this.dataBuff.length, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSearchMenuEnable() {
        this.mSearchChannelEdit.setText("");
        this.mSearchChannelEdit.setVisibility(0);
        this.mSearchChannelEdit.setFocusable(true);
        this.mSearchChannelEdit.setFocusableInTouchMode(true);
        this.mSearchChannelEdit.requestFocus();
        this.mSearchCancelBtn.setVisibility(0);
        this.mEnterSearchFlag = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSearchMenuDisable() {
        this.mSearchChannelEdit.setVisibility(8);
        this.mSearchCancelBtn.setVisibility(8);
        this.mClearSearchKeyword.setVisibility(8);
        this.mSearchFailedPrompt.setVisibility(8);
        this.mEnterSearchFlag = false;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws SocketException, UnsupportedEncodingException {
        int loginStrId;
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(getParent()).inflate(R.layout.channel_layout, (ViewGroup) null);
        setContentView(contentView);
        setMessageProcess();
        this.mainHandler = new Handler(this.mMsgHandle);
        this.mChannelData = ChannelData.getInstance();
        boolean phoneAndSmsRemindSetting = new EditPhoneAndSmsRemindSettingFile(this).getPhoneAndSmsRemindSetting();
        if (phoneAndSmsRemindSetting) {
            setPhoneAndSmsListener();
        }
        if (GMScreenGlobalInfo.isClientTypeSlave()) {
            loginStrId = R.string.str_login_as_slave_client;
            Toast.makeText(this, R.string.str_slave_limit, 1).show();
        } else {
            loginStrId = R.string.loading_data;
        }
        this.waitDialog = DialogBuilder.showProgressDialog(getParent(), loginStrId, R.string.please_wait, true, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_load_data_fail);
        this.titleText = (TextView) findViewById(R.id.title);
        this.titleText.setText(getResources().getStringArray(R.array.channel_list_type_string)[this.currentChannelListType]);
        this.ChannelListView = (ListView) findViewById(R.id.channel_list);
        this.allSelectedBtn = (ImageView) findViewById(R.id.all_selected);
        this.allSelectedBtnLayout = (LinearLayout) findViewById(R.id.all_selected_layout);
        Intent intent = getIntent();
        this.NetAddress = intent.getStringExtra("Address");
        this.NetPort = intent.getIntExtra("Port", GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM);
        try {
            CreateSocket cSocket = new CreateSocket(this.NetAddress, this.NetPort);
            this.tcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.tabHost = (TabHost) getParent().findViewById(R.id.tabhost);
        this.tabWidget = this.tabHost.getTabWidget();
        this.editMenu = (LinearLayout) findViewById(R.id.edit_menu);
        this.editMoveMenu = (LinearLayout) findViewById(R.id.edit_move_menu);
        this.editDeleteMenu = (LinearLayout) findViewById(R.id.edit_delete_menu);
        this.editLockMenu = (LinearLayout) findViewById(R.id.edit_lock_menu);
        this.editFavorMenu = (LinearLayout) findViewById(R.id.edit_favor_menu);
        this.editRenameMenu = (LinearLayout) findViewById(R.id.edit_rename_menu);
        this.mEditSortMenu = (LinearLayout) findViewById(R.id.edit_sort_menu);
        this.editMoveIcon = (ImageView) findViewById(R.id.edit_move);
        this.editDeleteIcon = (ImageView) findViewById(R.id.edit_delete);
        this.editLockIcon = (ImageView) findViewById(R.id.edit_lock);
        this.editFavorIcon = (ImageView) findViewById(R.id.edit_favor);
        this.editRenameIcon = (ImageView) findViewById(R.id.edit_rename);
        this.mEditSortIcon = (ImageView) findViewById(R.id.edit_sort);
        this.editMoveText = (TextView) findViewById(R.id.edit_move_string);
        this.editDeleteText = (TextView) findViewById(R.id.edit_delete_string);
        this.editLockText = (TextView) findViewById(R.id.edit_lock_string);
        this.editFavorText = (TextView) findViewById(R.id.edit_favor_string);
        this.editRenameText = (TextView) findViewById(R.id.edit_rename_string);
        this.mEditSortText = (TextView) findViewById(R.id.edit_sort_string);
        this.editBtn = (Button) findViewById(R.id.editMode);
        this.DoneBtn = (Button) findViewById(R.id.DoneMode);
        this.TypeSwitch = (Button) findViewById(R.id.ChannelType);
        this.channelTypeArrow = (ImageView) findViewById(R.id.channel_type_arrow);
        this.mSearchChannelLayout = (LinearLayout) findViewById(R.id.search_channel_layout);
        this.mSearchChannelEdit = (EditText) findViewById(R.id.search_channel_text);
        this.mClearSearchKeyword = (ImageView) findViewById(R.id.clear_search_input);
        this.mSearchCancelBtn = (Button) findViewById(R.id.search_cancel);
        this.mSearchFailedPrompt = (LinearLayout) findViewById(R.id.search_failed);
        setSearchMenuDisable();
        this.parser = ParserFactory.getParser();
        this.titleText.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.39
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsChannelListActivity.this.showChannelTypePopupWindow(v);
            }
        });
        try {
            this.tcpSocket.setSoTimeout(GamesStatusCodes.STATUS_MILESTONE_CLAIMED_PREVIOUSLY);
            this.ChannelListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.40
                @Override // android.widget.AdapterView.OnItemLongClickListener
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) throws NumberFormatException {
                    GsChannelListActivity.this.longClickPos = position;
                    GsChannelListActivity.this.enterEditMode();
                    return true;
                }
            });
            this.ChannelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.41
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) throws SocketException, UnsupportedEncodingException {
                    if (GsChannelListActivity.enable_edit) {
                        if (GsChannelListActivity.this.isEnableMove) {
                            List<DataConvertChannelModel> moveItems = new ArrayList<>();
                            for (int loop = GsChannelListActivity.this.mCurrentChannelList.size() - 1; loop >= 0; loop--) {
                                if (((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(loop)).getSelectedFlag()) {
                                    DataConvertChannelModel item = (DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(loop);
                                    item.setMoveToPosition(((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).GetProgramId());
                                    item.setChannelTpye(((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(loop)).getChannelTpye());
                                    item.SetProgramId(((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(loop)).GetProgramId());
                                    moveItems.add(item);
                                }
                            }
                            if (!moveItems.isEmpty()) {
                                List<DataConvertChannelModel> channelList = GsChannelListActivity.this.mChannelData.getChannelListByTvRadioType();
                                channelList.removeAll(moveItems);
                                if (position > channelList.size()) {
                                    for (int loop2 = moveItems.size() - 1; loop2 >= 0; loop2--) {
                                        channelList.add(channelList.size(), moveItems.get(loop2));
                                    }
                                } else {
                                    for (int loop3 = 0; loop3 < moveItems.size(); loop3++) {
                                        channelList.add(position, moveItems.get(loop3));
                                    }
                                }
                                GsChannelListActivity.this.mChannelData.channelListOfTvOrRadioChanged(channelList);
                                GsChannelListActivity.this.editMoveMenu.setBackgroundResource(R.drawable.edit_menu_background);
                                GsChannelListActivity.this.initItemChecked();
                                GsChannelListActivity.this.isEnableMove = false;
                                GsChannelListActivity.this.mCurrentChannelList = GsChannelListActivity.this.mChannelData.getChannelListByProgramType(GsChannelListActivity.this.mChannelData.getChannelListByTvRadioType(), GsChannelListActivity.this.currentChannelListType);
                                GsChannelListActivity.this.setCurrentChannelListDispIndex();
                                GsChannelListActivity.this.mOriginalChannelListModels = GsChannelListActivity.this.mCurrentChannelList;
                                GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                                try {
                                    GsChannelListActivity.this.parser = ParserFactory.getParser();
                                    byte[] buffer = GsChannelListActivity.this.parser.serialize(moveItems, GlobalConstantValue.GMS_MSG_DO_CHANNEL_MOVE).getBytes("UTF-8");
                                    GsSendSocket.sendSocketToStb(buffer, GsChannelListActivity.this.tcpSocket, 0, buffer.length, GlobalConstantValue.GMS_MSG_DO_CHANNEL_MOVE);
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                                if (GsChannelListActivity.this.waitDialog.isShowing()) {
                                    GsChannelListActivity.this.waitDialog.dismiss();
                                }
                                GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog(GsChannelListActivity.this.getParent(), R.string.moving_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_operate_fail);
                            }
                            return;
                        }
                        return;
                    }
                    GsChannelListActivity.this.mChannelListClickCount++;
                    switch (GsChannelListActivity.this.mChannelListClickCount) {
                        case 1:
                            GsChannelListActivity.this.mFirstChannelListClickTime = (int) System.currentTimeMillis();
                            break;
                        case 2:
                            GsChannelListActivity.this.mSecChannelListClickTime = (int) System.currentTimeMillis();
                            if (GsChannelListActivity.this.mSecChannelListClickTime - GsChannelListActivity.this.mFirstChannelListClickTime >= 1000) {
                                GsChannelListActivity.this.mFirstChannelListClickTime = GsChannelListActivity.this.mSecChannelListClickTime;
                                GsChannelListActivity.this.mChannelListClickCount = 1;
                            } else {
                                GsChannelListActivity.this.trueNewChannelInStb(position);
                                if (GsChannelListActivity.this.mEnterSearchFlag) {
                                    GsChannelListActivity.this.setSearchMenuDisable();
                                    GsChannelListActivity.this.tabWidget.setVisibility(0);
                                    GsChannelListActivity.this.mCurrentChannelList = GsChannelListActivity.this.mOriginalChannelListModels;
                                    GsChannelListActivity.this.inputManager.hideSoftInputFromWindow(GsChannelListActivity.this.mSearchChannelEdit.getWindowToken(), 0);
                                    ((list_single_button_adapter) GsChannelListActivity.this.ChannelListView.getAdapter()).notifyDataSetChanged();
                                    GsChannelListActivity.this.adjustSelectionOfChannelListView(false);
                                }
                                GsChannelListActivity.this.mFirstChannelListClickTime = 0;
                                GsChannelListActivity.this.mChannelListClickCount = 0;
                            }
                            GsChannelListActivity.this.mSecChannelListClickTime = 0;
                            break;
                    }
                }
            });
            this.mSearchChannelLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.42
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!GsChannelListActivity.this.mEnterSearchFlag) {
                        GsChannelListActivity.this.mSearchChannelKeywords = "";
                        GsChannelListActivity.this.mSearchChannelListModels = GsChannelListActivity.this.mCurrentChannelList;
                        GsChannelListActivity.this.mOriginalChannelListModels = GsChannelListActivity.this.mCurrentChannelList;
                        GsChannelListActivity.this.tabWidget.setVisibility(8);
                        GsChannelListActivity.this.setSearchMenuEnable();
                        GsChannelListActivity.this.inputManager = (InputMethodManager) GsChannelListActivity.this.mSearchChannelEdit.getContext().getSystemService("input_method");
                        GsChannelListActivity.this.inputManager.showSoftInput(GsChannelListActivity.this.mSearchChannelEdit, 0);
                    }
                }
            });
            this.mSearchChannelEdit.addTextChangedListener(new TextWatcher() { // from class: mktvsmart.screen.channel.GsChannelListActivity.43
                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) throws NumberFormatException {
                    GsChannelListActivity.this.mChannelListChangeFlag = false;
                    GsChannelListActivity.this.findChannel();
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                }
            });
            this.mClearSearchKeyword.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.44
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    GsChannelListActivity.this.mSearchChannelEdit.setText("");
                    GsChannelListActivity.this.mSearchChannelKeywords = "";
                }
            });
            this.mSearchCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.45
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    GsChannelListActivity.this.setSearchMenuDisable();
                    if (!GsChannelListActivity.enable_edit) {
                        GsChannelListActivity.this.tabWidget.setVisibility(0);
                    }
                    GsChannelListActivity.this.mCurrentChannelList = GsChannelListActivity.this.mOriginalChannelListModels;
                    GsChannelListActivity.this.inputManager.hideSoftInputFromWindow(GsChannelListActivity.this.mSearchChannelEdit.getWindowToken(), 0);
                    ((list_single_button_adapter) GsChannelListActivity.this.ChannelListView.getAdapter()).notifyDataSetChanged();
                    GsChannelListActivity.this.ChannelListView.post(new Runnable() { // from class: mktvsmart.screen.channel.GsChannelListActivity.45.1
                        @Override // java.lang.Runnable
                        public void run() {
                            GsChannelListActivity.this.adjustSelectionOfChannelListView(true);
                        }
                    });
                }
            });
            this.editBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.46
                @Override // android.view.View.OnClickListener
                public void onClick(View v) throws NumberFormatException {
                    if (!GsChannelListActivity.this.mCurrentChannelList.isEmpty()) {
                        GsChannelListActivity.this.enterEditMode();
                        GsChannelListActivity.this.editBtn.setVisibility(8);
                        GsChannelListActivity.this.DoneBtn.setVisibility(0);
                        return;
                    }
                    Toast.makeText(GsChannelListActivity.this, R.string.no_program, 0).show();
                }
            });
            this.DoneBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.47
                @Override // android.view.View.OnClickListener
                public void onClick(View v) throws NumberFormatException {
                    if (GsChannelListActivity.this.isEnableMove) {
                        GsChannelListActivity.this.editMoveMenu.setBackgroundResource(R.drawable.edit_menu_background);
                        GsChannelListActivity.this.isEnableMove = false;
                    }
                    if (GsChannelListActivity.this.mEnterSearchFlag) {
                        GsChannelListActivity.this.setSearchMenuDisable();
                        if (GsChannelListActivity.this.inputManager != null) {
                            GsChannelListActivity.this.inputManager.hideSoftInputFromWindow(GsChannelListActivity.this.mSearchChannelEdit.getWindowToken(), 0);
                        }
                    }
                    GsChannelListActivity.this.exitEditMode();
                }
            });
            this.TypeSwitch.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.48
                List<DataConvertOneDataModel> tvTypeModels = null;

                @Override // android.view.View.OnClickListener
                public void onClick(View v) throws NumberFormatException, UnsupportedEncodingException {
                    switch (DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type()) {
                        case 0:
                            GsChannelListActivity.this.TypeSwitch.setText(GsChannelListActivity.this.getResources().getText(R.string.type_tv));
                            DataConvertChannelTypeModel.setCurrent_channel_tv_radio_type(1);
                            break;
                        case 1:
                            GsChannelListActivity.this.TypeSwitch.setText(GsChannelListActivity.this.getResources().getText(R.string.type_radio));
                            DataConvertChannelTypeModel.setCurrent_channel_tv_radio_type(0);
                            break;
                    }
                    GsChannelListActivity.this.mCurrentChannelList = GsChannelListActivity.this.mChannelData.getChannelListByProgramType(GsChannelListActivity.this.mChannelData.getChannelListByTvRadioType(), GsChannelListActivity.this.currentChannelListType);
                    GsChannelListActivity.this.currentChannelListType = 0;
                    if (GsChannelListActivity.this.mEnterSearchFlag) {
                        GsChannelListActivity.this.mChannelListChangeFlag = true;
                    }
                    GsChannelListActivity.this.setCurrentChannelListDispIndex();
                    GsChannelListActivity.this.mOriginalChannelListModels = GsChannelListActivity.this.mCurrentChannelList;
                    if (!GsChannelListActivity.this.mEnterSearchFlag) {
                        if (GsChannelListActivity.this.channelListAdapter != null) {
                            GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                        }
                        GsChannelListActivity.this.adjustSelectionOfChannelListView(false);
                    } else {
                        GsChannelListActivity.this.findChannel();
                    }
                    GsChannelListActivity.this.titleText.setText(GsChannelListActivity.this.getResources().getStringArray(R.array.channel_list_type_string)[GsChannelListActivity.this.currentChannelListType]);
                    this.tvTypeModels = new ArrayList();
                    DataConvertOneDataModel tvTypeModel = new DataConvertOneDataModel();
                    tvTypeModel.setData(new StringBuilder(String.valueOf(DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type())).toString());
                    this.tvTypeModels.add(tvTypeModel);
                    try {
                        byte[] data_buff = GsChannelListActivity.this.parser.serialize(this.tvTypeModels, GlobalConstantValue.GMS_MSG_DO_TV_RADIO_SWITCH).getBytes("UTF-8");
                        GsSendSocket.sendSocketToStb(data_buff, GsChannelListActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_TV_RADIO_SWITCH);
                    } catch (UnsupportedEncodingException e2) {
                        e2.printStackTrace();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            });
            this.allSelectedBtnLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.49
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (GsChannelListActivity.this.allSelectedBtn_selected) {
                        GsChannelListActivity.this.allSelectedBtn.setImageResource(R.drawable.none_selected);
                        for (DataConvertChannelModel model : GsChannelListActivity.this.mCurrentChannelList) {
                            model.setSelectedFlag(false);
                        }
                        GsChannelListActivity.this.allSelectedBtn_selected = false;
                    } else {
                        GsChannelListActivity.this.allSelectedBtn.setImageResource(R.drawable.all_selected);
                        for (DataConvertChannelModel model2 : GsChannelListActivity.this.mCurrentChannelList) {
                            model2.setSelectedFlag(true);
                        }
                        GsChannelListActivity.this.allSelectedBtn_selected = true;
                    }
                    GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                }
            });
            this.editMoveMenu.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.50
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (!GsChannelListActivity.this.isEnableMove) {
                        GsChannelListActivity.this.editMoveMenu.setBackgroundResource(R.drawable.edit_menu_background_select);
                        GsChannelListActivity.this.isEnableMove = true;
                    } else {
                        GsChannelListActivity.this.editMoveMenu.setBackgroundResource(R.drawable.edit_menu_background);
                        GsChannelListActivity.this.isEnableMove = false;
                    }
                    GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                }
            });
            this.editRenameMenu.setOnClickListener(this.mRenameMenuOnClickListener);
            this.editFavorMenu.setOnClickListener(new AnonymousClass51());
            this.editLockMenu.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.52
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    GsChannelListActivity.this.repeatPassword = false;
                    GsChannelListActivity.this.inputPermissionPassword();
                    GsChannelListActivity.this.passwordType = 1;
                }
            });
            this.editDeleteMenu.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.53
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    CommonCofirmDialog showDownDialog = new CommonCofirmDialog(GsChannelListActivity.this.getParent());
                    showDownDialog.setmTitle(GsChannelListActivity.this.getResources().getString(R.string.delete_channel_title));
                    showDownDialog.setmContent(GsChannelListActivity.this.getResources().getString(R.string.delete_channel_content));
                    showDownDialog.setOnButtonClickListener(GsChannelListActivity.this.mDeleteMenuOnClickListener);
                    showDownDialog.show();
                }
            });
            this.mEditSortMenu.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.54
                @Override // android.view.View.OnClickListener
                public void onClick(View v) throws UnsupportedEncodingException {
                    GsSendSocket.sendOnlyCommandSocketToStb(GsChannelListActivity.this.tcpSocket, 18);
                }
            });
            this.ChannelListView.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.55
                @Override // android.widget.AbsListView.OnScrollListener
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == 0) {
                        GsChannelListActivity.this.LoadData();
                    }
                }

                @Override // android.widget.AbsListView.OnScrollListener
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    GsChannelListActivity.this.visibleItemCount = visibleItemCount;
                }
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        if (GMScreenGlobalInfo.isClientTypeSlave()) {
            disableSomeFunctionWhenSlave();
        }
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 23);
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 16);
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 20);
        if (GMScreenGlobalInfo.getCurStbInfo().getmSatEnable() == 1) {
            GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 22);
        }
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 24);
        requestProgramListFromTo(0, GMScreenGlobalInfo.getMaxProgramNumPerRequest() - 1);
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 12);
    }

    /* renamed from: mktvsmart.screen.channel.GsChannelListActivity$51, reason: invalid class name */
    class AnonymousClass51 implements View.OnClickListener {
        AnonymousClass51() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                case 30:
                case 31:
                case 32:
                case 71:
                case 72:
                case 74:
                    int selectChannelNum = 0;
                    GsChannelListActivity.this.favorModels = new ArrayList();
                    for (DataConvertChannelModel model : GsChannelListActivity.this.mCurrentChannelList) {
                        if (model.getSelectedFlag()) {
                            GsChannelListActivity.this.favorModels.add(model);
                            selectChannelNum++;
                        }
                    }
                    int[] favMarkArray = new int[selectChannelNum];
                    for (int i = 0; i < GsChannelListActivity.this.favorModels.size(); i++) {
                        favMarkArray[i] = ((DataConvertChannelModel) GsChannelListActivity.this.favorModels.get(i)).GetFavMark();
                    }
                    final GsEditFavorMenu editFavMenu = new GsEditFavorMenu(GsChannelListActivity.this.getParent(), GsChannelListActivity.this.favorModels);
                    editFavMenu.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.51.1
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialog) {
                            if (editFavMenu.isSubmitModify()) {
                                if (GsChannelListActivity.this.waitDialog.isShowing()) {
                                    GsChannelListActivity.this.waitDialog.dismiss();
                                }
                                GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog(GsChannelListActivity.this.getParent(), R.string.adding_fav, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_operate_fail);
                            }
                            GsChannelListActivity.this.allSelectedBtn.setImageResource(R.drawable.none_selected);
                            GsChannelListActivity.this.allSelectedBtn_selected = false;
                            GsChannelListActivity.this.initItemChecked();
                            GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                        }
                    });
                    editFavMenu.setCanceledOnTouchOutside(true);
                    editFavMenu.show();
                    break;
                default:
                    final Dialog favDialog = new Dialog(GsChannelListActivity.this.getParent(), R.style.dialog);
                    LayoutInflater factory = LayoutInflater.from(GsChannelListActivity.this);
                    View view = factory.inflate(R.layout.set_favor_menu, (ViewGroup) null);
                    GridView favView = (GridView) view.findViewById(R.id.favor_menu);
                    Button favorSave = (Button) view.findViewById(R.id.favor_save);
                    Button favorCancel = (Button) view.findViewById(R.id.favor_cancel);
                    GsChannelListActivity.this.mIsChoice = new boolean[GMScreenGlobalInfo.favType.size()];
                    GsChannelListActivity.this.favorModels = new ArrayList();
                    GsChannelListActivity.this.setDisplayFavValue();
                    GsChannelListActivity.this.mFavGridAdater = GsChannelListActivity.this.new grid_adapter(GsChannelListActivity.this);
                    favView.setSelector(new ColorDrawable(0));
                    favView.setAdapter((ListAdapter) GsChannelListActivity.this.mFavGridAdater);
                    favView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.51.2
                        @Override // android.widget.AdapterView.OnItemClickListener
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            GsChannelListActivity.this.mFavGridAdater.chiceState(position);
                        }
                    });
                    favView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.51.3
                        @Override // android.widget.AdapterView.OnItemLongClickListener
                        public boolean onItemLongClick(AdapterView<?> parent, View view2, final int position, long id) {
                            LayoutInflater inflater = LayoutInflater.from(GsChannelListActivity.this.getParent());
                            LinearLayout favorRenameLayout = (LinearLayout) inflater.inflate(R.layout.input_rename_dialog, (ViewGroup) null);
                            final EditText favRename = (EditText) favorRenameLayout.findViewById(R.id.input_name_edittext);
                            Button renameSaveBtn = (Button) favorRenameLayout.findViewById(R.id.input_name_confirm_btn);
                            Button renameCancelBtn = (Button) favorRenameLayout.findViewById(R.id.input_name_cancel_btn);
                            favRename.setText(GMScreenGlobalInfo.favType.get(position));
                            favRename.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
                            Selection.selectAll(favRename.getText());
                            renameSaveBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.51.3.1
                                @Override // android.view.View.OnClickListener
                                public void onClick(View v2) throws UnsupportedEncodingException {
                                    if (!favRename.getText().toString().equals("")) {
                                        DataConvertFavorModel model2 = new DataConvertFavorModel();
                                        List<DataConvertFavorModel> favorModel = new ArrayList<>();
                                        GMScreenGlobalInfo.favType.set(position, favRename.getText().toString());
                                        model2.SetFavorIndex(position);
                                        model2.SetFavorName(favRename.getText().toString());
                                        favorModel.add(model2);
                                        GsChannelListActivity.this.mFavGridAdater.notifyDataSetChanged();
                                        try {
                                            byte[] dataCommand = GsChannelListActivity.this.parser.serialize(favorModel, GlobalConstantValue.GMS_MSG_DO_FAV_GROUP_RENAME).getBytes("UTF-8");
                                            GsSendSocket.sendSocketToStb(dataCommand, GsChannelListActivity.this.tcpSocket, 0, dataCommand.length, GlobalConstantValue.GMS_MSG_DO_FAV_GROUP_RENAME);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    GsChannelListActivity.this.inputManager.hideSoftInputFromWindow(favRename.getWindowToken(), 0);
                                    GsChannelListActivity.this.mFavorRenameDialog.dismiss();
                                }
                            });
                            renameCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.51.3.2
                                @Override // android.view.View.OnClickListener
                                public void onClick(View v2) {
                                    GsChannelListActivity.this.inputManager.hideSoftInputFromWindow(favRename.getWindowToken(), 0);
                                    GsChannelListActivity.this.mFavorRenameDialog.dismiss();
                                }
                            });
                            GsChannelListActivity.this.mFavorRenameDialog = new Dialog(GsChannelListActivity.this.getParent(), R.style.dialog);
                            GsChannelListActivity.this.mFavorRenameDialog.setContentView(favorRenameLayout);
                            GsChannelListActivity.this.mFavorRenameDialog.setCanceledOnTouchOutside(false);
                            GsChannelListActivity.this.mFavorRenameDialog.show();
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() { // from class: mktvsmart.screen.channel.GsChannelListActivity.51.3.3
                                @Override // java.util.TimerTask, java.lang.Runnable
                                public void run() {
                                    GsChannelListActivity.this.inputManager = (InputMethodManager) favRename.getContext().getSystemService("input_method");
                                    GsChannelListActivity.this.inputManager.showSoftInput(favRename, 0);
                                }
                            }, 200L);
                            return true;
                        }
                    });
                    favorSave.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.51.4
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v2) throws SocketException, UnsupportedEncodingException {
                            int changeFavMark = 0;
                            for (int i2 = 0; i2 < GMScreenGlobalInfo.favType.size(); i2++) {
                                if (GsChannelListActivity.this.mIsChoice[i2]) {
                                    changeFavMark |= GsChannelListActivity.this.mFavValueArray[i2];
                                }
                            }
                            if (changeFavMark != GsChannelListActivity.this.mFavValue) {
                                for (DataConvertChannelModel model2 : GsChannelListActivity.this.favorModels) {
                                    model2.SetFavMark(changeFavMark);
                                }
                                try {
                                    byte[] data_buff = GsChannelListActivity.this.parser.serialize(GsChannelListActivity.this.favorModels, GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK).getBytes("UTF-8");
                                    GsChannelListActivity.this.tcpSocket.setSoTimeout(3000);
                                    GsSendSocket.sendSocketToStb(data_buff, GsChannelListActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                                if (GsChannelListActivity.this.waitDialog.isShowing()) {
                                    GsChannelListActivity.this.waitDialog.dismiss();
                                }
                                GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog(GsChannelListActivity.this.getParent(), R.string.adding_fav, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_operate_fail);
                            }
                            favDialog.dismiss();
                            GsChannelListActivity.this.allSelectedBtn.setImageResource(R.drawable.none_selected);
                            GsChannelListActivity.this.allSelectedBtn_selected = false;
                            GsChannelListActivity.this.initItemChecked();
                            GsChannelListActivity.this.channelListAdapter.notifyDataSetChanged();
                        }
                    });
                    favorCancel.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.51.5
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v2) {
                            favDialog.dismiss();
                        }
                    });
                    favDialog.setContentView(view);
                    favDialog.setCanceledOnTouchOutside(true);
                    favDialog.show();
                    break;
            }
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() throws UnsupportedEncodingException {
        super.onDestroy();
        this.waitDialog.dismiss();
        this.msgProc.recycle();
        this.msgProc.removeProcessCallback(null);
        stopStream();
        this.mChannelData.clearTVRadioProgramList();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) throws NumberFormatException {
        if (event.getKeyCode() == 4 && event.getAction() == 1 && enable_edit) {
            if (this.isEnableMove) {
                this.editMoveMenu.setBackgroundResource(R.drawable.edit_menu_background);
                this.isEnableMove = false;
                this.channelListAdapter.notifyDataSetChanged();
            } else {
                if (this.mEnterSearchFlag) {
                    setSearchMenuDisable();
                }
                exitEditMode();
                this.ChannelListView.setLongClickable(true);
                adjustSelectionOfChannelListView(true);
            }
            this.editBtn.setText(getResources().getText(R.string.edit_str));
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // android.app.Activity
    protected void onResume() throws UnsupportedEncodingException {
        super.onResume();
        this.isInForeground = true;
        stopStream();
        GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 20);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.isInForeground = false;
    }

    @Override // mktvsmart.screen.OnTabActivityResultListener
    public void onTabActivityResult(int requestCode, int resultCode, Intent data) throws SocketException, UnsupportedEncodingException {
        switch (resultCode) {
            case 20:
                this.isFavorChange = data.getBooleanExtra("favorChange", true);
                if (this.isFavorChange) {
                    this.favMark = data.getIntExtra("favorValue", 0);
                    for (DataConvertChannelModel model : this.favorModels) {
                        model.SetFavMark(this.favMark);
                    }
                    this.favMark = 0;
                    try {
                        byte[] data_buff = this.parser.serialize(this.favorModels, GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK).getBytes("UTF-8");
                        this.tcpSocket.setSoTimeout(3000);
                        GsSendSocket.sendSocketToStb(data_buff, this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    if (this.waitDialog.isShowing()) {
                        this.waitDialog.dismiss();
                    }
                    this.waitDialog = DialogBuilder.showProgressDialog(getParent(), R.string.adding_fav, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_operate_fail);
                }
                initItemChecked();
                this.channelListAdapter.notifyDataSetChanged();
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initItemChecked() {
        for (DataConvertChannelModel model : this.mCurrentChannelList) {
            model.setSelectedFlag(false);
        }
    }

    private class grid_adapter extends BaseAdapter {
        private Integer[] favImage = {Integer.valueOf(R.drawable.news), Integer.valueOf(R.drawable.movie), Integer.valueOf(R.drawable.music), Integer.valueOf(R.drawable.sport), Integer.valueOf(R.drawable.education), Integer.valueOf(R.drawable.weather), Integer.valueOf(R.drawable.children), Integer.valueOf(R.drawable.culture)};
        LayoutInflater inflater;

        public grid_adapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return GMScreenGlobalInfo.favType.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return GMScreenGlobalInfo.favType.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public void chiceState(int position) {
            if (GsChannelListActivity.this.mIsChoice[position]) {
                GsChannelListActivity.this.mIsChoice[position] = false;
            } else {
                GsChannelListActivity.this.mIsChoice[position] = true;
            }
            notifyDataSetChanged();
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.set_favor, (ViewGroup) null);
            }
            ImageView favCheckBox = (ImageView) convertView.findViewById(R.id.favor_check);
            ImageView favIcon = (ImageView) convertView.findViewById(R.id.fav_icon);
            TextView favText = (TextView) convertView.findViewById(R.id.favor_text);
            if (position == parent.getChildCount()) {
                if (GsChannelListActivity.this.mIsChoice[position]) {
                    favCheckBox.setImageResource(R.drawable.fav_checkbox);
                    favText.setBackgroundResource(R.drawable.fav_text);
                } else {
                    favCheckBox.setImageResource(R.drawable.fav_checkbox_grey);
                    favText.setBackgroundResource(R.drawable.fav_text_grey);
                }
                favIcon.setImageResource(this.favImage[position].intValue());
                favText.setText(GMScreenGlobalInfo.favType.get(position));
            }
            return convertView;
        }
    }

    private class list_single_button_adapter extends BaseAdapter {
        LayoutInflater inflater;

        private class ViewHolder {
            public ImageView editSelect;
            public LinearLayout editSelectLayout;
            public ImageView favorIcon;
            public LinearLayout linearLayoutProgIndex;
            public ImageView lockIcon;
            public ImageView moreBtnDemarcation;
            public LinearLayout moreButtonLayout;
            public TextView moreChatMenu;
            public TextView moreDeleteMenu;
            public TextView moreEpgMenu;
            public TextView moreLockMenu;
            public LinearLayout moreMenuLayout;
            public TextView morePlayMenu;
            public TextView moreRenameMenu;
            public TextView progIndex;
            public TextView progName;
            public RelativeLayout relativeLayoutProgName;
            public ImageView scrambleIcon;

            private ViewHolder() {
                this.progName = null;
                this.editSelect = null;
            }

            /* synthetic */ ViewHolder(list_single_button_adapter list_single_button_adapterVar, ViewHolder viewHolder) {
                this();
            }
        }

        public list_single_button_adapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            if (GMScreenGlobalInfo.isClientTypeSlave()) {
                return false;
            }
            return super.isEnabled(position);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return GsChannelListActivity.enable_edit ? GsChannelListActivity.this.mCurrentChannelList.size() : GsChannelListActivity.this.mCurrentChannelList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return GsChannelListActivity.enable_edit ? GsChannelListActivity.this.mCurrentChannelList.get(position) : GsChannelListActivity.this.mCurrentChannelList.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public void setEditMoveDisplay(boolean flag) {
            if (!flag || GsChannelListActivity.this.mEnterSearchFlag) {
                GsChannelListActivity.this.editMoveMenu.setClickable(false);
                GsChannelListActivity.this.editMoveIcon.setImageResource(R.drawable.move_unuse);
                GsChannelListActivity.this.editMoveText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.unable_text_color));
            } else {
                GsChannelListActivity.this.editMoveMenu.setClickable(true);
                GsChannelListActivity.this.editMoveIcon.setImageResource(R.drawable.move2);
                GsChannelListActivity.this.editMoveText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.white));
            }
        }

        public void setEditDeleteDisplay(boolean flag) {
            if (flag) {
                GsChannelListActivity.this.editDeleteMenu.setClickable(true);
                GsChannelListActivity.this.editDeleteIcon.setImageResource(R.drawable.delete2);
                GsChannelListActivity.this.editDeleteText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.white));
            } else {
                GsChannelListActivity.this.editDeleteMenu.setClickable(false);
                GsChannelListActivity.this.editDeleteIcon.setImageResource(R.drawable.delete_unuse);
                GsChannelListActivity.this.editDeleteText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.unable_text_color));
            }
        }

        public void setEditLockDisplay(boolean flag) {
            if (flag) {
                GsChannelListActivity.this.editLockMenu.setClickable(true);
                GsChannelListActivity.this.editLockIcon.setImageResource(R.drawable.lock2);
                GsChannelListActivity.this.editLockText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.white));
            } else {
                GsChannelListActivity.this.editLockMenu.setClickable(false);
                GsChannelListActivity.this.editLockIcon.setImageResource(R.drawable.lock_unuse);
                GsChannelListActivity.this.editLockText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.unable_text_color));
            }
        }

        public void setEditRenameDisplay(boolean flag) {
            if (flag) {
                GsChannelListActivity.this.editRenameMenu.setClickable(true);
                GsChannelListActivity.this.editRenameIcon.setImageResource(R.drawable.rename2);
                GsChannelListActivity.this.editRenameText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.white));
            } else {
                GsChannelListActivity.this.editRenameMenu.setClickable(false);
                GsChannelListActivity.this.editRenameIcon.setImageResource(R.drawable.rename_unuse);
                GsChannelListActivity.this.editRenameText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.unable_text_color));
            }
        }

        public void setEditFavDisplay(boolean flag) {
            if (flag) {
                GsChannelListActivity.this.editFavorMenu.setClickable(true);
                GsChannelListActivity.this.editFavorIcon.setImageResource(R.drawable.favor2);
                GsChannelListActivity.this.editFavorText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.white));
            } else {
                GsChannelListActivity.this.editFavorMenu.setClickable(false);
                GsChannelListActivity.this.editFavorIcon.setImageResource(R.drawable.favor_unuse);
                GsChannelListActivity.this.editFavorText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.unable_text_color));
            }
        }

        public void setEditSortDisplay(boolean flag) {
            if (!flag || GsChannelListActivity.this.mEnterSearchFlag) {
                GsChannelListActivity.this.mEditSortMenu.setClickable(false);
                GsChannelListActivity.this.mEditSortIcon.setImageResource(R.drawable.sort_btn_unuse);
                GsChannelListActivity.this.mEditSortText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.unable_text_color));
            } else {
                GsChannelListActivity.this.mEditSortMenu.setClickable(true);
                GsChannelListActivity.this.mEditSortIcon.setImageResource(R.drawable.sort_btn_use);
                GsChannelListActivity.this.mEditSortText.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.white));
            }
        }

        public void setAllSelectedBtndisplay(boolean flag) {
            if (flag) {
                GsChannelListActivity.this.allSelectedBtn.setImageResource(R.drawable.all_selected);
                GsChannelListActivity.this.allSelectedBtn_selected = true;
            } else {
                GsChannelListActivity.this.allSelectedBtn.setImageResource(R.drawable.none_selected);
                GsChannelListActivity.this.allSelectedBtn_selected = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void controlButtomMenu() {
            int selectNum = GsChannelListActivity.this.GetSelectedNum(GsChannelListActivity.this.mCurrentChannelList);
            if (selectNum == 0) {
                setAllSelectedBtndisplay(false);
                setEditMoveDisplay(false);
                setEditDeleteDisplay(false);
                setEditLockDisplay(false);
                setEditRenameDisplay(false);
                setEditFavDisplay(false);
                setEditSortDisplay(true);
                return;
            }
            if (selectNum != 1) {
                if (selectNum == GsChannelListActivity.this.mCurrentChannelList.size()) {
                    setAllSelectedBtndisplay(true);
                    setEditMoveDisplay(false);
                    setEditDeleteDisplay(true);
                    setEditLockDisplay(true);
                    setEditRenameDisplay(false);
                    setEditFavDisplay(true);
                    setEditSortDisplay(false);
                    return;
                }
                setAllSelectedBtndisplay(false);
                setEditMoveDisplay(true);
                setEditDeleteDisplay(true);
                setEditLockDisplay(true);
                setEditRenameDisplay(false);
                setEditFavDisplay(true);
                setEditSortDisplay(false);
                return;
            }
            setAllSelectedBtndisplay(false);
            setEditMoveDisplay(true);
            setEditDeleteDisplay(true);
            setEditLockDisplay(true);
            setEditRenameDisplay(true);
            setEditFavDisplay(true);
            setEditSortDisplay(false);
        }

        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:42:0x0389 -> B:57:0x02a8). Please report as a decompilation issue!!! */
        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            ImageView moreButton;
            if (convertView == null) {
                holder = new ViewHolder(this, null);
                convertView = this.inflater.inflate(R.layout.channellist, (ViewGroup) null);
                holder.linearLayoutProgIndex = (LinearLayout) convertView.findViewById(R.id.linear_layout_prog_index);
                holder.progIndex = (TextView) convertView.findViewById(R.id.prog_index);
                holder.progName = (TextView) convertView.findViewById(R.id.prog_name);
                holder.editSelectLayout = (LinearLayout) convertView.findViewById(R.id.linear_layout_edit_select);
                holder.editSelect = (ImageView) convertView.findViewById(R.id.edit_select);
                holder.relativeLayoutProgName = (RelativeLayout) convertView.findViewById(R.id.relative_layout_prog_name);
                holder.scrambleIcon = (ImageView) convertView.findViewById(R.id.scramble_icon);
                holder.favorIcon = (ImageView) convertView.findViewById(R.id.favor_icon);
                holder.lockIcon = (ImageView) convertView.findViewById(R.id.lock_icon);
                holder.moreButtonLayout = (LinearLayout) convertView.findViewById(R.id.linear_layout_btn_more);
                holder.moreBtnDemarcation = (ImageView) convertView.findViewById(R.id.more_btn_demarcation);
                moreButton = (ImageView) holder.moreButtonLayout.findViewById(R.id.btn_more);
                holder.moreMenuLayout = (LinearLayout) convertView.findViewById(R.id.more_menu);
                holder.moreEpgMenu = (TextView) convertView.findViewById(R.id.more_epg_menu);
                holder.morePlayMenu = (TextView) convertView.findViewById(R.id.more_play_menu);
                holder.moreChatMenu = (TextView) convertView.findViewById(R.id.more_gchat_menu);
                holder.moreRenameMenu = (TextView) convertView.findViewById(R.id.more_rename_menu);
                holder.moreDeleteMenu = (TextView) convertView.findViewById(R.id.more_delete_menu);
                holder.moreLockMenu = (TextView) convertView.findViewById(R.id.more_lock_menu);
            } else {
                holder = (ViewHolder) convertView.getTag();
                moreButton = (ImageView) holder.moreButtonLayout.findViewById(R.id.btn_more);
            }
            holder.moreEpgMenu.setTag(Integer.valueOf(position));
            holder.morePlayMenu.setTag(Integer.valueOf(position));
            holder.moreChatMenu.setTag(Integer.valueOf(position));
            holder.moreButtonLayout.setTag(Integer.valueOf(position));
            holder.editSelectLayout.setTag(Integer.valueOf(position));
            holder.progIndex.setText(new StringBuilder(String.valueOf(((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).getCurrentChannelListDispIndex())).toString());
            holder.progName.setText(((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).getProgramName());
            if (((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).GetIsProgramScramble() == 1) {
                holder.scrambleIcon.setBackgroundResource(R.drawable.scramble_icon);
            } else {
                holder.scrambleIcon.setBackgroundResource(R.drawable.scramble_icon_gray);
            }
            if (((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).GetFavMark() != 0) {
                holder.favorIcon.setBackgroundResource(R.drawable.favor_icon);
            } else {
                holder.favorIcon.setBackgroundResource(R.drawable.favor_icon_grey);
            }
            if (((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).getLockMark() != 0) {
                holder.lockIcon.setBackgroundResource(R.drawable.lock_icon);
            } else {
                holder.lockIcon.setBackgroundResource(R.drawable.lock_icon_gray);
            }
            if (!GsChannelListActivity.enable_edit) {
                holder.editSelect.setVisibility(8);
                holder.moreButtonLayout.setVisibility(0);
                holder.editSelect.setImageResource(R.drawable.checkbox_unselected);
                ((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).setSelectedFlag(false);
                holder.moreButtonLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.list_single_button_adapter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        int current_position = ((Integer) v.getTag()).intValue();
                        if (GsChannelListActivity.this.expandPosition != current_position) {
                            GsChannelListActivity.this.expandPosition = current_position;
                        } else {
                            GsChannelListActivity.this.expandPosition = -1;
                        }
                        ((list_single_button_adapter) GsChannelListActivity.this.ChannelListView.getAdapter()).notifyDataSetChanged();
                    }
                });
                if (position == GsChannelListActivity.this.expandPosition) {
                    holder.moreEpgMenu.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.list_single_button_adapter.2
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) throws SocketException, UnsupportedEncodingException {
                            int current_position = ((Integer) v.getTag()).intValue();
                            if (GsChannelListActivity.this.waitDialog.isShowing()) {
                                GsChannelListActivity.this.waitDialog.dismiss();
                            }
                            GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog(GsChannelListActivity.this.getParent(), R.string.loading_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_load_data_fail);
                            DataConvertChannelModel map = (DataConvertChannelModel) GsChannelListActivity.this.channelListAdapter.getItem(current_position);
                            GsChannelListActivity.this.epg_program_sat_tp_id = map.GetProgramId();
                            GsChannelListActivity.this.epg_program_name = map.getProgramName();
                            DataConvertChannelModel initData = new DataConvertChannelModel();
                            List<DataConvertChannelModel> models = new ArrayList<>();
                            GsChannelListActivity.this.parser = ParserFactory.getParser();
                            initData.SetProgramId(GsChannelListActivity.this.epg_program_sat_tp_id);
                            models.add(initData);
                            try {
                                String data = GsChannelListActivity.this.parser.serialize(models, 5);
                                byte[] data_buff = data.getBytes("UTF-8");
                                GsChannelListActivity.this.tcpSocket.setSoTimeout(3000);
                                GsChannelListActivity.this.in = GsChannelListActivity.this.tcpSocket.getInputStream();
                                GsSendSocket.sendSocketToStb(data_buff, GsChannelListActivity.this.tcpSocket, 0, data_buff.length, 5);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    holder.morePlayMenu.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.list_single_button_adapter.3
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            int position2 = ((Integer) v.getTag()).intValue();
                            if (GsChannelListActivity.this.controlModels != null && GsChannelListActivity.this.controlModels.size() > 0) {
                                if (((DataConvertControlModel) GsChannelListActivity.this.controlModels.get(0)).GetPowerOff() != 0) {
                                    if (((DataConvertControlModel) GsChannelListActivity.this.controlModels.get(0)).GetPswLockSwitch() != 0 && ((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position2)).getIsPlaying() != 1) {
                                        if (((DataConvertChannelModel) GsChannelListActivity.this.channelListAdapter.getItem(position2)).getLockMark() == 0) {
                                            GsChannelListActivity.this.playStream(position2);
                                            return;
                                        }
                                        GsChannelListActivity.this.sat2ipPlayPosition = position2;
                                        GsChannelListActivity.this.passwordType = 3;
                                        GsChannelListActivity.this.inputPermissionPassword();
                                        return;
                                    }
                                    GsChannelListActivity.this.playStream(position2);
                                    return;
                                }
                                CommonCofirmDialog showStbInStandbyDialog = new CommonCofirmDialog(GsChannelListActivity.this.getParent());
                                showStbInStandbyDialog.setmTitle(GsChannelListActivity.this.getResources().getString(R.string.warning_dialog));
                                showStbInStandbyDialog.setmContent(GsChannelListActivity.this.getResources().getString(R.string.stb_standby));
                                showStbInStandbyDialog.setOnButtonClickListener(GsChannelListActivity.this.mStbInStandbyOnClickListener);
                                showStbInStandbyDialog.show();
                            }
                        }
                    });
                    holder.moreRenameMenu.setOnClickListener(GsChannelListActivity.this.mRenameMenuOnClickListener);
                    holder.moreLockMenu.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.list_single_button_adapter.4
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            GsChannelListActivity.this.repeatPassword = false;
                            GsChannelListActivity.this.inputPermissionPassword();
                            GsChannelListActivity.this.passwordType = 1;
                        }
                    });
                    holder.moreDeleteMenu.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.list_single_button_adapter.5
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            CommonCofirmDialog showDownDialog = new CommonCofirmDialog(GsChannelListActivity.this.getParent());
                            showDownDialog.setmTitle(GsChannelListActivity.this.getResources().getString(R.string.delete_channel_title));
                            showDownDialog.setmContent(GsChannelListActivity.this.getResources().getString(R.string.delete_channel_content));
                            showDownDialog.setOnButtonClickListener(GsChannelListActivity.this.mDeleteMenuOnClickListener);
                            showDownDialog.show();
                        }
                    });
                    holder.moreChatMenu.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.list_single_button_adapter.6
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) throws SocketException, UnsupportedEncodingException {
                            int current_position = ((Integer) v.getTag()).intValue();
                            if (GsChannelListActivity.this.waitDialog.isShowing()) {
                                GsChannelListActivity.this.waitDialog.dismiss();
                            }
                            GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog(GsChannelListActivity.this.getParent(), R.string.loading_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_load_data_fail);
                            DataConvertChannelModel map = (DataConvertChannelModel) GsChannelListActivity.this.channelListAdapter.getItem(current_position);
                            DataConvertChannelModel chatModel = new DataConvertChannelModel();
                            List<DataConvertChannelModel> models = new ArrayList<>();
                            chatModel.SetProgramId(map.GetProgramId());
                            chatModel.setChannelTpye(map.getChannelTpye());
                            models.add(chatModel);
                            GsChannelListActivity.this.parser = ParserFactory.getParser();
                            try {
                                byte[] data_buff = GsChannelListActivity.this.parser.serialize(models, GlobalConstantValue.GMS_MSG_GCHAT_DO_START).getBytes("UTF-8");
                                GsChannelListActivity.this.tcpSocket.setSoTimeout(3000);
                                GsSendSocket.sendSocketToStb(data_buff, GsChannelListActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_GCHAT_DO_START);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    if (!GMScreenGlobalInfo.isChatSupport()) {
                        holder.moreChatMenu.setVisibility(8);
                    }
                    moreButton.setBackgroundResource(R.drawable.channel_list_more_selected);
                    holder.moreMenuLayout.setVisibility(0);
                    if (((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).GetHaveEPG() == 1) {
                        holder.moreEpgMenu.setClickable(true);
                        holder.moreEpgMenu.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.channel_list_more_menu_text_color));
                        holder.moreEpgMenu.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.channel_list_more_menu_epg, 0, 0);
                    } else {
                        holder.moreEpgMenu.setClickable(false);
                        holder.moreEpgMenu.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.channel_list_more_menu_unable_text_color));
                        holder.moreEpgMenu.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.channel_list_more_menu_epg_unable, 0, 0);
                    }
                    try {
                        if (GMScreenGlobalInfo.getCurStbInfo().getmSat2ipEnable() != 2 && GsChannelListActivity.this.mChannelData.canSat2ipChannelPlay(GsChannelListActivity.this.mChannelData.getCurrentPlayingProgram(), (DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position))) {
                            holder.morePlayMenu.setClickable(true);
                            holder.morePlayMenu.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.channel_list_more_menu_text_color));
                            holder.morePlayMenu.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.channel_list_more_menu_play, 0, 0);
                        } else {
                            holder.morePlayMenu.setClickable(false);
                            holder.morePlayMenu.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.channel_list_more_menu_unable_text_color));
                            holder.morePlayMenu.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.channel_list_more_menu_play_unable, 0, 0);
                        }
                    } catch (ProgramNotFoundException e1) {
                        Log.d("ProgramNotFoundException", e1.getMessage());
                        e1.printStackTrace();
                    }
                } else {
                    moreButton.setBackgroundResource(R.drawable.channel_list_more);
                    holder.moreMenuLayout.setVisibility(8);
                }
                if (((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).getIsPlaying() == 1) {
                    holder.relativeLayoutProgName.setBackgroundResource(R.drawable.list_item_focus);
                    holder.linearLayoutProgIndex.setBackgroundResource(R.drawable.list_item_index_focus);
                    holder.progName.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.white));
                    moreButton.setBackgroundResource(R.drawable.channel_list_more_white);
                    holder.moreButtonLayout.setBackgroundResource(R.drawable.list_item_index_focus);
                    holder.moreBtnDemarcation.setVisibility(8);
                } else {
                    holder.relativeLayoutProgName.setBackgroundResource(R.drawable.disp_channel);
                    holder.linearLayoutProgIndex.setBackgroundResource(R.drawable.disp_index);
                    holder.progName.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.black));
                    holder.moreButtonLayout.setBackgroundResource(GsChannelListActivity.this.getResources().getColor(R.color.transparent));
                    holder.moreBtnDemarcation.setVisibility(0);
                }
            } else {
                holder.moreButtonLayout.setVisibility(8);
                holder.moreMenuLayout.setVisibility(8);
                holder.editSelect.setVisibility(0);
                if (position == GsChannelListActivity.this.longClickPos) {
                    holder.editSelect.setImageResource(R.drawable.checkbox_selected);
                    ((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(GsChannelListActivity.this.longClickPos)).setSelectedFlag(true);
                    GsChannelListActivity.this.longClickPos = -1;
                }
                holder.relativeLayoutProgName.setBackgroundResource(R.drawable.disp_channel);
                holder.linearLayoutProgIndex.setBackgroundResource(R.drawable.disp_index);
                holder.progName.setTextColor(GsChannelListActivity.this.getResources().getColor(R.color.black));
                if (((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(position)).getSelectedFlag()) {
                    holder.editSelect.setImageResource(R.drawable.checkbox_selected);
                } else {
                    holder.editSelect.setImageResource(R.drawable.checkbox_unselected);
                }
                holder.editSelectLayout.setEnabled(!GsChannelListActivity.this.isEnableMove);
                controlButtomMenu();
                holder.editSelectLayout.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.list_single_button_adapter.7
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        int CurPos = ((Integer) v.getTag()).intValue();
                        if (((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(CurPos)).getSelectedFlag()) {
                            ((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(CurPos)).setSelectedFlag(false);
                        } else {
                            ((DataConvertChannelModel) GsChannelListActivity.this.mCurrentChannelList.get(CurPos)).setSelectedFlag(true);
                        }
                        list_single_button_adapter.this.controlButtomMenu();
                        list_single_button_adapter.this.notifyDataSetChanged();
                    }
                });
            }
            convertView.setTag(holder);
            GsChannelListActivity.this.mFirstVisibleChannelIdex = GsChannelListActivity.this.ChannelListView.getFirstVisiblePosition();
            GsChannelListActivity.this.mLastVisibleChannelIndex = GsChannelListActivity.this.ChannelListView.getLastVisiblePosition();
            return convertView;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void inputPermissionPassword() {
        LayoutInflater inflater = LayoutInflater.from(getParent());
        LinearLayout inputPswLayout = (LinearLayout) inflater.inflate(R.layout.input_passowrd_dialog, (ViewGroup) null);
        TextView name = (TextView) inputPswLayout.findViewById(R.id.input_password_title);
        final EditText edit = (EditText) inputPswLayout.findViewById(R.id.input_password_edittext);
        Button inputPswCancelBtn = (Button) inputPswLayout.findViewById(R.id.input_psw_cancel_btn);
        switch (this.passwordType) {
            case 1:
            case 2:
                if (!this.repeatPassword) {
                    name.setText(R.string.input_password_title);
                    break;
                } else {
                    this.repeatPassword = false;
                    name.setText(R.string.input_password_again);
                    break;
                }
            case 3:
                name.setText(this.mCurrentChannelList.get(this.sat2ipPlayPosition).getProgramName());
                break;
        }
        edit.addTextChangedListener(new TextWatcher() { // from class: mktvsmart.screen.channel.GsChannelListActivity.56
            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) throws SocketException, UnsupportedEncodingException {
                int responseStyle;
                int inputPswNum = edit.getText().toString().length();
                if (inputPswNum == GMScreenGlobalInfo.getmMaxPasswordNum()) {
                    if (GsChannelListActivity.this.passwordType != 1 && GsChannelListActivity.this.passwordType != 3) {
                        GsChannelListActivity.this.passwordType = 0;
                        responseStyle = GlobalConstantValue.GMS_MSG_DO_PLAYING_CHANNEL_PASSWORD_CHECK;
                    } else {
                        responseStyle = GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK;
                    }
                    List<DataConvertOneDataModel> lockModels = new ArrayList<>();
                    DataConvertOneDataModel model = new DataConvertOneDataModel();
                    model.setData(edit.getText().toString());
                    lockModels.add(model);
                    try {
                        GsChannelListActivity.this.parser = ParserFactory.getParser();
                        byte[] data_buff = GsChannelListActivity.this.parser.serialize(lockModels, responseStyle).getBytes("UTF-8");
                        GsChannelListActivity.this.tcpSocket.setSoTimeout(3000);
                        GsSendSocket.sendSocketToStb(data_buff, GsChannelListActivity.this.tcpSocket, 0, data_buff.length, responseStyle);
                        GsChannelListActivity.this.inputManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GsChannelListActivity.this.pswInputDialog.dismiss();
                    GsChannelListActivity.this.waitDialog = DialogBuilder.showProgressDialog((Context) GsChannelListActivity.this.getParent(), GsChannelListActivity.this.getString(R.string.verify_password), GsChannelListActivity.this.getString(R.string.please_wait), true);
                    GsChannelListActivity.this.pswInputDialog.dismiss();
                }
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        });
        inputPswCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.57
            @Override // android.view.View.OnClickListener
            public void onClick(View v) throws UnsupportedEncodingException {
                switch (GsChannelListActivity.this.passwordType) {
                    case 1:
                        GsChannelListActivity.this.repeatPassword = false;
                        break;
                    case 2:
                        GsSendSocket.sendOnlyCommandSocketToStb(GsChannelListActivity.this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_INPUT_PASSWORD_CANCEL);
                        break;
                }
                GsChannelListActivity.this.passwordType = 0;
                GsChannelListActivity.this.pswInputDialog.dismiss();
            }
        });
        if (this.pswInputDialog != null && this.pswInputDialog.isShowing()) {
            this.pswInputDialog.dismiss();
        }
        this.pswInputDialog = new Dialog(getParent(), R.style.dialog);
        this.pswInputDialog.setContentView(inputPswLayout);
        this.pswInputDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: mktvsmart.screen.channel.GsChannelListActivity.58
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) throws UnsupportedEncodingException {
                switch (GsChannelListActivity.this.passwordType) {
                    case 1:
                        GsChannelListActivity.this.repeatPassword = false;
                        break;
                    case 2:
                        GsSendSocket.sendOnlyCommandSocketToStb(GsChannelListActivity.this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_INPUT_PASSWORD_CANCEL);
                        break;
                }
                GsChannelListActivity.this.passwordType = 0;
                GsChannelListActivity.this.pswInputDialog.dismiss();
            }
        });
        this.pswInputDialog.setCanceledOnTouchOutside(false);
        this.pswInputDialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { // from class: mktvsmart.screen.channel.GsChannelListActivity.59
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                GsChannelListActivity.this.inputManager = (InputMethodManager) edit.getContext().getSystemService("input_method");
                GsChannelListActivity.this.inputManager.showSoftInput(edit, 0);
            }
        }, 200L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<String> getSortTypeArray() throws Resources.NotFoundException {
        ArrayList<String> data = new ArrayList<>();
        GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
        if (login.getPlatform_id() == 30) {
            for (String str : getResources().getStringArray(R.array.sortType_trident8471)) {
                data.add(str);
            }
        } else {
            String[] sortTypeArr = getResources().getStringArray(R.array.sortType);
            for (String str2 : sortTypeArr) {
                data.add(str2);
            }
        }
        return data;
    }

    private class sort_adapter extends BaseAdapter {
        private int commonTextColor;
        LayoutInflater inflater;
        private int mMacroFlag;
        private ArrayList<String> mSortList;
        private int mSortType;

        public sort_adapter(Context context, int sortType, ArrayList<String> list, int mMacroFlag) {
            this.inflater = LayoutInflater.from(context);
            this.mSortList = list;
            this.mMacroFlag = mMacroFlag;
            this.mSortType = sortType;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            switch (this.mMacroFlag) {
                case 0:
                    int sortTypeNum = this.mSortList.size() - 2;
                    return sortTypeNum;
                case 1:
                case 2:
                    int sortTypeNum2 = this.mSortList.size() - 1;
                    return sortTypeNum2;
                case 3:
                    int sortTypeNum3 = this.mSortList.size();
                    return sortTypeNum3;
                default:
                    return 0;
            }
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            switch (this.mMacroFlag) {
                case 0:
                    position += 2;
                    break;
                case 1:
                    if (position > 0) {
                        position++;
                        break;
                    }
                    break;
                case 2:
                    position++;
                    break;
            }
            return this.mSortList.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            int sortTypeIndex;
            switch (this.mMacroFlag) {
                case 0:
                    sortTypeIndex = position + 2;
                    break;
                case 1:
                    if (position > 0) {
                        sortTypeIndex = position + 1;
                        break;
                    } else {
                        sortTypeIndex = position;
                        break;
                    }
                case 2:
                    sortTypeIndex = position + 1;
                    break;
                default:
                    sortTypeIndex = position;
                    break;
            }
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.pop_window_item_layout, (ViewGroup) null);
            }
            RelativeLayout relativeSortName = (RelativeLayout) convertView.findViewById(R.id.pop_window_list_name);
            ImageView sortSelect = (ImageView) convertView.findViewById(R.id.list_select);
            TextView sortName = (TextView) convertView.findViewById(R.id.list_name);
            sortName.setText(this.mSortList.get(sortTypeIndex));
            relativeSortName.setBackgroundResource(R.drawable.disp_channel);
            if (position == this.mSortType) {
                sortSelect.setImageResource(R.drawable.list_selected);
            } else {
                sortSelect.setImageResource(R.drawable.list_unselected);
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

    private class MyPhoneStateListener extends PhoneStateListener {
        private int mTimeMark;

        private MyPhoneStateListener() {
            this.mTimeMark = 0;
        }

        /* synthetic */ MyPhoneStateListener(GsChannelListActivity gsChannelListActivity, MyPhoneStateListener myPhoneStateListener) {
            this();
        }

        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int state, String incomingNumber) throws UnsupportedEncodingException {
            Log.e("PhoneCallState", "state " + state);
            Log.e("PhoneCallState", "Incoming number " + incomingNumber);
            Time time = new Time();
            time.setToNow();
            int timeNow = (time.hour * 60 * 60) + (time.minute * 60) + time.second;
            if (state == 1 && incomingNumber.length() > 0 && timeNow - this.mTimeMark > 2) {
                DataConvertOneDataModel incommingNum = new DataConvertOneDataModel();
                List<DataConvertOneDataModel> modelList = new ArrayList<>();
                incommingNum.setData(incomingNumber);
                modelList.add(incommingNum);
                try {
                    byte[] buffer = GsChannelListActivity.this.parser.serialize(modelList, GlobalConstantValue.GMS_MSG_DO_INCOMMING_CALL_NUM_DISPLAY).getBytes("UTF-8");
                    GsSendSocket.sendSocketToStb(buffer, GsChannelListActivity.this.tcpSocket, 0, buffer.length, GlobalConstantValue.GMS_MSG_DO_INCOMMING_CALL_NUM_DISPLAY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.mTimeMark = timeNow;
            }
        }
    }

    private class SMSContentObserver extends ContentObserver {
        public SMSContentObserver() {
            super(new Handler());
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) throws UnsupportedEncodingException {
            super.onChange(selfChange);
            Cursor cursor = GsChannelListActivity.this.mResolver.query(GsChannelListActivity.this.mUri, null, null, null, "_id DESC LIMIT 1");
            while (cursor.moveToNext()) {
                String sender = cursor.getString(cursor.getColumnIndex("address"));
                int type = cursor.getInt(cursor.getColumnIndex(PlaylistSQLiteHelper.COL_TYPE));
                if (type == 1) {
                    DataConvertOneDataModel smsMessageNum = new DataConvertOneDataModel();
                    List<DataConvertOneDataModel> modelList = new ArrayList<>();
                    smsMessageNum.setData(sender);
                    modelList.add(smsMessageNum);
                    try {
                        byte[] buffer = GsChannelListActivity.this.parser.serialize(modelList, GlobalConstantValue.GMS_MSG_DO_SMS_NUM_DISPLAY).getBytes("UTF-8");
                        GsSendSocket.sendSocketToStb(buffer, GsChannelListActivity.this.tcpSocket, 0, buffer.length, GlobalConstantValue.GMS_MSG_DO_SMS_NUM_DISPLAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("GsChannelListActivity", "onConfigurationChanged");
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }
}
