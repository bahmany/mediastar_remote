package org.teleal.cling.support.messagebox.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.cybergarage.http.HTTP;
import org.teleal.cling.support.messagebox.parser.MessageElement;

/* loaded from: classes.dex */
public class DateTime implements ElementAppender {
    private final String date;
    private final String time;

    public DateTime() {
        this(getCurrentDate(), getCurrentTime());
    }

    public DateTime(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    @Override // org.teleal.cling.support.messagebox.model.ElementAppender
    public void appendMessageElements(MessageElement parent) {
        ((MessageElement) parent.createChild(HTTP.DATE)).setContent(getDate());
        ((MessageElement) parent.createChild("Time")).setContent(getTime());
    }

    public static String getCurrentDate() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return fmt.format(new Date());
    }

    public static String getCurrentTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
        return fmt.format(new Date());
    }
}
