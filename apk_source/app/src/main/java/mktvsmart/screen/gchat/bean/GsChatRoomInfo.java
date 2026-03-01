package mktvsmart.screen.gchat.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GsChatRoomInfo implements Serializable {
    private static final long serialVersionUID = 1;
    private String mEventTitle;
    private int mOnlineNum;
    private int mRoomID;
    private List<GsChatUser> mUserList = new ArrayList();

    public void setEventTitle(String eventTitle) {
        this.mEventTitle = eventTitle;
    }

    public String getEventTitle() {
        return this.mEventTitle;
    }

    public void setOnlineNum(int onlineNum) {
        this.mOnlineNum = onlineNum;
    }

    public int getOnlineNum() {
        return this.mOnlineNum;
    }

    public void setUserList(List<GsChatUser> userList) {
        this.mUserList = userList;
    }

    public void setRoomID(int roomID) {
        this.mRoomID = roomID;
    }

    public int getRoomID() {
        return this.mRoomID;
    }

    public List<GsChatUser> getUserList() {
        return this.mUserList;
    }

    public GsChatUser getUser(int position) {
        return this.mUserList.get(position);
    }

    public void removeUser(int position) {
        this.mUserList.remove(position);
    }
}
