package mktvsmart.screen.gchat.bean;

/* loaded from: classes.dex */
public class GChatLoginInfo {
    private static GChatLoginInfo mInstance = new GChatLoginInfo();
    private String mSignature;
    private int mUserId;
    private String mUsername;

    private GChatLoginInfo() {
    }

    public static GChatLoginInfo getInstance() {
        return mInstance;
    }

    public void setSignature(String signature) {
        this.mSignature = signature;
    }

    public String getSignature() {
        return this.mSignature;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public int getUserId() {
        return this.mUserId;
    }
}
