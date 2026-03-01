package org.apache.mina.filter.executor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: classes.dex */
public class OrderedThreadPoolExecutor extends ThreadPoolExecutor {
    private static final int DEFAULT_INITIAL_THREAD_POOL_SIZE = 0;
    private static final int DEFAULT_KEEP_ALIVE = 30;
    private static final int DEFAULT_MAX_THREAD_POOL = 16;
    private final AttributeKey TASKS_QUEUE;
    private long completedTaskCount;
    private final IoEventQueueHandler eventQueueHandler;
    private final AtomicInteger idleWorkers;
    private volatile int largestPoolSize;
    private volatile boolean shutdown;
    private final BlockingQueue<IoSession> waitingSessions;
    private final Set<Worker> workers;
    private static Logger LOGGER = LoggerFactory.getLogger(OrderedThreadPoolExecutor.class);
    private static final IoSession EXIT_SIGNAL = new DummySession();

    static /* synthetic */ long access$914(OrderedThreadPoolExecutor x0, long x1) {
        long j = x0.completedTaskCount + x1;
        x0.completedTaskCount = j;
        return j;
    }

    public OrderedThreadPoolExecutor() {
        this(0, 16, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
    }

    public OrderedThreadPoolExecutor(int maximumPoolSize) {
        this(0, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), null);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler eventQueueHandler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), eventQueueHandler);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler eventQueueHandler) {
        super(0, 1, keepAliveTime, unit, new SynchronousQueue(), threadFactory, new ThreadPoolExecutor.AbortPolicy());
        this.TASKS_QUEUE = new AttributeKey(getClass(), "tasksQueue");
        this.waitingSessions = new LinkedBlockingQueue();
        this.workers = new HashSet();
        this.idleWorkers = new AtomicInteger();
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (maximumPoolSize == 0 || maximumPoolSize < corePoolSize) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }
        super.setCorePoolSize(corePoolSize);
        super.setMaximumPoolSize(maximumPoolSize);
        if (eventQueueHandler == null) {
            this.eventQueueHandler = IoEventQueueHandler.NOOP;
        } else {
            this.eventQueueHandler = eventQueueHandler;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SessionTasksQueue getSessionTasksQueue(IoSession session) {
        SessionTasksQueue queue = (SessionTasksQueue) session.getAttribute(this.TASKS_QUEUE);
        if (queue == null) {
            SessionTasksQueue queue2 = new SessionTasksQueue();
            SessionTasksQueue oldQueue = (SessionTasksQueue) session.setAttributeIfAbsent(this.TASKS_QUEUE, queue2);
            if (oldQueue != null) {
                return oldQueue;
            }
            return queue2;
        }
        return queue;
    }

    public IoEventQueueHandler getQueueHandler() {
        return this.eventQueueHandler;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
    }

    private void addWorker() {
        synchronized (this.workers) {
            if (this.workers.size() < super.getMaximumPoolSize()) {
                Worker worker = new Worker();
                Thread thread = getThreadFactory().newThread(worker);
                this.idleWorkers.incrementAndGet();
                thread.start();
                this.workers.add(worker);
                if (this.workers.size() > this.largestPoolSize) {
                    this.largestPoolSize = this.workers.size();
                }
            }
        }
    }

    private void addWorkerIfNecessary() {
        if (this.idleWorkers.get() == 0) {
            synchronized (this.workers) {
                if (this.workers.isEmpty() || this.idleWorkers.get() == 0) {
                    addWorker();
                }
            }
        }
    }

    private void removeWorker() {
        synchronized (this.workers) {
            if (this.workers.size() > super.getCorePoolSize()) {
                this.waitingSessions.offer(EXIT_SIGNAL);
            }
        }
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public int getMaximumPoolSize() {
        return super.getMaximumPoolSize();
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < super.getCorePoolSize()) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }
        synchronized (this.workers) {
            super.setMaximumPoolSize(maximumPoolSize);
            for (int difference = this.workers.size() - maximumPoolSize; difference > 0; difference--) {
                removeWorker();
            }
        }
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.ExecutorService
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        synchronized (this.workers) {
            while (!isTerminated()) {
                long waitTime = deadline - System.currentTimeMillis();
                if (waitTime <= 0) {
                    break;
                }
                this.workers.wait(waitTime);
            }
        }
        return isTerminated();
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.ExecutorService
    public boolean isShutdown() {
        return this.shutdown;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.ExecutorService
    public boolean isTerminated() {
        boolean zIsEmpty;
        if (!this.shutdown) {
            return false;
        }
        synchronized (this.workers) {
            zIsEmpty = this.workers.isEmpty();
        }
        return zIsEmpty;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.ExecutorService
    public void shutdown() {
        if (!this.shutdown) {
            this.shutdown = true;
            synchronized (this.workers) {
                for (int i = this.workers.size(); i > 0; i--) {
                    this.waitingSessions.offer(EXIT_SIGNAL);
                }
            }
        }
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.ExecutorService
    public List<Runnable> shutdownNow() {
        shutdown();
        List<Runnable> answer = new ArrayList<>();
        while (true) {
            IoSession session = this.waitingSessions.poll();
            if (session != null) {
                if (session == EXIT_SIGNAL) {
                    this.waitingSessions.offer(EXIT_SIGNAL);
                    Thread.yield();
                } else {
                    SessionTasksQueue sessionTasksQueue = (SessionTasksQueue) session.getAttribute(this.TASKS_QUEUE);
                    synchronized (sessionTasksQueue.tasksQueue) {
                        for (Runnable task : sessionTasksQueue.tasksQueue) {
                            getQueueHandler().polled(this, (IoEvent) task);
                            answer.add(task);
                        }
                        sessionTasksQueue.tasksQueue.clear();
                    }
                }
            } else {
                return answer;
            }
        }
    }

    private void print(Queue<Runnable> queue, IoEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Adding event ").append(event.getType()).append(" to session ").append(event.getSession().getId());
        boolean first = true;
        sb.append("\nQueue : [");
        for (Runnable elem : queue) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(((IoEvent) elem).getType()).append(", ");
        }
        sb.append("]\n");
        LOGGER.debug(sb.toString());
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        boolean offerSession;
        if (this.shutdown) {
            rejectTask(task);
        }
        checkTaskType(task);
        IoEvent event = (IoEvent) task;
        IoSession session = event.getSession();
        SessionTasksQueue sessionTasksQueue = getSessionTasksQueue(session);
        Queue<Runnable> tasksQueue = sessionTasksQueue.tasksQueue;
        boolean offerEvent = this.eventQueueHandler.accept(this, event);
        if (offerEvent) {
            synchronized (tasksQueue) {
                tasksQueue.offer(event);
                if (!sessionTasksQueue.processingCompleted) {
                    offerSession = false;
                } else {
                    sessionTasksQueue.processingCompleted = false;
                    offerSession = true;
                }
                if (LOGGER.isDebugEnabled()) {
                    print(tasksQueue, event);
                }
            }
        } else {
            offerSession = false;
        }
        if (offerSession) {
            this.waitingSessions.offer(session);
        }
        addWorkerIfNecessary();
        if (offerEvent) {
            this.eventQueueHandler.offered(this, event);
        }
    }

    private void rejectTask(Runnable task) {
        getRejectedExecutionHandler().rejectedExecution(task, this);
    }

    private void checkTaskType(Runnable task) {
        if (!(task instanceof IoEvent)) {
            throw new IllegalArgumentException("task must be an IoEvent or its subclass.");
        }
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public int getActiveCount() {
        int size;
        synchronized (this.workers) {
            size = this.workers.size() - this.idleWorkers.get();
        }
        return size;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public long getCompletedTaskCount() {
        long answer;
        synchronized (this.workers) {
            answer = this.completedTaskCount;
            for (Worker w : this.workers) {
                answer += w.completedTaskCount;
            }
        }
        return answer;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public int getLargestPoolSize() {
        return this.largestPoolSize;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public int getPoolSize() {
        int size;
        synchronized (this.workers) {
            size = this.workers.size();
        }
        return size;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public long getTaskCount() {
        return getCompletedTaskCount();
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public boolean isTerminating() {
        boolean z;
        synchronized (this.workers) {
            z = isShutdown() && !isTerminated();
        }
        return z;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public int prestartAllCoreThreads() {
        int answer = 0;
        synchronized (this.workers) {
            for (int i = super.getCorePoolSize() - this.workers.size(); i > 0; i--) {
                addWorker();
                answer++;
            }
        }
        return answer;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public boolean prestartCoreThread() {
        boolean z;
        synchronized (this.workers) {
            if (this.workers.size() < super.getCorePoolSize()) {
                addWorker();
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public BlockingQueue<Runnable> getQueue() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public void purge() {
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public boolean remove(Runnable task) {
        boolean removed;
        checkTaskType(task);
        IoEvent event = (IoEvent) task;
        IoSession session = event.getSession();
        SessionTasksQueue sessionTasksQueue = (SessionTasksQueue) session.getAttribute(this.TASKS_QUEUE);
        Queue<Runnable> tasksQueue = sessionTasksQueue.tasksQueue;
        if (sessionTasksQueue == null) {
            return false;
        }
        synchronized (tasksQueue) {
            removed = tasksQueue.remove(task);
        }
        if (removed) {
            getQueueHandler().polled(this, event);
            return removed;
        }
        return removed;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public int getCorePoolSize() {
        return super.getCorePoolSize();
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (corePoolSize > super.getMaximumPoolSize()) {
            throw new IllegalArgumentException("corePoolSize exceeds maximumPoolSize");
        }
        synchronized (this.workers) {
            if (super.getCorePoolSize() > corePoolSize) {
                for (int i = super.getCorePoolSize() - corePoolSize; i > 0; i--) {
                    removeWorker();
                }
            }
            super.setCorePoolSize(corePoolSize);
        }
    }

    private class Worker implements Runnable {
        private volatile long completedTaskCount;
        private Thread thread;

        private Worker() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:9:0x002e, code lost:
        
            r6.this$0.workers.remove(r6);
         */
        /* JADX WARN: Removed duplicated region for block: B:19:0x0061  */
        /* JADX WARN: Removed duplicated region for block: B:51:0x0038 A[EDGE_INSN: B:51:0x0038->B:11:0x0038 BREAK  A[LOOP:0: B:41:0x0006->B:21:0x006c], SYNTHETIC] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                r6 = this;
                java.lang.Thread r1 = java.lang.Thread.currentThread()
                r6.thread = r1
            L6:
                org.apache.mina.core.session.IoSession r0 = r6.fetchSession()     // Catch: java.lang.Throwable -> L76
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L76
                java.util.concurrent.atomic.AtomicInteger r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$500(r1)     // Catch: java.lang.Throwable -> L76
                r1.decrementAndGet()     // Catch: java.lang.Throwable -> L76
                if (r0 != 0) goto L5b
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L76
                java.util.Set r2 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$600(r1)     // Catch: java.lang.Throwable -> L76
                monitor-enter(r2)     // Catch: java.lang.Throwable -> L76
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L99
                java.util.Set r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$600(r1)     // Catch: java.lang.Throwable -> L99
                int r1 = r1.size()     // Catch: java.lang.Throwable -> L99
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L99
                int r3 = r3.getCorePoolSize()     // Catch: java.lang.Throwable -> L99
                if (r1 <= r3) goto L5a
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L99
                java.util.Set r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$600(r1)     // Catch: java.lang.Throwable -> L99
                r1.remove(r6)     // Catch: java.lang.Throwable -> L99
                monitor-exit(r2)     // Catch: java.lang.Throwable -> L99
            L38:
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this
                java.util.Set r2 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$600(r1)
                monitor-enter(r2)
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> La7
                java.util.Set r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$600(r1)     // Catch: java.lang.Throwable -> La7
                r1.remove(r6)     // Catch: java.lang.Throwable -> La7
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> La7
                long r4 = r6.completedTaskCount     // Catch: java.lang.Throwable -> La7
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$914(r1, r4)     // Catch: java.lang.Throwable -> La7
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> La7
                java.util.Set r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$600(r1)     // Catch: java.lang.Throwable -> La7
                r1.notifyAll()     // Catch: java.lang.Throwable -> La7
                monitor-exit(r2)     // Catch: java.lang.Throwable -> La7
                return
            L5a:
                monitor-exit(r2)     // Catch: java.lang.Throwable -> L99
            L5b:
                org.apache.mina.core.session.IoSession r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$700()     // Catch: java.lang.Throwable -> L76
                if (r0 == r1) goto L38
                if (r0 == 0) goto L6c
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L9c
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor$SessionTasksQueue r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$800(r1, r0)     // Catch: java.lang.Throwable -> L9c
                r6.runTasks(r1)     // Catch: java.lang.Throwable -> L9c
            L6c:
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L76
                java.util.concurrent.atomic.AtomicInteger r1 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$500(r1)     // Catch: java.lang.Throwable -> L76
                r1.incrementAndGet()     // Catch: java.lang.Throwable -> L76
                goto L6
            L76:
                r1 = move-exception
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r2 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this
                java.util.Set r2 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$600(r2)
                monitor-enter(r2)
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> Laa
                java.util.Set r3 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$600(r3)     // Catch: java.lang.Throwable -> Laa
                r3.remove(r6)     // Catch: java.lang.Throwable -> Laa
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> Laa
                long r4 = r6.completedTaskCount     // Catch: java.lang.Throwable -> Laa
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$914(r3, r4)     // Catch: java.lang.Throwable -> Laa
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> Laa
                java.util.Set r3 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$600(r3)     // Catch: java.lang.Throwable -> Laa
                r3.notifyAll()     // Catch: java.lang.Throwable -> Laa
                monitor-exit(r2)     // Catch: java.lang.Throwable -> Laa
                throw r1
            L99:
                r1 = move-exception
                monitor-exit(r2)     // Catch: java.lang.Throwable -> L99
                throw r1     // Catch: java.lang.Throwable -> L76
            L9c:
                r1 = move-exception
                org.apache.mina.filter.executor.OrderedThreadPoolExecutor r2 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L76
                java.util.concurrent.atomic.AtomicInteger r2 = org.apache.mina.filter.executor.OrderedThreadPoolExecutor.access$500(r2)     // Catch: java.lang.Throwable -> L76
                r2.incrementAndGet()     // Catch: java.lang.Throwable -> L76
                throw r1     // Catch: java.lang.Throwable -> L76
            La7:
                r1 = move-exception
                monitor-exit(r2)     // Catch: java.lang.Throwable -> La7
                throw r1
            Laa:
                r1 = move-exception
                monitor-exit(r2)     // Catch: java.lang.Throwable -> Laa
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.mina.filter.executor.OrderedThreadPoolExecutor.Worker.run():void");
        }

        private IoSession fetchSession() {
            IoSession session = null;
            long currentTime = System.currentTimeMillis();
            long deadline = currentTime + OrderedThreadPoolExecutor.this.getKeepAliveTime(TimeUnit.MILLISECONDS);
            while (true) {
                long waitTime = deadline - currentTime;
                try {
                    if (waitTime <= 0) {
                        break;
                    }
                    try {
                        session = (IoSession) OrderedThreadPoolExecutor.this.waitingSessions.poll(waitTime, TimeUnit.MILLISECONDS);
                        if (session == null) {
                            System.currentTimeMillis();
                        }
                    } catch (Throwable th) {
                        if (session == null) {
                            System.currentTimeMillis();
                        }
                        throw th;
                    }
                } catch (InterruptedException e) {
                }
            }
            return session;
        }

        private void runTasks(SessionTasksQueue sessionTasksQueue) {
            Runnable task;
            while (true) {
                Queue<Runnable> tasksQueue = sessionTasksQueue.tasksQueue;
                synchronized (tasksQueue) {
                    task = tasksQueue.poll();
                    if (task == null) {
                        sessionTasksQueue.processingCompleted = true;
                        return;
                    }
                }
                OrderedThreadPoolExecutor.this.eventQueueHandler.polled(OrderedThreadPoolExecutor.this, (IoEvent) task);
                runTask(task);
            }
        }

        private void runTask(Runnable task) {
            OrderedThreadPoolExecutor.this.beforeExecute(this.thread, task);
            boolean ran = false;
            try {
                task.run();
                ran = true;
                OrderedThreadPoolExecutor.this.afterExecute(task, null);
                this.completedTaskCount++;
            } catch (RuntimeException e) {
                if (!ran) {
                    OrderedThreadPoolExecutor.this.afterExecute(task, e);
                }
                throw e;
            }
        }
    }

    private class SessionTasksQueue {
        private boolean processingCompleted;
        private final Queue<Runnable> tasksQueue;

        private SessionTasksQueue() {
            this.tasksQueue = new ConcurrentLinkedQueue();
            this.processingCompleted = true;
        }
    }
}
