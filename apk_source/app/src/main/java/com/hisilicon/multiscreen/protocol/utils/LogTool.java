package com.hisilicon.multiscreen.protocol.utils;

import android.util.Log;

/* loaded from: classes.dex */
public class LogTool {
    public static final String MSG_EMPTY = "Empty Msg";
    public static final String MSG_SEPARATOR = " ---- ";
    public static final int STACK_LEVEL = 5;
    public static final String TAG_PREFIX = "[Hisilicon]";
    public static boolean mVFlag = true;
    public static boolean mDFlag = true;
    public static boolean mIFlag = true;
    public static boolean mWFlag = true;
    public static boolean mEFlag = true;

    public static void v(String pMsg) {
        if (mVFlag) {
            Log.v(getFinalTag(), getFinalMsg(pMsg));
        }
    }

    public static void d(String pMsg) {
        if (mDFlag) {
            Log.d(getFinalTag(), getFinalMsg(pMsg));
        }
    }

    public static void i(String pMsg) {
        if (mIFlag) {
            Log.i(getFinalTag(), getFinalMsg(pMsg));
        }
    }

    public static void w(String pMsg) {
        if (mEFlag) {
            Log.w(getFinalTag(), getFinalMsg(pMsg));
        }
    }

    public static void e(String pMsg) {
        if (mWFlag) {
            Log.e(getFinalTag(), getFinalMsg(pMsg));
        }
    }

    private static String getFinalMsg(String pMsg) {
        if (pMsg == null || pMsg == "") {
            pMsg = MSG_EMPTY;
        }
        return String.valueOf(getMethodName()) + " " + getLineNumber() + MSG_SEPARATOR + pMsg + MSG_SEPARATOR;
    }

    private static String getFinalTag() {
        return TAG_PREFIX + getClassName();
    }

    private static String getLineNumber() {
        return "L" + Thread.currentThread().getStackTrace()[5].getLineNumber();
    }

    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[5].getMethodName();
    }

    private static String getClassName() {
        return Thread.currentThread().getStackTrace()[5].getClassName();
    }
}
