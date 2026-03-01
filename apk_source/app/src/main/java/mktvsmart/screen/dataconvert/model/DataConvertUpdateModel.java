package mktvsmart.screen.dataconvert.model;

/* loaded from: classes.dex */
public class DataConvertUpdateModel {
    private int customerId;
    private int dataLen;
    private int modelId;
    private int versionId;

    public int GetDataLen() {
        return this.dataLen;
    }

    public void SetDataLen(int len) {
        this.dataLen = len;
    }

    public int GetCustomerId() {
        return this.customerId;
    }

    public void SetCustomerId(int id) {
        this.customerId = id;
    }

    public int GetModelId() {
        return this.modelId;
    }

    public void SetModelId(int id) {
        this.modelId = id;
    }

    public int GetVersionId() {
        return this.versionId;
    }

    public void SetVersionId(int id) {
        this.versionId = id;
    }
}
