package com.sun.activation.registries;

import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public class LogSupport {
    private static boolean debug;
    private static final Level level = Level.FINE;
    private static Logger logger;

    static {
        debug = false;
        try {
            debug = Boolean.getBoolean("javax.activation.debug");
        } catch (Throwable th) {
        }
        logger = Logger.getLogger("javax.activation");
    }

    private LogSupport() {
    }

    public static void log(String msg) {
        if (debug) {
            System.out.println(msg);
        }
        logger.log(level, msg);
    }

    public static void log(String msg, Throwable t) {
        if (debug) {
            System.out.println(String.valueOf(msg) + "; Exception: " + t);
        }
        logger.log(level, msg, t);
    }

    public static boolean isLoggable() {
        return debug || logger.isLoggable(level);
    }
}
