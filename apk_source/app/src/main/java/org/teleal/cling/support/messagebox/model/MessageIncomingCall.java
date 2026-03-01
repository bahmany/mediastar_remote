package org.teleal.cling.support.messagebox.model;

import org.teleal.cling.support.messagebox.model.Message;
import org.teleal.cling.support.messagebox.parser.MessageElement;

/* loaded from: classes.dex */
public class MessageIncomingCall extends Message {
    private final DateTime callTime;
    private final NumberName callee;
    private final NumberName caller;

    public MessageIncomingCall(NumberName callee, NumberName caller) {
        this(new DateTime(), callee, caller);
    }

    public MessageIncomingCall(DateTime callTime, NumberName callee, NumberName caller) {
        this(Message.DisplayType.MAXIMUM, callTime, callee, caller);
    }

    public MessageIncomingCall(Message.DisplayType displayType, DateTime callTime, NumberName callee, NumberName caller) {
        super(Message.Category.INCOMING_CALL, displayType);
        this.callTime = callTime;
        this.callee = callee;
        this.caller = caller;
    }

    public DateTime getCallTime() {
        return this.callTime;
    }

    public NumberName getCallee() {
        return this.callee;
    }

    public NumberName getCaller() {
        return this.caller;
    }

    @Override // org.teleal.cling.support.messagebox.model.ElementAppender
    public void appendMessageElements(MessageElement parent) {
        getCallTime().appendMessageElements((MessageElement) parent.createChild("CallTime"));
        getCallee().appendMessageElements((MessageElement) parent.createChild("Callee"));
        getCaller().appendMessageElements((MessageElement) parent.createChild("Caller"));
    }
}
