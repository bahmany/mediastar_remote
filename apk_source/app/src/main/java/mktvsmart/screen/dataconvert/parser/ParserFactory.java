package mktvsmart.screen.dataconvert.parser;

import android.util.Log;

/* loaded from: classes.dex */
public class ParserFactory {
    private static final int JSON_DATA = 1;
    private static final int XML_DATA = 0;
    private static int mDataType = 0;
    private static DataParser XmlParser = null;
    private static DataParser JsonParser = null;

    public static DataParser getParser() {
        Log.d("benson", "mDataType = " + mDataType);
        switch (mDataType) {
            case 1:
                if (JsonParser == null) {
                    JsonParser = new JsonParser();
                }
                return JsonParser;
            default:
                if (XmlParser == null) {
                    XmlParser = new XmlParser();
                }
                return XmlParser;
        }
    }

    public static int getDataType() {
        return mDataType;
    }

    public static void setDataType(int mDataType2) {
        mDataType = mDataType2;
    }
}
