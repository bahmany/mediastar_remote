package com.hisilicon.multiscreen.protocol;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public class ServiceInfo implements Parcelable {
    public static final Parcelable.Creator<ServiceInfo> CREATOR = new Parcelable.Creator<ServiceInfo>() { // from class: com.hisilicon.multiscreen.protocol.ServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceInfo createFromParcel(Parcel source) {
            ServiceInfo p = new ServiceInfo();
            p.setServiceName(source.readString());
            p.setServicePort(source.readInt());
            return p;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceInfo[] newArray(int size) {
            return new ServiceInfo[size];
        }
    };
    private String mServiceName;
    private int mServicePort;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mServiceName);
        dest.writeInt(this.mServicePort);
    }

    public void setServiceName(String name) {
        this.mServiceName = name;
    }

    public void setServicePort(int port) {
        this.mServicePort = port;
    }

    public String getServiceName() {
        return this.mServiceName;
    }

    public int getServicePort() {
        return this.mServicePort;
    }
}
