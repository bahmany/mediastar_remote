package org.videolan.vlc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/* loaded from: classes.dex */
public class Logcat {
    public static final String TAG = "VLC/Util/Logcat";

    public static void writeLogcat(String filename) throws IOException {
        String[] args = {"logcat", "-v", "time", "-d"};
        Process process = Runtime.getRuntime().exec(args);
        InputStreamReader input = new InputStreamReader(process.getInputStream());
        try {
            FileOutputStream fileStream = new FileOutputStream(filename);
            OutputStreamWriter output = new OutputStreamWriter(fileStream);
            BufferedReader br = new BufferedReader(input);
            BufferedWriter bw = new BufferedWriter(output);
            while (true) {
                try {
                    String line = br.readLine();
                    if (line != null) {
                        bw.write(line);
                        bw.newLine();
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    return;
                } finally {
                    bw.close();
                    output.close();
                    br.close();
                    input.close();
                }
            }
        } catch (FileNotFoundException e2) {
        }
    }

    public static String getLogcat() throws IOException {
        String[] args = {"logcat", "-v", "time", "-d", "-t", "500"};
        Process process = Runtime.getRuntime().exec(args);
        InputStreamReader input = new InputStreamReader(process.getInputStream());
        BufferedReader br = new BufferedReader(input);
        StringBuilder log = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (line != null) {
                log.append(String.valueOf(line) + "\n");
            } else {
                br.close();
                input.close();
                return log.toString();
            }
        }
    }
}
