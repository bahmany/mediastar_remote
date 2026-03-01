package org.apache.mina.filter.executor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.mina.core.session.IoEvent;

/* loaded from: classes.dex */
public class UnorderedThreadPoolExecutor extends ThreadPoolExecutor {
    private static final Runnable EXIT_SIGNAL = new Runnable() { // from class: org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.1
        @Override // java.lang.Runnable
        public void run() {
            throw new Error("This method shouldn't be called. Please file a bug report.");
        }
    };
    private long completedTaskCount;
    private volatile int corePoolSize;
    private final AtomicInteger idleWorkers;
    private volatile int largestPoolSize;
    private volatile int maximumPoolSize;
    private final IoEventQueueHandler queueHandler;
    private volatile boolean shutdown;
    private final Set<Worker> workers;

    static /* synthetic */ long access$714(UnorderedThreadPoolExecutor x0, long x1) {
        long j = x0.completedTaskCount + x1;
        x0.completedTaskCount = j;
        return j;
    }

    public UnorderedThreadPoolExecutor() {
        this(16);
    }

    public UnorderedThreadPoolExecutor(int maximumPoolSize) {
        this(0, maximumPoolSize);
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS);
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory());
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler queueHandler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), queueHandler);
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler) {
        super(0, 1, keepAliveTime, unit, new LinkedBlockingQueue(), threadFactory, new ThreadPoolExecutor.AbortPolicy());
        this.workers = new HashSet();
        this.idleWorkers = new AtomicInteger();
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (maximumPoolSize == 0 || maximumPoolSize < corePoolSize) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }
        queueHandler = queueHandler == null ? IoEventQueueHandler.NOOP : queueHandler;
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.queueHandler = queueHandler;
    }

    public IoEventQueueHandler getQueueHandler() {
        return this.queueHandler;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
    }

    private void addWorker() {
        synchronized (this.workers) {
            if (this.workers.size() < this.maximumPoolSize) {
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
            if (this.workers.size() > this.corePoolSize) {
                getQueue().offer(EXIT_SIGNAL);
            }
        }
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < this.corePoolSize) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }
        synchronized (this.workers) {
            this.maximumPoolSize = maximumPoolSize;
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
                    getQueue().offer(EXIT_SIGNAL);
                }
            }
        }
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.ExecutorService
    public List<Runnable> shutdownNow() {
        shutdown();
        List<Runnable> answer = new ArrayList<>();
        while (true) {
            Runnable task = getQueue().poll();
            if (task != null) {
                if (task == EXIT_SIGNAL) {
                    getQueue().offer(EXIT_SIGNAL);
                    Thread.yield();
                } else {
                    getQueueHandler().polled(this, (IoEvent) task);
                    answer.add(task);
                }
            } else {
                return answer;
            }
        }
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        if (this.shutdown) {
            rejectTask(task);
        }
        checkTaskType(task);
        IoEvent e = (IoEvent) task;
        boolean offeredEvent = this.queueHandler.accept(this, e);
        if (offeredEvent) {
            getQueue().offer(e);
        }
        addWorkerIfNecessary();
        if (offeredEvent) {
            this.queueHandler.offered(this, e);
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
            for (int i = this.corePoolSize - this.workers.size(); i > 0; i--) {
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
            if (this.workers.size() < this.corePoolSize) {
                addWorker();
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public void purge() {
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public boolean remove(Runnable task) {
        boolean removed = super.remove(task);
        if (removed) {
            getQueueHandler().polled(this, (IoEvent) task);
        }
        return removed;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public int getCorePoolSize() {
        return this.corePoolSize;
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (corePoolSize > this.maximumPoolSize) {
            throw new IllegalArgumentException("corePoolSize exceeds maximumPoolSize");
        }
        synchronized (this.workers) {
            if (this.corePoolSize > corePoolSize) {
                for (int i = this.corePoolSize - corePoolSize; i > 0; i--) {
                    removeWorker();
                }
            }
            this.corePoolSize = corePoolSize;
        }
    }

    private class Worker implements Runnable {
        private volatile long completedTaskCount;
        private Thread thread;

        private Worker() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:9:0x002e, code lost:
        
            r8.this$0.workers.remove(r8);
         */
        /* JADX WARN: Removed duplicated region for block: B:19:0x0061  */
        /* JADX WARN: Removed duplicated region for block: B:51:0x0038 A[EDGE_INSN: B:51:0x0038->B:11:0x0038 BREAK  A[LOOP:0: B:49:0x0006->B:21:0x0075], SYNTHETIC] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                r8 = this;
                java.lang.Thread r3 = java.lang.Thread.currentThread()
                r8.thread = r3
            L6:
                java.lang.Runnable r2 = r8.fetchTask()     // Catch: java.lang.Throwable -> L7f
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L7f
                java.util.concurrent.atomic.AtomicInteger r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$200(r3)     // Catch: java.lang.Throwable -> L7f
                r3.decrementAndGet()     // Catch: java.lang.Throwable -> L7f
                if (r2 != 0) goto L5b
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L7f
                java.util.Set r4 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$300(r3)     // Catch: java.lang.Throwable -> L7f
                monitor-enter(r4)     // Catch: java.lang.Throwable -> L7f
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> La2
                java.util.Set r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$300(r3)     // Catch: java.lang.Throwable -> La2
                int r3 = r3.size()     // Catch: java.lang.Throwable -> La2
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r5 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> La2
                int r5 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$400(r5)     // Catch: java.lang.Throwable -> La2
                if (r3 <= r5) goto L5a
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> La2
                java.util.Set r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$300(r3)     // Catch: java.lang.Throwable -> La2
                r3.remove(r8)     // Catch: java.lang.Throwable -> La2
                monitor-exit(r4)     // Catch: java.lang.Throwable -> La2
            L38:
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this
                java.util.Set r4 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$300(r3)
                monitor-enter(r4)
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> Lb0
                java.util.Set r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$300(r3)     // Catch: java.lang.Throwable -> Lb0
                r3.remove(r8)     // Catch: java.lang.Throwable -> Lb0
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> Lb0
                long r6 = r8.completedTaskCount     // Catch: java.lang.Throwable -> Lb0
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$714(r3, r6)     // Catch: java.lang.Throwable -> Lb0
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> Lb0
                java.util.Set r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$300(r3)     // Catch: java.lang.Throwable -> Lb0
                r3.notifyAll()     // Catch: java.lang.Throwable -> Lb0
                monitor-exit(r4)     // Catch: java.lang.Throwable -> Lb0
                return
            L5a:
                monitor-exit(r4)     // Catch: java.lang.Throwable -> La2
            L5b:
                java.lang.Runnable r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$500()     // Catch: java.lang.Throwable -> L7f
                if (r2 == r3) goto L38
                if (r2 == 0) goto L75
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> La5
                org.apache.mina.filter.executor.IoEventQueueHandler r4 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$600(r3)     // Catch: java.lang.Throwable -> La5
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r5 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> La5
                r0 = r2
                org.apache.mina.core.session.IoEvent r0 = (org.apache.mina.core.session.IoEvent) r0     // Catch: java.lang.Throwable -> La5
                r3 = r0
                r4.polled(r5, r3)     // Catch: java.lang.Throwable -> La5
                r8.runTask(r2)     // Catch: java.lang.Throwable -> La5
            L75:
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L7f
                java.util.concurrent.atomic.AtomicInteger r3 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$200(r3)     // Catch: java.lang.Throwable -> L7f
                r3.incrementAndGet()     // Catch: java.lang.Throwable -> L7f
                goto L6
            L7f:
                r3 = move-exception
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r4 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this
                java.util.Set r4 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$300(r4)
                monitor-enter(r4)
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r5 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> Lb3
                java.util.Set r5 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$300(r5)     // Catch: java.lang.Throwable -> Lb3
                r5.remove(r8)     // Catch: java.lang.Throwable -> Lb3
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r5 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> Lb3
                long r6 = r8.completedTaskCount     // Catch: java.lang.Throwable -> Lb3
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$714(r5, r6)     // Catch: java.lang.Throwable -> Lb3
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r5 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> Lb3
                java.util.Set r5 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$300(r5)     // Catch: java.lang.Throwable -> Lb3
                r5.notifyAll()     // Catch: java.lang.Throwable -> Lb3
                monitor-exit(r4)     // Catch: java.lang.Throwable -> Lb3
                throw r3
            La2:
                r3 = move-exception
                monitor-exit(r4)     // Catch: java.lang.Throwable -> La2
                throw r3     // Catch: java.lang.Throwable -> L7f
            La5:
                r3 = move-exception
                org.apache.mina.filter.executor.UnorderedThreadPoolExecutor r4 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.this     // Catch: java.lang.Throwable -> L7f
                java.util.concurrent.atomic.AtomicInteger r4 = org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.access$200(r4)     // Catch: java.lang.Throwable -> L7f
                r4.incrementAndGet()     // Catch: java.lang.Throwable -> L7f
                throw r3     // Catch: java.lang.Throwable -> L7f
            Lb0:
                r3 = move-exception
                monitor-exit(r4)     // Catch: java.lang.Throwable -> Lb0
                throw r3
            Lb3:
                r3 = move-exception
                monitor-exit(r4)     // Catch: java.lang.Throwable -> Lb3
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.mina.filter.executor.UnorderedThreadPoolExecutor.Worker.run():void");
        }

        private Runnable fetchTask() {
            Runnable task = null;
            long currentTime = System.currentTimeMillis();
            long deadline = currentTime + UnorderedThreadPoolExecutor.this.getKeepAliveTime(TimeUnit.MILLISECONDS);
            while (true) {
                long waitTime = deadline - currentTime;
                try {
                    if (waitTime <= 0) {
                        break;
                    }
                    try {
                        task = UnorderedThreadPoolExecutor.this.getQueue().poll(waitTime, TimeUnit.MILLISECONDS);
                        if (task == null) {
                            System.currentTimeMillis();
                        }
                    } catch (Throwable th) {
                        if (task == null) {
                            System.currentTimeMillis();
                        }
                        throw th;
                    }
                } catch (InterruptedException e) {
                }
            }
            return task;
        }

        private void runTask(Runnable task) {
            UnorderedThreadPoolExecutor.this.beforeExecute(this.thread, task);
            boolean ran = false;
            try {
                task.run();
                ran = true;
                UnorderedThreadPoolExecutor.this.afterExecute(task, null);
                this.completedTaskCount++;
            } catch (RuntimeException e) {
                if (!ran) {
                    UnorderedThreadPoolExecutor.this.afterExecute(task, e);
                }
                throw e;
            }
        }
    }
}
