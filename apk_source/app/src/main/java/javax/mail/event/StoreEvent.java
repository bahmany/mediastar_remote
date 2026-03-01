package javax.mail.event;

import javax.mail.Store;

/* loaded from: classes.dex */
public class StoreEvent extends MailEvent {
    public static final int ALERT = 1;
    public static final int NOTICE = 2;
    private static final long serialVersionUID = 1938704919992515330L;
    protected String message;
    protected int type;

    public StoreEvent(Store store, int type, String message) {
        super(store);
        this.type = type;
        this.message = message;
    }

    public int getMessageType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    @Override // javax.mail.event.MailEvent
    public void dispatch(Object listener) {
        ((StoreListener) listener).notification(this);
    }
}
