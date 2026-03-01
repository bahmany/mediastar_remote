package org.teleal.cling.protocol.sync;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.gena.IncomingSubscribeResponseMessage;
import org.teleal.cling.model.message.gena.OutgoingSubscribeRequestMessage;
import org.teleal.cling.protocol.SendingSync;

/* loaded from: classes.dex */
public class SendingSubscribe extends SendingSync<OutgoingSubscribeRequestMessage, IncomingSubscribeResponseMessage> {
    private static final Logger log = Logger.getLogger(SendingSubscribe.class.getName());
    protected final RemoteGENASubscription subscription;

    public SendingSubscribe(UpnpService upnpService, RemoteGENASubscription subscription) {
        super(upnpService, new OutgoingSubscribeRequestMessage(subscription, subscription.getEventCallbackURLs(upnpService.getRouter().getActiveStreamServers(subscription.getService().getDevice().getIdentity().getDiscoveredOnLocalAddress()), upnpService.getConfiguration().getNamespace())));
        this.subscription = subscription;
    }

    @Override // org.teleal.cling.protocol.SendingSync
    public IncomingSubscribeResponseMessage executeSync() {
        IncomingSubscribeResponseMessage responseMessage = null;
        if (!getInputMessage().hasCallbackURLs()) {
            log.fine("Subscription failed, no active local callback URLs available (network disabled?)");
            getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.1
                AnonymousClass1() {
                }

                @Override // java.lang.Runnable
                public void run() {
                    SendingSubscribe.this.subscription.fail(null);
                }
            });
        } else {
            log.fine("Sending subscription request: " + getInputMessage());
            try {
                getUpnpService().getRegistry().lockRemoteSubscriptions();
                StreamResponseMessage response = getUpnpService().getRouter().send(getInputMessage());
                if (response == null) {
                    log.fine("Subscription failed, no response received");
                    getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.2
                        AnonymousClass2() {
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            SendingSubscribe.this.subscription.fail(null);
                        }
                    });
                } else {
                    responseMessage = new IncomingSubscribeResponseMessage(response);
                    if (response.getOperation().isFailed()) {
                        log.fine("Subscription failed, response was: " + responseMessage);
                        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.3
                            private final /* synthetic */ IncomingSubscribeResponseMessage val$responseMessage;

                            AnonymousClass3(IncomingSubscribeResponseMessage responseMessage2) {
                                incomingSubscribeResponseMessage = responseMessage2;
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                SendingSubscribe.this.subscription.fail(incomingSubscribeResponseMessage.getOperation());
                            }
                        });
                    } else if (!responseMessage2.isVaildHeaders()) {
                        log.severe("Subscription failed, invalid or missing (SID, Timeout) response headers");
                        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.4
                            private final /* synthetic */ IncomingSubscribeResponseMessage val$responseMessage;

                            AnonymousClass4(IncomingSubscribeResponseMessage responseMessage2) {
                                incomingSubscribeResponseMessage = responseMessage2;
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                SendingSubscribe.this.subscription.fail(incomingSubscribeResponseMessage.getOperation());
                            }
                        });
                    } else {
                        log.fine("Subscription established, adding to registry, response was: " + response);
                        this.subscription.setSubscriptionId(responseMessage2.getSubscriptionId());
                        this.subscription.setActualSubscriptionDurationSeconds(responseMessage2.getSubscriptionDurationSeconds());
                        getUpnpService().getRegistry().addRemoteSubscription(this.subscription);
                        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingSubscribe.5
                            AnonymousClass5() {
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                SendingSubscribe.this.subscription.establish();
                            }
                        });
                    }
                }
            } finally {
                getUpnpService().getRegistry().unlockRemoteSubscriptions();
            }
        }
        return responseMessage2;
    }

    /* renamed from: org.teleal.cling.protocol.sync.SendingSubscribe$1 */
    class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            SendingSubscribe.this.subscription.fail(null);
        }
    }

    /* renamed from: org.teleal.cling.protocol.sync.SendingSubscribe$2 */
    class AnonymousClass2 implements Runnable {
        AnonymousClass2() {
        }

        @Override // java.lang.Runnable
        public void run() {
            SendingSubscribe.this.subscription.fail(null);
        }
    }

    /* renamed from: org.teleal.cling.protocol.sync.SendingSubscribe$3 */
    class AnonymousClass3 implements Runnable {
        private final /* synthetic */ IncomingSubscribeResponseMessage val$responseMessage;

        AnonymousClass3(IncomingSubscribeResponseMessage responseMessage2) {
            incomingSubscribeResponseMessage = responseMessage2;
        }

        @Override // java.lang.Runnable
        public void run() {
            SendingSubscribe.this.subscription.fail(incomingSubscribeResponseMessage.getOperation());
        }
    }

    /* renamed from: org.teleal.cling.protocol.sync.SendingSubscribe$4 */
    class AnonymousClass4 implements Runnable {
        private final /* synthetic */ IncomingSubscribeResponseMessage val$responseMessage;

        AnonymousClass4(IncomingSubscribeResponseMessage responseMessage2) {
            incomingSubscribeResponseMessage = responseMessage2;
        }

        @Override // java.lang.Runnable
        public void run() {
            SendingSubscribe.this.subscription.fail(incomingSubscribeResponseMessage.getOperation());
        }
    }

    /* renamed from: org.teleal.cling.protocol.sync.SendingSubscribe$5 */
    class AnonymousClass5 implements Runnable {
        AnonymousClass5() {
        }

        @Override // java.lang.Runnable
        public void run() {
            SendingSubscribe.this.subscription.establish();
        }
    }
}
