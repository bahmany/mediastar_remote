package org.cybergarage.util;

/* loaded from: classes.dex */
public final class TimerUtil {
    public static final void wait(int waitTime) throws InterruptedException {
        try {
            Thread.sleep(waitTime);
        } catch (Exception e) {
        }
    }

    public static final void waitRandom(int time) throws InterruptedException {
        int waitTime = (int) (Math.random() * time);
        try {
            Thread.sleep(waitTime);
        } catch (Exception e) {
        }
    }
}
