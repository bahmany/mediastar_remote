package mktvsmart.screen.gchat.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mktvsmart.screen.R;
import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;
import mktvsmart.screen.gchat.bean.GsChatRoomInfo;
import mktvsmart.screen.gchat.bean.GsChatUser;
import mktvsmart.screen.gchat.database.ChatDatabaseHelper;
import mktvsmart.screen.gchat.database.ChatMessageManager;
import mktvsmart.screen.gchat.ui.MessagePopupWindow;

/* loaded from: classes.dex */
public abstract class GChatBaseFragment extends Fragment {
    private ChatCursorAdapter mAdapter;
    protected LinearLayout mBackBtn;
    private ChatMessageManager mChatMessageManager;
    protected GsChatRoomInfo mChatRoomInfo;
    protected int mCurrentRoomID;
    private EditText mEditMsg;
    protected TextView mEpgTitle;
    private HandlerThread mHandlerThread;
    private ListView mListView;
    private Handler mLoadDataHandler;
    protected ProgressBar mLoginBar;
    private Button mSendMsgBtn;
    protected Button mSetUsernameBtn;
    protected ImageView mSettingBtn;
    protected TextView mUserNum;
    private View mView;
    protected List<GsChatUser> mBlockList = new ArrayList();
    protected boolean mIsChatStart = false;
    protected boolean mChatByStb = false;
    private MessagePopupWindow.OnPopupMenuListener mPopupMenuClickListener = new MessagePopupWindow.OnPopupMenuListener() { // from class: mktvsmart.screen.gchat.ui.GChatBaseFragment.1
        @Override // mktvsmart.screen.gchat.ui.MessagePopupWindow.OnPopupMenuListener
        public boolean isUserInBlockList(int position) {
            Cursor cursor = (Cursor) GChatBaseFragment.this.mAdapter.getItem(position);
            int userId = cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_USER_ID));
            for (GsChatUser tmpUser : GChatBaseFragment.this.mBlockList) {
                if (tmpUser.getUserID() == userId) {
                    return true;
                }
            }
            return false;
        }

        @Override // mktvsmart.screen.gchat.ui.MessagePopupWindow.OnPopupMenuListener
        public void onBlockClick(int position, boolean needBlock) {
            GsChatUser user = GChatBaseFragment.this.getUserByMsg(position);
            if (needBlock) {
                user.setBlock(true);
                GChatBaseFragment.this.mBlockList.add(user);
            } else {
                user.setBlock(false);
                Iterator<GsChatUser> it = GChatBaseFragment.this.mBlockList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    GsChatUser tmpUser = it.next();
                    if (tmpUser.getUserID() == user.getUserID()) {
                        GChatBaseFragment.this.mBlockList.remove(tmpUser);
                        break;
                    }
                }
            }
            List<GsChatUser> blockList = new ArrayList<>();
            blockList.clear();
            blockList.add(user);
            GChatBaseFragment.this.sendBlockListCommand(blockList);
        }

        @Override // mktvsmart.screen.gchat.ui.MessagePopupWindow.OnPopupMenuListener
        public void onDeleteClick(int position) {
            GChatBaseFragment.this.messageRemove(position);
        }

        @Override // mktvsmart.screen.gchat.ui.MessagePopupWindow.OnPopupMenuListener
        public int getMessageType(int position) {
            Cursor cursor = (Cursor) GChatBaseFragment.this.mAdapter.getItem(position);
            return cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE_TYPE));
        }
    };

    protected abstract void doMessageSend(DataConvertChatMsgModel dataConvertChatMsgModel);

    protected abstract void finish();

    protected abstract GsChatUser getOwnerUserInfo();

    protected abstract void sendBlockListCommand(List<GsChatUser> list);

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mHandlerThread = new HandlerThread("load_data");
        this.mHandlerThread.start();
        this.mLoadDataHandler = new Handler(this.mHandlerThread.getLooper());
        this.mChatMessageManager = new ChatMessageManager(getActivity());
        clearMessages();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        clearMessages();
        if (this.mHandlerThread != null) {
            this.mHandlerThread.quit();
        }
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.chat_layout, container, false);
        initViews();
        return this.mView;
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected View findViewById(int id) {
        return this.mView.findViewById(id);
    }

    protected void setChatByStb(boolean chatByStb) {
        this.mChatByStb = chatByStb;
    }

    protected void messageInsert(DataConvertChatMsgModel ChatMsgModel) {
        this.mChatMessageManager.insertAsync(ChatMsgModel);
    }

    protected void messageInsert(final List<DataConvertChatMsgModel> messages) {
        this.mLoadDataHandler.post(new Runnable() { // from class: mktvsmart.screen.gchat.ui.GChatBaseFragment.2
            @Override // java.lang.Runnable
            public void run() {
                ChatMessageManager.bulkInsert(GChatBaseFragment.this.getActivity(), messages);
            }
        });
    }

    protected void clearMessages() {
        this.mChatMessageManager.deleteAllAsync();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void messageRemove(int position) {
        Cursor cursor = (Cursor) this.mAdapter.getItem(position);
        DataConvertChatMsgModel ChatMsgModel = new DataConvertChatMsgModel();
        ChatMsgModel.setUserID(cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_USER_ID)));
        ChatMsgModel.setMsgType(cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE_TYPE)));
        ChatMsgModel.setTimestamp(cursor.getLong(cursor.getColumnIndex(ChatDatabaseHelper.KEY_TIMESTAMP)));
        ChatMsgModel.setUsername(cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_USERNAME)));
        ChatMsgModel.setContent(cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_CONTENT)));
        this.mChatMessageManager.deleteAsync(ChatMsgModel);
    }

    private void initCursorLoader() {
        getActivity().getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() { // from class: mktvsmart.screen.gchat.ui.GChatBaseFragment.3
            @Override // android.support.v4.app.LoaderManager.LoaderCallbacks
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new CursorLoader(GChatBaseFragment.this.getActivity(), ChatMessageManager.CONTENT_URI, null, null, null, "_id ASC");
            }

            @Override // android.support.v4.app.LoaderManager.LoaderCallbacks
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                GChatBaseFragment.this.mAdapter.swapCursor(cursor);
            }

            @Override // android.support.v4.app.LoaderManager.LoaderCallbacks
            public void onLoaderReset(Loader<Cursor> loader) {
                GChatBaseFragment.this.mAdapter.swapCursor(null);
            }
        });
    }

    protected void initViews() {
        this.mBackBtn = (LinearLayout) findViewById(R.id.back_btn);
        this.mSettingBtn = (ImageView) findViewById(R.id.setting_btn);
        this.mListView = (ListView) findViewById(R.id.listview);
        this.mSendMsgBtn = (Button) findViewById(R.id.chat_send_btn);
        this.mUserNum = (TextView) findViewById(R.id.user_num);
        this.mEditMsg = (EditText) findViewById(R.id.chat_editmessage);
        this.mEpgTitle = (TextView) findViewById(R.id.epg_title);
        this.mSetUsernameBtn = (Button) findViewById(R.id.set_user_name);
        this.mLoginBar = (ProgressBar) findViewById(R.id.login_bar);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GChatBaseFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GChatBaseFragment.this.finish();
            }
        });
        this.mSettingBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GChatBaseFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(GChatBaseFragment.this.getActivity(), GsChatSettingActivity.class);
                GChatBaseFragment.this.getActivity().startActivity(intent);
            }
        });
        this.mSendMsgBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GChatBaseFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (!GChatBaseFragment.this.mIsChatStart) {
                    Toast.makeText(GChatBaseFragment.this.getActivity(), GChatBaseFragment.this.getActivity().getResources().getString(R.string.gchat_not_login_message), 0).show();
                } else {
                    GChatBaseFragment.this.sendMessage();
                }
            }
        });
        Cursor cursor = ChatMessageManager.getAllMessage(getActivity());
        this.mAdapter = new ChatCursorAdapter(getActivity(), cursor, this.mPopupMenuClickListener);
        initCursorLoader();
        this.mListView.setAdapter((ListAdapter) this.mAdapter);
        this.mListView.setOnTouchListener(new View.OnTouchListener() { // from class: mktvsmart.screen.gchat.ui.GChatBaseFragment.7
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 0) {
                    InputMethodManager imm = (InputMethodManager) GChatBaseFragment.this.getActivity().getSystemService("input_method");
                    imm.hideSoftInputFromWindow(GChatBaseFragment.this.getActivity().getCurrentFocus().getWindowToken(), 2);
                    return false;
                }
                return false;
            }
        });
        this.mUserNum.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GChatBaseFragment.8
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (GChatBaseFragment.this.mIsChatStart) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("is_stb_chat", GChatBaseFragment.this.mChatByStb);
                    bundle.putInt("room_id", GChatBaseFragment.this.mCurrentRoomID);
                    intent.putExtras(bundle);
                    intent.setClass(GChatBaseFragment.this.getActivity(), GsChatUserListActivity.class);
                    GChatBaseFragment.this.getActivity().startActivity(intent);
                    return;
                }
                Toast.makeText(GChatBaseFragment.this.getActivity(), GChatBaseFragment.this.getActivity().getResources().getString(R.string.gchat_not_login_message), 0).show();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public GsChatUser getUserByMsg(int position) {
        Cursor cursor = (Cursor) this.mAdapter.getItem(position);
        int userId = cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_USER_ID));
        String username = cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_USERNAME));
        GsChatUser user = new GsChatUser();
        user.setUserID(userId);
        user.setUsername(username);
        return user;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage() {
        String contString = this.mEditMsg.getText().toString();
        if (contString.length() > 0) {
            DataConvertChatMsgModel newMsg = new DataConvertChatMsgModel();
            newMsg.setMsgType(1);
            newMsg.setContent(contString);
            newMsg.setTimestamp(System.currentTimeMillis() / 1000);
            newMsg.setUsername(getOwnerUserInfo().getUsername());
            newMsg.setUserID(getOwnerUserInfo().getUserID());
            messageInsert(newMsg);
            this.mEditMsg.setText(R.string.Blank);
            doMessageSend(newMsg);
        }
    }
}
