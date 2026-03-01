package com.iflytek.cloud.util.a;

/* loaded from: classes.dex */
public class e {
    public static final String[] a = {"134", "135", "136", "137", "138", "139", "147", "150", "151", "152", "157", "158", "159", "182", "187", "188"};
    public static final String[] b = {"130", "131", "132", "155", "156", "185", "186"};
    public static final String[] c = {"133", "153", "180", "189"};

    public static String a(String str) {
        if (str == null) {
            return str;
        }
        String strReplaceAll = str.replaceAll(" ", "").replaceAll("-", "");
        StringBuilder sb = new StringBuilder(strReplaceAll);
        return sb.length() > 5 ? (sb.substring(0, 3).equals("+86") || sb.substring(0, 3).equals("086")) ? sb.substring(3, sb.length()) : sb.substring(0, 2).equals("86") ? sb.substring(2, sb.length()) : (sb.substring(0, 5).equals("12530") || sb.substring(0, 5).equals("12520") || sb.substring(0, 5).equals("17951") || sb.substring(0, 5).equals("17911") || sb.subSequence(0, 5).equals("12593")) ? sb.substring(5, sb.length()) : strReplaceAll : strReplaceAll;
    }

    public static String a(String[] strArr, char c2) {
        StringBuilder sb = new StringBuilder();
        if (strArr != null) {
            for (String str : strArr) {
                if (str != null) {
                    sb.append(str);
                    sb.append(c2);
                }
            }
        }
        return sb.toString();
    }
}
