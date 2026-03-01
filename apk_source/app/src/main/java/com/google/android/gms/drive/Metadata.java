package com.google.android.gms.drive;

import com.google.android.gms.common.data.Freezable;
import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.internal.kd;
import com.google.android.gms.internal.kf;
import com.google.android.gms.internal.kh;
import java.util.Date;

/* loaded from: classes.dex */
public abstract class Metadata implements Freezable<Metadata> {
    public static final int CONTENT_AVAILABLE_LOCALLY = 1;
    public static final int CONTENT_NOT_AVAILABLE_LOCALLY = 0;

    protected abstract <T> T a(MetadataField<T> metadataField);

    public String getAlternateLink() {
        return (String) a(kd.PF);
    }

    public int getContentAvailability() {
        Integer num = (Integer) a(kh.Qr);
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    public Date getCreatedDate() {
        return (Date) a(kf.Ql);
    }

    public String getDescription() {
        return (String) a(kd.PH);
    }

    public DriveId getDriveId() {
        return (DriveId) a(kd.PE);
    }

    public String getEmbedLink() {
        return (String) a(kd.PI);
    }

    public String getFileExtension() {
        return (String) a(kd.PJ);
    }

    public long getFileSize() {
        return ((Long) a(kd.PK)).longValue();
    }

    public Date getLastViewedByMeDate() {
        return (Date) a(kf.Qm);
    }

    public String getMimeType() {
        return (String) a(kd.PV);
    }

    public Date getModifiedByMeDate() {
        return (Date) a(kf.Qo);
    }

    public Date getModifiedDate() {
        return (Date) a(kf.Qn);
    }

    public String getOriginalFilename() {
        return (String) a(kd.PW);
    }

    public long getQuotaBytesUsed() {
        return ((Long) a(kd.Qb)).longValue();
    }

    public Date getSharedWithMeDate() {
        return (Date) a(kf.Qp);
    }

    public String getTitle() {
        return (String) a(kd.Qe);
    }

    public String getWebContentLink() {
        return (String) a(kd.Qg);
    }

    public String getWebViewLink() {
        return (String) a(kd.Qh);
    }

    public boolean isEditable() {
        Boolean bool = (Boolean) a(kd.PP);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isFolder() {
        return DriveFolder.MIME_TYPE.equals(getMimeType());
    }

    public boolean isInAppFolder() {
        Boolean bool = (Boolean) a(kd.PN);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isPinnable() {
        Boolean bool = (Boolean) a(kh.Qs);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isPinned() {
        Boolean bool = (Boolean) a(kd.PQ);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isRestricted() {
        Boolean bool = (Boolean) a(kd.PR);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isShared() {
        Boolean bool = (Boolean) a(kd.PS);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isStarred() {
        Boolean bool = (Boolean) a(kd.Qc);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isTrashed() {
        Boolean bool = (Boolean) a(kd.Qf);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean isViewed() {
        Boolean bool = (Boolean) a(kd.PU);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }
}
