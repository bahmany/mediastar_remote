package mktvsmart.screen.gchat;

/* loaded from: classes.dex */
public interface GChatManager {
    void closeChatClient();

    void requestUserList();

    void sendExitChatCommand();

    void setHide(boolean z);
}
