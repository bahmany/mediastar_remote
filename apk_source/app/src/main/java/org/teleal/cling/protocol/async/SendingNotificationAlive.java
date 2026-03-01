package org.teleal.cling.protocol.async;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.NotificationSubtype;

/* loaded from: classes.dex */
public class SendingNotificationAlive extends SendingNotification {
    private static final Logger log = Logger.getLogger(SendingNotification.class.getName());

    public SendingNotificationAlive(UpnpService upnpService, LocalDevice device) {
        super(upnpService, device);
    }

    @Override // org.teleal.cling.protocol.async.SendingNotification, org.teleal.cling.protocol.SendingAsync
    protected void execute() throws InterruptedException {
        log.fine("Sending alive messages (" + getBulkRepeat() + " times) for: " + getDevice());
        super.execute();
    }

    @Override // org.teleal.cling.protocol.async.SendingNotification
    protected NotificationSubtype getNotificationSubtype() {
        return NotificationSubtype.ALIVE;
    }
}
