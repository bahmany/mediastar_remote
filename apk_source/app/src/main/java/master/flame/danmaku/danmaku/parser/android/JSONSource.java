package master.flame.danmaku.danmaku.parser.android;

import android.net.Uri;
import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

/* loaded from: classes.dex */
public class JSONSource implements IDataSource<JSONArray> {
    private InputStream mInput;
    private JSONArray mJSONArray;

    public JSONSource(String json) throws JSONException {
        init(json);
    }

    public JSONSource(InputStream in) throws JSONException, IOException {
        init(in);
    }

    private void init(InputStream in) throws JSONException, IOException {
        if (in == null) {
            throw new NullPointerException("input stream cannot be null!");
        }
        this.mInput = in;
        String json = IOUtils.getString(this.mInput);
        init(json);
    }

    public JSONSource(URL url) throws JSONException, IOException {
        this(url.openStream());
    }

    public JSONSource(File file) throws JSONException, IOException {
        init(new FileInputStream(file));
    }

    public JSONSource(Uri uri) throws JSONException, IOException {
        String scheme = uri.getScheme();
        if (IDataSource.SCHEME_HTTP_TAG.equalsIgnoreCase(scheme) || IDataSource.SCHEME_HTTPS_TAG.equalsIgnoreCase(scheme)) {
            init(new URL(uri.getPath()).openStream());
        } else if (IDataSource.SCHEME_FILE_TAG.equalsIgnoreCase(scheme)) {
            init(new FileInputStream(uri.getPath()));
        }
    }

    private void init(String json) throws JSONException {
        if (!TextUtils.isEmpty(json)) {
            this.mJSONArray = new JSONArray(json);
        }
    }

    @Override // master.flame.danmaku.danmaku.parser.IDataSource
    public JSONArray data() {
        return this.mJSONArray;
    }

    @Override // master.flame.danmaku.danmaku.parser.IDataSource
    public void release() throws IOException {
        IOUtils.closeQuietly(this.mInput);
        this.mInput = null;
        this.mJSONArray = null;
    }
}
