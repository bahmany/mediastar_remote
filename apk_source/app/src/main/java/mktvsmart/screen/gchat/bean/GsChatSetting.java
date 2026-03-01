package mktvsmart.screen.gchat.bean;

/* loaded from: classes.dex */
public class GsChatSetting {
    private static GsChatSetting mInstance = new GsChatSetting();
    private String mSerialNumber;
    private int mShowWindow;
    private int mUserId;
    private String mUsername;
    private int mWindowPosition;
    private int mWindowSize;
    private int mWindowTransparency;

    private GsChatSetting() {
    }

    public static GsChatSetting getInstance() {
        return mInstance;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public void setSerialNumber(String serialNumber) {
        this.mSerialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return this.mSerialNumber;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public void setShowWindow(int showWindow) {
        this.mShowWindow = showWindow;
    }

    public int getSHowWindow() {
        return this.mShowWindow;
    }

    public void setWindowSize(int windowSize) {
        this.mWindowSize = windowSize;
    }

    public int getWindowSize() {
        return this.mWindowSize;
    }

    public void setWindowPosition(int windowPosition) {
        this.mWindowPosition = windowPosition;
    }

    public int getWindowPosition() {
        return this.mWindowPosition;
    }

    public void setWindowTransparency(int windowTransparency) {
        this.mWindowTransparency = windowTransparency;
    }

    public int getWindowTransparency() {
        return this.mWindowTransparency;
    }
}
