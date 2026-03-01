package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.view.View;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: classes.dex */
public final class ClientSettings {
    private final View IG;
    private final ParcelableClientSettings Lk;

    public static final class ParcelableClientSettings implements SafeParcelable {
        public static final ParcelableClientSettingsCreator CREATOR = new ParcelableClientSettingsCreator();
        private final int BR;
        private final String Dd;
        private final int IF;
        private final String IH;
        private final List<String> Jd;

        ParcelableClientSettings(int versionCode, String accountName, List<String> scopes, int gravityForPopups, String realClientPackageName) {
            this.Jd = new ArrayList();
            this.BR = versionCode;
            this.Dd = accountName;
            this.Jd.addAll(scopes);
            this.IF = gravityForPopups;
            this.IH = realClientPackageName;
        }

        public ParcelableClientSettings(String accountName, Collection<String> scopes, int gravityForPopups, String realClientPackageName) {
            this(3, accountName, new ArrayList(scopes), gravityForPopups, realClientPackageName);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String getAccountName() {
            return this.Dd;
        }

        public String getAccountNameOrDefault() {
            return this.Dd != null ? this.Dd : "<<default account>>";
        }

        public int getGravityForPopups() {
            return this.IF;
        }

        public String getRealClientPackageName() {
            return this.IH;
        }

        public List<String> getScopes() {
            return new ArrayList(this.Jd);
        }

        public int getVersionCode() {
            return this.BR;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            ParcelableClientSettingsCreator.a(this, out, flags);
        }
    }

    public ClientSettings(String accountName, Collection<String> scopes, int gravityForPopups, View viewForPopups, String realClientPackageName) {
        this.Lk = new ParcelableClientSettings(accountName, scopes, gravityForPopups, realClientPackageName);
        this.IG = viewForPopups;
    }

    public String getAccountName() {
        return this.Lk.getAccountName();
    }

    public String getAccountNameOrDefault() {
        return this.Lk.getAccountNameOrDefault();
    }

    public int getGravityForPopups() {
        return this.Lk.getGravityForPopups();
    }

    public ParcelableClientSettings getParcelableClientSettings() {
        return this.Lk;
    }

    public String getRealClientPackageName() {
        return this.Lk.getRealClientPackageName();
    }

    public List<String> getScopes() {
        return this.Lk.getScopes();
    }

    public String[] getScopesArray() {
        return (String[]) this.Lk.getScopes().toArray(new String[0]);
    }

    public View getViewForPopups() {
        return this.IG;
    }
}
