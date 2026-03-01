package mktvsmart.screen.dataconvert.model;

/* loaded from: classes.dex */
public class DataConvertControlModel {
    private int AgeRatingSwitch;
    private int EditChannelLockSwitch;
    private int InstallLockSwitch;
    private int IsLockScreen;
    private int IsPowerOff;
    private int NetworkLockSwitch;
    private String Password;
    private int PswLockSwitch;
    private int ServiceLockSwitch;
    private int SettingsLockSwitch;
    private int sleepSwitch;
    private int sleepTime;

    public void setSleepTime(int time) {
        this.sleepTime = time;
    }

    public int getSleepTime() {
        return this.sleepTime;
    }

    public void setSleepSwitch(int onOff) {
        this.sleepSwitch = onOff;
    }

    public int getSleepSwitch() {
        return this.sleepSwitch;
    }

    public void SetPassword(String pass) {
        this.Password = pass;
    }

    public String GetPassword() {
        return this.Password;
    }

    public void SetPswLockSwitch(int cSwitch) {
        this.PswLockSwitch = cSwitch;
    }

    public int GetPswLockSwitch() {
        return this.PswLockSwitch;
    }

    public void SetInstallLockSwitch(int cSwitch) {
        this.InstallLockSwitch = cSwitch;
    }

    public int GetInstallLockSwitch() {
        return this.InstallLockSwitch;
    }

    public void SetEditChannelLockSwitch(int cSwitch) {
        this.EditChannelLockSwitch = cSwitch;
    }

    public int GetEditChannelLockSwitch() {
        return this.EditChannelLockSwitch;
    }

    public void SetSettingsLockSwitch(int cSwitch) {
        this.SettingsLockSwitch = cSwitch;
    }

    public int GetSettingsLockSwitch() {
        return this.SettingsLockSwitch;
    }

    public void SetServiceLockSwitch(int cSwitch) {
        this.ServiceLockSwitch = cSwitch;
    }

    public int GetServiceLockSwitch() {
        return this.ServiceLockSwitch;
    }

    public void SetNetworkLockSwitch(int cSwitch) {
        this.NetworkLockSwitch = cSwitch;
    }

    public int GetNetworkLockSwitch() {
        return this.NetworkLockSwitch;
    }

    public void SetAgeRatingSwitch(int cSwitch) {
        this.AgeRatingSwitch = cSwitch;
    }

    public int GetAgeRatingSwitch() {
        return this.AgeRatingSwitch;
    }

    public void SetIsLockScreen(int lock) {
        this.IsLockScreen = lock;
    }

    public int GetIsLockScreen() {
        return this.IsLockScreen;
    }

    public void SetPowerOff(int power) {
        this.IsPowerOff = power;
    }

    public int GetPowerOff() {
        return this.IsPowerOff;
    }
}
