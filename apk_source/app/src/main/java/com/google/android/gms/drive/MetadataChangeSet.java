package com.google.android.gms.drive;

import com.google.android.gms.drive.metadata.internal.AppVisibleCustomProperties;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;
import com.google.android.gms.internal.kd;
import com.google.android.gms.internal.kf;
import java.util.Date;

/* loaded from: classes.dex */
public final class MetadataChangeSet {
    public static final MetadataChangeSet Nl = new MetadataChangeSet(MetadataBundle.io());
    private final MetadataBundle Nm;

    public static class Builder {
        private final MetadataBundle Nm = MetadataBundle.io();
        private AppVisibleCustomProperties.a Nn;

        public MetadataChangeSet build() {
            if (this.Nn != null) {
                this.Nm.b(kd.PG, this.Nn.im());
            }
            return new MetadataChangeSet(this.Nm);
        }

        public Builder setDescription(String description) {
            this.Nm.b(kd.PH, description);
            return this;
        }

        public Builder setIndexableText(String text) {
            this.Nm.b(kd.PM, text);
            return this;
        }

        public Builder setLastViewedByMeDate(Date date) {
            this.Nm.b(kf.Qm, date);
            return this;
        }

        public Builder setMimeType(String mimeType) {
            this.Nm.b(kd.PV, mimeType);
            return this;
        }

        public Builder setPinned(boolean pinned) {
            this.Nm.b(kd.PQ, Boolean.valueOf(pinned));
            return this;
        }

        public Builder setStarred(boolean starred) {
            this.Nm.b(kd.Qc, Boolean.valueOf(starred));
            return this;
        }

        public Builder setTitle(String title) {
            this.Nm.b(kd.Qe, title);
            return this;
        }

        public Builder setViewed(boolean viewed) {
            this.Nm.b(kd.PU, Boolean.valueOf(viewed));
            return this;
        }
    }

    public MetadataChangeSet(MetadataBundle bag) {
        this.Nm = MetadataBundle.a(bag);
    }

    public String getDescription() {
        return (String) this.Nm.a(kd.PH);
    }

    public String getIndexableText() {
        return (String) this.Nm.a(kd.PM);
    }

    public Date getLastViewedByMeDate() {
        return (Date) this.Nm.a(kf.Qm);
    }

    public String getMimeType() {
        return (String) this.Nm.a(kd.PV);
    }

    public String getTitle() {
        return (String) this.Nm.a(kd.Qe);
    }

    public MetadataBundle hS() {
        return this.Nm;
    }

    public Boolean isPinned() {
        return (Boolean) this.Nm.a(kd.PQ);
    }

    public Boolean isStarred() {
        return (Boolean) this.Nm.a(kd.Qc);
    }

    public Boolean isViewed() {
        return (Boolean) this.Nm.a(kd.PU);
    }
}
