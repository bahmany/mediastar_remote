package com.hisilicon.dlna.dmc.processor.model;

/* loaded from: classes.dex */
public class LocalPhoto {
    private long dateCreated;
    private String day;
    private String folder;
    private long id;
    private String mimeType;
    private String month;
    private String monthDisplayName;
    private String path;
    private long size;
    private long thumbId;
    private String thumbPath;
    private String title;
    private String year;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDay() {
        return this.day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFolder() {
        return this.folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMonth() {
        return this.month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMonthDisplayName() {
        return this.monthDisplayName;
    }

    public void setMonthDisplayName(String monthDisplayName) {
        this.monthDisplayName = monthDisplayName;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public long getThumbId() {
        return this.thumbId;
    }

    public void setThumbId(long thumbId) {
        this.thumbId = thumbId;
    }

    public String getThumbPath() {
        return this.thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
}
