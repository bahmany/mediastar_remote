package javax.mail.event;

import java.util.EventListener;

/* loaded from: classes.dex */
public interface MessageChangedListener extends EventListener {
    void messageChanged(MessageChangedEvent messageChangedEvent);
}
