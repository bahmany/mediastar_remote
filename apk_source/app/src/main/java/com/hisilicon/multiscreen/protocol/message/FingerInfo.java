package com.hisilicon.multiscreen.protocol.message;

/* loaded from: classes.dex */
public class FingerInfo {
    private boolean mIsPressed;
    private int mX;
    private int mY;

    public void setX(int x) {
        this.mX = x;
    }

    public int getX() {
        return this.mX;
    }

    public void setY(int y) {
        this.mY = y;
    }

    public int getY() {
        return this.mY;
    }

    public void setPress(boolean press) {
        this.mIsPressed = press;
    }

    public boolean getPress() {
        return this.mIsPressed;
    }
}
