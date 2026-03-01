package mktvsmart.screen.message.process;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public class MessageProcessor {
    private static final int CORE_WORKER_THREAD_NUM = 4;
    private static MessageProcessor sInstance;
    private static Handler sMessageHandler;
    private ExecutorService mWorkerThreadPool = Executors.newFixedThreadPool(4);
    private SparseArray<Map<Activity, List<Object>>> messageMappingList = new SparseArray<>();
    private HandlerThread messageReceiver = new HandlerThread("Handler Thread");
    private static final String TAG = MessageProcessor.class.getSimpleName();
    private static final Object sMessageListSync = new Object();

    public interface PerformOnBackground {
        void doInBackground(Message message);
    }

    public interface PerformOnForeground {
        void doInForeground(Message message);
    }

    private MessageProcessor() {
        this.messageReceiver.start();
    }

    public boolean postEmptyMessage(int what) {
        return postEmptyMessageDelayed(what, 0L);
    }

    public boolean postEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return postMessageDelayed(msg, delayMillis);
    }

    public boolean postEmptyMessageAtTime(int what, long uptimeMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return postMessageAtTime(msg, uptimeMillis);
    }

    public boolean postMessage(Message msg) {
        return postMessageDelayed(msg, 0L);
    }

    public boolean postMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return postMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public boolean postMessageAtTime(Message msg, long uptimeMillis) {
        return postMessageAtTime(msg, null, uptimeMillis);
    }

    public boolean postMessageAtTime(final Message msg, Object token, long uptimeMillis) {
        if (msg == null || this.messageReceiver == null) {
            return false;
        }
        if (sMessageHandler == null) {
            if (this.messageReceiver.getLooper() == null) {
                return false;
            }
            sMessageHandler = new Handler(this.messageReceiver.getLooper());
        }
        Message mMessage = Message.obtain(sMessageHandler, new Runnable() { // from class: mktvsmart.screen.message.process.MessageProcessor.1
            @Override // java.lang.Runnable
            public void run() {
                MessageProcessor.this.dispatchMessage(msg);
            }
        });
        mMessage.what = msg.what;
        mMessage.arg1 = msg.arg1;
        mMessage.arg2 = msg.arg2;
        mMessage.obj = token;
        mMessage.replyTo = msg.replyTo;
        if (msg.getData() != null) {
            mMessage.setData(new Bundle(msg.getData()));
        }
        return sMessageHandler.sendMessageAtTime(mMessage, uptimeMillis);
    }

    public boolean postMessageAtFrontOfQueue(final Message msg) {
        if (msg == null || this.messageReceiver == null) {
            return false;
        }
        if (sMessageHandler == null) {
            if (this.messageReceiver.getLooper() == null) {
                return false;
            }
            sMessageHandler = new Handler(this.messageReceiver.getLooper());
        }
        Message mMessage = Message.obtain(sMessageHandler, new Runnable() { // from class: mktvsmart.screen.message.process.MessageProcessor.2
            @Override // java.lang.Runnable
            public void run() {
                MessageProcessor.this.dispatchMessage(msg);
            }
        });
        mMessage.what = msg.what;
        mMessage.arg1 = msg.arg1;
        mMessage.arg2 = msg.arg2;
        mMessage.obj = msg.obj;
        mMessage.replyTo = msg.replyTo;
        if (msg.getData() != null) {
            mMessage.setData(new Bundle(msg.getData()));
        }
        return sMessageHandler.sendMessageAtFrontOfQueue(mMessage);
    }

    public boolean removeMessages(int what) {
        if (sMessageHandler == null || !sMessageHandler.hasMessages(what)) {
            return false;
        }
        sMessageHandler.removeMessages(what);
        return true;
    }

    public boolean removeMessages(int what, Object token) {
        if (sMessageHandler == null || !sMessageHandler.hasMessages(what, token)) {
            return false;
        }
        sMessageHandler.removeMessages(what, token);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchMessage(final Message msg) {
        synchronized (sMessageListSync) {
            if (this.messageMappingList != null) {
                if (this.messageMappingList.size() != 0) {
                    Map<Activity, List<Object>> activityPerformMap = this.messageMappingList.get(msg.what);
                    if (activityPerformMap != null) {
                        for (Activity activity : activityPerformMap.keySet()) {
                            List<Object> list = activityPerformMap.get(activity);
                            final PerformOnBackground pob = (PerformOnBackground) list.get(0);
                            final PerformOnForeground pof = (PerformOnForeground) list.get(1);
                            if (pob != null && (activity == null || (activity != null && !activity.isFinishing()))) {
                                this.mWorkerThreadPool.submit(new Runnable() { // from class: mktvsmart.screen.message.process.MessageProcessor.3
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        pob.doInBackground(msg);
                                    }
                                });
                            }
                            if (pof != null && activity != null && !activity.isFinishing()) {
                                activity.runOnUiThread(new Runnable() { // from class: mktvsmart.screen.message.process.MessageProcessor.4
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        pof.doInForeground(msg);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    public void setOnMessageProcess(int what, PerformOnBackground pob) {
        setOnMessageProcess(what, (Activity) null, pob);
    }

    public void setOnMessageProcess(int what, Activity activity, PerformOnBackground pob) {
        setOnMessageProcess(what, activity, pob, null);
    }

    public void setOnMessageProcess(int what, Activity activity, PerformOnForeground pof) {
        setOnMessageProcess(what, activity, null, pof);
    }

    public void setOnMessageProcess(int what, Activity activity, PerformOnBackground pob, PerformOnForeground pof) {
        List<Object> performList = new ArrayList<>();
        Map<Activity, List<Object>> activityPerformMap = this.messageMappingList.get(what);
        if (performList.add(pob) && performList.add(pof)) {
            if (activityPerformMap == null) {
                activityPerformMap = new HashMap<>();
            }
            activityPerformMap.put(activity, performList);
            synchronized (sMessageListSync) {
                this.messageMappingList.put(what, activityPerformMap);
            }
        }
    }

    public static synchronized MessageProcessor obtain() {
        if (sInstance == null) {
            sInstance = new MessageProcessor();
        }
        return sInstance;
    }

    private void recycleMessageMap() {
        synchronized (sMessageListSync) {
            int index = 0;
            while (index < this.messageMappingList.size()) {
                int what = this.messageMappingList.keyAt(index);
                Map<Activity, List<Object>> activityPerformMap = this.messageMappingList.get(what);
                if (activityPerformMap != null) {
                    List<Activity> removeItems = new ArrayList<>();
                    for (Activity activity : activityPerformMap.keySet()) {
                        if (activity != null && activity.isFinishing()) {
                            removeItems.add(activity);
                        }
                    }
                    if (removeItems.size() != 0) {
                        for (int i = 0; i < removeItems.size(); i++) {
                            activityPerformMap.remove(removeItems.get(i));
                        }
                    }
                    if (activityPerformMap == null || activityPerformMap.size() == 0) {
                        this.messageMappingList.remove(what);
                        index--;
                    }
                }
                index++;
            }
        }
    }

    public void recycle() {
        if (sInstance != null) {
            recycleMessageMap();
        }
    }

    public static void destroy() {
        if (sInstance != null) {
            sInstance.quitThread();
            synchronized (sMessageListSync) {
                sInstance.messageMappingList.clear();
                sInstance.messageMappingList = null;
            }
            sInstance = null;
        }
    }

    private void quitThread() {
        this.mWorkerThreadPool.shutdown();
        this.messageReceiver.quit();
        this.messageReceiver = null;
        sMessageHandler = null;
    }

    public void removeProcessCallback(Activity target) {
        synchronized (sMessageListSync) {
            int index = 0;
            while (index < this.messageMappingList.size()) {
                int what = this.messageMappingList.keyAt(index);
                Map<Activity, List<Object>> activityPerformMap = this.messageMappingList.get(what);
                if (activityPerformMap != null) {
                    List<Activity> removeItems = new ArrayList<>();
                    if (target != null) {
                        for (Activity activity : activityPerformMap.keySet()) {
                            if (activity != null && activity.equals(target)) {
                                removeItems.add(activity);
                            }
                        }
                    } else {
                        removeItems.add(target);
                    }
                    for (int i = 0; i < removeItems.size(); i++) {
                        activityPerformMap.remove(removeItems.get(i));
                    }
                    if (activityPerformMap.size() == 0) {
                        this.messageMappingList.remove(what);
                        index--;
                    }
                } else {
                    this.messageMappingList.remove(what);
                    index--;
                }
                index++;
            }
        }
    }

    public void removeProcessCallback(Activity target, int what) {
        synchronized (sMessageListSync) {
            int index = 0;
            while (index < this.messageMappingList.size()) {
                if (this.messageMappingList.keyAt(index) == what) {
                    Map<Activity, List<Object>> activityPerformMap = this.messageMappingList.get(what);
                    if (activityPerformMap != null) {
                        List<Activity> removeItems = new ArrayList<>();
                        if (target != null) {
                            for (Activity activity : activityPerformMap.keySet()) {
                                if (activity != null && activity.equals(target)) {
                                    removeItems.add(activity);
                                }
                            }
                        } else {
                            removeItems.add(target);
                        }
                        for (int i = 0; i < removeItems.size(); i++) {
                            activityPerformMap.remove(removeItems.get(i));
                        }
                        if (activityPerformMap.size() == 0) {
                            this.messageMappingList.remove(what);
                            index--;
                        }
                    } else {
                        this.messageMappingList.remove(what);
                        index--;
                    }
                }
                index++;
            }
        }
    }
}
