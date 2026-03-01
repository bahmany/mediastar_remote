package org.teleal.cling.support.messagebox.model;

import org.cybergarage.soap.SOAP;
import org.teleal.cling.support.messagebox.model.Message;
import org.teleal.cling.support.messagebox.parser.MessageElement;

/* loaded from: classes.dex */
public class MessageSMS extends Message {
    private final String body;
    private final DateTime receiveTime;
    private final NumberName receiver;
    private final NumberName sender;

    public MessageSMS(NumberName receiver, NumberName sender, String body) {
        this(new DateTime(), receiver, sender, body);
    }

    public MessageSMS(DateTime receiveTime, NumberName receiver, NumberName sender, String body) {
        this(Message.DisplayType.MAXIMUM, receiveTime, receiver, sender, body);
    }

    public MessageSMS(Message.DisplayType displayType, DateTime receiveTime, NumberName receiver, NumberName sender, String body) {
        super(Message.Category.SMS, displayType);
        this.receiveTime = receiveTime;
        this.receiver = receiver;
        this.sender = sender;
        this.body = body;
    }

    public DateTime getReceiveTime() {
        return this.receiveTime;
    }

    public NumberName getReceiver() {
        return this.receiver;
    }

    public NumberName getSender() {
        return this.sender;
    }

    public String getBody() {
        return this.body;
    }

    @Override // org.teleal.cling.support.messagebox.model.ElementAppender
    public void appendMessageElements(MessageElement parent) {
        getReceiveTime().appendMessageElements((MessageElement) parent.createChild("ReceiveTime"));
        getReceiver().appendMessageElements((MessageElement) parent.createChild("Receiver"));
        getSender().appendMessageElements((MessageElement) parent.createChild("Sender"));
        ((MessageElement) parent.createChild(SOAP.BODY)).setContent(getBody());
    }
}
