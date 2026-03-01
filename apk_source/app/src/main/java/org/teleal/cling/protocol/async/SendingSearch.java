package org.teleal.cling.protocol.async;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.message.discovery.OutgoingSearchRequest;
import org.teleal.cling.model.message.header.MXHeader;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.protocol.SendingAsync;

/* loaded from: classes.dex */
public class SendingSearch extends SendingAsync {
    private static final Logger log = Logger.getLogger(SendingSearch.class.getName());
    private final int mxSeconds;
    private final UpnpHeader searchTarget;

    public SendingSearch(UpnpService upnpService) {
        this(upnpService, new STAllHeader());
    }

    public SendingSearch(UpnpService upnpService, UpnpHeader searchTarget) {
        this(upnpService, searchTarget, MXHeader.DEFAULT_VALUE.intValue());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public SendingSearch(UpnpService upnpService, UpnpHeader searchTarget, int mxSeconds) {
        super(upnpService);
        if (!UpnpHeader.Type.ST.isValidHeaderType(searchTarget.getClass())) {
            throw new IllegalArgumentException("Given search target instance is not a valid header class for type ST: " + searchTarget.getClass());
        }
        this.searchTarget = searchTarget;
        this.mxSeconds = mxSeconds;
    }

    public UpnpHeader getSearchTarget() {
        return this.searchTarget;
    }

    public int getMxSeconds() {
        return this.mxSeconds;
    }

    @Override // org.teleal.cling.protocol.SendingAsync
    protected void execute() throws InterruptedException {
        log.fine("Executing search for target: " + this.searchTarget.getString() + " with MX seconds: " + getMxSeconds());
        OutgoingSearchRequest msg = new OutgoingSearchRequest(this.searchTarget, getMxSeconds());
        for (int i = 0; i < getBulkRepeat(); i++) {
            try {
                getUpnpService().getRouter().send(msg);
                log.finer("Sleeping " + getBulkIntervalMilliseconds() + " milliseconds");
                Thread.sleep(getBulkIntervalMilliseconds());
            } catch (InterruptedException ex) {
                log.warning("Search sending thread was interrupted: " + ex);
            }
        }
    }

    public int getBulkRepeat() {
        return 2;
    }

    public int getBulkIntervalMilliseconds() {
        return 100;
    }
}
