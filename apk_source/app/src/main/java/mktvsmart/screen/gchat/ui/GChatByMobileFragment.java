package mktvsmart.screen.gchat.ui;

import android.app.Dialog;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.R;
import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;
import mktvsmart.screen.dataconvert.model.DataConvertGChatChannelInfoModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.gchat.DanmakuManager;
import mktvsmart.screen.gchat.GChatManager;
import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import mktvsmart.screen.gchat.bean.GsChatUser;
import mktvsmart.screen.gchat.client.ChatClientAsync;
import mktvsmart.screen.gchat.client.ChatConnector;
import mktvsmart.screen.gchat.client.GChatClientListener;
import mktvsmart.screen.json.serialize.GChatJsonSerializeChatMsg;
import mktvsmart.screen.json.serialize.GChatJsonSerializeEnterRoom;
import mktvsmart.screen.json.serialize.GChatJsonSerializeExitChat;
import mktvsmart.screen.json.serialize.GChatJsonSerializeLogin;
import mktvsmart.screen.json.serialize.GChatJsonSerializeRename;
import mktvsmart.screen.json.serialize.GChatJsonSerializeRequestBlackList;
import mktvsmart.screen.json.serialize.GChatJsonSerializeRequestUserList;
import mktvsmart.screen.json.serialize.GChatJsonSerializeResponse;
import mktvsmart.screen.json.serialize.GChatJsonSerializeSetBlackList;
import mktvsmart.screen.message.process.MessageProcessor;

/* loaded from: classes.dex */
public class GChatByMobileFragment extends GChatBaseFragment implements GChatManager {
    private DataConvertGChatChannelInfoModel mChatChannelInfo;
    private GChatLoginInfo mLoginInfo;
    private MessageProcessor mProcessor;
    private String mUsername;
    private ChatClientAsync mChatClientAsync = ChatClientAsync.getInstance();
    private int mUserNumber = 0;
    private boolean mHide = false;
    private GChatClientListener mChatClientListener = new GChatClientListener() { // from class: mktvsmart.screen.gchat.ui.GChatByMobileFragment.1
        AnonymousClass1() {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onConnectServerSuccess(ChatConnector connector) {
            WifiManager wifi = (WifiManager) GChatByMobileFragment.this.getActivity().getSystemService("wifi");
            WifiInfo info = wifi.getConnectionInfo();
            String mac = info.getMacAddress();
            GChatJsonSerializeLogin loginSerialize = new GChatJsonSerializeLogin(mac);
            connector.send(loginSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onConnectServerFailure() {
            GChatByMobileFragment.this.mIsChatStart = false;
            GChatByMobileFragment.this.mChatClientAsync.setup();
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void heartbeatTimeoutListener() {
            Log.d("Hans", "heartbeatTimeoutListener");
            GChatByMobileFragment.this.mIsChatStart = false;
            GChatByMobileFragment.this.mLoginBar.setVisibility(0);
            GChatByMobileFragment.this.mEpgTitle.setText(R.string.Logining);
            GChatByMobileFragment.this.mChatClientAsync.setup();
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedLoginResult(ChatConnector connector, GChatLoginInfo loginInfo) throws MissingResourceException {
            GChatByMobileFragment.this.mLoginInfo = loginInfo;
            Locale locale = GChatByMobileFragment.this.getActivity().getResources().getConfiguration().locale;
            String language = locale.getISO3Language();
            GChatJsonSerializeEnterRoom enterRoomSerialize = new GChatJsonSerializeEnterRoom(GChatByMobileFragment.this.mChatChannelInfo, language);
            connector.send(enterRoomSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedRoomInfo(ChatConnector connector, int roomId) {
            GChatByMobileFragment.this.mCurrentRoomID = roomId;
            GChatByMobileFragment.this.requestUserList();
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedUserList(ChatConnector connector, List<GsChatUser> userList) {
            GChatByMobileFragment.this.mUserNumber = userList.size();
            GChatByMobileFragment.this.mUserNum.setText(GChatByMobileFragment.this.getActivity().getResources().getString(R.string.gchat_num_format, Integer.valueOf(GChatByMobileFragment.this.mUserNumber)));
            GChatJsonSerializeRequestBlackList requestBlackListSerialize = new GChatJsonSerializeRequestBlackList();
            connector.send(requestBlackListSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedBlacklist(ChatConnector connector, List<GsChatUser> blacklist) {
            GChatByMobileFragment.this.mBlockList = blacklist;
            GChatByMobileFragment.this.mLoginBar.setVisibility(8);
            String epgTitle = GChatByMobileFragment.this.mChatChannelInfo.getEpg();
            if (epgTitle != null) {
                GChatByMobileFragment.this.mEpgTitle.setText(epgTitle);
            }
            GChatByMobileFragment.this.mIsChatStart = true;
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedMsgSendResult(ChatConnector connector, int state) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedNewMessageEvent(ChatConnector connector, DataConvertChatMsgModel message) {
            if (GChatByMobileFragment.this.mIsChatStart) {
                GsChatUser user = new GsChatUser();
                user.setUserID(message.getUserID());
                if (!GChatByMobileFragment.this.isUserInBlockList(user)) {
                    if (GChatByMobileFragment.this.mHide) {
                        ((DanmakuManager) GChatByMobileFragment.this.getActivity()).addDanmaku(true, message.getContent());
                    }
                    GChatByMobileFragment.this.messageInsert(message);
                }
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedBlacklistSetResult(ChatConnector connector, int state) {
            if (state == 0) {
                GChatJsonSerializeRequestBlackList requestBlackListSerialize = new GChatJsonSerializeRequestBlackList();
                connector.send(requestBlackListSerialize);
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedUserChangeEvent(ChatConnector connector, GsChatUser user) throws Resources.NotFoundException {
            String notice = null;
            if (user.getState() == 0) {
                GChatByMobileFragment gChatByMobileFragment = GChatByMobileFragment.this;
                gChatByMobileFragment.mUserNumber--;
                notice = GChatByMobileFragment.this.getActivity().getResources().getString(R.string.gchat_user_out, user.getUsername());
            } else if (user.getState() == 1) {
                GChatByMobileFragment.this.mUserNumber++;
                notice = GChatByMobileFragment.this.getActivity().getResources().getString(R.string.gchat_user_in, user.getUsername());
            }
            if (notice != null && !GChatByMobileFragment.this.mHide) {
                Toast.makeText(GChatByMobileFragment.this.getActivity(), notice, 0).show();
            }
            GChatByMobileFragment.this.mUserNum.setText(GChatByMobileFragment.this.getActivity().getResources().getString(R.string.gchat_num_format, Integer.valueOf(GChatByMobileFragment.this.mUserNumber)));
            GChatJsonSerializeResponse responseSerialize = new GChatJsonSerializeResponse((short) 20, 0);
            connector.send(responseSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedRepeatLoginEvent(int state) {
            if (state == 1) {
                GChatByMobileFragment.this.sendExitChatCommand();
                GChatByMobileFragment.this.mIsChatStart = false;
                GChatByMobileFragment.this.mLoginBar.setVisibility(0);
                GChatByMobileFragment.this.mEpgTitle.setText(R.string.Logining);
                GChatByMobileFragment.this.mChatClientAsync.setup();
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedSetUsernameResult(ChatConnector connector, int state) {
            if (state == 0) {
                GChatByMobileFragment.this.mLoginInfo.setUsername(GChatByMobileFragment.this.mUsername);
                Toast.makeText(GChatByMobileFragment.this.getActivity(), "Rename Success !", 0).show();
            }
        }
    };

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mChatClientAsync.addListener(this.mChatClientListener);
        setMessageProcess();
        setChatByStb(false);
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment, android.support.v4.app.Fragment
    public void onDestroy() {
        sendExitChatCommand();
        this.mChatClientAsync.cleanListeners();
        this.mChatClientAsync.exit();
        super.onDestroy();
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    public void finish() {
        getActivity().finish();
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    public void initViews() {
        super.initViews();
        this.mSettingBtn.setVisibility(8);
        this.mBackBtn.setVisibility(8);
        this.mSetUsernameBtn.setOnClickListener(getRenameMenuOnClickListener());
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    public void sendBlockListCommand(List<GsChatUser> blockList) {
        GChatJsonSerializeSetBlackList setBlackListListSerialize = new GChatJsonSerializeSetBlackList(blockList.get(0));
        this.mChatClientAsync.send(setBlackListListSerialize);
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    public void doMessageSend(DataConvertChatMsgModel newMsg) {
        GChatJsonSerializeChatMsg chatMsgSerialize = new GChatJsonSerializeChatMsg(newMsg, this.mCurrentRoomID);
        this.mChatClientAsync.send(chatMsgSerialize);
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    protected GsChatUser getOwnerUserInfo() {
        GsChatUser user = new GsChatUser();
        user.setUserID(this.mLoginInfo.getUserId());
        user.setUsername(this.mLoginInfo.getUsername());
        return user;
    }

    public void setMessageProcess() {
        this.mProcessor = MessageProcessor.obtain();
        this.mProcessor.setOnMessageProcess(104, getActivity(), new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GChatByMobileFragment.2
            AnonymousClass2() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData != null) {
                        DataParser parser = ParserFactory.getParser();
                        InputStream inStream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        try {
                            List<?> list = parser.parse(inStream, 26);
                            GChatByMobileFragment.this.mChatChannelInfo = (DataConvertGChatChannelInfoModel) list.get(0);
                            GChatByMobileFragment.this.mChatClientAsync.setup();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GChatByMobileFragment$2 */
    class AnonymousClass2 implements MessageProcessor.PerformOnForeground {
        AnonymousClass2() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            if (msg.arg1 > 0) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                if (recvData != null) {
                    DataParser parser = ParserFactory.getParser();
                    InputStream inStream = new ByteArrayInputStream(recvData, 0, recvData.length);
                    try {
                        List<?> list = parser.parse(inStream, 26);
                        GChatByMobileFragment.this.mChatChannelInfo = (DataConvertGChatChannelInfoModel) list.get(0);
                        GChatByMobileFragment.this.mChatClientAsync.setup();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GChatByMobileFragment$1 */
    class AnonymousClass1 implements GChatClientListener {
        AnonymousClass1() {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onConnectServerSuccess(ChatConnector connector) {
            WifiManager wifi = (WifiManager) GChatByMobileFragment.this.getActivity().getSystemService("wifi");
            WifiInfo info = wifi.getConnectionInfo();
            String mac = info.getMacAddress();
            GChatJsonSerializeLogin loginSerialize = new GChatJsonSerializeLogin(mac);
            connector.send(loginSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onConnectServerFailure() {
            GChatByMobileFragment.this.mIsChatStart = false;
            GChatByMobileFragment.this.mChatClientAsync.setup();
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void heartbeatTimeoutListener() {
            Log.d("Hans", "heartbeatTimeoutListener");
            GChatByMobileFragment.this.mIsChatStart = false;
            GChatByMobileFragment.this.mLoginBar.setVisibility(0);
            GChatByMobileFragment.this.mEpgTitle.setText(R.string.Logining);
            GChatByMobileFragment.this.mChatClientAsync.setup();
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedLoginResult(ChatConnector connector, GChatLoginInfo loginInfo) throws MissingResourceException {
            GChatByMobileFragment.this.mLoginInfo = loginInfo;
            Locale locale = GChatByMobileFragment.this.getActivity().getResources().getConfiguration().locale;
            String language = locale.getISO3Language();
            GChatJsonSerializeEnterRoom enterRoomSerialize = new GChatJsonSerializeEnterRoom(GChatByMobileFragment.this.mChatChannelInfo, language);
            connector.send(enterRoomSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedRoomInfo(ChatConnector connector, int roomId) {
            GChatByMobileFragment.this.mCurrentRoomID = roomId;
            GChatByMobileFragment.this.requestUserList();
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedUserList(ChatConnector connector, List<GsChatUser> userList) {
            GChatByMobileFragment.this.mUserNumber = userList.size();
            GChatByMobileFragment.this.mUserNum.setText(GChatByMobileFragment.this.getActivity().getResources().getString(R.string.gchat_num_format, Integer.valueOf(GChatByMobileFragment.this.mUserNumber)));
            GChatJsonSerializeRequestBlackList requestBlackListSerialize = new GChatJsonSerializeRequestBlackList();
            connector.send(requestBlackListSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedBlacklist(ChatConnector connector, List<GsChatUser> blacklist) {
            GChatByMobileFragment.this.mBlockList = blacklist;
            GChatByMobileFragment.this.mLoginBar.setVisibility(8);
            String epgTitle = GChatByMobileFragment.this.mChatChannelInfo.getEpg();
            if (epgTitle != null) {
                GChatByMobileFragment.this.mEpgTitle.setText(epgTitle);
            }
            GChatByMobileFragment.this.mIsChatStart = true;
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedMsgSendResult(ChatConnector connector, int state) {
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedNewMessageEvent(ChatConnector connector, DataConvertChatMsgModel message) {
            if (GChatByMobileFragment.this.mIsChatStart) {
                GsChatUser user = new GsChatUser();
                user.setUserID(message.getUserID());
                if (!GChatByMobileFragment.this.isUserInBlockList(user)) {
                    if (GChatByMobileFragment.this.mHide) {
                        ((DanmakuManager) GChatByMobileFragment.this.getActivity()).addDanmaku(true, message.getContent());
                    }
                    GChatByMobileFragment.this.messageInsert(message);
                }
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedBlacklistSetResult(ChatConnector connector, int state) {
            if (state == 0) {
                GChatJsonSerializeRequestBlackList requestBlackListSerialize = new GChatJsonSerializeRequestBlackList();
                connector.send(requestBlackListSerialize);
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedUserChangeEvent(ChatConnector connector, GsChatUser user) throws Resources.NotFoundException {
            String notice = null;
            if (user.getState() == 0) {
                GChatByMobileFragment gChatByMobileFragment = GChatByMobileFragment.this;
                gChatByMobileFragment.mUserNumber--;
                notice = GChatByMobileFragment.this.getActivity().getResources().getString(R.string.gchat_user_out, user.getUsername());
            } else if (user.getState() == 1) {
                GChatByMobileFragment.this.mUserNumber++;
                notice = GChatByMobileFragment.this.getActivity().getResources().getString(R.string.gchat_user_in, user.getUsername());
            }
            if (notice != null && !GChatByMobileFragment.this.mHide) {
                Toast.makeText(GChatByMobileFragment.this.getActivity(), notice, 0).show();
            }
            GChatByMobileFragment.this.mUserNum.setText(GChatByMobileFragment.this.getActivity().getResources().getString(R.string.gchat_num_format, Integer.valueOf(GChatByMobileFragment.this.mUserNumber)));
            GChatJsonSerializeResponse responseSerialize = new GChatJsonSerializeResponse((short) 20, 0);
            connector.send(responseSerialize);
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedRepeatLoginEvent(int state) {
            if (state == 1) {
                GChatByMobileFragment.this.sendExitChatCommand();
                GChatByMobileFragment.this.mIsChatStart = false;
                GChatByMobileFragment.this.mLoginBar.setVisibility(0);
                GChatByMobileFragment.this.mEpgTitle.setText(R.string.Logining);
                GChatByMobileFragment.this.mChatClientAsync.setup();
            }
        }

        @Override // mktvsmart.screen.gchat.client.GChatClientListener
        public void onReceivedSetUsernameResult(ChatConnector connector, int state) {
            if (state == 0) {
                GChatByMobileFragment.this.mLoginInfo.setUsername(GChatByMobileFragment.this.mUsername);
                Toast.makeText(GChatByMobileFragment.this.getActivity(), "Rename Success !", 0).show();
            }
        }
    }

    public boolean isUserInBlockList(GsChatUser user) {
        if (user != null) {
            for (GsChatUser tmpUser : this.mBlockList) {
                if (tmpUser.getUserID() == user.getUserID()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // mktvsmart.screen.gchat.GChatManager
    public void requestUserList() {
        GChatJsonSerializeRequestUserList requestUserListSerialize = new GChatJsonSerializeRequestUserList(this.mCurrentRoomID);
        this.mChatClientAsync.send(requestUserListSerialize);
    }

    @Override // mktvsmart.screen.gchat.GChatManager
    public void sendExitChatCommand() {
        GChatJsonSerializeExitChat exitChatSerialize = new GChatJsonSerializeExitChat();
        this.mChatClientAsync.send(exitChatSerialize);
    }

    @Override // mktvsmart.screen.gchat.GChatManager
    public void closeChatClient() {
        this.mChatClientAsync.exit();
    }

    @Override // mktvsmart.screen.gchat.GChatManager
    public void setHide(boolean hide) {
        this.mHide = hide;
    }

    /* renamed from: mktvsmart.screen.gchat.ui.GChatByMobileFragment$3 */
    class AnonymousClass3 implements View.OnClickListener {
        InputMethodManager inputManager;
        Dialog renameInputDialog;

        AnonymousClass3() {
            this.renameInputDialog = new Dialog(GChatByMobileFragment.this.getActivity(), R.style.dialog);
            this.inputManager = (InputMethodManager) GChatByMobileFragment.this.getActivity().getSystemService("input_method");
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (!GChatByMobileFragment.this.mIsChatStart) {
                Toast.makeText(GChatByMobileFragment.this.getActivity(), GChatByMobileFragment.this.getActivity().getResources().getString(R.string.gchat_not_login_message), 0).show();
                return;
            }
            LayoutInflater inflater = LayoutInflater.from(GChatByMobileFragment.this.getActivity());
            LinearLayout renameLayout = (LinearLayout) inflater.inflate(R.layout.input_rename_dialog, (ViewGroup) null);
            EditText inputName = (EditText) renameLayout.findViewById(R.id.input_name_edittext);
            Button renameSaveBtn = (Button) renameLayout.findViewById(R.id.input_name_confirm_btn);
            Button renameCancelBtn = (Button) renameLayout.findViewById(R.id.input_name_cancel_btn);
            inputName.setText(GChatLoginInfo.getInstance().getUsername());
            Selection.selectAll(inputName.getText());
            renameSaveBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GChatByMobileFragment.3.1
                private final /* synthetic */ EditText val$inputName;

                AnonymousClass1(EditText inputName2) {
                    editText = inputName2;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View v2) {
                    GChatByMobileFragment.this.mUsername = editText.getText().toString();
                    if (GChatByMobileFragment.this.mUsername.length() > 0 && !GChatByMobileFragment.this.mUsername.equals(GChatLoginInfo.getInstance().getUsername())) {
                        GChatJsonSerializeRename renameSerialize = new GChatJsonSerializeRename(GChatByMobileFragment.this.mUsername);
                        GChatByMobileFragment.this.mChatClientAsync.send(renameSerialize);
                    }
                    AnonymousClass3.this.renameInputDialog.dismiss();
                }
            });
            renameCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.gchat.ui.GChatByMobileFragment.3.2
                private final /* synthetic */ EditText val$inputName;

                AnonymousClass2(EditText inputName2) {
                    editText = inputName2;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View v2) {
                    AnonymousClass3.this.inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    AnonymousClass3.this.renameInputDialog.dismiss();
                }
            });
            this.renameInputDialog.setContentView(renameLayout);
            this.renameInputDialog.setCanceledOnTouchOutside(false);
            this.renameInputDialog.show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() { // from class: mktvsmart.screen.gchat.ui.GChatByMobileFragment.3.3
                private final /* synthetic */ EditText val$inputName;

                C01593(EditText inputName2) {
                    editText = inputName2;
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    AnonymousClass3.this.inputManager = (InputMethodManager) editText.getContext().getSystemService("input_method");
                    AnonymousClass3.this.inputManager.showSoftInput(editText, 0);
                }
            }, 200L);
        }

        /* renamed from: mktvsmart.screen.gchat.ui.GChatByMobileFragment$3$1 */
        class AnonymousClass1 implements View.OnClickListener {
            private final /* synthetic */ EditText val$inputName;

            AnonymousClass1(EditText inputName2) {
                editText = inputName2;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v2) {
                GChatByMobileFragment.this.mUsername = editText.getText().toString();
                if (GChatByMobileFragment.this.mUsername.length() > 0 && !GChatByMobileFragment.this.mUsername.equals(GChatLoginInfo.getInstance().getUsername())) {
                    GChatJsonSerializeRename renameSerialize = new GChatJsonSerializeRename(GChatByMobileFragment.this.mUsername);
                    GChatByMobileFragment.this.mChatClientAsync.send(renameSerialize);
                }
                AnonymousClass3.this.renameInputDialog.dismiss();
            }
        }

        /* renamed from: mktvsmart.screen.gchat.ui.GChatByMobileFragment$3$2 */
        class AnonymousClass2 implements View.OnClickListener {
            private final /* synthetic */ EditText val$inputName;

            AnonymousClass2(EditText inputName2) {
                editText = inputName2;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v2) {
                AnonymousClass3.this.inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                AnonymousClass3.this.renameInputDialog.dismiss();
            }
        }

        /* renamed from: mktvsmart.screen.gchat.ui.GChatByMobileFragment$3$3 */
        class C01593 extends TimerTask {
            private final /* synthetic */ EditText val$inputName;

            C01593(EditText inputName2) {
                editText = inputName2;
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                AnonymousClass3.this.inputManager = (InputMethodManager) editText.getContext().getSystemService("input_method");
                AnonymousClass3.this.inputManager.showSoftInput(editText, 0);
            }
        }
    }

    private View.OnClickListener getRenameMenuOnClickListener() {
        return new AnonymousClass3();
    }
}
