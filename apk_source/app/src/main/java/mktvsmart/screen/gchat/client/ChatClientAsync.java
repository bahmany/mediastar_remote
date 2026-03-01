package mktvsmart.screen.gchat.client;

import android.content.AsyncQueryHandler;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;
import mktvsmart.screen.gchat.bean.GChatLoginInfo;
import mktvsmart.screen.gchat.bean.GsChatUser;
import mktvsmart.screen.json.parser.GChatJsonParseBlackList;
import mktvsmart.screen.json.parser.GChatJsonParseLoginInfo;
import mktvsmart.screen.json.parser.GChatJsonParseNewMessage;
import mktvsmart.screen.json.parser.GChatJsonParseRepeatLogin;
import mktvsmart.screen.json.parser.GChatJsonParseResponseState;
import mktvsmart.screen.json.parser.GChatJsonParseRoomInfo;
import mktvsmart.screen.json.parser.GChatJsonParseServerInfo;
import mktvsmart.screen.json.parser.GChatJsonParseUserChange;
import mktvsmart.screen.json.parser.GChatJsonParseUserList;
import mktvsmart.screen.json.serialize.GChatJsonSerializeCommand;
import mktvsmart.screen.json.serialize.JsonSerialize;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/* loaded from: classes.dex */
public class ChatClientAsync extends Handler {
    private static final String BALANCE_SERVER_HOST = "g-chat.webhop.net";
    private static final int BALANCE_SERVER_PORT = 9877;
    private static final int EVENT_ARG_CONNECT_BALANCE_SERVER = 0;
    private static final int EVENT_ARG_CONNECT_MESSAGE_SERVER = 1;
    private static final int EVENT_ARG_CONNECT_SERVER_FAILURE = 3;
    private static final int EVENT_ARG_CONNECT_SERVER_SUCCESS = 2;
    private static final int EVENT_ARG_HEARTBEAT_TIMEOUT = 5;
    private static final int EVENT_ARG_RECEIVED_BLACKLIST = 10;
    private static final int EVENT_ARG_RECEIVED_BLACKLIST_SET_RESULT = 13;
    private static final int EVENT_ARG_RECEIVED_HEARTBEAT = 4;
    private static final int EVENT_ARG_RECEIVED_LOGIN_RESULT = 7;
    private static final int EVENT_ARG_RECEIVED_MESSAGE_SERVER_INFO = 6;
    private static final int EVENT_ARG_RECEIVED_MSG_SEND_RESULT = 11;
    private static final int EVENT_ARG_RECEIVED_NEW_MESSAGE = 12;
    private static final int EVENT_ARG_RECEIVED_REPEAT_LOGIN = 15;
    private static final int EVENT_ARG_RECEIVED_ROOM_INFO = 8;
    private static final int EVENT_ARG_RECEIVED_SET_USERNAME_RESULT = 16;
    private static final int EVENT_ARG_RECEIVED_USER_CHANGE = 14;
    private static final int EVENT_ARG_RECEIVED_USER_LIST = 9;
    private static final int KEEP_ALIVE_TIME = 30;
    private static ChatClientAsync mInstance;
    private ChatConnector mBalanceServerConnector;
    private TimerTask mKeepAliveTask;
    private ChatConnector mMessageServerConnector;
    private Handler mWorkerThreadHandler;
    private static final String TAG = ChatClientAsync.class.getSimpleName();
    private static Looper sLooper = null;
    private List<WeakReference<GChatClientListener>> mChatClientListeners = new ArrayList();
    private long mLastHeartbeatSendTime = -1;
    private long mLastHeartbeatReceiveTime = -1;
    private IoHandlerAdapter mGChatHandler = new IoHandlerAdapter() { // from class: mktvsmart.screen.gchat.client.ChatClientAsync.1
        AnonymousClass1() {
        }

        @Override // org.apache.mina.core.service.IoHandlerAdapter, org.apache.mina.core.service.IoHandler
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            cause.printStackTrace();
        }

        @Override // org.apache.mina.core.service.IoHandlerAdapter, org.apache.mina.core.service.IoHandler
        public void messageReceived(IoSession session, Object message) throws Exception {
            ChatClientAsync.this.printPackage(message, true);
            TransmissionPackage msgPackage = (TransmissionPackage) message;
            byte[] data = msgPackage.getBody();
            switch (msgPackage.getType()) {
                case 0:
                    ChatClientAsync.this.processEventOnMainThread(4, null);
                    break;
                case 2:
                    if (data != null) {
                        GChatJsonParseServerInfo serverInfoParse = new GChatJsonParseServerInfo();
                        ChatClientAsync.this.processEventOnMainThread(6, serverInfoParse.parse(data));
                        break;
                    }
                    break;
                case 4:
                    if (data != null) {
                        GChatJsonParseLoginInfo loginInfoParse = new GChatJsonParseLoginInfo();
                        ChatClientAsync.this.processEventOnMainThread(7, loginInfoParse.parse(data));
                        break;
                    }
                    break;
                case 6:
                    if (data != null) {
                        GChatJsonParseRoomInfo RoomInfoParse = new GChatJsonParseRoomInfo();
                        ChatClientAsync.this.processEventOnMainThread(8, RoomInfoParse.parse(data));
                        break;
                    }
                    break;
                case 8:
                    if (data != null) {
                        GChatJsonParseUserList userListParse = new GChatJsonParseUserList();
                        ChatClientAsync.this.processEventOnMainThread(9, userListParse.parse(data));
                        break;
                    }
                    break;
                case 10:
                    if (data != null) {
                        GChatJsonParseResponseState responseStateParse = new GChatJsonParseResponseState();
                        ChatClientAsync.this.processEventOnMainThread(11, responseStateParse.parse(data));
                        break;
                    }
                    break;
                case 11:
                    if (data != null) {
                        GChatJsonParseNewMessage newMessageParse = new GChatJsonParseNewMessage();
                        ChatClientAsync.this.processEventOnMainThread(12, newMessageParse.parse(data));
                        break;
                    }
                    break;
                case 14:
                    if (data != null) {
                        GChatJsonParseResponseState responseStateParse2 = new GChatJsonParseResponseState();
                        ChatClientAsync.this.processEventOnMainThread(13, responseStateParse2.parse(data));
                        break;
                    }
                    break;
                case 16:
                    if (data != null) {
                        GChatJsonParseBlackList blackListParse = new GChatJsonParseBlackList();
                        ChatClientAsync.this.processEventOnMainThread(10, blackListParse.parse(data));
                        break;
                    }
                    break;
                case 18:
                    if (data != null) {
                        GChatJsonParseResponseState responseStateParse3 = new GChatJsonParseResponseState();
                        ChatClientAsync.this.processEventOnMainThread(16, responseStateParse3.parse(data));
                        break;
                    }
                    break;
                case 19:
                    if (data != null) {
                        GChatJsonParseUserChange userChangeParse = new GChatJsonParseUserChange();
                        ChatClientAsync.this.processEventOnMainThread(14, userChangeParse.parse(data));
                        break;
                    }
                    break;
                case 21:
                    if (data != null) {
                        GChatJsonParseRepeatLogin repeatLoginParse = new GChatJsonParseRepeatLogin();
                        ChatClientAsync.this.processEventOnMainThread(15, repeatLoginParse.parse(data));
                        break;
                    }
                    break;
            }
        }

        @Override // org.apache.mina.core.service.IoHandlerAdapter, org.apache.mina.core.service.IoHandler
        public void messageSent(IoSession session, Object message) throws Exception {
            ChatClientAsync.this.printPackage(message, false);
        }
    };

    public static ChatClientAsync getInstance() {
        if (mInstance == null) {
            mInstance = new ChatClientAsync();
        }
        return mInstance;
    }

    public void setup() {
        if (GMScreenGlobalInfo.isChatSupport()) {
            exit();
            startConnectBalanceServer();
        }
    }

    public void exit() {
        if (GMScreenGlobalInfo.isChatSupport()) {
            stopKeepAliveTask();
            IoSession session = this.mBalanceServerConnector == null ? null : this.mBalanceServerConnector.getmSession();
            if (session != null && session.isConnected()) {
                CloseFuture future = session.getCloseFuture();
                future.awaitUninterruptibly(1000L);
                this.mBalanceServerConnector.getmConnector().dispose();
            }
            IoSession session2 = this.mMessageServerConnector == null ? null : this.mMessageServerConnector.getmSession();
            if (session2 != null && session2.isConnected()) {
                CloseFuture future2 = session2.getCloseFuture();
                future2.awaitUninterruptibly(1000L);
                this.mMessageServerConnector.getmConnector().dispose();
            }
        }
    }

    private void startConnectBalanceServer() {
        Message msg = this.mWorkerThreadHandler.obtainMessage(0);
        WorkerArgs args = new WorkerArgs(null);
        args.handler = this;
        msg.obj = args;
        this.mWorkerThreadHandler.sendMessage(msg);
    }

    private void startConnectMessageServer(InetSocketAddress socketAddress) {
        Message msg = this.mWorkerThreadHandler.obtainMessage(1);
        WorkerArgs args = new WorkerArgs(null);
        args.handler = this;
        args.arg1 = socketAddress;
        msg.obj = args;
        this.mWorkerThreadHandler.sendMessage(msg);
    }

    public boolean isHeartbeatTimeout() {
        if (this.mLastHeartbeatSendTime == -1) {
            return false;
        }
        return this.mLastHeartbeatReceiveTime == -1 || this.mLastHeartbeatSendTime > this.mLastHeartbeatReceiveTime;
    }

    private void startKeepAliveTask() {
        if (this.mKeepAliveTask == null) {
            Timer timer = new Timer();
            this.mLastHeartbeatSendTime = -1L;
            this.mLastHeartbeatReceiveTime = -1L;
            this.mKeepAliveTask = new TimerTask() { // from class: mktvsmart.screen.gchat.client.ChatClientAsync.2
                AnonymousClass2() {
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (!ChatClientAsync.this.isHeartbeatTimeout()) {
                        ChatClientAsync.this.mLastHeartbeatSendTime = System.currentTimeMillis();
                        GChatJsonSerializeCommand commandSerialize = new GChatJsonSerializeCommand((short) 0);
                        ChatClientAsync.this.send(ChatClientAsync.this.mMessageServerConnector, commandSerialize);
                        return;
                    }
                    ChatClientAsync.this.processEventOnMainThread(5, null);
                }
            };
            timer.schedule(this.mKeepAliveTask, 10000L, TimeUnit.SECONDS.toMillis(30L));
        }
    }

    /* renamed from: mktvsmart.screen.gchat.client.ChatClientAsync$2 */
    class AnonymousClass2 extends TimerTask {
        AnonymousClass2() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            if (!ChatClientAsync.this.isHeartbeatTimeout()) {
                ChatClientAsync.this.mLastHeartbeatSendTime = System.currentTimeMillis();
                GChatJsonSerializeCommand commandSerialize = new GChatJsonSerializeCommand((short) 0);
                ChatClientAsync.this.send(ChatClientAsync.this.mMessageServerConnector, commandSerialize);
                return;
            }
            ChatClientAsync.this.processEventOnMainThread(5, null);
        }
    }

    private void stopKeepAliveTask() {
        if (this.mKeepAliveTask != null) {
            this.mKeepAliveTask.cancel();
            this.mKeepAliveTask = null;
        }
    }

    public void processEventOnMainThread(int what, Object result) {
        Message reply = obtainMessage(what);
        WorkerArgs args = new WorkerArgs(null);
        args.result = result;
        reply.obj = args;
        reply.sendToTarget();
    }

    public void send(ChatConnector chatConnector, JsonSerialize message) {
        if (GMScreenGlobalInfo.isChatSupport()) {
            IoSession session = chatConnector == null ? null : chatConnector.getmSession();
            if (session != null && session.isConnected()) {
                session.write(message.serialize());
            }
        }
    }

    public void send(JsonSerialize message) {
        send(this.mMessageServerConnector, message);
    }

    /* renamed from: mktvsmart.screen.gchat.client.ChatClientAsync$1 */
    class AnonymousClass1 extends IoHandlerAdapter {
        AnonymousClass1() {
        }

        @Override // org.apache.mina.core.service.IoHandlerAdapter, org.apache.mina.core.service.IoHandler
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            cause.printStackTrace();
        }

        @Override // org.apache.mina.core.service.IoHandlerAdapter, org.apache.mina.core.service.IoHandler
        public void messageReceived(IoSession session, Object message) throws Exception {
            ChatClientAsync.this.printPackage(message, true);
            TransmissionPackage msgPackage = (TransmissionPackage) message;
            byte[] data = msgPackage.getBody();
            switch (msgPackage.getType()) {
                case 0:
                    ChatClientAsync.this.processEventOnMainThread(4, null);
                    break;
                case 2:
                    if (data != null) {
                        GChatJsonParseServerInfo serverInfoParse = new GChatJsonParseServerInfo();
                        ChatClientAsync.this.processEventOnMainThread(6, serverInfoParse.parse(data));
                        break;
                    }
                    break;
                case 4:
                    if (data != null) {
                        GChatJsonParseLoginInfo loginInfoParse = new GChatJsonParseLoginInfo();
                        ChatClientAsync.this.processEventOnMainThread(7, loginInfoParse.parse(data));
                        break;
                    }
                    break;
                case 6:
                    if (data != null) {
                        GChatJsonParseRoomInfo RoomInfoParse = new GChatJsonParseRoomInfo();
                        ChatClientAsync.this.processEventOnMainThread(8, RoomInfoParse.parse(data));
                        break;
                    }
                    break;
                case 8:
                    if (data != null) {
                        GChatJsonParseUserList userListParse = new GChatJsonParseUserList();
                        ChatClientAsync.this.processEventOnMainThread(9, userListParse.parse(data));
                        break;
                    }
                    break;
                case 10:
                    if (data != null) {
                        GChatJsonParseResponseState responseStateParse = new GChatJsonParseResponseState();
                        ChatClientAsync.this.processEventOnMainThread(11, responseStateParse.parse(data));
                        break;
                    }
                    break;
                case 11:
                    if (data != null) {
                        GChatJsonParseNewMessage newMessageParse = new GChatJsonParseNewMessage();
                        ChatClientAsync.this.processEventOnMainThread(12, newMessageParse.parse(data));
                        break;
                    }
                    break;
                case 14:
                    if (data != null) {
                        GChatJsonParseResponseState responseStateParse2 = new GChatJsonParseResponseState();
                        ChatClientAsync.this.processEventOnMainThread(13, responseStateParse2.parse(data));
                        break;
                    }
                    break;
                case 16:
                    if (data != null) {
                        GChatJsonParseBlackList blackListParse = new GChatJsonParseBlackList();
                        ChatClientAsync.this.processEventOnMainThread(10, blackListParse.parse(data));
                        break;
                    }
                    break;
                case 18:
                    if (data != null) {
                        GChatJsonParseResponseState responseStateParse3 = new GChatJsonParseResponseState();
                        ChatClientAsync.this.processEventOnMainThread(16, responseStateParse3.parse(data));
                        break;
                    }
                    break;
                case 19:
                    if (data != null) {
                        GChatJsonParseUserChange userChangeParse = new GChatJsonParseUserChange();
                        ChatClientAsync.this.processEventOnMainThread(14, userChangeParse.parse(data));
                        break;
                    }
                    break;
                case 21:
                    if (data != null) {
                        GChatJsonParseRepeatLogin repeatLoginParse = new GChatJsonParseRepeatLogin();
                        ChatClientAsync.this.processEventOnMainThread(15, repeatLoginParse.parse(data));
                        break;
                    }
                    break;
            }
        }

        @Override // org.apache.mina.core.service.IoHandlerAdapter, org.apache.mina.core.service.IoHandler
        public void messageSent(IoSession session, Object message) throws Exception {
            ChatClientAsync.this.printPackage(message, false);
        }
    }

    private class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int what = msg.what;
            WorkerArgs args = (WorkerArgs) msg.obj;
            switch (what) {
                case 0:
                    SocketAddress socketAddress = new InetSocketAddress(ChatClientAsync.BALANCE_SERVER_HOST, ChatClientAsync.BALANCE_SERVER_PORT);
                    ChatClientAsync.this.mBalanceServerConnector = new ChatConnector((InetSocketAddress) socketAddress, ChatClientAsync.this.mGChatHandler);
                    args.result = Boolean.valueOf(ChatClientAsync.this.mBalanceServerConnector.connect());
                    break;
                case 1:
                    ChatClientAsync.this.mMessageServerConnector = new ChatConnector((InetSocketAddress) args.arg1, ChatClientAsync.this.mGChatHandler);
                    args.result = Boolean.valueOf(ChatClientAsync.this.mMessageServerConnector.connect());
                    break;
            }
            Message reply = args.handler.obtainMessage(what);
            reply.obj = args;
            reply.sendToTarget();
        }
    }

    private void dispatchListenerEvent(Message msg) {
        int what = msg.what;
        WorkerArgs args = (WorkerArgs) msg.obj;
        Iterator<WeakReference<GChatClientListener>> it = this.mChatClientListeners.iterator();
        while (it.hasNext()) {
            WeakReference<GChatClientListener> weakReference = it.next();
            GChatClientListener listener = weakReference == null ? null : weakReference.get();
            if (listener != null) {
                switch (what) {
                    case 2:
                        listener.onConnectServerSuccess(this.mMessageServerConnector);
                        continue;
                    case 3:
                        listener.onConnectServerFailure();
                        continue;
                    case 5:
                        listener.heartbeatTimeoutListener();
                        break;
                    case 8:
                        listener.onReceivedRoomInfo(this.mMessageServerConnector, ((Integer) args.result).intValue());
                        continue;
                    case 9:
                        listener.onReceivedUserList(this.mMessageServerConnector, (List) args.result);
                        continue;
                    case 10:
                        listener.onReceivedBlacklist(this.mMessageServerConnector, (List) args.result);
                        continue;
                    case 11:
                        listener.onReceivedMsgSendResult(this.mMessageServerConnector, ((Integer) args.result).intValue());
                        continue;
                    case 12:
                        listener.onReceivedNewMessageEvent(this.mMessageServerConnector, (DataConvertChatMsgModel) args.result);
                        continue;
                    case 13:
                        listener.onReceivedBlacklistSetResult(this.mMessageServerConnector, ((Integer) args.result).intValue());
                        continue;
                    case 14:
                        listener.onReceivedUserChangeEvent(this.mMessageServerConnector, (GsChatUser) args.result);
                        continue;
                    case 15:
                        listener.onReceivedRepeatLoginEvent(((Integer) args.result).intValue());
                        continue;
                    case 16:
                        listener.onReceivedSetUsernameResult(this.mMessageServerConnector, ((Integer) args.result).intValue());
                        continue;
                }
                listener.onReceivedLoginResult(this.mMessageServerConnector, (GChatLoginInfo) args.result);
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        int what = msg.what;
        WorkerArgs args = (WorkerArgs) msg.obj;
        switch (what) {
            case 0:
                if (((Boolean) args.result).booleanValue()) {
                    GChatJsonSerializeCommand commandSerialize = new GChatJsonSerializeCommand((short) 1);
                    send(this.mBalanceServerConnector, commandSerialize);
                    break;
                } else {
                    processEventOnMainThread(3, null);
                    break;
                }
            case 1:
                if (((Boolean) args.result).booleanValue()) {
                    processEventOnMainThread(2, null);
                    startKeepAliveTask();
                    break;
                } else {
                    processEventOnMainThread(3, null);
                    break;
                }
            case 2:
            case 3:
            case 5:
            default:
                dispatchListenerEvent(msg);
                break;
            case 4:
                this.mLastHeartbeatReceiveTime = System.currentTimeMillis();
                break;
            case 6:
                startConnectMessageServer((InetSocketAddress) args.result);
                break;
        }
    }

    private ChatClientAsync() {
        synchronized (AsyncQueryHandler.class) {
            if (sLooper == null) {
                HandlerThread thread = new HandlerThread("ChatClient");
                thread.start();
                sLooper = thread.getLooper();
            }
        }
        this.mWorkerThreadHandler = createHandler(sLooper);
    }

    private Handler createHandler(Looper looper) {
        return new WorkerHandler(looper);
    }

    public void addListener(GChatClientListener listener) {
        WeakReference<GChatClientListener> listenerWeakReference = listener == null ? null : new WeakReference<>(listener);
        this.mChatClientListeners.add(listenerWeakReference);
    }

    public void cleanListeners() {
        this.mChatClientListeners.clear();
    }

    private static final class WorkerArgs {
        public Object arg1;
        public Object arg2;
        public Handler handler;
        public Object result;

        private WorkerArgs() {
        }

        /* synthetic */ WorkerArgs(WorkerArgs workerArgs) {
            this();
        }
    }

    public void printPackage(Object msg, boolean packageIn) {
    }
}
