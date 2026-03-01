package javax.mail.event;

import java.util.EventListener;

/* loaded from: classes.dex */
public interface MessageCountListener extends EventListener {
    void messagesAdded(MessageCountEvent messageCountEvent);

    void messagesRemoved(MessageCountEvent messageCountEvent);
}
