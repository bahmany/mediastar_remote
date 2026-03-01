package org.teleal.cling.controlpoint;

import android.support.v7.internal.widget.ActivityChooserView;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.meta.Service;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public abstract class SubscriptionCallback implements Runnable {
    protected static Logger log = Logger.getLogger(SubscriptionCallback.class.getName());
    private ControlPoint controlPoint;
    protected final Integer requestedDurationSeconds;
    protected final Service service;
    private GENASubscription subscription;

    protected abstract void ended(GENASubscription gENASubscription, CancelReason cancelReason, UpnpResponse upnpResponse);

    protected abstract void established(GENASubscription gENASubscription);

    protected abstract void eventReceived(GENASubscription gENASubscription);

    protected abstract void eventsMissed(GENASubscription gENASubscription, int i);

    protected abstract void failed(GENASubscription gENASubscription, UpnpResponse upnpResponse, Exception exc, String str);

    protected SubscriptionCallback(Service service) {
        this.service = service;
        this.requestedDurationSeconds = 1800;
    }

    protected SubscriptionCallback(Service service, int requestedDurationSeconds) {
        this.service = service;
        this.requestedDurationSeconds = Integer.valueOf(requestedDurationSeconds);
    }

    public Service getService() {
        return this.service;
    }

    public synchronized ControlPoint getControlPoint() {
        return this.controlPoint;
    }

    public synchronized void setControlPoint(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }

    public synchronized GENASubscription getSubscription() {
        return this.subscription;
    }

    public synchronized void setSubscription(GENASubscription subscription) {
        this.subscription = subscription;
    }

    @Override // java.lang.Runnable
    public synchronized void run() {
        if (getControlPoint() == null) {
            throw new IllegalStateException("Callback must be executed through ControlPoint");
        }
        if (getService() instanceof LocalService) {
            establishLocalSubscription((LocalService) this.service);
        } else if (getService() instanceof RemoteService) {
            establishRemoteSubscription((RemoteService) this.service);
        }
    }

    private void establishLocalSubscription(LocalService service) {
        LocalGENASubscription localSubscription;
        if (getControlPoint().getRegistry().getLocalDevice(service.getDevice().getIdentity().getUdn(), false) == null) {
            log.fine("Local device service is currently not registered, failing subscription immediately");
            failed(null, null, new IllegalStateException("Local device is not registered"));
            return;
        }
        LocalGENASubscription localSubscription2 = null;
        try {
            localSubscription = new LocalGENASubscription(service, Integer.valueOf(ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED), Collections.EMPTY_LIST) { // from class: org.teleal.cling.controlpoint.SubscriptionCallback.1
                public void failed(Exception ex) {
                    synchronized (SubscriptionCallback.this) {
                        SubscriptionCallback.this.setSubscription(null);
                        SubscriptionCallback.this.failed(null, null, ex);
                    }
                }

                @Override // org.teleal.cling.model.gena.GENASubscription
                public void established() {
                    synchronized (SubscriptionCallback.this) {
                        SubscriptionCallback.this.setSubscription(this);
                        SubscriptionCallback.this.established(this);
                    }
                }

                @Override // org.teleal.cling.model.gena.LocalGENASubscription
                public void ended(CancelReason reason) {
                    synchronized (SubscriptionCallback.this) {
                        SubscriptionCallback.this.setSubscription(null);
                        SubscriptionCallback.this.ended(this, reason, null);
                    }
                }

                @Override // org.teleal.cling.model.gena.GENASubscription
                public void eventReceived() {
                    synchronized (SubscriptionCallback.this) {
                        SubscriptionCallback.log.fine("Local service state updated, notifying callback, sequence is: " + getCurrentSequence());
                        SubscriptionCallback.this.eventReceived(this);
                        incrementSequence();
                    }
                }
            };
        } catch (Exception e) {
            ex = e;
        }
        try {
            log.fine("Local device service is currently registered, also registering subscription");
            getControlPoint().getRegistry().addLocalSubscription(localSubscription);
            log.fine("Notifying subscription callback of local subscription availablity");
            localSubscription.establish();
            log.fine("Simulating first initial event for local subscription callback, sequence: " + localSubscription.getCurrentSequence());
            eventReceived(localSubscription);
            localSubscription.incrementSequence();
            log.fine("Starting to monitor state changes of local service");
            localSubscription.registerOnService();
        } catch (Exception e2) {
            ex = e2;
            localSubscription2 = localSubscription;
            log.fine("Local callback creation failed: " + ex.toString());
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            if (localSubscription2 != null) {
                getControlPoint().getRegistry().removeLocalSubscription(localSubscription2);
            }
            failed(localSubscription2, null, ex);
        }
    }

    private void establishRemoteSubscription(RemoteService service) {
        RemoteGENASubscription remoteSubscription = new RemoteGENASubscription(service, this.requestedDurationSeconds.intValue()) { // from class: org.teleal.cling.controlpoint.SubscriptionCallback.2
            @Override // org.teleal.cling.model.gena.RemoteGENASubscription
            public void failed(UpnpResponse responseStatus) {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.setSubscription(null);
                    SubscriptionCallback.this.failed(this, responseStatus, null);
                }
            }

            @Override // org.teleal.cling.model.gena.GENASubscription
            public void established() {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.setSubscription(this);
                    SubscriptionCallback.this.established(this);
                }
            }

            @Override // org.teleal.cling.model.gena.RemoteGENASubscription
            public void ended(CancelReason reason, UpnpResponse responseStatus) {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.setSubscription(null);
                    SubscriptionCallback.this.ended(this, reason, responseStatus);
                }
            }

            @Override // org.teleal.cling.model.gena.GENASubscription
            public void eventReceived() {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.eventReceived(this);
                }
            }

            @Override // org.teleal.cling.model.gena.RemoteGENASubscription
            public void eventsMissed(int numberOfMissedEvents) {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.eventsMissed(this, numberOfMissedEvents);
                }
            }
        };
        getControlPoint().getProtocolFactory().createSendingSubscribe(remoteSubscription).run();
    }

    public synchronized void end() {
        if (this.subscription != null) {
            if (this.subscription instanceof LocalGENASubscription) {
                endLocalSubscription((LocalGENASubscription) this.subscription);
            } else if (this.subscription instanceof RemoteGENASubscription) {
                endRemoteSubscription((RemoteGENASubscription) this.subscription);
            }
        }
    }

    private void endLocalSubscription(LocalGENASubscription subscription) {
        log.fine("Removing local subscription and ending it in callback: " + subscription);
        getControlPoint().getRegistry().removeLocalSubscription(subscription);
        subscription.end(null);
    }

    private void endRemoteSubscription(RemoteGENASubscription subscription) {
        log.fine("Ending remote subscription: " + subscription);
        getControlPoint().getConfiguration().getSyncProtocolExecutor().execute(getControlPoint().getProtocolFactory().createSendingUnsubscribe(subscription));
    }

    protected void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception) {
        failed(subscription, responseStatus, exception, createDefaultFailureMessage(responseStatus, exception));
    }

    public static String createDefaultFailureMessage(UpnpResponse responseStatus, Exception exception) {
        if (responseStatus != null) {
            String message = String.valueOf("Subscription failed: ") + " HTTP response was: " + responseStatus.getResponseDetails();
            return message;
        }
        if (exception != null) {
            String message2 = String.valueOf("Subscription failed: ") + " Exception occured: " + exception;
            return message2;
        }
        String message3 = String.valueOf("Subscription failed: ") + " No response received.";
        return message3;
    }

    public String toString() {
        return "(SubscriptionCallback) " + getService();
    }
}
