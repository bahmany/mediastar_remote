package org.teleal.cling.protocol;

import org.teleal.cling.UpnpService;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.header.UpnpHeader;

/* loaded from: classes.dex */
public abstract class ReceivingAsync<M extends UpnpMessage> implements Runnable {
    private M inputMessage;
    private final UpnpService upnpService;

    protected abstract void execute();

    protected ReceivingAsync(UpnpService upnpService, M inputMessage) {
        this.upnpService = upnpService;
        this.inputMessage = inputMessage;
    }

    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    public M getInputMessage() {
        return this.inputMessage;
    }

    @Override // java.lang.Runnable
    public void run() {
        boolean proceed;
        try {
            proceed = waitBeforeExecution();
        } catch (InterruptedException e) {
            proceed = false;
        }
        if (proceed) {
            execute();
        }
    }

    protected boolean waitBeforeExecution() throws InterruptedException {
        return true;
    }

    protected <H extends UpnpHeader> H getFirstHeader(UpnpHeader.Type type, Class<H> cls) {
        return (H) getInputMessage().getHeaders().getFirstHeader(type, cls);
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}
