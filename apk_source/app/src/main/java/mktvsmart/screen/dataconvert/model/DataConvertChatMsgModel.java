package mktvsmart.screen.dataconvert.model;

/* loaded from: classes.dex */
public class DataConvertChatMsgModel {
    public static final int MSG_TYPE_IN = 0;
    public static final int MSG_TYPE_OUT = 1;
    private String mContent;
    private int mMsgType;
    private long mTimestamp;
    private int mUserID;
    private String mUsername;

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public int getUserID() {
        return this.mUserID;
    }

    public void setUserID(int userID) {
        this.mUserID = userID;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getContent() {
        return this.mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public void setMsgType(int msgType) {
        this.mMsgType = msgType;
    }

    public int getMsgType() {
        return this.mMsgType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DataConvertChatMsgModel)) {
            return false;
        }
        DataConvertChatMsgModel message = (DataConvertChatMsgModel) o;
        return getUserID() == message.getUserID() && getMsgType() == message.getMsgType() && getContent().equals(message.getContent()) && getTimestamp() == message.getTimestamp() && getUsername().equals(message.getUsername());
    }

    public int hashCode() {
        int result = (getContent() == null ? 0 : getContent().hashCode()) + 527;
        return (((((((result * 31) + (getUsername() != null ? getUsername().hashCode() : 0)) * 31) + ((int) (getTimestamp() ^ (getTimestamp() >> 32)))) * 31) + getMsgType()) * 31) + getUserID();
    }
}
