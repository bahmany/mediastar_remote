package com.google.android.gms.analytics;

/* loaded from: classes.dex */
public class z {
    private static GoogleAnalytics AT;

    public static void T(String str) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.error(str);
        }
    }

    public static void U(String str) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.info(str);
        }
    }

    public static void V(String str) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.verbose(str);
        }
    }

    public static void W(String str) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.warn(str);
        }
    }

    public static boolean eL() {
        return getLogger() != null && getLogger().getLogLevel() == 0;
    }

    private static Logger getLogger() {
        if (AT == null) {
            AT = GoogleAnalytics.eE();
        }
        if (AT != null) {
            return AT.getLogger();
        }
        return null;
    }
}
