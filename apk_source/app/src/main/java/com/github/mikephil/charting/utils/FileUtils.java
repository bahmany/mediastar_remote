package com.github.mikephil.charting.utils;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FileUtils {
    private static final String LOG = "MPChart-FileUtils";

    public static List<Entry> loadEntriesFromFile(String path) throws IOException {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, path);
        List<Entry> entries = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                String[] split = line.split("#");
                if (split.length <= 2) {
                    entries.add(new Entry(Float.parseFloat(split[0]), Integer.parseInt(split[1])));
                } else {
                    float[] vals = new float[split.length - 1];
                    for (int i = 0; i < vals.length; i++) {
                        vals[i] = Float.parseFloat(split[i]);
                    }
                    entries.add(new BarEntry(vals, Integer.parseInt(split[split.length - 1])));
                }
            }
        } catch (IOException e) {
            Log.e(LOG, e.toString());
        }
        return entries;
    }

    public static List<Entry> loadEntriesFromAssets(AssetManager am, String path) throws Throwable {
        List<Entry> entries = new ArrayList<>();
        BufferedReader reader = null;
        try {
            try {
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(am.open(path), "UTF-8"));
                try {
                    for (String line = reader2.readLine(); line != null; line = reader2.readLine()) {
                        String[] split = line.split("#");
                        if (split.length <= 2) {
                            entries.add(new Entry(Float.parseFloat(split[0]), Integer.parseInt(split[1])));
                        } else {
                            float[] vals = new float[split.length - 1];
                            for (int i = 0; i < vals.length; i++) {
                                vals[i] = Float.parseFloat(split[i]);
                            }
                            entries.add(new BarEntry(vals, Integer.parseInt(split[split.length - 1])));
                        }
                    }
                    if (reader2 != null) {
                        try {
                            reader2.close();
                            reader = reader2;
                        } catch (IOException e) {
                            Log.e(LOG, e.toString());
                        }
                    } else {
                        reader = reader2;
                    }
                } catch (IOException e2) {
                    e = e2;
                    reader = reader2;
                    Log.e(LOG, e.toString());
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e3) {
                            Log.e(LOG, e3.toString());
                        }
                    }
                    return entries;
                } catch (Throwable th) {
                    th = th;
                    reader = reader2;
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e4) {
                            Log.e(LOG, e4.toString());
                        }
                    }
                    throw th;
                }
            } catch (IOException e5) {
                e = e5;
            }
            return entries;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void saveToSdCard(List<Entry> entries, String path) throws IOException {
        File sdcard = Environment.getExternalStorageDirectory();
        File saved = new File(sdcard, path);
        if (!saved.exists()) {
            try {
                saved.createNewFile();
            } catch (IOException e) {
                Log.e(LOG, e.toString());
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(saved, true));
            for (Entry e2 : entries) {
                buf.append((CharSequence) (String.valueOf(e2.getVal()) + "#" + e2.getXIndex()));
                buf.newLine();
            }
            buf.close();
        } catch (IOException e3) {
            Log.e(LOG, e3.toString());
        }
    }

    public static List<BarEntry> loadBarEntriesFromAssets(AssetManager am, String path) throws Throwable {
        List<BarEntry> entries = new ArrayList<>();
        BufferedReader reader = null;
        try {
            try {
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(am.open(path), "UTF-8"));
                try {
                    for (String line = reader2.readLine(); line != null; line = reader2.readLine()) {
                        String[] split = line.split("#");
                        entries.add(new BarEntry(Float.parseFloat(split[0]), Integer.parseInt(split[1])));
                    }
                    if (reader2 != null) {
                        try {
                            reader2.close();
                        } catch (IOException e) {
                            Log.e(LOG, e.toString());
                        }
                    }
                } catch (IOException e2) {
                    e = e2;
                    reader = reader2;
                    Log.e(LOG, e.toString());
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e3) {
                            Log.e(LOG, e3.toString());
                        }
                    }
                    return entries;
                } catch (Throwable th) {
                    th = th;
                    reader = reader2;
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e4) {
                            Log.e(LOG, e4.toString());
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e5) {
            e = e5;
        }
        return entries;
    }
}
