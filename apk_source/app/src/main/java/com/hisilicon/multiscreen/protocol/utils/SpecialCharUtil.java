package com.hisilicon.multiscreen.protocol.utils;

/* loaded from: classes.dex */
public class SpecialCharUtil {
    public static String getSpecialChar(String inputText) {
        if (inputText == null) {
            return null;
        }
        String outPutText = inputText.replaceAll("&lt;", "<");
        return outPutText.replaceAll("&gt;", ">").replaceAll("&amp;", "&");
    }
}
