package org.apache.mina.filter.keepalive;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: classes.dex */
public interface KeepAliveRequestTimeoutHandler {
    public static final KeepAliveRequestTimeoutHandler NOOP = new KeepAliveRequestTimeoutHandler() { // from class: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler.1
        AnonymousClass1() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
        }
    };
    public static final KeepAliveRequestTimeoutHandler LOG = new KeepAliveRequestTimeoutHandler() { // from class: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler.2
        private final Logger LOGGER = LoggerFactory.getLogger(KeepAliveFilter.class);

        AnonymousClass2() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            this.LOGGER.warn("A keep-alive response message was not received within {} second(s).", Integer.valueOf(filter.getRequestTimeout()));
        }
    };
    public static final KeepAliveRequestTimeoutHandler EXCEPTION = new KeepAliveRequestTimeoutHandler() { // from class: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler.3
        AnonymousClass3() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            throw new KeepAliveRequestTimeoutException("A keep-alive response message was not received within " + filter.getRequestTimeout() + " second(s).");
        }
    };
    public static final KeepAliveRequestTimeoutHandler CLOSE = new KeepAliveRequestTimeoutHandler() { // from class: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler.4
        private final Logger LOGGER = LoggerFactory.getLogger(KeepAliveFilter.class);

        AnonymousClass4() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            this.LOGGER.warn("Closing the session because a keep-alive response message was not received within {} second(s).", Integer.valueOf(filter.getRequestTimeout()));
            session.close(true);
        }
    };
    public static final KeepAliveRequestTimeoutHandler DEAF_SPEAKER = new KeepAliveRequestTimeoutHandler() { // from class: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler.5
        AnonymousClass5() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            throw new Error("Shouldn't be invoked.  Please file a bug report.");
        }
    };

    void keepAliveRequestTimedOut(KeepAliveFilter keepAliveFilter, IoSession ioSession) throws Exception;

    /* renamed from: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler$1 */
    static class AnonymousClass1 implements KeepAliveRequestTimeoutHandler {
        AnonymousClass1() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
        }
    }

    /* renamed from: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler$2 */
    static class AnonymousClass2 implements KeepAliveRequestTimeoutHandler {
        private final Logger LOGGER = LoggerFactory.getLogger(KeepAliveFilter.class);

        AnonymousClass2() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            this.LOGGER.warn("A keep-alive response message was not received within {} second(s).", Integer.valueOf(filter.getRequestTimeout()));
        }
    }

    /* renamed from: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler$3 */
    static class AnonymousClass3 implements KeepAliveRequestTimeoutHandler {
        AnonymousClass3() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            throw new KeepAliveRequestTimeoutException("A keep-alive response message was not received within " + filter.getRequestTimeout() + " second(s).");
        }
    }

    /* renamed from: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler$4 */
    static class AnonymousClass4 implements KeepAliveRequestTimeoutHandler {
        private final Logger LOGGER = LoggerFactory.getLogger(KeepAliveFilter.class);

        AnonymousClass4() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            this.LOGGER.warn("Closing the session because a keep-alive response message was not received within {} second(s).", Integer.valueOf(filter.getRequestTimeout()));
            session.close(true);
        }
    }

    /* renamed from: org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler$5 */
    static class AnonymousClass5 implements KeepAliveRequestTimeoutHandler {
        AnonymousClass5() {
        }

        @Override // org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            throw new Error("Shouldn't be invoked.  Please file a bug report.");
        }
    }
}
