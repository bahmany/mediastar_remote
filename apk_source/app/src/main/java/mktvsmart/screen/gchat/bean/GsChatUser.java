package mktvsmart.screen.gchat.bean;

import java.io.Serializable;

/* loaded from: classes.dex */
public class GsChatUser implements Serializable {
    private static final long serialVersionUID = 1;
    private boolean mBlock;
    private int mState;
    private int mUserID;
    private String mUsername;

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public void setUserID(int userID) {
        this.mUserID = userID;
    }

    public int getUserID() {
        return this.mUserID;
    }

    public boolean getBlock() {
        return this.mBlock;
    }

    public void setBlock(boolean block) {
        this.mBlock = block;
    }

    public int getState() {
        return this.mState;
    }

    public void setState(int state) {
        this.mState = state;
    }
}
