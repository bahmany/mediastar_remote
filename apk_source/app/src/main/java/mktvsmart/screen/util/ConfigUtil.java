package mktvsmart.screen.util;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/* loaded from: classes.dex */
public class ConfigUtil {
    public static final String FREEDUO_SP1512_2TUNER = "238-020";
    public static final String FREESKY_PNX8471_3TUNER = "250-024";
    public static final String Globalsat_GS300_SP1512_2TUNER = "239-020";
    public static final String RELEASE_EDITION = "CID_PID";
    public static final String START_PICTURE_URL = "START_PICTURE_URL";
    public static final String UNIVERSAL_EDITION = "000-000";
    public static final String UPDATE_ENABLE = "AUTO_UPDATE_ENABLE";
    public static final String UPDATE_INFO_XML = "UPDATE_INFO_XML";
    private static ConfigUtil instance = null;
    private static Properties props = new Properties();

    private ConfigUtil(Context context) throws IOException {
        try {
            InputStream is = context.getAssets().open("config.properties");
            props.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigUtil getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigUtil(context);
        }
        return instance;
    }

    public String getValue(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public String getValue(String key) {
        return props.getProperty(key);
    }

    public void updateProperties(String key, String value) {
        props.setProperty(key, value);
    }
}
