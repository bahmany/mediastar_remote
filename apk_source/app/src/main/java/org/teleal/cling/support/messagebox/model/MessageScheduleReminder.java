package org.teleal.cling.support.messagebox.model;

import org.cybergarage.http.HTTP;
import org.cybergarage.soap.SOAP;
import org.teleal.cling.support.messagebox.model.Message;
import org.teleal.cling.support.messagebox.parser.MessageElement;

/* loaded from: classes.dex */
public class MessageScheduleReminder extends Message {
    private final String body;
    private final DateTime endTime;
    private final String location;
    private final NumberName owner;
    private final DateTime startTime;
    private final String subject;

    public MessageScheduleReminder(DateTime startTime, NumberName owner, String subject, DateTime endTime, String location, String body) {
        this(Message.DisplayType.MAXIMUM, startTime, owner, subject, endTime, location, body);
    }

    public MessageScheduleReminder(Message.DisplayType displayType, DateTime startTime, NumberName owner, String subject, DateTime endTime, String location, String body) {
        super(Message.Category.SCHEDULE_REMINDER, displayType);
        this.startTime = startTime;
        this.owner = owner;
        this.subject = subject;
        this.endTime = endTime;
        this.location = location;
        this.body = body;
    }

    public DateTime getStartTime() {
        return this.startTime;
    }

    public NumberName getOwner() {
        return this.owner;
    }

    public String getSubject() {
        return this.subject;
    }

    public DateTime getEndTime() {
        return this.endTime;
    }

    public String getLocation() {
        return this.location;
    }

    public String getBody() {
        return this.body;
    }

    @Override // org.teleal.cling.support.messagebox.model.ElementAppender
    public void appendMessageElements(MessageElement parent) {
        getStartTime().appendMessageElements((MessageElement) parent.createChild("StartTime"));
        getOwner().appendMessageElements((MessageElement) parent.createChild("Owner"));
        ((MessageElement) parent.createChild("Subject")).setContent(getSubject());
        getEndTime().appendMessageElements((MessageElement) parent.createChild("EndTime"));
        ((MessageElement) parent.createChild(HTTP.LOCATION)).setContent(getLocation());
        ((MessageElement) parent.createChild(SOAP.BODY)).setContent(getBody());
    }
}
