package com.iflytek.speech;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class SpeechComponent {
    private ArrayList<String> mEngines = new ArrayList<>();
    private String mPackageName;

    public SpeechComponent(String str) {
        this.mPackageName = "";
        if (str != null) {
            this.mPackageName = str;
        }
    }

    public void addEngine(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mEngines.add(str);
    }

    public ArrayList<String> getEngines() {
        return this.mEngines;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public boolean isEngineAvaible(String str) {
        if (!TextUtils.isEmpty(str) && this.mEngines.size() != 0) {
            Iterator<String> it = this.mEngines.iterator();
            while (it.hasNext()) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (it.next().contains(str)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
