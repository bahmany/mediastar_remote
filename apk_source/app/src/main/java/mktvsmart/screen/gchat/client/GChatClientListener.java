package mktvsmart.screen.gchat.client;

import java.util.List;
import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;
import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import mktvsmart.screen.gchat.bean.GsChatUser;

/* loaded from: classes.dex */
public interface GChatClientListener {
    void heartbeatTimeoutListener();

    void onConnectServerFailure();

    void onConnectServerSuccess(ChatConnector chatConnector);

    void onReceivedBlacklist(ChatConnector chatConnector, List<GsChatUser> list);

    void onReceivedBlacklistSetResult(ChatConnector chatConnector, int i);

    void onReceivedLoginResult(ChatConnector chatConnector, GChatLoginInfo gChatLoginInfo);

    void onReceivedMsgSendResult(ChatConnector chatConnector, int i);

    void onReceivedNewMessageEvent(ChatConnector chatConnector, DataConvertChatMsgModel dataConvertChatMsgModel);

    void onReceivedRepeatLoginEvent(int i);

    void onReceivedRoomInfo(ChatConnector chatConnector, int i);

    void onReceivedSetUsernameResult(ChatConnector chatConnector, int i);

    void onReceivedUserChangeEvent(ChatConnector chatConnector, GsChatUser gsChatUser);

    void onReceivedUserList(ChatConnector chatConnector, List<GsChatUser> list);
}
