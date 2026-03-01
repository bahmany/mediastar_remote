package org.teleal.cling.registry;

import java.util.logging.Logger;

/* loaded from: classes.dex */
public class RegistryMaintainer implements Runnable {
    private static Logger log = Logger.getLogger(RegistryMaintainer.class.getName());
    private final RegistryImpl registry;
    private final int sleepIntervalMillis;
    private volatile boolean stopped = false;

    public RegistryMaintainer(RegistryImpl registry, int sleepIntervalMillis) {
        this.registry = registry;
        this.sleepIntervalMillis = sleepIntervalMillis;
    }

    public void stop() {
        log.fine("Setting stopped status on thread");
        this.stopped = true;
    }

    @Override // java.lang.Runnable
    public void run() throws InterruptedException {
        this.stopped = false;
        log.fine("Running registry maintenance loop every milliseconds: " + this.sleepIntervalMillis);
        while (!this.stopped) {
            try {
                this.registry.maintain();
                Thread.sleep(this.sleepIntervalMillis);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        log.fine("Stopped status on thread received, ending maintenance loop");
    }
}
