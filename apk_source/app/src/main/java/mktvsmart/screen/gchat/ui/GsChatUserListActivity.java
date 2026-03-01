package mktvsmart.screen.gchat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsSendSocket;
import mktvsmart.screen.R;
import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import mktvsmart.screen.gchat.bean.GsChatRoomInfo;
import mktvsmart.screen.gchat.bean.GsChatSetting;
import mktvsmart.screen.gchat.bean.GsChatUser;
import mktvsmart.screen.gchat.client.ChatClientAsync;
import mktvsmart.screen.gchat.client.ChatConnector;
import mktvsmart.screen.gchat.client.GChatClientListener;
import mktvsmart.screen.json.serialize.GChatJsonSerializeRequestBlackList;
import mktvsmart.screen.json.serialize.GChatJsonSerializeRequestUserList;
import mktvsmart.screen.json.serialize.GChatJsonSerializeResponse;
import mktvsmart.screen.json.serialize.GChatJsonSerializeSetBlackList;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.util.ADSProgressDialog;
import mktvsmart.screen.util.DialogBuilder;

/* loaded from: classes.dex */
public class GsChatUserListActivity extends Activity {
    private static final String TAG = GsChatUserListActivity.class.getSimpleName();
    private LinearLayout mBackBtn;
    private SwipeMenuListView mBlockList;
    private ChatUserListAdapter mBlockListAdapter;
    private SwipeMenu mBlockMenu;
    private LinearLayout mBlockedUsersBtn;
    private ImageView mBlockedUsersIcon;
    private TextView mBlockedUsersText;
    private int mCurrentRoomID;
    private LinearLayout mCurrentUsersBtn;
    private ImageView mCurrentUsersIcon;
    private TextView mCurrentUsersText;
    private boolean mIsStbChat;
    private int mMyUserId;
    private Socket mTcpSocket;
    private SwipeMenu mUnblockMenu;
    private SwipeMenuListView mUserList;
    private ChatUserListAdapter mUserListAdapter;
    private ADSProgressDialog mWaitDialog;
    private MessageProcessor msgProc;
    private List<GsChatUser> mUserListArrays = new ArrayList();
    private List<GsChatUser> mBlockListArrays = new ArrayList();
    private DataParser mParser = ParserFactory.getParser();
    private ChatClientAsync mChatClient = ChatClientAsync.getInstance();
    Runnable mRequestDataFailRunable = new Runnable() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.1
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Toast.makeText(GsChatUserListActivity.this, GsChatUserListActivity.this.getResources().getString(R.string.str_load_data_fail), 0).show();
            GsChatUserListActivity.this.onBackPressed();
        }
    };
    private View.OnClickListener listBtnOnClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.2
        AnonymousClass2() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (v.getId() == R.id.current_users_btn) {
                GsChatUserListActivity.this.mCurrentUsersBtn.setBackgroundResource(R.color.gchat_userlist_bottom_bar_light_bg);
                GsChatUserListActivity.this.mBlockedUsersBtn.setBackgroundResource(R.color.gchat_userlist_bottom_bar_normal_bg);
                GsChatUserListActivity.this.mCurrentUsersIcon.setBackgroundResource(R.drawable.gchat_current_user_light);
                GsChatUserListActivity.this.mBlockedUsersIcon.setBackgroundResource(R.drawable.gchat_block_user_normal);
                GsChatUserListActivity.this.mCurrentUsersText.setTextColor(GsChatUserListActivity.this.getResources().getColor(R.color.white));
                GsChatUserListActivity.this.mBlockedUsersText.setTextColor(GsChatUserListActivity.this.getResources().getColor(R.color.gchat_userlist_bottom_bar_text_normal_bg));
                GsChatUserListActivity.this.mUserList.setVisibility(0);
                GsChatUserListActivity.this.mBlockList.setVisibility(8);
                return;
            }
            if (v.getId() == R.id.blocked_users_btn) {
                GsChatUserListActivity.this.mBlockedUsersBtn.setBackgroundResource(R.color.gchat_userlist_bottom_bar_light_bg);
                GsChatUserListActivity.this.mCurrentUsersBtn.setBackgroundResource(R.color.gchat_userlist_bottom_bar_normal_bg);
                GsChatUserListActivity.this.mBlockedUsersIcon.setBackgroundResource(R.drawable.gchat_block_user_light);
                GsChatUserListActivity.this.mCurrentUsersIcon.setBackgroundResource(R.drawable.gchat_current_user_normal);
                GsChatUserListActivity.this.mBlockedUsersText.setTextColor(GsChatUserListActivity.this.getResources().getColor(R.color.white));
                GsChatUserListActivity.this.mCurrentUsersText.setTextColor(GsChatUserListActivity.this.getResources().getColor(R.color.gchat_userlist_bottom_bar_text_normal_bg));
                GsChatUserListActivity.this.mBlockList.setVisibility(0);
                GsChatUserListActivity.this.mUserList.setVisibility(8);
            }
        }
    };
    private final SwipeMenuCreator mUserListMenuCreator = new SwipeMenuCreator() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.3
        AnonymousClass3() {
        }

        @Override // com.baoyz.swipemenulistview.SwipeMenuCreator
        public void create(SwipeMenu swipeMenu) {
            SwipeMenuItem blockItem = new SwipeMenuItem(GsChatUserListActivity.this.getApplicationContext());
            switch (swipeMenu.getViewType()) {
                case 1:
                    blockItem.setBackground(R.color.gchat_block_icon_bg);
                    blockItem.setWidth(GsChatUserListActivity.this.getResources().getDimensionPixelSize(R.dimen.gchat_swipe_menu_width));
                    blockItem.setIcon(R.drawable.gchat_block_icon_light);
                    swipeMenu.addMenuItem(blockItem);
                    break;
                case 2:
                    blockItem.setBackground(R.color.gchat_block_icon_bg);
                    blockItem.setWidth(GsChatUserListActivity.this.getResources().getDimensionPixelSize(R.dimen.gchat_swipe_menu_width));
                    blockItem.setIcon(R.drawable.gchat_unblock_icon_light);
                    swipeMenu.addMenuItem(blockItem);
                    break;
            }
        }
    };
    private GChatClientListener mGChatClientListener = new GChatClientListener() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.4
        AnonymousClass4() {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onConnectServerSuccess(ChatConnector connector) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onConnectServerFailure() {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void heartbeatTimeoutListener() {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedLoginResult(ChatConnector connector, GChatLoginInfo loginInfo) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedRoomInfo(ChatConnector connector, int roomId) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedUserList(ChatConnector connector, List<GsChatUser> userList) {
            GsChatUserListActivity.this.mUserListArrays = userList;
            GsChatUserListActivity.this.mUserListAdapter.setListArrays(GsChatUserListActivity.this.mUserListArrays);
            GChatJsonSerializeRequestBlackList requestBlackListSerialize = new GChatJsonSerializeRequestBlackList();
            connector.send(requestBlackListSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedBlacklist(ChatConnector connector, List<GsChatUser> blacklist) {
            GsChatUserListActivity.this.mBlockListArrays = blacklist;
            GsChatUserListActivity.this.mBlockListAdapter.setListArrays(GsChatUserListActivity.this.mBlockListArrays);
            GsChatUserListActivity.this.mUserListAdapter.notifyDataSetChanged();
            GsChatUserListActivity.this.mBlockListAdapter.notifyDataSetChanged();
            if (GsChatUserListActivity.this.mWaitDialog != null && GsChatUserListActivity.this.mWaitDialog.isShowing()) {
                GsChatUserListActivity.this.mWaitDialog.dismiss();
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedMsgSendResult(ChatConnector connector, int state) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedNewMessageEvent(ChatConnector connector, DataConvertChatMsgModel message) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedBlacklistSetResult(ChatConnector connector, int state) {
            if (state == 0) {
                GChatJsonSerializeRequestBlackList requestBlackListSerialize = new GChatJsonSerializeRequestBlackList();
                connector.send(requestBlackListSerialize);
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedUserChangeEvent(ChatConnector connector, GsChatUser user) {
            GChatJsonSerializeResponse responseSerialize = new GChatJsonSerializeResponse((short) 20, 0);
            GsChatUserListActivity.this.mChatClient.send(responseSerialize);
            GChatJsonSerializeRequestUserList requestUserListSerialize = new GChatJsonSerializeRequestUserList(GsChatUserListActivity.this.mCurrentRoomID);
            GsChatUserListActivity.this.mChatClient.send(requestUserListSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedRepeatLoginEvent(int state) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedSetUsernameResult(ChatConnector connector, int state) {
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws UnsupportedEncodingException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Intent intent = getIntent();
        this.mIsStbChat = intent.getBooleanExtra("is_stb_chat", true);
        initViews();
        setViewListener();
        if (this.mIsStbChat) {
            try {
                CreateSocket cSocket = new CreateSocket(null, 0);
                this.mTcpSocket = cSocket.GetSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.mMyUserId = GsChatSetting.getInstance().getUserId();
            setMessageProcess();
            GsSendSocket.sendOnlyCommandSocketToStb(this.mTcpSocket, 100);
        } else {
            this.mMyUserId = GChatLoginInfo.getInstance().getUserId();
            this.mChatClient.addListener(this.mGChatClientListener);
            this.mCurrentRoomID = intent.getIntExtra("room_id", 0);
            GChatJsonSerializeRequestUserList requestUserListSerialize = new GChatJsonSerializeRequestUserList(this.mCurrentRoomID);
            this.mChatClient.send(requestUserListSerialize);
        }
        setCommonMessageProcess();
        this.mWaitDialog = DialogBuilder.showProgressDialog((Activity) this, R.string.loading_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), this.mRequestDataFailRunable);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        MessageProcessor.obtain().removeProcessCallback(this);
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$1 */
    class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Toast.makeText(GsChatUserListActivity.this, GsChatUserListActivity.this.getResources().getString(R.string.str_load_data_fail), 0).show();
            GsChatUserListActivity.this.onBackPressed();
        }
    }

    public boolean isUserInBlockList(GsChatUser user) {
        if (user != null) {
            for (GsChatUser tmpUser : this.mBlockListArrays) {
                if (tmpUser.getUserID() == user.getUserID()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initViews() {
        this.mBackBtn = (LinearLayout) findViewById(R.id.back_btn);
        this.mCurrentUsersBtn = (LinearLayout) findViewById(R.id.current_users_btn);
        this.mBlockedUsersBtn = (LinearLayout) findViewById(R.id.blocked_users_btn);
        this.mUserList = (SwipeMenuListView) findViewById(R.id.user_list);
        this.mBlockList = (SwipeMenuListView) findViewById(R.id.blocked_list);
        this.mCurrentUsersIcon = (ImageView) findViewById(R.id.current_users_icon);
        this.mBlockedUsersIcon = (ImageView) findViewById(R.id.blocked_users_icon);
        this.mCurrentUsersText = (TextView) findViewById(R.id.current_users_text);
        this.mBlockedUsersText = (TextView) findViewById(R.id.blocked_users_text);
    }

    private void setViewListener() {
        this.mBackBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.5
            AnonymousClass5() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GsChatUserListActivity.this.onBackPressed();
            }
        });
        this.mUserListAdapter = new ChatUserListAdapter(this, this.mUserListArrays);
        this.mUserList.setAdapter((ListAdapter) this.mUserListAdapter);
        this.mUserList.setMenuCreator(this.mUserListMenuCreator);
        this.mUserList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.6
            AnonymousClass6() {
            }

            @Override // com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) throws SocketException, UnsupportedEncodingException {
                switch (index) {
                    case 0:
                        GsChatUser user = (GsChatUser) GsChatUserListActivity.this.mUserListArrays.get(position);
                        if (GsChatUserListActivity.this.isUserInBlockList(user)) {
                            user.setBlock(false);
                        } else {
                            user.setBlock(true);
                        }
                        GsChatUserListActivity.this.sendBlockListCommand(user);
                    default:
                        return false;
                }
            }
        });
        this.mBlockListAdapter = new ChatUserListAdapter(this, this.mBlockListArrays);
        this.mBlockList.setAdapter((ListAdapter) this.mBlockListAdapter);
        this.mBlockList.setMenuCreator(this.mUserListMenuCreator);
        this.mBlockList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.7
            AnonymousClass7() {
            }

            @Override // com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) throws SocketException, UnsupportedEncodingException {
                switch (index) {
                    case 0:
                        GsChatUser user = (GsChatUser) GsChatUserListActivity.this.mBlockListArrays.get(position);
                        user.setBlock(false);
                        GsChatUserListActivity.this.sendBlockListCommand(user);
                    default:
                        return false;
                }
            }
        });
        this.mCurrentUsersBtn.setOnClickListener(this.listBtnOnClickListener);
        this.mBlockedUsersBtn.setOnClickListener(this.listBtnOnClickListener);
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$5 */
    class AnonymousClass5 implements View.OnClickListener {
        AnonymousClass5() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            GsChatUserListActivity.this.onBackPressed();
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$6 */
    class AnonymousClass6 implements SwipeMenuListView.OnMenuItemClickListener {
        AnonymousClass6() {
        }

        @Override // com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) throws SocketException, UnsupportedEncodingException {
            switch (index) {
                case 0:
                    GsChatUser user = (GsChatUser) GsChatUserListActivity.this.mUserListArrays.get(position);
                    if (GsChatUserListActivity.this.isUserInBlockList(user)) {
                        user.setBlock(false);
                    } else {
                        user.setBlock(true);
                    }
                    GsChatUserListActivity.this.sendBlockListCommand(user);
                default:
                    return false;
            }
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$7 */
    class AnonymousClass7 implements SwipeMenuListView.OnMenuItemClickListener {
        AnonymousClass7() {
        }

        @Override // com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) throws SocketException, UnsupportedEncodingException {
            switch (index) {
                case 0:
                    GsChatUser user = (GsChatUser) GsChatUserListActivity.this.mBlockListArrays.get(position);
                    user.setBlock(false);
                    GsChatUserListActivity.this.sendBlockListCommand(user);
                default:
                    return false;
            }
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$2 */
    class AnonymousClass2 implements View.OnClickListener {
        AnonymousClass2() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (v.getId() == R.id.current_users_btn) {
                GsChatUserListActivity.this.mCurrentUsersBtn.setBackgroundResource(R.color.gchat_userlist_bottom_bar_light_bg);
                GsChatUserListActivity.this.mBlockedUsersBtn.setBackgroundResource(R.color.gchat_userlist_bottom_bar_normal_bg);
                GsChatUserListActivity.this.mCurrentUsersIcon.setBackgroundResource(R.drawable.gchat_current_user_light);
                GsChatUserListActivity.this.mBlockedUsersIcon.setBackgroundResource(R.drawable.gchat_block_user_normal);
                GsChatUserListActivity.this.mCurrentUsersText.setTextColor(GsChatUserListActivity.this.getResources().getColor(R.color.white));
                GsChatUserListActivity.this.mBlockedUsersText.setTextColor(GsChatUserListActivity.this.getResources().getColor(R.color.gchat_userlist_bottom_bar_text_normal_bg));
                GsChatUserListActivity.this.mUserList.setVisibility(0);
                GsChatUserListActivity.this.mBlockList.setVisibility(8);
                return;
            }
            if (v.getId() == R.id.blocked_users_btn) {
                GsChatUserListActivity.this.mBlockedUsersBtn.setBackgroundResource(R.color.gchat_userlist_bottom_bar_light_bg);
                GsChatUserListActivity.this.mCurrentUsersBtn.setBackgroundResource(R.color.gchat_userlist_bottom_bar_normal_bg);
                GsChatUserListActivity.this.mBlockedUsersIcon.setBackgroundResource(R.drawable.gchat_block_user_light);
                GsChatUserListActivity.this.mCurrentUsersIcon.setBackgroundResource(R.drawable.gchat_current_user_normal);
                GsChatUserListActivity.this.mBlockedUsersText.setTextColor(GsChatUserListActivity.this.getResources().getColor(R.color.white));
                GsChatUserListActivity.this.mCurrentUsersText.setTextColor(GsChatUserListActivity.this.getResources().getColor(R.color.gchat_userlist_bottom_bar_text_normal_bg));
                GsChatUserListActivity.this.mBlockList.setVisibility(0);
                GsChatUserListActivity.this.mUserList.setVisibility(8);
            }
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$3 */
    class AnonymousClass3 implements SwipeMenuCreator {
        AnonymousClass3() {
        }

        @Override // com.baoyz.swipemenulistview.SwipeMenuCreator
        public void create(SwipeMenu swipeMenu) {
            SwipeMenuItem blockItem = new SwipeMenuItem(GsChatUserListActivity.this.getApplicationContext());
            switch (swipeMenu.getViewType()) {
                case 1:
                    blockItem.setBackground(R.color.gchat_block_icon_bg);
                    blockItem.setWidth(GsChatUserListActivity.this.getResources().getDimensionPixelSize(R.dimen.gchat_swipe_menu_width));
                    blockItem.setIcon(R.drawable.gchat_block_icon_light);
                    swipeMenu.addMenuItem(blockItem);
                    break;
                case 2:
                    blockItem.setBackground(R.color.gchat_block_icon_bg);
                    blockItem.setWidth(GsChatUserListActivity.this.getResources().getDimensionPixelSize(R.dimen.gchat_swipe_menu_width));
                    blockItem.setIcon(R.drawable.gchat_unblock_icon_light);
                    swipeMenu.addMenuItem(blockItem);
                    break;
            }
        }
    }

    public void sendBlockListCommand(GsChatUser user) throws SocketException, UnsupportedEncodingException {
        if (this.mIsStbChat) {
            List<GsChatUser> blockList = new ArrayList<>();
            blockList.clear();
            blockList.add(user);
            try {
                byte[] data_buff = this.mParser.serialize(blockList, GlobalConstantValue.GMS_MSG_GCHAT_DO_BLOCK_UNBLOCK_USER).getBytes("UTF-8");
                this.mTcpSocket.setSoTimeout(3000);
                GsSendSocket.sendSocketToStb(data_buff, this.mTcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_GCHAT_DO_BLOCK_UNBLOCK_USER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            GChatJsonSerializeSetBlackList setBlackListListSerialize = new GChatJsonSerializeSetBlackList(user);
            this.mChatClient.send(setBlackListListSerialize);
        }
        if (this.mWaitDialog == null || !this.mWaitDialog.isShowing()) {
            this.mWaitDialog = DialogBuilder.showProgressDialog((Activity) this, R.string.loading_data, R.string.please_wait, false, GMScreenGlobalInfo.getmWaitDialogTimeOut(), R.string.str_load_data_fail);
        }
    }

    class ChatUserListAdapter extends BaseAdapter {
        public static final int VIEW_TYPE_BLOCK = 1;
        public static final int VIEW_TYPE_COUNT = 3;
        public static final int VIEW_TYPE_OWNER = 0;
        public static final int VIEW_TYPE_UNBLOCK = 2;
        private LayoutInflater mInflater;
        private List<GsChatUser> mListArrays;

        private class ViewHolder {
            public ImageView blockIcon;
            public ImageView headIcon;
            public TextView userID;
            public TextView username;

            private ViewHolder() {
            }

            /* synthetic */ ViewHolder(ChatUserListAdapter chatUserListAdapter, ViewHolder viewHolder) {
                this();
            }
        }

        public ChatUserListAdapter(Context context, List<GsChatUser> userListArrays) {
            this.mListArrays = userListArrays;
            this.mInflater = LayoutInflater.from(context);
        }

        public void setListArrays(List<GsChatUser> userListArrays) {
            this.mListArrays = userListArrays;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mListArrays.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.mListArrays.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 3;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int position) {
            if (this.mListArrays.get(position).getUserID() != GsChatUserListActivity.this.mMyUserId) {
                if (GsChatUserListActivity.this.isUserInBlockList(this.mListArrays.get(position))) {
                    return 2;
                }
                return 1;
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            GsChatUser user = this.mListArrays.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder(this, null);
                convertView = this.mInflater.inflate(R.layout.gchat_user_list_item, parent, false);
                viewHolder.username = (TextView) convertView.findViewById(R.id.user_name);
                viewHolder.userID = (TextView) convertView.findViewById(R.id.user_id);
                viewHolder.blockIcon = (ImageView) convertView.findViewById(R.id.block_icon);
                viewHolder.headIcon = (ImageView) convertView.findViewById(R.id.head_icon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (user.getUserID() == GsChatUserListActivity.this.mMyUserId) {
                viewHolder.headIcon.setBackgroundResource(R.drawable.gchat_head_icon_mine);
            } else {
                viewHolder.headIcon.setBackgroundResource(R.drawable.gchat_head_icon);
            }
            viewHolder.username.setText(user.getUsername());
            viewHolder.userID.setText(GsChatUserListActivity.this.getResources().getString(R.string.gchat_id, Integer.valueOf(user.getUserID())));
            if (GsChatUserListActivity.this.isUserInBlockList(user)) {
                viewHolder.blockIcon.setVisibility(0);
            } else {
                viewHolder.blockIcon.setVisibility(8);
            }
            return convertView;
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$4 */
    class AnonymousClass4 implements GChatClientListener {
        AnonymousClass4() {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onConnectServerSuccess(ChatConnector connector) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onConnectServerFailure() {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void heartbeatTimeoutListener() {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedLoginResult(ChatConnector connector, GChatLoginInfo loginInfo) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedRoomInfo(ChatConnector connector, int roomId) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedUserList(ChatConnector connector, List<GsChatUser> userList) {
            GsChatUserListActivity.this.mUserListArrays = userList;
            GsChatUserListActivity.this.mUserListAdapter.setListArrays(GsChatUserListActivity.this.mUserListArrays);
            GChatJsonSerializeRequestBlackList requestBlackListSerialize = new GChatJsonSerializeRequestBlackList();
            connector.send(requestBlackListSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedBlacklist(ChatConnector connector, List<GsChatUser> blacklist) {
            GsChatUserListActivity.this.mBlockListArrays = blacklist;
            GsChatUserListActivity.this.mBlockListAdapter.setListArrays(GsChatUserListActivity.this.mBlockListArrays);
            GsChatUserListActivity.this.mUserListAdapter.notifyDataSetChanged();
            GsChatUserListActivity.this.mBlockListAdapter.notifyDataSetChanged();
            if (GsChatUserListActivity.this.mWaitDialog != null && GsChatUserListActivity.this.mWaitDialog.isShowing()) {
                GsChatUserListActivity.this.mWaitDialog.dismiss();
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedMsgSendResult(ChatConnector connector, int state) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedNewMessageEvent(ChatConnector connector, DataConvertChatMsgModel message) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedBlacklistSetResult(ChatConnector connector, int state) {
            if (state == 0) {
                GChatJsonSerializeRequestBlackList requestBlackListSerialize = new GChatJsonSerializeRequestBlackList();
                connector.send(requestBlackListSerialize);
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedUserChangeEvent(ChatConnector connector, GsChatUser user) {
            GChatJsonSerializeResponse responseSerialize = new GChatJsonSerializeResponse((short) 20, 0);
            GsChatUserListActivity.this.mChatClient.send(responseSerialize);
            GChatJsonSerializeRequestUserList requestUserListSerialize = new GChatJsonSerializeRequestUserList(GsChatUserListActivity.this.mCurrentRoomID);
            GsChatUserListActivity.this.mChatClient.send(requestUserListSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedRepeatLoginEvent(int state) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedSetUsernameResult(ChatConnector connector, int state) {
        }
    }

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.recycle();
        this.msgProc.setOnMessageProcess(2101, this, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.8
            AnonymousClass8() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GsChatUserListActivity.this.mTcpSocket, 100);
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_GCHAT_DO_BLOCK_UNBLOCK_USER, this, new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.9
            AnonymousClass9() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GsChatUserListActivity.this.mTcpSocket, 102);
            }
        });
        this.msgProc.setOnMessageProcess(100, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.10
            AnonymousClass10() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    try {
                        Bundle data = msg.getData();
                        byte[] recvData = data.getByteArray("ReceivedData");
                        if (recvData == null) {
                            Log.e(GsChatUserListActivity.TAG, "GsChatUserListActivity recvData = " + recvData);
                        } else {
                            InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                            GsChatUserListActivity.this.mUserListArrays = ((GsChatRoomInfo) GsChatUserListActivity.this.mParser.parse(istream, 22).get(0)).getUserList();
                            GsChatUserListActivity.this.mUserListAdapter.setListArrays(GsChatUserListActivity.this.mUserListArrays);
                            GsSendSocket.sendOnlyCommandSocketToStb(GsChatUserListActivity.this.mTcpSocket, 102);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(102, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.11
            AnonymousClass11() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                byte[] recvData;
                if (msg.arg1 > 0) {
                    try {
                        Bundle data = msg.getData();
                        recvData = data.getByteArray("ReceivedData");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (recvData == null) {
                        Log.e(GsChatUserListActivity.TAG, "GsChatUserListActivity recvData = " + recvData);
                        return;
                    }
                    InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                    GsChatUserListActivity.this.mBlockListArrays = GsChatUserListActivity.this.mParser.parse(istream, 24);
                    GsChatUserListActivity.this.mBlockListAdapter.setListArrays(GsChatUserListActivity.this.mBlockListArrays);
                    GsChatUserListActivity.this.mBlockListAdapter.notifyDataSetChanged();
                    GsChatUserListActivity.this.mUserListAdapter.notifyDataSetChanged();
                    if (GsChatUserListActivity.this.mWaitDialog != null && GsChatUserListActivity.this.mWaitDialog.isShowing()) {
                        GsChatUserListActivity.this.mWaitDialog.dismiss();
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(2100, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.12
            AnonymousClass12() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsChatUserListActivity.this.finish();
            }
        });
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$8 */
    class AnonymousClass8 implements MessageProcessor.PerformOnBackground {
        AnonymousClass8() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) throws UnsupportedEncodingException {
            GsSendSocket.sendOnlyCommandSocketToStb(GsChatUserListActivity.this.mTcpSocket, 100);
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$9 */
    class AnonymousClass9 implements MessageProcessor.PerformOnBackground {
        AnonymousClass9() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
        public void doInBackground(Message msg) throws UnsupportedEncodingException {
            GsSendSocket.sendOnlyCommandSocketToStb(GsChatUserListActivity.this.mTcpSocket, 102);
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$10 */
    class AnonymousClass10 implements MessageProcessor.PerformOnForeground {
        AnonymousClass10() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            if (msg.arg1 > 0) {
                try {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData == null) {
                        Log.e(GsChatUserListActivity.TAG, "GsChatUserListActivity recvData = " + recvData);
                    } else {
                        InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        GsChatUserListActivity.this.mUserListArrays = ((GsChatRoomInfo) GsChatUserListActivity.this.mParser.parse(istream, 22).get(0)).getUserList();
                        GsChatUserListActivity.this.mUserListAdapter.setListArrays(GsChatUserListActivity.this.mUserListArrays);
                        GsSendSocket.sendOnlyCommandSocketToStb(GsChatUserListActivity.this.mTcpSocket, 102);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$11 */
    class AnonymousClass11 implements MessageProcessor.PerformOnForeground {
        AnonymousClass11() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            byte[] recvData;
            if (msg.arg1 > 0) {
                try {
                    Bundle data = msg.getData();
                    recvData = data.getByteArray("ReceivedData");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (recvData == null) {
                    Log.e(GsChatUserListActivity.TAG, "GsChatUserListActivity recvData = " + recvData);
                    return;
                }
                InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                GsChatUserListActivity.this.mBlockListArrays = GsChatUserListActivity.this.mParser.parse(istream, 24);
                GsChatUserListActivity.this.mBlockListAdapter.setListArrays(GsChatUserListActivity.this.mBlockListArrays);
                GsChatUserListActivity.this.mBlockListAdapter.notifyDataSetChanged();
                GsChatUserListActivity.this.mUserListAdapter.notifyDataSetChanged();
                if (GsChatUserListActivity.this.mWaitDialog != null && GsChatUserListActivity.this.mWaitDialog.isShowing()) {
                    GsChatUserListActivity.this.mWaitDialog.dismiss();
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$12 */
    class AnonymousClass12 implements MessageProcessor.PerformOnForeground {
        AnonymousClass12() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            GsChatUserListActivity.this.finish();
        }
    }

    private void setCommonMessageProcess() {
        MessageProcessor messageProcessor = MessageProcessor.obtain();
        messageProcessor.recycle();
        messageProcessor.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GsChatUserListActivity.13
            AnonymousClass13() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                GsChatUserListActivity.this.finish();
            }
        });
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GsChatUserListActivity$13 */
    class AnonymousClass13 implements MessageProcessor.PerformOnForeground {
        AnonymousClass13() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            GsChatUserListActivity.this.finish();
        }
    }
}
