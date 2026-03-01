package com.iflytek.cloud.util.a;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.cybergarage.xml.XML;

/* loaded from: classes.dex */
public class d {
    public static int a(String str, String str2, boolean z) throws IOException {
        int length = 0;
        try {
            File file = new File(str);
            if (!file.exists()) {
                file.createNewFile();
            } else if (z) {
                file.delete();
                file.createNewFile();
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(str2.getBytes(XML.CHARSET_UTF8));
            length = (int) randomAccessFile.length();
            randomAccessFile.close();
            return length;
        } catch (IOException e) {
            com.iflytek.cloud.a.f.a.a.a("iFly_ContactManager", "save file failed. " + str);
            return length;
        }
    }

    public static String a(String str) throws IOException {
        String str2;
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(new File(str));
            byte[] bArr = new byte[fileInputStream.available()];
            fileInputStream.read(bArr);
            str2 = new String(bArr, XML.CHARSET_UTF8);
        } catch (IOException e) {
            str2 = null;
        }
        try {
            fileInputStream.close();
        } catch (IOException e2) {
            com.iflytek.cloud.a.f.a.a.a("iFly_ContactManager", "load file failed. " + str);
            return str2;
        }
        return str2;
    }
}
